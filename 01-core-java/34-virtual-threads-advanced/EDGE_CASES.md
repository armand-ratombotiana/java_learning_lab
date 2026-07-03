# Edge Cases & Pitfalls: Virtual Threads

Virtual Threads make concurrency simple again, but they have a few critical failure modes that can completely destroy an application's performance if ignored.

## 1. Thread Pinning (`synchronized` blocks)
*   **The Scenario**: A Virtual Thread enters a `synchronized` block or method. Inside that block, it performs a blocking I/O operation (e.g., a database query).
*   **The Pitfall**: Currently (as of Java 21), a Virtual Thread **cannot be unmounted** from its carrier OS thread while inside a `synchronized` block or while calling a native method (JNI). The Virtual Thread is "pinned" to the carrier thread. If the Virtual Thread blocks for I/O, the underlying OS carrier thread also blocks. If you have 4 CPU cores, and 4 Virtual Threads get pinned, your entire application freezes, unable to process any other Virtual Threads.
*   **Mitigation**: 
    1. Replace `synchronized` blocks with `java.util.concurrent.locks.ReentrantLock`. Virtual Threads *can* unmount while waiting on a `ReentrantLock`.
    2. Ensure third-party libraries (like JDBC drivers) have been updated to remove `synchronized` blocks around network calls.

## 2. Pooling Virtual Threads
*   **The Scenario**: You migrate to Java 21 and decide to use Virtual Threads. You configure your existing connection pool or thread pool to use a factory that creates Virtual Threads: `Executors.newFixedThreadPool(100, Thread.ofVirtual().factory())`.
*   **The Pitfall**: This is an anti-pattern. Virtual Threads are designed to be cheap and short-lived. Pooling them adds unnecessary overhead (managing the queue, thread lifecycle). More importantly, it artificially limits concurrency. A pool of 100 Virtual Threads restricts you to 100 concurrent tasks, completely defeating the purpose of Virtual Threads (which can scale to millions).
*   **Mitigation**: Never pool Virtual Threads. Always use `Executors.newVirtualThreadPerTaskExecutor()`, which creates a brand new Virtual Thread for every single submitted task and destroys it when the task completes.

## 3. `ThreadLocal` Memory Exhaustion
*   **The Scenario**: Your application heavily relies on `ThreadLocal` variables to store context (e.g., Spring Security context, MDC logging context). You switch to using 1,000,000 Virtual Threads concurrently.
*   **The Pitfall**: `ThreadLocal` variables allocate memory *per thread*. If you have 1 million Virtual Threads, you now have 1 million copies of that `ThreadLocal` data. This can quickly lead to an `OutOfMemoryError`.
*   **Mitigation**: 
    1. Minimize the use of `ThreadLocal` when using Virtual Threads.
    2. Migrate to `ScopedValue` (Preview in Java 21), which is designed specifically for efficient, immutable context sharing across massive numbers of Virtual Threads.

## 4. CPU-Bound Tasks on Virtual Threads
*   **The Scenario**: You need to calculate the hashes of 10,000 large files. You spawn 10,000 Virtual Threads to do it concurrently.
*   **The Pitfall**: Virtual Threads provide zero benefit for CPU-bound tasks. They only help with I/O-bound tasks (where threads spend most of their time waiting). If you spawn 10,000 CPU-bound Virtual Threads, they will constantly fight for time on the few available OS carrier threads, resulting in massive context-switching overhead and slower execution than if you had just used a standard `ForkJoinPool` sized to your CPU cores.
*   **Mitigation**: Continue using standard Platform Threads (e.g., `ForkJoinPool.commonPool()`) for heavy, CPU-bound computations.

## 5. Thread Dumps and Observability
*   **The Scenario**: Your application hangs. You trigger a standard thread dump (`jstack`) to see what is blocking.
*   **The Pitfall**: Standard thread dumps do not show Virtual Threads by default (because printing a stack trace for 1,000,000 threads would crash the diagnostic tool). You will only see the carrier threads, which might look idle or confusingly parked.
*   **Mitigation**: You must use `jcmd` with specific diagnostic commands (`Thread.dump_to_file`) to generate a specialized thread dump that groups Virtual Threads, or use JDK Flight Recorder (JFR) which has built-in events for Virtual Thread pinning and blocking.