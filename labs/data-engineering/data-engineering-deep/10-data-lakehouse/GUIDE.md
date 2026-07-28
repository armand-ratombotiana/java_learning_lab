# Lab 10: Data Lakehouse — Implementation Guide

## Step 1: Transaction Log (Delta-Style)

```java
public class TransactionLog {
    private final List<TransactionEntry> entries = new CopyOnWriteArrayList<>();
    private final AtomicLong version = new AtomicLong(0);

    public record TransactionEntry(long version, String action, Map<String, String> fileChanges, Instant timestamp) {}

    public long commit(String action, Map<String, String> fileChanges) {
        long v = version.incrementAndGet();
        entries.add(new TransactionEntry(v, action, fileChanges, Instant.now()));
        return v;
    }

    public TransactionEntry getVersion(long version) {
        return entries.stream().filter(e -> e.version() == version).findFirst().orElse(null);
    }

    public Snapshot snapshot(long version) {
        Map<String, String> state = new HashMap<>();
        for (var entry : entries) {
            if (entry.version() > version) break;
            state.putAll(entry.fileChanges());
        }
        return new Snapshot(version, state);
    }
}
```

## Step 2: Snapshot / Time Travel

```java
public record Snapshot(long version, Map<String, String> files) {
    public String readFile(String path) { return files.get(path); }
}
```

## Step 3: Merge Operation (Delta-Style)

```java
public class DeltaMerge {
    private final TransactionLog log;

    public void merge(String tablePath, List<String> sourceData, String mergeKey, String condition) {
        Map<String, String> changes = new HashMap<>();
        // Read current snapshot
        var current = log.snapshot(log.getCurrentVersion());
        // For each source record, match by key and UPSERT
        for (String record : sourceData) {
            String key = extractKey(record, mergeKey);
            if (current.files().containsValue(key)) {
                // UPDATE
                changes.put(key, record);
            } else {
                // INSERT
                String newFile = tablePath + "/" + UUID.randomUUID() + ".parquet";
                changes.put(newFile, record);
            }
        }
        log.commit("MERGE", changes);
    }
}
```

## Step 4: Iceberg-Style Partition Evolution

```java
public class PartitionEvolutionManager {
    private final List<PartitionSpec> history = new ArrayList<>();

    public record PartitionSpec(String column, String transform, int version) {}

    public void evolve(String column, String transform) {
        history.add(new PartitionSpec(column, transform, history.size() + 1));
    }

    public PartitionSpec getSpec(long version) {
        return history.stream().filter(s -> s.version() == version).findFirst().orElse(null);
    }

    public List<String> partitionPaths(Map<String, String> data) {
        var spec = history.get(history.size() - 1);
        return switch (spec.transform()) {
            case "identity" -> List.of(spec.column() + "=" + data.get(spec.column()));
            case "month" -> List.of(spec.column() + "=" + data.get(spec.column()).substring(0, 7));
            case "hash" -> List.of(spec.column() + "_bucket_" + Math.abs(data.get(spec.column()).hashCode() % 16));
            default -> List.of("unknown");
        };
    }
}
```

## Step 5: Hudi Merge-on-Read

```java
public class HudiMergeOnRead {
    // Base file (columnar) + Log files (row-based)
    private final Map<String, List<String>> baseFiles = new ConcurrentHashMap<>();
    private final Map<String, List<String>> logFiles = new ConcurrentHashMap<>();

    public void upsert(String recordKey, String record) {
        int bucket = Math.abs(recordKey.hashCode() % 10);
        String fileName = "bucket_" + bucket;
        logFiles.computeIfAbsent(fileName, k -> new ArrayList<>()).add(record);
        if (logFiles.get(fileName).size() >= 100) {
            compact(fileName);
        }
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
}
```
