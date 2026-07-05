package com.learning.lab20;

import java.lang.reflect.*;

/**
 * Demonstrates array reflection — creating and manipulating arrays dynamically.
 */
public class ArrayReflectionExample {

    public static void showArrayReflection() {
        System.out.println("=== Array Reflection ===");

        int[] intArray = (int[]) Array.newInstance(int.class, 5);
        for (int i = 0; i < 5; i++) {
            Array.setInt(intArray, i, i * 10);
        }

        System.out.print("Reflective array: ");
        for (int i = 0; i < Array.getLength(intArray); i++) {
            System.out.print(Array.getInt(intArray, i) + " ");
        }
        System.out.println();

        Object stringArray = Array.newInstance(String.class, 3);
        Array.set(stringArray, 0, "Reflection");
        Array.set(stringArray, 1, "is");
        Array.set(stringArray, 2, "powerful");
        System.out.println("String array element 0: " + Array.get(stringArray, 0));
        System.out.println("Array component type: " + stringArray.getClass().getComponentType());
    }
}
