package com.leetcode.countmin;

import java.util.Random;

/**
 * Custom: Count-Min Sketch
 * Probabilistic data structure for frequency estimation.
 * Always overestimates (never underestimates) frequencies.
 *
 * Time Complexity: O(1) per operation
 * Space Complexity: O(d * w)
 */
public class CountMinSketch {

    private final int[][] table;
    private final int[] hashSeeds;
    private final int width;
    private static final int DEPTH = 4;

    public CountMinSketch(int width) {
        this.width = width;
        this.table = new int[DEPTH][width];
        this.hashSeeds = new int[DEPTH];
        Random rand = new Random(42);
        for (int i = 0; i < DEPTH; i++) hashSeeds[i] = rand.nextInt();
    }

    private int hash(int value, int seed) {
        long h = value ^ seed;
        h = (h ^ (h >>> 33)) * 0xff51afd7ed558ccdL;
        h = (h ^ (h >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return (int) (Math.abs(h) % width);
    }

    public void add(int value) {
        for (int i = 0; i < DEPTH; i++) {
            table[i][hash(value, hashSeeds[i])]++;
        }
    }

    public int estimate(int value) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < DEPTH; i++) {
            min = Math.min(min, table[i][hash(value, hashSeeds[i])]);
        }
        return min;
    }

    public static void main(String[] args) {
        CountMinSketch cms = new CountMinSketch(1000);
        for (int i = 0; i < 1000; i++) {
            cms.add(i % 100);
        }
        System.out.println("Estimate of 5: " + cms.estimate(5) + " (expected: ~10)");

        // Overestimation test
        cms.add(999);
        int est = cms.estimate(999);
        System.out.println("Estimate of 999 (added once): " + est + " (expected: >= 1)");

        // Large frequency
        CountMinSketch cms2 = new CountMinSketch(10000);
        for (int i = 0; i < 100000; i++) cms2.add(42);
        System.out.println("Estimate of 42 (added 100K times): " + cms2.estimate(42) + " (expected: >= 100000)");
    }
}
