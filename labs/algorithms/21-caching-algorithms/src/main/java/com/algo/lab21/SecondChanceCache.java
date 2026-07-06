package com.algo.lab21;

import java.util.*;

/**
 * Second-Chance (Clock) cache eviction algorithm.
 * Gives entries a second chance before eviction using a reference bit.
 * Time: O(1) average for get/put, Space: O(capacity)
 */
public class SecondChanceCache<K, V> {

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private Node<K, V> head;
    private Node<K, V> hand;

    public SecondChanceCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) return null;
        node.referenced = true;
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            existing.referenced = true;
            return;
        }
        if (map.size() >= capacity) {
            evict();
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.referenced = true;
        map.put(key, newNode);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
            hand = head;
        } else {
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
        }
    }

    private void evict() {
        while (hand != null && hand.referenced) {
            hand.referenced = false;
            hand = hand.next;
        }
        if (hand == null) return;
        map.remove(hand.key);
        if (hand.next == hand) {
            head = null;
            hand = null;
        } else {
            hand.prev.next = hand.next;
            hand.next.prev = hand.prev;
            if (hand == head) head = hand.next;
            hand = hand.next;
        }
    }

    public int size() {
        return map.size();
    }

    public void clear() {
        map.clear();
        head = null;
        hand = null;
    }

    private static class Node<K, V> {
        final K key;
        V value;
        boolean referenced;
        Node<K, V> next;
        Node<K, V> prev;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
