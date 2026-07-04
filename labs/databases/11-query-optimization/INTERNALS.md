# Internals: Query Optimization

## PostgreSQL Optimizer

| Component | Role |
|---|---|
| `planner.c` | Top-level planner (total/query planning) |
| `allpaths.c` | Generates alternative paths for a relation |
| `geqo.c` | Genetic query optimizer (large joins) |
| `costsize.c` | Estimates path cost |
| `indxpath.c` | Generates index scan paths |
| `joinpath.c` | Generates join methods |
| `pathnode.c` | Path node construction |

## Join Methods

| Method | Algorithm | Best For |
|---|---|---|
| Nested Loop | O(N × M) | Small inner relation, good index |
| Hash Join | O(N + M) | Medium relations, equi-joins |
| Merge Join | O(N log N + M log M) | Sorted inputs, large relations |

## Sort Methods
- **In-memory**: Uses `work_mem` (default 4MB)
- **Disk-based**: External merge sort when data > `work_mem`
- **Incremental**: Top-N heapsort (for LIMIT + ORDER BY)

## Statistics Collection
```sql
-- Manual analyze
ANALYZE table_name;

-- Configure sampling
ALTER TABLE table_name SET STATISTICS 1000;
-- Higher = more accurate, slower (default 100)
```
