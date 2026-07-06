package com.javaacademy.lab46.jvm;

import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Generates many methods to fill the code cache.
 * Uses dynamic proxies to create new methods at runtime.
 */
public class CodeCacheDemo {

    interface Task {
        int compute(int x);
    }

    private static final List<Task> tasks = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println("=== Code Cache Pressure ===\n");

        // Generate many unique method implementations via proxy
        for (int i = 0; i < 1000; i++) {
            int multiplier = i;
            Task task = (Task) Proxy.newProxyInstance(
                CodeCacheDemo.class.getClassLoader(),
                new Class<?>[]{Task.class},
                (proxy, method, params) -> {
                    if (method.getName().equals("compute")) {
                        return (int) params[0] * multiplier;
                    }
                    return 0;
                }
            );
            tasks.add(task);
        }

        System.out.println("Generated " + tasks.size() + " unique method implementations");

        // Exercise all methods to trigger JIT compilation
        long start = System.nanoTime();
        for (int iter = 0; iter < 10_000; iter++) {
            for (Task t : tasks) {
                t.compute(iter);
            }
        }
        long end = System.nanoTime();

        System.out.println("Exercised all methods: " + (end - start) / 1_000_000 + " ms");
        System.out.println("\nRun with -XX:+PrintCodeCache to see code cache usage:");
        System.out.println("  -XX:ReservedCodeCacheSize=256m -XX:+PrintCodeCache");
    }
}
