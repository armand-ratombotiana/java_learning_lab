# Step-by-Step: Threading Internals

## Step 1: Run ThreadLifecycle.java
Execute `ThreadLifecycle.main()`. Observe the thread state transitions printed. The lifecycle-thread starts in NEW, transitions to RUNNABLE after start(), then to TIMED_WAITING during sleep(). The waiter-thread demonstrates BLOCKED while contending for the lock. Finally the lifecycle-thread reaches TERMINATED.

## Step 2: Run ThreadPoolInternals.java
Watch how tasks are assigned to threads. The first two tasks create worker threads. Tasks 2-3 are queued. Tasks 4-5 create additional workers (reaching max=4). Tasks 6-9 are rejected. The rejection handler prints "[REJECTED]". After the pool shuts down, examine the final pool metrics.

## Step 3: Run ForkJoinPoolDemo.java
The common pool has parallelism equal to available processors. The RecursiveTask splits the 100K array until sub-arrays are 10K or smaller. Observe the steal count — a non-zero value confirms work-stealing occurred. Compare with sequential sum performance.

## Step 4: Run CompletableFutureDeepDive.java
Each section demonstrates a different aspect:
- thenApply: chain of two transformations
- thenCompose: flat-mapping to avoid nested CompletableFuture
- exceptionally: error recovery path
- allOf: waiting for three independent futures
- anyOf: taking the first completed result
- Explicit complete: manually completing a CompletableFuture

## Step 5: Run StructuredConcurrencyExample.java
ShutdownOnSuccess forks two tasks with different latencies. The first task to complete successfully shuts down the scope, cancelling the other. ShutdownOnFailure runs both tasks and calls throwIfFailed() to propagate any exception.

## Step 6: Run ThreadingDeepDiveTest.java
Execute all JUnit tests. Each test verifies a specific aspect of threading internals. The tests are designed to be deterministic and independent.
