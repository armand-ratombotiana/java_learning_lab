package com.dataeng.thirteen;

import java.util.*;

public class DeltaOptimizer {
    private final long targetFileSize; // bytes

    public DeltaOptimizer(long targetFileSize) { this.targetFileSize = targetFileSize; }

    public int calculateRequiredFiles(long totalDataSize) {
        return (int) Math.max(1, Math.ceil((double) totalDataSize / targetFileSize));
    }

    public double estimateCompactionRatio(int currentFiles, long totalDataSize) {
        int optimalFiles = calculateRequiredFiles(totalDataSize);
        return (double) currentFiles / Math.max(1, optimalFiles);
    }

    public String[] recommendZOrderColumns(Map<String, Integer> columnCardinality, Set<String> frequentFilterColumns) {
        return frequentFilterColumns.stream()
            .filter(col -> columnCardinality.getOrDefault(col, 0) > 1000)
            .sorted((a, b) -> columnCardinality.getOrDefault(b, 0) - columnCardinality.getOrDefault(a, 0))
            .limit(3)
            .toArray(String[]::new);
    }

    public int calculateOptimalPartitions(long totalSize, long avgRecordSize) {
        long totalRecords = totalSize / Math.max(1, avgRecordSize);
        int partitions = (int) (totalRecords / 100_000);
        return Math.max(1, Math.min(partitions, 1024));
    }

    public String generateOptimizePlan(String tablePath, long totalSize, int fileCount) {
        double ratio = estimateCompactionRatio(fileCount, totalSize);
        if (ratio < 1.5) return "No optimization needed (ratio: " + String.format("%.1f", ratio) + ")";
        return "Optimize " + tablePath + ": " + fileCount + " files -> "
            + calculateRequiredFiles(totalSize) + " files (ratio: " + String.format("%.1f", ratio) + "x)";
    }
}
