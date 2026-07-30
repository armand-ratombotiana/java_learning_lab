# 06 - NoSQL Internals

## Topics Covered
- Document stores (MongoDB internal BSON, WiredTiger)
- Key-value stores (RocksDB, LevelDB — LSM-tree architecture)
- Wide-column stores (Cassandra, HBase — SSTables, MemTable, Compaction)
- Graph stores (Neo4j — adjacency lists, index-free adjacency)
- Storage engines: LSM-tree vs B-tree
- Write amplification, read amplification, space amplification

## Goal
Understand the internal data structures and trade-offs of different NoSQL storage engines.

## Exercises

1. Implement a minimal LSM-tree with in-memory MemTable and on-disk SSTables.
2. Compare B-tree vs LSM-tree write throughput in simulation.
3. Implement BSON-style document encoding/decoding.
4. Model a graph adjacency list and run BFS in-memory.