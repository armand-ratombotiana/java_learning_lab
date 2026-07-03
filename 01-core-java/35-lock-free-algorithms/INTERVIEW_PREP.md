# Interview Preparation: Lock-Free Algorithms

This document covers advanced questions related to lock-free guarantees, CAS spin-loops, the ABA problem, and memory management in lock-free structures.

## Q1: What is the difference between a "Lock-Free" algorithm and a "Wait-Free" algorithm?
**Answer:**
*   **Lock-Free**: Guarantees system-wide progress. At least one thread will always make progress in a finite number of steps. However, individual threads might be starved if they continually lose the CAS race to other threads (livelock).
*   **Wait-Free**: A much stronger guarantee. It guarantees that *every single thread* will complete its operation in a bounded, finite number of steps, regardless of what other threads are doing. Wait-free algorithms are incredibly complex and generally only used in hard real-time systems where bounded latency is strictly required.

## Q2: Why is `ConcurrentLinkedQueue.size()` an $O(N)$ operation? Why not just use an `AtomicInteger`?
**Answer:**
In a lock-free structure, operations must be isolated to a single CAS.
If you push a node to a queue, you CAS the `tail` pointer. If you also want to update a `size` variable, you need a second CAS.
Because you cannot perform two CAS operations simultaneously, there is a window of time where the `tail` is updated but the `size` is not. Another thread could read the `size` during this window and get an incorrect, inconsistent value. To maintain strict lock-free purity and avoid massive contention on a single `size` variable, the designers of `ConcurrentLinkedQueue` decided not to cache the size. Instead, calling `size()` physically traverses the linked list from head to tail to count the nodes.

## Q3: Explain the ABA problem in the context of a lock-free Stack (Treiber Stack).
**Answer:**
1.  Thread 1 wants to pop. It reads `head` (Node A) and `next` (Node B). It prepares to CAS the head from A to B.
2.  Thread 1 is suspended by the OS.
3.  Thread 2 pops Node A.
4.  Thread 2 pops Node B.
5.  Thread 2 pushes Node A back onto the stack. The stack is now just `[Node A]`.
6.  Thread 1 wakes up and executes: `compareAndSet(expected: Node A, new: Node B)`.
Because the head is indeed Node A, the CAS succeeds! However, Node B is no longer in the stack. Thread 1 just set the head to a deleted node, corrupting the entire stack.
*(Note: In Java, because of the Garbage Collector, Node A wouldn't be reused at the exact same memory address unless the developer implemented manual object pooling, making ABA rare in Java node-based structures compared to C++).*

## Q4: How does `AtomicStampedReference` solve the ABA problem?
**Answer:**
It pairs the object reference with an integer "stamp" (or version number).
Every time the reference is updated, the stamp must be incremented.
In the ABA scenario, when Thread 2 pushes Node A back onto the stack, the state changes from `(Node A, Stamp 1)` to `(Node A, Stamp 3)`.
When Thread 1 wakes up, its CAS expects `(Node A, Stamp 1)`. The CAS will fail because the stamp does not match, successfully preventing the corruption.

## Q5: Why is writing lock-free algorithms significantly easier in Java than in C or C++?
**Answer:**
Because of the **Garbage Collector**.
In C++, if you pop a node from a lock-free stack, you must free its memory (`delete`). However, you don't know if another thread is currently suspended while holding a pointer to that node, getting ready to perform a CAS. If you delete it, the other thread will cause a Segmentation Fault when it wakes up. This requires complex manual memory reclamation schemes (like Hazard Pointers).
In Java, the GC handles this. Even if a node is popped, the GC will not reclaim its memory as long as another suspended thread holds a reference to it on its stack.