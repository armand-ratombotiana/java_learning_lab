package com.leetcode.bloomfilter;

import java.util.BitSet;

/**
 * Custom: Design Bloom Filter
 * A probabilistic data structure for set membership with false positive possibility.
 *
 * Time Complexity: O(k) per operation where k is number of hash functions
 * Space Complexity: O(m) where m is bit array size
 */
public class DesignBloomFilter {

    private final BitSet bitSet;
    private final int size;
    private final int[] hashSeeds;

    public DesignBloomFilter(int expectedElements, double falsePositiveRate) {
        this.size = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
        int k = (int) Math.round((double) size / expectedElements * Math.log(2));
        this.bitSet = new BitSet(size);
        this.hashSeeds = new int[Math.max(k, 1)];
        for (int i = 0; i < hashSeeds.length; i++) {
            hashSeeds[i] = i * 31 + 7;
        }
    }

    private int hash(String value, int seed) {
        int result = 0;
        for (char c : value.toCharArray()) {
            result = 31 * result + c + seed;
        }
        return Math.abs(result % size);
    }

    public void add(String value) {
        for (int seed : hashSeeds) {
            bitSet.set(hash(value, seed));
        }
    }

    public boolean mightContain(String value) {
        for (int seed : hashSeeds) {
            if (!bitSet.get(hash(value, seed))) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        DesignBloomFilter bf = new DesignBloomFilter(100, 0.01);

        bf.add("apple");
        bf.add("banana");
        bf.add("cherry");

        System.out.println("Contains 'apple': " + bf.mightContain("apple") + " (expected: true)");
        System.out.println("Contains 'banana': " + bf.mightContain("banana") + " (expected: true)");
        System.out.println("Contains 'grape': " + bf.mightContain("grape") + " (expected: false, possibly true)");

        // Test with large set
        DesignBloomFilter bf2 = new DesignBloomFilter(1000, 0.01);
        for (int i = 0; i < 500; i++) {
            bf2.add("key" + i);
        }
        int falsePositives = 0;
        for (int i = 500; i < 1500; i++) {
            if (bf2.mightContain("key" + i)) falsePositives++;
        }
        System.out.println("False positives (500/1000): " + falsePositives + " (expected: ~10 at 1% rate)");
    }
}
