# Quiz: Threading Deep Dive

1. Which thread state does a thread enter when it calls `Object.wait()`?
   a) BLOCKED
   b) WAITING
   c) TIMED_WAITING
   d) RUNNABLE

2. What happens when a ThreadPoolExecutor's queue is full and pool size equals maxPoolSize?
   a) The task blocks until space is available
   b) The task is discarded
   c) The rejection handler executes
   d) A new thread is created anyway

3. In ForkJoinPool, when a worker steals a task, it takes from which end of the victim's deque?
   a) Top (LIFO)
   b) Bottom (FIFO)
   c) Middle
   d) Random position

4. What is the difference between `thenApply` and `thenCompose`?
   a) thenApply returns a value; thenCompose returns a CompletableFuture
   b) thenApply is async; thenCompose is sync
   c) They are identical
   d) thenApply handles errors; thenCompose does not

5. How does StructuredTaskScope.ShutdownOnSuccess differ from ShutdownOnFailure?
   a) ShutdownOnSuccess cancels on first success; ShutdownOnFailure cancels on first failure
   b) ShutdownOnSuccess waits for all; ShutdownOnFailure returns first
   c) They are the same
   d) ShutdownOnSuccess is for reading; ShutdownOnFailure is for writing

6. What does `fork()` return in a RecursiveTask?
   a) The computed result
   b) The ForkJoinTask itself
   c) A Future holding the result
   d) void

7. Which of the following is NOT a Thread.State enum value?
   a) SUSPENDED
   b) BLOCKED
   c) WAITING
   d) TIMED_WAITING

8. What is the purpose of `allowCoreThreadTimeOut` in ThreadPoolExecutor?
   a) Prevents core threads from dying
   b) Allows core threads to terminate after idle timeout
   c) Allows tasks to time out
   d) Prevents queue timeouts

## Answer Key
1-b, 2-c, 3-b, 4-a, 5-a, 6-b, 7-a, 8-b
