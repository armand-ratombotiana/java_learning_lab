package com.javaacademy.lab45.gc;

import java.lang.management.*;
import java.util.*;

/**
 * Programmatic GC logging using ManagementFactory.
 * Reports GC statistics via GarbageCollectorMXBean.
 */
public class GcLoggingExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Programmatic GC Logging ===\n");

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("Active GC Collectors:");
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + " (collections: " + bean.getCollectionCount()
                + ", time: " + bean.getCollectionTime() + " ms)");
        }

        // Allocate memory to trigger GC
        System.out.println("\nAllocating memory to trigger GC...");
        List<byte[]> alloc = new ArrayList<>();
        long totalAllocated = 0;
        for (int i = 0; i < 10_000; i++) {
            alloc.add(new byte[1024 * 100]); // 100 KB
            totalAllocated += 1024 * 100;
            if (i % 500 == 0) {
                System.out.println("  Allocated " + (totalAllocated / 1024 / 1024) + " MB");
                Thread.sleep(5);
            }
        }

        // Final stats
        System.out.println("\nFinal GC Statistics:");
        long totalCollections = 0;
        long totalGcTime = 0;
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + ": " + bean.getCollectionCount()
                + " collections, " + bean.getCollectionTime() + " ms");
            totalCollections += bean.getCollectionCount();
            totalGcTime += bean.getCollectionTime();
        }
        System.out.println("Total: " + totalCollections + " collections, " + totalGcTime + " ms");

        alloc.clear();
        System.gc();
        System.out.println("\nRun with -Xlog:gc* to see detailed GC logging");
    }
}
