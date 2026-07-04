# Math Foundation: PostgreSQL

## B-Tree Complexity
- **Search**: O(log_B N) where B = branching factor (300-400 per page)
- **Insert**: O(log_B N) with potential page splits
- **Delete**: O(log_B N) with potential page merges
- **Space**: O(N) for N keys

## Cost Estimation in Query Optimizer
- **Seq Page Cost**: `cpu_tuple_cost × tuples + seq_page_cost × pages`
- **Index Scan Cost**: `cpu_index_tuple_cost × index_tuples + random_page_cost × index_pages`
- **Selectivity**: Fraction of rows estimated to pass a filter
  - Equality: `1 / ndistinct(column)`
  - Range: `(high - low) / (max - min)`
- **Join estimation**:
  - Nested Loop: O(N × M)
  - Hash Join: O(N + M) with hash table build
  - Merge Join: O(N log N + M log M) with sorting

## Autovacuum Threshold
```
vacuum_threshold = vac_base_ins + vac_scale_factor × reltuples
```

## Buffer Cache Hit Ratio
```
hit_ratio = buffer_hits / (buffer_hits + buffer_misses)
```

## WAL Size Calculation
```
max_wal_size = wal_segment_size × checkpoint_completion_target × wal_buffers
```

## MVCC Tuple Overhead
- Each tuple: ~28 bytes header + actual data
- Dead tuples accumulate until VACUUM; storage = live + dead tuples

## Full-Text Search Ranking (TF-IDF variant)
```
ts_rank(vector, query) = Σ (log(N / df_t)) × (1 / (1 + dl))
```
Where N = total documents, df_t = docs containing term t, dl = document length
