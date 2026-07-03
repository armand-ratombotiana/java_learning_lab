# Deep Dive: Advanced Lock Mechanisms

## 1. Beyond `synchronized`
The `synchronized` keyword (intrinsic locks or monitor locks) is the foundation of Java concurrency. It is easy to use and prevents basic race conditions. However, it has severe limitations:
*   **Uninterruptible**: A thread waiting for a `synchronized` block cannot be interrupted.
*   **No Timeouts**: A thread will wait forever if the lock is held by another thread.
*   **Strict Scoping**: The lock must be acquired and released in the same lexical block.
*   **Fairness**: It does not guarantee that the longest-waiting thread gets the lock next.

To solve these issues, Java 5 introduced the `java.util.concurrent.locks.Lock` interface.

## 2. `ReentrantLock`
`ReentrantLock` implements the `Lock` interface. Like `synchronized`, it is "reentrant" (a thread holding the lock can acquire it again without deadlocking itself).

### Advanced Capabilities:
*   **`tryLock()`**: Attempts to acquire the lock. If it's not available, it immediately returns `false` instead of blocking, allowing the thread to do other work.
*   **`tryLock(long time, TimeUnit unit)`**: Waits for a specific amount of time before giving up.
*   **`lockInterruptibly()`**: Waits for the lock but will throw an `InterruptedException` if another thread interrupts it.
*   **Fairness**: `new ReentrantLock(true)` creates a "fair" lock, ensuring threads acquire the lock in the exact order they requested it (FIFO). *Note: Fairness significantly reduces throughput.*

```java
Lock lock = new ReentrantLock();
// Standard idiom: Always unlock in a finally block!
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();
}
```

## 3. `ReadWriteLock`
In many applications, data is read much more frequently than it is written. A standard lock forces all readers to wait for each other, which is highly inefficient.
`ReadWriteLock` maintains a pair of associated locks: one for read-only operations and one for writing.
*   **Read Lock**: Multiple threads can hold the read lock simultaneously, *provided no thread holds the write lock*.
*   **Write Lock**: Exclusive. Only one thread can hold it, and no read locks can be held.

```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();
// Readers acquire readLock, Writers acquire writeLock
```

## 4. `StampedLock` (Java 8+)
While `ReadWriteLock` improves read performance, it still suffers from "writer starvation" (if there is a continuous stream of readers, a writer might never get the lock).
`StampedLock` introduces a new paradigm: **Optimistic Reading**.

### Optimistic Reading
Instead of acquiring a lock, you ask for a "stamp" (a `long` value representing the lock state). You read the data without blocking anyone. Then, you validate the stamp. If a writer acquired the lock while you were reading, the validation fails, and you must fall back to a traditional read lock.

*   **Performance**: In highly concurrent read-heavy scenarios, optimistic reading is incredibly fast because it involves no CPU-level locking or context switching unless a collision actually occurs.
*   **Limitation**: It is *not* reentrant.

```java
StampedLock sl = new StampedLock();

// Optimistic Read
long stamp = sl.tryOptimisticRead();
int currentX = this.x; // Read data without locking
int currentY = this.y;

// Check if a writer modified the data while we were reading
if (!sl.validate(stamp)) {
    // Validation failed! Fall back to a pessimistic read lock
    stamp = sl.readLock();
    try {
        currentX = this.x;
        currentY = this.y;
    } finally {
        sl.unlockRead(stamp);
    }
}
```

## 5. Condition Variables
Sometimes a thread acquires a lock but realizes it cannot proceed until some condition is met (e.g., a buffer is not empty).
With `synchronized`, you use `wait()` and `notify()`. With `Lock`, you use `Condition`.

A single `Lock` can have *multiple* `Condition` objects. This allows you to group waiting threads and notify them specifically.
```java
Lock lock = new ReentrantLock();
Condition notFull  = lock.newCondition(); 
Condition notEmpty = lock.newCondition(); 

// Producer thread:
lock.lock();
try {
    while (count == items.length) {
        notFull.await(); // Releases lock, waits to be signaled
    }
    // add item...
    notEmpty.signal(); // Wakes up a consumer thread waiting on notEmpty
} finally {
    lock.unlock();
}
```