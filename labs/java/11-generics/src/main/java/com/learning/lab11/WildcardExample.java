package com.learning.lab11;

import java.util.List;

/**
 * Demonstrates wildcards: unbounded (?), upper-bounded (? extends T), lower-bounded (? super T).
 */
public class WildcardExample {

    public static void showWildcards() {
        System.out.println("=== Wildcards ===");

        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.1, 2.2, 3.3);
        List<String> strings = List.of("a", "b", "c");

        System.out.print("Unbounded wildcard: ");
        printAll(ints);
        printAll(strings);

        System.out.println("Sum of ints: " + sum(ints));
        System.out.println("Sum of doubles: " + sum(doubles));
    }

    static void printAll(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    static double sum(List<? extends Number> numbers) {
        double total = 0.0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }
}
