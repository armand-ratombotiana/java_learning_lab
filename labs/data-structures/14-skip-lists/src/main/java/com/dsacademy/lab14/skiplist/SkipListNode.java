package com.dsacademy.lab14.skiplist;

public class SkipListNode<K extends Comparable<K>, V> {

    K key;
    V value;
    SkipListNode<K, V>[] forward;
    int level;

    @SuppressWarnings("unchecked")
    public SkipListNode(K key, V value, int level) {
        this.key = key;
        this.value = value;
        this.level = level;
        this.forward = new SkipListNode[level + 1];
    }
}
