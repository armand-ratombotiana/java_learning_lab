# Code Deep Dive: Threading Internals

## ThreadPoolInternals.java Analysis
The constructor parameters (core=2, max=4, queue capacity=2) create a specific behavior profile. When tasks 0-1 arrive, they create core threads. Tasks 2-3 enter the queue. Task 4 triggers a new thread (now 3 workers). Task 5 triggers another (now 4 workers). Tasks 6+ trigger rejection.

The `ArrayBlockingQueue` is bounded, so it always rejects when full. With `SynchronousQueue`, every task requires a new thread (until max). With `LinkedBlockingQueue` (unbounded), maxPoolSize is effectively ignored — tasks always queue.

The `allowCoreThreadTimeOut(true)` combined with keepAliveTime=1s means core threads also terminate after 1 second idle. This is useful for elastic workloads but causes thread creation churn for bursty patterns.

## ForkJoinPoolDemo.java Analysis
The threshold of 10,000 elements was chosen to balance task creation overhead against parallel gain. With 100,000 elements and parallelism=4, each worker processes ~25,000 elements. The task tree has depth log₂(100,000/10,000) ≈ 4 levels.

The `fork()` call pushes the left task to the current worker's deque. The right task is computed directly (`compute()`), which may recursively fork. If the right task's recursive `fork()` steals a left task from another deque, the work-stealing manifests.

## CompletableFutureDeepDive.java Analysis
`thenApply` vs `thenApplyAsync`: thenApply runs the function on the completing thread (which could be from common pool or the thread that called `complete()`). thenApplyAsync always runs on the common pool (or a specified executor). Using thenApply when the function blocks is a common bug — it blocks the completing thread.

`exceptionally` returns a new CompletableFuture. If the original completes exceptionally, the function in exceptionally provides the fallback. If the original completes normally, exceptionally passes through the value unchanged.
