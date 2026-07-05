package com.algo.lab12;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplexityExamplesTest {

    @Test
    void testConstantTime() {
        int[] arr = {10, 20, 30};
        assertEquals(20, ComplexityExamples.constantTime(arr, 1));
    }

    @Test
    void testLogarithmicTime() {
        int[] arr = {1, 3, 5, 7, 9, 11, 13};
        assertEquals(2, ComplexityExamples.logarithmicTime(arr, 5));
        assertEquals(-1, ComplexityExamples.logarithmicTime(arr, 4));
    }

    @Test
    void testLinearTime() {
        int[] arr = {4, 2, 7, 1, 9};
        assertEquals(2, ComplexityExamples.linearTime(arr, 7));
        assertEquals(-1, ComplexityExamples.linearTime(arr, 5));
    }

    @Test
    void testLinearithmicTime() {
        int[] arr = {5, 2, 8, 1, 9, 3};
        ComplexityExamples.linearithmicTime(arr);
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, arr);
    }

    @Test
    void testQuadraticTime() {
        int[] arr = {5, 2, 8, 1, 9};
        ComplexityExamples.quadraticTime(arr);
        assertArrayEquals(new int[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testSumArray() {
        assertEquals(15, ComplexityExamples.sumArray(new int[]{1, 2, 3, 4, 5}));
        assertEquals(0, ComplexityExamples.sumArray(new int[]{}));
    }

    @Test
    void testHasDuplicates() {
        assertTrue(ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 2}));
        assertFalse(ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 4}));
    }

    @Test
    void testPower() {
        assertEquals(1, ComplexityExamples.power(2, 0));
        assertEquals(2, ComplexityExamples.power(2, 1));
        assertEquals(1024, ComplexityExamples.power(2, 10));
        assertEquals(81, ComplexityExamples.power(3, 4));
    }

    @Test
    void testHeapSort() {
        int[] arr = {12, 11, 13, 5, 6, 7};
        ComplexityExamples.heapSort(arr);
        assertArrayEquals(new int[]{5, 6, 7, 11, 12, 13}, arr);
    }

    @Test
    void testHeapSortEmpty() {
        int[] arr = {};
        ComplexityExamples.heapSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }
}