package com.learning.lab24;

/**
 * Demonstrates switch pattern matching with type patterns and when (guarded) clauses.
 */
public class SwitchPatternMatchingExample {

    public static void showSwitchMatching() {
        System.out.println("=== Switch Pattern Matching ===");

        System.out.println(evaluate("Hello"));
        System.out.println(evaluate(0));
        System.out.println(evaluate(-5));
        System.out.println(evaluate(42));
        System.out.println(evaluate(3.14));
        System.out.println(evaluate(null));
        System.out.println(evaluate(new int[]{1, 2, 3}));
    }

    static String evaluate(Object obj) {
        return switch (obj) {
            case null -> "Null value";
            case String s when s.length() > 5 -> "Long string: " + s;
            case String s -> "Short string: " + s;
            case Integer i when i < 0 -> "Negative integer: " + i;
            case Integer i when i == 0 -> "Zero";
            case Integer i -> "Positive integer: " + i;
            case Double d -> "Double: " + d;
            case int[] arr -> "Array of " + arr.length + " elements";
            default -> "Unknown: " + obj.getClass().getSimpleName();
        };
    }
}
