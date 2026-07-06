# Common Mistakes in Locking

## Mistake 1: Locking on Non-Final Fields
Synchronizing on a field that can change:
```java
synchronized (lock) { ... }  // lock is non-final, can be reassigned
```
If another thread reassigns `lock`, two threads can enter the synchronized block simultaneously on different objects. Always synchronize on a final field or `this`.

## Mistake 2: Forgetting try-finally
```java
lock.lock();
// code that throws
lock.unlock(); // never reached if exception thrown
```
Always use try-finally: `lock.lock(); try { ... } finally { lock.unlock(); }`.

## Mistake 3: Misunderstanding Reentrancy
AQS-based locks like the custom AqsLock are NOT reentrant by default. If the owning thread calls `lock()` again, it deadlocks. ReentrantLock handles this by tracking hold count. Custom AQS locks must override `tryAcquire` to check if the current thread is already the owner.

## Mistake 4: Unfair StampedLock Performance
StampedLock's optimistic reads are not always faster than read locks. If writes are frequent (>10% of operations), optimistic reads fail validation frequently, and the fallback to read lock adds overhead. Profile before using optimistic reads.

## Mistake 5: Wrong Lock Type for Workload
- read-heavy + infrequent writes → StampedLock (optimistic)
- balanced read/write → ReentrantLock or ReadWriteLock
- write-heavy → synchronized or ReentrantLock
- ultra-low contention → CAS (atomic classes)

## Mistake 6: CAS Without Volatile
If the field is not volatile, the CAS loop reads a stale value and may spin forever (the write from the CAS is visible, but the initial read might be cached). Always use `volatile` with CAS-accessed fields.

## Mistake 7: Forgetting Memory Ordering
CAS provides a full memory barrier, but plain reads/writes may still be reordered. Use `VarHandle` with acquire/release modes for fine-grained control, or stick with volatile for simplicity.
