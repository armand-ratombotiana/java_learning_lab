# Internals: CockroachDB

## Internal Architecture

### Distributed SQL Engine
CockroachDB implements a distributed SQL layer on top of a key-value store (RocksDB/Pebble).

**Architecture Layers:**
1. SQL Layer: Parsing, optimization, execution
2. Transaction Layer: MVCC, concurrency control
3. Distribution Layer: Range splitting, replication
4. Replication Layer: Raft consensus
5. Storage Layer: KV engine (Pebble)

### Geo-Partitioning
Data can be partitioned by geographic location:
```sql
ALTER TABLE orders PARTITION BY LIST (region) (
    PARTITION us_east VALUES IN ('us-east-1'),
    PARTITION eu_west VALUES IN ('eu-west-1')
);
ALTER PARTITION us_east CONFIGURE ZONE USING
    constraints = '{"+region=us-east-1": 1}',
    num_replicas = 3;
```

### Range Splitting
Ranges are automatically split when they exceed 512MB:
1. Range reaches threshold
2. Split point determined
3. New range created with separate Raft group
4. Load balanced across nodes

### Online Schema Changes
Schema changes use a multi-phase approach:
1. DELETE ONLY: New schema elements visible but not writable
2. WRITE ONLY: New elements accepting writes
3. BACKFILL: Existing data migrated
4. PUBLIC: Schema fully active

### Raft Consensus
- Leader lease: 3-6 seconds
- Quorum: majority of replicas
- Log replication: entries replicated to followers
- Leader election: triggered on failure detection

### Survivability Goals
- **Survive 1 node failure:** RF=3 ensures quorum with 2 remaining nodes
- **Survive rack failure:** Spread replicas across racks
- **Survive datacenter failure:** Geo-partitioning with regional replicas
