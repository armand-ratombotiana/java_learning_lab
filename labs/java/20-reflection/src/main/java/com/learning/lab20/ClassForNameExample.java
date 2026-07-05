package com.learning.lab20;

import java.lang.reflect.*;

/**
 * Demonstrates Class.forName(), getMethods(), and inspecting class metadata.
 */
public class ClassForNameExample {

    public static void showReflectionBasics() throws Exception {
        System.out.println("=== Class.forName() & getMethods() ===");

        Class<?> clazz = Class.forName("com.learning.lab20.SampleClass");
        System.out.println("Class name: " + clazz.getName());
        System.out.println("Simple name: " + clazz.getSimpleName());
        System.out.println("Package: " + clazz.getPackageName());

        System.out.println("\nMethods:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("  " + Modifier.toString(method.getModifiers()) 
                + " " + method.getReturnType().getSimpleName() 
                + " " + method.getName()
                + "(" + java.util.Arrays.toString(method.getParameters()) + ")");
        }

        System.out.println("\nConstructors:");
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            System.out.println("  " + ctor);
        }

        System.out.println("\nFields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("  " + Modifier.toString(field.getModifiers()) 
                + " " + field.getType().getSimpleName() 
                + " " + field.getName());
        }
    }
}

class SampleClass {
    private String name;
    public int count;

    public SampleClass() {}

    public SampleClass(String name) { this.name = name; }

    public String greet(String greeting) {
        return greeting + ", " + name + "!";
    }

    private void hiddenMethod() {
        System.out.println("This is private");
    }
}
