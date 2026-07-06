package com.javaacademy.lab47.profiling;

import java.util.ArrayList;
import java.util.List;

/**
 * Allocation-heavy workload that creates many short-lived objects.
 * Useful for demonstrating allocation profiling with async-profiler
 * and heap analysis with MAT/Eclipse Memory Analyzer.
 */
public class AllocationProfilingTarget {

    private static final int ALLOC_COUNT = 1_000_000;

    public static void main(String[] args) {
        List<byte[]> holder = new ArrayList<>();
        for (int i = 0; i < ALLOC_COUNT; i++) {
            byte[] chunk = new byte[64];
            chunk[0] = (byte) i;
            if (i % 100_000 == 0) {
                holder.add(chunk);
            }
        }
        System.out.println("Allocated " + holder.size() + " retained chunks");
    }
}
