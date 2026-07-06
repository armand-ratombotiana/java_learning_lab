package com.javaacademy.lab46.jvm;

import java.util.*;

/**
 * Allocates varying heap sizes and measures throughput.
 * Demonstrates the impact of -Xms, -Xmx, -Xmn, -XX:NewRatio.
 */
public class HeapSizingDemo {

    private static final List<byte[]> heap = new ArrayList<>();

    public static long allocate(int mbCount) {
        long start = System.nanoTime();
        try {
            for (int i = 0; i < mbCount; i++) {
                heap.add(new byte[1024 * 1024]); // 1 MB
            }
        } catch (OutOfMemoryError e) {
            // Expected when exceeding heap
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        System.out.println("=== Heap Sizing Impact ===\n");

        Runtime rt = Runtime.getRuntime();
        System.out.println("Max heap: " + rt.maxMemory() / 1024 / 1024 + " MB");
        System.out.println("Initial heap: " + rt.totalMemory() / 1024 / 1024 + " MB");
        System.out.println("Free heap: " + rt.freeMemory() / 1024 / 1024 + " MB\n");

        int[] sizes = {10, 50, 100, 200, 500};
        for (int mb : sizes) {
            heap.clear();
            double pct = (double) mb / (rt.maxMemory() / 1024 / 1024) * 100;
            long elapsed = allocate(mb);
            System.out.printf("Allocate %3d MB: %6d ns (%.1f%% of max heap)%n", mb, elapsed, pct);
        }

        System.out.println("\nJVM flags to experiment with:");
        System.out.println("  -Xms256m -Xmx2g -Xmn512m -XX:NewRatio=3");
        heap.clear();
    }
}
