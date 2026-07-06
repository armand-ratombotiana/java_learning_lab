package com.javaacademy.lab52.performanceantipatterns;

/**
 * Demonstrates boxing overhead by comparing boxed Integer vs
 * primitive int in a compute-intensive loop. Autoboxing creates
 * unnecessary allocations, increasing GC pressure and reducing throughput.
 */
public class BoxingPerformance {

    private static final int ITERATIONS = 50_000_000;

    public static void main(String[] args) {
        System.out.println("=== Boxing vs Primitive Performance ===");

        long t1 = System.nanoTime();
        long boxedResult = boxedSum(ITERATIONS);
        long boxedTime = System.nanoTime() - t1;

        long t2 = System.nanoTime();
        long primitiveResult = primitiveSum(ITERATIONS);
        long primitiveTime = System.nanoTime() - t2;

        System.out.println("Boxed sum: " + boxedResult + " in " + boxedTime / 1_000_000 + " ms");
        System.out.println("Primitive sum: " + primitiveResult + " in " + primitiveTime / 1_000_000 + " ms");
        System.out.println("Boxing overhead: " + (double) boxedTime / primitiveTime + "x");
    }

    static long boxedSum(int n) {
        Long sum = 0L;
        for (Integer i = 0; i < n; i++) {
            sum += i; // autoboxing and unboxing on each iteration
        }
        return sum;
    }

    static long primitiveSum(int n) {
        long sum = 0L;
        for (int i = 0; i < n; i++) {
            sum += i;
        }
        return sum;
    }
}
