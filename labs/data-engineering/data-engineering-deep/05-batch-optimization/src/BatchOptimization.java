package com.dataengineering.deep.lab05;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchOptimization {

    public static class SkewDetector {
        private final double skewThreshold;

        public SkewDetector(double skewThreshold) { this.skewThreshold = skewThreshold; }

        public List<String> detectSkewedKeys(List<Row> data, Function<Row, String> keyExtractor) {
            long total = data.size();
            var keyCounts = data.stream().collect(Collectors.groupingBy(keyExtractor, Collectors.counting()));
            return keyCounts.entrySet().stream()
                .filter(e -> (double) e.getValue() / total > skewThreshold)
                .map(Map.Entry::getKey)
                .toList();
        }
    }

    public record Row(String key, long value) {}

    public static class SaltedJoiner {
        private final int numSalts;

        public SaltedJoiner(int numSalts) { this.numSalts = numSalts; }

        public List<Row> saltForJoin(Row row, boolean isSkewed) {
            if (!isSkewed) return List.of(row);
            List<Row> salted = new ArrayList<>();
            for (int i = 0; i < numSalts; i++) {
                salted.add(new Row(row.key() + "_" + i, row.value()));
            }
            return salted;
        }

        public List<Row> replicateDimension(Row dimRow, Set<String> skewedKeys) {
            if (!skewedKeys.contains(dimRow.key())) return List.of(dimRow);
            List<Row> replicated = new ArrayList<>();
            for (int i = 0; i < numSalts; i++) {
                replicated.add(new Row(dimRow.key() + "_" + i, dimRow.value()));
            }
            return replicated;
        }

        public List<Row> saltedJoin(List<Row> fact, List<Row> dim, Set<String> skewedKeys) {
            var dimMap = dim.stream()
                .flatMap(r -> replicateDimension(r, skewedKeys).stream())
                .collect(Collectors.groupingBy(Row::key, Collectors.summingLong(Row::value)));

            return fact.stream()
                .map(f -> {
                    String saltKey = skewedKeys.contains(f.key())
                        ? f.key() + "_" + (int) (Math.random() * numSalts)
                        : f.key();
                    Long dimValue = dimMap.getOrDefault(saltKey, 0L);
                    return new Row(f.key(), f.value() * dimValue);
                })
                .toList();
        }
    }

    public static class MemoryTuner {
        private final long executorMemoryMB;
        private final int executorCores;
        private final int numExecutors;
        private final double memoryFraction;

        public MemoryTuner(long executorMemoryMB, int executorCores, int numExecutors, double memoryFraction) {
            this.executorMemoryMB = executorMemoryMB;
            this.executorCores = executorCores;
            this.numExecutors = numExecutors;
            this.memoryFraction = memoryFraction;
        }

        public long executionPoolSize() {
            return (long) (executorMemoryMB * memoryFraction * 0.5);
        }

        public long storagePoolSize() {
            return (long) (executorMemoryMB * memoryFraction * 0.5);
        }

        public long reservedMemory() {
            return 300; // MB, Spark reserved overhead
        }

        public long usableMemory() {
            return executorMemoryMB - reservedMemory();
        }

        public long totalClusterMemory() {
            return usableMemory() * numExecutors;
        }

        public int recommendedPartitionsForShuffle(long dataSizeMB) {
            int targetPerPartition = 128; // MB
            return (int) Math.max(200, Math.ceil((double) dataSizeMB / targetPerPartition));
        }

        @Override
        public String toString() {
            return "MemoryTuner{executors=" + numExecutors + ", memory=" + executorMemoryMB
                + "MB/core=" + executorCores + ", execPool=" + executionPoolSize()
                + "MB, storagePool=" + storagePoolSize() + "MB, totalCluster=" + totalClusterMemory() + "MB}";
        }
    }

    public static void main(String[] args) {
        var fact = new ArrayList<Row>();
        for (int i = 0; i < 1000; i++) fact.add(new Row(i < 600 ? "skew_key" : "key_" + i, i));
        var dim = new ArrayList<Row>();
        for (int i = 0; i < 100; i++) dim.add(new Row("key_" + i, 10L));
        dim.add(new Row("skew_key", 100L));

        var detector = new SkewDetector(0.05);
        var skewed = detector.detectSkewedKeys(fact, Row::key);
        System.out.println("Skewed keys: " + skewed);

        var joiner = new SaltedJoiner(10);
        var result = joiner.saltedJoin(fact, dim, new HashSet<>(skewed));
        System.out.println("Joined result count: " + result.size() + ", total: " + result.stream().mapToLong(Row::value).sum());

        var tuner = new MemoryTuner(4096, 4, 20, 0.6);
        System.out.println(tuner);
        System.out.println("Recommended partitions for 500GB: " + tuner.recommendedPartitionsForShuffle(500 * 1024));
    }
}
