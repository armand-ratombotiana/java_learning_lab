package com.javalab.02;

import java.util.concurrent.ThreadLocalRandom;

public final class ConcurrentHashMapInternalsDemo {

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private ConcurrentHashMapInternalsDemo() {
    }

    public static void main(String[] args) {
        System.out.println("=== Table Sizing ===");
        demoTableSizing();

        System.out.println("\n=== Index Distribution ===");
        demoIndexDistribution();

        System.out.println("\n=== Treeification Threshold ===");
        demoTreeificationThresholds();
    }

    static int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static int spread(int h) {
        return h ^ (h >>> 16);
    }

    static int index(int hash, int length) {
        return (length - 1) & spread(hash);
    }

    static int treeifyThreshold(int binCount, int tableLength) {
        final int TREEIFY_THRESHOLD = 8;
        final int MIN_TREEIFY_CAPACITY = 64;
        if (binCount >= TREEIFY_THRESHOLD && tableLength >= MIN_TREEIFY_CAPACITY) {
            return tableLength * 2;
        }
        return -1;
    }

    static int untreeifyThreshold(int binCount) {
        final int UNTREEIFY_THRESHOLD = 6;
        return (binCount <= UNTREEIFY_THRESHOLD) ? 1 : 0;
    }

    private static void demoTableSizing() {
        int[] inputs = {1, 5, 16, 17, 64, 100, 1_000, 1_000_000};
        for (int c : inputs) {
            System.out.printf("  requested=%7d  actual=%7d%n", c, tableSizeFor(c));
        }
    }

    private static void demoIndexDistribution() {
        int capacity = 64;
        int[] buckets = new int[capacity];
        int samples = 1_000_000;
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < samples; i++) {
            int h = rng.nextInt();
            int idx = index(h, capacity);
            buckets[idx]++;
        }

        double ideal = (double) samples / capacity;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int b : buckets) {
            double load = b / ideal;
            if (load < min) min = load;
            if (load > max) max = load;
        }
        System.out.printf("  capacity=%d, samples=%d, ideal=%.1f, min=%.2fx, max=%.2fx%n",
                capacity, samples, ideal, min, max);
    }

    private static void demoTreeificationThresholds() {
        System.out.println("  TREEIFY_THRESHOLD   = 8");
        System.out.println("  UNTREEIFY_THRESHOLD = 6");
        System.out.println("  MIN_TREEIFY_CAPACITY = 64");

        int[][] scenarios = {
                {9, 32},
                {9, 64},
                {7, 128},
                {12, 128}
        };

        for (int[] s : scenarios) {
            int result = treeifyThreshold(s[0], s[1]);
            if (result > 0) {
                System.out.printf("  binCount=%d, tableLen=%d -> TREEIFY (new capacity=%d)%n",
                        s[0], s[1], result);
            } else {
                System.out.printf("  binCount=%d, tableLen=%d -> no treeification (table too small)%n",
                        s[0], s[1]);
            }
        }
    }
}
