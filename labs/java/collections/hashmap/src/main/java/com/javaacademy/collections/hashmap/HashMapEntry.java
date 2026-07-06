package com.javaacademy.collections.hashmap;

import java.util.Objects;

/**
 * A simple key-value pair entry used by custom HashMap implementations.
 * Each entry holds a key, a value, and a reference to the next entry
 * in the bucket's linked list (for separate chaining).
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class HashMapEntry<K, V> {

    private final K key;
    private V value;
    HashMapEntry<K, V> next;

    public HashMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public HashMapEntry<K, V> getNext() {
        return next;
    }

    public void setNext(HashMapEntry<K, V> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashMapEntry<?, ?> that)) return false;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
