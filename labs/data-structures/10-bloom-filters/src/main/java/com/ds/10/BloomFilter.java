package com.ds10;

import java.util.BitSet;
import java.util.function.Function;

/*
 * BloomFilter - Probabilistic data structure for set membership testing.
 *
 * Characteristics:
 * - No false negatives
 * - Configurable false positive rate
 * - Multiple hash functions
 * - Space efficient
 *
 * Time Complexity:
 * - add: O(k) where k = number of hash functions
 * - contains: O(k)
 *
 * Space Complexity: O(m) where m = bit array size
 *
 * False positive probability: (1 - e^(-kn/m))^k
 * where k = hash functions, n = items, m = bits
 */
public class BloomFilter<T> {

    private final BitSet bitSet;
    private final int bitSetSize;
    private final int numHashFunctions;
    private int addedElements;
    private final Function<T, Integer>[] hashFunctions;

    @SuppressWarnings("unchecked")
    public BloomFilter(int expectedElements, double falsePositiveRate) {
        this.bitSetSize = optimalBitSetSize(expectedElements, falsePositiveRate);
        this.numHashFunctions = optimalNumHashFunctions(expectedElements, bitSetSize);
        this.bitSet = new BitSet(bitSetSize);
        this.hashFunctions = new Function[this.numHashFunctions];
        for (int i = 0; i < this.numHashFunctions; i++) {
            final int seed = i;
            hashFunctions[i] = item -> {
                int h = item.hashCode();
                h ^= (h << 13) | (h >>> 19);
                h ^= (h >>> 7);
                h += seed * 0x9e3779b9;
                h ^= (h >>> 16);
                h += seed * 0x9e3779b9;
                h ^= (h >>> 16);
                return Math.abs(h) % bitSetSize;
            };
        }
        this.addedElements = 0;
    }

    public BloomFilter(int bitSetSize, int numHashFunctions) {
        this.bitSetSize = bitSetSize;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet(bitSetSize);
        this.hashFunctions = new Function[this.numHashFunctions];
        for (int i = 0; i < this.numHashFunctions; i++) {
            final int seed = i;
            hashFunctions[i] = item -> {
                int h = item.hashCode();
                h ^= (h << 13) | (h >>> 19);
                h ^= (h >>> 7);
                h += seed * 0x9e3779b9;
                h ^= (h >>> 16);
                h += seed * 0x9e3779b9;
                h ^= (h >>> 16);
                return Math.abs(h) % bitSetSize;
            };
        }
        this.addedElements = 0;
    }

    public void add(T element) {
        for (Function<T, Integer> hashFunc : hashFunctions) {
            bitSet.set(hashFunc.apply(element));
        }
        addedElements++;
    }

    public boolean contains(T element) {
        for (Function<T, Integer> hashFunc : hashFunctions) {
            if (!bitSet.get(hashFunc.apply(element))) {
                return false;
            }
        }
        return true;
    }

    public int getAddedElements() {
        return addedElements;
    }

    public int getBitSetSize() {
        return bitSetSize;
    }

    public int getNumHashFunctions() {
        return numHashFunctions;
    }

    public double getCurrentFalsePositiveRate() {
        return Math.pow(1 - Math.exp(-(double) numHashFunctions * addedElements / bitSetSize), numHashFunctions);
    }

    public void clear() {
        bitSet.clear();
        addedElements = 0;
    }

    public double getBitDensity() {
        return (double) bitSet.cardinality() / bitSetSize;
    }

    public static int optimalBitSetSize(int expectedElements, double falsePositiveRate) {
        return (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
    }

    public static int optimalNumHashFunctions(int expectedElements, int bitSetSize) {
        return (int) Math.round((double) bitSetSize / expectedElements * Math.log(2));
    }

    @Override
    public String toString() {
        return String.format("BloomFilter{bits=%d, hashes=%d, elements=%d, fpr=%.4f, density=%.4f}",
                bitSetSize, numHashFunctions, addedElements,
                getCurrentFalsePositiveRate(), getBitDensity());
    }
}
