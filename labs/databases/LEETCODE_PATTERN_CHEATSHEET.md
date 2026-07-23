# Oracle Database — LeetCode Pattern Cheatsheet

<div align="center">

![SQL](https://img.shields.io/badge/LeetCode_SQL_Patterns-FFA116?style=for-the-badge&logo=leetcode&logoColor=black)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)

**Pattern-based reference for LeetCode database problems (LC 175–600+) — Oracle SQL focus**

</div>

---

## Table of Contents

1. [SELECT Query Patterns](#1-select-query-patterns)
2. [Filtering Patterns](#2-filtering-patterns)
3. [Aggregation Patterns](#3-aggregation-patterns)
4. [Window Function Patterns](#4-window-function-patterns)
5. [CTE / Recursive Patterns](#5-cte--recursive-patterns)
6. [Subquery Patterns](#6-subquery-patterns)
7. [String Patterns](#7-string-patterns)
8. [Date / Time Patterns](#8-date--time-patterns)
9. [Performance Patterns](#9-performance-patterns)
10. [PL/SQL Patterns](#10-plsql-patterns)
11. [Oracle-Specific Patterns](#11-oracle-specific-patterns)

---

## 1. SELECT Query Patterns

### Basic SELECT

```sql
SELECT column1, column2
FROM   table_name
WHERE  condition;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 175 | Combine Two Tables | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 181 | Employees Earning More Than Managers | Easy | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |
| 182 | Duplicate Emails | Easy | ★★★ | ★ | ★★ | ★ | ★ | ★ |
| 183 | Customers Who Never Order | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 196 | Delete Duplicate Emails | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 197 | Rising Temperature | Easy | ★★ | ★★ | ★★★ | ★★ | ★★ | ★ |

### INNER JOIN

```sql
SELECT a.col, b.col
FROM   table_a a
JOIN   table_b b ON a.key = b.key;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 181 | Employees Earning More Than Managers | Easy | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |

### LEFT / RIGHT JOIN

```sql
SELECT a.col, b.col
FROM   table_a a
LEFT JOIN table_b b ON a.key = b.key;
-- RIGHT JOIN is the reverse; use LEFT JOIN only for clarity
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 175 | Combine Two Tables | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 183 | Customers Who Never Order | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 577 | Employee Bonus | Easy | ★★ | ★ | ★★ | ★★ | ★ | ★ |
| 586 | Customer Placing the Largest Number of Orders | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |

### FULL OUTER JOIN

```sql
SELECT a.col, b.col
FROM   table_a a
FULL OUTER JOIN table_b b ON a.key = b.key
WHERE  a.key IS NULL OR b.key IS NULL;  -- anti-join for unmatched
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 1355 | Activity Participants | Medium | ★★ | ★ | ★ | ★ | ★ | ★ |

### CROSS JOIN

```sql
SELECT a.col, b.col
FROM   table_a a
CROSS JOIN table_b b;
-- Generates Cartesian product (every row in a × every row in b)
```

### SELF JOIN

```sql
SELECT a.col, b.col
FROM   table_a a
JOIN   table_a b ON a.parent_id = b.id;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 181 | Employees Earning More Than Managers | Easy | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 612 | Shortest Distance in a Plane | Medium | ★★ | ★ | ★ | ★ | ★ | ★ |
| 614 | Second Degree Follower | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |

### NATURAL JOIN

```sql
SELECT col1, col2
FROM   table_a
NATURAL JOIN table_b;
-- Oracle joins on columns with identical names; risky in production
```

### Set Operations

```sql
-- UNION (deduplicated)
SELECT col FROM table_a
UNION
SELECT col FROM table_b;

-- UNION ALL (w/ duplicates)
SELECT col FROM table_a
UNION ALL
SELECT col FROM table_b;

-- INTERSECT
SELECT col FROM table_a
INTERSECT
SELECT col FROM table_b;

-- MINUS (Oracle) / EXCEPT (ANSI)
SELECT col FROM table_a
MINUS
SELECT col FROM table_b;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 182 | Duplicate Emails | Easy | ★★★ | ★ | ★★ | ★ | ★ | ★ |
| 1965 | Employees With Missing Information | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |

**Oracle variation:** Uses `MINUS` instead of `EXCEPT`. Duplicate handling is the same.

---

## 2. Filtering Patterns

### WHERE Clause

```sql
SELECT col
FROM   table
WHERE  col1 = value
  AND  col2 IN (set)
  AND  col3 BETWEEN low AND high
  AND  col4 IS NOT NULL;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 182 | Duplicate Emails | Easy | ★★★ | ★ | ★★ | ★ | ★ | ★ |
| 183 | Customers Who Never Order | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 196 | Delete Duplicate Emails | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 197 | Rising Temperature | Easy | ★★ | ★★ | ★★★ | ★★ | ★★ | ★ |
| 586 | Customer Placing the Largest Number of Orders | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 595 | Big Countries | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 596 | Classes More Than 5 Students | Easy | ★★ | ★★ | ★★ | ★★ | ★ | ★ |
| 597 | Friend Requests I: Overall Acceptance Rate | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 607 | Sales Person | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |

### HAVING Clause

```sql
SELECT col, COUNT(*), SUM(amount)
FROM   table
GROUP BY col
HAVING COUNT(*) > 5
   AND SUM(amount) > 1000;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 182 | Duplicate Emails | Easy | ★★★ | ★ | ★★ | ★ | ★ | ★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 596 | Classes More Than 5 Students | Easy | ★★ | ★★ | ★★ | ★★ | ★ | ★ |
| 619 | Biggest Single Number | Easy | ★★ | ★ | ★ | ★ | ★ | ★ |
| 1045 | Customers Who Bought All Products | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |

### CASE Expression

```sql
SELECT col,
       CASE WHEN condition1 THEN 'A'
            WHEN condition2 THEN 'B'
            ELSE 'C'
       END AS category
FROM   table;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 608 | Tree Node | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 618 | Students Report By Geography | Hard | ★★ | ★ | ★★ | ★★ | ★ | ★ |
| 626 | Exchange Seats | Medium | ★★ | ★ | ★★ | ★ | ★★ | ★ |
| 627 | Swap Salary | Easy | ★★ | ★ | ★ | ★ | ★ | ★ |
| 1393 | Capital Gain/Loss | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |

**Oracle variation:** `DECODE` is a more concise alternative:

```sql
SELECT col,
       DECODE(col, 'A', 'Category A',
                    'B', 'Category B',
                    'Unknown') AS category
FROM   table;
```

### COALESCE / NVL

```sql
SELECT COALESCE(col, default_value) FROM table;   -- ANSI
SELECT NVL(col, default_value) FROM table;         -- Oracle
SELECT NVL2(col, not_null_val, null_val) FROM table; -- Oracle 3-arg
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 175 | Combine Two Tables | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 577 | Employee Bonus | Easy | ★★ | ★ | ★★ | ★★ | ★ | ★ |
| 610 | Triangle Judgement | Easy | ★ | ★ | ★ | ★ | ★ | ★ |

### NULL Handling Patterns

```sql
-- IS NULL / IS NOT NULL (never use = NULL)
SELECT * FROM table WHERE col IS NULL;

-- NULL-safe comparison
SELECT * FROM table WHERE col1 IS NOT DISTINCT FROM col2;  -- ANSI
SELECT * FROM table WHERE (col1 = col2 OR (col1 IS NULL AND col2 IS NULL));  -- Portable

-- NULL in ORDER BY
SELECT * FROM table ORDER BY col NULLS LAST;   -- Oracle default
SELECT * FROM table ORDER BY col NULLS FIRST;
```

---

## 3. Aggregation Patterns

### GROUP BY with Multiple Aggregates

```sql
SELECT col1,
       COUNT(*)        AS row_count,
       COUNT(DISTINCT col2) AS unique_count,
       SUM(amount)     AS total,
       AVG(amount)     AS average,
       MIN(amount)     AS minimum,
       MAX(amount)     AS maximum,
       MEDIAN(amount)  AS median         -- Oracle only
FROM   table
GROUP BY col1;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 511 | Game Play Analysis I | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 571 | Find Median Given Frequency of Numbers | Hard | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 586 | Customer Placing the Largest Number of Orders | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 596 | Classes More Than 5 Students | Easy | ★★ | ★★ | ★★ | ★★ | ★ | ★ |
| 1045 | Customers Who Bought All Products | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |

### ROLLUP, CUBE, GROUPING SETS

```sql
-- ROLLUP — hierarchical subtotals (dept → dept+job → grand total)
SELECT department_id, job_id, SUM(salary)
FROM   employees
GROUP BY ROLLUP(department_id, job_id);

-- CUBE — all combinations of subtotals
SELECT department_id, job_id, SUM(salary)
FROM   employees
GROUP BY CUBE(department_id, job_id);

-- GROUPING SETS — custom subtotal combinations
SELECT department_id, job_id, SUM(salary)
FROM   employees
GROUP BY GROUPING SETS(
  (department_id, job_id),
  (department_id),
  (job_id),
  ()
);
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 1440 | Evaluate Boolean Expression | Medium | ★ | ★ | ★ | ★ | ★ | ★ |

### PIVOT / UNPIVOT

```sql
-- PIVOT (Oracle 11g+)
SELECT *
FROM   (SELECT department_id, job_id, salary FROM employees)
PIVOT  (SUM(salary) FOR job_id IN ('IT_PROG' AS it, 'SA_REP' AS sa, 'MK_REP' AS mk));

-- UNPIVOT — columns back to rows
SELECT id, metric, value
FROM   sales_data
UNPIVOT (value FOR metric IN (q1 AS 'Q1', q2 AS 'Q2', q3 AS 'Q3', q4 AS 'Q4'));
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 618 | Students Report By Geography | Hard | ★★ | ★ | ★★ | ★★ | ★ | ★ |
| 1179 | Reformat Department Table | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |

---

## 4. Window Function Patterns

### ROW_NUMBER, RANK, DENSE_RANK

```sql
SELECT col,
       ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) AS rn,
       RANK()       OVER (PARTITION BY dept ORDER BY salary DESC) AS rk,
       DENSE_RANK() OVER (PARTITION BY dept ORDER BY salary DESC) AS dr
FROM   employees;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 177 | Nth Highest Salary | Medium | ★★★ | ★★★ | ★★★ | ★★ | ★★ | ★★ |
| 178 | Rank Scores | Medium | ★★ | ★★ | ★★ | ★★ | ★★ | ★★ |
| 184 | Department Highest Salary | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 185 | Department Top Three Salaries | Hard | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |
| 512 | Game Play Analysis II | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 534 | Game Play Analysis III | Medium | ★ | ★ | ★ | ★ | ★ | ★ |
| 550 | Game Play Analysis IV | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |
| 1070 | Product Sales Analysis III | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |

### NTILE

```sql
SELECT col,
       NTILE(4) OVER (ORDER BY salary DESC) AS quartile
FROM   employees;
-- Divides rows into N buckets (useful for histograms / top-percentile)
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 1917 | Leetcodify Friends Recommendations | Hard | ★ | ★ | ★ | ★ | ★ | ★ |

### LAG / LEAD

```sql
SELECT col,
       LAG(salary, 1, 0)  OVER (PARTITION BY dept ORDER BY hire_date) AS prev_salary,
       LEAD(salary, 1, 0) OVER (PARTITION BY dept ORDER BY hire_date) AS next_salary
FROM   employees;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 197 | Rising Temperature | Easy | ★★ | ★★ | ★★★ | ★★ | ★★ | ★ |
| 180 | Consecutive Numbers | Medium | ★★ | ★★ | ★★ | ★★ | ★ | ★★ |
| 602 | Friend Requests II: Who Has the Most Friends | Medium | ★ | ★ | ★ | ★ | ★ | ★ |
| 603 | Consecutive Available Seats | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 1321 | Restaurant Growth | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |

### FIRST_VALUE / LAST_VALUE

```sql
SELECT col,
       FIRST_VALUE(salary) OVER (PARTITION BY dept ORDER BY hire_date) AS first_salary,
       LAST_VALUE(salary)  OVER (PARTITION BY dept ORDER BY hire_date
                                 RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS last_salary
FROM   employees;
-- LAST_VALUE needs explicit frame to avoid defaulting to current row
```

### SUM / AVG OVER (Running Total / Moving Average)

```sql
-- Running total
SELECT date, amount,
       SUM(amount) OVER (ORDER BY date ROWS UNBOUNDED PRECEDING) AS running_total
FROM   sales;

-- Moving average (3-day)
SELECT date, amount,
       AVG(amount) OVER (ORDER BY date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS mov_avg_3d
FROM   sales;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 534 | Game Play Analysis III | Medium | ★ | ★ | ★ | ★ | ★ | ★ |
| 550 | Game Play Analysis IV | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |
| 1321 | Restaurant Growth | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |
| 1308 | Running Total for Different Genders | Medium | ★ | ★ | ★ | ★ | ★ | ★ |

### Window Frame Clauses

```sql
-- ROWS: physical offsets
SUM(amount) OVER (ORDER BY date ROWS BETWEEN 5 PRECEDING AND 1 FOLLOWING)

-- RANGE: logical offsets based on order-by value (same value = same bucket)
SUM(amount) OVER (ORDER BY date RANGE BETWEEN INTERVAL '7' DAY PRECEDING AND CURRENT ROW)

-- GROUPS: peer groups (Oracle 19c+)
SUM(amount) OVER (ORDER BY date GROUPS BETWEEN 1 PRECEDING AND 1 FOLLOWING)
```

---

## 5. CTE / Recursive Patterns

### CTE Basics (WITH Clause)

```sql
WITH department_stats AS (
    SELECT department_id,
           COUNT(*) AS emp_count,
           AVG(salary) AS avg_salary
    FROM   employees
    GROUP BY department_id
)
SELECT d.department_name, s.emp_count, s.avg_salary
FROM   departments d
JOIN   department_stats s ON d.department_id = s.department_id;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 184 | Department Highest Salary | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 185 | Department Top Three Salaries | Hard | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |
| 262 | Trips and Users | Hard | ★★ | ★★ | ★★ | ★★ | ★★ | ★★ |
| 569 | Median Employee Salary | Hard | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 571 | Find Median Given Frequency of Numbers | Hard | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 579 | Find Cumulative Salary of an Employee | Hard | ★★ | ★★ | ★★ | ★★ | ★ | ★ |
| 585 | Investments in 2016 | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |

### Recursive CTE — Hierarchies

```sql
WITH org_hierarchy (employee_id, manager_id, level, path) AS (
    -- Anchor: top-level managers
    SELECT employee_id, manager_id, 1,
           CAST(employee_id AS VARCHAR2(200)) AS path
    FROM   employees
    WHERE  manager_id IS NULL

    UNION ALL

    -- Recursive: employees reporting to managers
    SELECT e.employee_id, e.manager_id, oh.level + 1,
           oh.path || '->' || e.employee_id
    FROM   employees e
    JOIN   org_hierarchy oh ON e.manager_id = oh.employee_id
)
SELECT employee_id, level, path
FROM   org_hierarchy;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 608 | Tree Node | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 1270 | All People Report to the Given Manager | Medium | ★★ | ★ | ★ | ★ | ★ | ★ |
| 1384 | Total Sales Amount by Year | Hard | ★★ | ★ | ★ | ★ | ★ | ★ |

**Oracle alternative:** Use `CONNECT BY` (see Oracle-Specific Patterns).

### Multiple CTEs

```sql
WITH
cte1 AS (
    SELECT ... FROM table_a
),
cte2 AS (
    SELECT ... FROM table_b JOIN cte1 ON ...
),
final AS (
    SELECT ... FROM cte1 JOIN cte2 ON ...
)
SELECT * FROM final;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 262 | Trips and Users | Hard | ★★ | ★★ | ★★ | ★★ | ★★ | ★★ |
| 579 | Find Cumulative Salary of an Employee | Hard | ★★ | ★★ | ★★ | ★★ | ★ | ★ |

---

## 6. Subquery Patterns

### Non-Correlated Subquery

```sql
SELECT col FROM table
WHERE col IN (SELECT col FROM other_table WHERE condition);
-- Inner query runs once, independent of outer
```

### Correlated Subquery

```sql
SELECT e1.employee_id, e1.salary
FROM   employees e1
WHERE  e1.salary > (SELECT AVG(e2.salary)
                    FROM   employees e2
                    WHERE  e2.department_id = e1.department_id);
-- Inner query runs once per row in outer (can be slow)
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 184 | Department Highest Salary | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 585 | Investments in 2016 | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 597 | Friend Requests I: Overall Acceptance Rate | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 602 | Friend Requests II: Who Has the Most Friends | Medium | ★ | ★ | ★ | ★ | ★ | ★ |
| 607 | Sales Person | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 608 | Tree Node | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 626 | Exchange Seats | Medium | ★★ | ★ | ★★ | ★ | ★★ | ★ |

### EXISTS vs IN

```sql
-- EXISTS (better for large subqueries, stops on first match)
SELECT * FROM table_a a
WHERE EXISTS (SELECT 1 FROM table_b b WHERE b.key = a.key);

-- IN (evaluates entire subquery result set)
SELECT * FROM table_a
WHERE key IN (SELECT key FROM table_b);

-- NOT EXISTS vs NOT IN (NOT IN behaves unexpectedly with NULLs)
SELECT * FROM table_a a
WHERE NOT EXISTS (SELECT 1 FROM table_b b WHERE b.key = a.key);  -- Safe

SELECT * FROM table_a
WHERE key NOT IN (SELECT key FROM table_b WHERE key IS NOT NULL);  -- Explicit NULL handling
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 183 | Customers Who Never Order | Easy | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 262 | Trips and Users | Hard | ★★ | ★★ | ★★ | ★★ | ★★ | ★★ |
| 570 | Managers with at Least 5 Direct Reports | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★ |
| 1045 | Customers Who Bought All Products | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |
| 1070 | Product Sales Analysis III | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |

### Scalar Subqueries

```sql
SELECT employee_id,
       salary,
       (SELECT AVG(salary) FROM employees) AS company_avg,
       salary - (SELECT AVG(salary) FROM employees) AS diff_from_avg
FROM   employees;
-- Must return exactly one row and one column
```

### Derived Tables (Inline Views)

```sql
SELECT dept_stats.department_id, dept_stats.avg_salary
FROM   (SELECT department_id, AVG(salary) AS avg_salary
        FROM   employees
        GROUP BY department_id) dept_stats
WHERE  dept_stats.avg_salary > 5000;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 177 | Nth Highest Salary | Medium | ★★★ | ★★★ | ★★★ | ★★ | ★★ | ★★ |
| 184 | Department Highest Salary | Medium | ★★★ | ★★ | ★★★ | ★★ | ★ | ★★ |
| 185 | Department Top Three Salaries | Hard | ★★★ | ★★ | ★★★ | ★★ | ★★ | ★★ |

### Lateral Joins (Oracle LATERAL / CROSS APPLY)

```sql
-- Oracle LATERAL (12c+)
SELECT d.department_name, top_emp.*
FROM   departments d,
LATERAL (SELECT employee_id, salary
         FROM   employees e
         WHERE  e.department_id = d.department_id
         ORDER BY salary DESC
         FETCH FIRST 3 ROWS ONLY) top_emp;

-- Equivalent using CROSS APPLY (ANSI, Oracle 12c+)
SELECT d.department_name, top_emp.*
FROM   departments d
CROSS APPLY (SELECT employee_id, salary
             FROM   employees e
             WHERE  e.department_id = d.department_id
             ORDER BY salary DESC
             FETCH FIRST 3 ROWS ONLY) top_emp;
```

---

## 7. String Patterns

### String Functions

```sql
-- Oracle string functions
SELECT UPPER(col), LOWER(col), INITCAP(col),        -- case conversion
       LENGTH(col),                                  -- string length
       SUBSTR(col, 1, 10) AS substr,                 -- substring (1-indexed)
       INSTR(col, '@') AS at_position,               -- character position
       TRIM(col), LTRIM(col), RTRIM(col),            -- trim whitespace
       LPAD(col, 10, '0'), RPAD(col, 10, ' '),       -- padding
       REPLACE(col, 'old', 'new'),                   -- replace
       TRANSLATE(col, 'abc', '123'),                 -- character-level mapping
       CONCAT(col1, col2),                           -- concatenation
       col1 || col2                                  -- concatenation operator
FROM   table;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 1667 | Fix Names in a Table | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 1683 | Invalid Tweets | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 1741 | Find Total Time Spent by Each Employee | Easy | ★ | ★ | ★ | ★ | ★ | ★ |

### Pattern Matching / Regex

```sql
-- LIKE
SELECT * FROM table WHERE col LIKE 'A%';            -- starts with A
SELECT * FROM table WHERE col LIKE '%test%';        -- contains 'test'
SELECT * FROM table WHERE col LIKE '___';            -- exactly 3 chars

-- Oracle REGEXP_LIKE
SELECT * FROM table WHERE REGEXP_LIKE(col, '^[A-Z].*[0-9]$');

-- Oracle REGEXP_SUBSTR
SELECT REGEXP_SUBSTR(col, '[^,]+', 1, 2) FROM table;  -- 2nd CSV field

-- Oracle REGEXP_REPLACE
SELECT REGEXP_REPLACE(col, '[^0-9]', '') FROM table;   -- keep only digits

-- Oracle REGEXP_INSTR
SELECT REGEXP_INSTR(col, 'pattern') FROM table;
```

### String Aggregation

```sql
-- LISTAGG (Oracle 11gR2+) — preferred
SELECT department_id,
       LISTAGG(employee_name, ', ') WITHIN GROUP (ORDER BY hire_date) AS employees
FROM   employees
GROUP BY department_id;

-- WM_CONCAT (undocumented, deprecated)
SELECT department_id, WM_CONCAT(employee_name) FROM employees GROUP BY department_id;

-- COLLECT (SQL collection type)
SELECT department_id,
       CAST(COLLECT(employee_name) AS VARCHAR2_T) AS employees
FROM   employees
GROUP BY department_id;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 179 | Largest Number | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★★ |

---

## 8. Date / Time Patterns

### Date Formatting

```sql
SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS')         AS formatted,
       TO_CHAR(SYSDATE, 'MON DD, YYYY')                   AS text_date,
       TO_CHAR(SYSDATE, 'DAY')                            AS day_name,
       TO_CHAR(SYSDATE, 'Q')                              AS quarter,
       TO_CHAR(SYSDATE, 'IW')                             AS iso_week,
       EXTRACT(YEAR FROM SYSDATE)                         AS year,
       EXTRACT(MONTH FROM SYSDATE)                        AS month,
       EXTRACT(DAY FROM SYSDATE)                          AS day
FROM   dual;
```

### Date Arithmetic

```sql
-- Add/subtract days
SELECT SYSDATE + 7 AS next_week FROM dual;
SELECT SYSDATE - 30 AS last_month FROM dual;

-- Months between
SELECT MONTHS_BETWEEN(SYSDATE, hire_date) AS months_employed FROM employees;

-- Add months
SELECT ADD_MONTHS(SYSDATE, 6) AS six_months_ahead FROM dual;

-- Next day of week
SELECT NEXT_DAY(SYSDATE, 'FRIDAY') AS next_friday FROM dual;

-- Last day of month
SELECT LAST_DAY(SYSDATE) AS month_end FROM dual;

-- Truncate time portion
SELECT TRUNC(SYSDATE) AS today_midnight FROM dual;
SELECT TRUNC(SYSDATE, 'MM') AS month_start FROM dual;
SELECT TRUNC(SYSDATE, 'Q') AS quarter_start FROM dual;
SELECT TRUNC(SYSDATE, 'YYYY') AS year_start FROM dual;

-- Round date
SELECT ROUND(SYSDATE, 'MONTH') AS rounded_month FROM dual;
```

### Date Truncation Patterns

```sql
-- Group by day (truncate time)
SELECT TRUNC(transaction_date) AS day, SUM(amount)
FROM   sales
GROUP BY TRUNC(transaction_date)
ORDER BY day;

-- Group by month
SELECT TRUNC(transaction_date, 'MM') AS month, SUM(amount)
FROM   sales
GROUP BY TRUNC(transaction_date, 'MM')
ORDER BY month;

-- Group by week
SELECT TRUNC(transaction_date, 'IW') AS iso_week, SUM(amount)
FROM   sales
GROUP BY TRUNC(transaction_date, 'IW')
ORDER BY iso_week;
```

### Interval Patterns

```sql
-- INTERVAL data type
SELECT INTERVAL '3' MONTH AS three_months FROM dual;

-- Date + interval
SELECT SYSDATE + INTERVAL '7' DAY FROM dual;
SELECT SYSDATE + INTERVAL '1' HOUR FROM dual;
SELECT hire_date + INTERVAL '1' YEAR AS anniversary FROM employees;

-- Timestamp with timezone
SELECT SYSTIMESTAMP AT TIME ZONE 'US/Eastern' AS eastern_time FROM dual;
SELECT FROM_TZ(TIMESTAMP '2024-01-01 12:00:00', 'UTC') AT TIME ZONE 'Asia/Tokyo' AS tokyo_time FROM dual;
```

### AWR / ASH Date Queries

```sql
-- AWR snapshot range
SELECT snap_id, begin_interval_time, end_interval_time
FROM   dba_hist_snapshot
WHERE  begin_interval_time >= SYSDATE - 7
ORDER BY snap_id;

-- ASH active session history (last hour)
SELECT sample_time, session_id, sql_id, event, wait_class
FROM   v$active_session_history
WHERE  sample_time >= SYSDATE - 1/24
ORDER BY sample_time;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 197 | Rising Temperature | Easy | ★★ | ★★ | ★★★ | ★★ | ★★ | ★ |
| 511 | Game Play Analysis I | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 512 | Game Play Analysis II | Easy | ★ | ★ | ★ | ★ | ★ | ★ |
| 534 | Game Play Analysis III | Medium | ★ | ★ | ★ | ★ | ★ | ★ |
| 550 | Game Play Analysis IV | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |
| 571 | Find Median Given Frequency of Numbers | Hard | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 1321 | Restaurant Growth | Medium | ★★ | ★★ | ★★ | ★ | ★ | ★ |

---

## 9. Performance Patterns

### Index Usage Patterns

```sql
-- B-tree index (default)
CREATE INDEX idx_emp_dept ON employees(department_id);

-- Bitmap index (low cardinality columns)
CREATE BITMAP INDEX idx_emp_gender ON employees(gender);

-- Function-based index
CREATE INDEX idx_emp_upper_name ON employees(UPPER(last_name));
SELECT * FROM employees WHERE UPPER(last_name) = 'SMITH';  -- Uses index

-- Composite index (leading column matters)
CREATE INDEX idx_emp_dept_sal ON employees(department_id, salary);
-- WHERE department_id = 10 AND salary > 5000 → uses index
-- WHERE salary > 5000 → may NOT use index (leading column missing)

-- Invisible index (test before making visible)
CREATE INDEX idx_test ON employees(hire_date) INVISIBLE;

-- Index monitoring
ALTER INDEX idx_emp_dept MONITORING USAGE;
SELECT * FROM v$object_usage;
```

### Execution Plan Analysis

```sql
-- EXPLAIN PLAN (estimate)
EXPLAIN PLAN FOR SELECT * FROM employees WHERE department_id = 10;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Actual execution plan (Oracle 11g+)
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(sql_id => 'abc123', format => 'ALLSTATS LAST'));

-- SQL Monitor (Oracle 11gR2+)
SELECT * FROM TABLE(DBMS_SQL_MONITOR.REPORT_SQL_MONITOR(sql_id => 'abc123'));

-- Gather statistics for optimal plans
EXEC DBMS_STATS.GATHER_TABLE_STATS('HR', 'EMPLOYEES');

-- Key plan operations to watch for:
--   TABLE ACCESS FULL → missing index
--   TABLE ACCESS BY INDEX ROWID → index used, check clustered factor
--   NESTED LOOPS → good for small driving set
--   HASH JOIN → good for large data sets
--   SORT ORDER BY / SORT GROUP BY → consider indexes
```

### Hint Usage

```sql
-- Optimizer hints (Oracle-specific)
SELECT /*+ FULL(employees) */ * FROM employees WHERE department_id = 10;
SELECT /*+ INDEX(employees idx_emp_dept) */ * FROM employees WHERE department_id = 10;
SELECT /*+ LEADING(e d) USE_NL(d) */ * FROM employees e JOIN departments d ON e.dept_id = d.id;
SELECT /*+ PARALLEL(employees 4) */ * FROM employees;
SELECT /*+ NO_INDEX(employees idx_emp_dept) */ * FROM employees WHERE department_id = 10;
SELECT /*+ APPEND */ INTO target_table SELECT * FROM source_table;  -- Direct path insert

-- Gather plan hints from DBMS_XPLAN
-- /*+ GATHER_PLAN_STATISTICS */ then DBMS_XPLAN.DISPLAY_CURSOR
```

### Join Order Optimization

```sql
-- Leading hint controls join order
SELECT /*+ LEADING(d e) */ *
FROM   departments d
JOIN   employees e ON e.department_id = d.department_id;

-- Join method hints
SELECT /*+ USE_NL(e) */ * FROM dept d JOIN emp e ON e.dept_id = d.id;  -- Nested Loops
SELECT /*+ USE_HASH(e) */ * FROM dept d JOIN emp e ON e.dept_id = d.id; -- Hash Join
SELECT /*+ USE_MERGE(e) */ * FROM dept d JOIN emp e ON e.dept_id = d.id; -- Sort Merge
```

### Partition Pruning

```sql
-- Range partition
CREATE TABLE sales (
    sale_date DATE,
    amount NUMBER
)
PARTITION BY RANGE (sale_date) (
    PARTITION p2023q1 VALUES LESS THAN (DATE '2023-04-01'),
    PARTITION p2023q2 VALUES LESS THAN (DATE '2023-07-01'),
    PARTITION p_future VALUES LESS THAN (MAXVALUE)
);

-- Query that prunes partitions
SELECT * FROM sales WHERE sale_date BETWEEN DATE '2023-01-15' AND DATE '2023-03-15';
-- Only scans p2023q1

-- Check partition pruning
EXPLAIN PLAN FOR SELECT * FROM sales WHERE sale_date = DATE '2023-02-01';
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
-- Look for: PARTITION RANGE SINGLE / PARTITION RANGE ITERATOR

-- Partition-wise join
SELECT /*+ PQ_DISTRIBUTE(d NONE NONE) */ *  -- forces partition-wise join
FROM   sales s JOIN date_dim d ON s.sale_date = d.date;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 1965 | Employees With Missing Information | Easy | ★★ | ★ | ★★ | ★ | ★ | ★ |

---

## 10. PL/SQL Patterns

### Cursor FOR Loop

```sql
DECLARE
    CURSOR emp_cur IS
        SELECT employee_id, first_name, last_name
        FROM   employees
        WHERE  department_id = 50;
BEGIN
    FOR emp_rec IN emp_cur LOOP
        DBMS_OUTPUT.PUT_LINE(emp_rec.first_name || ' ' || emp_rec.last_name);
    END LOOP;
END;
```

### Explicit Cursor with Parameters

```sql
DECLARE
    CURSOR emp_by_dept (p_dept_id NUMBER) IS
        SELECT employee_id, salary FROM employees WHERE department_id = p_dept_id;
    v_emp_rec emp_by_dept%ROWTYPE;
BEGIN
    OPEN emp_by_dept(50);
    LOOP
        FETCH emp_by_dept INTO v_emp_rec;
        EXIT WHEN emp_by_dept%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_emp_rec.employee_id || ': ' || v_emp_rec.salary);
    END LOOP;
    CLOSE emp_by_dept;
END;
```

### BULK COLLECT

```sql
DECLARE
    TYPE emp_id_tab IS TABLE OF employees.employee_id%TYPE;
    TYPE emp_name_tab IS TABLE OF employees.last_name%TYPE;
    l_emp_ids   emp_id_tab;
    l_emp_names emp_name_tab;
BEGIN
    -- Bulk collect into index-by tables
    SELECT employee_id, last_name
    BULK COLLECT INTO l_emp_ids, l_emp_names
    FROM   employees
    WHERE  department_id = 50;

    -- Process in bulk
    FOR i IN 1 .. l_emp_ids.COUNT LOOP
        DBMS_OUTPUT.PUT_LINE(l_emp_ids(i) || ': ' || l_emp_names(i));
    END LOOP;
END;
```

### FORALL

```sql
DECLARE
    TYPE dept_id_tab IS TABLE OF departments.department_id%TYPE
        INDEX BY PLS_INTEGER;
    l_dept_ids dept_id_tab;
BEGIN
    l_dept_ids(1) := 10;
    l_dept_ids(2) := 20;
    l_dept_ids(3) := 30;

    FORALL i IN INDICES OF l_dept_ids
        UPDATE departments SET manager_id = 100
        WHERE  department_id = l_dept_ids(i);

    DBMS_OUTPUT.PUT_LINE(SQL%ROWCOUNT || ' rows updated');
END;
```

### Exception Handling

```sql
BEGIN
    INSERT INTO employees (employee_id, last_name, email, hire_date, job_id)
    VALUES (999, 'Test', 'test@example.com', SYSDATE, 'IT_PROG');
    COMMIT;

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Employee ID already exists');
        ROLLBACK;
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('No data found');
    WHEN TOO_MANY_ROWS THEN
        DBMS_OUTPUT.PUT_LINE('Query returned multiple rows');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        ROLLBACK;
        RAISE;
END;
```

### Autonomous Transactions

```sql
PROCEDURE log_error(p_message VARCHAR2) IS
    PRAGMA AUTONOMOUS_TRANSACTION;
BEGIN
    INSERT INTO error_log (log_date, message)
    VALUES (SYSTIMESTAMP, p_message);
    COMMIT;  -- Independent commit does not affect main transaction
END;
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 176 | Second Highest Salary | Medium | ★★ | ★★ | ★★ | ★ | ★★ | ★★ |
| 601 | Human Traffic of Stadium | Hard | ★★★ | ★★ | ★★ | ★ | ★ | ★ |

---

## 11. Oracle-Specific Patterns

### Hierarchical Queries (CONNECT BY)

```sql
-- Basic hierarchy
SELECT employee_id,
       manager_id,
       LEVEL,
       CONNECT_BY_ISLEAF AS is_leaf,
       CONNECT_BY_ISCYCLE AS is_cycle,
       SYS_CONNECT_BY_PATH(last_name, ' -> ') AS org_path
FROM   employees
START WITH manager_id IS NULL
CONNECT BY NOCYCLE PRIOR employee_id = manager_id
ORDER SIBLINGS BY last_name;

-- Top-down: CONNECT BY PRIOR child = parent
-- Bottom-up: CONNECT BY PRIOR parent = child
```

| LC # | Problem | Difficulty | Oracle | Google | Amazon | MS | Meta | Apple |
|------|---------|------------|--------|--------|--------|------|------|-------|
| 608 | Tree Node | Medium | ★★ | ★ | ★★ | ★ | ★ | ★ |
| 1270 | All People Report to the Given Manager | Medium | ★★ | ★ | ★ | ★ | ★ | ★ |

### Flashback Queries

```sql
-- Flashback Query — see data as of a past time
SELECT * FROM employees AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR);

-- Flashback Version Query — see all versions between two times
SELECT versions_startscn, versions_endscn,
       versions_operation, employee_id, salary
FROM   employees
VERSIONS BETWEEN TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' DAY) AND SYSTIMESTAMP;

-- Flashback Transaction Query
SELECT * FROM flashback_transaction_query WHERE table_name = 'EMPLOYEES';

-- Flashback Table
FLASHBACK TABLE employees TO TIMESTAMP (SYSTIMESTAMP - INTERVAL '30' MINUTE);
-- Requires undo retention and flashback privileges
```

### MERGE (UPSERT)

```sql
MERGE INTO target_table t
USING source_table s
ON (t.key = s.key)
WHEN MATCHED THEN
    UPDATE SET t.value = s.value,
               t.updated_date = SYSDATE
    WHERE t.value != s.value  -- conditional update
WHEN NOT MATCHED THEN
    INSERT (key, value, created_date)
    VALUES (s.key, s.value, SYSDATE);
```

### Multiset Operators

```sql
-- MULTISET UNION / INTERSECT / EXCEPT for nested table collections
SELECT d.department_name,
       CAST(COLLECT(e.last_name) AS VARCHAR2_T)
         MULTISET UNION
         CAST(COLLECT(m.last_name) AS VARCHAR2_T) AS all_names
FROM   departments d
LEFT JOIN employees e ON e.department_id = d.department_id
LEFT JOIN employees m ON m.employee_id = d.manager_id
GROUP BY d.department_name;
```

### XMLTABLE

```sql
SELECT xt.*
FROM   xml_data x,
XMLTABLE('/employees/employee'
         PASSING x.xml_content
         COLUMNS
            emp_id    NUMBER        PATH '@id',
            emp_name  VARCHAR2(100) PATH 'name',
            salary    NUMBER        PATH 'salary'
        ) xt;
```

### JSON_TABLE (Oracle 12c+)

```sql
SELECT jt.*
FROM   json_data j,
JSON_TABLE(j.json_doc, '$.employees[*]'
         COLUMNS
            emp_id    NUMBER        PATH '$.id',
            emp_name  VARCHAR2(100) PATH '$.name',
            salary    NUMBER        PATH '$.salary'
        ) jt;

-- JSON_VALUE, JSON_QUERY, JSON_EXISTS
SELECT JSON_VALUE(json_doc, '$.name') AS name
FROM   json_data
WHERE  JSON_EXISTS(json_doc, '$.employees[?(@.salary > 5000)]');
```

### FETCH FIRST / OFFSET (Oracle 12c+)

```sql
-- ANSI pagination (replaces ROWNUM)
SELECT employee_id, salary
FROM   employees
ORDER BY salary DESC
OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY;

-- WITH TIES
SELECT employee_id, salary
FROM   employees
ORDER BY salary DESC
FETCH FIRST 5 ROWS WITH TIES;
-- Includes ties in the 5th position
```

### MATCH_RECOGNIZE (Oracle 12c+)

```sql
-- Pattern matching across rows
SELECT *
FROM   stock_prices
MATCH_RECOGNIZE (
    PARTITION BY symbol
    ORDER BY trade_date
    MEASURES
        FIRST(price) AS start_price,
        LAST(price)  AS end_price,
        COUNT(*)     AS days_in_pattern
    PATTERN (up up+ down+)
    DEFINE
        up   AS price > PREV(price),
        down AS price < PREV(price)
);
-- Powerful for detecting stock patterns, log anomalies, etc.
```

---

## Appendix: Oracle-Specific Function Reference

| Function | Purpose | Example |
|----------|---------|---------|
| `DECODE` | Case-like expression | `DECODE(col, 'A', 1, 'B', 2, 0)` |
| `NVL` | NULL replacement (2-arg) | `NVL(col, 0)` |
| `NVL2` | NULL replacement (3-arg) | `NVL2(col, not_null, null_val)` |
| `NULLIF` | NULL if equal | `NULLIF(a, b)` — returns NULL if a=b else a |
| `COALESCE` | First non-NULL | `COALESCE(a, b, c, d)` |
| `LISTAGG` | String aggregation | `LISTAGG(col, ',') WITHIN GROUP (ORDER BY col)` |
| `LAG/LEAD` | Offset access | `LAG(sal, 1, 0) OVER (ORDER BY id)` |
| `RATIO_TO_REPORT` | Ratio within group | `RATIO_TO_REPORT(salary) OVER ()` |
| `CUME_DIST` | Cumulative distribution | `CUME_DIST() OVER (ORDER BY salary)` |
| `PERCENT_RANK` | Percent rank | `PERCENT_RANK() OVER (ORDER BY salary)` |
| `PERCENTILE_CONT` | Continuous percentile | `PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary)` |
| `PERCENTILE_DISC` | Discrete percentile | `PERCENTILE_DISC(0.5) WITHIN GROUP (ORDER BY salary)` |
| `WIDTH_BUCKET` | Equi-width histogram | `WIDTH_BUCKET(salary, 0, 10000, 10)` |

---

> **Pro Tip:** For each LeetCode problem, first write the ANSI SQL solution, then identify where Oracle syntax differs. Interviewers at Oracle and Oracle-centric roles will expect you to know both approaches. The most commonly asked Oracle-specific features in interviews are: `CONNECT BY`, `MERGE`, flashback queries, `LISTAGG`, `PIVOT`, and `MATCH_RECOGNIZE`.
