# Quizzes: Advanced Lock Mechanisms

Test your knowledge of explicit locks, ReadWrite mechanisms, and Condition variables.

## Quiz 1: ReentrantLock vs Synchronized

**Q1: Which of the following is a capability of `ReentrantLock` that is NOT possible with the `synchronized` keyword?**
- A) Reentrancy (a thread holding the lock can acquire it again).
- B) Mutual exclusion (only one thread can hold the lock at a time).
- C) Attempting to acquire the lock and immediately returning `false` if it is currently held by another thread, rather than blocking.
- D) Automatically releasing the lock if an exception is thrown.
*Answer: C (Using `tryLock()`. Note that D is actually a feature of `synchronized` that `ReentrantLock` lacks, requiring a `finally` block).*

**Q2: What is the primary performance drawback of instantiating a `ReentrantLock` with the "fairness" parameter set to `true`?**
- A) It causes deadlocks more frequently.
- B) It significantly reduces overall throughput because it prevents "barging" and forces expensive thread context switches to maintain strict FIFO order.
- C) It consumes more heap memory.
- D) It prevents read locks from being acquired.
*Answer: B*

## Quiz 2: ReadWrite and Stamped Locks

**Q1: In a `ReentrantReadWriteLock`, what happens if Thread A holds the Read lock, and then attempts to acquire the Write lock?**
- A) Thread A successfully acquires the Write lock (Lock Upgrading).
- B) The Read lock is automatically released, and Thread A acquires the Write lock.
- C) An `IllegalMonitorStateException` is thrown.
- D) Thread A blocks forever (Deadlock), because Lock Upgrading is not supported.
*Answer: D*

**Q2: How does `StampedLock`'s "Optimistic Reading" improve performance in read-heavy scenarios?**
- A) It uses multiple threads to read the data simultaneously.
- B) It does not actually acquire a lock or perform expensive volatile memory writes during the read phase; it only checks a stamp afterward to see if a writer interfered.
- C) It caches the read data in CPU L1 cache.
- D) It prevents writers from ever acquiring the lock.
*Answer: B*

## Quiz 3: Condition Variables

**Q1: Why must a call to `Condition.await()` always be placed inside a `while` loop checking the condition, rather than an `if` statement?**
- A) Because `await()` returns a boolean that must be checked.
- B) Because of "spurious wakeups" (the OS waking the thread without a signal) and the possibility that another thread altered the state between the signal and the thread fully waking up.
- C) Because the compiler requires it.
- D) To prevent the thread from consuming CPU cycles.
*Answer: B*