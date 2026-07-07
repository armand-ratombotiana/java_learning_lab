package com.dsacademy.lab21.hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuckooHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.5;
    private static final int MAX_CYCLE = 32;

    private K[] keys1, keys2;
    private V[] values1, values2;
    private int size;
    private final Random random;
    private final List<K> stash;
    private int capacity;

    public CuckooHashMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CuckooHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.keys1 = (K[]) new Object[capacity];
        this.keys2 = (K[]) new Object[capacity];
        this.values1 = (V[]) new Object[capacity];
        this.values2 = (V[]) new Object[capacity];
        this.size = 0;
        this.random = new Random();
        this.stash = new ArrayList<>();
    }

    private int hash1(K key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    private int hash2(K key) {
        int h = (key.hashCode() * 0x9E3779B9) & 0x7fffffff;
        return (h % capacity + capacity) % capacity;
    }

    public void put(K key, V value) {
        if (contains(key)) {
            remove(key);
        }
        if (size >= capacity * LOAD_FACTOR) {
            rehash();
        }
        K curKey = key;
        V curVal = value;
        int cycles = 0;
        while (cycles < MAX_CYCLE) {
            int h1 = hash1(curKey);
            if (keys1[h1] == null) {
                keys1[h1] = curKey;
                values1[h1] = curVal;
                size++;
                return;
            }
            K tempKey = keys1[h1];
            V tempVal = values1[h1];
            keys1[h1] = curKey;
            values1[h1] = curVal;
            curKey = tempKey;
            curVal = tempVal;

            int h2 = hash2(curKey);
            if (keys2[h2] == null) {
                keys2[h2] = curKey;
                values2[h2] = curVal;
                size++;
                return;
            }
            tempKey = keys2[h2];
            tempVal = values2[h2];
            keys2[h2] = curKey;
            values2[h2] = curVal;
            curKey = tempKey;
            curVal = tempVal;
            cycles++;
        }
        stash.add(curKey);
        size++;
    }

    public V get(K key) {
        int h1 = hash1(key);
        if (key.equals(keys1[h1])) {
            return values1[h1];
        }
        int h2 = hash2(key);
        if (key.equals(keys2[h2])) {
            return values2[h2];
        }
        int idx = stash.indexOf(key);
        if (idx >= 0) {
            return null;
        }
        return null;
    }

    public boolean contains(K key) {
        int h1 = hash1(key);
        if (key.equals(keys1[h1])) return true;
        int h2 = hash2(key);
        if (key.equals(keys2[h2])) return true;
        return stash.contains(key);
    }

    public V remove(K key) {
        int h1 = hash1(key);
        if (key.equals(keys1[h1])) {
            V val = values1[h1];
            keys1[h1] = null;
            values1[h1] = null;
            size--;
            return val;
        }
        int h2 = hash2(key);
        if (key.equals(keys2[h2])) {
            V val = values2[h2];
            keys2[h2] = null;
            values2[h2] = null;
            size--;
            return val;
        }
        if (stash.remove(key)) {
            size--;
            return null;
        }
        return null;
    }

    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void rehash() {
        capacity *= 2;
        K[] oldKeys1 = keys1;
        V[] oldValues1 = values1;
        K[] oldKeys2 = keys2;
        V[] oldValues2 = values2;
        List<K> oldStash = new ArrayList<>(stash);
        keys1 = (K[]) new Object[capacity];
        keys2 = (K[]) new Object[capacity];
        values1 = (V[]) new Object[capacity];
        values2 = (V[]) new Object[capacity];
        stash.clear();
        size = 0;
        for (int i = 0; i < oldKeys1.length; i++) {
            if (oldKeys1[i] != null) {
                put(oldKeys1[i], oldValues1[i]);
            }
            if (oldKeys2[i] != null) {
                put(oldKeys2[i], oldValues2[i]);
            }
        }
        for (K k : oldStash) {
            put(k, null);
        }
    }
}
