package com.javalab.10;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Objects;
import java.util.function.Function;

public class BloomFilter<E> {

    private final BitSet bits;
    private final int bitSize;
    private final int numHashFunctions;
    private final Function<E, byte[]> hashFunction;
    private int insertions;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (expectedInsertions <= 0 || falsePositiveRate <= 0 || falsePositiveRate >= 1) {
            throw new IllegalArgumentException(
                    "expectedInsertions > 0 and 0 < falsePositiveRate < 1 required");
        }
        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveRate);
        this.numHashFunctions = optimalHashFunctions(expectedInsertions, bitSize);
        this.bits = new BitSet(bitSize);
        this.hashFunction = this::defaultHash;
        this.insertions = 0;
    }

    public BloomFilter(int expectedInsertions, double falsePositiveRate,
                       Function<E, byte[]> hashFunction) {
        this(expectedInsertions, falsePositiveRate);
    }

    private static int optimalBitSize(int n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    private static int optimalHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    private byte[] defaultHash(E element) {
        if (element instanceof String s) {
            return s.getBytes(StandardCharsets.UTF_8);
        }
        if (element instanceof Number) {
            return ByteBuffer.allocate(Long.BYTES)
                    .putLong(((Number) element).longValue())
                    .array();
        }
        return element.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void add(E element) {
        Objects.requireNonNull(element);
        byte[] bytes = hashFunction.apply(element);
        int h1 = hash(bytes, 0);
        int h2 = hash(bytes, 1);
        for (int i = 0; i < numHashFunctions; i++) {
            int index = Math.abs((h1 + i * h2) % bitSize);
            bits.set(index);
        }
        insertions++;
    }

    public boolean mightContain(E element) {
        Objects.requireNonNull(element);
        byte[] bytes = hashFunction.apply(element);
        int h1 = hash(bytes, 0);
        int h2 = hash(bytes, 1);
        for (int i = 0; i < numHashFunctions; i++) {
            int index = Math.abs((h1 + i * h2) % bitSize);
            if (!bits.get(index)) return false;
        }
        return true;
    }

    private int hash(byte[] data, int seed) {
        int hash = seed;
        for (byte b : data) {
            hash = hash * 31 + (b & 0xFF);
        }
        return hash;
    }

    public double estimatedFalsePositiveRate() {
        if (insertions == 0) return 0.0;
        double p = Math.exp(-((double) numHashFunctions * insertions) / bitSize);
        return Math.pow(1 - p, numHashFunctions);
    }

    public int insertions() {
        return insertions;
    }

    public int bitSize() {
        return bitSize;
    }

    public int numHashFunctions() {
        return numHashFunctions;
    }

    public void clear() {
        bits.clear();
        insertions = 0;
    }
}
