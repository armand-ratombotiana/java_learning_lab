# Code Deep Dive: Locking Internals

## AqsLock.java Analysis
This custom lock extends AQS and implements a simple non-reentrant lock. The `Sync` inner class overrides three methods:
- `tryAcquire(int acquires)`: CAS on state from 0 to 1. The `acquires` parameter (from AQS convention) is always 1. On success, sets the exclusive owner thread via `setExclusiveOwnerThread()`.
- `tryRelease(int releases)`: Resets state to 0 and clears the owner. Throws `IllegalMonitorStateException` if the lock is not held (state was already 0).
- `isHeldExclusively()`: Returns true if state != 0 AND the current thread is the owner.

The lock is NOT reentrant — if the owning thread calls `lock()` again, `tryAcquire` returns false because state is already 1, causing deadlock. Reentrancy would require state to track the hold count.

## ReentrantLockDemo.java Analysis
The fair vs unfair comparison is the critical insight. With unfair lock, threads can "barge" ahead of queued threads. This reduces latency for the barging thread but increases variance. The fair lock guarantees FIFO ordering by checking `hasQueuedPredecessors()` before attempting CAS.

`tryLock()` does not honor the fairness setting — it always barges (CAS directly). This is intentional: tryLock is meant for non-blocking acquisition attempts.

`lockInterruptibly()` calls `acquireInterruptibly(1)`, which throws `InterruptedException` if the thread is interrupted while waiting. This enables responsive cancellation.

## StampedLockDemo.java Analysis
The `readOptimistic()` method demonstrates the pattern: `tryOptimisticRead()` returns a stamp without blocking. After reading x and y, `validate(stamp)` checks if a write occurred. If validation fails, the method falls back to a pessimistic read lock. This fallback ensures correctness at the cost of occasional read lock acquisition.

## CasCounter.java Analysis
The key line is `UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, prev, prev + 1)` in a retry loop. `VALUE_OFFSET` is computed once via `objectFieldOffset` — this is the memory address of the `value` field within the CasCounter object. The `volatile` keyword on `value` is crucial: it ensures the read in the CAS loop sees the latest write from any thread.
