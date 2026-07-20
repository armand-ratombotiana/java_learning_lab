package com.javalab.01;

import java.util.Objects;

public class HashMapSeparateChaining<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private HashMapEntry<K, V>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMapSeparateChaining() {
        buckets = (HashMapEntry<K, V>[]) new HashMapEntry[DEFAULT_CAPACITY];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public HashMapSeparateChaining(int initialCapacity) {
        buckets = (HashMapEntry<K, V>[]) new HashMapEntry[initialCapacity];
        size = 0;
    }

    private int bucketIndex(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (buckets.length - 1);
    }

    public void put(K key, V value) {
        int index = bucketIndex(key);
        HashMapEntry<K, V> entry = buckets[index];

        while (entry != null) {
            if (Objects.equals(entry.getKey(), key)) {
                entry.setValue(value);
                return;
            }
            entry = entry.getNext();
        }

        HashMapEntry<K, V> newNode = new HashMapEntry<>(key, value);
        newNode.setNext(buckets[index]);
        buckets[index] = newNode;
        size++;

        if ((double) size / buckets.length > LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        int index = bucketIndex(key);
        HashMapEntry<K, V> entry = buckets[index];

        while (entry != null) {
            if (Objects.equals(entry.getKey(), key)) {
                return entry.getValue();
            }
            entry = entry.getNext();
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = bucketIndex(key);
        HashMapEntry<K, V> entry = buckets[index];
        HashMapEntry<K, V> prev = null;

        while (entry != null) {
            if (Objects.equals(entry.getKey(), key)) {
                if (prev == null) {
                    buckets[index] = entry.getNext();
                } else {
                    prev.setNext(entry.getNext());
                }
                size--;
                return entry.getValue();
            }
            prev = entry;
            entry = entry.getNext();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        HashMapEntry<K, V>[] oldBuckets = buckets;
        buckets = (HashMapEntry<K, V>[]) new HashMapEntry[oldBuckets.length * 2];
        size = 0;

        for (HashMapEntry<K, V> head : oldBuckets) {
            HashMapEntry<K, V> current = head;
            while (current != null) {
                put(current.getKey(), current.getValue());
                current = current.getNext();
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
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
        size = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (HashMapEntry<K, V> head : buckets) {
            HashMapEntry<K, V> current = head;
            while (current != null) {
                if (!first) sb.append(", ");
                sb.append(current.getKey()).append("=").append(current.getValue());
                first = false;
                current = current.getNext();
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
