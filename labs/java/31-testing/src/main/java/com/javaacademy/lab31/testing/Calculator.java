package com.javaacademy.lab31.testing;

import java.util.function.BinaryOperator;
import java.util.Map;

public class Calculator {

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private static final Map<Operation, BinaryOperator<Double>> OPS = Map.of(
        Operation.ADD, (a, b) -> a + b,
        Operation.SUBTRACT, (a, b) -> a - b,
        Operation.MULTIPLY, (a, b) -> a * b,
        Operation.DIVIDE, (a, b) -> {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        }
    );

    public double calculate(double a, double b, Operation op) {
        return OPS.get(op).apply(a, b);
    }

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return a / b;
    }

    public int factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative input: " + n);
        int result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    public double sqrt(double x) {
        if (x < 0) throw new IllegalArgumentException("Cannot sqrt negative: " + x);
        return Math.sqrt(x);
    }
}
