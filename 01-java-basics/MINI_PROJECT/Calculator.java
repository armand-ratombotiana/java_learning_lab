package com.learning.lab.module01.mini;

public class Calculator {
    public int add(int a, int b) { return a + b; }
    public int subtract(int a, int b) { return a - b; }
    public int multiply(int a, int b) { return a * b; }
    public double divide(int a, int b) { 
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return (double) a / b; 
    }
    public int modulo(int a, int b) { return a % b; }
    
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println("5 + 3 = " + calc.add(5, 3));
        System.out.println("10 / 2 = " + calc.divide(10, 2));
    }
}