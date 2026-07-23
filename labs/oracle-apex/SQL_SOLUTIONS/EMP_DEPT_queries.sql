-- ============================================================================
-- EMP_DEPT_queries.sql
-- Classic Oracle SQL problems using the EMP and DEPT tables
-- For Oracle APEX and Oracle Database interviews
-- ============================================================================

-- Setup sample tables (if not exists)
/*
CREATE TABLE dept (
    deptno NUMBER(2) PRIMARY KEY,
    dname  VARCHAR2(14),
    loc    VARCHAR2(13)
);

CREATE TABLE emp (
    empno    NUMBER(4) PRIMARY KEY,
    ename    VARCHAR2(10),
    job      VARCHAR2(9),
    mgr      NUMBER(4),
    hiredate DATE,
    sal      NUMBER(7,2),
    comm     NUMBER(7,2),
    deptno   NUMBER(2) REFERENCES dept(deptno)
);
*/

-- ============================================================================
-- SECTION 1: Basic Queries (Interview Warm-up)
-- ============================================================================

-- 1.1 List all employees and their department names
SELECT e.empno, e.ename, e.job, d.dname, d.loc
FROM emp e
JOIN dept d ON e.deptno = d.deptno
ORDER BY e.ename;

-- 1.2 Find employees with salary greater than 3000
SELECT ename, sal, job
FROM emp
WHERE sal > 3000
ORDER BY sal DESC;

-- 1.3 List all department names and their locations (even if no employees)
SELECT d.deptno, d.dname, d.loc, COUNT(e.empno) AS employee_count
FROM dept d
LEFT JOIN emp e ON d.deptno = e.deptno
GROUP BY d.deptno, d.dname, d.loc
ORDER BY d.deptno;

-- 1.4 Find employees hired in 1981
SELECT ename, hiredate, TO_CHAR(hiredate, 'YYYY-MM-DD') AS formatted_date
FROM emp
WHERE EXTRACT(YEAR FROM hiredate) = 1981
ORDER BY hiredate;

-- 1.5 Find employees whose name starts with 'S'
SELECT empno, ename, job
FROM emp
WHERE ename LIKE 'S%'
ORDER BY ename;

-- 1.6 Calculate annual salary (including commission)
SELECT ename, sal, comm, NVL(comm, 0) AS comm_fixed,
       (sal * 12) + NVL(comm, 0) AS annual_income
FROM emp
ORDER BY annual_income DESC;

-- ============================================================================
-- SECTION 2: Aggregate Queries
-- ============================================================================

-- 2.1 Department-wise salary statistics
SELECT d.dname,
       COUNT(e.empno)      AS employee_count,
       SUM(e.sal)          AS total_salary,
       AVG(e.sal)          AS avg_salary,
       MEDIAN(e.sal)       AS median_salary,
       MIN(e.sal)          AS min_salary,
       MAX(e.sal)          AS max_salary,
       STDDEV(e.sal)       AS salary_stddev,
       VARIANCE(e.sal)     AS salary_variance
FROM dept d
LEFT JOIN emp e ON d.deptno = e.deptno
GROUP BY d.dname
ORDER BY avg_salary DESC;

-- 2.2 Job-wise salary analysis
SELECT job,
       COUNT(*)          AS employee_count,
       AVG(sal)          AS avg_salary,
       MIN(sal)          AS min_salary,
       MAX(sal)          AS max_salary,
       MAX(sal) - MIN(sal) AS salary_range
FROM emp
GROUP BY job
ORDER BY avg_salary DESC;

-- 2.3 Departments with average salary above 2500
SELECT d.dname, AVG(e.sal) AS avg_salary
FROM emp e
JOIN dept d ON e.deptno = d.deptno
GROUP BY d.dname
HAVING AVG(e.sal) > 2500
ORDER BY avg_salary DESC;

-- 2.4 Employees earning more than their department average
SELECT e.ename, e.sal, d.dname,
       ROUND(AVG(e.sal) OVER (PARTITION BY e.deptno), 2) AS dept_avg
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.sal > AVG(e.sal) OVER (PARTITION BY e.deptno)
ORDER BY e.sal DESC;

-- ============================================================================
-- SECTION 3: Subquery Questions
-- ============================================================================

-- 3.1 Find employees who earn more than their manager
SELECT e.ename AS employee, e.sal AS emp_salary,
       m.ename AS manager, m.sal AS mgr_salary
FROM emp e
JOIN emp m ON e.mgr = m.empno
WHERE e.sal > m.sal
ORDER BY (e.sal - m.sal) DESC;

