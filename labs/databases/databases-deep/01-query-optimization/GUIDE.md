# Query Optimization — Deep Dive Guide

## EXPLAIN Plans

Every SQL statement passes through a parser, rewriter, planner, and executor. The planner produces a **query tree** of plan nodes. Use `EXPLAIN` to inspect it.

```
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) SELECT ...
```

Key plan nodes:
- **Seq Scan** — full table scan (high cost for large tables)
- **Index Scan** — B-tree traversal (fast for selective predicates)
- **Bitmap Heap Scan** — combines multiple index scans via BitmapOr/BitmapAnd
- **Nested Loop** — O(n*m), best when one side is small
- **Hash Join** — builds hash table on inner relation, O(n+m)
- **Merge Join** — sorts both sides, O(n log n + m log m)

## Cost Parameters

PostgreSQL's planner uses weighted constants:
- `seq_page_cost = 1.0`
- `random_page_cost = 4.0`
- `cpu_tuple_cost = 0.01`
- `cpu_index_tuple_cost = 0.005`
- `cpu_operator_cost = 0.0025`

Total cost = (page reads × cost) + (CPU tuples × cost).

## Join Algorithms

| Algorithm    | Use Case                          | Complexity  |
|-------------|-----------------------------------|-------------|
| Nested Loop | Small inner + indexed lookup      | O(n × log m)|
| Hash Join   | No index, large unsorted data     | O(n + m)    |
| Merge Join  | Both sides sorted (e.g. ORDER BY) | O(n + m)    |

## Cardinality Estimation

The planner relies on `pg_class.reltuples` and `pg_statistics.histogram_bounds`. Stale statistics → bad plans.