# Production Scenarios: SQL Fundamentals (Oracle Focus)

## Scenario 1: NULL Handling Causing Wrong Query Results
**Context**: A financial report query was supposed to show all customers and their total orders.
**Problem**: The report was missing customers who had no orders (NULL total). The query used `WHERE total_amount > 1000` which excluded NULLs.
**Root Cause**: In Oracle SQL, NULL is neither equal to nor greater than any value. The condition `total_amount > 1000` evaluates to UNKNOWN (not FALSE) for NULL, so those rows are excluded. The developer assumed NULL would be treated as 0.
**Solution**: 1) Changed query to use `NVL(total_amount, 0) > 1000` or `total_amount > 1000 OR total_amount IS NULL`. 2) Better approach: used `LEFT JOIN` with `COALESCE(total_amount, 0)` in the SELECT. 3) Added NOT NULL constraint with DEFAULT 0 on the amount column. 4) Trained developers on Oracle NULL behavior. 5) Implemented SQL review checklist with NULL handling item.
**Lessons Learned**: Always handle NULLs explicitly in WHERE clauses. Use NVL/COALESCE to provide defaults. Train developers on three-valued logic. Include NULL checks in code review checklist.

## Scenario 2: MERGE Statement Fails with ORA-30926
**Context**: A data warehousing job uses MERGE to upsert 1M rows daily.
**Problem**: The MERGE statement failed with `ORA-30926: unable to get a stable set of rows in the source tables`. The job had to be re-run.
**Root Cause**: The source query joined multiple tables and produced duplicate rows for the same target key. When Oracle tried to merge, it found multiple source rows matching one target row and could not determine which to use.
**Solution**: 1) Identified duplicates in source: `SELECT key, COUNT(*) FROM source GROUP BY key HAVING COUNT(*) > 1`. 2) Deduplicated the source query with `ROW_NUMBER() OVER (PARTITION BY key ORDER BY priority)`. 3) Added unique constraint on target table to enforce no duplicates. 4) Changed to separate UPDATE and INSERT steps for better error handling. 5) Added pre-MERGE validation to check for source duplicates.
**Lessons Learned**: Validate source data has unique keys before MERGE. Use ROW_NUMBER for deduplication. Consider separate UPDATE/INSERT for better error handling. Add unique constraints on target merge keys.

## Scenario 3: Hierarchical Query Cycle Detection
**Context**: An organization hierarchy query using CONNECT BY started returning infinite results.
**Problem**: The query `SELECT * FROM employees CONNECT BY PRIOR employee_id = manager_id` produced 10M rows from a 500-row table. The query eventually failed with ORA-01436: CONNECT BY loop in user data.
**Root Cause**: A data entry error created a cycle: Employee A reported to B, B reported to C, C reported to A. The CONNECT BY query followed the cycle infinitely until it hit loop detection.
**Solution**: 1) Used `NOCYCLE` clause: `CONNECT BY NOCYCLE PRIOR employee_id = manager_id`. 2) Added `CONNECT_BY_ISCYCLE` pseudo-column to identify cycle-causing rows. 3) Fixed the data by updating the circular reference. 4) Added database constraint: trigger that prevents circular manager references. 5) Implemented regular cycle detection validation script.
**Lessons Learned**: Always use NOCYCLE in production CONNECT BY queries. Add constraints to prevent circular references. Validate hierarchical data on insert/update. Monitor CONNECT_BY_ISCYCLE regularly.

## Scenario 4: ROWNUM Pagination Returns Wrong Results
**Context**: A paginated report showing employees sorted by salary was showing inconsistent results.
**Problem**: When using `SELECT * FROM (SELECT * FROM emp ORDER BY salary) WHERE ROWNUM <= 10`, different pages sometimes showed the same employees. Results were not deterministic.
**Root Cause**: ROWNUM is assigned before the ORDER BY in the outer query. The subquery ordered by salary, but if two employees had the same salary, the order was non-deterministic. ROWNUM was assigned arbitrarily.
**Solution**: 1) Added a tiebreaker to the ORDER BY: `ORDER BY salary, employee_id`. 2) Used `ROW_NUMBER()` instead of ROWNUM for pagination: `ROW_NUMBER() OVER (ORDER BY salary, employee_id)`. 3) Implemented keyset pagination with `WHERE (salary, employee_id) > (:last_salary, :last_id)`. 4) Added unique constraint combination for deterministic ordering.
**Lessons Learned**: Always include tiebreaker columns in ORDER BY for pagination. Use ROW_NUMBER() instead of ROWNUM for reliable pagination. Consider keyset pagination for large datasets. Test pagination with duplicate sort values.
