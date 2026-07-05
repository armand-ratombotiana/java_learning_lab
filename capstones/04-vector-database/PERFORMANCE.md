# Performance: Vector Database

## Throughput Targets
- Single-vector insert: < 1ms (without WAL), < 5ms (with WAL fsync)
- ANN search (top-10): < 10ms P99 for 1M vectors (768 dim)
- Batch insert 10k vectors: < 1s
- Range metadata filter: < 1ms on 100k keys

## Bottlenecks
- **Distance computation**: Most expensive operation (O(d) per comparison). Use SIMD/Vector API (Project Panama).
- **Random memory access**: HNSW graph traversal is pointer-chasing; cache misses dominate. Optimize memory layout.
- **WAL fsync**: Disk write on every insert. Batch commits with configurable durability.
- **Lock contention**: ReadWriteLock on layers limits concurrent writes. Shard by partition.

## Optimization Strategies
- Use Java Vector API (Incubator) for batch distance computation
- Pack neighbor lists in flat arrays (not ArrayList of objects)
- Asynchronous WAL with batching (commit every 100ms or 1000 ops)
- Partition collection into segments, each with independent HNSW graph
- Pre-filter metadata before ANN search for selective queries
