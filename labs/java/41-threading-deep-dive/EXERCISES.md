# Exercises: Threading Deep Dive

## Exercise 1: Thread State Observation
Write a program that creates a thread and uses `Thread.getState()` to observe all six states. Use a second thread to trigger BLOCKED (by holding a lock) and WAITING (by calling `wait()`).

## Exercise 2: Custom Thread Pool
Implement a thread pool without using ThreadPoolExecutor. Use a BlockingQueue, a set of Worker threads, and an AtomicInteger for task counting. Support shutdown() and awaitTermination().

## Exercise 3: RecursiveTask for Fibonacci
Implement `FibonacciTask extends RecursiveTask<Integer>` to compute the nth Fibonacci number in parallel. Compare performance against sequential computation for n=30, 40, 45.

## Exercise 4: CompletableFuture Pipeline
Build a pipeline that:
1. Fetches user data from a simulated remote service (CompletableFuture.supplyAsync)
2. Transforms the user data (thenApply)
3. Fetches order history for the user (thenCompose)
4. Handles errors with exceptionally
5. Combines user + order data with thenAccept

## Exercise 5: Structured Concurrency Race
Implement a distributed voting system using StructuredTaskScope.ShutdownOnSuccess. Fork 3 subtasks that vote on a result. The first result wins; remaining subtasks are cancelled.

## Exercise 6: Threshold Optimization
Write a ForkJoin benchmark that tests different thresholds (100, 1000, 10000, 100000) for a sum task on a 1M-element array. Find the optimal threshold for your machine.
