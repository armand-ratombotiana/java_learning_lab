package com.algorithms.sorting;

import java.util.Arrays;

/**
 * Insertion Sort (context: LC 912 - Sort an Array)
 * https://leetcode.com/problems/sort-an-array/
 *
 * Build the sorted array one element at a time by inserting each element
 * into its correct position.
 *
 * Time Complexity: O(n^2) worst/avg, O(n) best (already sorted)
 * Space Complexity: O(1)
 */
public class InsertionSort {

    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        InsertionSort is = new InsertionSort();
        int[] arr1 = { 12, 11, 13, 5, 6 };
        is.sort(arr1);
        System.out.println("Test 1: " + Arrays.toString(arr1) + " (expected: [5, 6, 11, 12, 13])");

        int[] arr2 = { 1, 2, 3, 4 };
        is.sort(arr2);
        System.out.println("Test 2 (sorted): " + Arrays.toString(arr2) + " (expected: [1, 2, 3, 4])");

        int[] arr3 = {};
        is.sort(arr3);
        System.out.println("Test 3 (empty): " + Arrays.toString(arr3) + " (expected: [])");
    }
}
