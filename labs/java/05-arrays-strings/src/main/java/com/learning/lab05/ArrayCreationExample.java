package com.learning.lab05;

import java.util.Arrays;

/**
 * Demonstrates array creation, iteration, copying, and utility methods.
 */
public class ArrayCreationExample {

    public static void showArrays() {
        System.out.println("=== Array Creation & Iteration ===");

        int[] numbers = new int[5];
        numbers[0] = 10;
        numbers[1] = 20;
        numbers[2] = 30;
        numbers[3] = 40;
        numbers[4] = 50;

        String[] fruits = {"Apple", "Banana", "Cherry"};

        System.out.print("For loop iteration: ");
        for (int i = 0; i < numbers.length; i++) {
            System.out.print(numbers[i] + " ");
        }
        System.out.println();

        System.out.print("For-each iteration: ");
        for (String fruit : fruits) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        int[] copy = Arrays.copyOf(numbers, numbers.length);
        System.out.println("Copied array: " + Arrays.toString(copy));

        int[] sorted = {5, 3, 1, 4, 2};
        Arrays.sort(sorted);
        System.out.println("Sorted: " + Arrays.toString(sorted));

        int idx = Arrays.binarySearch(sorted, 3);
        System.out.println("Binary search for 3: index " + idx);

        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("Filled array: " + Arrays.toString(filled));
    }
}
