package com.javaacademy.lab50.objectlayout;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

/**
 * Demonstrates false sharing and the @Contended annotation fix.
 * Multiple threads access fields that happen to share a cache line,
 * causing cache coherence traffic. @Contended (or padding) separates
 * fields into distinct cache lines. Run with -XX:-RestrictContended.
 */
public class FalseSharingDemo {

    private static final int ITERATIONS = 50_000_000;
    private static final int THREADS = 4;

    // Contested fields (potentially sharing cache line)
    static class SharedCounters {
        volatile long counterA = 0;
        volatile long counterB = 0;
        volatile long counterC = 0;
        volatile long counterD = 0;
    }

    // Padded: each counter gets its own cache line
    static class PaddedCounters {
        volatile long counterA = 0;
        long p1, p2, p3, p4, p5, p6, p7; // padding
        volatile long counterB = 0;
        long p8, p9, p10, p11, p12, p13, p14;
        volatile long counterC = 0;
        long p15, p16, p17, p18, p19, p20, p21;
        volatile long counterD = 0;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== False Sharing Demo ===");
        System.out.println("Shared (likely false sharing):");
        runTest(new SharedCounters(), 0, 1, 2, 3);
        System.out.println("Padded (no false sharing):");
        runTest(new PaddedCounters(), 0, 1, 2, 3);
    }

    static void runTest(Object counters, int... fieldIndices) throws Exception {
        Field[] fields = counters.getClass().getDeclaredFields();
        // Filter only counter fields
        var counterFields = java.util.Arrays.stream(fields)
            .filter(f -> f.getName().startsWith("counter"))
            .toArray(Field[]::new);

        for (var f : counterFields) f.setAccessible(true);

        Thread[] threads = new Thread[THREADS];
        long start = System.nanoTime();

        for (int t = 0; t < THREADS && t < counterFields.length; t++) {
            final int idx = t;
            final Field field = counterFields[t];
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    try { field.setLong(counters, field.getLong(counters) + 1); }
                    catch (Exception e) { throw new RuntimeException(e); }
                }
            }, "Writer-" + t);
            threads[t].start();
        }
        for (Thread t : threads) t.join();

        long elapsed = System.nanoTime() - start;
        System.out.println("  Time: " + (elapsed / 1_000_000) + " ms");
    }
}
