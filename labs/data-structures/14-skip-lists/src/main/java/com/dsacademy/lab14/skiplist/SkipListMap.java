package com.dsacademy.lab14.skiplist;

import java.util.Collection;

public class SkipListMap<K extends Comparable<K>, V> {

    private final SkipList<K, V> skipList;

    public SkipListMap() {
        this.skipList = new SkipList<>();
    }

    public void put(K key, V value) {
        skipList.insert(key, value);
    }

    public V get(K key) {
        return skipList.search(key);
    }

    public V remove(K key) {
        V value = skipList.search(key);
        skipList.delete(key);
        return value;
    }

    public boolean containsKey(K key) {
        return skipList.contains(key);
    }

    public int size() {
        return skipList.size();
    }

    public boolean isEmpty() {
        return skipList.isEmpty();
    }

    public Collection<K> keySet() {
        java.util.List<K> keys = new java.util.ArrayList<>();
        SkipListNode<K, V> node = skipList.header.forward[0];
        while (node != null) {
            keys.add(node.key);
            node = node.forward[0];
        }
        return keys;
    }
}
