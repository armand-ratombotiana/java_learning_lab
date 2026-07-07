package com.algo.lab36;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Benchmarks and compares all bloom filter variants on insertion, query,
 * deletion (where supported), false positive rate, and memory usage.
 * Runs controlled experiments on random string data and reports results.
 */
public class BloomComparison {

    public static void main(String[] args) {
        int numElements = 10000;
        double fpRate = 0.01;
        Random rng = new Random(42);
        Set<String> inserted = new HashSet<>();

        String[] items = new String[numElements * 2];
        for (int i = 0; i < items.length; i++) {
            items[i] = "item-" + rng.nextLong();
        }
        for (int i = 0; i < numElements; i++) {
            inserted.add(items[i]);
        }

        // Counting Bloom Filter
        CountingBloomFilter cbf = new CountingBloomFilter(numElements, fpRate);
        long start = System.nanoTime();
        for (int i = 0; i < numElements; i++) cbf.add(items[i]);
        long addTime = System.nanoTime() - start;

        start = System.nanoTime();
        int fp = 0;
        for (int i = numElements; i < items.length; i++) {
            if (cbf.contains(items[i])) fp++;
        }
        long queryTime = System.nanoTime() - start;
        double actualFp = (double) fp / numElements;

        System.out.printf("CountingBF: add=%dns, query=%dns, fpr=%.4f, size=%d%n",
            addTime / numElements, queryTime / numElements, actualFp, cbf.size());

        // Scalable Bloom Filter
        ScalableBloomFilter sbf = new ScalableBloomFilter(fpRate);
        start = System.nanoTime();
        for (int i = 0; i < numElements; i++) sbf.add(items[i]);
        addTime = System.nanoTime() - start;

        start = System.nanoTime();
        fp = 0;
        for (int i = numElements; i < items.length; i++) {
            if (sbf.contains(items[i])) fp++;
        }
        queryTime = System.nanoTime() - start;
        actualFp = (double) fp / numElements;

        System.out.printf("ScalableBF: add=%dns, query=%dns, fpr=%.4f, filters=%d%n",
            addTime / numElements, queryTime / numElements, actualFp, sbf.filterCount());

        // Cuckoo Filter
        CuckooFilter cf = new CuckooFilter(numElements);
        start = System.nanoTime();
        int insertedCount = 0;
        for (int i = 0; i < numElements; i++) {
            if (cf.insert(items[i])) insertedCount++;
        }
        addTime = System.nanoTime() - start;

        start = System.nanoTime();
        fp = 0;
        for (int i = numElements; i < items.length; i++) {
            if (cf.contains(items[i])) fp++;
        }
        queryTime = System.nanoTime() - start;
        actualFp = (double) fp / numElements;

        System.out.printf("CuckooFilter: add=%dns, query=%dns, fpr=%.4f, inserted=%d%n",
            addTime / insertedCount, queryTime / numElements, actualFp, insertedCount);
    }
}
