package com.ds.advanced.lab06;

import java.util.BitSet;
import java.util.function.Function;

public class BloomFilter<T> {
    private final BitSet bits;
    private final int size;
    private final Function<T, Integer>[] hashes;

    @SafeVarargs
    public BloomFilter(int size, Function<T, Integer>... hashes) {
        this.size = size;
        this.bits = new BitSet(size);
        this.hashes = hashes;
    }

    public void insert(T item) {
        for (var h : hashes) bits.set(Math.floorMod(h.apply(item), size));
    }

    public boolean mightContain(T item) {
        for (var h : hashes) if (!bits.get(Math.floorMod(h.apply(item), size))) return false;
        return true;
    }

    public double falsePositiveRate(int n) {
        double p = Math.exp(-((double) hashes.length * n) / size);
        return Math.pow(1 - p, hashes.length);
    }

    public static int optimalHashCount(int size, int n) {
        return Math.max(1, (int) Math.round((double) size / n * Math.log(2)));
    }
}