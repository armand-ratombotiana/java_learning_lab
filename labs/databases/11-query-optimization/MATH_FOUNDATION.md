# Math Foundation: Query Optimization

## B-Tree Complexity
- Search: O(log_b N)
- Insert: O(log_b N)
- Delete: O(log_b N)
- Range scan: O(log_b N + K) where K = matching rows

b = branching factor (typically 100-500 for standard databases)

## Page vs Row Access
```
Random I/O cost per row       = random_page_cost (default 4)
Sequential I/O cost per page  = seq_page_cost (default 1)

If table is 10,000 pages:
  Full scan = 10,000 × 1 = 10,000 cost units
  Index scan fetching 100 rows scattered across 100 pages:
    100 × 4 = 400 cost units (index) + 100 × 4 = 400 cost units (heap)
    = 800 total
```

## Selectivity Estimation
```
Selectivity = estimated_rows / total_rows

High selectivity (< 5%):  Index scan preferred
Low selectivity  (> 10%): Seq scan preferred
```

## N+1 Problem Cost
```
Total queries = 1 + N
Total time   = parent_query_time + N × child_query_time

If parent query = 1ms, child query = 5ms, N = 100:
  = 1 + 100 × 5 = 501ms (vs 1-2ms with JOIN)
```
