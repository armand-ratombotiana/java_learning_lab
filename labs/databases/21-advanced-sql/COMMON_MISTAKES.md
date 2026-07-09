# Common Mistakes in Advanced SQL

## Window Function Mistakes
1. Forgetting the OVER clause
2. Using ORDER BY in PARTITION BY context
3. ROWS vs RANGE confusion: ROWS uses row counts, RANGE uses value differences
4. Not specifying frame clause for LAST_VALUE (defaults to current row)
5. Mixing DISTINCT with window functions (DISTINCT evaluates after window)
6. Using RANK when ROW_NUMBER is needed (ties treated differently)

## Recursive CTE Mistakes
1. No termination condition → infinite recursion
2. UNION instead of UNION ALL (duplicate elimination is expensive)
3. Anchor member too complex (keep base case simple)
4. Recursive member references multiple CTEs
5. Column types must match exactly between anchor and recursive members

## PIVOT Mistakes
1. Missing pivot value in IN clause → column omitted
2. Incorrect aggregate (SUM vs COUNT vs AVG)
3. Multiple rows for same group → add more GROUP BY columns
4. PIVOT on columns with NULLs → nulls may be excluded

## MERGE Mistakes
1. ORA-30926: duplicate rows in source matching same target row
2. No WHEN NOT MATCHED clause when source has new rows
3. Wrong join condition leading to full updates
4. Not using error logging for bulk MERGE

## CONNECT BY Mistakes
1. Cycles: use NOCYCLE or CYCLE clause
2. Invalid PRIOR: must reference the other side of the parent-child relationship
3. Order of siblings: use ORDER SIBLINGS BY, not regular ORDER BY
4. CONNECT BY and JOIN mixing can cause confusion

## MODEL Clause Mistakes
1. Missing measures initialization
2. Circular rule references
3. Symbolic vs positional reference confusion
4. Forgetting AUTOMATIC ORDER or SEQUENTIAL ORDER
5. UPSERT vs UPDATE semantics

## Partitioning Mistakes
1. Wrong partition key (low cardinality for range)
2. No partition pruning on query predicates
3. Too many partitions (overhead) or too few (no pruning benefit)
4. Global indexes on partitioned tables (rebuilt after partition maintenance)
5. Not using partition exchange load (PEL) for data loading

## Index Mistakes
1. Over-indexing (DML performance degradation)
2. Ignoring composite indexes for queries with multiple predicates
3. Bitmap indexes on OLTP tables (row locking issues)
4. Function-based indexes without matching function in query
5. Not rebuilding indexes after bulk deletes

## SPM Mistakes
1. Fixed plans not evolving (stale plans with better execution plans available)
2. Not loading plans after optimizer upgrade
3. Enabling SPM without capture (no baselines created)
4. Pinning suboptimal plans

## Optimizer Mistakes
1. Stale statistics: tables analyzed months ago
2. Missing histograms on skewed columns
3. Not gathering system statistics
4. Relying on OPTIMIZER_DYNAMIC_SAMPLING instead of proper stats

## General Mistakes
1. Using SELECT * in production
2. Cartesian joins missing join conditions
3. Not using bind variables for frequently executed queries
4. Nested subqueries where CTEs perform better
5. Ignoring the execution plan