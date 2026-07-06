# How Locking Works

## synchronized Block Execution
When the JVM encounters `monitorenter` (entering a synchronized block), it:
1. Checks if the object header's biased locking bit is set and the bias matches the current thread
2. If biased: enters without CAS
3. If not biased: attempts CAS on the object header to set the thin lock
4. If CAS fails: inflates the lock to an OS mutex and blocks the thread

On `monitorexit`, the JVM reverses the process: CAS to release thin lock, or OS call to release mutex.

## ReentrantLock with AQS
When `lock()` is called on a ReentrantLock:
1. `NonfairSync.lock()` attempts CAS on state (0→1). If successful, sets exclusive owner thread.
2. If CAS fails, calls `acquire(1)` which calls `tryAcquire` (checks if state is 0, or reentrant if current owner).
3. If `tryAcquire` fails, adds current thread to the CLH queue and parks via LockSupport.

Fair mode adds `hasQueuedPredecessors()` check before attempting CAS — if the queue is non-empty, queued threads take priority.

## StampedLock Modes
- **Write lock**: Exclusive, like a standard write lock. State stores the stamp (write lock version).
- **Read lock**: Shared, multiple readers. State tracks read count.
- **Optimistic read**: No lock acquisition. Returns a stamp (version number). After reading, call `validate(stamp)` — if true, data was consistent; if false, a write occurred and the read must be retried.

## LockSupport Mechanics
`LockSupport.park()` checks the permit. If permit is available (1), it consumes it and returns. If permit is 0, the thread blocks (via `Unsafe.park()`). `unpark(thread)` sets the permit to 1. If the thread is blocked, it wakes up. Unlike `wait/notify`, `unpark` can be called before `park`, and the permit will be available when `park` is called.

## CAS Internals
`Unsafe.compareAndSwapInt(Object o, long offset, int expected, int x)` maps to a single CPU instruction (`cmpxchg` on x86, `ldrex/strex` on ARM). The CPU guarantees atomicity at the cache line level. On x86, `cmpxchg` with a LOCK prefix ensures exclusive cache access (cache locking or bus locking).
