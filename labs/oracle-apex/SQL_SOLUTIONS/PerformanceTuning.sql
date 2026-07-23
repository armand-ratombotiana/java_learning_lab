-- ============================================================================
-- PerformanceTuning.sql
-- Oracle SQL performance tuning techniques for APEX and database interviews
-- Covers execution plans, indexing, query optimization, statistics, hints
-- ============================================================================

-- ============================================================================
-- SECTION 1: Execution Plans
-- ============================================================================

-- 1.1 Generate and display execution plan
EXPLAIN PLAN SET STATEMENT_ID = 'my_query' FOR
SELECT e.ename, e.sal, d.dname
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.sal > 2500
ORDER BY e.sal DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY('PLAN_TABLE', 'my_query', 'ALL'));

-- 1.2 Display last executed plan (in session)
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(FORMAT => 'ALLSTATS LAST +OUTLINE +PEEKED_BINDS'));

-- 1.3 Display plan from AWR
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_AWR('5jx7p9y8k6fq1')); -- SQL ID

-- 1.4 Format options for DBMS_XPLAN
-- BASIC: minimal output
-- TYPICAL: default output
-- ALL: full output
-- ADVANCED: includes outline and query block
-- ALLSTATS LAST: actual execution statistics from cursor cache
-- +OUTLINE: includes optimizer outline hints
-- +PEEKED_BINDS: shows bind variable values used

-- 1.5 Real-time SQL monitoring (for long-running queries)
SELECT DBMS_SQL_MONITOR.REPORT_SQL_MONITOR(
    sql_id => '5jx7p9y8k6fq1',
    type => 'TEXT'
) AS report
FROM DUAL;

-- ============================================================================
-- SECTION 2: Indexing Strategies
-- ============================================================================

-- 2.1 Create useful indexes for APEX reports
-- B-tree index for equality and range queries
CREATE INDEX idx_emp_deptno ON emp(deptno);
CREATE INDEX idx_emp_sal ON emp(sal DESC);
CREATE INDEX idx_emp_hiredate ON emp(hiredate);

-- Composite index for common query patterns
CREATE INDEX idx_emp_dept_sal ON emp(deptno, sal DESC);

-- Function-based index for case-insensitive search
CREATE INDEX idx_emp_upper_name ON emp(UPPER(ename));

-- Bitmap index for low-cardinality columns
CREATE BITMAP INDEX idx_emp_job_bm ON emp(job);

-- 2.2 Find missing indexes (potential candidates)
SELECT
    sql_id,
    plan_hash_value,
    object_owner,
    object_name,
    options,
    cardinality,
    access_predicates,
    filter_predicates
FROM v$sql_plan
WHERE operation LIKE '%TABLE ACCESS%'
  AND options = 'FULL'
  AND object_owner NOT IN ('SYS', 'SYSTEM')
  AND cardinality > 1000
ORDER BY cardinality DESC;

-- 2.3 Index usage statistics
SELECT
    i.index_name,
    i.table_name,
    i.uniqueness,
    i.distinct_keys,
    i.num_rows,
    i.leaf_blocks,
    i.clustering_factor,
    s.rows_per_key,
    s.used    AS stats_used
FROM dba_indexes i
JOIN dba_ind_statistics s ON i.index_name = s.index_name
WHERE i.table_owner = 'SCOTT'
ORDER BY i.table_name, i.index_name;

-- 2.4 Unused indexes detection
ALTER INDEX idx_emp_job_bm MONITORING USAGE;

SELECT index_name, monitoring, used, start_monitoring
FROM v$object_usage
WHERE index_name = 'IDX_EMP_JOB_BM';

-- ============================================================================
-- SECTION 3: SQL Tuning Advisor
-- ============================================================================

-- 3.1 Run SQL Tuning Advisor on a SQL statement
DECLARE
    v_task_name VARCHAR2(100);
    v_sqltext   CLOB;
BEGIN
    v_sqltext := 'SELECT e.ename, e.sal, d.dname
                  FROM emp e
                  JOIN dept d ON e.deptno = d.deptno
                  WHERE e.sal > 2500
                  ORDER BY e.sal DESC';

    v_task_name := DBMS_SQLTUNE.CREATE_TUNING_TASK(
        sql_text    => v_sqltext,
        user_name   => 'SCOTT',
        scope       => 'COMPREHENSIVE',
        time_limit  => 300,
        task_name   => 'emp_tuning_task'
    );

    DBMS_SQLTUNE.EXECUTE_TUNING_TASK(task_name => 'emp_tuning_task');
END;
/

-- 3.2 View tuning task recommendations
SELECT DBMS_SQLTUNE.REPORT_TUNING_TASK('emp_tuning_task') AS recommendations
FROM DUAL;

-- 3.3 Accept SQL Profile recommendation
-- (Only after reviewing recommendations)
BEGIN
    DBMS_SQLTUNE.ACCEPT_SQL_PROFILE(
        task_name => 'emp_tuning_task',
        name      => 'emp_profile',
        replace   => TRUE
    );
