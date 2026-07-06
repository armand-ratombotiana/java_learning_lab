package com.dsacademy.lab14.skiplist;

import java.util.Random;

public class SkipList<K extends Comparable<K>, V> {

    private static final int MAX_LEVEL = 32;
    private static final double P = 0.5;

    private final SkipListNode<K, V> header;
    private final Random random;
    private int size;
    private int currentMaxLevel;

    public SkipList() {
        this.header = new SkipListNode<>(null, null, MAX_LEVEL);
        this.random = new Random();
        this.size = 0;
        this.currentMaxLevel = 0;
    }

    private int randomLevel() {
        int level = 0;
        while (level < MAX_LEVEL - 1 && random.nextDouble() < P) {
            level++;
        }
        return level;
    }

    public V search(K key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        SkipListNode<K, V> current = header;
        for (int i = currentMaxLevel; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
        }
        current = current.forward[0];
        if (current != null && current.key.compareTo(key) == 0) {
            return current.value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        SkipListNode<K, V>[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode<K, V> current = header;

        for (int i = currentMaxLevel; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        current = current.forward[0];

        if (current != null && current.key.compareTo(key) == 0) {
            current.value = value;
            return;
        }

        int newLevel = randomLevel();
        if (newLevel > currentMaxLevel) {
            for (int i = currentMaxLevel + 1; i <= newLevel; i++) {
                update[i] = header;
            }
            currentMaxLevel = newLevel;
        }

        SkipListNode<K, V> newNode = new SkipListNode<>(key, value, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        size++;
    }

    @SuppressWarnings("unchecked")
    public boolean delete(K key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        SkipListNode<K, V>[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode<K, V> current = header;

        for (int i = currentMaxLevel; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        current = current.forward[0];

        if (current == null || current.key.compareTo(key) != 0) {
            return false;
        }

        for (int i = 0; i <= currentMaxLevel; i++) {
            if (update[i].forward[i] != current) break;
            update[i].forward[i] = current.forward[i];
        }

        while (currentMaxLevel > 0 && header.forward[currentMaxLevel] == null) {
            currentMaxLevel--;
        }
        size--;
        return true;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
