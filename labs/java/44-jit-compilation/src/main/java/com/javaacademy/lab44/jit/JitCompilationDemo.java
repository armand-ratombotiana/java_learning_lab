package com.javaacademy.lab44.jit;

/**
 * Hot method that triggers C2 tiered compilation.
 * Exercises a tight loop to exceed the compilation threshold
 * and trigger the JIT compiler.
 */
public class JitCompilationDemo {

    private static volatile double sink;

    public static double compute(int iterations) {
        double sum = 0.0;
        for (int i = 0; i < iterations; i++) {
            sum += Math.sin(i) * Math.cos(i);
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("=== JIT Compilation Trigger Demo ===\n");
        System.out.println("Compilation threshold: ~10,000 invocations for C1, ~15,000 for C2");

        long totalTime = 0;
        for (int warmup = 0; warmup < 20_000; warmup++) {
            long start = System.nanoTime();
            sink = compute(1000);
            long elapsed = System.nanoTime() - start;
            if (warmup > 15_000) totalTime += elapsed;
        }

        System.out.println("Average time after C2: " + (totalTime / 5_000) + " ns");
        System.out.println("\nRun with -XX:+PrintCompilation to see compilation events:");
        System.out.println("  java -XX:+PrintCompilation " + JitCompilationDemo.class.getName());
    }
}
