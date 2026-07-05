package com.algo.lab09;

import java.util.Arrays;
import java.util.Random;

public class DivideAndConquerExample {
    public static void main(String[] args) {
        System.out.println("=== Divide and Conquer Demo ===\n");

        System.out.println("--- Merge Sort ---");
        Integer[] arr1 = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.mergeSort(arr1);
        System.out.println("Result: " + Arrays.toString(arr1));

        System.out.println("\n--- Quick Sort ---");
        Integer[] arr2 = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.quickSort(arr2);
        System.out.println("Result: " + Arrays.toString(arr2));

        System.out.println("\n--- Closest Pair ---");
        DivideAndConquer.Point[] points = {
            new DivideAndConquer.Point(2, 3), new DivideAndConquer.Point(12, 30), new DivideAndConquer.Point(40, 50),
            new DivideAndConquer.Point(5, 1), new DivideAndConquer.Point(12, 10), new DivideAndConquer.Point(3, 4)
        };
        double minDist = DivideAndConquer.closestPair(points);
        System.out.printf("Minimum distance: %.4f%n", minDist);

        System.out.println("\n--- Max Subarray (Kadane) ---");
        int[] arr3 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Array: " + Arrays.toString(arr3));
        System.out.println("Maximum subarray sum: " + DivideAndConquer.maxSubarraySum(arr3));
    }
}