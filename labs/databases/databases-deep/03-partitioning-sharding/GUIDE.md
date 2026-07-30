# Partitioning & Sharding — Deep Dive Guide

## Vertical Partitioning

Split a wide table into "hot" (frequently accessed) and "cold" (large blobs) columns. Each partition shares the same primary key.

## Horizontal Partitioning

Divide rows into disjoint subsets. PostgreSQL 10+ supports declarative partitioning:

```sql
CREATE TABLE logs (id BIGSERIAL, level TEXT, msg TEXT, ts TIMESTAMPTZ)
PARTITION BY RANGE (ts);

CREATE TABLE logs_2024 PARTITION OF logs FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
```

- **Range**: by time, ID ranges
- **List**: by region, status
- **Hash**: by hash of a key (2, 4, 8 partitions)

## Sharding

Partitioning across independent database servers. Each shard is a separate DB.

- **Key-based**: hash(shard_key) % N — simple but rebalancing moves all data
- **Directory-based**: lookup table maps key → shard — flexible but adds hop
- **Geographic**: shard by region for low-latency

## Consistent Hashing

Each node gets a position on a hash ring (0..2³²-1). Keys are assigned to the next clockwise node.

**Virtual nodes**: each physical node maps to multiple ring positions for better distribution.

**Rebalancing**: when a node joins/leaves, only keys adjacent to it move — O(K/N) instead of O(K).