package com.learning.lab10;

/**
 * Demonstrates try-catch-finally blocks for exception handling.
 */
public class TryCatchFinallyExample {

    public static void showTryCatchFinally() {
        System.out.println("=== try-catch-finally ===");

        System.out.println("Case 1: No exception");
        safeDivide(10, 2);

        System.out.println("Case 2: Division by zero");
        safeDivide(10, 0);

        System.out.println("Case 3: Multiple catch blocks");
        processValue("not-a-number");
    }

    static void safeDivide(int a, int b) {
        try {
            int result = a / b;
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException: " + e.getMessage());
        } finally {
            System.out.println("  finally block always executes");
        }
    }

    static void processValue(String value) {
        try {
            int num = Integer.parseInt(value);
            System.out.println("Parsed: " + num);
            int[] arr = new int[1];
            arr[num] = 0;
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException: " + e.getMessage());
        }
    }
}
