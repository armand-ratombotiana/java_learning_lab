package com.dsacademy.lab18.circularbuffer;

import java.util.Arrays;

public class CircularBuffer<T> {
    private final Object[] buffer;
    private int head;
    private int tail;
    private int size;
    private final int capacity;

    public CircularBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.buffer = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public boolean offer(T value) {
        if (isFull()) return false;
        buffer[tail] = value;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    public void add(T value) {
        if (isFull()) {
            head = (head + 1) % capacity;
            size--;
        }
        buffer[tail] = value;
        tail = (tail + 1) % capacity;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) return null;
        T value = (T) buffer[head];
        head = (head + 1) % capacity;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return isEmpty() ? null : (T) buffer[head];
    }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
    public int size() { return size; }
    public int capacity() { return capacity; }

    public void clear() { head = 0; tail = 0; size = 0; Arrays.fill(buffer, null); }
}
