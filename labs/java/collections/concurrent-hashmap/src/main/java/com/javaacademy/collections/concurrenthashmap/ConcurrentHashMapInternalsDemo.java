package com.javaacademy.collections.concurrenthashmap;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates core internal concepts of {@code java.util.concurrent.ConcurrentHashMap}:
 * table sizing, resizing, and treeification.
 *
 * <p>This class does not replicate the real implementation but illustrates
 * the algorithms and heuristics that make CHM highly scalable.
 *
 * <h3>Table Sizing</h3>
 * The internal table length is always a power of two. Indexing uses
 * {@code (hash ^ (hash >>> 16)) & (table.length - 1)}, which distributes
 * entries uniformly and avoids expensive modulo operations.
 *
 * <h3>Resizing (Transfer)</h3>
 * When the load factor threshold is exceeded, the table is doubled.
 * The transfer operation in real CHM uses a {@link java.util.concurrent.ForkJoinPool}
 * style work-stealing so that multiple threads can help with the resize.
 *
 * <h3>Treeification</h3>
 * If a bucket's linked list exceeds {@code TREEIFY_THRESHOLD = 8} and the
 * table is large enough (minimum 64), the list is converted to a balanced
 * tree ({@code TreeNode}) to guarantee O(log n) worst-case lookups.
 */
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
