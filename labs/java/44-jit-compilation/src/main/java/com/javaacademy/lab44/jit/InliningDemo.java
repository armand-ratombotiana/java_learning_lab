package com.javaacademy.lab44.jit;

/**
 * Demonstrates method inlining with small methods.
 * The JIT inlines methods below -XX:MaxInlineSize (default 35 bytes).
 */
public class InliningDemo {

    private static volatile int result;

    // Small method likely to be inlined
    static int add(int a, int b) {
        return a + b;
    }

    // Medium method
    static int compute(int x) {
        int r = 0;
        for (int i = 0; i < x; i++) {
            r += add(i, i);  // This call site is a candidate for inlining
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println("=== Inlining Demo ===\n");
        System.out.println("Measuring performance of small method calls\n");

        long start = System.nanoTime();
        for (int i = 0; i < 50_000; i++) {
            result = compute(100);
        }
        long end = System.nanoTime();

        System.out.println("Result: " + result);
        System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
        System.out.println("\nRun with -XX:+PrintInlining to see inlining decisions:");
        System.out.println("  java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining " + InliningDemo.class.getName());
    }
}
