# Exercises — Concurrency

## Beginner
1. Create two threads that print numbers 1-10 alternately.
2. Use `synchronized` to make a `Counter` class thread-safe.
3. Create a `Runnable` via lambda and submit it to `ExecutorService`.

## Intermediate
4. Use `ReentrantLock` to implement a thread-safe `Stack`.
5. Write a producer-consumer using `BlockingQueue`.
6. Use `CompletableFuture.supplyAsync` + `thenApply` to chain two async operations.
7. Implement `CountDownLatch` to coordinate 5 worker threads.

## Advanced
8. Implement a custom thread pool with work-stealing.
9. Use `CompletableFuture.allOf` to run 10 parallel tasks and combine results.
10. Build a rate-limiter using `Semaphore`.
11. Compare performance: `synchronized` vs `ReentrantLock` vs `AtomicInteger`.

## Debugging
12. Create and detect a deadlock using jstack.
13. Fix a race condition in a multi-threaded banking application.

## Reflection
14. Use `CompletableFuture.exceptionally` to handle failure in an async pipeline.
