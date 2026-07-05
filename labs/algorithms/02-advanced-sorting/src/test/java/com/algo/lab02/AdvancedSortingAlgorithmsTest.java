package com.algo.lab02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class AdvancedSortingAlgorithmsTest {

    private <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].compareTo(arr[i - 1]) < 0) return false;
        }
        return true;
    }

    @Test
    void testMergeSort() {
        Integer[] arr = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.mergeSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testMergeSortEmpty() {
        Integer[] arr = {};
        AdvancedSortingAlgorithms.mergeSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testQuickSort() {
        Integer[] arr = {10, 7, 8, 9, 1, 5};
        AdvancedSortingAlgorithms.quickSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testQuickSortWithDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        AdvancedSortingAlgorithms.quickSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testHeapSort() {
        Integer[] arr = {12, 11, 13, 5, 6, 7};
        AdvancedSortingAlgorithms.heapSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testHeapSortStrings() {
        String[] arr = {"zebra", "apple", "monkey", "banana"};
        AdvancedSortingAlgorithms.heapSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testCountingSort() {
        int[] arr = {4, 2, 2, 8, 3, 3, 1};
        AdvancedSortingAlgorithms.countingSort(arr);
        assertArrayEquals(new int[]{1, 2, 2, 3, 3, 4, 8}, arr);
    }

    @Test
    void testCountingSortEmpty() {
        int[] arr = {};
        AdvancedSortingAlgorithms.countingSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testRadixSort() {
        int[] arr = {170, 45, 75, 90, 802, 24, 2, 66};
        AdvancedSortingAlgorithms.radixSort(arr);
        assertArrayEquals(new int[]{2, 24, 45, 66, 75, 90, 170, 802}, arr);
    }

    @Test
    void testRadixSortWithZero() {
        int[] arr = {0, 5, 3, 0, 9, 1};
        AdvancedSortingAlgorithms.radixSort(arr);
        assertArrayEquals(new int[]{0, 0, 1, 3, 5, 9}, arr);
    }

    @Test
    void testAllComparisonSortsAgree() {
        Integer[] original = {42, 17, 8, 99, 23, 56, 3, 11, 77, 31};
        Integer[] mergeArr = original.clone();
        Integer[] quickArr = original.clone();
        Integer[] heapArr = original.clone();

        AdvancedSortingAlgorithms.mergeSort(mergeArr);
        AdvancedSortingAlgorithms.quickSort(quickArr);
        AdvancedSortingAlgorithms.heapSort(heapArr);

        assertArrayEquals(mergeArr, quickArr);
        assertArrayEquals(mergeArr, heapArr);
    }
}