END;
/

-- ============================================================================
-- SECTION 4: Query Optimization Techniques
-- ============================================================================

-- 4.1 Use bind variables (critical for APEX)
-- ❌ Bad: literal values (hard parse each time)
SELECT * FROM emp WHERE deptno = 10;

-- ✅ Good: bind variables (soft parse, plan reuse)
SELECT * FROM emp WHERE deptno = :P101_DEPTNO;

-- 4.2 Optimize SELECT list
-- ❌ Bad: SELECT * (unnecessary columns, more I/O)
SELECT * FROM emp WHERE deptno = 20;

-- ✅ Good: specific columns (reduced I/O, index-only scan possible)
SELECT empno, ename, job, sal FROM emp WHERE deptno = 20;

-- 4.3 Use EXISTS instead of IN (when appropriate)
-- ❌ Sometimes slower with large subquery
SELECT e.* FROM emp e
WHERE e.deptno IN (SELECT deptno FROM dept WHERE loc = 'NEW YORK');

-- ✅ Often faster with large subquery
SELECT e.* FROM emp e
WHERE EXISTS (SELECT 1 FROM dept d WHERE d.deptno = e.deptno AND d.loc = 'NEW YORK');

-- 4.4 Avoid functions on indexed columns in WHERE
-- ❌ Bad: function prevents index usage
SELECT * FROM emp WHERE UPPER(ename) = 'SMITH';

-- ✅ Good: use function-based index or compare directly
SELECT * FROM emp WHERE ename = 'SMITH';

-- 4.5 Use UNION ALL instead of UNION when duplicates are acceptable
-- UNION does implicit DISTINCT (sort + filter)
-- UNION ALL does no duplicate check (faster)

-- 4.6 Optimize pagination with row limiting
-- ❌ Bad: old-style pagination (OFFSET reads all rows)
SELECT * FROM (
    SELECT e.*, ROWNUM AS rnum FROM (
        SELECT * FROM emp ORDER BY empno
    ) e WHERE ROWNUM <= 30
) WHERE rnum >= 21;

-- ✅ Good: OFFSET/FETCH (optimized in 12c+)
SELECT * FROM emp
ORDER BY empno
OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;

-- ============================================================================
-- SECTION 5: Statistics and Optimizer
-- ============================================================================

-- 5.1 Gather table statistics
EXEC DBMS_STATS.GATHER_TABLE_STATS('SCOTT', 'EMP');
EXEC DBMS_STATS.GATHER_TABLE_STATS('SCOTT', 'DEPT');

-- 5.2 Gather schema statistics
EXEC DBMS_STATS.GATHER_SCHEMA_STATS('SCOTT');

-- 5.3 Check when statistics were last gathered
SELECT table_name, num_rows, blocks, avg_row_len,
       last_analyzed, stale_stats
FROM dba_tab_statistics
WHERE owner = 'SCOTT'
  AND table_name IN ('EMP', 'DEPT');

-- 5.4 View optimizer parameters
SELECT name, value, isdefault, description
FROM v$parameter
WHERE name LIKE '%optimizer%'
ORDER BY name;

-- 5.5 Force fresh statistics for APEX reporting (if data changes frequently)
BEGIN
    DBMS_STATS.SET_TABLE_PREFS('SCOTT', 'EMP', 'STALE_PERCENT', '5');
END;
/

-- ============================================================================
-- SECTION 6: SQL Hints (Use Sparingly)
-- ============================================================================

-- 6.1 Access path hints
SELECT /*+ FULL(e) */ * FROM emp e WHERE e.sal > 2000;
SELECT /*+ INDEX(e idx_emp_sal) */ * FROM emp e WHERE e.sal > 2000;
SELECT /*+ INDEX_FFS(e idx_emp_deptno) */ deptno FROM emp e;

-- 6.2 Join order hints
SELECT /*+ LEADING(e d) USE_HASH(e d) */ *
FROM emp e, dept d
WHERE e.deptno = d.deptno;

SELECT /*+ LEADING(d e) USE_NL(e d) */ *
FROM emp e, dept d
WHERE e.deptno = d.deptno;

-- 6.3 Parallel execution hints
SELECT /*+ PARALLEL(e, 4) */ * FROM emp e;
SELECT /*+ PARALLEL(e, DEFAULT) */ COUNT(*) FROM emp e;

-- 6.4 Result cache hint (useful in APEX for static data)
SELECT /*+ RESULT_CACHE */ dname, loc FROM dept;

-- ============================================================================
-- SECTION 7: Common Performance Problems and Solutions
-- ============================================================================

-- 7.1 Identify full table scans in current session
SELECT sql_id, sql_text, plan_hash_value,
       disk_reads, buffer_gets, elapsed_time
