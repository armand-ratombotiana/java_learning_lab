package com.leetcode.bloomfilter;

import java.util.Arrays;

/**
 * LeetCode 705: Design HashSet
 * https://leetcode.com/problems/design-hashset/
 *
 * Design a HashSet without using any built-in hash table libraries.
 *
 * Time Complexity: O(1) average per operation
 * Space Complexity: O(n)
 */
public class DesignHashSet {

    private static final int SIZE = 1000;
    private final int[][] buckets;
    private final int[] bucketSizes;

    public DesignHashSet() {
        buckets = new int[SIZE][];
        bucketSizes = new int[SIZE];
    }

    private int hash(int key) {
        return key % SIZE;
    }

    public void add(int key) {
        int idx = hash(key);
        if (buckets[idx] == null) {
            buckets[idx] = new int[100];
            bucketSizes[idx] = 0;
        }
        if (!contains(key)) {
            if (bucketSizes[idx] >= buckets[idx].length) {
                buckets[idx] = Arrays.copyOf(buckets[idx], buckets[idx].length * 2);
            }
            buckets[idx][bucketSizes[idx]++] = key;
        }
    }

    public void remove(int key) {
        int idx = hash(key);
        if (buckets[idx] == null) return;
        for (int i = 0; i < bucketSizes[idx]; i++) {
            if (buckets[idx][i] == key) {
                buckets[idx][i] = buckets[idx][--bucketSizes[idx]];
                return;
            }
        }
    }

    public boolean contains(int key) {
        int idx = hash(key);
        if (buckets[idx] == null) return false;
        for (int i = 0; i < bucketSizes[idx]; i++) {
            if (buckets[idx][i] == key) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        DesignHashSet hs = new DesignHashSet();
        hs.add(1);
        hs.add(2);
        System.out.println("Contains 1: " + hs.contains(1) + " (expected: true)");
        System.out.println("Contains 3: " + hs.contains(3) + " (expected: false)");
        hs.add(2);
        System.out.println("Contains 2 (after add dup): " + hs.contains(2) + " (expected: true)");
        hs.remove(2);
        System.out.println("Contains 2 (after remove): " + hs.contains(2) + " (expected: false)");

        // Large key
        hs.add(1000000);
        System.out.println("Contains 1000000: " + hs.contains(1000000) + " (expected: true)");
    }
}
