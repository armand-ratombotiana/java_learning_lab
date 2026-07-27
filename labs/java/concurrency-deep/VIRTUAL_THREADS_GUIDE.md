# Virtual Threads (Project Loom) — Deep Interview Guide

> Java 21+ complete guide to virtual threads for senior/staff level interviews

---

## 1. What Are Virtual Threads?

Virtual threads are lightweight threads managed by the JVM, not by the OS. They enable high-throughput concurrent applications with a simple thread-per-request programming model, avoiding the complexity of reactive programming.

**Key insight**: Virtual threads are not "faster threads" — they enable you to have *more* threads (millions) than the OS can support.

### How They're Implemented

```
Application code (runs on virtual thread)
         │
         ▼
┌─────────────────────┐
│    VirtualThread     │  ← extends Thread (backward compatible)
│  - continuation      │  ← stored on heap, not native stack
│  - carrier thread    │  ← platform thread executing this VT
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│  ForkJoinPool (carrier) │  ← pool of platform threads
└─────────────────────┘
         │
         ▼
┌─────────────────────┐
│   OS Platform Thread  │  ← actual kernel thread
└─────────────────────┘
```

**Implementation files in OpenJDK:**
- `src/java.base/share/classes/java/lang/VirtualThread.java`
- `src/java.base/share/classes/jdk/internal/vm/Continuation.java`
- `src/java.base/share/classes/jdk/internal/vm/ContinuationScope.java`

---

## 2. Carrier Threads, Mount/Unmount, Continuation

### Carrier Threads

- Virtual threads execute on platform threads called *carrier threads*
- Carrier pool is a `ForkJoinPool` with parallelism = `Runtime.availableProcessors()`
- Configured via `jdk.virtualThreadScheduler.parallelism` system property
- Typical size: 1-2 per CPU core (not 200-1000 like traditional thread pools)

### Mount/Unmount

**Mount**: When a virtual thread starts executing, it's *mounted* onto a carrier thread. The carrier's native stack frames are replaced with the virtual thread's stack frames.

**Unmount**: When a virtual thread blocks (I/O, `park()`, `sleep()`), it's *unmounted* from the carrier. The carrier is freed to run another virtual thread.

```
Timeline (simplified):
VT1:  [Mount]─────[block]─────[Mount]──────────────[block]─────[Mount]──
                     │                                    │
                     ▼                                    ▼
                 unmount VT1                          unmount VT1
Carrier: [=======VT1=======][=======VT2=======][=======VT3=======][===...
```

### Continuation

A `Continuation` captures the execution state of a virtual thread — its stack frames, program counter, and local variables. When a virtual thread is unmounted, the continuation is stored on the heap. When remounted, the continuation is resumed.

```java
// Simplified (not actual API):
Continuation cont = new Continuation(() -> {
    method1();
    method2();
});
cont.run(); // runs until yield
cont.run(); // resumes from yield point
```

**Yield points** are automatically inserted at:
- `java.net.Socket` I/O
- `java.io` operations
- `LockSupport.park()`
- `Thread.sleep()`
- `Object.wait()`... actually no — `synchronized` does NOT yield (pinning!)

---

## 3. Pinning

Pinning occurs when a virtual thread cannot be unmounted from its carrier thread, effectively blocking the carrier.

### What Causes Pinning

| Cause | Severity | Mitigation |
|-------|----------|------------|
| `synchronized` block/method | High — carrier blocked | Use `ReentrantLock` instead |
| `synchronized` inside I/O block | Very high — defeats VT purpose | Restructure to avoid |
| JNI call | High — native code may call back | Minimize JNI usage |
| `Thread.yield()` inside synchronized | Medium | Avoid |
| `Object.wait()` | Medium — but `wait()` yields | Use `Condition.await()` |

### Synchronized Pinning

**Why it happens**: The JVM cannot unwind the native stack frame for a `synchronized` block because:
1. The monitor's ownership is tracked in the native stack
2. The JIT compiles `synchronized` with `monitorenter`/`monitorexit` bytecodes
3. These cannot be split across mount/unmount boundaries

**Detection**: Use `-Djdk.tracePinnedThreads`:
```
java -Djdk.tracePinnedThreads=short MyApp
// Output shows stack traces where pinning occurred
```

**Full vs short mode**:
- `short`: prints only frames involved in pinning
- `full`: prints full stack trace

