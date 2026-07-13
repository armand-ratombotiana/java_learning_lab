# Advanced Locks Internals

## ⚙️ AbstractQueuedSynchronizer (AQS)
Almost all advanced synchronizers in `java.util.concurrent` (`ReentrantLock`, `CountDownLatch`, `Semaphore`, `ReentrantReadWriteLock`) are built on top of a single, brilliant framework: the **AbstractQueuedSynchronizer (AQS)**.

AQS provides a framework for implementing blocking locks and related synchronizers that rely on FIFO wait queues.

### How AQS Works
1. **The State**: AQS maintains a single, `volatile int state` variable. 
   - For a `ReentrantLock`, `state = 0` means unlocked, `state = 1` means locked. (If the same thread locks it again, `state = 2`, which is why it's "reentrant").
   - For a `CountDownLatch(5)`, `state = 5`. Every `countDown()` decrements it. When `state == 0`, waiting threads are unparked.
2. **The Wait Queue**: If a thread tries to acquire the lock and fails (because `state` is already taken), AQS creates a `Node` representing that thread and appends it to a strict FIFO doubly-linked list (the wait queue).
3. **Parking**: AQS then uses `LockSupport.park()` to suspend the thread at the OS level, removing it from CPU scheduling.
4. **Unparking**: When the thread holding the lock releases it, AQS takes the next `Node` from the head of the wait queue and calls `LockSupport.unpark()` to wake it up and hand it the lock.

This architecture completely eliminates the need for busy-waiting (spin loops) and provides massive scalability.

## 🔬 StampedLock Mechanics
`StampedLock` is not based on AQS. It is a highly optimized, complex custom implementation.

When you call `stampedLock.tryOptimisticRead()`, it does **not** acquire a lock. It simply reads an internal `volatile long state` variable and returns it as a "stamp".

You then read your data.
After reading, you call `stampedLock.validate(stamp)`.
- If a writer acquired the write lock while you were reading, the internal `state` variable was changed. The `validate` method will see that the current state no longer matches your stamp, and it will return `false`.
- If it returns `true`, you know absolutely no writer touched the data while you were reading, meaning your read data is perfectly consistent.

This avoids the memory contention of updating the AQS state variable for every single reader, providing near-native read speeds.