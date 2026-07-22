# Interview Questions: ForkJoinPool

## Company-Specific Focus

### Google
- ForkJoinPool: work-stealing thread pool for divide-and-conquer parallelism
- Work-stealing: idle threads steal tasks from busy threads' queues
- ForkJoinTask: RecursiveAction (void) and RecursiveTask (return value)

### Microsoft
- ForkJoinPool vs C# Task Parallel Library: work-stealing differences
- CommonPool: used by parallel streams and CompletableFuture

### Amazon
- ForkJoinPool for parallel data processing: large dataset computation
- Work-stealing: automatically load balance across worker threads
- Parallelism level: default equals availableProcessors - 1

### Meta
- RecursiveTask: compute() method divides and forks subtasks
- ForkJoinPool.invoke(): starts main task
- ManagedBlocker: handling blocking operations in ForkJoinPool

### Apple
- ForkJoinPool vs ThreadPoolExecutor: when to use each
- Work queues: deques per worker thread, LIFO for own tasks, FIFO for stolen
- Pool shutdown: shutdownNow, awaitTermination

### Oracle
- java.util.concurrent.ForkJoinPool specification
- Work-stealing algorithm: reduce contention and improve throughput
- ForkJoinTask: lightweight thread representation
- ManagedBlocker: for blocking tasks in ForkJoinPool

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 148 Sort List | Medium | Amazon, Google, Apple | Merge sort with fork-join parallelism |
| 315 Count of Smaller Numbers After Self | Hard | Google, Amazon | Divide and conquer with merge sort |
| 493 Reverse Pairs | Hard | Google, Amazon | Divide and conquer parallelism |
| 53 Maximum Subarray | Easy | Amazon, Apple, Google | Divide and conquer approach |

## Real Production Scenarios
- **Uber**: ForkJoinPool for parallel trip matching — reduced computation from 2s to 200ms
- **LinkedIn**: Parallel processing of user profiles for graph analysis using RecursiveTask

## Interview Patterns & Tips
- **Work-stealing**: idle threads steal from busy threads' queue tails
- **RecursiveAction for void**: use compute() with fork() and join()
- **threshold**: define a threshold for sequential computation
- **ManagedBlocker**: use for blocking operations in ForkJoinPool

## Deep Dive Questions
- **Work queue**: How are work queues structured in ForkJoinPool?
- **Stealing**: How does work-stealing reduce contention?
- **join() vs get()**: Difference between ForkJoinTask.join() and Future.get()
- **CommonPool**: What is ForkJoinPool.commonPool() used for?
- **Parallelism**: How does ForkJoinPool determine the number of worker threads?