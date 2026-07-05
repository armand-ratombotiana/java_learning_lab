package com.learning.lab20;

import java.lang.reflect.*;

/**
 * Demonstrates invoking methods and accessing fields via reflection.
 */
public class MethodInvokeExample {

    public static void showInvocation() throws Exception {
        System.out.println("=== Method Invocation & Field Access ===");

        Class<?> clazz = Class.forName("com.learning.lab20.Calculator");
        Object calc = clazz.getDeclaredConstructor().newInstance();

        Method addMethod = clazz.getMethod("add", int.class, int.class);
        Object result = addMethod.invoke(calc, 10, 20);
        System.out.println("add(10, 20) = " + result);

        Method multiplyMethod = clazz.getMethod("multiply", int.class, int.class);
        result = multiplyMethod.invoke(calc, 6, 7);
        System.out.println("multiply(6, 7) = " + result);

        Field multiplierField = clazz.getDeclaredField("multiplier");
        multiplierField.setAccessible(true);
        int originalValue = multiplierField.getInt(calc);
        System.out.println("Original multiplier field: " + originalValue);
        multiplierField.setInt(calc, 5);
        System.out.println("Set multiplier to 5");
        result = multiplyMethod.invoke(calc, 4, 3);
        System.out.println("multiply(4, 3) after field change = " + result);
    }
}

class Calculator {
    private int multiplier = 2;

    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b * multiplier;
    }
}
