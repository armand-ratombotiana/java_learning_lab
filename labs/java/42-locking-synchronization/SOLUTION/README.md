# Solutions: Locking & Synchronization

## AqsLock.java
Implements a non-reentrant exclusive lock by extending AQS. `tryAcquire` uses `compareAndSetState(0, 1)` to atomically claim the lock. `tryRelease` resets state to 0 and clears the owner thread. The CLH queue in AQS handles waiting threads, parking them via LockSupport.

## ReentrantLockDemo.java
Fair locks grant access to the longest-waiting thread (via AQS's `hasQueuedPredecessors`), while unfair locks allow barging. The demo runs 5 threads incrementing a counter 1000 times each. `tryLock` returns immediately if unavailable. `lockInterruptibly` throws `InterruptedException` if the thread is interrupted while waiting.

## StampedLockDemo.java
Three locking modes:
- Write lock: exclusive, traditional lock semantics
- Read lock: shared, multiple readers
- Optimistic read: non-blocking, must validate before using data

`tryOptimisticRead()` returns a stamp without blocking. After reading, `validate(stamp)` checks if a write occurred. If validation fails, fall back to read lock. `tryConvertToWriteLock` upgrades without releasing the read lock first.

## LockSupportDemo.java
`LockSupport.park()` blocks the current thread until `unpark(thread)` is called. Each thread has a permit (like a semaphore with max 1). `park` consumes the permit; `unpark` provides one. The Blocker object is used by `Thread.getStackTrace()` and monitoring tools to identify why a thread is parked.

## CasCounter.java
Uses `Unsafe.compareAndSwapInt(object, offset, expected, newValue)` for atomic increment. The offset is the memory address of the `value` field (computed once via `objectFieldOffset`). The retry loop (`do...while`) handles CAS failure from concurrent writes. This is exactly how `AtomicInteger.incrementAndGet()` works internally.
