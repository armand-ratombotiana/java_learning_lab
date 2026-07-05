package com.learning.lab15;

import java.util.*;

/**
 * Demonstrates pure functions and immutability principles.
 */
public class PureFunctionExample {

    public static void showPureFunctions() {
        System.out.println("=== Pure Functions & Immutability ===");

        List<Integer> original = List.of(1, 2, 3, 4, 5);

        List<Integer> doubled = applyToEach(original, n -> n * 2);
        List<Integer> squared = applyToEach(original, n -> n * n);

        System.out.println("Original (immutable): " + original);
        System.out.println("Doubled (pure function): " + doubled);
        System.out.println("Squared (pure function): " + squared);

        int sum = sumPure(original);
        System.out.println("Sum (pure function): " + sum);

        String greeting = formatGreeting("Alice");
        System.out.println(greeting);
    }

    static <T, R> List<R> applyToEach(List<T> list, java.util.function.Function<T, R> fn) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(fn.apply(item));
        }
        return Collections.unmodifiableList(result);
    }

    static int sumPure(List<Integer> numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    static String formatGreeting(String name) {
        return "Hello, " + name + "! Welcome to functional programming.";
    }
}
