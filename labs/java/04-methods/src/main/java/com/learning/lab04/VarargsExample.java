package com.learning.lab04;

/**
 * Demonstrates variable arguments (varargs) using the ... syntax.
 */
public class VarargsExample {

    public static void showVarargs() {
        System.out.println("=== Varargs ===");

        System.out.println("Sum of no args: " + sum());
        System.out.println("Sum of 1, 2, 3: " + sum(1, 2, 3));
        System.out.println("Sum of 10, 20, 30, 40, 50: " + sum(10, 20, 30, 40, 50));

        System.out.print("Print all: ");
        printAll("apple", "banana", "cherry");
    }

    static int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    @SafeVarargs
    static <T> void printAll(T... items) {
        for (T item : items) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
