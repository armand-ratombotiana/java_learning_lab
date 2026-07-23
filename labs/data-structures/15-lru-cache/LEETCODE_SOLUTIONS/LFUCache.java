package com.leetcode.lrucache;

import java.util.*;

/**
 * LeetCode 460: LFU Cache
 * https://leetcode.com/problems/lfu-cache/
 *
 * Design a Least Frequently Used (LFU) cache with O(1) operations.
 * When the cache reaches capacity, evict the least frequently used key.
 * If there is a tie, evict the least recently used key.
 *
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 */
public class LFUCache {

    private static class Node {
        int key, val, freq;
        Node prev, next;
        Node(int key, int val) {
            this.key = key;
            this.val = val;
            this.freq = 1;
        }
    }

    private static class FreqList {
        Node head, tail;
        FreqList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }
        void addToHead(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        boolean isEmpty() {
            return head.next == tail;
        }
        Node removeTail() {
            Node node = tail.prev;
            removeNode(node);
            return node;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> nodeMap;
    private final Map<Integer, FreqList> freqMap;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.nodeMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 0;
    }

    private void updateFreq(Node node) {
        FreqList oldList = freqMap.get(node.freq);
        oldList.removeNode(node);
        if (oldList.isEmpty() && node.freq == minFreq) minFreq++;

        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new FreqList()).addToHead(node);
    }

    public int get(int key) {
        Node node = nodeMap.get(key);
        if (node == null) return -1;
        updateFreq(node);
        return node.val;
    }

    public void put(int key, int value) {
        if (capacity <= 0) return;
        Node node = nodeMap.get(key);
        if (node != null) {
            node.val = value;
            updateFreq(node);
            return;
        }
        if (nodeMap.size() >= capacity) {
            FreqList minList = freqMap.get(minFreq);
            Node evicted = minList.removeTail();
            nodeMap.remove(evicted.key);
        }
        Node newNode = new Node(key, value);
        nodeMap.put(key, newNode);
        minFreq = 1;
        freqMap.computeIfAbsent(1, k -> new FreqList()).addToHead(newNode);
    }

    public static void main(String[] args) {
        LFUCache lfu = new LFUCache(2);
        lfu.put(1, 1);
        lfu.put(2, 2);
        System.out.println("Get 1: " + lfu.get(1) + " (expected: 1)");
        lfu.put(3, 3); // evicts key 2 (freq=1, LRU)
        System.out.println("Get 2: " + lfu.get(2) + " (expected: -1)");
        System.out.println("Get 3: " + lfu.get(3) + " (expected: 3)");
        lfu.put(4, 4); // evicts key 1 (freq=1, LRU)
        System.out.println("Get 1: " + lfu.get(1) + " (expected: -1)");
        System.out.println("Get 3: " + lfu.get(3) + " (expected: 3)");
        System.out.println("Get 4: " + lfu.get(4) + " (expected: 4)");
    }
}
