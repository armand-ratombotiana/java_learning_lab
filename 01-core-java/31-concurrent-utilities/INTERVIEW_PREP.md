# Interview Preparation: Concurrent Utilities

This document covers advanced questions related to thread coordination, barriers, and semaphores.

## Q1: Explain the difference between `CountDownLatch` and `CyclicBarrier`.
**Answer:**
1.  **Reusability**: `CountDownLatch` is a one-shot synchronizer. Once the count reaches zero, it cannot be reset. `CyclicBarrier` is reusable; once the barrier is tripped, it resets for the next cycle.
2.  **Mechanics**: A latch is decremented by calling `countDown()`, which does not block the calling thread. Other threads wait for the count to reach zero using `await()`. A barrier blocks *every* thread that calls `await()` until a specified number of threads have arrived.
3.  **Barrier Action**: `CyclicBarrier` allows you to specify a `Runnable` that executes exactly once when the barrier is tripped, before the waiting threads are released. `CountDownLatch` has no such feature.

## Q2: How does a `Semaphore` differ from a `ReentrantLock`?
**Answer:**
A `ReentrantLock` provides **Mutual Exclusion** (only one thread can hold the lock at a time). Furthermore, it tracks ownership: only the thread that acquired the lock can release it.
A `Semaphore` provides **Capacity Control**. It holds a pool of permits (e.g., 5). Up to 5 threads can acquire permits simultaneously. Furthermore, a `Semaphore` does *not* track ownership. Thread A can call `acquire()`, and Thread B can call `release()`. This makes Semaphores useful for signaling, but dangerous if `release()` is called erroneously (which inflates the permit pool).

## Q3: What happens if a thread waiting at a `CyclicBarrier` is interrupted?
**Answer:**
If a thread waiting at a `CyclicBarrier` is interrupted (or times out), the barrier enters a **broken state**.
The interrupted thread throws an `InterruptedException`. Crucially, all *other* threads currently waiting at the barrier will immediately wake up and throw a `BrokenBarrierException`. This is a fail-fast mechanism to prevent threads from waiting forever for a peer that will never arrive. The barrier must be explicitly `reset()` before it can be used again.

## Q4: When would you use a `Phaser` instead of a `CyclicBarrier`?
**Answer:**
You use a `Phaser` when the number of parties participating in the synchronization is **dynamic** (not known in advance, or changes over time).
A `CyclicBarrier` requires you to specify the exact number of parties in its constructor. A `Phaser` allows threads to dynamically `register()` and `arriveAndDeregister()` themselves at runtime. It also tracks "phases" (generation numbers), making it highly flexible for complex, multi-stage fork/join algorithms where worker threads are spawned and destroyed dynamically.

## Q5: How can a `CountDownLatch` cause a permanent deadlock, and how do you prevent it?
**Answer:**
A deadlock occurs if a worker thread throws an unhandled exception or hangs *before* it calls `latch.countDown()`. The latch count will never reach zero, and any thread calling `latch.await()` will block indefinitely.
**Prevention**: 
1.  Always place `countDown()` inside a `finally` block to ensure it executes regardless of task success or failure.
2.  Instead of `await()`, use `await(long timeout, TimeUnit unit)`. If the latch isn't released within a reasonable timeframe, the waiting thread can wake up, log an error, and recover rather than hanging forever.