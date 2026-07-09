# Math Foundation for Advanced SQL

## Window Function Mathematics
- Partition cardinality: number of rows per partition
- Frame cardinality: number of rows in the current window frame
- Ranking: ROW_NUMBER assigns integer sequence 1, 2, 3... within partition
- DENSE_RANK formula: DENSE_RANK = 1 + (number of distinct ORDER BY values before current)
- RANK formula: RANK = 1 + (number of rows with strictly greater ORDER BY value)
- NTILE distribution: remainder r = rows MOD n, first r buckets get CEIL(rows/n) rows
- LAG/LEAD: LAG(v, n, d): offset n rows back, default d if beyond partition

## Set Operations and Recursion
Recursive CTEs solve: R0 = anchor; Ri = F(R_{i-1}); result = Union(R0, R1, ..., Rk) where k = smallest such that Rk = empty.

The transitive closure of relation R: R^+ = Union over n>=1 of R^n, where R^1 = R, R^n = R ∘ R^{n-1}.

## PIVOT Algebraic Formulation
Given relation R(K, J, V) where K is key columns, J is the pivot column, V is value:
PIVOT_agg(R) = GroupBy(K, {(J=j1) → agg(V), (J=j2) → agg(V), ...})

## Cost Model for Query Optimization
Cost = (blocks_read * io_cost_per_block) + (rows_processed * cpu_cost_per_row)
Oracle cost model weighs I/O and CPU based on system statistics.

## Join Selectivity
selectivity = (NDV_join_column_t1 * NDV_join_column_t2) / max(NDV_join_column_t1, NDV_join_column_t2)
For equality join: selectivity = 1 / MAX(NDV(t1.col), NDV(t2.col))

## Cardinality Estimation
- Table: rows = R
- Filter: card ≈ R × selectivity(predicate)
- Filter with histogram: card = sum of frequencies over matching buckets
- Join: card ≈ R1 × R2 × selectivity(join condition)
- GROUP BY: card ≈ NDV(grouping columns) or R × 0.1 if unknown

## Index Cost Mathematics
B-tree height: h ≈ 1 + log_b(leaf_blocks) where b is the branching factor
Index range scan cost = h + CEIL(number of leaf blocks in range)
Table access by rowid cost = h + number of rows (deduplicated)

## Parallel Query Acceleration
Speedup = P / (1 - (1 - 1/P) × S) where P = parallel degree, S = serial portion
Amdahl's law: parallel processing limited by the serial fraction of the query.

## Partition Cost
Full partition scan = (blocks per partition) × io_cost
Partition-wise join = both tables partitioned on join key, joining corresponding partitions

## Histogram Mathematics
- Frequency histogram: for bucket i, frequency = count of distinct values in bucket
- Height-balanced histogram: each bucket has ~1/num_buckets of rows total
- Hybrid histogram: 1000-2048 buckets, exact endpoints, epc (endpoint count) for cardinality
- Top-N histogram: tracks top-N values, all others in one "others" bucket

## Statistical Sampling
Simple random sampling: margin of error = z × sqrt(p × (1-p) / n)
For DBMS_STATS ESTIMATE_PERCENT: larger sample = more accurate but slower.

## Merge Operation
MERGE cardinality estimate = SOURCE rows affected:
- MATCHED rows = source join cardinality with matching target
- NOT MATCHED rows = source cardinality NOT IN (distinct join values in target)

## Join Cost Formulas
- Nested Loop Cost = C_outside + C_inside × (number of iterations)
- Hash Join Cost = (build_input_blocks × 2) + probe_input_blocks × join_cardinality
- Sort Merge Join Cost = 2 × (outer_blocks + inner_blocks) × log(blocks) for sorting + scan cost

## Memory for Hash Joins
Work area = total_blocks × row_size × hash_ratio. If work area > PGA_AGGREGATE_LIMIT, spills to temp. Use PGA_AGGREGATE_TARGET to control memory allocation.