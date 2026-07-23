# Oracle APEX — LeetCode Pattern Cheatsheet

<div align="center">

![SQL](https://img.shields.io/badge/SQL_Patterns-CC2927?style=for-the-badge&logo=leetcode&logoColor=white)
![PL/SQL](https://img.shields.io/badge/PL/SQL_Patterns-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Database Design](https://img.shields.io/badge/Database_Design-00758F?style=for-the-badge)

**SQL and database pattern cheatsheet for APEX interview preparation — mapped to LeetCode problems**

</div>

---

## Table of Contents

1. [SQL Query Patterns](#sql-query-patterns)
2. [PL/SQL Patterns](#plsql-patterns)
3. [Database Design Patterns](#database-design-patterns)
4. [Performance Tuning Patterns](#performance-tuning-patterns)
5. [LeetCode SQL Database Problems](#leetcode-sql-database-problems)
6. [Data Structure Patterns for APEX](#data-structure-patterns-for-apex)

---

## SQL Query Patterns

### JOIN Patterns

#### INNER JOIN
```sql
SELECT e.employee_id, e.last_name, d.department_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Relating two tables via FK | LC 175, 181, 197 | Oracle: H, Google: M, Amazon: H, MS: H | Use table aliases; prefer `JOIN` over `WHERE` comma joins |
| Filtering on both tables | LC 183 | Oracle: M, Amazon: M | Index FK columns in APEX apps |

#### LEFT JOIN / ANTI-JOIN
```sql
SELECT c.customer_id, c.name
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Find records in one table not in another | LC 183, 196 | Oracle: H, Google: M, Amazon: H, MS: H | NULL check is faster than `NOT EXISTS` for small sets |

#### Self-JOIN
```sql
SELECT e1.name AS employee, e2.name AS manager
FROM employees e1
JOIN employees e2 ON e1.manager_id = e2.employee_id;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Hierarchical/cross-row comparison | LC 181, 180, 197 | Oracle: H, Google: M, Amazon: H | Use `CONNECT BY` for deeper hierarchies in APEX |

#### CROSS JOIN
```sql
SELECT d.department_name, l.city
FROM departments d
CROSS JOIN locations l;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Generate all combinations, date ranges | LC 1661 | Google: L | Use sparingly; can cause Cartesian explosions |

### Subquery Patterns

#### Scalar Subquery
```sql
SELECT employee_id,
       salary,
       (SELECT AVG(salary) FROM employees) AS avg_salary
FROM employees;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Returning single aggregate alongside row data | LC 176, 197 | Oracle: H, Amazon: M | Avoid in WHERE for large tables |

#### Correlated Subquery
```sql
SELECT e.department_id, e.last_name, e.salary
FROM employees e
WHERE e.salary > (
    SELECT AVG(salary) FROM employees
    WHERE department_id = e.department_id
);
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Per-row comparison with aggregate | LC 185, 184 | Oracle: H, Google: H, Amazon: M, MS: M | Rewrite as window function for performance |

#### EXISTS / NOT EXISTS
```sql
SELECT d.department_name
FROM departments d
WHERE EXISTS (
    SELECT 1 FROM employees e
    WHERE e.department_id = d.department_id
    AND e.salary > 100000
);
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Existence check (stops at first match) | LC 183 | Oracle: H, Amazon: H | Faster than `IN` when subquery is large |

### CTE (Common Table Expression) Patterns

#### Simple CTE
```sql
WITH dept_avg AS (
    SELECT department_id, AVG(salary) AS avg_sal
    FROM employees
    GROUP BY department_id
)
SELECT e.last_name, e.salary, da.avg_sal
FROM employees e
JOIN dept_avg da ON e.department_id = da.department_id
WHERE e.salary > da.avg_sal;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Break complex queries into readable steps | LC 180, 185, 550 | Oracle: H, Google: H, Amazon: M | CTEs are optimization fences in Oracle |

#### Recursive CTE
```sql
WITH org_hierarchy(employee_id, manager_id, level, path) AS (
    SELECT employee_id, manager_id, 1, last_name
    FROM employees WHERE manager_id IS NULL
    UNION ALL
    SELECT e.employee_id, e.manager_id,
           oh.level + 1,
           oh.path || ' > ' || e.last_name
    FROM employees e
    JOIN org_hierarchy oh ON e.manager_id = oh.employee_id
)
SELECT * FROM org_hierarchy;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Hierarchical/bill-of-materials data | LC 1270, 2159 | Oracle: H, Google: M | Oracle also has `CONNECT BY`; CTE is ANSI standard |

### Window Function Patterns

#### RANK / DENSE_RANK / ROW_NUMBER
```sql
SELECT employee_id,
       department_id,
       salary,
       RANK()       OVER (PARTITION BY department_id ORDER BY salary DESC) AS rnk,
       DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS dense_rnk,
       ROW_NUMBER() OVER (PARTITION BY department_id ORDER BY salary DESC) AS row_num
FROM employees;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Top-N per group, leaderboards, ranking | LC 178, 184, 185, 603 | Oracle: H, Google: H, Amazon: H, MS: M | DENSE_RANK gives consecutive ranks; RANK skips |

#### LEAD / LAG
```sql
SELECT employee_id,
       salary,
       LAG(salary)  OVER (ORDER BY employee_id) AS prev_salary,
       LEAD(salary) OVER (ORDER BY employee_id) AS next_salary,
       salary - LAG(salary) OVER (ORDER BY employee_id) AS diff
FROM employees;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Compare consecutive rows, trend analysis | LC 180, 197, 601 | Oracle: H, Google: H, Amazon: M | Default offset is 1; specify `IGNORE NULLS` for sparse data |

#### FIRST_VALUE / LAST_VALUE
```sql
SELECT employee_id,
       department_id,
       salary,
       FIRST_VALUE(salary) OVER (
           PARTITION BY department_id
           ORDER BY salary DESC
           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
       ) AS highest_in_dept
FROM employees;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| First/last value within a window | — | Oracle: M | Always specify window frame for LAST_VALUE |

#### NTILE
```sql
SELECT employee_id, salary,
       NTILE(4) OVER (ORDER BY salary DESC) AS salary_quartile
FROM employees;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Percentile analysis, bucketing | — | Google: M | Useful for APEX dashboard quartile reports |

### Aggregate Patterns

#### GROUP BY + HAVING
```sql
SELECT department_id,
       COUNT(*) AS emp_count,
       AVG(salary) AS avg_salary,
       SUM(salary) AS total_salary,
       MIN(salary) AS min_salary,
       MAX(salary) AS max_salary
FROM employees
GROUP BY department_id
HAVING COUNT(*) > 5;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Summary reports, data validation | LC 182, 596, 619 | Oracle: H, Amazon: M, MS: M | HAVING filters after GROUP BY; WHERE before |

#### LISTAGG
```sql
SELECT department_id,
       LISTAGG(last_name, ', ') WITHIN GROUP (ORDER BY last_name) AS employees
FROM employees
GROUP BY department_id;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| CSV aggregation, report formatting | — | Oracle: H | Watch for 4000-byte limit; use ON OVERFLOW TRUNCATE in 12c+ |

#### PIVOT / UNPIVOT
```sql
SELECT *
FROM (
    SELECT department_id, job_id, salary
    FROM employees
)
PIVOT (
    AVG(salary) FOR job_id IN (
        'IT_PROG' AS avg_it,
        'SA_REP'  AS avg_sa,
        'ST_CLERK' AS avg_st
    )
);
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Cross-tabulation, matrix reports | — | Oracle: M | Great for APEX Interactive Grid pivot regions |

---

## PL/SQL Patterns

### Cursor Patterns

#### Explicit Cursor
```sql
DECLARE
    CURSOR emp_cur IS
        SELECT employee_id, salary FROM employees WHERE department_id = :P_DEPT;
    v_emp_id employees.employee_id%TYPE;
    v_salary employees.salary%TYPE;
BEGIN
    OPEN emp_cur;
    LOOP
        FETCH emp_cur INTO v_emp_id, v_salary;
        EXIT WHEN emp_cur%NOTFOUND;
        UPDATE bonuses SET amount = v_salary * 0.1
        WHERE employee_id = v_emp_id;
    END LOOP;
    CLOSE emp_cur;
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Row-by-row processing | — | Oracle: H | Avoid row-by-row; use BULK COLLECT for performance |

#### Cursor FOR Loop
```sql
BEGIN
    FOR rec IN (
        SELECT employee_id, salary FROM employees WHERE department_id = :P_DEPT
    ) LOOP
        UPDATE bonuses SET amount = rec.salary * 0.1
        WHERE employee_id = rec.employee_id;
    END LOOP;
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Simple row iteration | — | Oracle: H | Auto-opens, fetches, closes; use with LIMIT for large sets |

#### REF CURSOR (SYS_REFCURSOR)
```sql
FUNCTION get_employees(p_dept_id NUMBER) RETURN SYS_REFCURSOR IS
    v_cur SYS_REFCURSOR;
BEGIN
    OPEN v_cur FOR
        SELECT employee_id, last_name, salary
        FROM employees
        WHERE department_id = p_dept_id;
    RETURN v_cur;
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| APEX report source, returning result sets | — | Oracle: H | Used in APEX classic report PL/SQL function body sources |

### Exception Handling Patterns

#### WHEN OTHERS with Logging
```sql
BEGIN
    INSERT INTO orders(order_id, customer_id, total)
    VALUES(:P101_ORDER_ID, :P101_CUSTOMER, :P101_TOTAL);
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        apex_error.add_error(
            p_message => 'Order ID already exists',
            p_display_location => apex_error.c_inline_with_field_and_notif
        );
    WHEN OTHERS THEN
        apex_debug.message('Unexpected error: ' || SQLERRM);
        RAISE;
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| APEX page process error handling | — | Oracle: H | Use `apex_error` API in APEX; log to `apex_debug` |

#### PRAGMA EXCEPTION_INIT
```sql
DECLARE
    e_budget_exceeded EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_budget_exceeded, -20001);
BEGIN
    IF :P101_TOTAL > 100000 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Budget exceeded');
    END IF;
EXCEPTION
    WHEN e_budget_exceeded THEN
        apex_error.add_error(p_message => SQLERRM);
END;
```

### Bulk Collect Patterns

#### BULK COLLECT + FORALL
```sql
DECLARE
    TYPE emp_ids IS TABLE OF employees.employee_id%TYPE;
    l_ids emp_ids;
    CURSOR emp_cur IS SELECT employee_id FROM employees WHERE status = 'ACTIVE';
BEGIN
    OPEN emp_cur;
    LOOP
        FETCH emp_cur BULK COLLECT INTO l_ids LIMIT 1000;
        EXIT WHEN l_ids.COUNT = 0;
        FORALL i IN 1..l_ids.COUNT
            UPDATE employees SET last_updated = SYSDATE
            WHERE employee_id = l_ids(i);
        COMMIT;
    END LOOP;
    CLOSE emp_cur;
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Large-scale DML (10K+ rows) | — | Oracle: H | Use LIMIT clause; FORALL reduces context switches 1000x |

### Dynamic SQL Patterns

#### EXECUTE IMMEDIATE
```sql
DECLARE
    v_table_name VARCHAR2(30) := 'EMPLOYEES';
    v_sql VARCHAR2(4000);
    v_count NUMBER;
BEGIN
    v_sql := 'SELECT COUNT(*) FROM ' || DBMS_ASSERT.SQL_OBJECT_NAME(v_table_name);
    EXECUTE IMMEDIATE v_sql INTO v_count;
    DBMS_OUTPUT.PUT_LINE('Count: ' || v_count);
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Dynamic filtering, table-driven logic | — | Oracle: H | Always use `DBMS_ASSERT` to prevent SQL injection in dynamic SQL |

#### DBMS_SQL
```sql
DECLARE
    v_cursor NUMBER;
    v_col_cnt NUMBER;
    v_desc DBMS_SQL.DESC_TAB;
    v_sql VARCHAR2(4000) := 'SELECT * FROM employees WHERE rownum < 10';
BEGIN
    v_cursor := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(v_cursor, v_sql, DBMS_SQL.NATIVE);
    DBMS_SQL.DESCRIBE_COLUMNS(v_cursor, v_col_cnt, v_desc);
    -- Dynamic column handling
    DBMS_SQL.CLOSE_CURSOR(v_cursor);
END;
```

| When to Use | LeetCode Ref | Company Freq | Oracle Tip |
|-------------|-------------|--------------|------------|
| Unknown result structure at compile time | — | Oracle: M | Used internally by APEX for Interactive Grids |

---

## Database Design Patterns

### Normalization Patterns

| Normal Form | Rule | APEX Application Example |
|-------------|------|-------------------------|
| 1NF | Atomic columns, no repeating groups | Separate `phone1`, `phone2` into `phones` table |
| 2NF | Full functional dependency on PK | Remove partial dependencies: `order_date` doesn't belong in `order_items` |
| 3NF | No transitive dependencies | `customer_zip` → `customer_city` should be in `zip_codes` table |
| BCNF | Every determinant is a candidate key | Overlapping composite keys |

**Interview Tip:** "In APEX, denormalization is often acceptable for read-heavy Interactive Grids to avoid complex JOINs. Use materialized views for reporting."

### Indexing Strategies

```sql
-- B-Tree Index (default)
CREATE INDEX idx_emp_dept ON employees(department_id);

-- Composite Index (leading column matters)
CREATE INDEX idx_emp_dept_status ON employees(department_id, status);

-- Function-Based Index
CREATE INDEX idx_emp_upper_name ON employees(UPPER(last_name));

-- Bitmap Index (low cardinality)
CREATE BITMAP INDEX idx_emp_gender ON employees(gender);

-- Unique Index
CREATE UNIQUE INDEX idx_emp_email ON employees(email);

-- Partial Index (Oracle 19c+)
CREATE INDEX idx_emp_active ON employees(employee_id)
    WHERE status = 'ACTIVE';
```

| Pattern | When to Use | APEX Relevance |
|---------|-------------|----------------|
| B-Tree | OLTP, high cardinality | Default for PKs, FK columns in APEX apps |
| Composite | Queries filtering on multiple columns | Reports filtering by `dept_id` AND `status` |
| Function | Case-insensitive search | LOV search with `UPPER` |
| Bitmap | Low cardinality, data warehouse | Reporting cubes, star schemas |
| Partial | Large table, mostly inactive data | Archive tables in APEX |

### Partitioning Patterns

```sql
-- Range Partitioning (by date)
CREATE TABLE orders (
    order_id NUMBER, order_date DATE, customer_id NUMBER, total NUMBER
)
PARTITION BY RANGE (order_date) (
    PARTITION p_2024 VALUES LESS THAN (DATE '2025-01-01'),
    PARTITION p_2025 VALUES LESS THAN (DATE '2026-01-01'),
    PARTITION p_future VALUES LESS THAN (MAXVALUE)
);

-- List Partitioning (by region)
CREATE TABLE sales_data (
    id NUMBER, region VARCHAR2(20), amount NUMBER
)
PARTITION BY LIST (region) (
    PARTITION p_na VALUES ('NORTH_AMERICA'),
    PARTITION p_eu VALUES ('EUROPE'),
    PARTITION p_apac VALUES ('ASIA_PAC')
);

-- Hash Partitioning (even distribution)
CREATE TABLE audit_log (
    log_id NUMBER, log_date DATE, message VARCHAR2(4000)
)
PARTITION BY HASH (log_id) PARTITIONS 8;
```

| Pattern | Use Case | APEX Scenario |
|---------|----------|---------------|
| Range | Time-series data | Order history by month |
| List | Categorical data | Regional sales dashboards |
| Hash | Even data distribution | High-volume audit logs |
| Composite | Range + hash subpartition | Orders by year, subpartitioned by region |

---

## Performance Tuning Patterns

### Execution Plan Analysis

```sql
-- Generate execution plan
EXPLAIN PLAN FOR
    SELECT * FROM employees WHERE department_id = 50;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Actual execution plan with runtime stats
SELECT /*+ GATHER_PLAN_STATISTICS */ *
FROM employees WHERE department_id = 50;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(FORMAT => 'ALLSTATS LAST'));

-- Find slow SQL in APEX
SELECT sql_id, executions, elapsed_time, sql_text
FROM v$sql
WHERE module LIKE '%APEX%'
ORDER BY elapsed_time DESC
FETCH FIRST 10 ROWS ONLY;
```

| Pattern | When to Use | Oracle Tip |
|---------|-------------|------------|
| EXPLAIN PLAN | Development, understanding optimizer | First step in any performance investigation |
| DBMS_XPLAN | Actual execution verification | Use `ALLSTATS LAST` for real vs estimated row counts |
| V$SQL | Identifying problem queries in production | Filter by `module` for APEX queries |

### Hint Patterns

```sql
-- Optimizer mode
SELECT /*+ ALL_ROWS */ * FROM employees WHERE department_id = 50;
SELECT /*+ FIRST_ROWS(10) */ * FROM employees WHERE department_id = 50;

-- Access path hints
SELECT /*+ INDEX(employees idx_emp_dept) */ * FROM employees WHERE department_id = 50;
SELECT /*+ FULL(employees) */ * FROM employees WHERE department_id = 50;

-- JOIN hints
SELECT /*+ USE_HASH(e d) LEADING(e d) */ * FROM employees e JOIN departments d ON e.department_id = d.department_id;
SELECT /*+ USE_NL(e d) */ * FROM employees e JOIN departments d ON e.department_id = d.department_id;

-- Parallel hints
SELECT /*+ PARALLEL(employees 4) */ * FROM employees;

-- Result cache
SELECT /*+ RESULT_CACHE */ department_id, AVG(salary) FROM employees GROUP BY department_id;
```

| Hint | Use Case | APEX Context |
|------|----------|--------------|
| ALL_ROWS | Batch processing, reports | Export operations |
| FIRST_ROWS | Interactive queries, pagination | Interactive Grid pagination |
| INDEX | Force index usage | When optimizer chooses wrong plan |
| USE_HASH | Large table JOINs | Data warehouse reports |
| RESULT_CACHE | Frequently run static queries | Dashboard regions |

### Materialized View Patterns

```sql
-- Basic materialized view
CREATE MATERIALIZED VIEW mv_dept_salary
BUILD IMMEDIATE
REFRESH COMPLETE ON DEMAND
AS
SELECT d.department_name,
       COUNT(*) AS emp_count,
       AVG(e.salary) AS avg_salary,
       SUM(e.salary) AS total_salary
FROM employees e
JOIN departments d ON e.department_id = d.department_id
GROUP BY d.department_name;

-- Fast refresh materialized view (requires MV log)
CREATE MATERIALIZED VIEW LOG ON employees WITH ROWID, PRIMARY KEY
INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW mv_emp_summary
BUILD IMMEDIATE
REFRESH FAST ON COMMIT
AS
SELECT department_id, AVG(salary) AS avg_salary
FROM employees
GROUP BY department_id;

-- Query rewrite enabled
CREATE MATERIALIZED VIEW mv_fast_dashboard
REFRESH FAST ON DEMAND
ENABLE QUERY REWRITE
AS
SELECT /* ... */;
```

| Pattern | When to Use | APEX Scenario |
|---------|-------------|---------------|
| Complete Refresh | Small lookup tables, nightly batches | Reference data for LOVs |
| Fast Refresh (MV Log) | Large tables, frequent changes | Real-time dashboards |
| Query Rewrite | Transparent optimization | APEX reports with no schema changes |

---

## LeetCode SQL Database Problems

### Easy Problems

| Problem | Pattern | Difficulty | Company Freq | Related Lab |
|---------|---------|------------|-------------|-------------|
| 175. Combine Two Tables | LEFT JOIN | Easy | Oracle: H, Google: M, Amazon: H, MS: H | Lab 03 |
| 176. Second Highest Salary | Subquery, LIMIT/ROWNUM | Easy | Oracle: H, Amazon: M, MS: H | Lab 03 |
| 181. Employees Earning More Than Managers | Self-JOIN | Easy | Oracle: M, Amazon: H | Lab 03 |
| 182. Duplicate Emails | GROUP BY, HAVING | Easy | Oracle: M, Google: L, Amazon: M | Lab 03 |
| 183. Customers Who Never Order | LEFT JOIN, IS NULL | Easy | Oracle: H, Amazon: H, MS: M | Lab 03 |
| 196. Delete Duplicate Emails | Self-JOIN DELETE | Easy | Oracle: M, Amazon: L | Lab 03 |
| 197. Rising Temperature | Self-JOIN, DATEDIFF | Easy | Oracle: M, Google: M, Amazon: M | Lab 03 |
| 511. Game Play Analysis I | GROUP BY MIN | Easy | Oracle: L, Amazon: M | Lab 03 |
| 577. Employee Bonus | LEFT JOIN | Easy | Oracle: M, Amazon: M | Lab 03 |
| 584. Find Customer Referee | WHERE != / IS NULL | Easy | Oracle: L, MS: M | Lab 03 |
| 586. Customer Placing Largest Orders | GROUP BY, ORDER BY | Easy | Oracle: L, Amazon: L | Lab 04 |
| 595. Big Countries | WHERE, OR | Easy | Oracle: L, Amazon: M | Lab 03 |
| 596. Classes More Than 5 Students | GROUP BY, HAVING | Easy | Oracle: M, Google: M, Amazon: M | Lab 03 |
| 607. Sales Person | Multiple JOINs | Easy | Oracle: L, Amazon: M | Lab 03 |
| 610. Triangle Judgement | CASE WHEN | Easy | Oracle: L | Lab 03 |
| 619. Biggest Single Number | GROUP BY, HAVING | Easy | Oracle: M, Amazon: M | Lab 03 |
| 620. Not Boring Movies | WHERE, MOD | Easy | Oracle: L | Lab 03 |
| 1050. Actors and Directors | GROUP BY, HAVING | Easy | Oracle: L | Lab 03 |
| 1068. Product Sales Analysis I | JOIN | Easy | Oracle: L, Amazon: M | Lab 03 |
| 1084. Sales Analysis III | JOIN, GROUP BY | Easy | Oracle: M | Lab 03 |
| 1141. User Activity | GROUP BY, COUNT DISTINCT | Easy | Oracle: L, Google: M | Lab 04 |
| 1148. Article Views | DISTINCT, WHERE | Easy | Oracle: L, Google: L | Lab 03 |
| 1179. Reformat Department Table | PIVOT / CASE | Easy | Oracle: M, Google: M | Lab 04 |
| 1251. Average Selling Price | JOIN, GROUP BY | Easy | Oracle: M | Lab 03 |
| 1280. Students and Examinations | CROSS JOIN, LEFT JOIN | Easy | Oracle: H, Google: M | Lab 03 |
| 1327. List the Products Ordered | JOIN, GROUP BY | Easy | Oracle: L | Lab 04 |
| 1378. Replace Employee ID | LEFT JOIN | Easy | Oracle: L | Lab 03 |
| 1407. Top Travellers | LEFT JOIN, GROUP BY | Easy | Oracle: L | Lab 04 |
| 1484. Group Sold Products | GROUP BY, GROUP_CONCAT | Easy | Oracle: M | Lab 04 |
| 1517. Find Users With Valid E-Mails | REGEXP_LIKE | Easy | Oracle: L | Lab 03 |
| 1527. Patients With a Condition | LIKE, REGEXP | Easy | Oracle: L | Lab 03 |
| 1581. Customer Who Visited | LEFT JOIN, GROUP BY | Easy | Oracle: M, Amazon: M | Lab 04 |
| 1587. Bank Account Summary | JOIN, GROUP BY | Easy | Oracle: L | Lab 04 |
| 1633. Percentage of Users | JOIN, GROUP BY, COUNT | Easy | Oracle: M, Google: M | Lab 04 |
| 1661. Average Time Per Machine | JOIN | Easy | Oracle: M, Google: M | Lab 03 |
| 1667. Fix Names | String functions | Easy | Oracle: L | Lab 03 |
| 1683. Invalid Tweets | LENGTH, WHERE | Easy | Oracle: L | Lab 03 |
| 1693. Daily Leads | GROUP BY | Easy | Oracle: L | Lab 04 |
| 1729. Find Followers Count | GROUP BY | Easy | Oracle: L | Lab 04 |
| 1731. The Number of Employees | JOIN, GROUP BY | Easy | Oracle: M, Amazon: L | Lab 04 |
| 1741. Find Total Time | SUM, GROUP BY | Easy | Oracle: L | Lab 04 |
| 1757. Recyclable and Low Fat Products | WHERE | Easy | Oracle: L, Amazon: M | Lab 03 |
| 1795. Rearrange Products Table | UNPIVOT | Easy | Oracle: M | Lab 04 |
| 1873. Calculate Special Bonus | CASE WHEN | Easy | Oracle: L | Lab 03 |
| 1890. The Latest Login | GROUP BY MAX | Easy | Oracle: L, Google: L | Lab 04 |
| 1965. Employees With Missing Information | FULL OUTER JOIN | Easy | Oracle: M | Lab 03 |

### Medium Problems

| Problem | Pattern | Difficulty | Company Freq | Related Lab |
|---------|---------|------------|-------------|-------------|
| 177. Nth Highest Salary | Window function | Medium | Oracle: H, Google: M, Amazon: H, MS: H | Lab 03 |
| 178. Rank Scores | DENSE_RANK | Medium | Oracle: H, Amazon: H, MS: M | Lab 04 |
| 180. Consecutive Numbers | LEAD, Self-JOIN | Medium | Oracle: H, Google: M, Amazon: M | Lab 03 |
| 184. Department Highest Salary | Window function, JOIN | Medium | Oracle: H, Google: H, Amazon: H, MS: M | Lab 04 |
| 534. Game Play Analysis III | SUM OVER | Medium | Oracle: M, Google: M | Lab 04 |
| 550. Game Play Analysis IV | CTE, aggregation | Medium | Oracle: M, Google: H, Amazon: M | Lab 03 |
| 570. Managers With 5 Reports | JOIN, GROUP BY | Medium | Oracle: M, Amazon: M | Lab 04 |
| 571. Find Median Given Frequency | Window function, SUM | Hard-Medium | Oracle: M, Google: M | Lab 04 |
| 574. Winning Candidate | JOIN, GROUP BY | Medium | Oracle: M | Lab 04 |
| 578. Get Highest Answer Rate | CASE AVG | Medium | Oracle: M, Google: L | Lab 04 |
| 580. Count Student Number | LEFT JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 585. Investments in 2016 | Self-JOIN, aggregation | Medium | Oracle: M, Google: L | Lab 04 |
| 602. Friend Requests II | UNION ALL, GROUP BY | Medium | Oracle: L, Google: M | Lab 04 |
| 608. Tree Node | CASE, subquery | Medium | Oracle: M, Amazon: M | Lab 03 |
| 612. Shortest Distance | Self-JOIN, MIN | Medium | Oracle: L, Google: L | Lab 03 |
| 614. Second Degree Follower | JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 626. Exchange Seats | LEAD/LAG, CASE | Medium | Oracle: M, Google: M, Amazon: M | Lab 03 |
| 1045. Customers Who Bought All Products | GROUP BY HAVING COUNT | Medium | Oracle: M, Amazon: L | Lab 04 |
| 1070. Product Sales Analysis III | Window function ROW_NUMBER | Medium | Oracle: M, Google: M | Lab 04 |
| 1075. Project Employees I | JOIN, GROUP BY, AVG | Medium | Oracle: L | Lab 04 |
| 1077. Project Employees II | RANK | Medium | Oracle: L | Lab 04 |
| 1083. Sales Analysis II | JOIN, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1097. Game Play Analysis V | CTE, LEAD, aggregation | Hard-Medium | Oracle: L, Google: M | Lab 04 |
| 1107. New Users Daily Count | GROUP BY, date | Medium | Oracle: L, Google: L | Lab 04 |
| 1112. Highest Grade For Each Student | ROW_NUMBER | Medium | Oracle: M | Lab 04 |
| 1126. Active Businesses | HAVING COUNT | Medium | Oracle: L | Lab 04 |
| 1132. Reported Posts II | AVG, date filtering | Medium | Oracle: L | Lab 04 |
| 1142. User Activity Past 30 Days | GROUP BY, COUNT DISTINCT | Medium | Oracle: L | Lab 04 |
| 1149. Article Views II | DISTINCT, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1158. Market Analysis I | JOIN, CASE, GROUP BY | Medium | Oracle: M, Amazon: M | Lab 04 |
| 1164. Product Price at a Date | CTE, COALESCE, window | Medium | Oracle: H, Google: H | Lab 04 |
| 1173. Immediate Food Delivery I | CASE AVG | Medium | Oracle: L | Lab 04 |
| 1174. Immediate Food Delivery II | ROW_NUMBER, AVG | Medium | Oracle: M, Google: M | Lab 04 |
| 1193. Monthly Transactions | DATE_FORMAT, SUM, CASE | Medium | Oracle: M, Google: M | Lab 04 |
| 1204. Last Person to Fit in Bus | SUM OVER, ORDER BY | Medium | Oracle: M, Google: M, Amazon: M | Lab 03 |
| 1205. Monthly Transactions II | UNION ALL, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1211. Queries Quality | AVG, ROUND | Medium | Oracle: L | Lab 04 |
| 1212. Team Scores | UNION ALL, SUM | Medium | Oracle: L | Lab 04 |
| 1241. Number of Comments | LEFT JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1251. Average Selling Price | JOIN, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1264. Page Recommendations | JOIN, NOT IN, UNION | Medium | Oracle: L, Google: M | Lab 04 |
| 1270. All People Report to Manager | Recursive CTE | Medium | Oracle: H, Google: M | Lab 03 |
| 1285. Find Start and End of Ranges | ROW_NUMBER, GROUP BY | Medium | Oracle: M, Google: M | Lab 03 |
| 1303. Find Team Size | JOIN, COUNT | Medium | Oracle: L | Lab 04 |
| 1308. Running Total for Genders | SUM OVER | Medium | Oracle: M, Google: L | Lab 04 |
| 1321. Restaurant Growth | SUM OVER, AVG OVER | Medium | Oracle: H, Google: M | Lab 04 |
| 1341. Movie Rating | UNION ALL, RANK, LIMIT | Medium | Oracle: M, Google: M | Lab 04 |
| 1355. Activity Participants | GROUP BY, NTILE | Medium | Oracle: L | Lab 04 |
| 1364. Number of Trusted Contacts | LEFT JOIN, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1384. Total Sales Amount | PIVOT, GROUP BY | Hard-Medium | Oracle: M, Google: M | Lab 04 |
| 1393. Capital Gain/Loss | SUM, CASE | Medium | Oracle: M, Amazon: L | Lab 04 |
| 1398. Customers Who Bought Products | GROUP BY, HAVING, COUNT | Medium | Oracle: M, Amazon: L | Lab 04 |
| 1407. Top Travellers | LEFT JOIN, SUM | Medium | Oracle: L | Lab 04 |
| 1412. Find the Quiet Students | JOIN, NOT IN | Medium | Oracle: L | Lab 04 |
| 1421. NPV Queries | LEFT JOIN | Medium | Oracle: L | Lab 04 |
| 1435. Create a Session Bar Chart | CASE, GENERATE_SERIES | Medium | Oracle: M, Google: M | Lab 04 |
| 1440. Evaluate Boolean Expression | CASE, JOIN | Medium | Oracle: L | Lab 04 |
| 1445. Apples and Oranges | SUM, CASE | Medium | Oracle: M | Lab 04 |
| 1454. Active Users | CTE, ROW_NUMBER, DATE | Medium | Oracle: L, Google: M | Lab 04 |
| 1459. Rectangles Area | Self-JOIN | Medium | Oracle: L | Lab 03 |
| 1468. Calculate Salaries | CASE, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1479. Sales by Day | PIVOT, SUM | Medium | Oracle: M, Google: L | Lab 04 |
| 1485. Group Sold Products | GROUP_CONCAT | Medium | Oracle: M | Lab 04 |
| 1495. Friendly Movies | JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1501. Countries You Can Safely Invest In | JOIN, GROUP BY, AVG | Medium | Oracle: L, Google: L | Lab 04 |
| 1511. Customer Order Frequency | JOIN, GROUP BY, SUM | Medium | Oracle: M | Lab 04 |
| 1532. Most Recent Orders | ROW_NUMBER, GROUP BY | Medium | Oracle: M, Amazon: M | Lab 04 |
| 1543. Fix Product Name | String functions | Medium | Oracle: L | Lab 03 |
| 1549. Most Recent Orders | ROW_NUMBER | Medium | Oracle: M | Lab 04 |
| 1555. Bank Account Summary | CASE, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1565. Unique Orders and Customers | COUNT DISTINCT | Medium | Oracle: L | Lab 04 |
| 1571. Warehouse Manager | JOIN, SUM | Medium | Oracle: L | Lab 04 |
| 1587. Bank Account Summary | JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1596. Most Frequent Products | RANK, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1607. Sellers With No Sales | LEFT JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1613. Find IDs With At Least | CTE, NOT IN | Medium | Oracle: M, Google: L | Lab 03 |
| 1623. All Valid Triplets | JOIN, NOT IN | Medium | Oracle: L | Lab 03 |
| 1635. Hopper Company Queries | GENERATE_SERIES, CTE | Hard-Medium | Oracle: M, Google: M | Lab 04 |
| 1645. Hopper Company Queries II | CTE, date, aggregation | Hard-Medium | Oracle: M, Google: M | Lab 04 |
| 1651. Hopper Company Queries III | CTE, AVG | Hard-Medium | Oracle: M, Google: L | Lab 04 |
| 1699. Number of Calls | LEAST, GREATEST, GROUP BY | Medium | Oracle: L, Google: L | Lab 04 |
| 1709. Biggest Window | LEAD, DATEDIFF | Medium | Oracle: L, Google: M | Lab 03 |
| 1715. Count Apples and Oranges | SUM, COALESCE | Medium | Oracle: M | Lab 04 |
| 1725. Number of Rectangles | GROUP BY, COUNT | Medium | Oracle: L | Lab 04 |
| 1731. Employees Who Earn More | JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1747. Leetflex Banned Accounts | Self-JOIN, date overlap | Medium | Oracle: M, Google: M | Lab 03 |
| 1751. Maximum Path Quality | Recursive CTE, graph | Hard | Oracle: L, Google: M | Lab 03 |
| 1767. Find Subtasks | Recursive CTE | Hard | Oracle: M | Lab 03 |
| 1777. Product's Price for Each Store | PIVOT | Medium | Oracle: M | Lab 04 |
| 1783. Grand Slam Titles | UNION ALL, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1789. Primary Department | UNION, ROW_NUMBER | Medium | Oracle: M | Lab 04 |
| 1795. Rearrange Products | UNPIVOT | Medium | Oracle: M | Lab 04 |
| 1809. Ad-Free Sessions | LEFT JOIN, NOT EXISTS | Medium | Oracle: L | Lab 04 |
| 1811. Find Interview Candidates | JOIN, GROUP BY, HAVING | Medium | Oracle: L | Lab 04 |
| 1821. Find Customers With Positive Revenue | GROUP BY, SUM, HAVING | Medium | Oracle: L | Lab 04 |
| 1831. Maximum Transaction | ROW_NUMBER, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1841. League Statistics | SUM, CASE, GROUP BY | Medium | Oracle: M | Lab 04 |
| 1843. Suspicious Bank Accounts | JOIN, GROUP BY, AVG | Medium | Oracle: L | Lab 04 |
| 1853. Convert Date Format | TO_CHAR, date formatting | Medium | Oracle: L | Lab 03 |
| 1867. Orders With Max Quantity | RANK, ORDER BY | Medium | Oracle: L | Lab 04 |
| 1875. Group Employees | GROUP BY, CASE | Medium | Oracle: L | Lab 04 |
| 1892. Page Recommendations II | JOIN, NOT IN, UNION | Medium | Oracle: L, Google: M | Lab 04 |
| 1907. Count Salary Categories | UNION ALL, CASE | Medium | Oracle: M | Lab 04 |
| 1917. Leetflex Accounts Banned | Self-JOIN, date ranges | Medium | Oracle: L, Google: L | Lab 03 |
| 1919. Leetcodify Friends | Self-JOIN, GROUP BY | Hard | Oracle: L | Lab 03 |
| 1934. Confirmation Rate | LEFT JOIN, AVG, CASE | Medium | Oracle: M | Lab 04 |
| 1949. Strong Friendship | Self-JOIN, GROUP BY | Medium | Oracle: L | Lab 03 |
| 1951. All the Pairs | JOIN, GROUP BY | Medium | Oracle: L | Lab 04 |
| 1978. Employees Whose Manager Left | LEFT JOIN, IS NULL | Medium | Oracle: M | Lab 03 |
| 1988. Find Cutoff Score | GROUP BY, HAVING, RANK | Medium | Oracle: M | Lab 04 |
| 1990. Count the Number of Experiments | CROSS JOIN, LEFT JOIN | Medium | Oracle: M | Lab 04 |
| 2004. Number of Employees | GROUP BY, COUNT | Medium | Oracle: L | Lab 04 |
| 2010. Employee Salary Median | PERCENTILE_CONT | Hard | Oracle: M, Google: M | Lab 04 |
| 2020. Number of Accounts | CTE, LEAD | Medium | Oracle: L | Lab 03 |
| 2026. Low-Quality Problems | JOIN, GROUP BY, AVG | Medium | Oracle: L | Lab 04 |
| 2041. Accepted Candidates | JOIN, GROUP BY, HAVING | Medium | Oracle: L | Lab 04 |
| 2051. Category Statistics | PIVOT, aggregation | Hard | Oracle: L | Lab 04 |
| 2066. Account Balance | SUM OVER | Medium | Oracle: M | Lab 04 |
| 2072. The Winner University | COUNT, CASE | Medium | Oracle: L | Lab 04 |
| 2084. Drop Type 1 Orders | Anti-JOIN | Medium | Oracle: M | Lab 03 |
| 2112. Airport With Most Traffic | UNION ALL, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2142. Number of Students | JOIN, GROUP BY, RANK | Medium | Oracle: L | Lab 04 |
| 2153. Number of Flights per Route | SUM, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2159. Order Two Columns | Recursive CTE | Hard-Medium | Oracle: M, Google: M | Lab 03 |
| 2175. World Championship | JOIN, SUM, CASE | Medium | Oracle: L | Lab 04 |
| 2199. Number of Users | GENERATE_SERIES, COUNT | Hard | Oracle: L, Google: M | Lab 04 |
| 2205. Problem Pairs | Self-JOIN, GROUP BY | Medium | Oracle: L | Lab 03 |
| 2228. Users With Two Appointments | DATE overlap | Medium | Oracle: M, Google: M | Lab 03 |
| 2230. Users That Buy All Products | GROUP BY, HAVING COUNT | Medium | Oracle: L | Lab 04 |
| 2238. Number of Times a Driver | JOIN, COUNT | Medium | Oracle: L | Lab 04 |
| 2252. Dynamic Pivoting | PIVOT, dynamic SQL | Hard | Oracle: M | Lab 04 |
| 2253. Dynamic Unpivoting | UNPIVOT, dynamic SQL | Hard | Oracle: M | Lab 04 |
| 2292. Products With Three or More Orders | GROUP BY, HAVING COUNT | Medium | Oracle: L | Lab 04 |
| 2298. Tasks Count in Weekend | CASE, date functions | Medium | Oracle: L | Lab 03 |
| 2308. Arrange Table by Gender | ROW_NUMBER, CASE | Medium | Oracle: M | Lab 03 |
| 2314. First Day | GROUP BY MIN | Medium | Oracle: L | Lab 04 |
| 2324. Product Sales Analysis IV | JOIN, RANK | Medium | Oracle: M | Lab 04 |
| 2329. Product Sales Analysis V | JOIN, GROUP BY, SUM | Medium | Oracle: M | Lab 04 |
| 2339. All Matches | CROSS JOIN | Medium | Oracle: L | Lab 03 |
| 2356. Number of Unique Subjects | COUNT DISTINCT, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2362. Top Travelers | LEFT JOIN, SUM | Medium | Oracle: L | Lab 04 |
| 2372. Calculate Salary | CASE, GROUP BY | Medium | Oracle: M | Lab 04 |
| 2377. Sort Olympics Table | ORDER BY, CASE | Medium | Oracle: L | Lab 04 |
| 2388. Change Null Values | COALESCE, CASE | Medium | Oracle: L | Lab 03 |
| 2394. Employees With Deductions | JOIN, GROUP BY, HAVING | Medium | Oracle: L | Lab 04 |
| 2474. Customers With Strictly Increasing | JOIN, GROUP BY, HAVING | Hard | Oracle: L | Lab 04 |
| 2480. Form a Chemical Bond | CROSS JOIN | Medium | Oracle: L | Lab 03 |
| 2494. Merge Overlapping Events | Window functions, gaps | Hard | Oracle: M, Google: M | Lab 03 |
| 2504. Time Spent in Office | SUM, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2668. Find Employees | Multiple JOINs | Medium | Oracle: M | Lab 03 |
| 2669. Count Artist Occurrences | GROUP BY, RANK | Medium | Oracle: L | Lab 04 |
| 2686. Immediate Food Delivery III | JOIN, GROUP BY, RANK | Medium | Oracle: M | Lab 04 |
| 2687. Bikes Last Time Used | MAX, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2688. Find Active Users | GROUP BY, HAVING, date | Medium | Oracle: L, Google: L | Lab 04 |
| 2701. Consecutive Transactions | ROW_NUMBER, grouping | Hard | Oracle: M, Google: M | Lab 03 |
| 2720. Popularity Percentage | Self-JOIN, COUNT DISTINCT | Hard | Oracle: L | Lab 03 |
| 2738. Count Occurrences | GROUP BY, string | Medium | Oracle: L | Lab 04 |
| 2752. Customers With Maximum | RANK, GROUP BY | Hard | Oracle: M | Lab 04 |
| 2793. Status of Flight Tickets | JOIN, CASE, GROUP BY | Hard | Oracle: L | Lab 04 |
| 2820. Election Results | GROUP BY, RANK | Medium | Oracle: M | Lab 04 |
| 2837. Total Traveled Distance | LEFT JOIN, SUM | Medium | Oracle: L | Lab 04 |
| 2853. Highest Salary Difference | MAX, MIN, CASE | Medium | Oracle: M | Lab 04 |
| 2890. Reformat Date | String functions | Medium | Oracle: L | Lab 03 |
| 2922. Market Analysis III | JOIN, GROUP BY, RANK | Medium | Oracle: M | Lab 04 |
| 2938. Separate Black and White Balls | ROW_NUMBER, date | Medium | Oracle: L | Lab 04 |
| 2958. Length of Longest Subarray | Window function | Hard | Oracle: L | Lab 04 |
| 2964. Number of Distinct Products | COUNT DISTINCT, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2978. Smallest Missing Integer | CTE, ROW_NUMBER | Hard | Oracle: L | Lab 03 |
| 2985. Calculate Compressed Mean | AVG, SUM | Medium | Oracle: L | Lab 04 |
| 2986. Find Third Transaction | ROW_NUMBER | Medium | Oracle: M | Lab 04 |
| 2987. Find Expensive Cities | AVG, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2988. Manager of the Largest Department | GROUP BY, RANK | Medium | Oracle: M | Lab 04 |
| 2989. Class Performance | MAX, MIN | Medium | Oracle: L | Lab 04 |
| 2990. Loan Types | GROUP BY, CASE | Medium | Oracle: L | Lab 04 |
| 2991. Top Three Salaries | ROW_NUMBER | Hard | Oracle: M | Lab 04 |
| 2992. Number of Self-Divisible Permutations | Recursive CTE | Hard | Oracle: L | Lab 03 |
| 2993. Friday Purchases | Date functions, GROUP BY | Medium | Oracle: L | Lab 04 |
| 2994. Friday Purchases II | Window functions | Hard | Oracle: L | Lab 04 |
| 2995. Viewers Turned Streamers | JOIN, GROUP BY | Hard | Oracle: L | Lab 04 |
| 3050. Pizza Toppings Cost | Self-JOIN, combinations | Medium | Oracle: L | Lab 03 |
| 3051. Find Candidates for Data Scientist | GROUP BY, HAVING | Medium | Oracle: L | Lab 04 |
| 3052. Maximize Items | GROUP BY, CASE | Medium | Oracle: L | Lab 04 |
| 3053. Classifying Triangles | CASE | Medium | Oracle: L | Lab 03 |
| 3054. Binary Tree Nodes | CASE, subquery | Medium | Oracle: M | Lab 03 |
| 3055. Top Percentile Fraud | PERCENT_RANK | Medium | Oracle: M | Lab 04 |
| 3056. Snaps Analysis | CASE, AVG | Medium | Oracle: L | Lab 04 |
| 3057. Employees Missing Information | FULL OUTER JOIN | Medium | Oracle: M | Lab 03 |
| 3058. Friendship Requests | ROUND, CASE | Medium | Oracle: L | Lab 04 |
| 3059. Find All Unique Email Domains | SUBSTR, REGEXP | Medium | Oracle: L | Lab 03 |
| 3060. User Activities | ROW_NUMBER, GROUP BY | Medium | Oracle: L | Lab 04 |
| 3061. Traffic Light | CASE, COUNT | Medium | Oracle: L | Lab 04 |

### Hard Problems

| Problem | Pattern | Difficulty | Company Freq | Related Lab |
|---------|---------|------------|-------------|-------------|
| 185. Department Top Three Salaries | DENSE_RANK, JOIN | Hard | Oracle: H, Google: H, Amazon: H, MS: M | Lab 04 |
| 262. Trips and Users | JOIN, date filtering, CASE | Hard | Oracle: M, Google: M, Amazon: M | Lab 04 |
| 569. Median Employee Salary | PERCENTILE_CONT | Hard | Oracle: H, Google: M | Lab 04 |
| 571. Find Median Given Frequency | SUM OVER, self-JOIN | Hard | Oracle: M, Google: M | Lab 04 |
| 579. Find Cumulative Salary | SUM OVER, date range | Hard | Oracle: M, Google: M | Lab 04 |
| 601. Human Traffic of Stadium | Window function, LEAD | Hard | Oracle: H, Google: H, Amazon: M | Lab 03 |
| 615. Average Salary | GROUP BY, date | Hard | Oracle: L | Lab 04 |
| 618. Students Report | PIVOT, ROW_NUMBER | Hard | Oracle: M | Lab 04 |
| 1097. Game Play Analysis V | LEAD, CTE, aggregation | Hard | Oracle: M, Google: M | Lab 04 |
| 1127. User Purchase Platform | CTE, CROSS JOIN | Hard | Oracle: M, Google: M | Lab 04 |
| 1159. Market Analysis II | ROW_NUMBER, JOIN | Hard | Oracle: M | Lab 04 |
| 1194. Tournament Winners | GROUP BY, RANK | Hard | Oracle: L | Lab 04 |
| 1225. Report Contiguous Dates | CTE, ROW_NUMBER, date diff | Hard | Oracle: M, Google: M | Lab 03 |
| 1336. Number of Transactions | RECURSIVE CTE, GENERATE_SERIES | Hard | Oracle: M, Google: L | Lab 03 |
| 1369. Second Most Recent Activity | ROW_NUMBER, COUNT OVER | Hard | Oracle: M | Lab 04 |
| 1384. Total Sales Amount | PIVOT, GROUP BY | Hard | Oracle: M | Lab 04 |
| 1412. Find Students | GROUP BY, HAVING | Hard | Oracle: L | Lab 04 |
| 1479. Sales by Day | PIVOT, date series | Hard | Oracle: M | Lab 04 |
| 1597. Build Binary Expression | JOIN, recursive | Hard | Oracle: L | Lab 03 |
| 1615. Maximal Network Rank | JOIN, COUNT | Hard | Oracle: L | Lab 04 |
| 1635. Hopper Company Queries I | CTE, date generation | Hard | Oracle: M, Google: M | Lab 04 |
| 1645. Hopper Company Queries II | CTE, date, aggregation | Hard | Oracle: M, Google: M | Lab 04 |
| 1651. Hopper Company Queries III | CTE, AVG | Hard | Oracle: M, Google: L | Lab 04 |
| 1767. Find Subtasks | Recursive CTE | Hard | Oracle: M | Lab 03 |
| 1919. Leetcodify Similar Friends | Self-JOIN, COUNT | Hard | Oracle: L | Lab 03 |
| 1972. First and Last Call | ROW_NUMBER, CTE | Hard | Oracle: L, Google: M | Lab 03 |
| 2004. Number of Employees | GROUP BY, CASE | Hard | Oracle: L | Lab 04 |
| 2010. Employee Salary Median | PERCENTILE_CONT | Hard | Oracle: M | Lab 04 |
| 2051. Category Statistics | PIVOT, aggregation | Hard | Oracle: M | Lab 04 |
| 2102. Sequentially Ordinal Rank | ROW_NUMBER, CASE | Hard | Oracle: L | Lab 04 |
| 2153. Number of Flights | JOIN, GROUP BY, DENSE_RANK | Hard | Oracle: L | Lab 04 |
| 2173. Longest Winning Streak | Window function, grouping | Hard | Oracle: M, Google: H | Lab 03 |
| 2199. Number of Users | CTE, date generation | Hard | Oracle: M, Google: M | Lab 04 |
| 2252. Dynamic Pivoting | PIVOT, dynamic SQL | Hard | Oracle: M | Lab 04 |
| 2253. Dynamic Unpivoting | UNPIVOT, dynamic SQL | Hard | Oracle: M | Lab 04 |
| 2295. Replace Question Marks | CASE, string | Hard | Oracle: L | Lab 03 |
| 2362. Top Percentile | PERCENT_RANK, NTILE | Hard | Oracle: L | Lab 04 |
| 2474. Customers With Strictly Increasing | Window, GROUP BY | Hard | Oracle: L | Lab 04 |
| 2494. Merge Overlapping Events | Window functions, gaps | Hard | Oracle: M | Lab 03 |
| 2668. Find Employee Salary | Multiple JOINs | Hard | Oracle: M | Lab 03 |
| 2701. Consecutive Transactions | ROW_NUMBER, date grouping | Hard | Oracle: M | Lab 03 |
| 2720. Popularity Percentage | Self-JOIN, COUNT | Hard | Oracle: L | Lab 03 |
| 2752. Customers With Maximum | RANK | Hard | Oracle: M | Lab 04 |
| 2793. Status of Flight Tickets | CASE, GROUP BY | Hard | Oracle: L | Lab 04 |
| 2964. Number of Distinct Products | COUNT DISTINCT | Hard | Oracle: L | Lab 04 |
| 2978. Smallest Missing Integer | CTE, ROW_NUMBER | Hard | Oracle: L | Lab 03 |
| 2991. Top Three Salaries | ROW_NUMBER | Hard | Oracle: M | Lab 04 |
| 2992. Self-Divisible Permutations | Recursive CTE | Hard | Oracle: L | Lab 03 |
| 2995. Viewers Turned Streamers | JOIN, GROUP BY | Hard | Oracle: L | Lab 04 |
| 3050+ | Various | Hard | Varies | Lab 03-04 |

---

## Data Structure Patterns for APEX

### APEX Collections

```sql
-- Create collection (APEX session-level table)
APEX_COLLECTION.CREATE_COLLECTION(
    p_collection_name => 'EMP_CART',
    p_truncate_if_exists => TRUE
);

-- Add member
APEX_COLLECTION.ADD_MEMBER(
    p_collection_name => 'EMP_CART',
    p_c001 => :P101_EMP_ID,   -- character attribute
    p_c002 => :P101_EMP_NAME,
    p_n001 => :P101_SALARY     -- numeric attribute
);

-- Query collection (like a regular table)
SELECT c001 AS emp_id, c002 AS emp_name, n001 AS salary
FROM APEX_collections
WHERE collection_name = 'EMP_CART';

-- Iterate over collection
FOR rec IN (SELECT * FROM APEX_collections WHERE collection_name = 'EMP_CART') LOOP
    apex_debug.message('Employee: ' || rec.c002);
END LOOP;

-- Delete collection
APEX_COLLECTION.DELETE_COLLECTION('EMP_CART');
```

| LeetCode Pattern | APEX Collection Analogy | Use Case |
|------------------|------------------------|----------|
| Temporary table operations | Collection CRUD | Wizard multi-step data |
| GROUP BY aggregates | Aggregate collection attributes | Cart total calculations |
| Self-JOIN | Compare members within collection | Detect duplicates |
| Recursive CTE | Nested collection (one per level) | Tree data in APEX |

### APEX Application Items / Page Items

```sql
-- Set application item
APEX_UTIL.SET_SESSION_STATE(
    p_item_name => 'GLOBAL_DEPT_ID',
    p_value => '50'
);

-- Get value (can be used in SQL as bind variable)
-- :GLOBAL_DEPT_ID in any SQL query

-- Item-level session state protection levels
-- Unrestricted: no checks
-- Checksum Required: generated page must set it
-- Checksum + Session State: session-level integrity
-- Restricted: maximum protection
```

| LeetCode Pattern | APEX Item Analogy | Use Case |
|------------------|-------------------|----------|
| WHERE clause variables | Bind variable `:P_ITEM` | Dynamic filtering |
| Scalar subquery | Application-level item | Cached configuration |
| Session-based filtering | `:APP_USER` in queries | Row-level security |

### APEX Data Dictionary Views

```sql
-- All APEX applications in workspace
SELECT application_id, application_name, owner
FROM APEX_APPLICATIONS;

-- All pages
SELECT application_id, page_id, page_title, page_group
FROM APEX_APPLICATION_PAGES;

-- All page items
SELECT page_id, region_name, item_name, item_type
FROM APEX_APPLICATION_PAGE_ITEMS;

-- All regions
SELECT page_id, region_name, region_type, source_type, source
FROM APEX_APPLICATION_PAGE_REGIONS;

-- Authorization schemes
SELECT authorization_scheme_name, error_message
FROM APEX_APPLICATION_AUTHORIZATION;

-- Navigation entries
SELECT parent_entry_name, entry_name, target_link
FROM APEX_APPLICATION_NAV;

-- APEX session state
SELECT workspace, apex_user, application_id, page_id, session_id
FROM APEX_WORKSPACE_SESSIONS;
```

| LeetCode Pattern | APEX Dictionary View | Use Case |
|------------------|---------------------|----------|
| JOIN multiple tables | Join APEX dictionary views | Application metadata analysis |
| GROUP BY aggregation | Count pages per application | Application complexity audit |
| EXISTS / NOT EXISTS | Check if authorization exists | Security gap analysis |

### APEX Interactive Grid Data Model

```sql
-- Interactive Grid internal model
-- 1. Grid view (display): Interactive Grid region with SQL source
-- 2. Grid model (edit): Interactive Grid region with updatable columns
-- 3. Grid actions: Add row, delete row, save

-- I need to modify grid data programmatically:
BEGIN
    APEX_IG.add_row(
        p_region_id => 12345,
        p_values => APEX_IG.G_ROW_T(
            'COLUMN_NAME' => 'VALUE'
        )
    );
END;
```

### ORDS RESTful Service Patterns

```sql
-- Expose table as REST API
BEGIN
    ORDS.ENABLE_OBJECT(
        p_object => 'EMPLOYEES',
        p_object_type => 'TABLE',
        p_rest_data_type => 'PLSQL'
    );
END;
/

-- REST handler for custom endpoint
BEGIN
    ORDS.DEFINE_HANDLER(
        p_module_name => 'emp_api',
        p_pattern => 'employees/dept/:dept_id',
        p_method => 'GET',
        p_source_type => ORDS.SOURCE_TYPE_QUERY,
        p_source => 'SELECT * FROM employees WHERE department_id = :dept_id'
    );
END;
/
```

| LeetCode Pattern | ORDS Analogy | Use Case |
|------------------|-------------|----------|
| Subquery returning scalar | REST endpoint returning single value | API aggregation |
| JOIN across tables | REST endpOINT composing multiple sources | Microservices |
| Window function ranking | REST pagination with sort | API cursor-based pagination |

### Oracle-Specific APEX Utilities

```sql
-- APEX_UTIL: String operations
APEX_UTIL.STRING_TO_TABLE('a:b:c', ':')   -- Split string
APEX_UTIL.TABLE_TO_STRING(coll, ':')      -- Join collection

-- APEX_JSON: JSON manipulation
APEX_JSON.WRITE('employees', APEX_JSON.TABLE_T(coll));
APEX_JSON.PARSE(p_source => :P_JSON_INPUT);

-- APEX_WEB_SERVICE: HTTP calls
APEX_WEB_SERVICE.MAKE_REST_REQUEST(
    p_url => 'https://api.example.com/data',
    p_http_method => 'GET'
);

-- APEX_MAIL: Email
APEX_MAIL.SEND(
    p_to => 'user@example.com',
    p_from => 'apex@example.com',
    p_subj => 'Report Ready',
    p_body => 'Your report is ready'
);

-- APEX_ZIP: File operations
APEX_ZIP.NEW_FILE('export.zip');
APEX_ZIP.ADD_FILE('data.csv', v_csv_content);
APEX_ZIP.FINISH;
```

---

<div align="center">

**"LeetCode SQL tests your syntax — Oracle APEX tests your architecture. Master both, ace any database interview."**

---

[Back to Top](#oracle-apex--leetcode-pattern-cheatsheet)

</div>
