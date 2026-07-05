package com.learning.lab04;

/**
 * Demonstrates method overloading — same name, different parameters.
 */
public class MethodOverloadingExample {

    public static void showOverloading() {
        System.out.println("=== Method Overloading ===");

        System.out.println("add(int, int): " + add(5, 3));
        System.out.println("add(int, int, int): " + add(5, 3, 2));
        System.out.println("add(double, double): " + add(5.5, 3.3));
        System.out.println("add(String, String): " + add("Hello ", "World"));
    }

    static int add(int a, int b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static String add(String a, String b) {
        return a + b;
    }
}
