# Quizzes: Deadlock & Livelock

Test your knowledge of the Coffman conditions, Livelock, and Starvation.

## Quiz 1: The Coffman Conditions

**Q1: Which of the following is NOT one of the four Coffman conditions required for a deadlock to occur?**
- A) Mutual Exclusion (Resources cannot be shared).
- B) Hold and Wait (A thread holds a resource while waiting for another).
- C) High Contention (Many threads competing for the same resource).
- D) Circular Wait (Thread A waits for B, B waits for C, C waits for A).
*Answer: C (High contention causes performance degradation, but is not a logical requirement for deadlock).*

**Q2: How does the "Lock Ordering" strategy prevent deadlocks?**
- A) It breaks the "No Preemption" condition by forcing threads to release their locks.
- B) It breaks the "Circular Wait" condition by ensuring that all threads acquire locks in the exact same global order, making a cycle mathematically impossible.
- C) It breaks the "Mutual Exclusion" condition by using ReadWrite locks.
- D) It breaks the "Hold and Wait" condition by making threads acquire all locks simultaneously.
*Answer: B*

## Quiz 2: Livelock and Starvation

**Q1: What is the primary difference between Deadlock and Livelock?**
- A) In a deadlock, threads are suspended (asleep). In a livelock, threads are actively executing (awake) and changing state, but they are stuck in a loop of reacting to each other and making no actual progress.
- B) Livelock only happens with `synchronized` blocks.
- C) Deadlock consumes 100% of the CPU, while Livelock consumes 0%.
- D) There is no difference; they are synonyms.
*Answer: A*

**Q2: You have a low-priority thread that occasionally needs to acquire a `synchronized` lock. However, there are 10 high-priority threads constantly acquiring and releasing the same lock. The low-priority thread never gets a turn. What is this called?**
- A) Deadlock
- B) The ABA Problem
- C) Thread Starvation
- D) Priority Inversion
*Answer: C (The thread is not deadlocked, it is just continually losing the race to acquire the lock due to the unfair nature of the `synchronized` keyword).*

## Quiz 3: Mitigation Strategies

**Q1: When attempting to prevent deadlocks using `tryLock()`, what is the recommended way to handle a failed lock acquisition to prevent Livelock?**
- A) Immediately call `tryLock()` again in a tight `while(true)` loop.
- B) Throw a `RuntimeException`.
- C) Release any currently held locks, sleep for a *random* amount of time, and then restart the entire acquisition process.
- D) Call `System.gc()`.
*Answer: C (The random backoff desynchronizes the clashing threads).*