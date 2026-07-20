package com.javalab.01;

import java.util.Objects;

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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashMapEntry<?, ?> that)) return false;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    public int hashCode() {
        return Objects.hash(key, value);
    }

    public String toString() {
        return key + "=" + value;
    }
}
