# NoSQL Internals — Interview Questions

## Beginner
1. What is the difference between a B-tree and an LSM-tree?
2. What is an SSTable?
3. What is compaction and why is it needed?

## Intermediate
4. Explain write amplification in LSM-trees. How do tiered vs leveled compaction differ?
5. How does MongoDB's WiredTiger storage engine support both B-tree and LSM modes?
6. What is index-free adjacency in graph databases?

## Advanced
7. Describe the bloom filter used in LSM-tree read path (probe multiple SSTables).
8. How does Cassandra's `SizeTieredCompactionStrategy` compare to `LeveledCompactionStrategy`?
9. Explain how accumulating a BSON document in memory differs from JSON and the trade-offs.

## System Design
10. Design a storage engine for a time-series database that ingests 1M writes/second and supports range scans.