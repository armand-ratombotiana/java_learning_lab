package com.java.reflection.annotations.lab01;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionBasicsLab {

    private String secret = "hidden";
    public final String exposed = "visible";

    public ReflectionBasicsLab() {}

    private ReflectionBasicsLab(String secret) {
        this.secret = secret;
    }

    public void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    private String innerGreet(String name) {
        return "Hi, " + name + ". " + secret;
    }

    public static void main(String[] args) throws Exception {
        // 1. Obtain Class object
        Class<?> clazz = ReflectionBasicsLab.class;

        // 2. Print declared methods
        System.out.println("=== Methods ===");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println("  " + m.getName() + " -> " + m.getParameterCount() + " params");
        }

        // 3. Inspect fields
        System.out.println("=== Fields ===");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println("  " + f.getName() + " : " + f.getType().getSimpleName());
        }

        // 4. Use private constructor
        Constructor<?> privateCtor = clazz.getDeclaredConstructor(String.class);
        privateCtor.setAccessible(true);
        Object instance = privateCtor.newInstance("world");

        // 5. Invoke private method
        Method privateMethod = clazz.getDeclaredMethod("innerGreet", String.class);
        privateMethod.setAccessible(true);
        String msg = (String) privateMethod.invoke(instance, "reflection");
        System.out.println("Private method says: " + msg);

        // 6. Read and write private field
        Field secretField = clazz.getDeclaredField("secret");
        secretField.setAccessible(true);
        System.out.println("Before: " + secretField.get(instance));
        secretField.set(instance, "modified");
        System.out.println("After: " + secretField.get(instance));
    }
}
