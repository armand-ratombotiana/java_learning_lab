package com.javaacademy.lab45.gc;

import java.util.*;

/**
 * Demonstrates G1 region-based allocation pattern.
 * Allocates varied-sized objects across the heap to show
 * how G1 divides the heap into 1MB regions.
 */
public class G1GcDemo {

    private static final List<Object> roots = new ArrayList<>();
    private static final Random rnd = new Random(42);

    static class RegionData {
        byte[] payload;
        RegionData next;

        RegionData(int size) {
            this.payload = new byte[size];
        }
    }

    public static void main(String[] args) {
        System.out.println("=== G1 GC Region-Based Allocation ===\n");

        System.out.println("Allocating varied-size objects (simulating G1 regions)...");
        long total = 0;

        try {
            for (int i = 0; i < 50_000; i++) {
                int size = 128 + rnd.nextInt(4096); // 128B to 4KB
                RegionData data = new RegionData(size);
                // 10% chance to keep as root
                if (rnd.nextInt(10) == 0) {
                    roots.add(data);
                } else {
                    data.next = roots.isEmpty() ? null : (RegionData) roots.get(rnd.nextInt(Math.max(1, roots.size())));
                }
                total += size;

                if (i % 10_000 == 0 && i > 0) {
                    System.out.println("  Allocated " + i + " objects (~" + (total / 1024 / 1024) + " MB)");
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("  Heap full: " + e.getMessage());
        }

        System.out.println("Roots retained: " + roots.size());
        System.out.println("\nRun with -XX:+PrintGCDetails -XX:+UseG1GC to see region details");
        roots.clear();
        System.gc();
    }
}
