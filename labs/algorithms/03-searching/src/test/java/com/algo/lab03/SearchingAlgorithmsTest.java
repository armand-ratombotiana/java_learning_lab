package com.algo.lab03;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchingAlgorithmsTest {

    private static final Integer[] SORTED = {10, 20, 30, 40, 50, 60, 70, 80, 90};
    private static final int[] INT_SORTED = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};

    @Test
    void testLinearSearchFound() {
        assertEquals(0, SearchingAlgorithms.linearSearch(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.linearSearch(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.linearSearch(SORTED, 90));
    }

    @Test
    void testLinearSearchNotFound() {
        assertEquals(-1, SearchingAlgorithms.linearSearch(SORTED, 5));
        assertEquals(-1, SearchingAlgorithms.linearSearch(SORTED, 100));
    }

    @Test
    void testLinearSearchEmpty() {
        Integer[] empty = {};
        assertEquals(-1, SearchingAlgorithms.linearSearch(empty, 1));
    }

    @Test
    void testBinarySearchIterativeFound() {
        assertEquals(0, SearchingAlgorithms.binarySearchIterative(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.binarySearchIterative(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.binarySearchIterative(SORTED, 90));
    }

    @Test
    void testBinarySearchIterativeNotFound() {
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 5));
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 100));
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(SORTED, 55));
    }

    @Test
    void testBinarySearchIterativeEmpty() {
        Integer[] empty = {};
        assertEquals(-1, SearchingAlgorithms.binarySearchIterative(empty, 1));
    }

    @Test
    void testBinarySearchRecursiveFound() {
        assertEquals(0, SearchingAlgorithms.binarySearchRecursive(SORTED, 10));
        assertEquals(4, SearchingAlgorithms.binarySearchRecursive(SORTED, 50));
        assertEquals(8, SearchingAlgorithms.binarySearchRecursive(SORTED, 90));
    }

    @Test
    void testBinarySearchRecursiveNotFound() {
        assertEquals(-1, SearchingAlgorithms.binarySearchRecursive(SORTED, 55));
    }

    @Test
    void testBinaryMethodsAgree() {
        for (int i = 0; i < SORTED.length; i++) {
            int v = SORTED[i];
            assertEquals(
                SearchingAlgorithms.binarySearchIterative(SORTED, v),
                SearchingAlgorithms.binarySearchRecursive(SORTED, v));
        }
    }

    @Test
    void testInterpolationSearchFound() {
        assertEquals(0, SearchingAlgorithms.interpolationSearch(INT_SORTED, 2));
        assertEquals(6, SearchingAlgorithms.interpolationSearch(INT_SORTED, 14));
        assertEquals(9, SearchingAlgorithms.interpolationSearch(INT_SORTED, 20));
    }

    @Test
    void testInterpolationSearchNotFound() {
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 3));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 1));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(INT_SORTED, 25));
    }

    @Test
    void testInterpolationSearchSingleElement() {
        int[] arr = {5};
        assertEquals(0, SearchingAlgorithms.interpolationSearch(arr, 5));
        assertEquals(-1, SearchingAlgorithms.interpolationSearch(arr, 3));
    }
}