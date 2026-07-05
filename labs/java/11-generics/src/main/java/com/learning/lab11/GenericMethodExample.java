package com.learning.lab11;

import java.util.List;

/**
 * Demonstrates generic methods and bounded type parameters.
 */
public class GenericMethodExample {

    public static void showGenericMethods() {
        System.out.println("=== Generic Methods & Bounded Types ===");

        Integer[] nums = {1, 2, 3, 4, 5};
        String[] names = {"Alice", "Bob", "Charlie"};

        System.out.println("First element: " + getFirst(nums));
        System.out.println("First element: " + getFirst(names));

        List<Integer> intList = List.of(3, 1, 4, 1, 5);
        double avg = average(intList);
        System.out.println("Average: " + avg);

        System.out.println("Max of 10, 20, 15: " + max(10, 20, 15));
    }

    static <T> T getFirst(T[] array) {
        return array.length > 0 ? array[0] : null;
    }

    static <T extends Number> double average(List<T> list) {
        double sum = 0.0;
        for (T item : list) {
            sum += item.doubleValue();
        }
        return sum / list.size();
    }

    static <T extends Comparable<T>> T max(T a, T b, T c) {
        T max = a;
        if (b.compareTo(max) > 0) max = b;
        if (c.compareTo(max) > 0) max = c;
        return max;
    }
}
