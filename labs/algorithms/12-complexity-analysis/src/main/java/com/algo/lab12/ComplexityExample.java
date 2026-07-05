package com.algo.lab12;

import java.util.Arrays;
import java.util.Random;

public class ComplexityExample {
    public static void main(String[] args) {
        System.out.println("=== Complexity Analysis Demo ===\n");

        Random rand = new Random(42);

        System.out.println("--- O(1) Constant Time ---");
        int[] arr = {10, 20, 30, 40, 50};
        System.out.println("arr[2] = " + ComplexityExamples.constantTime(arr, 2));

        System.out.println("\n--- O(log n) Binary Search ---");
        int[] sorted = {1, 3, 5, 7, 9, 11, 13, 15};
        System.out.println("Index of 9: " + ComplexityExamples.logarithmicTime(sorted, 9));

        System.out.println("\n--- O(log n) Power ---");
        System.out.println("2^10 = " + ComplexityExamples.power(2, 10));

        System.out.println("\n--- O(n) Sum ---");
        System.out.println("Sum: " + ComplexityExamples.sumArray(arr));

        System.out.println("\n--- O(n^2) Has Duplicates ---");
        System.out.println("Has dups: " + ComplexityExamples.hasDuplicates(new int[]{1, 2, 3, 2}));

        System.out.println("\n--- O(n log n) Sort ---");
        int[] toSort = rand.ints(20, 0, 100).toArray();
        System.out.println("Before: " + Arrays.toString(toSort));
        ComplexityExamples.linearithmicTime(toSort);
        System.out.println("After:  " + Arrays.toString(toSort));

        System.out.println("\n--- Time Measurement ---");
        int n = 100000;
        long start, end;

        int[] testArr = rand.ints(n, 0, n).toArray();
        start = System.nanoTime();
        long sum = ComplexityExamples.sumArray(testArr);
        end = System.nanoTime();
        System.out.printf("O(n) sum: sum=%d, time=%.2f ms%n", sum, (end - start) / 1e6);

        int[] sortArr = rand.ints(n, 0, n).toArray();
        start = System.nanoTime();
        ComplexityExamples.linearithmicTime(sortArr);
        end = System.nanoTime();
        System.out.printf("O(n log n) sort: time=%.2f ms%n", (end - start) / 1e6);

        int[] bubbleArr = rand.ints(10000, 0, 10000).toArray();
        start = System.nanoTime();
        ComplexityExamples.quadraticTime(bubbleArr);
        end = System.nanoTime();
        System.out.printf("O(n^2) bubble sort (n=10000): time=%.2f ms%n", (end - start) / 1e6);
    }
}