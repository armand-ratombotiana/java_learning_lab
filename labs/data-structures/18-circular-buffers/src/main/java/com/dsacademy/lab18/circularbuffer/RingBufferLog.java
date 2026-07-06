package com.dsacademy.lab18.circularbuffer;

public class RingBufferLog {
    private final String[] buffer;
    private int head, tail, size;
    private final int capacity;

    public RingBufferLog(int capacity) {
        this.capacity = capacity;
        this.buffer = new String[capacity];
    }

    public void append(String entry) {
        buffer[tail] = entry;
        tail = (tail + 1) % capacity;
        if (size < capacity) size++;
        else head = (head + 1) % capacity;
    }

    public String[] getRecent(int n) {
        int count = Math.min(n, size);
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = buffer[(head + i) % capacity];
        }
        return result;
    }

    public String[] getAll() { return getRecent(size); }
    public int size() { return size; }
    public int capacity() { return capacity; }
}
