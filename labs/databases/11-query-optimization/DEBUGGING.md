# Debugging: Query Optimization

## Tools

| Tool | Purpose |
|---|---|
| `pg_stat_statements` | Identify slow queries |
| `EXPLAIN (ANALYZE, BUFFERS)` | Detailed execution plan |
| `auto_explain` | Log slow queries with plans |
| `pgBadger` | Log analysis reports |
| `pg_stat_user_indexes` | Index usage statistics |

## Reading EXPLAIN Output

```
QUERY PLAN
────────────────────────────────────────────────────
Sort (cost=100.00..200.00 rows=1000)              ← High sort cost
  Sort Key: created_at DESC
  -> Seq Scan on orders (cost=0.00..50.00 rows=1000) ← Full scan
     Filter: (status = 'PENDING'::text)
     Rows Removed by Filter: 90000
```

Red flags:
- `Seq Scan` on large table (>10000 rows)
- Large `rows` estimates with small actual (stale stats)
- `Sort Method: external merge` (disk sort, insufficient work_mem)
- `Nested Loop` with large inner scan

## Query Profiling
```sql
-- Enable timing
\timing on

-- See actual vs estimated
EXPLAIN (ANALYZE, BUFFERS) SELECT ...;

-- Track index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes
WHERE idx_scan = 0;  -- unused indexes
```
