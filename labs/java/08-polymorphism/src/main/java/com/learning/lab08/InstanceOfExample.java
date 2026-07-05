package com.learning.lab08;

/**
 * Demonstrates instanceof operator and pattern matching for instanceof (Java 16+).
 */
public class InstanceOfExample {

    public static void showInstanceOf() {
        System.out.println("=== instanceof & Pattern Matching ===");

        Object obj1 = "Hello, World!";
        Object obj2 = 42;
        Object obj3 = 3.14;

        checkType(obj1);
        checkType(obj2);
        checkType(obj3);
    }

    static void checkType(Object obj) {
        if (obj instanceof String s) {
            System.out.println("String of length " + s.length() + ": " + s);
        } else if (obj instanceof Integer i) {
            System.out.println("Integer with value " + i + " (squared: " + (i * i) + ")");
        } else if (obj instanceof Double d) {
            System.out.println("Double with value " + d);
        } else {
            System.out.println("Unknown type: " + obj);
        }
    }
}
