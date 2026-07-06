# Common Mistakes in Threading

## Mistake 1: ThreadStarved ThreadPool
Setting corePoolSize too small with an unbounded queue causes the pool to never create more than core threads. Tasks pile up in the queue, but no new threads are created because the queue never fills. Solution: use a bounded queue or set corePoolSize to the expected baseline load.

## Mistake 2: Confusing thenApply with thenApplyAsync
`thenApply` runs the function on the completing thread. If that thread is a common pool worker, the function blocks the entire common pool. `thenApplyAsync` (with explicit executor) runs on a separate thread. Rule: use thenApply for fast functions (< 1 microsecond), thenApplyAsync for blocking or CPU-intensive work.

## Mistake 3: ForkJoin Threshold Selection
Setting a threshold that is too small creates more tasks than available parallelism, and the overhead of task creation and stealing dominates. Setting a threshold that is too large leaves workers idle. The optimal threshold depends on the task complexity per element. A simple rule: threshold = total / (parallelism × 4) then adjust based on measurement.

## Mistake 4: Ignoring RejectedExecutionException
ThreadPoolExecutor's AbortPolicy throws RejectedExecutionException, which is a RuntimeException. Many applications don't handle this exception, causing tasks to silently fail. Always set a custom rejection handler that either blocks (blocking policy), runs on caller's thread (CallerRunsPolicy), or logs and queues to a dead-letter queue.

## Mistake 5: StructuredTaskScope in Production
Using StructuredTaskScope prematurely (it was preview in Java 21, not finalized) on production JVMs that don't enable preview features. Check the Java version and --enable-preview flag. Also, forgetting to call `scope.join()` before accessing results causes incomplete data.

## Mistake 6: Memory Leak with CompletableFuture
Forgetting that an exceptionally handler on a CompletableFuture that never completes exceptionally causes the handler to be retained indefinitely. If the CompletableFuture is long-lived (e.g., a global variable), the handler captures its enclosing scope, preventing GC.
