package com.algo.lab01;

import java.util.Arrays;

/**
 * Example class demonstrating sorting algorithms.
 */
public class SortingExample {
    public static void main(String[] args) {
        System.out.println("=== Sorting Algorithms Demo ===");

        Integer[] arr1 = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("Original: " + Arrays.toString(arr1));
        SortingAlgorithms.bubbleSort(arr1);
        System.out.println("Bubble Sort: " + Arrays.toString(arr1));

        Integer[] arr2 = {64, 34, 25, 12, 22, 11, 90};
        SortingAlgorithms.selectionSort(arr2);
        System.out.println("Selection Sort: " + Arrays.toString(arr2));

        Integer[] arr3 = {64, 34, 25, 12, 22, 11, 90};
        SortingAlgorithms.insertionSort(arr3);
        System.out.println("Insertion Sort: " + Arrays.toString(arr3));

        String[] words = {"banana", "apple", "cherry", "date"};
        SortingAlgorithms.insertionSort(words);
        System.out.println("Sorted words: " + Arrays.toString(words));
    }
}