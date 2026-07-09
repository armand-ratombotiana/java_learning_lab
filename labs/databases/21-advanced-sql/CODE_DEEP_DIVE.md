# Window Functions and Analytical SQL

## ROW_NUMBER
ROW_NUMBER assigns a unique sequential integer within each partition:
```sql
SELECT ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn,
       emp_id, ename, salary
FROM employees;
```
Use cases: pagination (SELECT * FROM (...) WHERE rn BETWEEN :1 AND :2), de-duplication (DELETE FROM t WHERE rowid IN (SELECT rid FROM (SELECT rowid AS rid, ROW_NUMBER() OVER (PARTITION BY dup_col ORDER BY id) rn FROM t) WHERE rn > 1)).

## RANK and DENSE_RANK
RANK skips positions after ties; DENSE_RANK does not:
```sql
SELECT RANK() OVER (ORDER BY score DESC) AS rank,
       DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rank,
       score, student_name
FROM exam_scores;
```
If scores are 100, 95, 95, 90: rank = 1, 2, 2, 4; dense_rank = 1, 2, 2, 3.

## NTILE
NTILE distributes rows into approximately equal buckets:
```sql
SELECT NTILE(10) OVER (ORDER BY total_sales DESC) AS decile,
       salesperson, total_sales
FROM sales_data;
```
Use for: percentile analysis, top-N per group, data distribution analysis.

## LAG and LEAD
These access rows at a specified offset:
```sql
SELECT trade_date, closing_price,
       LAG(closing_price, 1) OVER (ORDER BY trade_date) AS prev_day,
       LEAD(closing_price, 1) OVER (ORDER BY trade_date) AS next_day,
       closing_price - LAG(closing_price, 1) OVER (ORDER BY trade_date) AS daily_change
FROM stock_prices;
```

## FIRST_VALUE and LAST_VALUE
Return first/last value in an ordered window:
```sql
SELECT emp_id, salary,
       FIRST_VALUE(salary) OVER (PARTITION BY dept_id ORDER BY salary DESC) AS max_salary,
       LAST_VALUE(salary) OVER (PARTITION BY dept_id ORDER BY salary DESC
         ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS min_salary
FROM employees;
```
Note: LAST_VALUE without frame clause defaults to current row; always specify the window frame.

## CUME_DIST and PERCENT_RANK
CUME_DIST: cumulative distribution (relative position within a group).
PERCENT_RANK: relative rank (rank-1)/(total_rows-1).

## Ratio to Report
```sql
SELECT dept_id, SUM(salary) AS dept_salary,
       RATIO_TO_REPORT(SUM(salary)) OVER () AS pct_of_total
FROM employees GROUP BY dept_id;
```

## Window Frame Clauses
- ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW (default)
- ROWS BETWEEN 3 PRECEDING AND 3 FOLLOWING
- RANGE BETWEEN INTERVAL '7' DAY PRECEDING AND CURRENT ROW
- RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING

## WINDOW Clause (Oracle 21c+)
```sql
SELECT emp_id, salary,
       ROW_NUMBER() OVER w AS rn,
       RANK() OVER w AS rk
FROM employees
WINDOW w AS (PARTITION BY dept_id ORDER BY salary DESC)
ORDER BY emp_id;
```

## GROUP BY vs Window Functions
GROUP BY collapses rows; window functions preserve detail rows while aggregating. Use GROUP BY for aggregate-only results; use window functions when you need both detail and aggregate values.

## Pivoting with Window Functions
```sql
SELECT * FROM (
  SELECT dept_id,
    MAX(CASE WHEN job_id = 'IT_PROG' THEN salary END) AS it_salary,
    MAX(CASE WHEN job_id = 'SA_MAN'  THEN salary END) AS sa_salary
  FROM employees
  GROUP BY dept_id
);
```
This simulates PIVOT without the PIVOT syntax.

## Running Total
```sql
SELECT trade_date, amount,
       SUM(amount) OVER (ORDER BY trade_date ROWS UNBOUNDED PRECEDING) AS running_total
FROM transactions;
```

## Moving Average
```sql
SELECT trade_date, closing_price,
       AVG(closing_price) OVER (ORDER BY trade_date ROWS BETWEEN 4 PRECEDING AND CURRENT ROW) AS ma5
FROM stock_prices;
```

## Advanced Frame: Time-Based
```sql
SELECT sale_date, sale_amount,
       SUM(sale_amount) OVER (ORDER BY sale_date
         RANGE BETWEEN INTERVAL '30' DAY PRECEDING AND CURRENT ROW) AS rolling_30d
FROM daily_sales;
```

## WIDTH_BUCKET for Histograms
```sql
SELECT WIDTH_BUCKET(salary, 0, 10000, 10) AS bucket,
       COUNT(*) AS cnt
FROM employees
GROUP BY WIDTH_BUCKET(salary, 0, 10000, 10)
ORDER BY bucket;
```

## LISTAGG and Pivot Aggregations
```sql
SELECT dept_id,
       LISTAGG(ename, ', ') WITHIN GROUP (ORDER BY ename) AS employees
FROM employees
GROUP BY dept_id;
```

## LAG for Growth Rate
```sql
SELECT revenue_date, revenue,
       (revenue - LAG(revenue, 1) OVER (ORDER BY revenue_date)) / 
         LAG(revenue, 1) OVER (ORDER BY revenue_date) * 100 AS growth_pct
FROM quarterly_revenue;
```

## Window Function Performance
Window functions typically perform a full table scan with a sort. For large datasets, enable parallelism: `/*+ PARALLEL(4) */`. Consider materialized views with pre-computed aggregations for frequently used window queries.