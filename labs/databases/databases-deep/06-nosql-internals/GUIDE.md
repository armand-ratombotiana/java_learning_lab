# NoSQL Internals — Deep Dive Guide

## LSM-Tree (Log-Structured Merge-Tree)

Used by: RocksDB, LevelDB, Cassandra, Scylla, HBase (HFile)

**Components:**
1. **MemTable** — in-memory sorted structure (skiplist / red-black tree)
2. **WAL** — write-ahead log for durability
3. **SSTable** — immutable on-disk sorted file
4. **Compaction** — merges SSTables, removes tombstones, reduces read amplification

**Amplification factors:**
- Write amplification (WA): how many times data is rewritten during compaction
- Read amplification (RA): how many SSTables must be probed per read
- Space amplification (SA): ratio of logical to physical data size

## B-Tree

Used by: MySQL InnoDB, MongoDB WiredTiger (also LSM mode), PostgreSQL

- Pages read/written in fixed blocks (4KB–16KB)
- In-place updates → less WA, more RA for random writes
- Good for range scans; worse for high-write workloads vs LSM

## Document Stores: BSON

MongoDB stores documents as BSON (Binary JSON). Internal structure:
- Document header (length)
- Element list (type + name + value)
- Null terminator

## Graph Stores: Index-Free Adjacency

Neo4j stores each node with direct pointers to its relationships. Traversals follow pointers (O(degree)) — no index lookup per hop.