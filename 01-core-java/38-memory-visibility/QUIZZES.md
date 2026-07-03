# Quizzes: Memory Visibility & Ordering

Test your knowledge of the Java Memory Model, Happens-Before rules, and the `volatile` keyword.

## Quiz 1: The Memory Model

**Q1: Why might Thread B not immediately see a variable change made by Thread A?**
- A) Because Thread B is sleeping.
- B) Because modern CPUs write changes to their local L1/L2 caches first. Unless a memory barrier forces a flush to main memory, Thread B (running on a different core) will continue to read the stale value from its own local cache.
- C) Because variables are immutable by default in Java.
- D) Because the JVM garbage collector paused Thread B.
*Answer: B*

**Q2: What does the compiler/CPU "Instruction Reordering" optimization entail?**
- A) The CPU executes instructions in reverse order.
- B) The compiler translates Java into C++.
- C) The compiler or CPU rearranges the execution order of instructions to maximize performance, provided the reordering does not change the outcome of a *single-threaded* execution.
- D) The JVM reorders threads in the thread pool.
*Answer: C*

## Quiz 2: Happens-Before and Volatile

**Q1: According to the JMM "Happens-Before" rules, what is the relationship between a `synchronized` unlock and a subsequent lock?**
- A) There is no relationship.
- B) An unlock on a monitor happens-before every subsequent lock on that *same* monitor, guaranteeing that the thread acquiring the lock sees all memory changes made by the thread that released the lock.
- C) The thread acquiring the lock must wait 10 milliseconds.
- D) The lock is automatically upgraded to a write lock.
*Answer: B*

**Q2: Which of the following statements about the `volatile` keyword is TRUE?**
- A) It guarantees that compound operations (like `count++`) are atomic.
- B) It prevents a variable from being garbage collected.
- C) It guarantees memory visibility (forces reads/writes to main memory) and prevents instruction reordering around the variable.
- D) It acquires a lock on the object.
*Answer: C*

## Quiz 3: Edge Cases

**Q1: In the Double-Checked Locking (DCL) Singleton pattern, why must the `instance` variable be declared as `volatile`?**
- A) To make the object creation faster.
- B) To prevent instruction reordering. Without `volatile`, the CPU could assign the memory reference to the `instance` variable *before* the constructor finishes executing. Another thread could read the non-null reference and try to use a partially constructed object, causing a crash.
- C) To prevent the Singleton from being cloned.
- D) To allow multiple instances to be created temporarily.
*Answer: B*