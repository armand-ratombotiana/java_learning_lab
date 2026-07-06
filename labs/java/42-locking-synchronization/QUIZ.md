# Quiz: Locking & Synchronization

1. What does the `synchronized` keyword compile to at bytecode level?
   a) lock/unlock
   b) monitorenter/monitorexit
   c) enter/exit
   d) acquire/release

2. What is the purpose of AQS's CLH queue?
   a) To store thread-local variables
   b) To maintain a queue of waiting threads
   c) To track heap allocations
   d) To schedule threads on cores

3. Which StampedLock method provides non-blocking read access?
   a) readLock()
   b) tryReadLock()
   c) tryOptimisticRead()
   d) peekLock()

4. When would a fair ReentrantLock be slower than an unfair one?
   a) Under no contention
   b) Under high contention
   c) With a single thread
   d) Never

5. What is the difference between `park()` and `wait()`?
   a) park doesn't need a monitor; wait does
   b) park requires a monitor; wait doesn't
   c) They are the same
   d) park is for threads; wait is for processes

6. What value does Unsafe.compareAndSwapInt return?
   a) The new value
   b) The old value
   c) boolean (true if successful)
   d) void

7. Which of the following is NOT a lock state for Java monitors?
   a) Biased
   b) Thin
   c) Fat (inflated)
   d) Reentrant

8. What happens when StampedLock.tryOptimisticRead() fails validation?
   a) The read data is inconsistent
   b) An exception is thrown
   c) The stamp is automatically renewed
   d) A write lock is acquired

## Answer Key
1-b, 2-b, 3-c, 4-b, 5-a, 6-c, 7-d, 8-a
