# Quizzes: Concurrent Utilities

Test your knowledge of CountDownLatch, CyclicBarrier, Semaphore, and Phaser.

## Quiz 1: Latch vs Barrier

**Q1: What is the primary difference between a `CountDownLatch` and a `CyclicBarrier`?**
- A) `CountDownLatch` is for threads, `CyclicBarrier` is for processes.
- B) `CountDownLatch` can only be used once (it cannot be reset), whereas `CyclicBarrier` can be reused after the barrier is tripped.
- C) `CountDownLatch` requires a `Runnable` action, `CyclicBarrier` does not.
- D) `CyclicBarrier` does not block threads.
*Answer: B*

**Q2: In a `CyclicBarrier`, what happens if one of the waiting threads is interrupted?**
- A) The interrupted thread throws an exception, but the others continue waiting.
- B) The barrier is considered "broken". All other waiting threads immediately wake up and throw a `BrokenBarrierException`.
- C) The barrier automatically resets.
- D) The OS kills all threads at the barrier.
*Answer: B*

## Quiz 2: Semaphores

**Q1: How does a `Semaphore` differ from a standard `ReentrantLock`?**
- A) A Semaphore is faster.
- B) A Semaphore does not support fairness.
- C) A Lock allows only one thread to access a resource (mutual exclusion). A Semaphore can allow $N$ threads to access a resource simultaneously (capacity control).
- D) A Semaphore cannot be used in a `finally` block.
*Answer: C*

**Q2: What happens if you call `semaphore.release()` on a `Semaphore(1)` without ever calling `acquire()` first?**
- A) It throws an `IllegalMonitorStateException`.
- B) It does nothing.
- C) The number of available permits increases to 2, effectively inflating the capacity of the Semaphore.
- D) It resets the Semaphore to 0.
*Answer: C (Semaphores do not track thread ownership, unlike Locks).*

## Quiz 3: Phaser

**Q1: Which feature makes `Phaser` more flexible than `CyclicBarrier`?**
- A) `Phaser` uses less memory.
- B) `Phaser` does not throw exceptions.
- C) `Phaser` allows threads to dynamically register and deregister themselves at runtime, changing the number of parties required to advance to the next phase.
- D) `Phaser` can be used across multiple JVMs.
*Answer: C*