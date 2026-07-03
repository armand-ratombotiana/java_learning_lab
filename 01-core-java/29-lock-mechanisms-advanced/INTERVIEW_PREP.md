# Interview Preparation: Advanced Lock Mechanisms

This document covers advanced questions related to `ReentrantLock`, `ReadWriteLock`, `StampedLock`, and `Condition` variables.

## Q1: What are the advantages of `ReentrantLock` over the `synchronized` keyword?
**Answer:**
While `synchronized` is simpler and less prone to developer error (no missing `finally` blocks), `ReentrantLock` offers advanced features:
1.  **Non-blocking acquisition**: `tryLock()` returns `false` immediately if the lock is held, rather than blocking the thread forever.
2.  **Timeouts**: `tryLock(long time, TimeUnit unit)` allows a thread to wait for a specific duration before giving up.
3.  **Interruptibility**: `lockInterruptibly()` allows a waiting thread to be interrupted by another thread, throwing an `InterruptedException` and preventing deadlocks.
4.  **Fairness**: It can be instantiated as a "fair" lock, guaranteeing that threads acquire the lock in the exact order they requested it (FIFO), preventing starvation.

## Q2: Explain the concept of "Lock Downgrading" and "Lock Upgrading" in a `ReentrantReadWriteLock`.
**Answer:**
*   **Lock Downgrading**: A thread holding the Write lock acquires the Read lock, and then releases the Write lock. This is **supported** and safe. It allows a writer to cleanly transition to a reader without giving another writer a chance to modify the data in between.
*   **Lock Upgrading**: A thread holding the Read lock attempts to acquire the Write lock. This is **NOT supported** and will cause a permanent deadlock. The thread will wait forever for itself to release the Read lock. To upgrade, the thread must explicitly release the Read lock before requesting the Write lock.

## Q3: Why must `Condition.await()` always be called inside a `while` loop?
**Answer:**
Because of **Spurious Wakeups**.
The underlying operating system or hardware can occasionally wake up a waiting thread even if `Condition.signal()` was never called. 
Furthermore, even if the wakeup was legitimate, another thread might have acquired the lock and changed the state of the shared resource *between* the time the signal was sent and the waiting thread fully woke up and re-acquired the lock.
By putting the `await()` call inside a `while(conditionIsNotMet)` loop, the thread immediately re-checks the condition upon waking up. If the condition is still not met, it goes back to sleep.

## Q4: How does `StampedLock` achieve higher throughput than `ReentrantReadWriteLock` in read-heavy scenarios?
**Answer:**
`ReentrantReadWriteLock` suffers from writer starvation (if there are constant readers, a writer never gets in) and still incurs CPU cache-coherency overhead because readers must update the lock's internal state to register their presence.
`StampedLock` introduces **Optimistic Reading**. `tryOptimisticRead()` returns a stamp but does *not* actually acquire a lock or write to memory. The thread reads the shared variables and then calls `validate(stamp)`. If a writer acquired the write lock during the read process, validation fails, and the reader falls back to a standard pessimistic read lock. If validation succeeds, the read was practically free, resulting in massive throughput gains.

## Q5: What happens if an exception is thrown inside a critical section protected by `ReentrantLock` and there is no `finally` block?
**Answer:**
The lock is never released. Any other thread attempting to acquire that lock will block indefinitely, causing a deadlock. 
Unlike the `synchronized` keyword, which guarantees monitor release via the JVM bytecode (using `monitorexit` even on exception paths), explicit `Lock` implementations are just Java objects. You must explicitly call `lock.unlock()` inside a `finally` block to guarantee release.