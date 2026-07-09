# How Advanced SQL Works

## Window Functions
Window functions compute values over sets of rows related to the current row:
```sql
SELECT emp_id, dept_id, salary,
       ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rank_in_dept,
       SUM(salary) OVER (PARTITION BY dept_id) AS dept_total
FROM employees;
```
The PARTITION BY clause divides the result set; ORDER BY defines row order within each partition. The frame clause (ROWS/RANGE BETWEEN) restricts the window to a subset of rows.

## ROW_NUMBER, RANK, DENSE_RANK
```sql
SELECT salary,
       ROW_NUMBER() OVER (ORDER BY salary DESC) AS rn,
       RANK() OVER (ORDER BY salary DESC) AS rk,
       DENSE_RANK() OVER (ORDER BY salary DESC) AS dr
FROM employees;
```
If salaries are (5000,5000,4000,3000): ROW_NUMBER gives (1,2,3,4); RANK gives (1,1,3,4); DENSE_RANK gives (1,1,1,2).

## NTILE
```sql
SELECT emp_id, salary,
       NTILE(4) OVER (ORDER BY salary DESC) AS quartile
FROM employees;
```
Distributes rows into 4 buckets. If 10 rows, distribution: buckets 1-2 get 3 rows, buckets 3-4 get 2 rows.

## LAG and LEAD
```sql
SELECT emp_id, salary,
       LAG(salary, 1, 0) OVER (ORDER BY hire_date) AS previous_salary,
       LEAD(salary, 1, 0) OVER (ORDER BY hire_date) AS next_salary
FROM employees;
```
LAG accesses a row before the current row. LEAD accesses a row after the current row. Default offset is 1; default value is NULL.

## Recursive CTE
```sql
WITH RECURSIVE org_chart (emp_id, emp_name, mgr_id, level) AS (
  -- Anchor member
  SELECT emp_id, emp_name, mgr_id, 1 FROM employees WHERE mgr_id IS NULL
  UNION ALL
  -- Recursive member
  SELECT e.emp_id, e.emp_name, e.mgr_id, oc.level + 1
  FROM employees e
  JOIN org_chart oc ON e.mgr_id = oc.emp_id
)
SELECT * FROM org_chart;
```

## PIVOT and UNPIVOT
```sql
-- PIVOT: rows to columns
SELECT * FROM (
  SELECT dept_id, job_id, salary FROM employees
) PIVOT (
  SUM(salary) FOR job_id IN ('IT_PROG' AS it, 'SA_MAN' AS sa, 'FI_ACCOUNT' AS fi)
);

-- UNPIVOT: columns to rows
SELECT * FROM (
  SELECT dept_id, it, sa, fi FROM dept_stats
) UNPIVOT (
  salary FOR job_type IN (it AS 'IT_PROG', sa AS 'SA_MAN', fi AS 'FI_ACCOUNT')
);
```

## MERGE (Upsert)
```sql
MERGE INTO employees t
USING (SELECT 101, 'Alice', 'IT_PROG', 5000 FROM dual) s
ON (t.emp_id = s.emp_id)
WHEN MATCHED THEN UPDATE SET t.salary = s.salary
WHEN NOT MATCHED THEN INSERT (emp_id, ename, job, salary)
  VALUES (s.emp_id, s.ename, s.job, s.salary);
```

## CONNECT BY
```sql
SELECT emp_id, ename, mgr_id, LEVEL, SYS_CONNECT_BY_PATH(ename, '/') AS path
FROM employees
START WITH mgr_id IS NULL
CONNECT BY PRIOR emp_id = mgr_id
ORDER SIBLINGS BY ename;
```

## MODEL Clause
```sql
SELECT region, month, sales, cumulative_sales
FROM sales_data
MODEL
  PARTITION BY (region)
  DIMENSION BY (month)
  MEASURES (sales, 0 AS cumulative_sales)
  RULES (
    cumulative_sales[month] = sales[CV()] + NVL(cumulative_sales[CV()-1], 0)
  );
```

## MATCH_RECOGNIZE
```sql
SELECT * FROM stock_prices
MATCH_RECOGNIZE (
  PARTITION BY symbol
  ORDER BY trade_date
  MEASURES FINAL LAST(price) AS final_price
  ONE ROW PER MATCH
  PATTERN (up+ down+)
  DEFINE
    up AS price > PREV(price),
    down AS price < PREV(price)
);
```

## Query Execution Plan
```sql
EXPLAIN PLAN FOR SELECT * FROM employees WHERE dept_id = 10;
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
```
The plan shows access paths (FULL TABLE SCAN vs INDEX SCAN), join methods (NESTED LOOPS, HASH JOIN), and estimated cardinalities.

## Partition Pruning
```sql
SELECT * FROM partitioned_sales
WHERE sale_date BETWEEN DATE '2024-01-01' AND DATE '2024-03-31';
```
The optimizer prunes to only partitions containing Jan-Mar 2024 data.