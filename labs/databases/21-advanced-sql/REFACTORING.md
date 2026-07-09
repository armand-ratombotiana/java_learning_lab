# Refactoring SQL Queries

## Replace Subqueries with CTEs
Before:
```sql
SELECT e.*, (SELECT MAX(salary) FROM employees WHERE dept_id = e.dept_id) AS max_sal
FROM employees e;
```

After:
```sql
WITH dept_max AS (
  SELECT dept_id, MAX(salary) AS max_salary FROM employees GROUP BY dept_id
)
SELECT e.*, d.max_salary FROM employees e JOIN dept_max d ON e.dept_id = d.dept_id;
```

## Replace Correlated Subqueries with Window Functions
Before:
```sql
SELECT e.*, (SELECT COUNT(*) FROM employees e2 WHERE e2.dept_id = e.dept_id) AS dept_count
FROM employees e;
```

After:
```sql
SELECT e.*, COUNT(*) OVER (PARTITION BY dept_id) AS dept_count
FROM employees e;
```

## Replace Self-Join with Window Function
Before:
```sql
SELECT a.emp_id, a.salary, b.avg_salary
FROM employees a
JOIN (SELECT dept_id, AVG(salary) AS avg_salary FROM employees GROUP BY dept_id) b
  ON a.dept_id = b.dept_id;
```

After:
```sql
SELECT emp_id, salary,
       AVG(salary) OVER (PARTITION BY dept_id) AS avg_salary
FROM employees;
```

## Replace Manual Pivot with PIVOT
Before:
```sql
SELECT dept_id,
  MAX(CASE WHEN job_id = 'IT_PROG' THEN salary END) AS it_salary,
  MAX(CASE WHEN job_id = 'SA_MAN' THEN salary END) AS sa_salary
FROM employees GROUP BY dept_id;
```

After:
```sql
SELECT * FROM (
  SELECT dept_id, job_id, salary FROM employees
) PIVOT (SUM(salary) FOR job_id IN ('IT_PROG' AS it, 'SA_MAN' AS sa));
```

## Replace UNION ALL with CTE
Before:
```sql
SELECT 'Manager' AS type, emp_id, ename FROM employees WHERE job_id LIKE '%MAN%'
UNION ALL
SELECT 'Clerk', emp_id, ename FROM employees WHERE job_id LIKE '%CLERK%';
```

After:
```sql
WITH cte AS (SELECT emp_id, ename, job_id FROM employees)
SELECT 'Manager' AS type, emp_id, ename FROM cte WHERE job_id LIKE '%MAN%'
UNION ALL
SELECT 'Clerk', emp_id, ename FROM cte WHERE job_id LIKE '%CLERK%';
```

## INSERT INTO SELECT Upgrade
Before:
```sql
INSERT INTO sales_history (sale_id, amount, sale_date)
SELECT sale_id, amount, sale_date FROM sales WHERE sale_date < ADD_MONTHS(SYSDATE, -12);
```

After:
```sql
INSERT /*+ APPEND */ INTO sales_history PARTITION (old_data)
SELECT sale_id, amount, sale_date FROM sales
WHERE sale_date < ADD_MONTHS(SYSDATE, -12)
LOG ERRORS INTO sales_load_errors REJECT LIMIT UNLIMITED;
```

## Replace CONNECT BY with Recursive CTE (for portability)
Before (Oracle):
```sql
SELECT emp_id, ename, LEVEL FROM employees
START WITH mgr_id IS NULL
CONNECT BY PRIOR emp_id = mgr_id;
```

After:
```sql
WITH RECURSIVE org(emp_id, ename, level) AS (
  SELECT emp_id, ename, 1 FROM employees WHERE mgr_id IS NULL
  UNION ALL
  SELECT e.emp_id, e.ename, o.level + 1
  FROM employees e JOIN org o ON e.mgr_id = o.emp_id
)
SELECT * FROM org;
```

## Replace Manual Pagination with ROW_NUMBER
Before:
```sql
SELECT * FROM (SELECT rownum AS rn, alias.* FROM (
  SELECT * FROM employees ORDER BY emp_id
) alias WHERE rownum <= :limit)
WHERE rn > :offset;
```

After:
```sql
SELECT * FROM (
  SELECT e.*, ROW_NUMBER() OVER (ORDER BY emp_id) AS rn
  FROM employees e
) WHERE rn BETWEEN :start AND :end;
```

## Replace Multiple Aggregates with PIVOT
Before:
```sql
SELECT dept_id,
  SUM(CASE WHEN year = 2023 THEN amount END) AS y2023,
  SUM(CASE WHEN year = 2024 THEN amount END) AS y2024
FROM sales GROUP BY dept_id;
```

After:
```sql
SELECT * FROM (SELECT dept_id, year, amount FROM sales)
PIVOT (SUM(amount) FOR year IN (2023 AS y2023, 2024 AS y2024));
```

## Replace Materialized View with Partition
Before: Month-end aggregation on large table.
After: Partition by month, query single partition.

## Index Refactoring
Before: `CREATE INDEX idx_emp_dept ON employees(dept_id);`
After: `CREATE INDEX idx_emp_dept_sal ON employees(dept_id, salary DESC);`
Covering index eliminates table access for queries selecting dept_id and salary.