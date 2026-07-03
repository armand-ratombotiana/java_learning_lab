# Quizzes: Advanced Virtual Threads

Test your knowledge of Virtual Threads, Structured Concurrency, and Scoped Values.

## Quiz 1: Virtual Thread Mechanics

**Q1: What happens when a Virtual Thread makes a blocking network call (e.g., waiting for an HTTP response)?**
- A) The underlying OS carrier thread blocks until the response arrives.
- B) The JVM unmounts the Virtual Thread, saves its stack to the heap, and frees the OS carrier thread to execute another Virtual Thread.
- C) The Virtual Thread throws an `InterruptedException`.
- D) The JVM creates a new OS thread.
*Answer: B*

**Q2: Which of the following is considered an anti-pattern when using Virtual Threads?**
- A) Spawning a new Virtual Thread for every incoming HTTP request.
- B) Using `ReentrantLock` instead of `synchronized`.
- C) Creating a fixed-size Thread Pool of 100 Virtual Threads using `Executors.newFixedThreadPool(100, factory)`.
- D) Making synchronous, blocking database calls.
*Answer: C (Virtual Threads are cheap; pooling them adds overhead and artificially limits concurrency. You should always use `newVirtualThreadPerTaskExecutor()`).*

## Quiz 2: Pinning and CPU Bounds

**Q1: What is "Thread Pinning" in the context of Java Virtual Threads?**
- A) When a Virtual Thread is permanently assigned to a specific CPU core.
- B) When a Virtual Thread enters a `synchronized` block or a native method, preventing the JVM from unmounting it. If the thread blocks while pinned, the underlying OS carrier thread also blocks.
- C) When a Virtual Thread refuses to yield to a higher priority thread.
- D) When a Virtual Thread is garbage collected.
*Answer: B*

**Q2: Why should you NOT use Virtual Threads for heavy, CPU-bound computations (like video rendering or cryptography)?**
- A) Virtual Threads are not thread-safe.
- B) Virtual Threads only provide performance benefits when they can unmount during I/O blocking. For CPU-bound tasks, they simply fight for time on the limited carrier threads, adding context-switching overhead without increasing throughput.
- C) Virtual Threads cannot access the CPU's floating-point unit.
- D) Virtual Threads have a strict 1-second execution limit.
*Answer: B*

## Quiz 3: Structured Concurrency

**Q1: In Structured Concurrency, what happens when using `StructuredTaskScope.ShutdownOnFailure()` if one of the three spawned subtasks throws an exception?**
- A) The scope waits for the other two to finish, then throws the exception.
- B) The scope immediately cancels the other two running subtasks and propagates the exception (Fail-Fast).
- C) The scope ignores the exception and returns the results of the two successful tasks.
- D) The JVM crashes.
*Answer: B*