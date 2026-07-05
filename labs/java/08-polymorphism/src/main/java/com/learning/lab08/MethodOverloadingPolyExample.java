package com.learning.lab08;

/**
 * Demonstrates compile-time polymorphism through method overloading.
 */
public class MethodOverloadingPolyExample {

    public static void showOverloadingPolymorphism() {
        System.out.println("=== Compile-Time Polymorphism (Overloading) ===");

        Calculator calc = new Calculator();
        System.out.println("add(2, 3): " + calc.add(2, 3));
        System.out.println("add(2.5, 3.7): " + calc.add(2.5, 3.7));
        System.out.println("add(1, 2, 3): " + calc.add(1, 2, 3));
        System.out.println("add(1.1, 2.2, 3.3): " + calc.add(1.1, 2.2, 3.3));
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public double add(double a, double b, double c) {
        return a + b + c;
    }
}
