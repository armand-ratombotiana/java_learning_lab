# Interview Preparation: Advanced Thread Pools

This document covers advanced questions related to `ThreadPoolExecutor` internals, task rejection policies, and the `ForkJoinPool` work-stealing algorithm.

## Q1: Explain the exact sequence of events when a new task is submitted to a `ThreadPoolExecutor`.
**Answer:**
When `execute(task)` is called:
1.  **Core Pool Check**: If the number of running threads is less than `corePoolSize`, a new thread is created to run the task, even if other worker threads are idle.
2.  **Queue Check**: If the number of running threads is $\ge$ `corePoolSize`, the executor attempts to place the task into the `workQueue`.
3.  **Max Pool Check**: If the queue is full, and the number of running threads is less than `maxPoolSize`, a new thread is created to run the task.
4.  **Rejection**: If the queue is full and the number of running threads equals `maxPoolSize`, the task is rejected, and the `RejectedExecutionHandler` is invoked.

## Q2: Why is using `Executors.newFixedThreadPool(n)` considered a bad practice in production?
**Answer:**
The `Executors.newFixedThreadPool(n)` factory method creates a `ThreadPoolExecutor` with a `LinkedBlockingQueue` that has no capacity limit (it defaults to `Integer.MAX_VALUE`).
If the application receives a sudden spike in traffic, or if the tasks take a long time to process (e.g., a database slowdown), the queue will grow indefinitely. This will eventually consume all available heap memory, resulting in an `OutOfMemoryError` and crashing the JVM.
In production, you should always manually instantiate a `ThreadPoolExecutor` with a bounded queue and a defined rejection policy to ensure system stability under load.

## Q3: What is the `CallerRunsPolicy`, and why is it useful?
**Answer:**
`CallerRunsPolicy` is a `RejectedExecutionHandler`. When a thread pool is saturated (queue is full, max threads reached), instead of throwing an exception or discarding the task, this policy forces the thread that *submitted* the task to execute the task itself.
This is highly useful because it provides automatic **backpressure**. If the main HTTP request thread submits a task and the pool is full, the main thread is forced to do the heavy lifting. While it's doing that, it cannot accept new HTTP requests. This naturally slows down the rate of incoming tasks, giving the thread pool time to catch up, preventing system overload.

## Q4: How does the "Work Stealing" algorithm in `ForkJoinPool` reduce lock contention?
**Answer:**
In a standard `ThreadPoolExecutor`, all threads pull tasks from a single, centralized queue, which requires locking and causes contention.
In a `ForkJoinPool`, every worker thread has its own internal double-ended queue (deque).
*   When a thread generates sub-tasks, it pushes them to the *head* of its own deque.
*   When it needs work, it pops from the *head* of its own deque (LIFO). Since it's the only thread accessing the head, synchronization overhead is minimal.
*   If a thread's deque is empty, it becomes a "thief" and steals a task from the *tail* of another busy thread's deque (FIFO). Because the owner works at the head and the thief steals from the tail, they rarely contend for the same lock.

## Q5: What is the danger of using `ForkJoinPool.commonPool()` for blocking I/O operations?
**Answer:**
The `commonPool` is shared across the entire JVM. It is used by default for all Java 8 Parallel Streams and `CompletableFuture` operations. Its size is typically equal to the number of CPU cores minus 1.
If you execute a task inside the `commonPool` that blocks (e.g., waiting for an HTTP response or a database query), that thread is suspended. If you submit several blocking tasks, you will quickly exhaust all threads in the common pool. 
Once exhausted, *any* other part of your application that attempts to use a parallel stream or `CompletableFuture` will hang indefinitely, waiting for a thread to become available. Blocking I/O should always be offloaded to a dedicated, custom thread pool.