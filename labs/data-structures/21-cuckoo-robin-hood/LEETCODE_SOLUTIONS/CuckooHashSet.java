package com.leetcode.cuckoo;

import java.util.Arrays;

/**
 * Custom: Cuckoo Hashing
 * Uses two hash tables with two hash functions.
 * On collision, evict existing element to the other table.
 *
 * Time Complexity: O(1) average, amortized O(1) for insert
 * Space Complexity: O(n)
 */
public class CuckooHashSet {

    private int[] table1, table2;
    private int size;
    private static final double LOAD_FACTOR = 0.5;
    private static final int MAX_LOOP = 10;

    public CuckooHashSet(int capacity) {
        int cap = nextPowerOf2(capacity);
        table1 = new int[cap];
        table2 = new int[cap];
        Arrays.fill(table1, Integer.MIN_VALUE);
        Arrays.fill(table2, Integer.MIN_VALUE);
        size = 0;
    }

    private int nextPowerOf2(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    private int hash1(int key) { return key & (table1.length - 1); }
    private int hash2(int key) { return (key >>> 16) & (table2.length - 1); }

    public boolean add(int key) {
        if (contains(key)) return false;
        if ((double) size / table1.length > LOAD_FACTOR) rehash();

        for (int i = 0; i < MAX_LOOP; i++) {
            int h1 = hash1(key);
            if (table1[h1] == Integer.MIN_VALUE) {
                table1[h1] = key; size++; return true;
            }
            int temp = table1[h1];
            table1[h1] = key;
            key = temp;

            int h2 = hash2(key);
            if (table2[h2] == Integer.MIN_VALUE) {
                table2[h2] = key; size++; return true;
            }
            temp = table2[h2];
            table2[h2] = key;
            key = temp;
        }
        rehash();
        return add(key);
    }

    public boolean contains(int key) {
        return table1[hash1(key)] == key || table2[hash2(key)] == key;
    }

    public boolean remove(int key) {
        int h1 = hash1(key);
        if (table1[h1] == key) { table1[h1] = Integer.MIN_VALUE; size--; return true; }
        int h2 = hash2(key);
        if (table2[h2] == key) { table2[h2] = Integer.MIN_VALUE; size--; return true; }
        return false;
    }

    private void rehash() {
        int[] old1 = table1, old2 = table2;
        int newCap = table1.length * 2;
        table1 = new int[newCap]; Arrays.fill(table1, Integer.MIN_VALUE);
        table2 = new int[newCap]; Arrays.fill(table2, Integer.MIN_VALUE);
        size = 0;
        for (int k : old1) if (k != Integer.MIN_VALUE) add(k);
        for (int k : old2) if (k != Integer.MIN_VALUE) add(k);
    }

    public static void main(String[] args) {
        CuckooHashSet set = new CuckooHashSet(4);
        set.add(10); set.add(20); set.add(30);
        System.out.println("Contains 10: " + set.contains(10) + " (expected: true)");
        System.out.println("Contains 40: " + set.contains(40) + " (expected: false)");
        set.remove(10);
        System.out.println("Contains 10 after remove: " + set.contains(10) + " (expected: false)");
        set.add(40); set.add(50); set.add(60); set.add(70);
        System.out.println("Contains 70 (after rehash): " + set.contains(70) + " (expected: true)");
    }
}
