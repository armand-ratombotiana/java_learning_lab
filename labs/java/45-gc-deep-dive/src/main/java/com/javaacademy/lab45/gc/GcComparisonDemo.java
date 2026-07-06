package com.javaacademy.lab45.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Runs an allocation workload and reports GC collection counts and times
 * for whatever collector is currently active.
 */
public class GcComparisonDemo {

    private static final List<byte[]> heap = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== GC Comparison Demo ===\n");

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("Active GC Collectors:");
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + " (collections: " + bean.getCollectionCount()
                + ", time: " + bean.getCollectionTime() + " ms)");
        }

        System.out.println("\nAllocating memory...");
        long start = System.currentTimeMillis();
        try {
            for (int i = 0; i < 100_000; i++) {
                heap.add(new byte[1024]); // 1 KB each
                if (heap.size() % 10_000 == 0) {
                    System.out.println("  Allocated " + heap.size() + " KB");
                    Thread.sleep(10);
                }
            }
        } catch (OutOfMemoryError | InterruptedException e) {
            System.out.println("  Allocation stopped: " + e.getMessage());
        }

        long end = System.currentTimeMillis();
        System.out.println("\nGC Statistics after allocation (" + (end - start) + " ms):");
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + ": " + bean.getCollectionCount()
                + " collections, " + bean.getCollectionTime() + " ms total");
        }

        heap.clear();
        System.gc();
    }
}
