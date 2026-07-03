# Quizzes: Atomic Variables & Hardware Concurrency

Test your knowledge of CAS, memory visibility, and lock-free programming.

## Quiz 1: CAS and Atomic Classes

**Q1: What does the CPU instruction "Compare-And-Swap" (CAS) actually do?**
- A) It compares two variables and swaps their values if they are equal.
- B) It atomicaly updates a memory location to a new value, *only if* the current value in memory matches the expected value provided by the thread.
- C) It swaps a thread out of the CPU to let another thread run.
- D) It compares a lock's state and swaps it to "locked".
*Answer: B*

**Q2: Why is `AtomicInteger.incrementAndGet()` considered "Optimistic Concurrency"?**
- A) Because it assumes the operation will never fail.
- B) Because it does not acquire a lock; it calculates the new value and attempts a CAS. If the CAS fails (because another thread interfered), it optimistically spins (loops) and tries again.
- C) Because it always returns a positive number.
- D) Because it uses the `synchronized` keyword internally.
*Answer: B*

## Quiz 2: Advanced Mechanics

**Q1: What is the primary advantage of using `AtomicIntegerFieldUpdater` over `AtomicInteger`?**
- A) The Updater is faster.
- B) The Updater prevents the ABA problem.
- C) Memory savings. If you have 1,000,000 objects, using `AtomicInteger` creates 1,000,000 extra objects (with header overhead). The Updater allows atomic operations directly on a primitive `volatile int` field, saving massive amounts of RAM.
- D) The Updater is thread-safe, while `AtomicInteger` is not.
*Answer: C*

**Q2: Under extremely high contention (many threads updating the same counter simultaneously), which class provides the highest throughput?**
- A) `AtomicInteger`
- B) `AtomicLong`
- C) `LongAdder`
- D) `volatile long`
*Answer: C (LongAdder distributes the updates across an array of variables to prevent CAS spin-loop contention).*

## Quiz 3: Edge Cases

**Q1: What is the "ABA Problem" in lock-free programming?**
- A) When a thread gets stuck in an infinite A-B-A loop.
- B) When a thread reads value A, another thread changes it to B and back to A, and the first thread's CAS succeeds because it still sees A, even though the system state was altered in the interim.
- C) A syntax error when using VarHandles.
- D) When two threads try to write A and B simultaneously.
*Answer: B*

**Q2: Which statement about the `volatile` keyword is TRUE?**
- A) `volatile int x; x++;` is completely thread-safe.
- B) `volatile` guarantees that operations on the variable are atomic.
- C) `volatile` guarantees memory visibility; changes made by one thread are immediately visible to other threads, bypassing CPU caches.
- D) `volatile` prevents deadlocks.
*Answer: C*