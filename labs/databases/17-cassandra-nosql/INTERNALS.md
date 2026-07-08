# Internals: Cassandra NoSQL

## Internal Architecture

### Data Model Internals
Cassandra uses a wide-column store model with partition keys and clustering columns.

**Partition Key:** Determines data placement across nodes (consistent hashing).
**Clustering Key:** Determines sort order within a partition.
**Static Columns:** Shared across all rows in a partition.

### CQL (Cassandra Query Language)
CQL is a SQL-like language for Cassandra with important differences:
- No JOINs or subqueries
- WHERE clauses must include partition key
- ORDER BY is restricted to clustering columns
- ALLOW FILTERING required for non-key column filters

### Data Distribution
Uses consistent hashing with virtual nodes:
- Each node is responsible for a range of token values
- Partitioner (Murmur3Partitioner by default) hashes partition keys
- Replication factor determines how many nodes store each row

### Tunable Consistency
| Level | Coordinator Behavior |
|-------|---------------------|
| ONE | Respond after 1 replica |
| QUORUM | Respond after (RF/2 + 1) replicas |
| ALL | Respond after ALL replicas |
| LOCAL_QUORUM | Quorum within local datacenter |
| EACH_QUORUM | Quorum in EACH datacenter |

### Compaction Strategies
- **SizeTieredCompactionStrategy (STCS):** Default, good for write-heavy
- **LeveledCompactionStrategy (LCS):** Better read performance
- **TimeWindowCompactionStrategy (TWCS):** Optimized for time-series

### Hinted Handoff
When a replica is down, the coordinator stores hints and replays them when the replica recovers.

### Read Repair
On read, if replicas have different data, Cassandra repairs stale replicas in the background.

### Gossip Protocol
Nodes exchange state information every second using a gossip protocol for failure detection and topology discovery.
