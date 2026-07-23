package com.algorithms.caching;

import java.util.*;

/**
 * LeetCode 146: LRU Cache
 * https://leetcode.com/problems/lru-cache/
 *
 * Design a cache that evicts the least recently used item first.
 *
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 */
public class LRUCache extends LinkedHashMap<Integer, Integer> {

    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    public int get(int key) {
        return super.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        super.put(key, value);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }

    public static void main(String[] args) {
        LRUCache lru = new LRUCache(2);
        lru.put(1, 1);
        lru.put(2, 2);
        System.out.println("Get 1: " + lru.get(1) + " (expected: 1)");
        lru.put(3, 3);
        System.out.println("Get 2: " + lru.get(2) + " (expected: -1)");
        lru.put(4, 4);
        System.out.println("Get 1: " + lru.get(1) + " (expected: -1)");
        System.out.println("Get 3: " + lru.get(3) + " (expected: 3)");
        System.out.println("Get 4: " + lru.get(4) + " (expected: 4)");
    }
}