FROM v$sql
WHERE sql_id IN (
    SELECT sql_id FROM v$sql_plan
    WHERE operation = 'TABLE ACCESS'
      AND options = 'FULL'
)
ORDER BY elapsed_time DESC;

-- 7.2 Top SQL by resource consumption
SELECT sql_id,
       sql_text,
       executions,
       elapsed_time / 1000000 AS elapsed_sec,
       cpu_time / 1000000 AS cpu_sec,
       buffer_gets,
       disk_reads,
       rows_processed
FROM v$sql
WHERE elapsed_time > 0
  AND command_type = 3  -- SELECT
ORDER BY elapsed_time DESC
FETCH FIRST 10 ROWS ONLY;

-- 7.3 Identify missing or unused indexes
SELECT
    object_name,
    operation,
    options,
    cardinality,
    access_predicates,
    filter_predicates
FROM v$sql_plan
WHERE operation = 'TABLE ACCESS'
  AND options = 'FULL'
  AND cardinality > 10000
ORDER BY cardinality DESC;

-- 7.4 Check for SQL with high parse count
SELECT sql_id,
       sql_text,
       parse_calls,
       executions,
       ROUND(parse_calls / GREATEST(executions, 1), 2) AS parses_per_exec
FROM v$sql
WHERE executions > 0
  AND parse_calls > executions * 2
ORDER BY parses_per_exec DESC;

-- 7.5 Find queries without bind variables (literal SQL)
SELECT sql_id,
       sql_text,
       executions,
       parse_calls,
       SUBSTR(sql_text, 1, 100) AS sql_preview
FROM v$sql
WHERE sql_text NOT LIKE '%:%'  -- No bind variables
  AND command_type = 3          -- SELECT
  AND executions > 1
  AND parsing_schema_name NOT IN ('SYS', 'SYSTEM')
ORDER BY executions DESC;

-- ============================================================================
-- SECTION 8: APEX-Specific Performance
-- ============================================================================

-- 8.1 APEX page timing analysis
SELECT apex_page_id,
       apex_page_name,
       apex_session_id,
       apex_user,
       elapsed_time,
       page_view_type,
       application_id
FROM apex_workspace_activity_log
WHERE application_id = :APP_ID
  AND view_date > SYSDATE - 7
ORDER BY elapsed_time DESC
FETCH FIRST 20 ROWS ONLY;

-- 8.2 Identify slow APEX regions
-- (In debug output, look for "Execute SQL" with high duration)
-- Enable debug: ?p_debug=YES

-- 8.3 Optimize APEX Interactive Grid with large datasets
-- Use "Max Row Count" to limit displayed rows
-- Set "Show maximum row count" to 1000 or less
-- Use pagination type: "Page" instead of "Scroll"
-- Enable "Lazy Loading" for detail regions

-- 8.4 Query result cache for frequently accessed data
CREATE OR REPLACE FUNCTION get_dept_name(p_deptno NUMBER) RETURN VARCHAR2
RESULT_CACHE RELIES_ON (dept) AS
    v_dname dept.dname%TYPE;
BEGIN
    SELECT dname INTO v_dname FROM dept WHERE deptno = p_deptno;
    RETURN v_dname;
END get_dept_name;
/

-- ============================================================================
-- SECTION 9: ASH and AWR Analysis
-- ============================================================================

-- 9.1 Active Session History (ASH) top wait events
SELECT event,
       COUNT(*) AS session_count,
       ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 1) AS pct
FROM v$active_session_history
WHERE sample_time > SYSDATE - 1/24  -- Last hour
  AND session_type = 'FOREGROUND'
  AND wait_class != 'Idle'
GROUP BY event
ORDER BY session_count DESC;

-- 9.2 AWR report generation (requires Diagnostic Pack license)
-- From SQL*Plus:
-- @?/rdbms/admin/awrrpt.sql

-- 9.3 AWR top SQL by elapsed time
SELECT *
FROM TABLE(DBMS_WORKLOAD_REPOSITORY.AWR_REPORT_TEXT(
    l_dbid       => (SELECT dbid FROM v$database),
    l_inst_num   => 1,
    l_bid        => (SELECT MIN(snap_id) FROM dba_hist_snapshot WHERE begin_interval_time > SYSDATE - 1),
    l_eid        => (SELECT MAX(snap_id) FROM dba_hist_snapshot)
));

-- 9.4 ADDM report (Automatic Database Diagnostic Monitor)
VARIABLE v_task_name VARCHAR2(100);
EXEC :v_task_name := DBMS_ADDM.ANALYZE_INST(
    task_name  => 'addm_analysis',
    begin_snap => (SELECT MIN(snap_id) FROM dba_hist_snapshot WHERE begin_interval_time > SYSDATE - 1),
    end_snap   => (SELECT MAX(snap_id) FROM dba_hist_snapshot)
);

SELECT DBMS_ADDM.GET_REPORT(:v_task_name) AS addm_report FROM DUAL;

-- ============================================================================
-- END OF PERFORMANCE TUNING
-- ============================================================================
