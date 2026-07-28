# Deep Dive: Virtual Threads

## 1. Creating Virtual Threads

```java
// Builder API
Thread vthread = Thread.ofVirtual()
    .name("my-vthread")
    .start(() -> System.out.println("Hello from " + Thread.currentThread()));

// With Executors
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    Future<String> result = executor.submit(() -> "Done");
}
```

## 2. Structured Concurrency (Preview -> Finalized)

`StructuredTaskScope` ensures subtasks complete before the enclosing scope exits:

```java
record Response(String user, String orders) {}

Response handle() throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<String> user  = scope.fork(() -> fetchUser());
        Future<String> orders = scope.fork(() -> fetchOrders());

        scope.join();            // wait for both
        scope.throwIfFailed();   // propagate any failure

        return new Response(user.resultNow(), orders.resultNow());
    }
}
```

### ShutdownOnSuccess

Returns the first successful result and cancels remaining tasks:

```java
String fastest(List<Callable<String>> providers) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
        for (var p : providers) scope.fork(p);
        scope.join();
        return scope.result();   // throws if all failed
    }
}
```

## 3. Scoped Values

Scoped values (`ScopedValue`, incubator -> finalized) replace thread-local for immutable context:

```java
public static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

void handleRequest() {
    ScopedValue.where(USER_ID, "user-123")
        .run(() -> {
            // USER_ID is available here and in any subtask
            System.out.println(USER_ID.get());
            process();
        });
}

void process() {
    // Transparently available — no parameter passing needed
    String uid = USER_ID.get();
}
```

## 4. ThreadLocal vs ScopedValue

| Aspect | ThreadLocal | ScopedValue |
|--------|-------------|-------------|
| Mutability | Mutable (per-thread) | Immutable per scope |
| Inheritance | InheritableThreadLocal | Automatic via structured scopes |
| Memory leak risk | High (pooled threads) | None (bounded lifetime) |
| Virtual threads | Works | Preferred |
| Cost | Higher (map lookup per access) | Optimized (inline in stack) |

## 5. Pinning

A virtual thread is **pinned** to a carrier thread when it:
- Executes a `synchronized` block or method
- Calls native code or JNI

Pinning limits scalability. Use `ReentrantLock` instead:

```java
// Bad — pins virtual thread
synchronized (this) { ... }

// Good — does not pin
private final Lock lock = new ReentrantLock();
lock.lock();
try { ... } finally { lock.unlock(); }
```

## 6. Best Practices

- Use virtual threads for **I/O-bound** workloads
- Use platform threads for CPU-bound tasks
- Replace `synchronized` with `ReentrantLock` in library code
- Use `StructuredTaskScope` over raw `CompletableFuture` for task groups
- Use scoped values instead of thread-local for request-scoped context
