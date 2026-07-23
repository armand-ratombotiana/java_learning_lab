package com.leetcode.lrucache;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 146: LRU Cache
 * https://leetcode.com/problems/lru-cache/
 *
 * Design a data structure that follows the Least Recently Used (LRU) cache policy.
 * Both get and put must run in O(1) average time.
 *
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 */
public class LRUCache {

    private static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTail() {
        Node node = tail.prev;
        removeNode(node);
        return node;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.val;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.val = value;
            moveToHead(node);
            return;
        }
        if (map.size() >= capacity) {
            Node removed = removeTail();
            map.remove(removed.key);
        }
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToHead(newNode);
    }

    public static void main(String[] args) {
        LRUCache lru = new LRUCache(2);
        lru.put(1, 1);
        lru.put(2, 2);
        System.out.println("Get 1: " + lru.get(1) + " (expected: 1)");
        lru.put(3, 3); // evicts key 2
        System.out.println("Get 2: " + lru.get(2) + " (expected: -1)");
        lru.put(4, 4); // evicts key 1
        System.out.println("Get 1: " + lru.get(1) + " (expected: -1)");
        System.out.println("Get 3: " + lru.get(3) + " (expected: 3)");
        System.out.println("Get 4: " + lru.get(4) + " (expected: 4)");

        // Edge: capacity 1
        LRUCache lru2 = new LRUCache(1);
        lru2.put(1, 10);
        System.out.println("Get 1 (cap=1): " + lru2.get(1) + " (expected: 10)");
        lru2.put(2, 20);
        System.out.println("Get 1 after evict: " + lru2.get(1) + " (expected: -1)");
        System.out.println("Get 2: " + lru2.get(2) + " (expected: 20)");
    }
}
