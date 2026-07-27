# Partitioning & Sharding - Interview Preparation

> Key interview questions about data partitioning strategies.

---

## Core Interview Questions

### Q1: Compare range-based vs hash-based vs consistent hashing
**Answer**: Range: ordered by key ranges, supports range queries, risk of hot spots. Hash: hash(key) % N, even distribution, no range queries. Consistent hashing: hash ring with virtual nodes, minimal redistribution on node changes, supports key locality.

### Q2: How does consistent hashing minimize rebalancing?
**Answer**: Each node maps to multiple points on ring (virtual nodes). When node added/removed, only adjacent keys on ring move. For N nodes with V virtual nodes each, expected redistribution = 1/(N*V) fraction of keys.

### Q3: How do you choose a shard key?
**Answer**: High cardinality (many distinct values), even distribution (no hot spots), query pattern alignment (common queries colocated on same shard). Bad shard key leads to hot shards, which require re-sharding or splitting.

### Q4: What is a hot partition and how to handle it?
**Answer**: Small key range receives disproportionate traffic. Solutions: split hot partition, add more virtual nodes, cache aggressively, application-level load shedding, re-shard with different key.

### Q5: Explain rebalancing strategies
**Answer**: Fixed number of partitions (DynamoDB, Cassandra): pre-create N partitions, reassign partitions to nodes. Dynamic partitioning (Bigtable): split when partition exceeds threshold. Consistent hashing: redistribute only affected keys.

## Company-Specific Focus

| Company | Partitioning Focus |
|---------|-------------------|
| Amazon | "DynamoDB partition key design - why choice matters" |
| Google | "Bigtable's tablet splitting" |
| Meta | "TAO's graph partitioning by object ID" |
| Uber | "H3 geospatial partitioning for ride dispatch" |

## LeetCode Connections

| Problem | # | Partitioning Concept |
|---------|---|--------------------|
| Design HashMap | 706 | Bucket partitioning |
| Design HashSet | 705 | Hash partitioning |
| Range Module | 715 | Range partitioning |
| Encode and Decode TinyURL | 535 | ID-based partitioning |

## System Design Connections

- **Design a Distributed Database**: Choose partitioning strategy
- **Design a Chat Application**: Partition by user_id or conversation_id
- **Design a Rate Limiter**: Partition by user_id
- **Design a Search Engine**: Partition inverted index

> **Key Insight**: Partitioning is the most critical design decision for distributed databases. Interviewers will probe your shard key choice heavily.