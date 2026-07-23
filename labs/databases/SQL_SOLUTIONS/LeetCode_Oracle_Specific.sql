-- ============================================================================
-- LeetCode_Oracle_Specific.sql
-- Oracle-Specific SQL Features for LeetCode-style problems
-- Demonstrates Oracle-only syntax: CONNECT BY, MODEL, PIVOT, MATCH_RECOGNIZE
-- ============================================================================

-- ============================================================================
-- Oracle Hierarchical Queries (CONNECT BY)
-- ============================================================================

-- Problem: Find all employees who report (directly or indirectly) to a specific manager
SELECT ename, empno, LEVEL,
       SYS_CONNECT_BY_PATH(ename, ' -> ') AS reporting_chain
FROM emp
START WITH ename = 'KING'
CONNECT BY PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- Problem: Generate date-series (all days in a month)
SELECT DATE '2024-01-01' + LEVEL - 1 AS day
FROM DUAL
CONNECT BY LEVEL <= EXTRACT(DAY FROM LAST_DAY(DATE '2024-01-01'));

-- ============================================================================
-- Oracle PIVOT / UNPIVOT
-- ============================================================================

-- Problem: Pivot employee counts by job and department
SELECT *
FROM (
    SELECT d.dname AS department, e.job, e.empno
    FROM emp e
    JOIN dept d ON e.deptno = d.deptno
)
PIVOT (
    COUNT(empno)
    FOR job IN ('CLERK' AS clerk, 'SALESMAN' AS salesman, 'MANAGER' AS manager,
                'ANALYST' AS analyst, 'PRESIDENT' AS president)
)
ORDER BY department;

-- Problem: Unpivot columns to rows
WITH monthly_sales AS (
    SELECT 'Product A' AS product, 100 AS jan, 200 AS feb, 150 AS mar FROM DUAL
    UNION ALL
    SELECT 'Product B' AS product, 300 AS jan, 250 AS feb, 350 AS mar FROM DUAL
)
SELECT product, month, sales
FROM monthly_sales
UNPIVOT (
    sales FOR month IN (jan AS 'January', feb AS 'February', mar AS 'March')
);

-- ============================================================================
-- Oracle MODEL Clause
-- ============================================================================

-- Problem: Salary projection with different raise percentages by department
SELECT deptno, ename, sal AS current_salary,
       s[0] AS projected_salary,
       s[1] AS bonus
FROM emp
MODEL
    DIMENSION BY (deptno, ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY empno) AS rn)
    MEASURES (sal, 0 AS s, ename)
    RULES (
        s[ANY, ANY] = CASE WHEN deptno = 10 THEN sal[CV(), CV()] * 1.15
                           WHEN deptno = 20 THEN sal[CV(), CV()] * 1.10
                           ELSE sal[CV(), CV()] * 1.05 END
    )
ORDER BY deptno, ename;

-- ============================================================================
-- Oracle MATCH_RECOGNIZE (Pattern Matching)
-- ============================================================================

-- Problem: Find stock price patterns (three consecutive increases)
-- Requires Oracle 12c+
/*
SELECT *
FROM stock_prices
MATCH_RECOGNIZE (
    PARTITION BY symbol
    ORDER BY trade_date
    MEASURES
        FIRST(price) AS start_price,
        LAST(price) AS end_price,
        COUNT(*) AS days_increase
    PATTERN (START_UP UP+)
    DEFINE
        UP AS price > PREV(price)
);
*/

-- Simplified MATCH_RECOGNIZE for employee salary increases
/*
SELECT ename, hiredate, sal
FROM emp_history
MATCH_RECOGNIZE (
    PARTITION BY empno
    ORDER BY effective_date
    MEASURES
        FIRST(sal) AS starting_sal,
        LAST(sal) AS current_sal
    PATTERN (START ANY_ROW+)
    DEFINE
        ANY_ROW AS sal >= LAG(sal, 1, 0) OVER (PARTITION BY empno ORDER BY effective_date)
);
*/

-- ============================================================================
-- Oracle Flashback Query
-- ============================================================================

-- Problem: Retrieve data as of a specific point in time
-- (Requires Oracle Flashback feature)
SELECT * FROM emp AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR);

SELECT * FROM emp VERSIONS BETWEEN TIMESTAMP
    SYSTIMESTAMP - INTERVAL '2' HOUR AND SYSTIMESTAMP;

-- ============================================================================
-- Oracle JSON Functions (Oracle 12c+ / 23c)
-- ============================================================================

