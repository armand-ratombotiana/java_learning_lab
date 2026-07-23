package com.algorithms.bloomfilter;

import java.util.BitSet;
import java.util.Random;

/**
 * Custom: Bloom Filter Variants
 * Standard Bloom, Counting Bloom, and Cuckoo Filter concepts.
 *
 * Time Complexity: O(k) per operation
 * Space Complexity: O(m)
 */
public class BloomFilterVariants {

    // Standard Bloom Filter
    public static class StandardBloom {
        private final BitSet bits;
        private final int size;
        private final int[] seeds;

        public StandardBloom(int expectedElements, double fpr) {
            size = (int) Math.ceil(-expectedElements * Math.log(fpr) / (Math.log(2) * Math.log(2)));
            bits = new BitSet(size);
            int k = Math.max(1, (int) Math.round((double) size / expectedElements * Math.log(2)));
            seeds = new int[k];
            Random r = new Random(42);
            for (int i = 0; i < k; i++) seeds[i] = r.nextInt();
        }

        private int hash(String val, int seed) {
            int h = seed;
            for (char c : val.toCharArray()) h = 31 * h + c;
            return Math.abs(h) % size;
        }

        public void add(String val) { for (int s : seeds) bits.set(hash(val, s)); }
        public boolean mightContain(String val) {
            for (int s : seeds) if (!bits.get(hash(val, s))) return false;
            return true;
        }
    }

    // Counting Bloom Filter (supports delete)
    public static class CountingBloom {
        private final int[] counters;
        private final int size;
        private final int[] seeds;

        public CountingBloom(int expectedElements, double fpr) {
            size = (int) Math.ceil(-expectedElements * Math.log(fpr) / (Math.log(2) * Math.log(2)));
            counters = new int[size];
            int k = Math.max(1, (int) Math.round((double) size / expectedElements * Math.log(2)));
            seeds = new int[k];
            Random r = new Random(42);
            for (int i = 0; i < k; i++) seeds[i] = r.nextInt();
        }

        private int hash(String val, int seed) {
            int h = seed;
            for (char c : val.toCharArray()) h = 31 * h + c;
            return Math.abs(h) % size;
        }

        public void add(String val) { for (int s : seeds) counters[hash(val, s)]++; }
        public boolean delete(String val) {
            for (int s : seeds) if (counters[hash(val, s)] <= 0) return false;
            for (int s : seeds) counters[hash(val, s)]--;
            return true;
        }
        public boolean mightContain(String val) {
            for (int s : seeds) if (counters[hash(val, s)] <= 0) return false;
            return true;
        }
    }

    public static void main(String[] args) {
        StandardBloom bf = new StandardBloom(100, 0.01);
        bf.add("apple"); bf.add("banana");
        System.out.println("Standard Bloom: 'apple'? " + bf.mightContain("apple") + " (true)");
        System.out.println("Standard Bloom: 'grape'? " + bf.mightContain("grape") + " (usually false)");

        CountingBloom cbf = new CountingBloom(100, 0.01);
        cbf.add("test"); cbf.add("test");
        System.out.println("Counting Bloom: 'test'? " + cbf.mightContain("test") + " (true)");
        cbf.delete("test");
        System.out.println("Counting Bloom: 'test' after 1 delete? " + cbf.mightContain("test") + " (true)");
        cbf.delete("test");
        System.out.println("Counting Bloom: 'test' after 2 deletes? " + cbf.mightContain("test") + " (false)");
    }
}
