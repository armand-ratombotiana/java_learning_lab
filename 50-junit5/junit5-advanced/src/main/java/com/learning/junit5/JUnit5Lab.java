package com.learning.junit5;

import java.util.*;
import java.util.stream.*;

public class JUnit5Lab {

    public static void main(String[] args) {
        System.out.println("=== JUnit 5 Advanced Lab ===\n");

        System.out.println("1. JUnit 5 Features:");
        System.out.println("   - @ParameterizedTest - Run tests with different arguments");
        System.out.println("   - @Nested - Group related tests hierarchically");
        System.out.println("   - @RepeatedTest - Run same test multiple times");
        System.out.println("   - @DisplayName - Custom test names");
        System.out.println("   - Assertions.assertThrows() - Exception testing");
        System.out.println("   - Assertions.assertTimeout() - Timeout testing");
        System.out.println("   - DynamicTest - Generate tests at runtime");
        System.out.println("   - @TempDir - Temporary directory injection");

        System.out.println("\n2. Calculator Demo:");
        Calculator calc = new Calculator();
        System.out.println("   add(2, 3) = " + calc.add(2, 3));
        System.out.println("   subtract(10, 4) = " + calc.subtract(10, 4));
        System.out.println("   multiply(3, 5) = " + calc.multiply(3, 5));
        System.out.println("   divide(10, 2) = " + calc.divide(10, 2));

        System.out.println("\n=== JUnit 5 Advanced Lab Complete ===");
    }

    static class Calculator {
        int add(int a, int b) { return a + b; }
        int subtract(int a, int b) { return a - b; }
        int multiply(int a, int b) { return a * b; }
        double divide(int a, int b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return (double) a / b;
        }
    }
}