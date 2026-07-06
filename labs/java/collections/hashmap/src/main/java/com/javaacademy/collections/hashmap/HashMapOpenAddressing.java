package com.javaacademy.collections.hashmap;

import java.util.Objects;

/**
 * A hash map implemented using open addressing with linear probing.
 *
 * <p>Unlike separate chaining, open addressing stores entries directly in the
 * bucket array. When a collision occurs, linear probing scans sequentially
 * through the array to find the next available slot. This implementation:
 * <ul>
 *   <li>Uses {@code (key == null ? 0 : key.hashCode()) % capacity} as the base index</li>
 *   <li>Probes with {@code (index + 1) % capacity} on collision</li>
 *   <li>Performs a full rehash on remove to preserve probe chain integrity</li>
 *   <li>Resizes (doubles capacity) when the load factor exceeds 0.75</li>
 * </ul>
 *
 * <p>Open addressing is more cache-friendly than separate chaining but
 * requires careful handling of deletions to avoid breaking probe chains.
 *
 * <p><b>Note:</b> null keys are not supported because {@code null} is used
 * internally as the empty-slot sentinel.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class HashMapOpenAddressing<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private K[] keys;
    private V[] values;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public HashMapOpenAddressing() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public HashMapOpenAddressing(int initialCapacity) {
        this.capacity = initialCapacity;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        size = 0;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return (h & 0x7FFFFFFF) % capacity;
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "null keys not supported in open addressing");
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        int startIndex = index;

        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                values[index] = value;
                return;
            }
            index = (index + 1) % capacity;
            if (index == startIndex) {
                resize();
                put(key, value);
                return;
            }
        }

        keys[index] = key;
        values[index] = value;
        size++;
    }

    public V get(K key) {
        Objects.requireNonNull(key, "null keys not supported in open addressing");
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

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        Objects.requireNonNull(key, "null keys not supported in open addressing");
        int index = hash(key);
        int startIndex = index;

        while (keys[index] != null) {
            if (Objects.equals(keys[index], key)) {
                V oldValue = values[index];
                keys[index] = null;
                values[index] = null;
                size--;
                rehashCluster(index);
                return oldValue;
            }
            index = (index + 1) % capacity;
            if (index == startIndex) break;
        }
        return null;
    }

    private void rehashCluster(int removedIndex) {
        int index = (removedIndex + 1) % capacity;
        while (keys[index] != null) {
            K rehashKey = keys[index];
            V rehashValue = values[index];
            keys[index] = null;
            values[index] = null;
            size--;
            int correctIndex = hash(rehashKey);
            if (correctIndex != index) {
                put(rehashKey, rehashValue);
            } else {
                keys[index] = rehashKey;
                values[index] = rehashValue;
                size++;
            }
            index = (index + 1) % capacity;
        }
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

        for (int i = 0; i < oldCapacity; i++) {
            if (oldKeys[i] != null) {
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
