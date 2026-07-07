package com.dsacademy.lab21.hashing;

import java.util.Arrays;

public class RobinHoodHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private static final int MAX_DISPLACEMENT = 32;

    private K[] keys;
    private V[] values;
    private int[] displacements;
    private int size;
    private int capacity;
    private int mask;

    @SuppressWarnings("unchecked")
    public RobinHoodHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.mask = capacity - 1;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.displacements = new int[capacity];
        this.size = 0;
    }

    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) & mask;
    }

    public void put(K key, V value) {
        if (size >= capacity * LOAD_FACTOR) {
            rehash();
        }
        int idx = hash(key);
        int dist = 0;
        while (true) {
            if (keys[idx] == null) {
                keys[idx] = key;
                values[idx] = value;
                displacements[idx] = dist;
                size++;
                return;
            }
            if (dist > displacements[idx]) {
                K tmpK = keys[idx];
                V tmpV = values[idx];
                int tmpD = displacements[idx];
                keys[idx] = key;
                values[idx] = value;
                displacements[idx] = dist;
                key = tmpK;
                value = tmpV;
                dist = tmpD;
            }
            idx = (idx + 1) & mask;
            dist++;
            if (dist > MAX_DISPLACEMENT) {
                rehash();
                put(key, value);
                return;
            }
        }
    }

    public V get(K key) {
        int idx = hash(key);
        int dist = 0;
        while (keys[idx] != null && dist <= displacements[idx]) {
            if (key.equals(keys[idx])) {
                return values[idx];
            }
            idx = (idx + 1) & mask;
            dist++;
        }
        return null;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int idx = hash(key);
        int dist = 0;
        while (keys[idx] != null && dist <= displacements[idx]) {
            if (key.equals(keys[idx])) {
                V val = values[idx];
                keys[idx] = null;
                values[idx] = null;
                displacements[idx] = 0;
                size--;
                shiftBackward(idx);
                return val;
            }
            idx = (idx + 1) & mask;
            dist++;
        }
        return null;
    }

    private void shiftBackward(int idx) {
        int next = (idx + 1) & mask;
        while (keys[next] != null && displacements[next] > 0) {
            keys[idx] = keys[next];
            values[idx] = values[next];
            displacements[idx] = displacements[next] - 1;
            keys[next] = null;
            values[next] = null;
            displacements[next] = 0;
            idx = next;
            next = (next + 1) & mask;
        }
    }

    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void rehash() {
        K[] oldKeys = keys;
        V[] oldValues = values;
        int oldCap = capacity;
        capacity *= 2;
        mask = capacity - 1;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        displacements = new int[capacity];
        size = 0;
        for (int i = 0; i < oldCap; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }
}
