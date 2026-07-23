# LEETCODE_SOLUTIONS — CockroachDB

## Distributed SQL Solutions

| LeetCode Problem | CockroachDB Feature | Benefit |
|-----------------|--------------------|---------|
| All SQL problems | PostgreSQL-compatible SQL | Same queries as PostgreSQL |
| 175 Combine Tables | Distributed JOIN | Works across nodes |
| 184 Dept Highest | Global secondary index | Fast aggregation |
| 185 Top 3 Salaries | Window functions | DENSE_RANK works distributed |
| 197 Rising Temperature | Date functions | Same as PostgreSQL |

### CockroachDB-Specific Considerations

| Feature | LeetCode Relevance |
|---------|-------------------|
| Serial Isolation | LeetCode 585 investments — strong consistency |
| Geo-partitioning | Locality-optimized queries |
| Automatic rebalancing | Even data distribution for JOINs |
| Multi-region | Low-latency queries worldwide |

### Migration Pattern
```sql
-- CockroachDB supports PostgreSQL syntax for all LeetCode problems
-- Example: LeetCode 178 Rank Scores
SELECT score,
       DENSE_RANK() OVER (ORDER BY score DESC) AS rank
FROM scores;
```
