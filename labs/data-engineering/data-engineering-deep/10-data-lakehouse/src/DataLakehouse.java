package com.dataengineering.deep.lab10;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class DataLakehouse {

    public static class TransactionLog {
        private final List<TransactionEntry> entries = new CopyOnWriteArrayList<>();
        private final AtomicLong version = new AtomicLong(0);

        public record TransactionEntry(long version, String action, Map<String, String> fileChanges, Instant timestamp) {}

        public long commit(String action, Map<String, String> fileChanges) {
            long v = version.incrementAndGet();
            entries.add(new TransactionEntry(v, action, fileChanges, Instant.now()));
            return v;
        }

        public TransactionEntry getEntry(long version) {
            return entries.stream().filter(e -> e.version() == version).findFirst().orElse(null);
        }

        public long getCurrentVersion() { return version.get(); }

        public Snapshot snapshot(long version) {
            Map<String, String> state = new HashMap<>();
            for (var entry : entries) {
                if (entry.version() > version) break;
                var changes = new HashMap<>(entry.fileChanges());
                changes.entrySet().removeIf(e -> e.getValue() == null);
                state.putAll(changes);
                for (var e : entry.fileChanges().entrySet()) {
                    if (e.getValue() == null) state.remove(e.getKey());
                }
            }
            return new Snapshot(version, state);
        }
    }

    public record Snapshot(long version, Map<String, String> files) {
        public String readFile(String path) { return files.get(path); }
    }

    public static class VersionedTable {
        private final TransactionLog log = new TransactionLog();
        private final Map<Long, Snapshot> cache = new ConcurrentHashMap<>();

        public long write(Map<String, String> files) { return log.commit("WRITE", files); }

        public long delete(List<String> paths) {
            Map<String, String> deletes = new HashMap<>();
            for (String p : paths) deletes.put(p, null);
            return log.commit("DELETE", deletes);
        }

        public Snapshot read(long version) {
            return cache.computeIfAbsent(version, v -> log.snapshot(v));
        }

        public Snapshot latest() { return read(log.getCurrentVersion()); }
    }

    public record SnapshotDiff(List<String> added, List<String> removed, List<String> modified) {}

    public static class SnapshotDiffer {
        public SnapshotDiff diff(Snapshot oldSnap, Snapshot newSnap) {
            List<String> added = new ArrayList<>();
            List<String> removed = new ArrayList<>();
            List<String> modified = new ArrayList<>();
            for (var entry : newSnap.files().entrySet()) {
                if (!oldSnap.files().containsKey(entry.getKey())) added.add(entry.getKey());
                else if (!oldSnap.files().get(entry.getKey()).equals(entry.getValue())) modified.add(entry.getKey());
            }
            for (var key : oldSnap.files().keySet()) {
                if (!newSnap.files().containsKey(key)) removed.add(key);
            }
            return new SnapshotDiff(added, removed, modified);
        }
    }

    public static class PartitionEvolutionManager {
        private final List<PartitionSpec> history = new ArrayList<>();

        public record PartitionSpec(String column, String transform, int version) {}

        public void evolve(String column, String transform) {
            history.add(new PartitionSpec(column, transform, history.size() + 1));
        }

        public PartitionSpec getSpec(long version) {
            return history.stream().filter(s -> s.version() == version).findFirst().orElse(null);
        }

        public List<String> partitionPaths(Map<String, String> data) {
            if (history.isEmpty()) return List.of("unknown");
            var spec = history.get(history.size() - 1);
            return switch (spec.transform()) {
                case "identity" -> List.of(spec.column() + "=" + data.get(spec.column()));
                case "month" -> List.of(spec.column() + "=" + data.get(spec.column()).substring(0, 7));
                case "hash" -> List.of(spec.column() + "_bucket_" + Math.abs(data.get(spec.column()).hashCode() % 16));
                default -> List.of("unknown");
            };
        }
    }

    public static class HudiMergeOnRead {
        private final Map<String, List<String>> baseFiles = new ConcurrentHashMap<>();
        private final Map<String, List<String>> logFiles = new ConcurrentHashMap<>();

        public void upsert(String recordKey, String record) {
            int bucket = Math.abs(recordKey.hashCode() % 10);
            String fileName = "bucket_" + bucket;
            logFiles.computeIfAbsent(fileName, k -> new ArrayList<>()).add(record);
            if (logFiles.get(fileName).size() >= 100) compact(fileName);
        }

        private void compact(String fileName) {
            var records = logFiles.get(fileName);
            if (records != null) {
                baseFiles.merge(fileName, records, (old, _new) -> {
                    var merged = new ArrayList<>(old);
                    merged.addAll(_new);
                    return merged;
                });
                logFiles.put(fileName, new ArrayList<>());
            }
        }

        public Map<String, Long> stats() {
            return Map.of(
                "baseFiles", (long) baseFiles.size(),
                "logFiles", logFiles.values().stream().mapToLong(List::size).sum()
            );
        }
    }

    public static void main(String[] args) {
        var table = new VersionedTable();
        long v1 = table.write(Map.of("file1.parquet", "data1", "file2.parquet", "data2"));
        long v2 = table.write(Map.of("file3.parquet", "data3"));
        long v3 = table.delete(List.of("file1.parquet"));

        System.out.println("Latest snapshot: " + table.latest());
        System.out.println("Time travel to v1: " + table.read(v1));
        System.out.println("Time travel to v2: " + table.read(v2));

        var differ = new SnapshotDiffer();
        System.out.println("Diff v1 -> v3: " + differ.diff(table.read(v1), table.read(v3)));

        var partitionEvolver = new PartitionEvolutionManager();
        partitionEvolver.evolve("event_date", "month");
        var paths = partitionEvolver.partitionPaths(Map.of("event_date", "2025-07-28"));
        System.out.println("Partition paths: " + paths);

        var hudi = new HudiMergeOnRead();
        for (int i = 0; i < 250; i++) hudi.upsert("key_" + i, "record_" + i);
        System.out.println("Hudi stats: " + hudi.stats());
    }
}