### JNI Pinning

JNI pinning is more fundamental: native code has pointers into the Java heap (via `GetPrimitiveArrayCritical`). The JVM cannot move object references while native code holds them.

**Mitigation**: Avoid JNI in virtual-thread-intensive code. Use Panama Foreign Function & Memory API instead.

### How to Avoid Pinning

```java
// BAD — causes pinning:
synchronized (lock) {
    socket.read(buffer); // I/O blocks, carrier stuck
}

// GOOD — no pinning:
lock.lock();
try {
    socket.read(buffer); // yields, unmounts, carrier freed
} finally {
    lock.unlock();
}
```

---

## 4. ThreadLocal and Scoped Values with Virtual Threads

### Problem with ThreadLocal

```java
private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

// In a virtual thread:
currentUser.set(user);
// ... millions of virtual threads ...
// Each ThreadLocal entry persists as long as the thread lives
// Virtual threads can be long-lived → memory leak
```

**Memory pressure**: With 100,000 virtual threads each having a ThreadLocal with 1KB data → 100MB resident memory.

### Solution: ScopedValues

```java
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

void handleRequest(User user) {
    ScopedValue.where(CURRENT_USER, user).run(() -> {
        // Inside this scope, CURRENT_USER.get() returns the user
        process();
    });
    // After run(), the binding is gone
}

void process() {
    User user = CURRENT_USER.get(); // immutable — cannot set
    // ...
}
```

**ScopedValue advantages over ThreadLocal:**
1. **Immutable binding** — once set in a scope, cannot be changed
2. **No memory leak** — binding is scoped; after `run()` returns, it's garbage collected
3. **Inheritance** — scopes can be nested; inner scopes inherit outer bindings (unless overridden)
4. **No per-thread map** — stored only on the stack (in the continuation)

---

## 5. Performance: When Virtual Threads Help, When They Don't

### Virtual Threads Excel

| Scenario | Why | Speedup |
|----------|-----|---------|
| Many concurrent I/O-bound tasks | Unmount on blocking → high utilization | 10-100x |
| High connection counts (10K+) | ~KB vs ~MB per thread | 1000x memory savings |
| Simple request-per-thread servers | No reactive complexity | Developer productivity |
| Fine-grained tasks (database calls) | Thousands of concurrent JDBC calls | 5-10x |

### Virtual Threads DON'T Help

| Scenario | Why | Alternative |
|----------|-----|-------------|
| CPU-bound computation | No blocking → no unmounting benefit | Platform threads, parallel streams |
| Heavy JDBC connection pooling | Pool limit becomes bottleneck | Increase pool or use async drivers |
| High contention on synchronized | Pinning defeats purpose | ReentrantLock, restructure |
| Graphics/UI frameworks | Usually use platform threads | Platform threads |
| Native interop heavy code | Pinning from JNI | Panama FFI |
| Latency-sensitive CPU workloads | Carrier switching overhead | Bind VT to specific carrier |

### Performance Characteristics

```java
// Benchmark mental model:
// 10,000 tasks, each sleeping 100ms
Executors.newFixedThreadPool(100)     → ~10 seconds (100 concurrent × 100ms)
Executors.newVirtualThreadPerTask()  → ~0.1 seconds (10,000 concurrent — all sleep simultaneously)
```

**Carrier pool sizing**: Use `Runtime.availableProcessors()` carriers. More carriers don't help — virtual threads unmount on blocking, so a small carrier pool suffices.

---

## 6. Integration with Spring Boot 3.2+

### Configuration

```properties
# application.properties
spring.threads.virtual.enabled=true
```

### What Changes

| Component | Before (Platform Threads) | After (Virtual Threads) |
|-----------|--------------------------|------------------------|
| Tomcat | Thread pool (200 threads) | Virtual threads per request |
| `@Async` | Platform thread pool | Virtual thread per task |
| `@Scheduled` | Platform thread | Platform thread (unchanged) |
| WebClient | Event loop thread | Virtual thread blocked thread |

### Tomcat Configuration

With virtual threads enabled:
```java
@Configuration
public class TomcatConfig {
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerCustomizer() {
        return handler -> {
            handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
}
```

**Key benefit**: Blocking operations (JDBC, RestTemplate) no longer consume a platform thread. 10,000 concurrent requests with 100 JDBC connections each — platform threads would need 10,000 threads (impossible); virtual threads handle it easily.