-- 3.2 Find departments that have at least one employee
SELECT deptno, dname
FROM dept d
WHERE EXISTS (SELECT 1 FROM emp e WHERE e.deptno = d.deptno)
ORDER BY deptno;

-- 3.3 Find employees who earn the maximum salary in their department
SELECT e.ename, e.sal, d.dname
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.sal = (SELECT MAX(sal) FROM emp WHERE deptno = e.deptno)
ORDER BY e.sal DESC;

-- 3.4 Find employees who were hired before their manager
SELECT e.ename AS employee, e.hiredate AS emp_hire,
       m.ename AS manager, m.hiredate AS mgr_hire
FROM emp e
JOIN emp m ON e.mgr = m.empno
WHERE e.hiredate < m.hiredate
ORDER BY e.hiredate;

-- 3.5 Find the top 3 highest paid employees overall
SELECT empno, ename, sal, job
FROM (
    SELECT empno, ename, sal, job,
           DENSE_RANK() OVER (ORDER BY sal DESC) AS sal_rank
    FROM emp
)
WHERE sal_rank <= 3
ORDER BY sal DESC;

-- ============================================================================
-- SECTION 4: Complex JOIN Questions
-- ============================================================================

-- 4.1 Full organizational hierarchy with manager names
SELECT e.ename AS employee, e.job,
       m.ename AS manager,
       d.dname AS department,
       d.loc   AS location
FROM emp e
LEFT JOIN emp m ON e.mgr = m.empno
JOIN dept d ON e.deptno = d.deptno
ORDER BY e.ename;

-- 4.2 Cross-department salary comparison
SELECT e.ename, e.sal, d.dname,
       RANK() OVER (PARTITION BY e.deptno ORDER BY e.sal DESC) AS dept_rank,
       RANK() OVER (ORDER BY e.sal DESC) AS overall_rank
FROM emp e
JOIN dept d ON e.deptno = d.deptno
ORDER BY overall_rank;

-- 4.3 Find all employees and their colleagues in the same department
SELECT e1.ename AS employee, e2.ename AS colleague, d.dname
FROM emp e1
JOIN emp e2 ON e1.deptno = e2.deptno AND e1.empno < e2.empno
JOIN dept d ON e1.deptno = d.deptno
ORDER BY d.dname, e1.ename;

-- ============================================================================
-- SECTION 5: Date and Time Questions
-- ============================================================================

-- 5.1 Calculate years of service (as of today)
SELECT ename, hiredate,
       FLOOR(MONTHS_BETWEEN(SYSDATE, hiredate) / 12) AS years_employed,
       FLOOR(MOD(MONTHS_BETWEEN(SYSDATE, hiredate), 12)) AS months_employed,
       hiredate + (365 * 30) AS retirement_estimate
FROM emp
ORDER BY hiredate;

-- 5.2 Find employees hired in each year
SELECT EXTRACT(YEAR FROM hiredate) AS hire_year,
       COUNT(*) AS hires,
       LISTAGG(ename, ', ') WITHIN GROUP (ORDER BY hiredate) AS employees
FROM emp
GROUP BY EXTRACT(YEAR FROM hiredate)
ORDER BY hire_year;

-- 5.3 Find the most recent hire in each department
SELECT d.dname, e.ename, e.hiredate
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.hiredate = (SELECT MAX(hiredate) FROM emp WHERE deptno = e.deptno)
ORDER BY d.dname;

-- ============================================================================
-- SECTION 6: Oracle-Specific Solutions
-- ============================================================================

-- 6.1 Hierarchical query (organization chart) using CONNECT BY
SELECT LEVEL,
       LPAD(' ', (LEVEL - 1) * 3, ' ') || ename AS org_chart,
       empno, mgr, job,
       CONNECT_BY_ISCYCLE AS is_cycle,
       SYS_CONNECT_BY_PATH(ename, ' / ') AS org_path
FROM emp
START WITH mgr IS NULL
CONNECT BY NOCYCLE PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 6.2 PIVOT: salary distribution by job and department
SELECT * FROM (
    SELECT d.dname AS department, e.job, e.sal
    FROM emp e
    JOIN dept d ON e.deptno = d.deptno
)
PIVOT (
    AVG(sal) AS avg_salary,
    COUNT(*) AS emp_count
    FOR job IN (
        'ANALYST'    AS analyst,
        'CLERK'      AS clerk,
        'MANAGER'    AS manager,
        'PRESIDENT'  AS president,
        'SALESMAN'   AS salesman
    )
)
ORDER BY department;

