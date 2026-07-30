# 03 - Partitioning & Sharding

## Topics Covered
- Horizontal partitioning (range, list, hash)
- Vertical partitioning (columnar splitting)
- Sharding strategies (key-based, directory-based, geographic)
- Consistent hashing (hash ring, virtual nodes)
- Rebalancing trade-offs

## Goal
Understand when to partition within a database and when to shard across databases.

## Exercises

1. Create a range-partitioned table by date and compare query performance.
2. Implement a list-partitioned table for regional data.
3. Implement a consistent hashing ring in Java with virtual nodes.
4. Compare rebalancing cost between hash partitioning and consistent hashing.