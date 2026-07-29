# LeetCode 1188: Design Bounded Blocking Queue

> **Difficulty**: Medium | **Company**: Amazon, Google, Microsoft | **Category**: Reactive Deep (Backpressure / Reactive Streams)

## Problem

Implement a thread-safe bounded blocking queue with:

- `enqueue(int element)`: Add an element. Wait if the queue is full.
- `dequeue()`: Remove and return an element. Wait if the queue is empty.
- `size()`: Return the current number of elements.

The queue should support multiple producer and consumer threads.

## Solution

Uses `ReentrantLock` with two `Condition` variables (notFull / notEmpty) — the same mechanism underlying `ArrayBlockingQueue`. This demonstrates reactive-style backpressure: producers are blocked when full, consumers when empty.

```java
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * LeetCode 1188: Design Bounded Blocking Queue
 *
 * Reactive-style backpressure: producers block when full, consumers block when empty.
 *
 * Time: O(1) per operation
 * Space: O(capacity)
 */
public class BoundedBlockingQueue {

    private final int[] queue;
    private int head, tail, count;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        queue = new int[capacity];
    }

    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            while (count == queue.length) {
                notFull.await();  // backpressure: producer waits
            }
            queue[tail] = element;
            tail = (tail + 1) % queue.length;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();  // consumer waits
            }
            int val = queue[head];
            head = (head + 1) % queue.length;
            count--;
            notFull.signal();
            return val;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws Exception {
        BoundedBlockingQueue q = new BoundedBlockingQueue(3);

        // Single-threaded test
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        assert q.size() == 3;
        assert q.dequeue() == 1;
        assert q.dequeue() == 2;
        q.enqueue(4);
        assert q.dequeue() == 3;
        assert q.dequeue() == 4;
        assert q.size() == 0;

        // Multi-threaded producer/consumer test
        q = new BoundedBlockingQueue(5);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try { q.enqueue(i); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });
        java.util.concurrent.atomic.AtomicInteger sum = new java.util.concurrent.atomic.AtomicInteger(0);
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try { sum.addAndGet(q.dequeue()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        assert sum.get() == 4950 : "Expected sum 4950, got " + sum.get();  // sum 0..99 = 4950

        System.out.println("All tests passed.");
    }
}
```

## Complexity

| Operation | Time | Space |
|-----------|------|-------|
| enqueue   | O(1) | O(1)  |
| dequeue   | O(1) | O(1)  |
| size      | O(1) | O(1)  |
| **Total** |      | O(capacity) |

## Key Insights

1. **Condition variables**: `notFull` and `notEmpty` enable efficient waiting — threads sleep until the condition is met (no busy-waiting).
2. **Backpressure alignment**: This is exactly how Reactive Streams handles backpressure: the subscriber signals demand, and the producer blocks/pauses when the buffer is full.
3. **Circular buffer**: Using head/tail pointers modulo capacity avoids element shifting.
4. **Thread safety**: `ReentrantLock` with `Condition` provides the same semantics as `synchronized` + `wait/notify` but with more flexibility (e.g., multiple conditions, fairness).
