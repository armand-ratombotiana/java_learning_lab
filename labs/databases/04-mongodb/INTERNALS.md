# Internals: MongoDB

## BSON Wire Protocol
MongoDB uses its own TCP-based wire protocol for client-server communication. Messages are serialized BSON documents prefixed with a 16-36 byte header containing:
- Message length
- Request ID
- Response to ID
- Opcode (OP_MSG, OP_QUERY, etc.)

## WiredTiger Storage Engine Internals

### B-tree Page Types
- **Internal pages**: Routing nodes with key/pointer pairs
- **Leaf pages**: Store document data or index entries
- **Overflow pages**: Large values (>32KB) stored separately

### MVCC Snapshots
- Each transaction gets a snapshot (set of active transaction IDs)
- Readers see the latest version written by a committed transaction
- Writers create new versions, old versions cleaned by eviction

### Journal
```
Journal Entry: { LSN, Transaction ID, Page modifications }
```
- Journal synced every 100ms or per write with `j: true`
- Checkpoints every 60s or when journal reaches 2GB
- Recovery replays journal from last checkpoint

## Oplog (Replication)
- Capped collection `local.oplog.rs`
- Contains operations: `{ op: "i"|"u"|"d"|"c", ns, o, o2 }`
- Size: 5% of free disk space (minimum 990MB, maximum 50GB)
- Secondaries track position with `optime`

## Sharding Internals
- **Config servers**: Store cluster metadata (shards, databases, chunks)
- **Mongos routers**: Lightweight query routers
- **Balancer**: Migrates chunks evenly across shards
- **Chunk splitting**: 64MB default, splits at 1.3× threshold

## Index Internals
- B-tree with internal/leaf nodes
- Compound indexes: leftmost prefix nesting
- Bloom filters for non-existent index key checks
- Covered queries: all required fields in index

## Change Streams
Uses oplog tailing with resumable tokens:
- `_data` field is a BSON binary token
- Can resume from any point in oplog history
- Full document lookup for update operations
