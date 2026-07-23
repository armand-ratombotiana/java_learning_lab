package com.algorithms.complexity;

/**
 * Custom: Complexity Analysis Examples
 * Demonstrates O(1), O(log n), O(n), O(n log n), O(n^2) algorithms.
 *
 * Time Complexity: varies by method
 * Space Complexity: varies by method
 */
public class ComplexityAnalysis {

    // O(1) - Constant
    public int constantTime(int[] arr) { return arr[0]; }

    // O(log n) - Binary Search
    public int logarithmicTime(int[] arr, int target) {
        int l = 0, r = arr.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) l = mid + 1;
            else r = mid - 1;
        }
        return -1;
    }

    // O(n) - Linear
    public int linearTime(int[] arr) {
        int max = arr[0];
        for (int n : arr) if (n > max) max = n;
        return max;
    }

    // O(n log n) - Mergesort
    public void linearithmicTime(int[] arr) {
        if (arr.length < 2) return;
        int mid = arr.length / 2;
        int[] left = java.util.Arrays.copyOfRange(arr, 0, mid);
        int[] right = java.util.Arrays.copyOfRange(arr, mid, arr.length);
        linearithmicTime(left);
        linearithmicTime(right);
        merge(arr, left, right);
    }

    private void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) arr[k++] = left[i] <= right[j] ? left[i++] : right[j++];
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }

    // O(n^2) - Bubble Sort
    public void quadraticTime(int[] arr) {
        for (int i = 0; i < arr.length; i++)
            for (int j = i + 1; j < arr.length; j++)
                if (arr[i] > arr[j]) { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; }
    }

    public static void main(String[] args) {
        ComplexityAnalysis ca = new ComplexityAnalysis();
        int[] arr = { 64, 34, 25, 12, 22, 11, 90 };
        ca.linearithmicTime(arr);
        System.out.println("Sorted: " + java.util.Arrays.toString(arr));

        // Master theorem examples
        System.out.println("Binary search T(n) = T(n/2) + O(1) => O(log n)");
        System.out.println("Merge sort T(n) = 2T(n/2) + O(n) => O(n log n)");
        System.out.println("Bubble sort T(n) = T(n-1) + O(n) => O(n^2)");
    }
}
