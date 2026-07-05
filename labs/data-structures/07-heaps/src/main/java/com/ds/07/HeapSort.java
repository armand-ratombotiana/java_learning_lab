package com.ds07;

import java.util.Arrays;

/*
 * HeapSort - Sorting algorithm using heap data structure.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(1) (in-place)
 */
public class HeapSort {

    public static <T extends Comparable<T>> void sort(T[] array) {
        int n = array.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        for (int i = n - 1; i > 0; i--) {
            T temp = array[0];
            array[0] = array[i];
            array[i] = temp;
            heapify(array, i, 0);
        }
    }

    private static <T extends Comparable<T>> void heapify(T[] array, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && array[left].compareTo(array[largest]) > 0) {
            largest = left;
        }
        if (right < n && array[right].compareTo(array[largest]) > 0) {
            largest = right;
        }
        if (largest != i) {
            T swap = array[i];
            array[i] = array[largest];
            array[largest] = swap;
            heapify(array, n, largest);
        }
    }

    public static void sort(int[] array) {
        Integer[] boxed = Arrays.stream(array).boxed().toArray(Integer[]::new);
        sort(boxed);
        for (int i = 0; i < array.length; i++) {
            array[i] = boxed[i];
        }
    }
}
