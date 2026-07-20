package com.javalab.02;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentHashMapSimulator<K, V> {

    private static final int DEFAULT_SEGMENTS = 16;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private final Segment<K, V>[] segments;
    private final Lock[] locks;
    private final int segmentMask;

    static class Segment<K, V> {
        private Node<K, V>[] table;
        private int size;

        @SuppressWarnings("unchecked")
        Segment(int capacity) {
            table = new Node[capacity];
            size = 0;
        }

        int index(K key, int mask) {
            if (key == null) return 0;
            int h = key.hashCode();
            h ^= (h >>> 16);
            return h & mask;
        }

        V put(K key, V value) {
            int mask = table.length - 1;
            int idx = index(key, mask);
            Node<K, V> n = table[idx];
            while (n != null) {
                if (Objects.equals(n.key, key)) {
                    V old = n.value;
                    n.value = value;
                    return old;
                }
                n = n.next;
            }
            Node<K, V> head = table[idx];
            table[idx] = new Node<>(key, value, head);
            size++;

            if ((double) size / table.length > LOAD_FACTOR) {
                resize();
            }
            return null;
        }

        V get(K key) {
            int mask = table.length - 1;
            int idx = index(key, mask);
            Node<K, V> n = table[idx];
            while (n != null) {
                if (Objects.equals(n.key, key)) {
                    return n.value;
                }
                n = n.next;
            }
            return null;
        }

        V remove(K key) {
            int mask = table.length - 1;
            int idx = index(key, mask);
            Node<K, V> n = table[idx];
            Node<K, V> prev = null;
            while (n != null) {
                if (Objects.equals(n.key, key)) {
                    if (prev == null) {
                        table[idx] = n.next;
                    } else {
                        prev.next = n.next;
                    }
                    size--;
                    return n.value;
                }
                prev = n;
                n = n.next;
            }
            return null;
        }

        boolean containsKey(K key) {
            return get(key) != null;
        }

        @SuppressWarnings("unchecked")
        void resize() {
            Node<K, V>[] oldTable = table;
            int newCapacity = oldTable.length * 2;
            table = new Node[newCapacity];
            int newMask = newCapacity - 1;
            for (Node<K, V> head : oldTable) {
                Node<K, V> cur = head;
                while (cur != null) {
                    int idx = index(cur.key, newMask);
                    table[idx] = new Node<>(cur.key, cur.value, table[idx]);
                    cur = cur.next;
                }
            }
        }

        int size() {
            return size;
        }
    }

    static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public ConcurrentHashMapSimulator() {
        this(DEFAULT_SEGMENTS, DEFAULT_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentHashMapSimulator(int segmentCount, int initialCapacity) {
        if ((segmentCount & (segmentCount - 1)) != 0) {
            throw new IllegalArgumentException("Segment count must be a power of two");
        }
        this.segmentMask = segmentCount - 1;
        this.segments = new Segment[segmentCount];
        this.locks = new Lock[segmentCount];
        for (int i = 0; i < segmentCount; i++) {
            locks[i] = new ReentrantLock();
            segments[i] = new Segment<>(initialCapacity);
        }
    }

    private int segmentIndex(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return h & segmentMask;
    }

    private void lock(int idx) {
        locks[idx].lock();
    }

    private void unlock(int idx) {
        locks[idx].unlock();
    }

    public V put(K key, V value) {
        int segIdx = segmentIndex(key);
        lock(segIdx);
        try {
            return segments[segIdx].put(key, value);
        } finally {
            unlock(segIdx);
        }
    }

    public V get(K key) {
        int segIdx = segmentIndex(key);
        lock(segIdx);
        try {
            return segments[segIdx].get(key);
        } finally {
            unlock(segIdx);
        }
    }

    public V remove(K key) {
        int segIdx = segmentIndex(key);
        lock(segIdx);
        try {
            return segments[segIdx].remove(key);
        } finally {
            unlock(segIdx);
        }
    }

    public boolean containsKey(K key) {
        int segIdx = segmentIndex(key);
        lock(segIdx);
        try {
            return segments[segIdx].containsKey(key);
        } finally {
            unlock(segIdx);
        }
    }

    public int size() {
        int total = 0;
        for (int i = 0; i < locks.length; i++) {
            lock(i);
        }
        try {
            for (Segment<K, V> seg : segments) {
                total += seg.size();
            }
        } finally {
            for (int i = locks.length - 1; i >= 0; i--) {
                unlock(i);
            }
        }
        return total;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        for (int i = 0; i < locks.length; i++) {
            lock(i);
        }
        try {
            for (Segment<K, V> seg : segments) {
                seg.size = 0;
                @SuppressWarnings("unchecked")
                Node<K, V>[] fresh = new Node[seg.table.length];
                seg.table = fresh;
            }
        } finally {
            for (int i = locks.length - 1; i >= 0; i--) {
                unlock(i);
            }
        }
    }
}
