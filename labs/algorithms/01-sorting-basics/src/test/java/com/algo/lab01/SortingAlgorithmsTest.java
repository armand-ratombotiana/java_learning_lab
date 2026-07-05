package com.algo.lab01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortingAlgorithmsTest {

    private <T extends Comparable<T>> boolean isSorted(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].compareTo(arr[i - 1]) < 0) return false;
        }
        return true;
    }

    @Test
    void testBubbleSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.bubbleSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testBubbleSortEmpty() {
        Integer[] arr = {};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testBubbleSortSingle() {
        Integer[] arr = {1};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{1}, arr);
    }

    @Test
    void testBubbleSortAlreadySorted() {
        Integer[] arr = {1, 2, 3, 4, 5};
        SortingAlgorithms.bubbleSort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testBubbleSortDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        SortingAlgorithms.bubbleSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testSelectionSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.selectionSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testSelectionSortStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        SortingAlgorithms.selectionSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testInsertionSort() {
        Integer[] arr = {5, 2, 8, 1, 9};
        SortingAlgorithms.insertionSort(arr);
        assertTrue(isSorted(arr));
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, arr);
    }

    @Test
    void testInsertionSortReverseSorted() {
        Integer[] arr = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        SortingAlgorithms.insertionSort(arr);
        assertTrue(isSorted(arr));
    }

    @Test
    void testAllSortsProduceSameResult() {
        Integer[] original = {42, 17, 8, 99, 23, 56, 3, 11};
        Integer[] bubbleArr = original.clone();
        Integer[] selectionArr = original.clone();
        Integer[] insertionArr = original.clone();

        SortingAlgorithms.bubbleSort(bubbleArr);
        SortingAlgorithms.selectionSort(selectionArr);
        SortingAlgorithms.insertionSort(insertionArr);

        assertArrayEquals(bubbleArr, selectionArr);
        assertArrayEquals(bubbleArr, insertionArr);
    }
}