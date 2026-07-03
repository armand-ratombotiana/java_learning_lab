# Edge Cases & Pitfalls: Advanced Thread Pools

Configuring thread pools incorrectly is one of the most common causes of production outages in enterprise Java applications.

## 1. The `Executors.newFixedThreadPool()` OOM Trap
*   **The Scenario**: You need a pool of 10 threads. You use the convenient factory method: `ExecutorService pool = Executors.newFixedThreadPool(10);`. You submit 5 million tasks to it during a batch process.
*   **The Pitfall**: If you look at the source code for `newFixedThreadPool`, you will see it uses a `LinkedBlockingQueue` without specifying a capacity. This means the queue is **unbounded** (capacity is `Integer.MAX_VALUE`). If your producer submits tasks faster than the 10 threads can process them, the queue will grow indefinitely until the JVM throws an `OutOfMemoryError` and crashes.
*   **Mitigation**: In production, **never use unbounded queues**. Always construct a `ThreadPoolExecutor` manually with a bounded queue (e.g., `new ArrayBlockingQueue<>(1000)`) and a defined `RejectedExecutionHandler`.

## 2. The `Executors.newCachedThreadPool()` Thread Explosion
*   **The Scenario**: You have a bursty workload, so you use `Executors.newCachedThreadPool()`, which creates new threads as needed and reuses old ones.
*   **The Pitfall**: The source code for `newCachedThreadPool` sets the `maxPoolSize` to `Integer.MAX_VALUE` and uses a `SynchronousQueue` (capacity 0). If a massive spike of requests hits your server, the pool will attempt to create a new thread for *every single request*. Creating 10,000 threads simultaneously will crash the OS or the JVM with a `java.lang.OutOfMemoryError: unable to create new native thread`.
*   **Mitigation**: Again, avoid the convenience methods in production. Manually configure a `ThreadPoolExecutor` with a sensible `maxPoolSize` limit.

## 3. The ThreadLocal Leak
*   **The Scenario**: You use `ThreadLocal` variables to store user context (e.g., security tokens or database connections) for the duration of a request.
*   **The Pitfall**: Thread pools *reuse* threads. If you do not explicitly clear the `ThreadLocal` at the end of the task, the next task executed by that same thread will inherit the previous task's context. This causes catastrophic security breaches (User B sees User A's data) and memory leaks.
*   **Mitigation**: Always clear `ThreadLocal` variables in a `finally` block before the `Runnable` or `Callable` finishes executing.

## 4. The ForkJoinPool Common Pool Deadlock
*   **The Scenario**: You use Java 8 Parallel Streams (`list.parallelStream()...`) or `CompletableFuture.supplyAsync()`. Inside the stream operation, you make a blocking network call or database query.
*   **The Pitfall**: By default, all parallel streams and `CompletableFuture`s share the same, global `ForkJoinPool.commonPool()`. The size of this pool is equal to the number of CPU cores minus one. If you have 4 cores, the pool has 3 threads. If you submit 3 tasks that all block waiting for a database response, the *entire common pool is exhausted*. Any other part of your application that attempts to use a parallel stream will hang indefinitely.
*   **Mitigation**: Never execute blocking I/O operations inside the common `ForkJoinPool`. For blocking operations, submit tasks to a dedicated, custom thread pool specifically sized for I/O bounds.

## 5. Swallowing Exceptions
*   **The Scenario**: You submit a `Runnable` to a thread pool via `executor.submit(Runnable)`. The `Runnable` throws a `NullPointerException`.
*   **The Pitfall**: The thread does not crash the application, and the exception is not printed to the standard error output. The thread pool catches the exception, stores it inside the resulting `Future` object, and silently terminates the task. If you never call `future.get()`, you will never know the task failed.
*   **Mitigation**: 
    1. Wrap the inside of your `Runnable` in a `try-catch(Throwable t)` block and log it.
    2. Use `executor.execute(Runnable)` instead of `submit()`, which will pass uncaught exceptions to the thread's `UncaughtExceptionHandler`.
    3. Always check the result of `future.get()`.