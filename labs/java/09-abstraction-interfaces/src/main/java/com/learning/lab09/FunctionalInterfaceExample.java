package com.learning.lab09;

/**
 * Demonstrates functional interfaces and how they relate to abstraction.
 */
public class FunctionalInterfaceExample {

    public static void showFunctionalInterface() {
        System.out.println("=== Functional Interfaces ===");

        MathOperation add = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;
        MathOperation power = (a, b) -> (int) Math.pow(a, b);

        System.out.println("10 + 5 = " + add.operate(10, 5));
        System.out.println("10 * 5 = " + multiply.operate(10, 5));
        System.out.println("10 ^ 5 = " + power.operate(10, 5));
    }
}

@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}
