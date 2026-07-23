# Mock Interview: Advanced SQL (Lab 21)

**Role:** Database Developer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are window functions (analytic functions) and how do they differ from GROUP BY?

**Candidate:** Window functions perform calculations across a set of rows related to the current row without collapsing them. Unlike GROUP BY (which collapses rows), window functions preserve individual rows while adding aggregate values.

Common window functions:
- `ROW_NUMBER()` — sequential number
- `RANK()`, `DENSE_RANK()` — ranking with gaps and without gaps
- `LEAD()`, `LAG()` — access subsequent/previous rows
- `SUM() OVER(...)` — running total
- `FIRST_VALUE()`, `LAST_VALUE()` — first/last in window
- `NTILE(n)` — divide into n buckets

**Interviewer:** Write a query using NTILE to divide employees into 4 salary quartiles.

**Candidate:**
```sql
SELECT employee_id, salary,
       NTILE(4) OVER (ORDER BY salary DESC) AS salary_quartile
FROM employees;
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Write a recursive CTE to generate an employee org chart.

**Candidate:**
```sql
WITH org_chart(employee_id, manager_id, level, path) AS (
    -- Anchor: Top-level managers
    SELECT employee_id, manager_id, 1, 
           LPAD(first_name || ' ' || last_name, LEVEL * 4, ' ')
    FROM employees
    WHERE manager_id IS NULL
    
    UNION ALL
    
    -- Recursive: Direct reports
    SELECT e.employee_id, e.manager_id, oc.level + 1,
           oc.path || ' → ' || e.first_name || ' ' || e.last_name
    FROM employees e
    JOIN org_chart oc ON e.manager_id = oc.employee_id
)
SEARCH DEPTH FIRST BY employee_id SET ordering
SELECT employee_id, manager_id, level, path
FROM org_chart
ORDER BY ordering;
```

**Interviewer:** Write a query using PIVOT to show total sales by quarter for each product category.

**Candidate:**
```sql
SELECT * FROM (
    SELECT category_name, 
           TO_CHAR(order_date, 'Q') AS quarter,
           SUM(quantity * unit_price) AS revenue
    FROM products p
    JOIN order_items oi ON p.product_id = oi.product_id
    JOIN orders o ON oi.order_id = o.order_id
    WHERE EXTRACT(YEAR FROM order_date) = 2024
    GROUP BY category_name, TO_CHAR(order_date, 'Q')
)
PIVOT (
    SUM(revenue)
    FOR quarter IN ('1' AS Q1, '2' AS Q2, '3' AS Q3, '4' AS Q4)
)
ORDER BY category_name;
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** You have a table with 500M daily transactions. Write a query to find accounts that have more than 5 consecutive days of declining balance. Include optimization considerations.

**Candidate:** 

**Query using MATCH_RECOGNIZE (Oracle 12c+):**
```sql
SELECT account_id, match_start, match_end, decline_days
FROM transactions
MATCH_RECOGNIZE (
    PARTITION BY account_id
    ORDER BY transaction_date
    MEASURES
        FIRST(transaction_date) AS match_start,
        LAST(transaction_date) AS match_end,
        COUNT(*) AS decline_days
    ONE ROW PER MATCH
    PATTERN (STRT DOWN+)
    DEFINE
        DOWN AS balance < PREV(balance)
)
WHERE decline_days >= 5;
```

**Optimization:**
1. **Index:** Composite index on `(account_id, transaction_date, balance)` — covers the query
2. **Partitioning:** Range-partition by month — query scans only relevant partitions
3. **Materialized view:** Pre-compute daily balances snapshot for faster matching
4. **Parallel execution:** Enable parallel query for the full table scan

**Alternative without MATCH_RECOGNIZE:**
```sql
WITH daily_balance AS (
    SELECT account_id, transaction_date, balance,
           LAG(balance) OVER (PARTITION BY account_id ORDER BY transaction_date) AS prev_balance,
           CASE WHEN balance < LAG(balance) OVER (PARTITION BY account_id ORDER BY transaction_date) 
                THEN 0 ELSE 1 END AS decline_break
    FROM transactions
),
groups AS (
    SELECT account_id, transaction_date, balance,
           SUM(decline_break) OVER (PARTITION BY account_id ORDER BY transaction_date) AS grp
    FROM daily_balance
)
SELECT account_id, MIN(transaction_date) AS match_start, 
       MAX(transaction_date) AS match_end, COUNT(*) AS decline_days
FROM groups
WHERE decline_break = 0
GROUP BY account_id, grp
HAVING COUNT(*) >= 5;
```

---

## Interviewer Feedback

**Strengths:** Strong advanced SQL knowledge, practical MATCH_RECOGNIZE usage, creative gap-and-islands solution  
**Areas to Improve:** Could discuss Oracle 23c SQL property graph enhancements  
**Verdict:** Strong Hire

---

*Databases Lab 21 MOCK_INTERVIEW.md*
