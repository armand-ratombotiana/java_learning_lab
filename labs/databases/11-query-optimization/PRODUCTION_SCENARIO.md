# Production Scenarios: Query Optimization (Oracle Focus)

## Scenario 1: SQL Plan Regression After Statistics Gather
**Context**: After a scheduled statistics gather, a critical OLTP query started running 100x slower.
**Problem**: The query that normally took 50ms started taking 5 seconds. AWR showed high `buffer gets` (10M vs 100K normally). Execution plan changed from index range scan to full table scan.
**Root Cause**: The statistics gather updated the `HISTOGRAM` for a skewed column. The new histogram caused the CBO to estimate that 30% of rows matched the predicate (instead of 0.1%). The CBO chose a full table scan instead of an index scan based on the overestimated cardinality.
**Solution**: 1) Identified the regression: compared execution plans using `DBMS_XPLAN.DISPLAY_CURSOR`. 2) Fixed the plan using SQL Plan Baseline: captured the good plan and added it as a baseline. 3) Loaded the good plan from AWR: `SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_AWR('sql_id'))`. 4) Created baseline: `DBMS_SPM.LOAD_PLANS_FROM_CURSOR_CACHE(sql_id => 'good_sql_id')`. 5) Fixed the plan: `DBMS_SPM.EVOLVE_SQL_PLAN_BASELINE`. 6) Deleted the problematic histogram and locked statistics to prevent recurrence.
**Lessons Learned**: Use SQL Plan Management to prevent regressions. Lock statistics for tables with stable data. Monitor plan changes with AWR comparison. Review new histograms for skewed columns.

## Scenario 2: Full Table Scan on Partitioned Table
**Context**: A range-partitioned table (by date) query was scanning all partitions instead of pruning.
**Problem**: The query `SELECT * FROM sales WHERE sale_date = '15-JAN-2024'` scanned 12 partitions instead of 1. Query time was 10 seconds instead of 100ms.
**Root Cause**: The `sale_date` column was of type `VARCHAR2(20)` but the partition key was `sale_date DATE`. The predicate used a string literal without explicit conversion. Oracle implicitly converted the partition key to string for comparison, disabling partition pruning.
**Solution**: 1) Changed the predicate to use date literal: `WHERE sale_date = DATE '2024-01-15'`. 2) Or used explicit TO_DATE: `WHERE sale_date = TO_DATE('15-JAN-2024', 'DD-MON-YYYY')`. 3) Checked execution plan for `PARTITION RANGE ALL` vs `PARTITION RANGE SINGLE`. 4) Added partition pruning verification to query review checklist. 5) Used `EXPLAIN PLAN` with `DBMS_XPLAN.DISPLAY` to verify pruning.
**Lessons Learned**: Ensure partition key predicates use the correct data type. Check for implicit type conversion disabling pruning. Validate partition pruning in execution plan review. Use DATE/TO_DATE explicitly for date partition keys.

## Scenario 3: Hash Join Spilling to Disk
**Context**: A nightly reporting query was taking 2 hours instead of 20 minutes.
**Problem**: AWR showed `direct path write temp` and `direct path read temp` as top events. The query was spilling to temporary tablespace.
**Root Cause**: Two large tables (5GB and 3GB) were being hash-joined. The PGA was set to 2GB. The hash table for the smaller table did not fit in the PGA. Oracle spilled part of the hash table to disk, causing 5GB of temporary tablespace I/O.
**Solution**: 1) Increased `PGA_AGGREGATE_TARGET` from 2GB to 8GB. 2) Used `/*+ NO_USE_HASH(t1 t2) USE_MERGE(t1 t2) */` to avoid hash join's memory-intensive operation. 3) Added indexes on the join columns to enable nested loops join. 4) Created materialized views with pre-joined data for the reporting query. 5) Monitored `V$SQL_WORKAREA` to verify in-memory operations after fix.
**Lessons Learned**: Size PGA based on largest hash join tables. Monitor `V$SQL_WORKAREA` for workarea overflows. Consider merge join or nested loops for memory-constrained environments. Use materialized views for recurring heavy join queries.

## Scenario 4: High Version Count — Latch Contention
**Context**: An OLTP application experienced intermittent CPU spikes and latch contention.
**Problem**: AWR showed `cursor: pin S wait on X` and `library cache lock` as top wait events. `V$SQLAREA` showed one SQL with 5000 child cursors (version count).
**Root Cause**: The SQL used literals instead of bind variables: `SELECT * FROM products WHERE product_id = 123`. Each unique product_id created a new child cursor. The high version count caused library cache latch contention as Oracle tried to find and parse SQL.
**Solution**: 1) Rewrote the application to use bind variables: `SELECT * FROM products WHERE product_id = :id`. 2) Set `CURSOR_SHARING=FORCE` as a temporary workaround to force bind variable usage. 3) Set `SESSION_CACHED_CURSORS=200` to cache cursors per session. 4) Flushed the shared pool: `ALTER SYSTEM FLUSH SHARED_POOL`. 5) Implemented SQL review to enforce bind variable usage.
**Lessons Learned**: Always use bind variables in OLTP applications. Monitor `V$SQLAREA` for high version counts. Set `CURSOR_SHARING` as interim fix, but fix the code permanently. Monitor `V$SQL_SHARED_CURSOR` to understand why versions differ.
