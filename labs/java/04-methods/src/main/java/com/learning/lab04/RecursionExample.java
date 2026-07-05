package com.learning.lab04;

/**
 * Demonstrates recursion — a method calling itself.
 */
public class RecursionExample {

    public static void showRecursion() {
        System.out.println("=== Recursion ===");

        int n = 7;
        System.out.println("Factorial of " + n + ": " + factorial(n));

        int fibN = 10;
        System.out.println("Fibonacci number at position " + fibN + ": " + fibonacci(fibN));

        System.out.print("Countdown from 5: ");
        countdown(5);
        System.out.println();
    }

    static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    static int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    static void countdown(int n) {
        if (n < 0) return;
        System.out.print(n + " ");
        countdown(n - 1);
    }
}
