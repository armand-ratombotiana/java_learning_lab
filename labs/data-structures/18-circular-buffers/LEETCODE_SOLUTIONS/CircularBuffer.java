package com.leetcode.circular;

/**
 * Custom: Circular Buffer (Ring Buffer)
 * A fixed-size buffer that overwrites oldest elements when full.
 *
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(capacity)
 */
public class CircularBuffer {

    private final int[] buffer;
    private int head;
    private int tail;
    private int size;
    private final int capacity;

    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public boolean enqueue(int val) {
        if (isFull()) return false;
        buffer[tail] = val;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Buffer is empty");
        int val = buffer[head];
        head = (head + 1) % capacity;
        size--;
        return val;
    }

    public int peek() {
        if (isEmpty()) throw new IllegalStateException("Buffer is empty");
        return buffer[head];
    }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
    public int size() { return size; }

    public static void main(String[] args) {
        CircularBuffer cb = new CircularBuffer(3);
        System.out.println("Enqueue 1: " + cb.enqueue(1));
        System.out.println("Enqueue 2: " + cb.enqueue(2));
        System.out.println("Enqueue 3: " + cb.enqueue(3));
        System.out.println("Enqueue 4 (full): " + cb.enqueue(4) + " (expected: false)");
        System.out.println("Dequeue: " + cb.dequeue() + " (expected: 1)");
        System.out.println("Enqueue 4: " + cb.enqueue(4) + " (expected: true)");
        System.out.println("Peek: " + cb.peek() + " (expected: 2)");
        System.out.println("Size: " + cb.size() + " (expected: 3)");
    }
}
