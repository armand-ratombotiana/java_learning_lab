package com.algo.lab02;

import java.util.Arrays;
import java.util.Random;

public class AdvancedSortingExample {
    public static void main(String[] args) {
        System.out.println("=== Advanced Sorting Demo ===");

        Integer[] arr1 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.mergeSort(arr1);
        System.out.println("Merge Sort: " + Arrays.toString(arr1));

        Integer[] arr2 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.quickSort(arr2);
        System.out.println("Quick Sort: " + Arrays.toString(arr2));

        Integer[] arr3 = {38, 27, 43, 3, 9, 82, 10};
        AdvancedSortingAlgorithms.heapSort(arr3);
        System.out.println("Heap Sort: " + Arrays.toString(arr3));

        int[] arr4 = {4, 2, 2, 8, 3, 3, 1};
        AdvancedSortingAlgorithms.countingSort(arr4);
        System.out.println("Counting Sort: " + Arrays.toString(arr4));

        int[] arr5 = {170, 45, 75, 90, 802, 24, 2, 66};
        AdvancedSortingAlgorithms.radixSort(arr5);
        System.out.println("Radix Sort: " + Arrays.toString(arr5));

        // Benchmark comparison on large array
        Random rand = new Random(42);
        int n = 50000;
        Integer[] large1 = rand.ints(n, 0, 1000000).boxed().toArray(Integer[]::new);
        Integer[] large2 = large1.clone();
        Integer[] large3 = large1.clone();

        long t1 = System.nanoTime();
        AdvancedSortingAlgorithms.mergeSort(large1);
        long t2 = System.nanoTime();
        AdvancedSortingAlgorithms.quickSort(large2);
        long t3 = System.nanoTime();
        AdvancedSortingAlgorithms.heapSort(large3);
        long t4 = System.nanoTime();

        System.out.printf("%nBenchmark (n=%d):%n", n);
        System.out.printf("Merge Sort:  %.2f ms%n", (t2 - t1) / 1e6);
        System.out.printf("Quick Sort:  %.2f ms%n", (t3 - t2) / 1e6);
        System.out.printf("Heap Sort:   %.2f ms%n", (t4 - t3) / 1e6);
    }
}