package com.leetcode.hyperloglog;

/**
 * Custom: HyperLogLog for Cardinality Estimation
 * Estimates the number of unique elements in a multiset using very little memory.
 *
 * Time Complexity: O(1) per add, O(1) for count
 * Space Complexity: O(m) where m is number of registers
 */
public class HyperLogLog {

    private final int[] registers;
    private final int m;
    private final double alphaMM;

    public HyperLogLog(int b) {
        this.m = 1 << b;
        this.registers = new int[m];
        this.alphaMM = (m == 16 ? 0.673 : m == 32 ? 0.697 : m == 64 ? 0.709 : 0.7213 / (1 + 1.079 / m)) * m * m;
    }

    private int hash(int value) {
        value ^= (value << 13);
        value ^= (value >>> 17);
        value ^= (value << 5);
        return value;
    }

    public void add(int value) {
        int h = hash(value);
        int idx = h & (m - 1);
        int w = h >>> Integer.numberOfLeadingZeros(m);
        registers[idx] = Math.max(registers[idx], Integer.numberOfLeadingZeros(w) + 1);
    }

    public long count() {
        double sum = 0;
        for (int r : registers) sum += 1.0 / (1 << r);
        double estimate = alphaMM / sum;
        if (estimate <= 2.5 * m) {
            int zeroCount = 0;
            for (int r : registers) if (r == 0) zeroCount++;
            if (zeroCount > 0) estimate = m * Math.log((double) m / zeroCount);
        }
        return Math.round(estimate);
    }

    public static void main(String[] args) {
        HyperLogLog hll = new HyperLogLog(10);
        for (int i = 0; i < 10000; i++) hll.add(i);
        System.out.println("Estimated unique count: " + hll.count() + " (expected: ~10000)");

        // Duplicates shouldn't change count
        for (int i = 0; i < 5000; i++) hll.add(i);
        System.out.println("After duplicates: " + hll.count() + " (expected: ~10000)");
    }
}
