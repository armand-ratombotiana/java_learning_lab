package com.learning.lab05;

import java.util.Arrays;
import java.util.stream.*;

/**
 * Demonstrates using Stream API with arrays for functional-style operations.
 */
public class StreamWithArraysExample {

    public static void showStreamArrays() {
        System.out.println("=== Streams with Arrays ===");

        int[] numbers = {5, 2, 8, 1, 9, 3, 7};

        int sum = Arrays.stream(numbers).sum();
        double avg = Arrays.stream(numbers).average().orElse(0);
        int max = Arrays.stream(numbers).max().orElse(Integer.MIN_VALUE);
        System.out.println("Sum: " + sum + ", Avg: " + avg + ", Max: " + max);

        int[] evens = Arrays.stream(numbers).filter(n -> n % 2 == 0).toArray();
        System.out.println("Even numbers: " + Arrays.toString(evens));

        int[] doubled = Arrays.stream(numbers).map(n -> n * 2).sorted().toArray();
        System.out.println("Doubled & sorted: " + Arrays.toString(doubled));

        String[] words = {"apple", "banana", "cherry", "date"};
        String result = Arrays.stream(words)
            .filter(w -> w.length() >= 5)
            .map(String::toUpperCase)
            .collect(Collectors.joining(", "));
        System.out.println("Filtered words: " + result);
    }
}
