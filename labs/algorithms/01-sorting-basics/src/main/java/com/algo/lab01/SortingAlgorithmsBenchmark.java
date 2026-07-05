package com.algo.lab01;

import java.util.Random;

/**
 * Benchmark comparing sorting algorithm performance.
 */
public class SortingAlgorithmsBenchmark {
    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 10000};

        for (int n : sizes) {
            System.out.printf("\n--- Array size: %d ---%n", n);
            Integer[] base = generateRandomArray(n);

            Integer[] arr1 = base.clone();
            long start = System.nanoTime();
            SortingAlgorithms.bubbleSort(arr1);
            long end = System.nanoTime();
            System.out.printf("Bubble Sort: %.2f ms%n", (end - start) / 1e6);

            Integer[] arr2 = base.clone();
            start = System.nanoTime();
            SortingAlgorithms.selectionSort(arr2);
            end = System.nanoTime();
            System.out.printf("Selection Sort: %.2f ms%n", (end - start) / 1e6);

            Integer[] arr3 = base.clone();
            start = System.nanoTime();
            SortingAlgorithms.insertionSort(arr3);
            end = System.nanoTime();
            System.out.printf("Insertion Sort: %.2f ms%n", (end - start) / 1e6);
        }
    }

    private static Integer[] generateRandomArray(int n) {
        Random rand = new Random(42);
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(n * 10);
        }
        return arr;
    }
}