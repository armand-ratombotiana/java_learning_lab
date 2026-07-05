package com.learning.lab02;

/**
 * Demonstrates numeric overflow, underflow, and floating-point precision issues.
 */
public class OverflowExample {

    public static void showOverflow() {
        System.out.println("=== Overflow & Precision ===");

        int maxInt = Integer.MAX_VALUE;
        System.out.println("MAX_VALUE + 1: " + (maxInt + 1) + " (overflow wraps to negative)");

        int minInt = Integer.MIN_VALUE;
        System.out.println("MIN_VALUE - 1: " + (minInt - 1) + " (underflow wraps to positive)");

        double result = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + result + " (floating-point imprecision)");

        double precise = 1.0 - 0.9;
        System.out.println("1.0 - 0.9 = " + precise);

        System.out.println("Use BigDecimal for exact decimal arithmetic");
    }
}
