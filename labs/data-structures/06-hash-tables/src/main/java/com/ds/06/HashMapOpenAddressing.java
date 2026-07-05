package com.ds06;

import java.util.*;

/*
 * HashMapOpenAddressing - Hash table with linear probing.
 *
 * Time Complexity:
 * - put: O(1) average, O(n) worst
 * - get: O(1) average, O(n) worst
 * - remove: O(1) average, O(n) worst
 *
 * Space Complexity: O(n + capacity)
 */
public class HashMapOpenAddressing<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.5;

    private K[] keys;
    private V[] values;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public HashMapOpenAddressing() {
        this.capacity = DEFAULT_CAPACITY;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.size = 0;
    }

    private int hash(K key) {
        return (key == null ? 0 : Math.abs(key.hashCode() % capacity));
    }

    private int probe(K key) {
        int index = hash(key);
        while (keys[index] != null && !Objects.equals(keys[index], key)) {
            index = (index + 1) % capacity;
        }
        return index;
    }

    public void put(K key, V value) {
        if ((double) (size + 1) / capacity > LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        int index = probe(key);
        if (keys[index] == null) {
            keys[index] = key;
            size++;
        }
        values[index] = value;
    }

    public V get(K key) {
        int index = hash(key);
        int startIndex = index;
        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                return values[index];
            }
            index = (index + 1) % capacity;
            if (index == startIndex) break;
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        int startIndex = index;
        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                V value = values[index];
                keys[index] = null;
                values[index] = null;
                size--;
                rehash(index);
                return value;
            }
            index = (index + 1) % capacity;
            if (index == startIndex) break;
        }
        return null;
    }

    private void rehash(int start) {
        int index = (start + 1) % capacity;
        while (keys[index] != null) {
            K keyToRehash = keys[index];
            V valueToRehash = values[index];
            keys[index] = null;
            values[index] = null;
            size--;
            int newIndex = probe(keyToRehash);
            keys[newIndex] = keyToRehash;
            values[newIndex] = valueToRehash;
            size++;
            index = (index + 1) % capacity;
        }
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        K[] oldKeys = keys;
        V[] oldValues = values;
        keys = (K[]) new Object[newCapacity];
        values = (V[]) new Object[newCapacity];
        capacity = newCapacity;
        size = 0;
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) keySet.add(keys[i]);
        }
        return keySet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null) {
                if (!first) sb.append(", ");
                sb.append(keys[i]).append("=").append(values[i]);
                first = false;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
