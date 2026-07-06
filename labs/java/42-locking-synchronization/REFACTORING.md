# Refactoring Locking Code

## From synchronized to ReentrantLock
**Before:**
```java
public synchronized int increment() { return ++count; }
```
**After:**
```java
private final ReentrantLock lock = new ReentrantLock();
public int increment() {
    lock.lock();
    try { return ++count; } finally { lock.unlock(); }
}
```
Benefits: tryLock, lockInterruptibly, Condition, fairness selection.

## From double-checked locking to ReentrantLock or volatile
**Before (broken):**
```java
if (instance == null) { synchronized (this) { if (instance == null) instance = new X(); } }
```
**After:**
```java
private static volatile Instance instance;
// or use an inner static holder class
```
The volatile ensures the `happens-before` for the instance reference.

## From ReadWriteLock to StampedLock
**Before:**
```java
ReadWriteLock rw = new ReentrantReadWriteLock();
rw.readLock().lock(); try { read data } finally { rw.readLock().unlock(); }
```
**After:**
```java
StampedLock sl = new StampedLock();
long stamp = sl.tryOptimisticRead();
int x = data.x, y = data.y;
if (!sl.validate(stamp)) {
    stamp = sl.readLock();
    try { x = data.x; y = data.y; } finally { sl.unlockRead(stamp); }
}
```
Benefits: non-blocking optimistic reads for read-heavy workloads.

## From synchronized counter to AtomicInteger
**Before:**
```java
private int count;
public synchronized int inc() { return count++; }
```
**After:**
```java
private final AtomicInteger count = new AtomicInteger();
public int inc() { return count.incrementAndGet(); }
```
Benefits: lock-free, better scalability under contention.

## From wait/notify to LockSupport
**Before:**
```java
synchronized (obj) { while (!condition) obj.wait(); }
```
**After:**
```java
if (!condition) LockSupport.park();
```
Benefits: no monitor required, permit accumulates, interruptible with isInterrupted() check.
