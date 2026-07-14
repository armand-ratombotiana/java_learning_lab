# ThreadPoolExecutor Internals

## 🔬 The Lifecycle of a Task
When you call `executor.submit(task)`, the `ThreadPoolExecutor` follows a very specific logic to decide how to handle it. This is a common interview question.

1. **Check Core Size**: If the number of running threads is less than `corePoolSize`, the executor creates a **new thread** to run the task immediately.
2. **Queue the Task**: If `corePoolSize` is reached, the executor tries to put the task into the **Work Queue**.
3. **Check Max Size**: If the queue is full, the executor checks if the number of threads is less than `maximumPoolSize`. If yes, it creates **another new thread** to run the task.
4. **Reject**: If `maximumPoolSize` is reached AND the queue is full, the executor invokes the **RejectedExecutionHandler**.

**Crucial Point**: Notice that the executor prefers to queue tasks rather than create threads beyond the core size. It only creates "extra" threads (up to max size) if the queue is already full.

## 🗂️ Choosing a Work Queue
The behavior of your pool depends heavily on the queue type:
- **`LinkedBlockingQueue` (Unbounded)**: The queue can grow infinitely. `maximumPoolSize` is ignored because the queue is never full. This is dangerous as it can lead to OOM.
- **`ArrayBlockingQueue` (Bounded)**: Has a fixed capacity. Once full, the pool starts scaling up to `maximumPoolSize`.
- **`SynchronousQueue`**: Has a capacity of zero. Every task must be handed off directly to a thread. If no thread is free, the pool creates a new one immediately (up to max). This is used by `Executors.newCachedThreadPool()`.

## 🛑 Rejection Policies
What happens when the system is overloaded?
1. **`AbortPolicy` (Default)**: Throws a `RejectedExecutionException`.
2. **`CallerRunsPolicy`**: The thread that submitted the task runs it itself. This naturally slows down the producer (throttling).
3. **`DiscardPolicy`**: Silently drops the task.
4. **`DiscardOldestPolicy`**: Drops the oldest task in the queue and tries to resubmit the new one.