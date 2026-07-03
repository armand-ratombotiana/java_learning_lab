# Edge Cases & Pitfalls: Lock-Free Algorithms

Writing lock-free data structures is notoriously difficult. Small mistakes in the ordering of reads and writes can lead to corrupted data structures and impossible-to-reproduce bugs.

## 1. The ABA Problem (The Silent Corruptor)
*   **The Scenario**: You implement a lock-free stack.
    1. Thread 1 wants to pop. It reads `head` (Node A) and `head.next` (Node B).
    2. Before Thread 1 can execute its CAS, it is paused by the OS.
    3. Thread 2 pops Node A.
    4. Thread 2 pops Node B.
    5. Thread 2 pushes Node A back onto the stack. The stack is now just `[Node A]`.
    6. Thread 1 wakes up. It executes its CAS: `compareAndSet(expected: Node A, new: Node B)`.
*   **The Pitfall**: Thread 1's CAS succeeds because the head is indeed Node A again! However, Node B is no longer in the stack; it was popped by Thread 2. Thread 1 just set the head of the stack to a deleted node, corrupting the entire data structure.
*   **Mitigation**: In Java, use `AtomicStampedReference`. It pairs the object reference with an integer stamp. Every time the reference is modified, the stamp increments. Thread 1's CAS would fail because it expects (Node A, Stamp 1), but the current state is (Node A, Stamp 3). *Note: In garbage-collected languages like Java, the ABA problem is less common in node-based structures because Node A wouldn't be reused at the exact same memory address like it would in C++, but it can still occur if object pooling is used.*

## 2. Memory Reclamation (The GC Crutch)
*   **The Scenario**: You write a lock-free algorithm in C++. You pop a node from a queue. When is it safe to `delete` that node from memory?
*   **The Pitfall**: You don't know if another thread is currently suspended while holding a pointer to that node, getting ready to perform a CAS. If you delete it, the other thread will cause a Segmentation Fault when it wakes up. This requires extremely complex memory reclamation schemes (like Hazard Pointers or Epoch-Based Reclamation).
*   **Mitigation (Java)**: Java developers are spoiled. The Garbage Collector handles this perfectly. Even if a node is popped, the GC will not delete it from RAM as long as another suspended thread holds a reference to it on its stack. This makes writing lock-free algorithms in Java significantly easier than in C/C++.

## 3. The "Lost Update" in Compound Operations
*   **The Scenario**: You have a lock-free structure and you want to implement a `size()` method. You add an `AtomicInteger size` and increment it inside your `push()` CAS loop.
    ```java
    do { ... } while (!head.compareAndSet(old, newHead));
    size.incrementAndGet(); // BUG!
    ```
*   **The Pitfall**: The `size` is no longer strictly synchronized with the `head`. Thread 1 could successfully push a node, but get paused *before* incrementing `size`. Thread 2 could read the `size`, getting an incorrect, outdated value.
*   **Mitigation**: In true lock-free structures, maintaining an exact, atomic `size` counter is incredibly difficult without causing massive contention. This is why `ConcurrentLinkedQueue.size()` is an $O(N)$ operation—it literally traverses the entire queue to count the nodes.

## 4. Livelock
*   **The Scenario**: Two threads are constantly trying to update the same lock-free variable.
*   **The Pitfall**: Thread A reads the state. Thread B updates the state. Thread A's CAS fails. Thread A loops and reads the new state. Thread B updates the state again. Thread A's CAS fails again.
While a lock-free system guarantees *overall* progress (Thread B is succeeding), a specific thread (Thread A) might be starved indefinitely if it constantly loses the CAS race. This is called a Livelock.
*   **Mitigation**: In extreme cases, introduce exponential backoff (e.g., `Thread.yield()` or a short `LockSupport.parkNanos()`) inside the spin-loop to give other threads a chance to finish their work and reduce contention.

## 5. False Sharing
*   **The Pitfall**: As discussed in the Atomic Variables module, if multiple threads are updating different `AtomicReference`s that happen to reside on the same CPU cache line, the hardware will constantly invalidate the cache, destroying the performance benefits of the lock-free algorithm.
*   **Mitigation**: Use `@Contended` to pad the heavily mutated atomic references (like the `head` and `tail` pointers of a queue) so they sit on different cache lines.