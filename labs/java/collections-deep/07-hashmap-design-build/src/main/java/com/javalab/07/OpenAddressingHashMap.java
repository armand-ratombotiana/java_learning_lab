package com.javalab.07;

import java.util.Objects;

public class OpenAddressingHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.7;
    private static final Object TOMBSTONE = new Object();

    private K[] keys;
    private V[] values;
    private int size;
    private int capacity;
    private int tombstoneCount;

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public OpenAddressingHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.size = 0;
        this.tombstoneCount = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return (h & 0x7FFFFFFF) % capacity;
    }

    private int probe(K key) {
        int index = hash(key);
        int firstTombstone = -1;
        int startIndex = index;

        do {
            if (keys[index] == null) {
                return firstTombstone >= 0 ? firstTombstone : index;
            }
            if (keys[index] == TOMBSTONE) {
                if (firstTombstone < 0) firstTombstone = index;
            } else if (Objects.equals(keys[index], key)) {
                return index;
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);

        return firstTombstone >= 0 ? firstTombstone : index;
    }

    public void put(K key, V value) {
        if ((double) (size + tombstoneCount) / capacity >= MAX_LOAD_FACTOR) {
            resize();
        }
        int index = probe(key);
        if (keys[index] == null || keys[index] == TOMBSTONE) {
            keys[index] = key;
            values[index] = value;
            size++;
            if (keys[index] == TOMBSTONE) tombstoneCount--;
        } else {
            values[index] = value;
        }
    }

    public V get(K key) {
        int index = hash(key);
        int startIndex = index;

        do {
            if (keys[index] == null) return null;
            if (keys[index] != TOMBSTONE && Objects.equals(keys[index], key)) {
                return values[index];
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);

        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = hash(key);
        int startIndex = index;

        do {
            if (keys[index] == null) return null;
            if (keys[index] != TOMBSTONE && Objects.equals(keys[index], key)) {
                V oldValue = values[index];
                keys[index] = (K) TOMBSTONE;
                values[index] = null;
                size--;
                tombstoneCount++;
                return oldValue;
            }
            index = (index + 1) % capacity;
        } while (index != startIndex);

        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        K[] oldKeys = keys;
        V[] oldValues = values;
        int oldCapacity = capacity;

        capacity *= 2;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        size = 0;
        tombstoneCount = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldKeys[i] != null && oldKeys[i] != TOMBSTONE) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            keys[i] = null;
            values[i] = null;
        }
        size = 0;
        tombstoneCount = 0;
    }

    public double loadFactor() {
        return (double) size / capacity;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null && keys[i] != TOMBSTONE) {
                if (!first) sb.append(", ");
                sb.append(keys[i]).append("=").append(values[i]);
                first = false;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
