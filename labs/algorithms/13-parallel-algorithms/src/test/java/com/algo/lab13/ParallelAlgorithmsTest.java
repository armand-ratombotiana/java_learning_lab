package com.algo.lab13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

class ParallelAlgorithmsTest {

    @Test
    void testParallelSum() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertEquals(55, ParallelAlgorithms.parallelSum(arr));
    }

    @Test
    void testParallelSumEmpty() {
        assertEquals(0, ParallelAlgorithms.parallelSum(new int[]{}));
    }

    @Test
    void testParallelSumLarge() {
        int[] arr = new Random(42).ints(100000, 0, 100).toArray();
        long parallel = ParallelAlgorithms.parallelSum(arr);
        long sequential = ParallelAlgorithms.sequentialSum(arr);
        assertEquals(sequential, parallel);
    }

    @Test
    void testParallelSort() {
        int[] arr = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, arr);
    }

    @Test
    void testParallelSortEmpty() {
        int[] arr = {};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testParallelSortSingle() {
        int[] arr = {1};
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(new int[]{1}, arr);
    }

    @Test
    void testParallelSortAgreesWithArraysSort() {
        int[] arr = new Random(42).ints(5000, 0, 10000).toArray();
        int[] expected = arr.clone();
        java.util.Arrays.sort(expected);
        ParallelAlgorithms.parallelSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testParallelMatrixMultiply() {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        int[][] result = ParallelAlgorithms.parallelMatrixMultiply(a, b);
        assertEquals(19, result[0][0]);
        assertEquals(22, result[0][1]);
        assertEquals(43, result[1][0]);
        assertEquals(50, result[1][1]);
    }

    @Test
    void testSequentialSum() {
        assertEquals(55, ParallelAlgorithms.sequentialSum(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals(0, ParallelAlgorithms.sequentialSum(new int[]{}));
    }
}