# How SQL Works

## Query Processing Pipeline

```
SQL String
    │
    ▼
┌─────────────┐
│   Parser    │  Tokenize and build AST
└─────────────┘
    │
    ▼
┌─────────────┐
│  Rewriter   │  Expand views, resolve names, rewrite queries
└─────────────┘
    │
    ▼
┌─────────────┐
│   Planner   │  Generate possible execution plans
└─────────────┘
    │
    ▼
┌─────────────┐
│  Optimizer  │  Cost-based: estimate cost for each plan, pick cheapest
└─────────────┘
    │
    ▼
┌─────────────┐
│  Executor   │  Execute plan, read pages, apply filters
└─────────────┘
    │
    ▼
   Result Rows
```

## Join Algorithms

| Algorithm | Use Case | Complexity |
|---|---|---|
| Nested Loop | Small table joining small table | O(N × M) |
| Hash Join | Large table joining large table (equality) | O(N + M) |
| Merge Join | Both tables sorted on join key | O(N + M) |
| Sort-Merge | Joining unsorted large tables | O(N log N + M log M) |

## How WHERE Clause Works
1. For each row in FROM/JOIN result:
2. Evaluate WHERE condition
3. If true → include row; if false/NULL → skip

## How GROUP BY Works
1. Partition result set into groups by grouping columns
2. For each group, compute aggregate functions
3. Apply HAVING filter on groups

## How Window Functions Work
1. Define partition (PARTITION BY) and ordering (ORDER BY)
2. For each row, compute function over its window frame
3. Default frame: `RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW`

## JDBC Query Execution Flow
```
Connection.createStatement()
    → Statement.executeQuery(sql)
        → Driver sends SQL to DB server
            → DB parses, plans, optimizes, executes
        → Driver receives ResultSet
    → ResultSet.next() fetches rows (may be lazy/streaming)
```
