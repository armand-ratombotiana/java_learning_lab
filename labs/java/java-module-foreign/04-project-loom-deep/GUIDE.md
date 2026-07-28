# Deep Dive: Project Loom Internals

## 1. Virtual Thread Architecture

Every virtual thread has:
- A **continuation** (suspendable execution state)
- A **scheduler** (usually `ForkJoinPool`)
- A **carrier thread** (platform thread that executes it)

```
Virtual Thread ──► Continuation ──► Carrier Thread (ForkJoinPool worker)
   (task)        (suspendable stack)   (platform thread)
```

## 2. Mount and Unmount

When a virtual thread blocks on I/O or park:
1. The continuation **yields** (captures stack + locals)
2. The virtual thread is **unmounted** from the carrier
3. The carrier thread picks up another virtual thread
4. When I/O completes, the virtual thread is **mounted** on a (possibly different) carrier

```java
// Inside the JVM (pseudocode)
void park() {
    if (currentThread() instanceof VirtualThread vt) {
        vt.yieldContinuation();  // unmount
        // carrier thread returns to ForkJoinPool
    } else {
        Unsafe.park();  // OS-level blocking
    }
}
```

## 3. Carrier Threads

Default scheduler: `ForkJoinPool` with `MAX(1, Runtime.availableProcessors() - 1)` workers.

```java
// Inspect carriers
Thread carrier = Thread.currentThread();
System.out.println("Carrier: " + carrier);
// Virtual threads run on platform carriers
```

## 4. Pinning

A virtual thread is **pinned** when it cannot be unmounted from its carrier.

### Causes of Pinning

| Cause | Severity | Solution |
|-------|----------|----------|
| `synchronized` block/method | Medium — blocks briefly | Use `ReentrantLock` |
| Native method / JNI | High — blocks carrier | Avoid in hot paths |
| `wait()`/`notify()` | Immediate pin | Use `Lock` + `Condition` |

### Detecting Pinning

```java
// JVM flag: -Djdk.tracePinnedThreads=short|full
// Short: prints stack trace only for pinning cases
```

Output:
```
Thread[#42,ForkJoinPool-1-worker-1,5,main]
    java.base/java.lang.VirtualThread$VThreadCarrierInfo.pin(VirtualThread.java:...)
    at com.example.MyService.syncMethod(MyService.java:15)
    <<< pinned >>>   ← indicates pinning
```

## 5. Continuations

A continuation represents a suspendable computation:

```java
// Low-level (internal API, not for direct use)
Continuation cont = new Continuation(scope, () -> {
    System.out.println("Part 1");
    Continuation.yield(scope);  // suspend
    System.out.println("Part 2");
});

cont.run();  // prints "Part 1"
cont.run();  // prints "Part 2"
```

Virtual threads use continuations internally for cooperative scheduling.

## 6. Thread-Locals with Virtual Threads

Thread-local variables work with virtual threads but have caveats:

```java
// Each virtual thread gets its own ThreadLocal instance
ThreadLocal<String> ctx = new ThreadLocal<>();
ctx.set("value");  // works fine
```

**Performance note**: ThreadLocal on virtual threads is slower than on platform threads due to indirection through the carrier.

**Memory leak risk**: In thread pools, `ThreadLocal` values persist. In virtual threads, the thread is GC'd after use, but if a virtual thread is pooled or reused, leaks can still occur.

## 7. Best Practices

- Replace `synchronized` with `ReentrantLock` in library code used by virtual threads
- Avoid pooling virtual threads — create new ones (they're cheap)
- Monitor pinning with `-Djdk.tracePinnedThreads`
- Use `ScopedValue` instead of `ThreadLocal` for request-scoped data
- Use `StructuredTaskScope` for task composition
