package com.learning.lab30;

/**
 * Demonstrates Runtime memory management and garbage collection hints.
 */
public class MemoryUsageExample {

    public static void showMemoryUsage() {
        System.out.println("=== Runtime Memory Usage ===");

        Runtime runtime = Runtime.getRuntime();

        System.out.println("Max memory: " + formatBytes(runtime.maxMemory()));
        System.out.println("Total memory: " + formatBytes(runtime.totalMemory()));
        System.out.println("Free memory: " + formatBytes(runtime.freeMemory()));
        System.out.println("Used memory: " 
            + formatBytes(runtime.totalMemory() - runtime.freeMemory()));

        System.out.println("Available processors: " + runtime.availableProcessors());

        System.out.print("Running GC...");
        runtime.gc();
        System.out.println(" done");

        System.out.println("Free memory after GC: " + formatBytes(runtime.freeMemory()));
    }

    static String formatBytes(long bytes) {
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
