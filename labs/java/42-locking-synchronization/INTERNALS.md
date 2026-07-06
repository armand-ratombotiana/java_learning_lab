# Locking Internals

## Object Header and Lock State
Every Java object has a mark word (8 bytes on 64-bit) in its header:
```
[unused:25 | hash:31 | age:4 | biased_lock:1 | lock:2]
```
Lock bits: 00 = thin lock, 01 = biased, 10 = inflated (heavyweight), 11 = GC marking.
The mark word stores the lock record pointer (thin) or mutex pointer (inflated).

## AQS CLH Queue Internals
AQS maintains a `Node` class with:
- `waitStatus`: SIGNAL (-1), CANCELLED (1), CONDITION (-2), PROPAGATE (-3), 0 (initial)
- `prev`, `next`: doubly-linked queue
- `thread`: the waiting thread
- `nextWaiter`: for Condition queue

`acquireQueued()` spins on `shouldParkAfterFailedAcquire()` which checks the predecessor's waitStatus. If predecessor is SIGNAL, park. If CANCELLED, skip predecessors. Otherwise, CAS predecessor to SIGNAL.

## ReentrantLock Fairness
- **NonfairSync**: `lock()` does CAS immediately (barging). If CAS fails, falls through to `acquire(1)`.
- **FairSync**: `lock()` calls `acquire(1)` directly, which calls `tryAcquire`. `tryAcquire` checks `hasQueuedPredecessors()` — if any thread is queued before the current thread, the current thread must queue.

## StampedLock Validation
StampedLock embeds a version counter in each stamp. When a write lock is acquired, the version increments. `validate(stamp)` checks whether the version is still current. If a write occurred after the stamp was obtained, validation fails.

## Unsafe CAS and Memory Ordering
`Unsafe.compareAndSwapInt` provides atomic read-modify-write with full memory barrier semantics:
- On x86: `lock cmpxchg [addr], reg` — the LOCK prefix provides a full memory fence
- On ARM: `ldrex/strex` pair with `dmb` (data memory barrier)

Java's `VarHandle` (Java 9+) provides finer-grained memory ordering:
- `getVolatile`, `setVolatile`: full volatile semantics
- `getOpaque`, `setOpaque`: prevents compiler reordering
- `getAcquire`, `setRelease`: one-way barrier
