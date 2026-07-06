package com.javaacademy.lab44.jit;

/**
 * Demonstrates JVM intrinsic methods: System.arraycopy, Math functions,
 * and array length. These are recognized by the JIT and replaced with
 * hand-optimized machine code.
 */
public class IntrinsicExample {

    private static final int SIZE = 10_000;

    public static void main(String[] args) {
        System.out.println("=== JVM Intrinsics Demo ===\n");

        int[] src = new int[SIZE];
        int[] dst = new int[SIZE];
        for (int i = 0; i < SIZE; i++) src[i] = i;

        // System.arraycopy is an intrinsic
        long start = System.nanoTime();
        for (int i = 0; i < 100_000; i++) {
            System.arraycopy(src, 0, dst, 0, SIZE);
        }
        long end = System.nanoTime();
        System.out.println("System.arraycopy (intrinsic): " + (end - start) / 1_000_000 + " ms");
        System.out.println("  Last element: " + dst[SIZE - 1]);

        // Math intrinsics: min, max, abs, sin, cos, sqrt
        double d = 0.0;
        long mathStart = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            d += Math.sqrt(i) + Math.abs(-i) + Math.min(i, 1000);
        }
        long mathEnd = System.nanoTime();
        System.out.println("\nMath intrinsic operations: " + (mathEnd - mathStart) / 1_000_000 + " ms");
        System.out.println("  Result: " + d);

        // Array length intrinsic
        long lenStart = System.nanoTime();
        int len = 0;
        for (int i = 0; i < 1_000_000; i++) {
            len = src.length;  // intrinsic: arraylength bytecode
        }
        long lenEnd = System.nanoTime();
        System.out.println("\nArray length intrinsic: " + (lenEnd - lenStart) / 1_000_000 + " ms");
    }
}
