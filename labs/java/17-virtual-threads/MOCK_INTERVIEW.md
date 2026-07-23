# Mock Interview Transcript: Virtual Threads

## Interviewer: Senior SWE, Oracle
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: Virtual thread internals, structured concurrency, scoped values

---

**Q1: What are virtual threads and how do they differ from platform threads?**

**Candidate**: Virtual threads (Project Loom, JEP 444) are lightweight threads managed by the JVM rather than the OS. They're mounted on platform (carrier) threads. When a virtual thread blocks on I/O or parking, it's unmounted from the carrier, which can then run another virtual thread. This allows millions of concurrent virtual threads with minimal overhead.

**Interviewer**: How is the scheduler implemented?

**Candidate**: Virtual threads use a `ForkJoinPool` as the scheduler. The pool has a fixed number of carrier threads (usually equal to CPU count). When a virtual thread blocks, the scheduler unmounts it from the carrier and picks another virtual thread. The pool uses FIFO mode (not work-stealing) to avoid deep call stacks.

**Interviewer**: Explain "pinning". What happens when a virtual thread is pinned?

**Candidate**: Pinning occurs when a virtual thread can't be unmounted from its carrier. This happens during: (1) `synchronized` blocks/methods, (2) native method calls, (3) `wait()/notify()`. When pinned, the carrier thread is blocked for the duration, reducing parallelism. The fix: use `ReentrantLock` instead of `synchronized`, avoid native calls in virtual threads.

**Interviewer**: How do you detect pinning in production?

**Candidate**: JFR events: `jdk.VirtualThreadPinned`. You can start a JFR recording and filter for these events. Also, `jdk.VirtualThreadSubmitFailed` indicates the scheduler couldn't mount a virtual thread.

**Interviewer**: Write a web server handler using virtual threads that fetches user data, order data, and inventory data in parallel.

**Candidate**:
```java
Response handleRequest(Request req) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<User> user = scope.fork(() -> userService.getUser(req.userId()));
        Future<Order> order = scope.fork(() -> orderService.getOrder(req.orderId()));
        Future<Inventory> inv = scope.fork(() -> invService.check(req.sku()));
        
        scope.join();          // Wait for all or fail if any fails
        scope.throwIfFailed(); // Propagate exceptions
        
        return new Response(user.resultNow(), order.resultNow(), inv.resultNow());
    }
}
```

**Interviewer**: What are the advantages of `StructuredTaskScope` over `CompletableFuture.allOf`?

**Candidate**: (1) Automatic cancellation: if one subtask fails, others are cancelled. (2) No thread leakage: all subtasks complete before scope.close(). (3) Clear error propagation: `throwIfFailed()` throws the original exception. (4) Stack traces include the relationship between parent and child tasks. (5) Scoped values are inherited properly.

**Interviewer**: Compare `ScopedValue` with `ThreadLocal`.

**Candidate**: `ScopedValue` is designed for virtual threads: (1) Immutable — you can't change it after setting (no `set()` method). (2) Inherited by child threads in structured concurrency. (3) No per-thread map lookup — stored in stack, much faster. (4) No memory leak from thread pooling (ThreadLocal leaks when threads are reused). (5) Works correctly with virtual threads — no pinning concerns.

**Interviewer**: How does ScopedValue affect performance?

**Candidate**: `ScopedValue.get()` is O(1) — it reads from a stack frame reference. `ThreadLocal.get()` is O(1) too but involves a ThreadLocalMap lookup, which may have collisions. `ScopedValue` is measurably faster (Shiplilev's benchmarks show ~2x improvement). And `ScopedValue.where()` is faster than ThreadLocal.set().

**Interviewer**: Can you use `synchronized` with virtual threads?

**Candidate**: Yes, but it causes pinning. The `ReentrantLock` has been modified to not pin virtual threads. Since Java 21, `ReentrantLock` detects when it's running on a virtual thread and avoids pinning. Synchronized blocks pin, so prefer ReentrantLock in virtual thread code.

**Interviewer**: Final question: When would you still choose platform threads over virtual threads?

**Candidate**: (1) CPU-bound computation — virtual threads don't add parallelism beyond CPU count. (2) Native code (JNI) that holds locks — pinning defeats the purpose. (3) ThreadLocal-heavy code that can't be migrated to ScopedValue. (4) Priority-based scheduling — virtual threads don't support priorities. (5) Cases where you need OS-level thread isolation (e.g., per-request process limits).

---

## Feedback

**Strengths**:
- Deep understanding of virtual thread architecture
- Correctly uses StructuredTaskScope and ScopedValue
- Explains pinning causes and detection
- Knows when NOT to use virtual threads

**Areas for Improvement**:
- Could discuss continuation implementation details (yield, mount, unmount)
- Might mention `jdk.VirtualThreadPinned` and `jdk.VirtualThreadEnd` events

**Score**: 4.5/5 — Ready for production virtual thread usage
