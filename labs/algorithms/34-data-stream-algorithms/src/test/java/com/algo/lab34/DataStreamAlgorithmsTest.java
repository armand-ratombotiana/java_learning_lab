package com.algo.lab34;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataStreamAlgorithmsTest {

    @Test
    void testAMS() {
        int[] stream = {1, 2, 1, 3, 1, 2, 3, 3};
        AMSMomentEstimation ams = new AMSMomentEstimation(10, stream);
        long f2 = ams.estimateF2(stream);
        assertTrue(f2 > 0);
    }

    @Test
    void testFrequentItems() {
        FrequentItems fi = new FrequentItems(3);
        int[] stream = {1, 2, 1, 3, 1, 2, 3, 2, 1, 1};
        for (int v : stream) fi.add(v);
        assertTrue(fi.getCandidates().size() <= 2);
    }

    @Test
    void testSlidingWindowCount() {
        SlidingWindowCount sw = new SlidingWindowCount(5);
        for (int i = 0; i < 10; i++) sw.add(1);
        int distinct = sw.getDistinctCount();
        assertTrue(distinct >= 0);
    }

    @Test
    void testStreamStatistics() {
        StreamStatistics stats = new StreamStatistics();
        stats.add(1);
        stats.add(2);
        stats.add(3);
        stats.add(4);
        stats.add(5);
        assertEquals(3.0, stats.getMean(), 1e-9);
        assertEquals(2.5, stats.getVariance(), 1e-9);
    }

    @Test
    void testStreamStatisticsSingle() {
        StreamStatistics stats = new StreamStatistics();
        stats.add(42);
        assertEquals(0.0, stats.getVariance(), 1e-9);
    }
}