-- Problem: Query JSON data stored in a relational column
-- Assuming a table with json_data column of type VARCHAR2/CLOB
SELECT jt.*
FROM customers c,
     JSON_TABLE(c.preferences, '$'
         COLUMNS (
             theme VARCHAR2(50) PATH '$.theme',
             language VARCHAR2(10) PATH '$.language',
             notifications NUMBER PATH '$.notifications.enabled'
         )
     ) jt
WHERE jt.language = 'en';

-- Oracle 23c: JSON-relational duality views
/*
CREATE JSON RELATIONAL DUALITY VIEW emp_json AS
    SELECT JSON {'empno': e.empno,
                 'ename': e.ename,
                 'department': JSON {'deptno': d.deptno,
                                     'dname': d.dname} }
    FROM emp e JOIN dept d ON e.deptno = d.deptno;
*/

-- ============================================================================
-- Oracle Recursive CTE (WITH ... SEARCH)
-- ============================================================================

-- Problem: Organizational hierarchy with breadth-first or depth-first ordering
WITH org_tree (empno, ename, mgr, level_num) AS (
    SELECT empno, ename, mgr, 1
    FROM emp
    WHERE mgr IS NULL

    UNION ALL

    SELECT e.empno, e.ename, e.mgr, ot.level_num + 1
    FROM emp e
    JOIN org_tree ot ON e.mgr = ot.empno
)
SEARCH DEPTH FIRST BY ename SET order_col
SELECT level_num, LPAD(' ', (level_num - 1) * 3) || ename AS org_chart
FROM org_tree
ORDER BY order_col;

-- ============================================================================
-- Oracle Analytical Functions (Advanced)
-- ============================================================================

-- Problem: Running totals with different window specifications
SELECT ename, hiredate, sal,
       -- Default: entire partition
       SUM(sal) OVER () AS total_all,
       -- Cumulative: first row to current
       SUM(sal) OVER (ORDER BY hiredate) AS cumulative,
       -- Running 3-row average
       AVG(sal) OVER (ORDER BY hiredate ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS moving_avg,
       -- Cumulative by department
       SUM(sal) OVER (PARTITION BY deptno ORDER BY hiredate) AS dept_cumulative
FROM emp
ORDER BY hiredate;

-- Problem: Ratio to report (percentage of total)
SELECT ename, sal,
       RATIO_TO_REPORT(sal) OVER () AS pct_of_company,
       RATIO_TO_REPORT(sal) OVER (PARTITION BY deptno) AS pct_of_dept
FROM emp
ORDER BY deptno, sal DESC;

-- Problem: Top-N per group with window function
SELECT deptno, ename, sal
FROM (
    SELECT deptno, ename, sal,
           ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY sal DESC) AS rn
    FROM emp
)
WHERE rn <= 3
ORDER BY deptno, sal DESC;

-- ============================================================================
-- Oracle MERGE (Upsert)
-- ============================================================================

-- Problem: Insert or update employee salary record
MERGE INTO emp_target t
USING (
    SELECT 7369 AS empno, 'SMITH' AS ename, 5000 AS sal FROM DUAL
) s
ON (t.empno = s.empno)
WHEN MATCHED THEN
    UPDATE SET t.sal = s.sal, t.updated_at = SYSDATE
WHEN NOT MATCHED THEN
    INSERT (empno, ename, sal, hiredate)
    VALUES (s.empno, s.ename, s.sal, SYSDATE);

-- ============================================================================
-- Oracle Multi-Table Insert
-- ============================================================================

-- Problem: Split data into different tables based on conditions
INSERT ALL
    WHEN sal > 3000 THEN INTO high_salary (empno, ename, sal) VALUES (empno, ename, sal)
    WHEN sal BETWEEN 2000 AND 3000 THEN INTO mid_salary (empno, ename, sal) VALUES (empno, ename, sal)
    ELSE INTO low_salary (empno, ename, sal) VALUES (empno, ename, sal)
SELECT empno, ename, sal FROM emp;

-- ============================================================================
-- Oracle Temporal Validity (Period-Based Data)
-- ============================================================================
-- Oracle 12c+: Temporal validity for slowly changing dimensions

-- Create a table with temporal validity
/*
CREATE TABLE emp_history (
    empno NUMBER,
    ename VARCHAR2(10),
    sal NUMBER,
    deptno NUMBER,
    effective_start DATE,
    effective_end DATE,
    PERIOD FOR valid_time (effective_start, effective_end)
);

-- Query data as of a point in time
SELECT * FROM emp_history
AS OF PERIOD FOR valid_time DATE '2024-06-01';
*/

-- ============================================================================
-- END OF ORACLE-SPECIFIC SOLUTIONS
-- ============================================================================
