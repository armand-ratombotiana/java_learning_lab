package com.dsacademy.lab18.circularbuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingCircularBuffer<T> {
    private final Object[] buffer;
    private int head, tail, size;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BlockingCircularBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public void put(T value) throws InterruptedException {
        lock.lock();
        try {
            while (isFull()) notFull.await();
            buffer[tail] = value;
            tail = (tail + 1) % capacity;
            size++;
            notEmpty.signal();
        } finally { lock.unlock(); }
    }

    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (isEmpty()) notEmpty.await();
            T value = (T) buffer[head];
            head = (head + 1) % capacity;
            size--;
            notFull.signal();
            return value;
        } finally { lock.unlock(); }
    }

    public boolean isEmpty() { lock.lock(); try { return size == 0; } finally { lock.unlock(); } }
    public boolean isFull() { lock.lock(); try { return size == capacity; } finally { lock.unlock(); } }
    public int size() { lock.lock(); try { return size; } finally { lock.unlock(); } }
}
