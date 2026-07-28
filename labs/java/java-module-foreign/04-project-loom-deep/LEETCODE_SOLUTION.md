# LeetCode Solution: Bounded Blocking Queue (Virtual Threads)

**Problem:** [1188. Design Bounded Blocking Queue](https://leetcode.com/problems/design-bounded-blocking-queue/)

Demonstrates a bounded queue using `ReentrantLock` (to avoid pinning) with virtual thread-friendly condition variables.

## Approach

Use `ReentrantLock` + `Condition` instead of `synchronized` + `wait()/notify()` to avoid pinning virtual threads.

## Java 21 Solution

```java
import java.util.*;
import java.util.concurrent.locks.*;

class BoundedBlockingQueue {
    private final Deque<Integer> queue = new ArrayDeque<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();   // virtual-thread-friendly
            }
            queue.addLast(element);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();  // virtual-thread-friendly
            }
            int val = queue.removeFirst();
            notFull.signal();
            return val;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
```

## Key Takeaway

Using `ReentrantLock` + `Condition` instead of `synchronized` prevents pinning — virtual threads can yield while waiting, so a **single carrier thread can manage thousands** of blocked producers/consumers.
