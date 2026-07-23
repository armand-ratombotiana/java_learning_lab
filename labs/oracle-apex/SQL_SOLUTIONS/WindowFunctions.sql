-- ============================================================================
-- WindowFunctions.sql
-- Oracle SQL Window Functions (Analytic Functions) for APEX interviews
-- Covers RANK, DENSE_RANK, ROW_NUMBER, LEAD, LAG, FIRST_VALUE, LAST_VALUE,
-- SUM/AVG/COUNT with OVER, NTILE, PERCENT_RANK, CUME_DIST
-- ============================================================================

-- ============================================================================
-- SECTION 1: Rank Functions
-- ============================================================================

-- 1.1 ROW_NUMBER: Unique sequential number per partition
SELECT ename, deptno, sal,
       ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY sal DESC) AS row_num
FROM emp
ORDER BY deptno, row_num;

-- 1.2 RANK: Ties get same rank, next rank skips
SELECT ename, deptno, sal,
       RANK() OVER (PARTITION BY deptno ORDER BY sal DESC) AS rank
FROM emp
ORDER BY deptno, rank;

-- 1.3 DENSE_RANK: Ties get same rank, no skipping
SELECT ename, deptno, sal,
       DENSE_RANK() OVER (PARTITION BY deptno ORDER BY sal DESC) AS dense_rank
FROM emp
ORDER BY deptno, dense_rank;

-- 1.4 Comparison of rank functions
SELECT ename, deptno, sal,
       ROW_NUMBER() OVER (ORDER BY sal DESC) AS row_num,
       RANK() OVER (ORDER BY sal DESC) AS rank,
       DENSE_RANK() OVER (ORDER BY sal DESC) AS dense_rank
FROM emp
ORDER BY sal DESC;

-- 1.5 Top-N per group using window function
SELECT ename, deptno, sal
FROM (
    SELECT ename, deptno, sal,
           ROW_NUMBER() OVER (PARTITION BY deptno ORDER BY sal DESC) AS rn
    FROM emp
)
WHERE rn <= 2
ORDER BY deptno, rn;

-- ============================================================================
-- SECTION 2: Aggregate Window Functions
-- ============================================================================

-- 2.1 Running total
SELECT hiredate, ename, sal,
       SUM(sal) OVER (ORDER BY hiredate) AS running_total
FROM emp
ORDER BY hiredate;

-- 2.2 Running total with department partition
SELECT deptno, ename, hiredate, sal,
       SUM(sal) OVER (PARTITION BY deptno ORDER BY hiredate) AS dept_running_total
FROM emp
ORDER BY deptno, hiredate;

