package com.algorithms.sorting;

import java.util.Arrays;

/**
 * Quick Sort (context: LC 912 - Sort an Array)
 * https://leetcode.com/problems/sort-an-array/
 *
 * Pick a pivot, partition around it, recursively sort partitions.
 *
 * Time Complexity: O(n log n) avg, O(n^2) worst
 * Space Complexity: O(log n) for recursion stack
 */
public class QuickSort {

    public void sort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        QuickSort qs = new QuickSort();
        int[] arr1 = { 10, 7, 8, 9, 1, 5 };
        qs.sort(arr1);
        System.out.println("Test 1: " + Arrays.toString(arr1) + " (expected: [1, 5, 7, 8, 9, 10])");

        int[] arr2 = { 3, 1, 4, 1, 5, 9, 2, 6 };
        qs.sort(arr2);
        System.out.println("Test 2: " + Arrays.toString(arr2) + " (expected: [1, 1, 2, 3, 4, 5, 6, 9])");
    }
}
