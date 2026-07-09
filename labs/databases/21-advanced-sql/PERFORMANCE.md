# Performance of Advanced SQL Queries

## Window Function Performance Tips
- Use PARTITION BY on indexed columns when possible
- Filter early with WHERE before window processing
- Avoid ORDER BY on unindexed large columns in OVER clause
- Use parallel query: ALTER SESSION ENABLE PARALLEL DML
- Materialize pre-computed window results

## Recursive CTE Performance
- Always use UNION ALL (not UNION) unless dedup is needed
- Add a LEVEL or depth column to monitor recursion depth
- Use CYCLE clause to prevent infinite recursion
- Keep anchor member very selective
- Consider CONNECT BY for hierarchical queries (often faster)

## PIVOT Performance
- PIVOT performs a single scan; manual CASE may be faster with many pivot columns
- Ensure underlying query is optimized before PIVOT
- Use bitmap indexes on the pivot column if high cardinality

## MERGE Performance
- Index the ON columns in both source and target
- Use direct-path operations: APPEND hint
- For bulk MERGE, use error logging: `LOG ERRORS INTO err_table`
- Avoid MERGE when most rows will NOT MATCHED; use separate INSERT

## Index Selection Guidelines
- B-tree: primary key, unique constraints, foreign keys, high-selectivity queries
- Bitmap: low-cardinality columns, data warehousing, read-mostly
- Function-based: queries using functions on indexed columns
- Domain: specialized indexing (e.g., spatial, text)

## Partitioning Performance
- Range partitioning: time-series data, date-based queries
- List partitioning: categorical data, region-based queries
- Hash partitioning: evenly distribute data, parallel processing
- Composite partitioning: multiple partition keys for complex access patterns

## Query Hints and Optimizer
```sql
SELECT /*+ INDEX(e emp_dept_idx) FULL(d) LEADING(e d) USE_NL(d) PARALLEL(4) */
       e.ename, d.dname FROM employees e, depts d WHERE e.dept_id = d.dept_id;
```

## Optimizer Statistics
```sql
-- Gather statistics
EXEC DBMS_STATS.GATHER_TABLE_STATS('HR', 'EMPLOYEES', CASCADE => TRUE);
-- Set statistics manually
EXEC DBMS_STATS.SET_TABLE_STATS('HR', 'SALES', NUMROWS => 1000000);
-- Copy statistics from production
EXEC DBMS_STATS.COPY_TABLE_STATS('HR', 'SALES', 'DEV', 'SALES');
```

## SQL Plan Management
```sql
-- Load plans from SQL Tuning Set
EXEC DBMS_SPM.LOAD_PLANS_FROM_SQLSET('STS_SALES_Q1');
-- Evolve plans
EXEC DBMS_SPM.EVOLVE_SQL_PLAN_BASELINE('sql_handle');
-- Set plan baseline
EXEC DBMS_SPM.ALTER_SQL_PLAN_BASELINE('sql_handle', 'plan_name', 'ENABLED', 'YES');
```

## Adaptive Execution Plans
- Enable: OPTIMIZER_ADAPTIVE_PLANS=TRUE
- Monitor: V$SQL with IS_ADAPTIVE_PLAN flag
- Review: DBMS_XPLAN with ADAPTIVE format

## SQL Profiles
```sql
-- Create profile
DECLARE
  stmt VARCHAR2(4000);
BEGIN
  stmt := 'SELECT /*+ FULL(e) */ ...';
  DBMS_SQLTUNE.IMPORT_SQL_PROFILE(SQL_TEXT => stmt,
    PROFILE => sqlprof_attr('OPTIMIZER_FEATURES_ENABLE("19.1.0")'));
END;
```

## Benchmarking Tips
1. Run query multiple times (warm caches)
2. Compare EXPLAIN PLAN output before and after
3. Use DBMS_UTILITY.GET_TIME for elapsed timing
4. Monitor with V$SQL and V$SESSION
5. Test with production-like data volumes

## Join Method Selection
- Nested Loops: small driving set, good indexes, good for OLTP
- Hash Join: large result sets, equal joins, good for DW
- Sort Merge: sorted inputs, inequality joins, range queries
- Cartesian Cartesian: rare, for cross-joining with constant filter