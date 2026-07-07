package com.dsacademy.lab27.countminsketch;

import java.util.Arrays;

public class CountMinSketch {

    private final int depth;
    private final int width;
    private final long[][] table;
    private final int[] seeds;
    private long totalCount;

    public CountMinSketch(int depth, int width) {
        this.depth = depth;
        this.width = width;
        this.table = new long[depth][width];
        this.seeds = new int[depth];
        for (int i = 0; i < depth; i++) {
            seeds[i] = 31 + i * 137;
        }
        this.totalCount = 0;
    }

    public void add(int key) {
        for (int i = 0; i < depth; i++) {
            int idx = hash(key, i);
            table[i][idx]++;
        }
        totalCount++;
    }

    public void add(int key, long count) {
        for (int i = 0; i < depth; i++) {
            int idx = hash(key, i);
            table[i][idx] += count;
        }
        totalCount += count;
    }

    public long estimateCount(int key) {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < depth; i++) {
            int idx = hash(key, i);
            min = Math.min(min, table[i][idx]);
        }
        return min;
    }

    public long getTotalCount() { return totalCount; }

    private int hash(int key, int i) {
        long h = key * 0x9E3779B97F4A7C15L + seeds[i];
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        return (int)(Math.abs(h) % width);
    }
}
