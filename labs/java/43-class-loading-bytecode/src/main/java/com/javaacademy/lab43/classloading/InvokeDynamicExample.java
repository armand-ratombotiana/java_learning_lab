package com.javaacademy.lab43.classloading;

import java.lang.invoke.*;
import java.util.function.Function;

/**
 * Demonstrates invokedynamic via lambda metafactory.
 * Shows how lambda expressions are compiled to invokedynamic
 * with bootstrap methods in the constant pool.
 */
public class InvokeDynamicExample {

    public static void main(String[] args) throws Throwable {
        System.out.println("=== invokedynamic Demo ===\n");

        // Lambda - compiled to invokedynamic
        Function<String, String> upper = String::toUpperCase;
        System.out.println("Lambda result: " + upper.apply("hello"));

        // Manual invokedynamic via MethodHandles.Lookup
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType mt = MethodType.methodType(String.class);
        MethodHandle toUpper = lookup.findVirtual(String.class, "toUpperCase", mt);

        CallSite site = LambdaMetafactory.metafactory(
            lookup,
            "apply",
            MethodType.methodType(Function.class),
            MethodType.methodType(Object.class, Object.class),
            toUpper,
            MethodType.methodType(String.class, String.class)
        );

        @SuppressWarnings("unchecked")
        Function<String, String> fn = (Function<String, String>) site.getTarget().invokeExact();
        System.out.println("MetaFactory result: " + fn.apply("world"));

        // Show invokedynamic usage via MethodHandle
        MethodHandle invokeDynamic = site.getTarget();
        System.out.println("Bootstrap method: " + site.getClass().getName());
    }
}
