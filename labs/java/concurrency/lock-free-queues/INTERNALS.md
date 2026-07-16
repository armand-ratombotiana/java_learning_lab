# Lock-Free Queue Internals

## 🔬 The Michael-Scott Algorithm
The standard algorithm for a lock-free concurrent queue (used by Java's `ConcurrentLinkedQueue`) is the **Michael-Scott Algorithm**.

It uses a linked list with a dummy head node. The head and tail are `AtomicReference` objects.

### 1. The Enqueue Operation (CAS Loop)
To add a new node:
1. Read the current `tail`.
2. Read the `tail.next` pointer.
3. If `tail.next` is not null, it means another thread is in the middle of an enqueue. We help them by moving the `tail` forward.
4. If `tail.next` is null, we try to set it to our new node using **CAS**.
5. If CAS succeeds, we try to move the `tail` pointer forward to our new node.

### 2. The ABA Problem
A common bug in lock-free programming.
1. Thread 1 reads value A from a pointer.
2. Thread 2 changes the pointer to B, then back to A.
3. Thread 1 performs CAS(A, new). It succeeds because the value is A, but it doesn't realize the state changed and came back.
- **The Fix**: Use **AtomicStampedReference**. It attaches a "version number" (stamp) to the pointer. Even if the value is A, if the stamp changed, the CAS fails.