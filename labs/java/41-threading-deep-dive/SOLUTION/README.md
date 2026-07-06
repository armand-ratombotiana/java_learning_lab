# Solutions: Threading Deep Dive

## ThreadLifecycle.java
Thread states are observable via `Thread.getState()`. The demo creates a thread and monitors its transition through NEW → RUNNABLE → TIMED_WAITING → TERMINATED. A waiter thread demonstrates BLOCKED state by contending for a monitor lock. The `describeState()` method maps each enum constant to its semantics.

## ThreadPoolInternals.java
Demonstrates ThreadPoolExecutor with core=2, max=4, ArrayBlockingQueue capacity=2. When 10 tasks are submitted, the first 2 run immediately on core threads, the next 2 queue, the next 2 trigger max thread creation, and the remaining 4 trigger rejection. The custom rejection handler logs rejected tasks.

## ForkJoinPoolDemo.java
RecursiveTask splits arrays smaller than 10,000 elements. ForkJoinPool.commonPool() with parallelism=4 processes the 100K-element array. The `fork()` method pushes the left task to the worker's deque; `compute()` processes the right task; `join()` waits for the left. Steal count > 0 confirms work-stealing.

## CompletableFutureDeepDive.java
- thenApply: transforms value synchronously on completion
- thenCompose: flatMap to avoid CompletableFuture<CompletableFuture>
- exceptionally: recovers from errors with a fallback value
- allOf: blocks until all futures complete
- anyOf: returns result of the first completed future

## StructuredConcurrencyExample.java
StructuredTaskScope (JEP 428) creates a scope where subtasks are children of the scope. ShutdownOnSuccess cancels all remaining tasks when one succeeds. ShutdownOnFailure tracks all failures, propagating the first failure. Both ensure all subtasks complete (or are cancelled) before the scope returns.
