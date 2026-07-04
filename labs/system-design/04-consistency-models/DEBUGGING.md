# Consistency Models - DEBUGGING

## Detecting Inconsistencies

### Jepsen-Style Verification
```java
// Track operation history
public class HistoryTracker {
    private List<Operation> history = new CopyOnWriteArrayList<>();

    public void record(Operation op) {
        history.add(op);
    }

    public boolean verifyLinearizability() {
        // Check that all operations can be
        // arranged in a valid sequential order
        // Matching real-time constraints
        return LinearizabilityChecker.check(history);
    }
}
```

## Monitoring Replication Lag

### PostgreSQL
```sql
-- Check replication lag
SELECT
    pid,
    application_name,
    pg_wal_lsn_diff(pg_current_wal_lsn(), replay_lsn) AS lag_bytes,
    now() - pg_last_xact_replay_timestamp() AS lag_time
FROM pg_stat_replication;
```

### Cassandra
```bash
# Check read repair stats
nodetool cfstats keyspace.column_family
# Look for: ReadRepairRequests, ReadRepairRepairedRows
```

### Kafka
```bash
# Check consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
    --group replicator --describe
# Column: LAG — difference between latest and consumed offset
```

## Common Consistency Bugs

| Symptom | Cause | Fix |
|---------|-------|-----|
| Phantom reads | Read from replica during write | Ensure W+R > N |
| Lost updates | LWW dropping concurrent writes | Use CRDTs or merge operators |
| Stale session data | No session affinity | Use session tokens with read-your-writes |
| Duplicate events | Producer retry without idempotency | Make producers idempotent |
| Non-causal ordering | Events reordered by broker | Use same partition key for related events |

## Testing Consistency

### Tools
- **Jepsen**: Automated consistency verification
- **Porcupine**: Linearizability checker
- **Knossos**: Sequential consistency checker
- **JUnit with jepsen-java**: Integration tests

### Example Test
```java
@Test
public void testReadAfterWriteConsistency() {
    String key = "test-" + UUID.randomUUID();
    store.write(key, "value1");
    String read = store.read(key);
    assertEquals("value1", read);  // should always pass
}
```
