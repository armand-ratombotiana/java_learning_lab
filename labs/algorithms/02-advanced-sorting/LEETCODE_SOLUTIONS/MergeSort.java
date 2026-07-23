package com.algorithms.sorting;

import java.util.Arrays;

/**
 * Merge Sort (context: LC 912 - Sort an Array)
 * https://leetcode.com/problems/sort-an-array/
 *
 * Divide array into halves, recursively sort, merge sorted halves.
 *
 * Time Complexity: O(n log n) for all cases
 * Space Complexity: O(n) for auxiliary array
 */
public class MergeSort {

    public void sort(int[] arr) {
        if (arr.length < 2) return;
        int mid = arr.length / 2;
        int[] left = Arrays.copyOfRange(arr, 0, mid);
        int[] right = Arrays.copyOfRange(arr, mid, arr.length);

        sort(left);
        sort(right);
        merge(arr, left, right);
    }

    private void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            arr[k++] = (left[i] <= right[j]) ? left[i++] : right[j++];
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }

    public static void main(String[] args) {
        MergeSort ms = new MergeSort();
        int[] arr1 = { 38, 27, 43, 3, 9, 82, 10 };
        ms.sort(arr1);
        System.out.println("Test 1: " + Arrays.toString(arr1) + " (expected: [3, 9, 10, 27, 38, 43, 82])");

        int[] arr2 = { 5, 4, 3, 2, 1 };
        ms.sort(arr2);
        System.out.println("Test 2: " + Arrays.toString(arr2) + " (expected: [1, 2, 3, 4, 5])");

        int[] arr3 = { 1 };
        ms.sort(arr3);
        System.out.println("Test 3 (single): " + Arrays.toString(arr3) + " (expected: [1])");
    }
}
