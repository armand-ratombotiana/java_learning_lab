package com.algo.lab09;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DivideAndConquerTest {

    @Test
    void testMergeSort() {
        Integer[] arr = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.mergeSort(arr);
        assertArrayEquals(new Integer[]{3, 9, 10, 27, 38, 43, 82}, arr);
    }

    @Test
    void testMergeSortEmpty() {
        Integer[] arr = {};
        DivideAndConquer.mergeSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testQuickSort() {
        Integer[] arr = {10, 7, 8, 9, 1, 5};
        DivideAndConquer.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 5, 7, 8, 9, 10}, arr);
    }

    @Test
    void testQuickSortWithDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        DivideAndConquer.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, arr);
    }

    @Test
    void testClosestPair() {
        Point[] points = {
            new Point(2, 3), new Point(12, 30), new Point(40, 50),
            new Point(5, 1), new Point(12, 10), new Point(3, 4)
        };
        double dist = DivideAndConquer.closestPair(points);
        assertTrue(dist > 0);
        assertEquals(Math.sqrt(2), dist, 0.0001);
    }

    @Test
    void testClosestPairTwoPoints() {
        Point[] points = {new Point(0, 0), new Point(3, 4)};
        assertEquals(5.0, DivideAndConquer.closestPair(points), 0.0001);
    }

    @Test
    void testMaxSubarraySum() {
        assertEquals(6, DivideAndConquer.maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
    }

    @Test
    void testMaxSubarraySumAllNegative() {
        assertEquals(-1, DivideAndConquer.maxSubarraySum(new int[]{-5, -3, -1, -7}));
    }

    @Test
    void testMaxSubarraySumAllPositive() {
        assertEquals(15, DivideAndConquer.maxSubarraySum(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void testMaxSubarraySumSingle() {
        assertEquals(7, DivideAndConquer.maxSubarraySum(new int[]{7}));
    }

    @Test
    void testMaxSubarraySumEmpty() {
        assertEquals(0, DivideAndConquer.maxSubarraySum(new int[]{}));
    }
}