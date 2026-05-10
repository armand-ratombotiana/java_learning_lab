package com.learning.lab.module01;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 01: Java Basics ===");
        dataTypesDemo();
        variablesDemo();
        operatorsDemo();
        controlFlowDemo();
        arraysDemo();
        stringsDemo();
        methodsDemo();
    }

    static void dataTypesDemo() {
        System.out.println("\n--- Data Types ---");
        byte b = 127;
        short s = 32767;
        int i = 2147483647;
        long l = 9223372036854775807L;
        float f = 3.14f;
        double d = 3.141592653589793;
        char c = 'A';
        boolean bool = true;
        System.out.printf("byte: %d, short: %d, int: %d, long: %d%n", b, s, i, l);
        System.out.printf("float: %f, double: %f%n", f, d);
        System.out.println("char: " + c + ", boolean: " + bool);
    }

    static void variablesDemo() {
        System.out.println("\n--- Variables ---");
        int age = 25;
        String name = "Java";
        final double PI = 3.14159;
        var dynamicType = "inferred";
        System.out.println("Age: " + age + ", Name: " + name);
        System.out.println("PI (final): " + PI + ", Dynamic: " + dynamicType);
    }

    static void operatorsDemo() {
        System.out.println("\n--- Operators ---");
        int a = 10, b = 3;
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b));
        System.out.println("a % b = " + (a % b));
        System.out.println("a++ = " + (a++));
        System.out.println("++a = " + (++a));
    }

    static void controlFlowDemo() {
        System.out.println("\n--- Control Flow ---");
        int num = 5;
        if (num > 0) System.out.println("Positive");
        else if (num < 0) System.out.println("Negative");
        else System.out.println("Zero");

        String day = "Monday";
        switch (day) {
            case "Monday": System.out.println("Start of week"); break;
            case "Friday": System.out.println("End of week"); break;
            default: System.out.println("Midweek");
        }

        for (int i = 0; i < 3; i++) System.out.print(i + " ");
        System.out.println();
    }

    static void arraysDemo() {
        System.out.println("\n--- Arrays ---");
        int[] numbers = {1, 2, 3, 4, 5};
        int[] initialized = new int[3];
        initialized[0] = 10;
        System.out.print("Numbers: ");
        for (int n : numbers) System.out.print(n + " ");
        System.out.println();
        System.out.println("Array length: " + numbers.length);
    }

    static void stringsDemo() {
        System.out.println("\n--- Strings ---");
        String str = "Hello Java";
        System.out.println("Length: " + str.length());
        System.out.println("Upper: " + str.toUpperCase());
        System.out.println("Substring: " + str.substring(6));
        System.out.println("Contains 'Java': " + str.contains("Java"));
        System.out.println("Replace: " + str.replace("Java", "World"));
    }

    static void methodsDemo() {
        System.out.println("\n--- Methods ---");
        System.out.println("Sum: " + add(5, 3));
        System.out.println("Factorial: " + factorial(5));
    }

    static int add(int a, int b) { return a + b; }
    static int factorial(int n) { return n <= 1 ? 1 : n * factorial(n - 1); }
}