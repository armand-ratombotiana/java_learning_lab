package com.dsacademy.lab26.hyperloglog;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Random;

public class HyperLogLogTest {

    @Test
    void testSmallCardinality() {
        HyperLogLog hll = new HyperLogLog(10);
        hll.add(42);
        hll.add(42);
        hll.add(99);
        long card = hll.cardinality();
        assertTrue(card >= 1);
        assertTrue(card <= 10);
    }

    @Test
    void testLargeCardinality() {
        HyperLogLog hll = new HyperLogLog(12);
        int n = 10000;
        for (int i = 0; i < n; i++) {
            hll.add(i);
        }
        long card = hll.cardinality();
        double error = Math.abs(card - n) / (double) n;
        assertTrue(error < 0.1, "Error too high: " + error);
    }

    @Test
    void testMerge() {
        HyperLogLog hll1 = new HyperLogLog(10);
        HyperLogLog hll2 = new HyperLogLog(10);
        for (int i = 0; i < 500; i++) hll1.add(i);
        for (int i = 500; i < 1000; i++) hll2.add(i);
        hll1.merge(hll2);
        long card = hll1.cardinality();
        double error = Math.abs(card - 1000) / 1000.0;
        assertTrue(error < 0.15);
    }

    @Test
    void testHllMerger() {
        java.util.List<HyperLogLog> sketches = new java.util.ArrayList<>();
        for (int j = 0; j < 4; j++) {
            HyperLogLog hll = new HyperLogLog(10);
            for (int i = j * 250; i < (j + 1) * 250; i++) {
                hll.add(i);
            }
            sketches.add(hll);
        }
        HyperLogLog merged = HllMerger.merge(sketches);
        long card = merged.cardinality();
        double error = Math.abs(card - 1000) / 1000.0;
        assertTrue(error < 0.15);
    }

    @Test
    void testDuplicateInserts() {
        HyperLogLog hll = new HyperLogLog(10);
        for (int i = 0; i < 1000; i++) hll.add(i);
        long first = hll.cardinality();
        for (int i = 0; i < 1000; i++) hll.add(i);
        long second = hll.cardinality();
        assertEquals(first, second);
    }

    @Test
    void testPrecisionValidation() {
        assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(3));
        assertThrows(IllegalArgumentException.class, () -> new HyperLogLog(17));
        assertDoesNotThrow(() -> new HyperLogLog(4));
        assertDoesNotThrow(() -> new HyperLogLog(16));
    }

    @Test
    void testEmptyHLL() {
        HyperLogLog hll = new HyperLogLog(10);
        assertTrue(hll.cardinality() <= 2);
    }
}
