# Design Circular Queue (LeetCode 622)

**Problem:** Design an implementation of a circular queue (ring buffer). Support the following operations:

- `MyCircularQueue(int k)` — Initializes the queue with a maximum size of k.
- `boolean enQueue(int value)` — Inserts an element into the queue. Returns true on success.
- `boolean deQueue()` — Deletes an element from the queue. Returns true on success.
- `int Front()` — Returns the front element, or -1 if empty.
- `int Rear()` — Returns the last element, or -1 if empty.
- `boolean isEmpty()` — Returns whether the queue is empty.
- `boolean isFull()` — Returns whether the queue is full.

## Java Solution

```java
import java.util.NoSuchElementException;

/**
 * Array-based circular queue with a fixed capacity.
 *
 * <p>Uses a single integer {@code size} to distinguish between empty and full
 * states, avoiding the need to waste a slot. The head pointer points to the
 * front element, and the tail pointer points to the next insertion slot.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>enQueue(value)</b> — O(1)</li>
 *   <li><b>deQueue()</b> — O(1)</li>
 *   <li><b>Front()</b> — O(1)</li>
 *   <li><b>Rear()</b> — O(1)</li>
 *   <li><b>isEmpty()</b> — O(1)</li>
 *   <li><b>isFull()</b> — O(1)</li>
 * </ul>
 *
 * <b>Space:</b> O(k) where k is the capacity
 */
public class MyCircularQueue {

    private final int[] data;
    private final int capacity;
    private int head;
    private int tail;
    private int size;

    /**
     * Constructs a circular queue with the given capacity.
     *
     * @param k the maximum number of elements
     * @throws IllegalArgumentException if capacity is not positive
     */
    public MyCircularQueue(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = k;
        this.data = new int[k];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * Inserts an element into the queue. Returns true on success.
     *
     * @param value the value to insert
     * @return true if the element was added, false if the queue is full
     */
    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }
        data[tail] = value;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    /**
     * Removes the front element from the queue.
     *
     * @return true if the element was removed, false if the queue is empty
     */
    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }
        head = (head + 1) % capacity;
        size--;
        return true;
    }

    /**
     * Returns the front element without removing it.
     *
     * @return the front element, or -1 if the queue is empty
     */
    public int Front() {
        if (isEmpty()) {
            return -1;
        }
        return data[head];
    }

    /**
     * Returns the last element without removing it.
     *
     * @return the last element, or -1 if the queue is empty
     */
    public int Rear() {
        if (isEmpty()) {
            return -1;
        }
        int lastIndex = (tail - 1 + capacity) % capacity;
        return data[lastIndex];
    }

    /**
     * Returns true if the queue is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if the queue is full.
     *
     * @return true if full
     */
    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Returns the current number of elements in the queue.
     *
     * @return current size
     */
    public int size() {
        return size;
    }
}
```

## Test Cases

```java
/**
 * Unit tests for MyCircularQueue.
 */
public class MyCircularQueueTest {

    public static void main(String[] args) {
        // --- Test 1: Example from LeetCode ---
        MyCircularQueue q = new MyCircularQueue(3);
        assert q.enQueue(1) : "enQueue(1) should succeed";
        assert q.enQueue(2) : "enQueue(2) should succeed";
        assert q.enQueue(3) : "enQueue(3) should succeed";
        assert !q.enQueue(4) : "enQueue(4) should fail (full)";
        assert q.Rear() == 3 : "Rear should be 3";
        assert q.isFull() : "should be full";
        assert q.deQueue() : "deQueue should succeed";
        assert q.enQueue(4) : "enQueue(4) should succeed now";
        assert q.Rear() == 4 : "Rear should be 4";

        // --- Test 2: Empty queue ---
        MyCircularQueue q2 = new MyCircularQueue(3);
        assert q2.isEmpty() : "should be empty";
        assert q2.Front() == -1 : "Front should be -1 on empty";
        assert q2.Rear() == -1 : "Rear should be -1 on empty";
        assert !q2.deQueue() : "deQueue on empty should fail";

        // --- Test 3: Single element ---
        MyCircularQueue q3 = new MyCircularQueue(1);
        assert q3.isEmpty() : "should be empty";
        assert q3.enQueue(5) : "enQueue(5) should succeed";
        assert q3.isFull() : "should be full";
        assert q3.Front() == 5 : "Front should be 5";
        assert q3.Rear() == 5 : "Rear should be 5";
        assert q3.deQueue() : "deQueue should succeed";
        assert q3.isEmpty() : "should be empty again";

        // --- Test 4: Wrap-around behavior ---
        MyCircularQueue q4 = new MyCircularQueue(3);
        q4.enQueue(1); q4.enQueue(2); q4.enQueue(3);
        q4.deQueue(); // remove 1, head = 1
        q4.deQueue(); // remove 2, head = 2
        q4.enQueue(4); // tail wraps to 0
        q4.enQueue(5); // tail wraps to 1
        assert q4.Front() == 3 : "Front should be 3";
        assert q4.Rear() == 5 : "Rear should be 5";
        assert q4.isFull() : "should be full";

        // --- Test 5: Fill then empty then fill again ---
        MyCircularQueue q5 = new MyCircularQueue(2);
        q5.enQueue(10); q5.enQueue(20);
        q5.deQueue(); q5.deQueue();
        assert q5.isEmpty() : "should be empty";
        q5.enQueue(30);
        assert q5.Front() == 30 : "Front should be 30";
        assert q5.Rear() == 30 : "Rear should be 30";

        // --- Test 6: Capacity 5 with many operations ---
        MyCircularQueue q6 = new MyCircularQueue(5);
        for (int i = 1; i <= 5; i++) q6.enQueue(i * 10);
        assert q6.isFull() : "should be full";
        assert q6.Front() == 10 : "Front should be 10";
        assert q6.Rear() == 50 : "Rear should be 50";
        q6.deQueue(); q6.deQueue(); // remove 10, 20
        assert q6.Front() == 30 : "Front should be 30";
        q6.enQueue(60); q6.enQueue(70); // add after wrap
        assert q6.Rear() == 70 : "Rear should be 70";
        assert q6.isFull() : "should be full again";

        // --- Test 7: deQueue until empty ---
        MyCircularQueue q7 = new MyCircularQueue(3);
        q7.enQueue(1); q7.enQueue(2); q7.enQueue(3);
        q7.deQueue(); q7.deQueue(); q7.deQueue();
        assert q7.isEmpty() : "should be empty after 3 dequeues";

        System.out.println("All MyCircularQueue tests passed!");
    }
}
```
