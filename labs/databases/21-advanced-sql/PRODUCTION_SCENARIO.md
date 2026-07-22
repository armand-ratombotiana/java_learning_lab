# Production Scenarios: Advanced SQL (Oracle Focus)

## Scenario 1: Window Function Causes Temporary Tablespace Exhaustion
**Context**: A daily reporting query using multiple window functions on a 50M row table.
**Problem**: The query failed with "ORA-01652: unable to extend temp segment". The 20GB temporary tablespace was exhausted. The report could not be generated.
**Root Cause**: The query used `ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC)` on 50M rows without partitioning. Oracle sorted the entire result set in the temporary tablespace to compute the window function. The sort required 30GB of temporary space.
**Solution**: 1) Added date range filter to reduce the dataset to 5M rows per run. 2) Processed the report in batches: one department at a time. 3) Added index on `(department, salary)` to enable sort avoidance. 4) Increased temporary tablespace to 60GB. 5) Used `PGA_AGGREGATE_TARGET` to allow in-memory sorting for smaller batches.
**Lessons Learned**: Filter window function inputs to reduce sort size. Process large window function queries in batches. Index partition and order by columns for sort optimization. Size temporary tablespace for peak window function usage.

## Scenario 2: MATCH_RECOGNIZE Timing Out
**Context**: A fraud detection query using `MATCH_RECOGNIZE` to find patterns in transaction streams.
**Problem**: The query processed 24 hours of transactions and timed out after 30 minutes. The pattern matching was scanning the entire transaction history for each account.
**Root Cause**: `MATCH_RECOGNIZE` without a `PARTITION BY` account clause processed all accounts together. The pattern `PATTERN (STRT DOWN+ UP+)` required scanning all transactions in sequence. Without partitioning by account, the matcher treated all transactions as one stream.
**Solution**: 1) Added `PARTITION BY account_id` to limit pattern matching per account. 2) Added date range filter: only analyze last 7 days instead of all history. 3) Used `AFTER MATCH SKIP TO NEXT ROW` to avoid re-scanning. 4) Limited the duration of pattern matching: `SUBSET` with time constraint. 5) Created a materialized view with pre-processed per-account aggregates.
**Lessons Learned**: Always partition `MATCH_RECOGNIZE` by relevant dimension. Filter data to minimum required range. Use `SKIP` clauses to optimize pattern matching. Test pattern matching performance with production data volumes.

## Scenario 3: MERGE Statement Causing ORA-30926
**Context**: A daily data warehouse load used MERGE to upsert 1M records.
**Problem**: The MERGE statement failed with `ORA-30926: unable to get a stable set of rows in the source tables`. The load job failed and had to be re-run.
**Root Cause**: The source query joined three tables and produced duplicate rows for the same target key. Two records with the same `order_id` and different modification timestamps existed in the source. Oracle could not determine which source row to use for the merge.
**Solution**: 1) Identified duplicates: `SELECT order_id, COUNT(*) FROM source GROUP BY order_id HAVING COUNT(*) > 1`. 2) Deduplicated using `ROW_NUMBER() OVER (PARTITION BY order_id ORDER BY last_modified DESC) AS rn`. 3) Used `SELECT ... WHERE rn = 1` to pick the latest version. 4) Added unique constraint on source table to prevent future duplicates. 5) Implemented pre-MERGE validation in the ETL script.
**Lessons Learned**: Always deduplicate source data before MERGE. Use ROW_NUMBER for latest-record dedup. Add unique constraints on merge key columns. Validate source data quality before merge operations.

## Scenario 4: Recursive CTE Infinite Loop
**Context**: An organizational hierarchy query used a recursive CTE to find all subordinates.
**Problem**: The query returned billions of rows and was terminated after 10 minutes by the DBA. It caused CPU at 100% and filled the temporary tablespace.
**Root Cause**: A cycle existed in the hierarchy: Employee 101 managed Employee 102, who managed Employee 103, who managed Employee 101. The recursive CTE followed the cycle infinitely because no `CYCLE` clause was added.
**Solution**: 1) Added `CYCLE employee_id SET is_cycle TO '1' DEFAULT '0'` to detect and stop at cycles. 2) Added `MAXRECURSION 10` to limit recursion depth. 3) Fixed the data: updated Employee 103's manager_id to NULL. 4) Added a trigger to prevent circular manager references. 5) Implemented pre-query validation for hierarchical data integrity.
**Lessons Learned**: Always use `CYCLE` clause in recursive CTEs for hierarchical data. Set `MAXRECURSION` as a safety net. Validate hierarchical data on insert/update. Monitor iteration count in recursive queries.
