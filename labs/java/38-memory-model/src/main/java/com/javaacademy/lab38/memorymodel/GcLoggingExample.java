package com.javaacademy.lab38.memorymodel;

import java.io.*;
import java.util.*;

public class GcLoggingExample {

    private static final List<byte[]> garbage = new ArrayList<>();

    public void generateGarbage(int megabytes) {
        for (int i = 0; i < megabytes; i++) {
            garbage.add(new byte[1024 * 1024]);
        }
    }

    public void clearGarbage() {
        garbage.clear();
    }

    public void simulateGcLoad(int cycles) {
        for (int cycle = 0; cycle < cycles; cycle++) {
            int allocSize = 5 + new Random().nextInt(10);
            generateGarbage(allocSize);
            clearGarbage();
            System.gc();
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class GcStats {
        private final long totalAllocated;
        private final long collections;
        private final long gcTime;

        public GcStats(long totalAllocated, long collections, long gcTime) {
            this.totalAllocated = totalAllocated;
            this.collections = collections;
            this.gcTime = gcTime;
        }

        public long getTotalAllocated() { return totalAllocated; }
        public long getCollections() { return collections; }
        public long getGcTime() { return gcTime; }

        public double getAvgGcTime() {
            return collections == 0 ? 0 : (double) gcTime / collections;
        }
    }

    public GcStats analyzeGcLog(String logFile) throws IOException {
        long totalAllocated = 0;
        long collections = 0;
        long gcTime = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("GC pause")) collections++;
                if (line.contains("Allocated")) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("Allocated") && i + 1 < parts.length) {
                            totalAllocated += Long.parseLong(parts[i + 1].replaceAll("[^0-9]", ""));
                        }
                    }
                }
            }
        }
        return new GcStats(totalAllocated, collections, gcTime);
    }
}
