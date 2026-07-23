package com.algorithms.sorting;

import java.util.Arrays;

/**
 * Bubble Sort (context: LC 912 - Sort an Array)
 * https://leetcode.com/problems/sort-an-array/
 *
 * Repeatedly step through the list, compare adjacent elements, swap if wrong order.
 *
 * Time Complexity: O(n^2) worst/avg, O(n) best (already sorted with optimization)
 * Space Complexity: O(1)
 */
public class BubbleSort {

    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static void main(String[] args) {
        BubbleSort bs = new BubbleSort();
        int[] arr1 = { 64, 34, 25, 12, 22, 11, 90 };
        bs.sort(arr1);
        System.out.println("Test 1: " + Arrays.toString(arr1) + " (expected: [11, 12, 22, 25, 34, 64, 90])");

        int[] arr2 = { 1, 2, 3, 4, 5 };
        bs.sort(arr2);
        System.out.println("Test 2 (sorted): " + Arrays.toString(arr2) + " (expected: [1, 2, 3, 4, 5])");

        int[] arr3 = { 5, 4, 3, 2, 1 };
        bs.sort(arr3);
        System.out.println("Test 3 (reverse): " + Arrays.toString(arr3) + " (expected: [1, 2, 3, 4, 5])");

        int[] arr4 = {};
        bs.sort(arr4);
        System.out.println("Test 4 (empty): " + Arrays.toString(arr4) + " (expected: [])");

        int[] arr5 = { 1 };
        bs.sort(arr5);
        System.out.println("Test 5 (single): " + Arrays.toString(arr5) + " (expected: [1])");
    }
}
