# Edge Cases & Pitfalls: Concurrent Utilities

Synchronizers simplify thread coordination, but they introduce new failure modes, particularly around thread interruption, exception handling, and permit leakage.

## 1. The `CountDownLatch` Deadlock (Missed `countDown`)
*   **The Scenario**: A master thread awaits a latch of size 3. Three worker threads are spawned to do work and call `countDown()`.
*   **The Pitfall**: If one of the worker threads throws an unhandled exception *before* it reaches the `countDown()` method, the thread dies. The latch count will permanently remain at 1. The master thread calling `await()` will wait forever, causing a deadlock.
*   **Mitigation**: Always place `latch.countDown()` inside a `finally` block to guarantee execution, even if the worker task fails.
    ```java
    try {
        doWork();
    } finally {
        latch.countDown();
    }
    ```

## 2. Semaphore Permit Leakage
*   **The Scenario**: You use a `Semaphore(5)` to limit database connections. A thread acquires a permit, performs a query, and then releases it.
*   **The Pitfall**: Similar to the latch issue, if the database query throws an exception, the `release()` method is skipped. The permit is permanently lost. After 5 exceptions, the Semaphore has 0 permits, and all future threads block forever.
*   **Mitigation**: Always use `try { ... } finally { semaphore.release(); }`.

## 3. The `CyclicBarrier` Broken State
*   **The Scenario**: 4 threads are waiting at a `CyclicBarrier(5)`. The 5th thread is interrupted by the OS or another thread while doing its pre-barrier work.
*   **The Pitfall**: If a thread waiting at a barrier is interrupted, or if it times out, the barrier is considered **broken**. All *other* threads currently waiting at the barrier will immediately wake up and throw a `BrokenBarrierException`. This cascading failure is intentional (to prevent the other threads from waiting forever for a thread that will never arrive), but it requires careful exception handling.
*   **Mitigation**: Catch `BrokenBarrierException`. If caught, the operation has failed and must be aborted or restarted. You can reset the barrier using `barrier.reset()`, but you must ensure all threads are coordinated to start the new cycle.

## 4. `Semaphore` Permit Inflation
*   **The Scenario**: You initialize a `Semaphore(1)`. A thread calls `release()` without ever calling `acquire()`.
*   **The Pitfall**: The `Semaphore` does not track *who* owns the permits. If you call `release()`, the permit count simply increments. Your `Semaphore(1)` is now a `Semaphore(2)`. This completely breaks the intended capacity limit or mutual exclusion.
*   **Mitigation**: Ensure strict symmetry between `acquire()` and `release()`. If you are using a Semaphore as a Mutex (binary semaphore), consider using a `ReentrantLock` instead, which strictly enforces ownership (only the thread holding the lock can release it).

## 5. `Phaser` Unregistered Arrival
*   **The Scenario**: You use a `Phaser`. A thread that did not call `register()` attempts to call `arrive()`.
*   **The Pitfall**: It throws an `IllegalStateException`. A Phaser strictly tracks the number of registered parties vs. the number of arrived parties.
*   **Mitigation**: Ensure dynamic registration is handled correctly, often by having the parent thread call `register()` *before* spawning the child thread, rather than the child thread registering itself (which introduces a race condition).