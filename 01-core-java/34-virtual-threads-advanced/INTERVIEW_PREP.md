# Interview Preparation: Virtual Threads & Structured Concurrency

This document covers advanced questions related to Project Loom, Virtual Thread mechanics, Thread Pinning, and Scoped Values.

## Q1: How do Virtual Threads differ from traditional Platform Threads in Java?
**Answer:**
*   **Platform Threads** are 1:1 wrappers around OS threads. They are heavy (requiring ~1MB of stack memory) and slow to context switch. You can typically only spawn a few thousand before exhausting OS resources.
*   **Virtual Threads** are managed entirely by the JVM. They are lightweight and multiplexed over a small pool of carrier OS threads (usually sized to the number of CPU cores). When a Virtual Thread encounters a blocking I/O operation (like `Thread.sleep()` or a database query), the JVM "unmounts" it, saving its state to the heap, and allows the carrier thread to execute a different Virtual Thread. This allows you to spawn millions of concurrent Virtual Threads.

## Q2: What is "Thread Pinning" and why is it currently a problem for Virtual Threads?
**Answer:**
Thread Pinning occurs when a Virtual Thread is unable to unmount from its carrier thread during a blocking operation. 
Currently (as of Java 21), a Virtual Thread gets pinned if it blocks while executing inside a `synchronized` block/method, or while executing a native method (JNI). 
If a Virtual Thread is pinned and performs a blocking I/O operation, the underlying OS carrier thread is also blocked. If all carrier threads become blocked by pinned Virtual Threads, the entire application freezes (starvation). The mitigation is to replace `synchronized` blocks with `java.util.concurrent.locks.ReentrantLock`.

## Q3: Why is pooling Virtual Threads considered an anti-pattern?
**Answer:**
Thread pools were invented because creating and destroying OS threads is expensive. Virtual Threads, however, are extremely cheap to create and destroy (comparable to allocating a standard Java object).
Pooling Virtual Threads adds unnecessary overhead (managing the pool queue and lifecycle). More importantly, it artificially limits concurrency. The power of Virtual Threads is the ability to have 1,000,000 concurrent tasks. If you use a pool of 100 Virtual Threads, you have bottlenecked your application to 100 concurrent tasks, completely defeating the purpose of Project Loom. You should always use `Executors.newVirtualThreadPerTaskExecutor()`.

## Q4: Explain the difference between `ShutdownOnFailure` and `ShutdownOnSuccess` in Structured Concurrency.
**Answer:**
Both are policies within `StructuredTaskScope` used to manage a group of concurrent child tasks.
*   **`ShutdownOnFailure` (Fail-Fast)**: If *any* child task throws an exception, the scope immediately cancels all other running child tasks and propagates the error. It is used when you need the result of *all* tasks to proceed.
*   **`ShutdownOnSuccess` (First-to-Finish)**: As soon as *one* child task completes successfully, the scope immediately cancels all other running child tasks and returns the successful result. It is used for redundancy (e.g., querying 3 different weather APIs and taking the first one that responds).

## Q5: Why are `ScopedValue`s preferred over `ThreadLocal` variables when using Virtual Threads?
**Answer:**
If you have 1,000,000 Virtual Threads, and each thread uses a `ThreadLocal` to store a security context, you have 1,000,000 copies of that data in memory, leading to massive memory overhead. Furthermore, when spawning child threads, `ThreadLocal` inheritance requires expensive deep copying.
`ScopedValue` solves this. It is immutable and strictly bounded by a lexical scope (`ScopedValue.where(...).run(...)`). Because it is immutable and strictly scoped, child threads spawned via `StructuredTaskScope` can safely share the exact same `ScopedValue` instance in memory with zero copying overhead, making it highly efficient for massive concurrency.