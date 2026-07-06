package com.javaacademy.lab46.jvm;

import java.util.*;

/**
 * Repeated strings with measurement of memory impact.
 * Demonstrates -XX:+UseStringDeduplication (G1) and String.intern().
 */
public class StringDedupDemo {

    private static final List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== String Deduplication Demo ===\n");

        Runtime rt = Runtime.getRuntime();

        // Create many duplicate strings
        long memBefore = rt.totalMemory() - rt.freeMemory();
        for (int i = 0; i < 100_000; i++) {
            // Create distinct String objects with identical content
            strings.add(new String("This is a repeated string value that appears many times"));
        }
        long memAfter = rt.totalMemory() - rt.freeMemory();
        long perString = (memAfter - memBefore) / strings.size();

        System.out.println("Memory before: " + memBefore / 1024 + " KB");
        System.out.println("Memory after:  " + memAfter / 1024 + " KB");
        System.out.println("Per unique string object: ~" + perString + " bytes");

        // With String.intern() (or -XX:+UseStringDeduplication)
        strings.clear();
        memBefore = rt.totalMemory() - rt.freeMemory();
        for (int i = 0; i < 100_000; i++) {
            strings.add(new String("Deduplicated repeated string").intern());
        }
        memAfter = rt.totalMemory() - rt.freeMemory();
        System.out.println("\nWith intern():");
        System.out.println("Memory before: " + memBefore / 1024 + " KB");
        System.out.println("Memory after:  " + memAfter / 1024 + " KB");

        System.out.println("\nJVM flags for string dedup:");
        System.out.println("  -XX:+UseStringDeduplication (G1 only)");
        System.out.println("  -XX:StringDeduplicationAgeThreshold=3");
    }
}
