# Debugging Advanced SQL

## Execution Plan Analysis
```sql
EXPLAIN PLAN SET STATEMENT_ID = 'QS1' FOR
SELECT /*+ GATHER_PLAN_STATISTICS */ * FROM employees WHERE dept_id = 10;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY('PLAN_TABLE', 'QS1', 'ALL'));
```

## Real-Time SQL Monitoring
```sql
-- Requires TUNE or MONITOR hint
SELECT /*+ MONITOR */ * FROM employees WHERE salary > 10000;
-- Query the monitoring report
SELECT DBMS_SQLTUNE.REPORT_SQL_MONITOR(sql_id => 'abc123') FROM dual;
```

## Common Window Function Debugs
1. "missing keyword": missing OVER clause
2. "invalid window specification": incorrect frame clause
3. "not a GROUP BY expression": mixing GROUP BY and window functions
4. Unintended ties: ROW_NUMBER vs RANK confusion

## Recursive CTE Debugging
1. "Infinite recursion": use MAXRECURSION hint or CYCLE clause
2. "Anchor member returns too many rows": the anchor should be a base set
3. "UNION vs UNION ALL": use UNION ALL to avoid duplicate elimination overhead

## PIVOT Debugging
1. "Missing pivot values": the IN clause must list all source values
2. "NULL values in source": PIVOT excludes or includes nulls based on query
3. "Multiple matches": PIVOT requires an aggregate function

## MERGE Debugging
1. "Duplicate rows in source": MERGE fails if the ON clause matches multiple rows
2. "Unintended updates": verify the ON condition is selective
3. "ORA-30926": classic "unable to get a stable set of rows"

## Optimizer Debugging
```sql
-- Show histogram info
SELECT COLUMN_NAME, HISTOGRAM FROM USER_TAB_COL_STATISTICS WHERE TABLE_NAME = 'EMPLOYEES';
-- Check statistics freshness
SELECT TABLE_NAME, LAST_ANALYZED FROM USER_TABLES WHERE TABLE_NAME = 'EMPLOYEES';
```

## Hints Not Working
1. Table aliases must match in hints: `/*+ INDEX(e emp_dept_idx) */`
2. Hint conflicts: incompatible hints cancel out
3. Query block names: use QB_NAME hint to reference subquery blocks

## Adaptive Plan Debugging
```sql
-- Display adaptive plan
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(sql_id => 'abc123', format => 'ADAPTIVE'));
-- Check final plan after adaptation
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(sql_id => 'abc123', format => 'ALLSTATS LAST'));
```

## SQL Profile Debugging
```sql
-- Check SQL profiles
SELECT NAME, STATUS, CREATED FROM DBA_SQL_PROFILES;
-- Drop bad profile
EXEC DBMS_SQLTUNE.DROP_SQL_PROFILE(name => 'profile_name');
```

## SPM Debugging
```sql
-- Check SQL plan baselines
SELECT SQL_HANDLE, PLAN_NAME, ACCEPTED, FIXED FROM DBA_SQL_PLAN_BASELINES;
-- Evolve a plan
EXEC DBMS_SPM.EVOLVE_SQL_PLAN_BASELINE(sql_handle => 'handle');
```

## Performance Debugging
1. Run the query with and without hints
2. Compare actual vs estimated cardinality using `DBMS_XPLAN.DISPLAY_CURSOR(format => '+COST'))
3. Check for type conversion in predicates
4. Verify partition pruning with `EXPLAIN PLAN`
5. Use SQL Monitor for long-running queries