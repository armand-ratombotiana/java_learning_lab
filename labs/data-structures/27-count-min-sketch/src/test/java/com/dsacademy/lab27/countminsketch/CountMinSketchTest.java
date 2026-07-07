package com.dsacademy.lab27.countminsketch;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CountMinSketchTest {

    @Test
    void testPointQuery() {
        CountMinSketch cms = new CountMinSketch(5, 100);
        cms.add(42);
        cms.add(42);
        cms.add(42);
        long est = cms.estimateCount(42);
        assertTrue(est >= 3, "Estimate should be at least 3, got " + est);
    }

    @Test
    void testNeverAdded() {
        CountMinSketch cms = new CountMinSketch(3, 50);
        assertEquals(0, cms.estimateCount(999));
    }

    @Test
    void testMultipleItems() {
        CountMinSketch cms = new CountMinSketch(4, 100);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < i; j++) {
                cms.add(i);
            }
        }
        for (int i = 0; i < 100; i++) {
            assertTrue(cms.estimateCount(i) >= i);
        }
    }

    @Test
    void testConservativeEstimate() {
        CountMinSketch cms = new CountMinSketch(3, 1000);
        cms.add(1, 100);
        cms.add(2, 200);
        assertTrue(cms.estimateCount(1) <= cms.estimateCount(2));
    }

    @Test
    void testTotalCount() {
        CountMinSketch cms = new CountMinSketch(3, 50);
        cms.add(1); cms.add(2);
        cms.add(1); cms.add(3);
        assertEquals(4, cms.getTotalCount());
    }

    @Test
    void testLargeCount() {
        CountMinSketch cms = new CountMinSketch(5, 1000);
        int n = 10000;
        for (int i = 0; i < n; i++) {
            cms.add(i % 100);
        }
        assertEquals(n, cms.getTotalCount());
    }
}