-- 2.3 Moving average (3-row window)
SELECT ename, hiredate, sal,
       AVG(sal) OVER (ORDER BY hiredate ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_avg_3
FROM emp
ORDER BY hiredate;

-- 2.4 Moving average (1 year window)
SELECT ename, hiredate, sal,
       AVG(sal) OVER (ORDER BY hiredate RANGE BETWEEN INTERVAL '1' YEAR PRECEDING AND CURRENT ROW) AS moving_avg_year
FROM emp
ORDER BY hiredate;

-- 2.5 Count per department with overall total
SELECT deptno, ename, sal,
       COUNT(*) OVER (PARTITION BY deptno) AS dept_count,
       COUNT(*) OVER () AS total_count
FROM emp
ORDER BY deptno, ename;

-- 2.6 Department salary as percentage of total
SELECT ename, sal, deptno,
       ROUND(sal / SUM(sal) OVER (PARTITION BY deptno) * 100, 2) AS pct_of_dept,
       ROUND(sal / SUM(sal) OVER () * 100, 2) AS pct_of_total
FROM emp
ORDER BY deptno, sal DESC;

-- ============================================================================
-- SECTION 3: LEAD and LAG
-- ============================================================================

-- 3.1 Previous salary (LAG)
SELECT ename, hiredate, sal,
       LAG(sal, 1, 0) OVER (ORDER BY hiredate) AS prev_sal,
       sal - LAG(sal, 1, 0) OVER (ORDER BY hiredate) AS diff
FROM emp
ORDER BY hiredate;

-- 3.2 Next salary (LEAD)
SELECT ename, hiredate, sal,
       LEAD(sal, 1, 0) OVER (ORDER BY hiredate) AS next_sal,
       LEAD(sal, 1, sal) OVER (PARTITION BY deptno ORDER BY hiredate) AS next_dept_sal
FROM emp
ORDER BY hiredate;

-- 3.3 Compare with previous in same department
SELECT ename, deptno, sal,
       LAG(sal, 1, 0) OVER (PARTITION BY deptno ORDER BY sal DESC) AS prev_dept_sal,
       CASE
           WHEN LAG(sal, 1, 0) OVER (PARTITION BY deptno ORDER BY sal DESC) = 0 THEN 'HIGHEST'
           WHEN sal > LAG(sal, 1, 0) OVER (PARTITION BY deptno ORDER BY sal DESC) THEN 'UP'
           WHEN sal < LAG(sal, 1, 0) OVER (PARTITION BY deptno ORDER BY sal DESC) THEN 'DOWN'
           ELSE 'SAME'
       END AS trend
FROM emp
ORDER BY deptno, sal DESC;

-- 3.4 Find consecutive salary increases
WITH salary_changes AS (
    SELECT ename, hiredate, sal,
           LAG(sal) OVER (PARTITION BY ename ORDER BY hiredate) AS prev_sal
    FROM emp_salary_history
)
SELECT ename, hiredate, sal, prev_sal
FROM salary_changes
WHERE sal > NVL(prev_sal, 0)
ORDER BY ename, hiredate;

-- ============================================================================
-- SECTION 4: FIRST_VALUE and LAST_VALUE
-- ============================================================================

-- 4.1 Highest and lowest paid in each department
SELECT ename, deptno, sal,
       FIRST_VALUE(ename) OVER (PARTITION BY deptno ORDER BY sal DESC) AS highest_paid,
       LAST_VALUE(ename) OVER (PARTITION BY deptno ORDER BY sal DESC
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS lowest_paid
FROM emp
ORDER BY deptno, sal DESC;

-- 4.2 First and last hire in department
SELECT ename, deptno, hiredate,
       FIRST_VALUE(ename) OVER (PARTITION BY deptno ORDER BY hiredate) AS first_hire,
       LAST_VALUE(ename) OVER (PARTITION BY deptno ORDER BY hiredate
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS latest_hire
FROM emp
ORDER BY deptno, hiredate;

-- 4.3 NTH_VALUE: nth highest salary in department
SELECT ename, deptno, sal,
       NTH_VALUE(ename, 2) OVER (PARTITION BY deptno ORDER BY sal DESC
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS second_highest
FROM emp
ORDER BY deptno, sal DESC;

-- ============================================================================
-- SECTION 5: NTILE, PERCENT_RANK, CUME_DIST
-- ============================================================================

-- 5.1 NTILE: Divide into quartiles
SELECT ename, sal,
       NTILE(4) OVER (ORDER BY sal DESC) AS quartile,
       CASE NTILE(4) OVER (ORDER BY sal DESC)
           WHEN 1 THEN 'Top 25%'
           WHEN 2 THEN 'Upper Middle'
           WHEN 3 THEN 'Lower Middle'
           WHEN 4 THEN 'Bottom 25%'
       END AS salary_band
FROM emp
ORDER BY sal DESC;

-- 5.2 PERCENT_RANK: Relative rank (0 to 1)
SELECT ename, sal,
       ROUND(PERCENT_RANK() OVER (ORDER BY sal DESC), 4) AS pct_rank,
       ROUND(PERCENT_RANK() OVER (PARTITION BY deptno ORDER BY sal DESC), 4) AS dept_pct_rank
FROM emp
ORDER BY sal DESC;

-- 5.3 CUME_DIST: Cumulative distribution
SELECT ename, sal,
       ROUND(CUME_DIST() OVER (ORDER BY sal DESC), 4) AS cum_dist,
       ROUND(CUME_DIST() OVER (PARTITION BY deptno ORDER BY sal DESC), 4) AS dept_cum_dist
FROM emp
ORDER BY sal DESC;

-- 5.4 Salary bands using NTILE
WITH salary_bands AS (
    SELECT ename, sal,
           NTILE(10) OVER (ORDER BY sal) AS decile
    FROM emp
)
SELECT decile,
       MIN(sal) AS min_sal,
       MAX(sal) AS max_sal,
       COUNT(*) AS emp_count,
       LISTAGG(ename, ', ') WITHIN GROUP (ORDER BY ename) AS employees
FROM salary_bands
GROUP BY decile
ORDER BY decile;

-- ============================================================================
-- SECTION 6: Advanced Window Frame Clauses
-- ============================================================================

-- 6.1 ROWS vs RANGE vs GROUPS
-- ROWS: physical rows
-- RANGE: logical rows (same value = same range)
-- GROUPS: groups of rows with same ORDER BY value

SELECT ename, hiredate, sal,
       SUM(sal) OVER (ORDER BY sal ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS rows_window,
       SUM(sal) OVER (ORDER BY sal RANGE BETWEEN 50 PRECEDING AND 50 FOLLOWING) AS range_window
FROM emp
ORDER BY sal;

-- 6.2 Cumulative sum with different frame boundaries
SELECT ename, hiredate, sal,
       SUM(sal) OVER (ORDER BY hiredate) AS cumulative,
       SUM(sal) OVER (ORDER BY hiredate ROWS UNBOUNDED PRECEDING) AS rows_unbounded,
       SUM(sal) OVER (ORDER BY hiredate RANGE BETWEEN INTERVAL '6' MONTH PRECEDING AND CURRENT ROW) AS six_month_window
FROM emp
ORDER BY hiredate;

-- 6.3 Moving median (using PERCENTILE_CONT)
SELECT ename, hiredate, sal,
       PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY sal)
           OVER (ORDER BY hiredate ROWS BETWEEN 3 PRECEDING AND 3 FOLLOWING) AS moving_median
FROM emp
ORDER BY hiredate;

-- ============================================================================
-- SECTION 7: Common Interview-Focused Window Problems
-- ============================================================================

-- 7.1 LeetCode 178: Rank Scores
-- (Already solved in LeetCode SQL solutions)

-- 7.2 LeetCode 177: Nth Highest Salary
-- (Already solved in LeetCode SQL solutions)

-- 7.3 Find the department salary gap (difference between highest and lowest)
SELECT deptno,
       FIRST_VALUE(sal) OVER (PARTITION BY deptno ORDER BY sal DESC) AS highest_sal,
       LAST_VALUE(sal) OVER (PARTITION BY deptno ORDER BY sal DESC
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS lowest_sal,
       FIRST_VALUE(sal) OVER (PARTITION BY deptno ORDER BY sal DESC) -
       LAST_VALUE(sal) OVER (PARTITION BY deptno ORDER BY sal DESC
           RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS salary_gap
FROM emp
ORDER BY deptno, sal DESC;

-- 7.4 Find employees who earn more than their department's average
-- (Using window function instead of subquery)
SELECT ename, deptno, sal,
       AVG(sal) OVER (PARTITION BY deptno) AS dept_avg
FROM emp
WHERE sal > AVG(sal) OVER (PARTITION BY deptno)
ORDER BY deptno, sal DESC;

-- 7.5 Identify duplicate records (non-primary-key duplicates)
SELECT ename, job, deptno, COUNT(*) OVER (PARTITION BY ename, job, deptno) AS dup_count
FROM emp
WHERE COUNT(*) OVER (PARTITION BY ename, job, deptno) > 1
ORDER BY ename;

-- 7.6 Running count of hires per year
SELECT EXTRACT(YEAR FROM hiredate) AS hire_year,
       ename, hiredate,
       COUNT(*) OVER (PARTITION BY EXTRACT(YEAR FROM hiredate) ORDER BY hiredate) AS ytd_hires
FROM emp
ORDER BY hiredate;

-- 7.7 All employees with their department's salary range
SELECT ename, sal, deptno,
       MAX(sal) OVER (PARTITION BY deptno) - MIN(sal) OVER (PARTITION BY deptno) AS dept_range,
       RATIO_TO_REPORT(sal) OVER (PARTITION BY deptno) * 100 AS pct_contribution
FROM emp
ORDER BY deptno, sal DESC;

-- ============================================================================
-- SECTION 8: Conditional Logic with Window Functions
-- ============================================================================

-- 8.1 Cumulative sum of only high salaries (> 2500)
SELECT ename, sal,
       SUM(CASE WHEN sal > 2500 THEN sal ELSE 0 END) OVER (ORDER BY hiredate) AS cum_high_salaries,
       SUM(sal) OVER (ORDER BY hiredate) AS cum_all_salaries
FROM emp
ORDER BY hiredate;

-- 8.2 Count of managers vs non-managers over time
SELECT hiredate, ename, job,
       SUM(CASE WHEN job IN ('MANAGER', 'PRESIDENT') THEN 1 ELSE 0 END) OVER (ORDER BY hiredate) AS managers_so_far,
       SUM(CASE WHEN job NOT IN ('MANAGER', 'PRESIDENT') THEN 1 ELSE 0 END) OVER (ORDER BY hiredate) AS staff_so_far
FROM emp
ORDER BY hiredate;

-- ============================================================================
-- SECTION 9: Window Functions with Partitions and Filtering
-- ============================================================================

-- 9.1 Department stats with overall comparison
SELECT ename, deptno, sal,
       AVG(sal) OVER (PARTITION BY deptno) AS dept_avg,
       AVG(sal) OVER () AS overall_avg,
       CASE
           WHEN AVG(sal) OVER (PARTITION BY deptno) > AVG(sal) OVER () THEN 'ABOVE_AVERAGE'
           ELSE 'BELOW_AVERAGE'
       END AS dept_performance
FROM emp
ORDER BY deptno, sal DESC;

-- 9.2 Salary percentile within department
SELECT ename, deptno, sal,
       ROUND(PERCENT_RANK() OVER (PARTITION BY deptno ORDER BY sal), 2) AS salary_pct_rank
FROM emp
ORDER BY deptno, sal DESC;

-- ============================================================================
-- END OF WINDOW FUNCTIONS
-- ============================================================================
