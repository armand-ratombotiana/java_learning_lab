package com.javalab.02;

import java.util.Objects;

public class MainImplementation<K, V> {

    static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] table;
    private int size;
    private int modCount;
    private final float loadFactor;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MainImplementation(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0)
            throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.table = (Node<K, V>[]) new Node[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    public MainImplementation() {
        this(16, 0.75f);
    }

    private int hash(K key) {
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (table.length - 1);
    }

    public V put(K key, V value) {
        Objects.requireNonNull(key);
        int idx = hash(key);
        Node<K, V> head = table[idx];
        if (head == null) {
            table[idx] = new Node<>(key, value);
        } else {
            Node<K, V> curr = head;
            while (curr != null) {
                if (Objects.equals(curr.key, key)) {
                    V old = curr.value;
                    curr.value = value;
                    return old;
                }
                if (curr.next == null) break;
                curr = curr.next;
            }
            curr.next = new Node<>(key, value);
        }
        size++;
        modCount++;
        if (size > threshold) resize();
        return null;
    }

    public V get(K key) {
        if (key == null) return null;
        int idx = hash(key);
        Node<K, V> curr = table[idx];
        while (curr != null) {
            if (Objects.equals(curr.key, key)) return curr.value;
            curr = curr.next;
        }
        return null;
    }

    public V remove(K key) {
        if (key == null) return null;
        int idx = hash(key);
        Node<K, V> curr = table[idx];
        Node<K, V> prev = null;
        while (curr != null) {
            if (Objects.equals(curr.key, key)) {
                if (prev == null) table[idx] = curr.next;
                else prev.next = curr.next;
                size--;
                modCount++;
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCap = table.length * 2;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCap];
        for (Node<K, V> head : table) {
            Node<K, V> curr = head;
            while (curr != null) {
                Node<K, V> next = curr.next;
                int idx = (curr.key.hashCode() ^ (curr.key.hashCode() >>> 16)) & (newCap - 1);
                curr.next = newTable[idx];
                newTable[idx] = curr;
                curr = next;
            }
        }
        table = newTable;
        threshold = (int) (newCap * loadFactor);
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void clear() {
        for (int i = 0; i < table.length; i++) table[i] = null;
        size = 0;
        modCount++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Node<K, V> head : table) {
            Node<K, V> curr = head;
            while (curr != null) {
                if (!first) sb.append(", ");
                sb.append(curr.key).append("=").append(curr.value);
                first = false;
                curr = curr.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
