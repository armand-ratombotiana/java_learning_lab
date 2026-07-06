package com.javaacademy.lab45.gc;

import java.lang.management.*;
import java.util.*;

/**
 * Programmatic GC logging using ManagementFactory.
 * Listens for GC notifications via GarbageCollectorMXBean.
 */
public class GcLoggingExample {

    private static long totalCollections = 0;
    private static long totalGcTime = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== Programmatic GC Logging ===\n");

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (var bean : gcBeans) {
            System.out.println("GC Bean: " + bean.getName());
        }

        // Register GC notification listener
        for (var bean : gcBeans) {
            if (bean instanceof NotificationEmitter emitter) {
                emitter.addNotificationListener((notification, handback) -> {
                    if (notification.getType().equals("com.sun.management.gc.notification")) {
                        long count = ((GarbageCollectorMXBean) handback).getCollectionCount();
                        long time = ((GarbageCollectorMXBean) handback).getCollectionTime();
                        System.out.println("[GC Notification] " + notification.getMessage()
                            + " (collections: " + count + ", time: " + time + " ms)");
                    }
                }, null, bean);
            }
        }

        // Allocate memory to trigger GC
        List<byte[]> alloc = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            alloc.add(new byte[1024 * 100]); // 100 KB
            if (i % 500 == 0) {
                System.out.println("Allocated " + (i * 100) + " KB");
                Thread.sleep(5);
            }
        }

        // Final stats
        System.out.println("\nFinal GC Statistics:");
        for (var bean : gcBeans) {
            System.out.println("  " + bean.getName() + ": " + bean.getCollectionCount()
                + " collections, " + bean.getCollectionTime() + " ms");
            totalCollections += bean.getCollectionCount();
            totalGcTime += bean.getCollectionTime();
        }
        System.out.println("Total: " + totalCollections + " collections, " + totalGcTime + " ms");

        alloc.clear();
        System.gc();
    }
}
