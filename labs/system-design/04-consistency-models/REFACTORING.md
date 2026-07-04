# Consistency Models - REFACTORING

## From Eventual to Strong Consistency

### Before: Eventual consistency (Cassandra)
```java
// WRONG: Write may not be visible on immediate read
session.execute("INSERT INTO orders (id, status) VALUES (?, ?)", id, "NEW");
ResultSet rs = session.execute("SELECT * FROM orders WHERE id = ?", id);
// rs may be empty! → inconsistency bug
```

### After: Strong consistency with quorum
```java
// RIGHT: Use strong consistency level
Statement insert = new SimpleStatement(
    "INSERT INTO orders (id, status) VALUES (?, ?)", id, "NEW");
insert.setConsistencyLevel(ConsistencyLevel.QUORUM);
session.execute(insert);

Statement select = new SimpleStatement(
    "SELECT * FROM orders WHERE id = ?", id);
select.setConsistencyLevel(ConsistencyLevel.QUORUM);
// Now guaranteed to see the write
```

## Adding Read-After-Write Guarantee

### Before: No session tracking
```java
public String getOrder(String id) {
    return replicaDao.findById(id);  // potentially stale
}
```

### After: Session-aware reads
```java
public String getOrder(String id, String sessionId) {
    SessionMeta meta = sessionCache.get(sessionId);
    if (meta != null && meta.wroteOrder(id)) {
        return masterDao.findById(id);  // consistent read
    }
    return replicaDao.findById(id);    // eventually consistent
}
```

## From Single Master to Multi-Master with CRDTs

### Before: Single master, synchronous replication
```java
// Writes go to one master only
// Failover required if master goes down
```

### After: CRDT-based multi-master
```java
// Both nodes accept writes simultaneously
// CRDT (G-Counter example):
public class GCounter {
    private Map<String, Integer> counts = new HashMap<>();

    public void increment(String node) {
        counts.merge(node, 1, Integer::sum);
    }

    public int get() {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void merge(GCounter other) {
        other.counts.forEach((k, v) ->
            counts.merge(k, v, Integer::max));
    }
}
```

## Migrating to Raft Consensus

### Strategy
1. Start with single leader (current master) as initial Raft leader
2. Add Raft log alongside existing replication
3. Gradually move clients to Raft-based reads/writes
4. Remove old replication protocol

### Compatibility Layer
```java
public class RaftCompatibleStore implements DataStore {
    private final RaftClient raftClient;
    private final LegacyStore legacy;  // during migration

    public void write(String key, String value) {
        raftClient.submit(new PutCommand(key, value));
        legacy.write(key, value);  // dual write during migration
    }
}
```
