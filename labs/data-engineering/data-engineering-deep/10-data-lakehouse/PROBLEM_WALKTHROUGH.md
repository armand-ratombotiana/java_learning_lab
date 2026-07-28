# Lab 10: Problem Walkthrough — Lakehouse Time Travel Engine

## Problem

Build a `TimeTravelEngine` for a lakehouse that supports versioned reads, snapshot diffing, and rollback to previous versions — without needing a separate database.

## Walkthrough

### Step 1: Versioned Table

```java
public class VersionedTable {
    private final TransactionLog log = new TransactionLog();
    private final Map<Long, Snapshot> cache = new ConcurrentHashMap<>();

    public long write(Map<String, String> files) {
        return log.commit("WRITE", files);
    }

    public long delete(List<String> paths) {
        Map<String, String> deletes = new HashMap<>();
        for (String p : paths) deletes.put(p, null);
        return log.commit("DELETE", deletes);
    }

    public Snapshot read(long version) {
        return cache.computeIfAbsent(version, v -> log.snapshot(v));
    }

    public Snapshot latest() {
        return log.snapshot(log.getCurrentVersion());
    }
}
```

### Step 2: Snapshot Diff

```java
public record SnapshotDiff(List<String> added, List<String> removed, List<String> modified) {}

public class SnapshotDiffer {
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
```

### Step 3: Rollback

```java
public class RollbackManager {
    private final VersionedTable table;
    private final TransactionLog log;

    public long rollback(long targetVersion) {
        var targetSnapshot = table.read(targetVersion);
        var currentSnapshot = table.latest();
        return log.commit("ROLLBACK", targetSnapshot.files());
    }
}
```

### Step 4: Time Travel Query

```java
public class TimeTravelQuery {
    private final VersionedTable table;

    public String query(String path, long version) {
        var snapshot = table.read(version);
        return snapshot.readFile(path);
    }

    public List<String> listFiles(long version) {
        return List.copyOf(table.read(version).files().keySet());
    }
}
```

## Complexity

- **Time**: O(F) for snapshot build (F = files in version)
- **Space**: O(V * F) in worst case if all snapshots cached
