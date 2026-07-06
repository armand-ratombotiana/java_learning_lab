package com.algo.lab20;

import java.util.random.RandomGenerator;

/**
 * Randomized QuickSelect for finding the k-th smallest element.
 * Uses random pivot selection for O(n) expected time.
 * Time: O(n) expected, O(n^2) worst-case, Space: O(log n)
 */
public class RandomizedQuickSelect {

    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private RandomizedQuickSelect() {}

    public static int select(int[] arr, int k) {
        if (arr == null || k < 0 || k >= arr.length) {
            throw new IllegalArgumentException("Invalid input");
        }
        return select(arr, 0, arr.length - 1, k);
    }

    private static int select(int[] arr, int left, int right, int k) {
        if (left == right) return arr[left];
        int pivotIdx = left + RNG.nextInt(right - left + 1);
        pivotIdx = partition(arr, left, right, pivotIdx);
        if (k == pivotIdx) return arr[k];
        if (k < pivotIdx) return select(arr, left, pivotIdx - 1, k);
        return select(arr, pivotIdx + 1, right, k);
    }

    private static int partition(int[] arr, int left, int right, int pivotIdx) {
        int pivot = arr[pivotIdx];
        swap(arr, pivotIdx, right);
        int store = left;
        for (int i = left; i < right; i++) {
            if (arr[i] < pivot) {
                swap(arr, store, i);
                store++;
            }
        }
        swap(arr, store, right);
        return store;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
