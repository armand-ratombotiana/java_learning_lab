package com.algo.lab03;

import java.util.Arrays;

public class SearchingExample {
    public static void main(String[] args) {
        System.out.println("=== Searching Algorithms Demo ===");

        Integer[] items = {10, 20, 30, 40, 50, 60, 70, 80, 90};
        System.out.println("Array: " + Arrays.toString(items));

        int target = 50;
        int linResult = SearchingAlgorithms.linearSearch(items, target);
        int binIter = SearchingAlgorithms.binarySearchIterative(items, target);
        int binRecur = SearchingAlgorithms.binarySearchRecursive(items, target);
        System.out.printf("Linear search(%d): index=%d%n", target, linResult);
        System.out.printf("Binary iterative(%d): index=%d%n", target, binIter);
        System.out.printf("Binary recursive(%d): index=%d%n", target, binRecur);

        target = 55;
        System.out.printf("%nSearching for %d (not present):%n", target);
        System.out.printf("Linear: %d | BinaryIter: %d | BinaryRecur: %d%n",
            SearchingAlgorithms.linearSearch(items, target),
            SearchingAlgorithms.binarySearchIterative(items, target),
            SearchingAlgorithms.binarySearchRecursive(items, target));

        int[] intArr = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        System.out.printf("%nInterpolation search for 14: index=%d%n",
            SearchingAlgorithms.interpolationSearch(intArr, 14));
        System.out.printf("Interpolation search for 3 (not present): %d%n",
            SearchingAlgorithms.interpolationSearch(intArr, 3));
    }
}