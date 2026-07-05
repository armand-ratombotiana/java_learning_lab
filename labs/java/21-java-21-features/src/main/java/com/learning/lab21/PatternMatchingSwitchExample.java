package com.learning.lab21;

/**
 * Demonstrates pattern matching for switch (finalized in Java 21).
 */
public class PatternMatchingSwitchExample {

    public static void showPatternMatchingSwitch() {
        System.out.println("=== Pattern Matching for switch ===");

        System.out.println(describe("Hello"));
        System.out.println(describe(42));
        System.out.println(describe(3.14));
        System.out.println(describe(null));
        System.out.println(describe(java.util.List.of(1, 2, 3)));
    }

    static String describe(Object obj) {
        return switch (obj) {
            case null -> "Null value";
            case String s && s.length() > 0 -> "Non-empty String: " + s;
            case Integer i when i > 0 -> "Positive Integer: " + i;
            case Double d -> "Double: " + d;
            case int[] arr -> "Array of length " + arr.length;
            default -> "Unknown type: " + obj.getClass().getSimpleName();
        };
    }
}
