package com.dsacademy.lab18.circularbuffer;

public class CircularDeque<T> {
    private final Object[] buffer;
    private int head, tail, size;
    private final int capacity;

    public CircularDeque(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public void addFirst(T value) {
        if (isFull()) throw new IllegalStateException("Deque is full");
        head = (head - 1 + capacity) % capacity;
        buffer[head] = value;
        size++;
    }

    public void addLast(T value) {
        if (isFull()) throw new IllegalStateException("Deque is full");
        buffer[tail] = value;
        tail = (tail + 1) % capacity;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) return null;
        T value = (T) buffer[head];
        head = (head + 1) % capacity;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) return null;
        tail = (tail - 1 + capacity) % capacity;
        T value = (T) buffer[tail];
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peekFirst() { return isEmpty() ? null : (T) buffer[head]; }

    @SuppressWarnings("unchecked")
    public T peekLast() { return isEmpty() ? null : (T) buffer[(tail - 1 + capacity) % capacity]; }

    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
    public int size() { return size; }
    public int capacity() { return capacity; }
}
