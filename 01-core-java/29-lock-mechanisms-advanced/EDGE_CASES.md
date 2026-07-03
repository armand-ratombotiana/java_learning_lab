# Edge Cases & Pitfalls: Advanced Lock Mechanisms

Advanced locks provide fine-grained control, but with great power comes great responsibility. Misusing them can lead to deadlocks, starvation, and severe performance degradation.

## 1. The Missing `finally` Block
*   **The Scenario**: You use a `ReentrantLock` but fail to put the `unlock()` call inside a `finally` block.
    ```java
    lock.lock();
    doWork(); // Throws a RuntimeException
    lock.unlock(); // Never executes!
    ```
*   **The Pitfall**: If `doWork()` throws an exception, the thread crashes and the lock is *never released*. Every other thread waiting for that lock is now permanently deadlocked. Unlike `synchronized` blocks, which automatically release the monitor lock upon exiting the block (even via exception), explicit locks require explicit release.
*   **Mitigation**: The standard idiom is non-negotiable: `lock.lock(); try { ... } finally { lock.unlock(); }`.

## 2. Lock Downgrading vs. Upgrading (`ReentrantReadWriteLock`)
*   **The Scenario**: You hold a Read lock. You realize you need to write data, so you try to acquire the Write lock.
    ```java
    rwLock.readLock().lock();
    // ...
    rwLock.writeLock().lock(); // DEADLOCK!
    ```
*   **The Pitfall**: `ReentrantReadWriteLock` allows **Lock Downgrading** (acquiring the Write lock, then acquiring the Read lock, then releasing the Write lock). However, it strictly forbids **Lock Upgrading**. If a thread holding a Read lock tries to acquire the Write lock, it will block forever, waiting for itself to release the Read lock.
*   **Mitigation**: You must explicitly release the Read lock before requesting the Write lock.

## 3. StampedLock Optimistic Read Corruption
*   **The Scenario**: You perform an optimistic read using `StampedLock`. You read an object reference, and then call a method on that object *before* validating the stamp.
    ```java
    long stamp = sl.tryOptimisticRead();
    User u = this.currentUser;
    String name = u.getName(); // DANGER!
    if (!sl.validate(stamp)) { ... }
    ```
*   **The Pitfall**: Optimistic reads do not prevent writers from modifying data. While you were reading `this.currentUser`, a writer might have set it to `null` or left it in an inconsistent state. Calling `u.getName()` could throw a `NullPointerException` or cause unpredictable behavior before you ever reach the `validate()` check.
*   **Mitigation**: Only read primitive values or copy references to local variables during the optimistic phase. Perform any operations that could throw exceptions or cause side effects *after* validating the stamp.

## 4. Condition Variable "Spurious Wakeups"
*   **The Scenario**: You use `Condition.await()` to wait for a buffer to become not empty. You use an `if` statement to check the condition.
    ```java
    if (buffer.isEmpty()) {
        notEmpty.await();
    }
    // Proceed assuming buffer is not empty
    ```
*   **The Pitfall**: The OS can wake up a waiting thread even if `notEmpty.signal()` was never called. This is a well-known hardware/OS phenomenon called a "spurious wakeup". Furthermore, even if it was a legitimate signal, another thread might have grabbed the lock and emptied the buffer before your thread fully woke up.
*   **Mitigation**: **Always** wait inside a `while` loop, never an `if` statement.
    ```java
    while (buffer.isEmpty()) { notEmpty.await(); }
    ```

## 5. Fair Lock Throughput Penalty
*   **The Scenario**: You initialize a lock with `new ReentrantLock(true)` to ensure that threads acquire the lock in strict FIFO order, preventing starvation.
*   **The Pitfall**: Fair locks require maintaining a complex internal queue and forcing context switches to ensure the exact right thread wakes up next. This overhead is massive. A fair lock can be 10x to 100x slower than an unfair lock (which allows "barging"—a new thread grabbing the lock immediately if it happens to be available when requested).
*   **Mitigation**: Only use fair locks if absolute FIFO ordering is a strict business requirement or if thread starvation is a proven, critical issue. Default to unfair locks for performance.