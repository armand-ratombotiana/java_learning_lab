# How Threading Internals Work

## Thread Creation
When `new Thread()` is called, Java allocates a Thread object on the heap. The OS thread is not yet created. When `start()` is called, the JVM calls the native `createThread` function, which calls the OS (e.g., `pthread_create` on Linux, `CreateThread` on Windows). The new OS thread immediately enters `Thread.run()`.

## ThreadPoolExecutor Execution Flow
1. A task is submitted via `execute()` or `submit()`.
2. If the running thread count < corePoolSize, a new thread is created.
3. Otherwise, the task is offered to the work queue.
4. If the queue is full and running count < maxPoolSize, a new thread is created.
5. If the queue is full and running count == maxPoolSize, the rejection handler executes.

The `addWorker` method atomically increments the worker count and creates a new Worker thread. Each Worker loops: poll the queue (with timeout if allowCoreThreadTimeOut), execute the task, then loop again.

## ForkJoinPool Work-Stealing Mechanics
Each worker has a `WorkQueue` containing a `ForkJoinTask<?>[]` array (deque). `fork()` calls `push(task)` on the current worker's queue. `join()` calls `doJoin()` which checks if the task was stolen (executed by another thread). If stolen, it waits via `awaitJoin()` which spins, then parks. The stealing thread scans random worker queues and takes from the tail.

## CompletableStage Completion
Each stage has a `result` field (Object) and a list of dependent stages (stack). When a stage completes, it calls `postComplete()` which pops the stack and runs each dependent stage's completion handler. Dependents are executed based on the executor (default: ForkJoinPool.commonPool() for async, direct thread for sync).

## StructuredTaskScope Lifecycle
When a scope is created, it captures the current thread as the owner. Subtasks are registered and await a countdown latch. When the owner calls `join()`, it blocks until all subtasks complete or the scope is shut down. ShutdownOnSuccess cancels on first success; ShutdownOnFailure collects failures and rethrows on `throwIfFailed()`.