### Pitfalls

1. **JDBC connection pool exhaustion**: Virtual threads block waiting for connections; pool must be sized appropriately
2. **ThreadLocal usage**: Scoped values recommended; verify ThreadLocal usage in libraries
3. **`synchronized` in framework code**: Spring internally uses synchronized in some places; Spring 6.1+ has fixes
4. **Pooled objects**: Don't assume thread identity for cached objects

---

## 7. Integration with Async Frameworks

### RxJava / Project Reactor

**Recommendation from Spring team**: Consider migrating from reactive to virtual threads for I/O-bound services.

**Migration path:**
1. Start with reactive + virtual threads side-by-side (playground)
2. Migrate simple endpoints to MVC with virtual threads
3. Keep reactive for high-throughput streaming (WebSocket, SSE)

**Performance comparison:**
```
Virtual threads: 10,000 req/s, 100ms blocking I/O → 10,000 concurrent requests
WebFlux:         Same throughput, but requires reactive stack throughout
```

**When to keep reactive:**
- WebSocket/Server-Sent Events with high concurrency
- Services that already have full reactive stack (no migration cost)
- Mixed CPU + I/O workloads where reactive backpressure helps

---

## 8. StructuredTaskScope

### API Overview

```java
// ShutdownOnFailure — fail-fast (if any subtask fails, cancel all)
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<A> a = scope.fork(() -> fetchA());
    Future<B> b = scope.fork(() -> fetchB());
    scope.join();
    scope.throwIfFailed();
    return combine(a.resultNow(), b.resultNow());
}

// ShutdownOnSuccess — return first success, cancel others
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
    scope.fork(() -> fetchFromDC1());
    scope.fork(() -> fetchFromDC2());
    scope.fork(() -> fetchFromDC3());
    return scope.join(); // returns first successful result
}
```

### Custom Subclass

```java
class RetryScope<T> extends StructuredTaskScope<T> {
    private volatile T result;
    private volatile Exception exception;

    @Override
    protected void handleComplete(Future<T> future) {
        switch (future.state()) {
            case SUCCESS -> {
                if (result == null) result = future.resultNow();
            }
            case FAILED -> {
                if (exception == null) exception = new Exception(future.exceptionNow());
                // Retry logic could go here
            }
            case CANCELLED -> {}
            case RUNNING -> {}
        }
    }
}
```

---

## 9. Comparison with Other Concurrency Models

| Feature | Virtual Threads | Go Goroutines | Erlang Processes | Kotlin Coroutines |
|---------|---------------|---------------|------------------|-------------------|
| **Managed by** | JVM | Go runtime | BEAM VM | Kotlin compiler |
| **Stack size** | Growable (from ~few KB) | Growable (starts ~2KB) | ~300 words | State machine |
| **M:N threading** | Yes | Yes (GMP model) | Yes (SMP) | Yes |
| **Preemptive** | No (cooperative at yield points) | No (cooperative at function calls) | Yes (reductions) | No (cooperative at suspend) |
| **Communication** | `StructuredTaskScope`, `CompletableFuture` | Channels | Message passing | Channels, Flow |
| **Backpressure** | Manual (blocking queue) | Built-in (buffered channels) | Selective receive | Flow buffer |
| **Debugging** | Good (JVM thread dump) | Good (pprof) | Excellent | Good (debug agent) |
| **Maturity** | Java 21 (2023) | Go 1.0 (2012) | 30+ years | Kotlin 1.3 (2018) |

---

## 10. Interview Questions with Answers

### Q1: "How do virtual threads achieve better throughput than platform threads?"

**A**: Virtual threads are lightweight (few KB vs ~1MB for platform threads). They yield at blocking operations instead of blocking the underlying carrier thread. This means a pool of 8 carrier threads can handle 10,000+ concurrent virtual threads. The key is that blocking I/O doesn't waste an OS thread — the carrier is reused for other virtual threads.

### Q2: "What is pinning and how do you detect it?"

**A**: Pinning occurs when a virtual thread cannot be unmounted from its carrier, usually due to `synchronized` blocks or JNI calls. The carrier thread remains occupied even when the virtual thread is blocked. Use `-Djdk.tracePinnedThreads` to detect it. Mitigate by replacing `synchronized` with `ReentrantLock`.

