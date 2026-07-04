# Internals: SQL Query Execution

## PostgreSQL Query Executor Nodes

| Node Type | Description |
|---|---|
| Seq Scan | Full table scan |
| Index Scan | B-tree index lookup |
| Index Only Scan | All needed data in index |
| Bitmap Heap Scan | Multiple index scans combined |
| Nested Loop | For each outer row, scan inner |
| Hash Join | Build hash table on one side, probe with other |
| Merge Join | Sort both sides, merge |
| Aggregate | GROUP BY processing (hash or sort-based) |
| WindowAgg | Window function evaluation |
| Sort | In-memory or on-disk sorting |

## How PostgreSQL Executes a Query

```
EXPLAIN (ANALYZE, BUFFERS)
SELECT d.name, COUNT(e.id)
FROM departments d
LEFT JOIN employees e ON d.id = e.dept_id
WHERE d.active = true
GROUP BY d.name;

                                 QUERY PLAN
┌──────────────────────────────────────────────────────────┐
│ HashAggregate                                             │
│   Group Key: d.name                                       │
│   → Hash Left Join                                        │
│       Hash Cond: (d.id = e.dept_id)                       │
│       → Seq Scan on departments d                         │
│           Filter: active                                  │
│       → Hash                                              │
│           → Seq Scan on employees e                       │
│ Buffers: shared hit=42                                    │
└──────────────────────────────────────────────────────────┘
```

## Three-Valued Logic (3VL)
SQL uses three-valued logic due to NULL:

| A | B | A AND B | A OR B |
|---|---|---|---|
| TRUE | UNKNOWN | UNKNOWN | TRUE |
| FALSE | UNKNOWN | FALSE | UNKNOWN |
| UNKNOWN | UNKNOWN | UNKNOWN | UNKNOWN |

**Critical**: `WHERE x NOT IN (1, 2, NULL)` is always UNKNOWN (false) for any x!

## JDBC Statement Types

| Type | Prevent Injection? | Precompiled? | Use Case |
|---|---|---|---|
| Statement | No | No | DDL, simple queries |
| PreparedStatement | Yes | Yes (may be cached) | Repeated queries |
| CallableStatement | Yes | Yes | Stored procedures |

## ResultSet Internals
- **Forward-only** (default): Stream rows, no random access
- **Scrollable**: `TYPE_SCROLL_INSENSITIVE` – buffer all rows in memory
- **Holdable**: `HOLD_CURSORS_OVER_COMMIT` – keep cursor after commit
