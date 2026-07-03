# Deep Dive: Lock-Free Algorithms

## 1. The Philosophy of Lock-Free Programming
Traditional concurrency relies on **mutual exclusion** (locks). If Thread A holds a lock, Thread B must wait. If Thread A crashes or is paused by the OS while holding the lock, Thread B waits forever (deadlock/starvation).

**Lock-Free** programming guarantees that *at least one thread will make progress* in a finite number of steps, regardless of what other threads are doing. It relies on atomic hardware instructions, primarily **Compare-And-Swap (CAS)**.

### The CAS Loop
Every lock-free algorithm relies on a spin-loop:
1.  Read the current state.
2.  Calculate the new state based on the current state.
3.  Attempt to swap the old state with the new state atomically (CAS).
4.  If the CAS fails (another thread changed the state in the meantime), go back to step 1.

## 2. Lock-Free Stacks (Treiber Stack)
A stack (LIFO) is the simplest lock-free data structure because all operations happen at a single point: the `head`.

### The Algorithm:
*   **Push**: Create a new node. Set its `next` pointer to the current `head`. CAS the `head` from the old head to the new node.
*   **Pop**: Read the current `head`. Read `head.next`. CAS the `head` from the current head to `head.next`.

```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {
    private static class Node<T> {
        final T item;
        Node<T> next;
        Node(T item) { this.item = item; }
    }

    private final AtomicReference<Node<T>> head = new AtomicReference<>(null);

    public void push(T item) {
        Node<T> newHead = new Node<>(item);
        Node<T> oldHead;
        do {
            oldHead = head.get();
            newHead.next = oldHead;
        } while (!head.compareAndSet(oldHead, newHead));
    }

    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        do {
            oldHead = head.get();
            if (oldHead == null) return null; // Stack is empty
            newHead = oldHead.next;
        } while (!head.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }
}
```

## 3. Lock-Free Queues (Michael-Scott Queue)
A queue (FIFO) is significantly harder to make lock-free because operations happen at two different points: the `head` (dequeue) and the `tail` (enqueue). You must maintain the integrity of both pointers simultaneously.

### The Algorithm (Enqueue):
1.  Read the `tail` and `tail.next`.
2.  If `tail.next` is not null, the tail pointer has fallen behind. Another thread added a node but hasn't updated the tail pointer yet. Help the other thread by CASing the `tail` forward.
3.  If `tail.next` is null, attempt to CAS `tail.next` to the new node.
4.  If successful, attempt to CAS the `tail` pointer to the new node.

This algorithm (implemented in `ConcurrentLinkedQueue`) requires a dummy node at the head to handle edge cases gracefully.

## 4. Performance Analysis: When to use Lock-Free?
Lock-free does **not** always mean faster.
*   **Low to Moderate Contention**: Lock-free algorithms shine. They avoid the massive overhead of OS-level thread suspension and context switching required by locks.
*   **High Contention**: Lock-free algorithms can perform *worse* than locks. If 100 threads are trying to push to a lock-free stack, 1 succeeds and 99 fail the CAS. Those 99 threads immediately loop and try again, burning 100% of the CPU in a useless spin-loop (contention). A traditional lock would put 99 threads to sleep, freeing the CPU to do other work.

## 5. Wait-Free vs. Lock-Free
*   **Lock-Free**: Guarantees system-wide progress. (Some threads might starve if they constantly lose the CAS race, but the system as a whole moves forward).
*   **Wait-Free**: A stronger guarantee. Guarantees that *every* thread will make progress in a finite number of steps, completely eliminating starvation. Wait-free algorithms are incredibly complex to write and are usually reserved for highly specialized, mission-critical real-time systems.