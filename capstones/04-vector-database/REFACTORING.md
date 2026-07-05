# Refactoring: Vector Database

## Current Pain Points
- Single-threaded index construction limits throughput
- WAL and index are tightly coupled
- Metadata filtering is naive (full scan for range queries)
- No support for vector partitioning (multi-tenancy)
- Distance function is virtual call per comparison (slow)

## Suggested Improvements
- Parallel HNSW construction using batch insert optimization
- Separate WAL from index with checkpoint/compaction logic
- Use segment tree or B-tree for range metadata queries
- Add collection/namespace support with separate index instances
- Use hotpath optimizations: inline distance functions, avoid boxing
- Add PQ compression for large-scale deployments
