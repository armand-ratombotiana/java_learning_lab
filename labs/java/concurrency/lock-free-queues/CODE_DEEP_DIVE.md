# Lock-Free Queue Code Deep Dive

This lab provides a pure Java implementation of a simplified Michael-Scott Lock-Free Queue using `AtomicReference`.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/lock-free-queues/SOLUTION/LockFreeQueue.java"
package java.concurrency.lockfree;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A simplified Michael-Scott Lock-Free Queue.
 */
public class LockFreeQueue<T> {

    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next;

        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> dummy = new Node<>(null);
        head = new AtomicReference<>(dummy);
        tail = new AtomicReference<>(dummy);
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> curTail = tail.get();
            Node<T> tailNext = curTail.next.get();

            if (curTail == tail.get()) { // Consistent read
                if (tailNext != null) {
                    // Another thread is enqueuing. Help them move tail.
                    tail.compareAndSet(curTail, tailNext);
                } else {
                    // Try to link the new node
                    if (curTail.next.compareAndSet(null, newNode)) {
                        // Link succeeded. Try to move tail to our new node.
                        tail.compareAndSet(curTail, newNode);
                        return;
                    }
                }
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> curHead = head.get();
            Node<T> curTail = tail.get();
            Node<T> headNext = curHead.next.get();

            if (curHead == head.get()) {
                if (curHead == curTail) {
                    if (headNext == null) return null; // Queue empty
                    // Tail is falling behind. Help move it.
                    tail.compareAndSet(curTail, headNext);
                } else {
                    T value = headNext.value;
                    if (head.compareAndSet(curHead, headNext)) {
                        return value;
                    }
                }
            }
        }
    }
}
```

## 🔍 Key Takeaways
1. **The "Help" Pattern**: Notice that both `enqueue` and `dequeue` check if the `tail` is falling behind and try to fix it (`tail.compareAndSet`). This makes the algorithm **Wait-Free** for some operations; one slow thread cannot block others.
2. **Infinite Loop**: The `while(true)` loops are standard in CAS algorithms. If contention is high, the CAS will fail, and the thread will immediately retry. This is much faster than context-switching to a blocked state if the "lock" is only held for a few nanoseconds.