-- 6.3 MERGE (upsert) example for department statistics
MERGE INTO dept_stats t
USING (
    SELECT d.deptno, d.dname,
           COUNT(e.empno) AS emp_count,
           AVG(e.sal) AS avg_salary
    FROM dept d
    LEFT JOIN emp e ON d.deptno = e.deptno
    GROUP BY d.deptno, d.dname
) s
ON (t.deptno = s.deptno)
WHEN MATCHED THEN
    UPDATE SET t.emp_count = s.emp_count, t.avg_salary = s.avg_salary, t.updated_at = SYSDATE
WHEN NOT MATCHED THEN
    INSERT (deptno, dname, emp_count, avg_salary, updated_at)
    VALUES (s.deptno, s.dname, s.emp_count, s.avg_salary, SYSDATE);

-- 6.4 MODEL clause for salary projections
SELECT ename, sal AS current_salary,
       s[0] AS projected_salary,
       s[1] AS bonus
FROM emp
MODEL
    DIMENSION BY (ROW_NUMBER() OVER (ORDER BY empno) AS rn)
    MEASURES (sal, 0 AS s, 0 AS bonus)
    RULES (
        s[ANY] = sal[CV()] * 1.10,     -- 10% raise
        bonus[ANY] = sal[CV()] * 0.05   -- 5% bonus
    )
ORDER BY ename;

-- ============================================================================
-- SECTION 7: Correlated Subquery Patterns
-- ============================================================================

-- 7.1 Employees who earn more than the average salary of their department
SELECT e.ename, e.sal, d.dname,
       ROUND((SELECT AVG(sal) FROM emp WHERE deptno = e.deptno), 2) AS dept_avg
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.sal > (SELECT AVG(sal) FROM emp WHERE deptno = e.deptno)
ORDER BY (e.sal - (SELECT AVG(sal) FROM emp WHERE deptno = e.deptno)) DESC;

-- 7.2 Departments without any employees
SELECT deptno, dname
FROM dept d
WHERE NOT EXISTS (SELECT 1 FROM emp e WHERE e.deptno = d.deptno);

-- 7.3 Find employees with the same job as 'JONES'
SELECT ename, job, sal
FROM emp
WHERE job = (SELECT job FROM emp WHERE ename = 'JONES')
  AND ename != 'JONES'
ORDER BY sal DESC;

-- ============================================================================
-- SECTION 8: Analytical Functions
-- ============================================================================

-- 8.1 Running total of salaries by hire date
SELECT hiredate, ename, sal,
       SUM(sal) OVER (ORDER BY hiredate) AS running_total,
       AVG(sal) OVER (ORDER BY hiredate ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_avg
FROM emp
ORDER BY hiredate;

-- 8.2 Lag and Lead: compare salary with previous and next hire
SELECT ename, hiredate, sal,
       LAG(sal, 1, 0) OVER (ORDER BY hiredate) AS prev_salary,
       LEAD(sal, 1, 0) OVER (ORDER BY hiredate) AS next_salary,
       sal - LAG(sal, 1, 0) OVER (ORDER BY hiredate) AS diff_from_prev
FROM emp
ORDER BY hiredate;

-- 8.3 First and last values in department
SELECT d.dname, e.ename, e.sal,
       FIRST_VALUE(e.ename) OVER (PARTITION BY e.deptno ORDER BY e.sal DESC) AS highest_paid,
       LAST_VALUE(e.ename) OVER (PARTITION BY e.deptno ORDER BY e.sal DESC
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS lowest_paid
FROM emp e
JOIN dept d ON e.deptno = d.deptno
ORDER BY d.dname, e.sal DESC;

-- ============================================================================
-- SECTION 9: Performance-Focused Queries
-- ============================================================================

-- 9.1 Find missing indexes (tables with many rows but no indexes)
SELECT table_name, num_rows, last_analyzed
FROM all_tables
WHERE owner = 'SCOTT'
  AND num_rows > 1000
  AND NOT EXISTS (
      SELECT 1 FROM all_indexes
      WHERE table_name = all_tables.table_name
        AND table_owner = all_tables.owner
  )
ORDER BY num_rows DESC;

-- 9.2 Identify inefficient queries using execution plan
EXPLAIN PLAN FOR
SELECT e.ename, e.sal, d.dname
FROM emp e
JOIN dept d ON e.deptno = d.deptno
WHERE e.sal > (SELECT AVG(sal) FROM emp)
ORDER BY e.sal DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- 9.3 Query with bind variables (APEX best practice)
-- In APEX, always use bind variables:
-- SELECT * FROM emp WHERE deptno = :P101_DEPTNO

-- ============================================================================
-- END OF EMP/DEPT QUERIES
-- ============================================================================
