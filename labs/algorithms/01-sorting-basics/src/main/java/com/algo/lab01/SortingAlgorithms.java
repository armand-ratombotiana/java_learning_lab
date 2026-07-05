package com.algo.lab01;

/**
 * Collection of basic sorting algorithms.
 *
 * Time Complexity:
 * - Bubble Sort: O(n^2) avg/worst, O(n) best (optimized)
 * - Selection Sort: O(n^2) all cases
 * - Insertion Sort: O(n^2) avg/worst, O(n) best
 *
 * Space Complexity: O(1) for all (in-place)
 */
public class SortingAlgorithms {

    private SortingAlgorithms() {}

    /**
     * Bubble Sort with early exit optimization.
     * Repeatedly swaps adjacent elements if they are in wrong order.
     */
    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    T temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    /**
     * Selection Sort.
     * Finds minimum element and places it at the beginning.
     */
    public static <T extends Comparable<T>> void selectionSort(T[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j].compareTo(arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                T temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
    }

    /**
     * Insertion Sort.
     * Builds sorted array one element at a time.
     */
    public static <T extends Comparable<T>> void insertionSort(T[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].compareTo(key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}