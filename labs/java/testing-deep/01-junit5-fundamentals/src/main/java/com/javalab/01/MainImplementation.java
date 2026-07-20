package com.javalab.01;

import java.util.function.IntUnaryOperator;

public class MainImplementation {
    
    public int add(int a, int b) { return a + b; }
    public int divide(int a, int b) { if (b == 0) throw new ArithmeticException("Division by zero"); return a / b; }
    public boolean isPositive(int n) { return n > 0; }
    
    public static class Calculator {
        private final IntUnaryOperator operator;
        public Calculator(IntUnaryOperator op) { this.operator = op; }
        public int compute(int input) { return operator.applyAsInt(input); }
    }
}
