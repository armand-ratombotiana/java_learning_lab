# Quizzes: Lock-Free Algorithms

Test your knowledge of CAS loops, lock-free data structures, and the ABA problem.

## Quiz 1: Philosophy and Mechanics

**Q1: What is the fundamental guarantee of a "Lock-Free" algorithm?**
- A) It guarantees that every thread will finish its task in a specific amount of time.
- B) It guarantees that the system as a whole will make progress in a finite number of steps, even if individual threads are suspended or delayed.
- C) It guarantees that no memory allocation is required.
- D) It guarantees that it is always faster than using a `ReentrantLock`.
*Answer: B*

**Q2: What happens in a typical lock-free spin loop if the `compareAndSet` (CAS) operation returns `false`?**
- A) The thread throws a `ConcurrentModificationException`.
- B) The thread goes to sleep (blocks) until the OS wakes it up.
- C) The loop restarts: the thread reads the *new* state of the variable, recalculates the desired outcome, and attempts the CAS again.
- D) The thread gives up and returns `null`.
*Answer: C*

## Quiz 2: Data Structures

**Q1: Why is implementing a lock-free Queue (FIFO) significantly harder than implementing a lock-free Stack (LIFO)?**
- A) A Stack only requires maintaining one pointer (the `head`), which can be updated with a single CAS. A Queue requires maintaining two pointers (`head` and `tail`), and it is impossible to update two separate memory locations simultaneously with a single CAS instruction.
- B) Queues require more memory.
- C) Stacks do not suffer from the ABA problem.
- D) Java's `AtomicReference` only works on Stacks.
*Answer: A (This is why lock-free queues require complex algorithms where threads "help" each other update the tail pointer).*

**Q2: In `ConcurrentLinkedQueue`, what is the time complexity of calling `.size()`?**
- A) $O(1)$
- B) $O(\log N)$
- C) $O(N)$
- D) $O(N^2)$
*Answer: C (Maintaining an exact, atomic size counter alongside the head/tail pointers is too complex/contentious, so `size()` actually traverses the entire linked list to count the nodes).*

## Quiz 3: Edge Cases

**Q1: How does Java's Garbage Collector make writing lock-free algorithms easier compared to C++?**
- A) The GC automatically writes the CAS loops.
- B) The GC prevents the ABA problem.
- C) In C++, if Thread A pops a node and deletes it from memory while Thread B is suspended holding a pointer to it, Thread B will crash upon waking. Java's GC will not delete the node as long as Thread B holds a reference, eliminating complex memory reclamation schemes like Hazard Pointers.
- D) The GC makes the CPU cache faster.
*Answer: C*