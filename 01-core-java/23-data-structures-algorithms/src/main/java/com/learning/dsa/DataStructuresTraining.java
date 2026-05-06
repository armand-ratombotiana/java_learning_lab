package com.learning.dsa;

import java.util.*;

public class DataStructuresTraining {

    public static void main(String[] args) {
        System.out.println("=== Data Structures and Algorithms Training ===");

        demonstrateTimeComplexity();
        demonstrateCollections();
        demonstrateSorting();
        demonstrateSearching();
    }

    private static void demonstrateTimeComplexity() {
        System.out.println("\n--- Big O Notation ---");

        Map<String, String> complexity = new LinkedHashMap<>();
        complexity.put("O(1)", "Constant - hash table lookup");
        complexity.put("O(log n)", "Logarithmic - binary search");
        complexity.put("O(n)", "Linear - simple loop");
        complexity.put("O(n log n)", "Linearithmic - merge sort");
        complexity.put("O(n^2)", "Quadratic - nested loops");
        complexity.put("O(2^n)", "Exponential - recursive fibonacci");

        complexity.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nData Structure Complexity:");
        String[] structures = {
            "Array: Access O(1), Search O(n), Insert O(n)",
            "LinkedList: Access O(n), Insert O(1)",
            "HashMap: Insert O(1), Search O(1)",
            "Tree: Search O(log n), Insert O(log n)",
            "Heap: Insert O(log n), Extract O(log n)"
        };
        for (String s : structures) System.out.println("  " + s);
    }

    private static void demonstrateCollections() {
        System.out.println("\n--- Java Collections Framework ---");

        System.out.println("List implementations:");
        System.out.println("  - ArrayList: Random access O(1), resizable");
        System.out.println("  - LinkedList: Insert/Delete O(1), no random access");
        System.out.println("  - Vector: Synchronized ArrayList");

        System.out.println("\nSet implementations:");
        System.out.println("  - HashSet: Unordered, O(1) operations");
        System.out.println("  - TreeSet: Sorted, O(log n) operations");
        System.out.println("  - LinkedHashSet: Insertion order preserved");

        System.out.println("\nMap implementations:");
        System.out.println("  - HashMap: Key-value, O(1) average");
        System.out.println("  - TreeMap: Sorted by keys, O(log n)");
        System.out.println("  - LinkedHashMap: Order preserved");

        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));
        Collections.sort(numbers);
        System.out.println("\nSorted list: " + numbers);

        Collections.reverse(numbers);
        System.out.println("Reversed: " + numbers);
    }

    private static void demonstrateSorting() {
        System.out.println("\n--- Sorting Algorithms ---");

        int[] arr = {64, 34, 25, 12, 22, 11, 90};

        System.out.println("Original: " + Arrays.toString(arr));

        int[] bubble = arr.clone();
        bubbleSort(bubble);
        System.out.println("Bubble Sort: " + Arrays.toString(bubble));

        int[] quick = arr.clone();
        quickSort(quick, 0, quick.length - 1);
        System.out.println("Quick Sort: " + Arrays.toString(quick));

        System.out.println("\nSorting Algorithm Comparison:");
        String[] algos = {
            "Bubble Sort: O(n^2) - simple, educational",
            "Selection Sort: O(n^2) - minimal swaps",
            "Insertion Sort: O(n^2) - good for small data",
            "Merge Sort: O(n log n) - stable, divide-conquer",
            "Quick Sort: O(n log n) average - in-place",
            "Heap Sort: O(n log n) - uses binary heap"
        };
        for (String a : algos) System.out.println("  " + a);
    }

    private static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    private static void demonstrateSearching() {
        System.out.println("\n--- Searching Algorithms ---");

        int[] sorted = {1, 5, 8, 12, 16, 23, 38, 56, 72, 91};

        System.out.println("Sorted array: " + Arrays.toString(sorted));

        int linearResult = linearSearch(sorted, 23);
        System.out.println("Linear search for 23: index " + linearResult);

        int binaryResult = binarySearch(sorted, 23);
        System.out.println("Binary search for 23: index " + binaryResult);

        System.out.println("\nSearch Algorithm Comparison:");
        String[] algos = {
            "Linear Search: O(n) - simple, unsorted data",
            "Binary Search: O(log n) - requires sorted data",
            "Interpolation Search: O(log log n) - uniform distribution",
            "Jump Search: O(sqrt(n)) - block-based search"
        };
        for (String a : algos) System.out.println("  " + a);
    }

    private static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    private static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            else if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }
}