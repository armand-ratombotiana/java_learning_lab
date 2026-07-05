package com.ds10;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BloomFilterTest {

    private BloomFilter<String> filter;

    @BeforeEach
    void setUp() {
        filter = new BloomFilter<>(100, 0.01);
    }

    @Test
    void addAndContains() {
        filter.add("hello");
        assertTrue(filter.contains("hello"));
    }

    @Test
    void noFalseNegatives() {
        String[] items = {"apple", "banana", "cherry", "date", "elderberry",
                          "fig", "grape", "honeydew", "kiwi", "lemon"};
        for (String item : items) {
            filter.add(item);
        }
        for (String item : items) {
            assertTrue(filter.contains(item), "False negative for: " + item);
        }
    }

    @Test
    void unknownItemsNotReported() {
        filter.add("existing");
        assertFalse(filter.contains("nonexistent"));
    }

    @Test
    void clearRemovesAll() {
        filter.add("temp");
        assertTrue(filter.contains("temp"));
        filter.clear();
        assertFalse(filter.contains("temp"));
        assertEquals(0, filter.getAddedElements());
    }

    @Test
    void constructorParameters() {
        BloomFilter<Integer> bf = new BloomFilter<>(1000, 0.001);
        assertTrue(bf.getBitSetSize() > 0);
        assertTrue(bf.getNumHashFunctions() > 0);
    }

    @Test
    void customBitSetSizeAndHashFunctions() {
        BloomFilter<String> bf = new BloomFilter<>(256, 5);
        assertEquals(256, bf.getBitSetSize());
        assertEquals(5, bf.getNumHashFunctions());
    }

    @Test
    void falsePositiveRateDecreasesWithSize() {
        BloomFilter<String> small = new BloomFilter<>(100, 0.5);
        BloomFilter<String> large = new BloomFilter<>(100, 0.001);
        assertTrue(small.getBitSetSize() < large.getBitSetSize());
    }

    @Test
    void getAddedElements() {
        assertEquals(0, filter.getAddedElements());
        filter.add("a");
        assertEquals(1, filter.getAddedElements());
        filter.add("b");
        assertEquals(2, filter.getAddedElements());
    }

    @Test
    void largeScaleNoFalseNegatives() {
        BloomFilter<Integer> bf = new BloomFilter<>(10000, 0.001);
        for (int i = 0; i < 5000; i++) bf.add(i);
        for (int i = 0; i < 5000; i++) assertTrue(bf.contains(i));
    }

    @Test
    void optimalSizeCalculation() {
        int size = BloomFilter.optimalBitSetSize(100, 0.01);
        assertTrue(size > 0);
        int hashes = BloomFilter.optimalNumHashFunctions(100, size);
        assertTrue(hashes > 0);
    }

    @Test
    void bitDensityAfterInsert() {
        assertEquals(0.0, filter.getBitDensity(), 0.001);
        for (int i = 0; i < 50; i++) filter.add("item-" + i);
        assertTrue(filter.getBitDensity() > 0);
    }

    @Test
    void deterministicHash() {
        BloomFilter<String> bf1 = new BloomFilter<>(1000, 0.01);
        BloomFilter<String> bf2 = new BloomFilter<>(1000, 0.01);
        bf1.add("test");
        bf2.add("test");
        assertTrue(bf1.contains("test"));
        assertTrue(bf2.contains("test"));
    }
}
