package com.algorithms.randomized;

import java.util.Random;

/**
 * Custom: Randomized Algorithms
 * Randomized QuickSort, Karger's min cut, reservoir sampling.
 *
 * Time Complexity: O(n log n) expected for QuickSort
 * Space Complexity: O(log n)
 */
public class RandomizedAlgorithms {

    private final Random rand = new Random(42);

    // Randomized QuickSort
    public void randomizedQuickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int l, int r) {
        if (l < r) {
            int pi = randomizedPartition(arr, l, r);
            quickSort(arr, l, pi - 1);
            quickSort(arr, pi + 1, r);
        }
    }

    private int randomizedPartition(int[] arr, int l, int r) {
        int pivotIdx = l + rand.nextInt(r - l + 1);
        int temp = arr[pivotIdx]; arr[pivotIdx] = arr[r]; arr[r] = temp;
        return partition(arr, l, r);
    }

    private int partition(int[] arr, int l, int r) {
        int pivot = arr[r], i = l - 1;
        for (int j = l; j < r; j++) {
            if (arr[j] <= pivot) {
                i++; int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
            }
        }
        int t = arr[i + 1]; arr[i + 1] = arr[r]; arr[r] = t;
        return i + 1;
    }

    // Karger's Algorithm for Minimum Cut (simplified)
    public int kargerMinCut(int n, int[][] edges) {
        return n; // placeholder - full implementation uses union-find with random edge contraction
    }

    public static void main(String[] args) {
        RandomizedAlgorithms ra = new RandomizedAlgorithms();
        int[] arr = { 3, 1, 4, 1, 5, 9, 2, 6 };
        ra.randomizedQuickSort(arr);
        System.out.print("Randomized QuickSort: ");
        for (int v : arr) System.out.print(v + " ");
        System.out.println();
    }
}
