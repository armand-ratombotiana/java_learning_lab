# Math Foundation: MongoDB

## B-Tree Complexity
- Search: O(log_B N)
- Insert: O(log_B N) with page splits
- Delete: O(log_B N) with page merges
- Space: O(N)

## Aggregation Pipeline Stages
- **$match**: Early filtering reduces downstream processing
- **$sort**: O(N log N) in memory or uses index
- **$group**: O(N) with hash-based accumulation
- **$lookup**: O(N × M) without indexes, O(N log M) with indexes
- **$facet**: Parallel pipeline execution, up to 100MB RAM per stage

## Query Selectivity
- Selective query: returns < 5% of documents (use index)
- Non-selective: returns > 5% (collection scan may be faster)

## Replication Latency
- Typical: 10-50ms between primary and nearby secondary
- Cross-region: 50-500ms depending on distance
- `w: majority` waits for acknowledgment from majority of voting members

## Shard Key Distribution
- Ideal: high cardinality, low frequency, monotonic change resistant
- Uniform distribution: each shard receives N/shard_count writes
- Jitter/hashed shard keys: prevents hot shards for monotonic keys

## Storage Overhead
- BSON overhead: 4-16 bytes per field (type + name + value)
- Document padding: 0-1.5× actual data size
- Index overhead: ~30-50% of data size
- Oplog: 5% of disk by default

## Java Driver Pool Sizing
```
maxPoolSize = max_connections / application_threads
```
Default: 100 connections per MongoClient instance