### Q3: "When would you NOT use virtual threads?"

**A**: (1) CPU-bound computations — no blocking means no unmounting benefit. (2) High-contention synchronized code — pinning causes carrier blockage. (3) Applications already heavily optimized with reactive/async patterns — migration cost may outweigh benefits. (4) JDBC-heavy services without pool tuning — connection pool becomes bottleneck.

### Q4: "How does the JVM implement virtual thread scheduling?"

**A**: Virtual threads are mounted on a `ForkJoinPool` (the carrier pool). Each carrier thread has a work-stealing deque. Virtual threads are submitted to the pool and execute until they hit a blocking/yield point. At the yield point, the `Continuation` object captures the stack, and the virtual thread is unmounted. When unblocked (e.g., I/O completes), the continuation is resubmitted to the carrier pool.

### Q5: "Explain the difference between structured concurrency and unstructured concurrency."

**A**: In unstructured concurrency (CompletableFuture, ExecutorService), tasks launched by a scope can outlive the parent scope. With structured concurrency (StructuredTaskScope), all forked tasks complete (or are cancelled) when the scope closes. This provides:
- Clear lifetime boundaries
- Automatic error propagation and cancellation
- Observation (thread dumps show task hierarchy)
- Deterministic resource cleanup

### Q6: "How do ScopedValues differ from ThreadLocal?"

**A**: ScopedValues are immutable within a scope — you can only rebind by creating a nested scope. They don't require explicit cleanup (no `remove()` calls needed). Memory is allocated only for the active scope, not for every thread. They work correctly with virtual threads where millions of threads may share the same scoped value binding.

### Q7: "What is the carrier thread pool size and how do you tune it?"

**A**: The default carrier pool is a ForkJoinPool with `Runtime.availableProcessors()` workers. It's intentionally small — virtual threads unmount on blocking, so carriers are reused. Tuning via `jdk.virtualThreadScheduler.parallelism`. Increasing beyond CPU count rarely helps (and can hurt due to contention). Only increase if virtual threads are CPU-bound or frequently pinned.

### Q8: "How does platform thread context switching differ from virtual thread switching?"

**A**: Platform thread context switching is a kernel-mode operation (~1-10µs) involving TLB flush, register save/restore, scheduler decision. Virtual thread switching is a user-mode operation (~0.1µs) involving saving/restoring the continuation on the heap — no kernel transition needed. This is why virtual threads can switch in nanoseconds.

### Q9: "Can you use Thread.interrupt() with virtual threads?"

**A**: Yes. Virtual threads implement `Thread` and support interruption via `Thread.interrupt()` and `InterruptedException`. When interrupted during blocking I/O, the I/O operation throws `InterruptedException`, allowing the virtual thread to clean up and terminate. This works even across mount/unmount boundaries.

### Q10: "How does the JVM handle I/O operations for virtual threads?"

**A**: The JDK's I/O methods (Socket, InputStream, etc.) have been modified to detect when they're running on a virtual thread. When blocking I/O is called from a virtual thread, instead of blocking the OS thread, it:
1. Registers the I/O operation with a poller (Java's `PollerProvider`)
2. Parks the virtual thread (saves the continuation)
3. When I/O completes, the poller unparks the virtual thread (resubmits to carrier pool)
4. Execution resumes from the yield point

---

## Quick Reference: Virtual Threads Cheat Sheet

```java
// Create
Thread vt = Thread.startVirtualThread(() -> { ... });
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> { ... });
}

// Structured concurrency
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<X> f1 = scope.fork(() -> fetch1());
    Future<Y> f2 = scope.fork(() -> fetch2());
    scope.join();
    scope.throwIfFailed();
    return combine(f1.resultNow(), f2.resultNow());
}

// ScopedValue
private static final ScopedValue<User> USER = ScopedValue.newInstance();
ScopedValue.where(USER, user).run(() -> {
    User u = USER.get();
});

// Avoid pinning — DON'T use synchronized in blocking code:
// synchronized(lock) { socket.read(buf); }   ← BAD
lock.lock(); try { socket.read(buf); } finally { lock.unlock(); }  ← GOOD

// Debug pinning
// JVM args: -Djdk.tracePinnedThreads=short
```

---

*Last updated for Java 21+ (LTS). Virtual threads are production-ready but evolving — check JEPs for newer features.*
