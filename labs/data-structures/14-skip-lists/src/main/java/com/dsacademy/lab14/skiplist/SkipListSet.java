package com.dsacademy.lab14.skiplist;

import java.util.Collection;
import java.util.Iterator;

public class SkipListSet<K extends Comparable<K>> implements Iterable<K> {

    private final SkipList<K, Boolean> skipList;

    public SkipListSet() {
        this.skipList = new SkipList<>();
    }

    public boolean add(K key) {
        if (skipList.contains(key)) return false;
        skipList.insert(key, Boolean.TRUE);
        return true;
    }

    public boolean remove(K key) {
        return skipList.delete(key);
    }

    public boolean contains(K key) {
        return skipList.contains(key);
    }

    public int size() {
        return skipList.size();
    }

    public boolean isEmpty() {
        return skipList.isEmpty();
    }

    public void addAll(Collection<? extends K> keys) {
        for (K key : keys) add(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private SkipListNode<K, Boolean> current = null;
            private boolean started = false;

            @Override
            public boolean hasNext() {
                if (!started) {
                    started = true;
                    SkipListNode<K, Boolean> node = skipList.header.forward[0];
                    current = node;
                    return current != null;
                }
                return current != null && current.forward[0] != null;
            }

            @Override
            public K next() {
                if (current == null) throw new java.util.NoSuchElementException();
                K key = current.key;
                current = current.forward[0];
                return key;
            }
        };
    }
}
