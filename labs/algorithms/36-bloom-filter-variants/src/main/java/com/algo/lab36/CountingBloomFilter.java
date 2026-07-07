package com.algo.lab36;

/**
 * Counting Bloom Filter with 4-bit counters supporting add, remove, and check operations.
 * Uses multiple hash functions and a counter array instead of a bit array,
 * enabling element deletion without false negatives.
 */
public class CountingBloomFilter {
    private final int[] counters;
    private final int numHashes;
    private final int size;

    public CountingBloomFilter(int expectedElements, double falsePositiveRate) {
        this.size = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
        this.numHashes = (int) Math.ceil((double) size / expectedElements * Math.log(2));
        this.counters = new int[size];
    }

    private int[] hash(String item) {
        int[] hashes = new int[numHashes];
        int h1 = item.hashCode();
        int h2 = Integer.rotateRight(h1, 16);
        for (int i = 0; i < numHashes; i++) {
            hashes[i] = Math.abs((h1 + i * h2) % size);
        }
        return hashes;
    }

    public void add(String item) {
        for (int h : hash(item)) {
            if (counters[h] < 15) counters[h]++;
        }
    }

    public boolean contains(String item) {
        for (int h : hash(item)) {
            if (counters[h] == 0) return false;
        }
        return true;
    }

    public void remove(String item) {
        if (!contains(item)) return;
        for (int h : hash(item)) {
            if (counters[h] > 0) counters[h]--;
        }
    }

    public int getCounter(int index) { return counters[index]; }
    public int size() { return size; }
}
