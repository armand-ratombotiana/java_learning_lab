package com.algo.lab11;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BranchAndBoundTest {

    @Test
    void testTSP() {
        int[][] graph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        int cost = BranchAndBound.tspBranchAndBound(graph);
        assertEquals(80, cost);
    }

    @Test
    void testTSP3Cities() {
        int[][] graph = {
            {0, 10, 15},
            {10, 0, 5},
            {15, 5, 0}
        };
        int cost = BranchAndBound.tspBranchAndBound(graph);
        assertTrue(cost > 0);
    }

    @Test
    void testKnapSackBB() {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        assertEquals(220, BranchAndBound.knapSackBranchBound(weights, values, 50));
    }

    @Test
    void testKnapSackBBEmpty() {
        assertEquals(0, BranchAndBound.knapSackBranchBound(new int[]{}, new int[]{}, 50));
    }

    @Test
    void testKnapSackBBZeroCapacity() {
        assertEquals(0, BranchAndBound.knapSackBranchBound(
            new int[]{10, 20}, new int[]{60, 100}, 0));
    }

    @Test
    void testKnapSackBBLargeCapacity() {
        int[] weights = {5, 10, 15, 20};
        int[] values = {50, 100, 150, 200};
        int result = BranchAndBound.knapSackBranchBound(weights, values, 100);
        assertEquals(500, result);
    }
}