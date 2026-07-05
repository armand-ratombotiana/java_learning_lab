package com.learning.lab01;

/**
 * Demonstrates variable declaration, initialization, and basic data types.
 */
public class VariablesExample {

    public static void showVariables() {
        System.out.println("=== Variables and Data Types ===");

        // Primitive types
        byte b = 127;
        short s = 32767;
        int i = 2_147_483_647;
        long l = 9_223_372_036_854_775_807L;
        float f = 3.14f;
        double d = 3.141592653589793;
        char c = 'A';
        boolean flag = true;

        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double: " + d);
        System.out.println("char: " + c);
        System.out.println("boolean: " + flag);

        // Type inference with var
        var message = "Hello, Java 21!";
        var number = 42;
        System.out.println("var message: " + message);
        System.out.println("var number: " + number);
    }
}
