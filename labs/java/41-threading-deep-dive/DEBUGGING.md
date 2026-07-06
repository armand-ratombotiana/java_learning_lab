# Debugging Threading Issues

## Thread Dumps
The most important debugging tool. Capture a thread dump via:
- `jstack <pid>`
- `kill -3 <pid>` (Linux)
- `Ctrl+Break` (Windows)
- `jcmd <pid> Thread.print`

Look for:
- BLOCKED threads: identify which lock they're waiting on
- WAITING threads: identify what they're waiting for (park, wait, join)
- RUNNABLE threads with same stack trace: may indicate a tight loop or spin-waiting

## ThreadPoolExecutor Triage
Check these when the pool misbehaves:
1. `getPoolSize()` — how many threads exist
2. `getActiveCount()` — how many are executing tasks
3. `getQueue().size()` — how many tasks are queued
4. `getCompletedTaskCount()` — throughput indicator
5. `getLargestPoolSize()` — historical peak

If activeCount == poolSize and queue is growing, increase core/max pool size. If activeCount is significantly less than poolSize, threads are dying — check the rejection handler and uncaught exception handler.

## ForkJoinPool Debugging
- `getStealCount()` — should be non-zero for a well-balanced workload
- `getQueuedTaskCount()` — queued tasks across all workers
- `getRunningThreadCount()` — threads actively executing

A steal count of 0 with parallelism > 1 and queued tasks > 0 means work-stealing is not occurring — check that tasks use fork()/join() correctly.

## CompletableFuture Debugging
Use `future.state()` (Java 21) to check completion status:
- `RUNNING` — still executing
- `COMPLETED` — completed normally
- `COMPLETED_EXCEPTIONALLY` — completed with exception
- `CANCELLED` — was cancelled

Check the `defaultExecutor()` — if it's the common pool, blocking operations may starve it.

## StructuredTaskScope Debugging
Common issues:
- `scope.join()` never returns: check that all subtasks eventually complete
- `throwIfFailed()` always succeeds: scope may not have been shut down
- Subtask exceptions lost: check that the scope type matches the desired policy
