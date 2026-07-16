# AQS Internals: The CLH Lock Queue

## ⚙️ The Wait Queue (CLH)
AQS manages waiting threads using a variant of a **CLH (Craig, Landin, and Hagersten) lock queue**. 
It is a FIFO doubly-linked list of `Node` objects.

### 1. Node Structure
Each `Node` in the queue represents a thread and its status:
- `waitStatus`: Signal, Cancelled, Condition, or Propagate.
- `prev` / `next`: Links to other nodes in the queue.
- `thread`: The actual thread object being parked.

### 2. The Acquisition Flow (Exclusive Mode)
When a thread calls `acquire(int)`:
1. `tryAcquire(arg)` is called (your overridden method).
2. If it returns `false`, AQS creates a new `Node` for the current thread.
3. `addWaiter(Node.EXCLUSIVE)`: The node is appended to the tail of the queue using a CAS operation to ensure thread safety without locks.
4. `acquireQueued(node, arg)`: The thread enters a loop. It checks if its predecessor is the `head`. If so, it tries `tryAcquire` again. If it fails or its predecessor isn't the head, it calls `LockSupport.park()` to sleep.

### 3. The Release Flow
When a thread calls `release(int)`:
1. `tryRelease(arg)` is called.
2. If it returns `true`, AQS looks at the `head` of the queue.
3. It finds the next successor node and calls `LockSupport.unpark(successor.thread)`.
4. The unparked thread wakes up in its `acquireQueued` loop, tries `tryAcquire` again, succeeds, and becomes the new `head`.

## 🔬 Shared Mode (Propagating Success)
In Shared mode (e.g., `CountDownLatch` or `ReadLock`), when one thread is unparked, it might need to wake up *other* waiting threads as well.
- When `tryAcquireShared` returns a value $\ge 0$, the thread becomes the head and immediately calls `setHeadAndPropagate`.
- This ensures that if multiple threads can now acquire the shared resource, they are all woken up in a cascading "propagate" signal.