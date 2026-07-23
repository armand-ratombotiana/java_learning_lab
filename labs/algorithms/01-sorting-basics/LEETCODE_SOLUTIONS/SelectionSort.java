package com.algorithms.sorting;

import java.util.Arrays;

/**
 * Selection Sort (context: LC 912 - Sort an Array)
 * https://leetcode.com/problems/sort-an-array/
 *
 * Find the minimum element and swap it to the front.
 *
 * Time Complexity: O(n^2) for all cases
 * Space Complexity: O(1)
 */
public class SelectionSort {

    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int temp = arr[i];
            arr[i] = arr[minIdx];
            arr[minIdx] = temp;
        }
    }

    public static void main(String[] args) {
        SelectionSort ss = new SelectionSort();
        int[] arr1 = { 64, 25, 12, 22, 11 };
        ss.sort(arr1);
        System.out.println("Test 1: " + Arrays.toString(arr1) + " (expected: [11, 12, 22, 25, 64])");

        int[] arr2 = { 3, 1, 2 };
        ss.sort(arr2);
        System.out.println("Test 2: " + Arrays.toString(arr2) + " (expected: [1, 2, 3])");

        int[] arr3 = {};
        ss.sort(arr3);
        System.out.println("Test 3 (empty): " + Arrays.toString(arr3) + " (expected: [])");
    }
}
