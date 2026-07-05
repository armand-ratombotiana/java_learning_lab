package com.algo.lab03;

/**
 * Searching algorithms.
 *
 * Linear Search: O(n) time, O(1) space
 * Binary Search (Iterative): O(log n) time, O(1) space
 * Binary Search (Recursive): O(log n) time, O(log n) space
 * Interpolation Search: O(log log n) avg, O(n) worst, O(1) space
 */
public class SearchingAlgorithms {

    private SearchingAlgorithms() {}

    /**
     * Linear search - finds index of target in array.
     * @return index of target or -1 if not found
     */
    public static <T extends Comparable<T>> int linearSearch(T[] arr, T target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].compareTo(target) == 0) return i;
        }
        return -1;
    }

    /**
     * Iterative binary search on sorted array.
     */
    public static <T extends Comparable<T>> int binarySearchIterative(T[] arr, T target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = arr[mid].compareTo(target);
            if (cmp == 0) return mid;
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    /**
     * Recursive binary search on sorted array.
     */
    public static <T extends Comparable<T>> int binarySearchRecursive(T[] arr, T target) {
        return binarySearchRecursive(arr, target, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> int binarySearchRecursive(T[] arr, T target, int left, int right) {
        if (left > right) return -1;
        int mid = left + (right - left) / 2;
        int cmp = arr[mid].compareTo(target);
        if (cmp == 0) return mid;
        if (cmp < 0) return binarySearchRecursive(arr, target, mid + 1, right);
        return binarySearchRecursive(arr, target, left, mid - 1);
    }

    /**
     * Interpolation search on sorted uniformly distributed array.
     * Works best with integers.
     */
    public static int interpolationSearch(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high && target >= arr[low] && target <= arr[high]) {
            if (low == high) {
                return arr[low] == target ? low : -1;
            }
            int pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low]);
            if (arr[pos] == target) return pos;
            if (arr[pos] < target) low = pos + 1;
            else high = pos - 1;
        }
        return -1;
    }
}