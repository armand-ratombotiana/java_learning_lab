# Mock Interview: SQL Fundamentals (Lab 02)

**Role:** Database Developer (Junior/Mid)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Write a query to find employees who earn more than their managers.

**Candidate:** 
```sql
SELECT e.employee_id, e.first_name, e.last_name, e.salary
FROM employees e
JOIN employees m ON e.manager_id = m.employee_id
WHERE e.salary > m.salary;
```
This uses a self-join on the EMPLOYEES table. The alias `e` represents employees, `m` represents managers.

**Interviewer:** Explain different types of JOINs in SQL.

**Candidate:**
- **INNER JOIN:** Returns rows with matching values in both tables
- **LEFT JOIN:** Returns all rows from left table, matching rows from right (NULL where no match)
- **RIGHT JOIN:** Returns all rows from right table, matching rows from left
- **FULL OUTER JOIN:** Returns all rows from both tables, NULLs where no match
- **CROSS JOIN:** Cartesian product of both tables
- **NATURAL JOIN:** Joins on columns with the same name (use with caution)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Write a query using window functions to find the top 3 products by revenue each month.

**Candidate:**
```sql
WITH monthly_revenue AS (
    SELECT 
        TO_CHAR(order_date, 'YYYY-MM') AS month,
        product_id,
        SUM(quantity * unit_price) AS revenue,
        DENSE_RANK() OVER (
            PARTITION BY TO_CHAR(order_date, 'YYYY-MM') 
            ORDER BY SUM(quantity * unit_price) DESC
        ) AS rank
    FROM orders o
    JOIN order_items oi ON o.order_id = oi.order_id
    GROUP BY TO_CHAR(order_date, 'YYYY-MM'), product_id
)
SELECT month, product_id, revenue, rank
FROM monthly_revenue
WHERE rank <= 3
ORDER BY month, rank;
```

This uses a CTE (WITH clause) for readability. `DENSE_RANK()` allows ties — if two products have the same revenue, they share the same rank.

**Interviewer:** How do you optimize a query that performs poorly? Walk through the steps.

**Candidate:** 
1. **Get the execution plan:** `EXPLAIN PLAN FOR <query>` then `SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY)`
2. **Look for:** Full table scans on large tables, inefficient JOIN order, missing indexes, cartesian joins
3. **Check statistics:** `SELECT table_name, num_rows, last_analyzed FROM user_tables` — stale stats cause poor plans
4. **Common fixes:**
   - Add indexes on WHERE and JOIN columns
   - Use `/*+ INDEX(table_name index_name) */` hint for specific index
   - Rewrite subqueries as JOINs when possible
   - Use `EXISTS` instead of `IN` for correlated subqueries
   - Consider materialized views for complex aggregations

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** You have a table with 100M customer purchase records. Write an optimized query to find customers who made purchases in all 12 months of 2023. Then optimize it.

**Candidate:** 

**Query:**
```sql
SELECT customer_id
FROM purchases
WHERE EXTRACT(YEAR FROM purchase_date) = 2023
GROUP BY customer_id
HAVING COUNT(DISTINCT EXTRACT(MONTH FROM purchase_date)) = 12;
```

**Optimization:**
1. **Index:** Create a composite index on `(customer_id, purchase_date)`. Better yet, a function-based index for the EXTRACT patterns.
2. **Rewrite as anti-pattern detection:** Find customers missing a month (faster if most customers aren't active all 12 months):
```sql
WITH all_months AS (
    SELECT LEVEL AS month_num FROM DUAL CONNECT BY LEVEL <= 12
),
customer_months AS (
    SELECT DISTINCT customer_id, EXTRACT(MONTH FROM purchase_date) AS month_num
    FROM purchases
    WHERE purchase_date >= DATE '2023-01-01' AND purchase_date < DATE '2024-01-01'
)
SELECT cm.customer_id
FROM customer_months cm
RIGHT JOIN all_months am ON cm.month_num = am.month_num
GROUP BY cm.customer_id
HAVING COUNT(cm.month_num) = 12;
```

3. **Performance comparison:** For a table with 100M rows:
   - Full table scan: ~2 minutes
   - With index: ~30 seconds
   - With materialized view pre-computing monthly activity: < 1 second
4. **Partitioning:** Partition the table by month. Query scans only 12 partitions for each year.

**Interviewer:** Write a query using MODEL clause to do what-if analysis: if we increase prices by 10% on products in category 'Electronics', what's the projected revenue?

**Candidate:**
```sql
SELECT product_id, product_name, current_price, new_price, 
       projected_sales, projected_revenue
FROM (
    SELECT product_id, product_name, price AS current_price,
           quantity_sold
    FROM products p
    JOIN sales s ON p.product_id = s.product_id
    WHERE category = 'Electronics'
)
MODEL
    DIMENSION BY (product_id)
    MEASURES (current_price, 0 AS new_price, quantity_sold, 
              0 AS projected_sales, 0 AS projected_revenue)
    RULES (
        new_price[ANY] = current_price[CV()] * 1.10,
        projected_sales[ANY] = quantity_sold[CV()] * 0.85, -- assume 15% demand drop
        projected_revenue[ANY] = new_price[CV()] * projected_sales[CV()]
    )
ORDER BY product_id;
```

The MODEL clause performs inter-row calculations similar to spreadsheet formulas. It's powerful but harder to maintain.

---

## Interviewer Feedback

**Strengths:** Strong SQL knowledge, practical query optimization, creative use of advanced features  
**Areas to Improve:** Could discuss Oracle 23c SQL enhancements (Boolean vectors, property graph)  
**Verdict:** Strong Hire

---

*Databases Lab 02 MOCK_INTERVIEW.md — Part of Databases Academy Interview Preparation*
