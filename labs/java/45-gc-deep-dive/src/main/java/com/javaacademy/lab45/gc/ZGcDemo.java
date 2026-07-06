package com.javaacademy.lab45.gc;

import java.util.*;

/**
 * Allocation pattern that works well with ZGC.
 * ZGC uses colored pointers and load barriers.
 * This demo allocates many short-lived objects (typical for ZGC).
 */
public class ZGcDemo {

    private static final List<int[]> survivors = new ArrayList<>();

    static class ShortLived {
        final long id;
        final byte[] data;

        ShortLived(long id, int size) {
            this.id = id;
            this.data = new byte[size];
        }
    }

    public static void main(String[] args) {
        System.out.println("=== ZGC Allocation Pattern ===\n");
        System.out.println("ZGC excels with many short-lived objects\n");

        long start = System.nanoTime();

        for (int cycle = 0; cycle < 20; cycle++) {
            // Allocate many short-lived objects (die in young gen)
            List<ShortLived> temp = new ArrayList<>();
            for (int i = 0; i < 50_000; i++) {
                temp.add(new ShortLived(i * cycle, 256));
            }

            // Keep 5% as survivors
            for (int i = 0; i < temp.size(); i += 20) {
                survivors.add(new int[512]);
            }

            System.out.println("  Cycle " + cycle + ": allocated " + temp.size()
                + " objects, survivors: " + survivors.size());
        }

        long end = System.nanoTime();
        System.out.println("\nTotal time: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Total survivors: " + survivors.size());
        System.out.println("\nRun with -XX:+UseZGC -Xlog:gc* to see ZGC behavior");
    }
}
