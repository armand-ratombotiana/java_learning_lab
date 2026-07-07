package com.algo.lab31;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OnlineAlgorithmsTest {

    @Test
    void testLRU() {
        OnlinePaging.LRU lru = new OnlinePaging.LRU(3);
        int faults = lru.simulate(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3});
        assertTrue(faults > 0);
    }

    @Test
    void testFIFO() {
        OnlinePaging.FIFO fifo = new OnlinePaging.FIFO(3);
        int faults = fifo.simulate(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3});
        assertTrue(faults > 0);
    }

    @Test
    void testMarker() {
        OnlinePaging.Marker marker = new OnlinePaging.Marker(3);
        int faults = marker.simulate(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3});
        assertTrue(faults > 0);
    }

    @Test
    void testSkiRental() {
        int cost = SkiRental.deterministic(5, 3);
        assertEquals(3, cost);
        cost = SkiRental.deterministic(5, 10);
        assertEquals(9, cost);
    }

    @Test
    void testSkiRentalCompetitiveRatio() {
        double ratio = SkiRental.competitiveRatio(5);
        assertTrue(ratio > 1.5 && ratio < 2.0);
    }

    @Test
    void testSecretaryProblem() {
        double prob = SecretaryProblem.successProbability(100, 1000);
        assertTrue(prob > 0.25 && prob < 0.50);
    }

    @Test
    void testMultiArmedBandit() {
        MultiArmedBandit bandit = new MultiArmedBandit(3, 0.1);
        for (int t = 0; t < 100; t++) {
            int arm = bandit.selectArm();
            double reward = arm == 0 ? 1.0 : 0.0;
            bandit.update(arm, reward);
        }
        assertTrue(bandit.getCount(0) > bandit.getCount(1));
    }
}
