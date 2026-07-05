package com.learning.lab24;

/**
 * Demonstrates pattern matching for instanceof (Java 16+).
 */
public class InstanceOfPatternMatchingExample {

    public static void showInstanceOfMatching() {
        System.out.println("=== instanceof Pattern Matching ===");

        printLength("Hello, World!");
        printLength(42);
        printLength(java.util.List.of(1, 2, 3));
    }

    static void printLength(Object obj) {
        if (obj instanceof String s) {
            System.out.println("String length: " + s.length());
        } else if (obj instanceof Integer i) {
            System.out.println("Integer: " + i);
        } else {
            System.out.println("Other type: " + obj.getClass().getSimpleName());
        }
    }
}
