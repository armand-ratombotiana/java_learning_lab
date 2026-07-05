package com.algo.lab13;

import java.util.Arrays;
import java.util.Random;

public class ParallelExample {
    public static void main(String[] args) {
        System.out.println("=== Parallel Algorithms Demo ===\n");

        Random rand = new Random(42);
        int n = 10_000_000;
        int[] arr = rand.ints(n, 0, 1000).toArray();

        System.out.println("--- Parallel Sum ---");
        long start = System.nanoTime();
        long pSum = ParallelAlgorithms.parallelSum(arr);
        long pTime = System.nanoTime();
        System.out.printf("Parallel sum: %d (%.2f ms)%n", pSum, (pTime - start) / 1e6);

        start = System.nanoTime();
        long sSum = ParallelAlgorithms.sequentialSum(arr);
        long sTime = System.nanoTime();
        System.out.printf("Sequential sum: %d (%.2f ms)%n", sSum, (sTime - start) / 1e6);
        System.out.printf("Speedup: %.2fx%n", (double)(sTime - start) / (pTime - start));

        System.out.println("\n--- Parallel Sort ---");
        int[] sortArr = rand.ints(5_000_000, 0, 10_000_000).toArray();
        int[] sortArrSeq = sortArr.clone();

        start = System.nanoTime();
        ParallelAlgorithms.parallelSort(sortArr);
        pTime = System.nanoTime();
        System.out.printf("Parallel sort: %.2f ms%n", (pTime - start) / 1e6);
        System.out.println("Sorted: " + isSorted(sortArr));

        start = System.nanoTime();
        Arrays.sort(sortArrSeq);
        sTime = System.nanoTime();
        System.out.printf("Arrays.sort: %.2f ms%n", (sTime - start) / 1e6);

        System.out.println("\n--- Parallel Matrix Multiply ---");
        int size = 512;
        int[][] a = new int[size][size];
        int[][] b = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a[i][j] = rand.nextInt(10);
                b[i][j] = rand.nextInt(10);
            }
        }
        start = System.nanoTime();
        int[][] result = ParallelAlgorithms.parallelMatrixMultiply(a, b);
        pTime = System.nanoTime();
        System.out.printf("Parallel matrix multiply (%dx%d): %.2f ms%n",
            size, size, (pTime - start) / 1e6);
        System.out.printf("Sample: result[0][0] = %d%n", result[0][0]);
    }

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] > arr[i]) return false;
        }
        return true;
    }
}