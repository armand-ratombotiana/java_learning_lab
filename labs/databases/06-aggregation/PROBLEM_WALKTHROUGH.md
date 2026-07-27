# SQL Problem Walkthrough: 06-aggregation

## Problem 1: Customers with Positive Revenue (LC SQL 182) — Oracle/Amazon

### Interview Scenario
"You're interviewing at Amazon for a financial analytics role. They give you a Purchases table and ask you to find customers who have made at least one purchase with a revenue greater than 0."

### The Problem
Table **Purchases**: id (NUMBER PK), customer_id (NUMBER), amount (NUMBER, can be negative for returns). Write a query to find all customers who have at least one purchase with amount > 0. Return customer_id, grouped. Then find customers whose total revenue is positive.

### Step 1: Understand Schema
- Each row is a purchase transaction
- amount can be positive (purchase) or negative (return)
- A customer may have multiple rows
- Need to aggregate by customer_id

Sample data:
| id | customer_id | amount |
|----|-------------|--------|
| 1  | 1           | 100    |
| 2  | 1           | -20    |
| 3  | 2           | -50    |
| 4  | 3           | 200    |
| 5  | 3           | -10    |

### Step 2: Think Aloud
Two interpretations:
1. Any customer with at least one positive transaction → EXISTS or GROUP BY with MAX(amount) > 0
2. Customer with total revenue > 0 → GROUP BY, HAVING SUM(amount) > 0

### Step 3: Write the Query
```sql
-- Customers with at least one positive purchase (even if net is negative)
SELECT customer_id
  FROM purchases
 GROUP BY customer_id
HAVING MAX(amount) > 0;
```

```sql
-- Customers with net positive revenue
SELECT customer_id,
       SUM(amount) AS total_revenue
  FROM purchases
 GROUP BY customer_id
HAVING SUM(amount) > 0
 ORDER BY total_revenue DESC;
```

```sql
-- Detailed report with counts
SELECT customer_id,
       COUNT(*) AS total_transactions,
       COUNT(CASE WHEN amount > 0 THEN 1 END) AS positive_transactions,
       COUNT(CASE WHEN amount < 0 THEN 1 END) AS returns,
       SUM(amount) AS net_revenue,
       AVG(amount) AS avg_transaction
  FROM purchases
 GROUP BY customer_id
HAVING SUM(amount) > 0
 ORDER BY net_revenue DESC;
```

### Step 4: Execution Plan
```
| Id | Operation          | Name      | Cost |
|----|--------------------|-----------|------|
|  0 | SELECT STATEMENT   |           |      |
|  1 |  SORT GROUP BY     |           |      |
|  2 |   TABLE ACCESS FULL| PURCHASES |      |
```

Oracle does a full scan, groups by customer_id, computes SUM/MAX/COUNT, and applies the HAVING filter.

### Step 5: Optimize
```sql
CREATE INDEX purchases_customer_amount_idx ON purchases(customer_id, amount);
```

A composite index on (customer_id, amount) allows Oracle to do an INDEX FAST FULL SCAN instead of a table access, significantly reducing I/O.

Partitioning by customer_id range is another option for large tables:
```sql
CREATE TABLE purchases_part
  PARTITION BY RANGE (customer_id)
  (PARTITION p1 VALUES LESS THAN (1000000),
   PARTITION p2 VALUES LESS THAN (2000000),
   PARTITION p3 VALUES LESS THAN (MAXVALUE))
AS SELECT * FROM purchases;
```

### Step 6: Test
Edge cases:
- **Customer with all returns (all negative)**: Excluded by HAVING SUM > 0
- **Customer with net zero (e.g., +100, -100)**: Excluded (SUM = 0)
- **Customer with single transaction > 0**: Included
- **Customer with no transactions**: Not in the table at all — excluded
- **NULL amount**: SUM ignores NULLs, but COUNT(*) would not — be explicit

```sql
-- Comprehensive aggregation test
WITH purchases_test AS (
  SELECT 1 AS customer_id, 100 AS amount FROM dual UNION ALL
  SELECT 1, -100 FROM dual UNION ALL      -- net zero
  SELECT 2, 200 FROM dual UNION ALL
  SELECT 3, -50 FROM dual UNION ALL       -- all negative
  SELECT 4, 300 FROM dual UNION ALL
  SELECT 4, -10 FROM dual                 -- net positive
)
SELECT customer_id,
       SUM(amount) AS total,
       COUNT(*) AS cnt
  FROM purchases_test
 GROUP BY customer_id
HAVING SUM(amount) > 0;
-- Returns 2 (total 200), 4 (total 290)
```

### Company Evaluation
- **Amazon**: Focus on financial data aggregation, handling refunds/returns in revenue calculation.
- **Oracle**: HAVING vs WHERE distinction, GROUP BY optimization.
- **Microsoft**: T-SQL has the same syntax. Discuss ORDER BY in aggregate queries.

---

## Problem 2: Find Total Time Spent by Each Employee (LC SQL 1741) — Microsoft

### Interview Scenario
"Microsoft interview: An Employees table logs daily check-in and check-out times. Calculate the total time each employee spent in the office each day."

### The Problem
Table **Employees**: id (NUMBER PK), event_day (DATE), in_time (NUMBER, minutes from midnight 0-1439), out_time (NUMBER). Calculate total time in office per employee per day. Return (day, emp_id, total_time). Order by day, emp_id.

### Step 1: Understand Schema
- Each row is one employee's entry for one day
- in_time and out_time are minutes since midnight (0-1439)
- total_time = out_time - in_time per row
- An employee has one row per day (per the problem — no duplicates per day)

Sample data:
| id | event_day  | in_time | out_time |
|----|------------|---------|----------|
| 1  | 2020-11-28 | 480     | 1080     |
| 1  | 2020-11-28 | 60      | 300      |
| 1  | 2020-11-29 | 300     | 600      |
| 2  | 2020-11-28 | 0       | 1440     |

Wait — in this LeetCode problem, each row is a separate event, so an employee can have multiple events per day. Total time per day = SUM(out_time - in_time) per emp_id per day.

### Step 2: Think Aloud
Simple GROUP BY on (emp_id, event_day). Sum of (out_time - in_time) gives total minutes.

### Step 3: Write the Query
```sql
SELECT TO_CHAR(event_day, 'YYYY-MM-DD') AS day,
       emp_id,
       SUM(out_time - in_time) AS total_time
  FROM employees
 GROUP BY event_day, emp_id
 ORDER BY event_day, emp_id;
```

### Step 4: Execution Plan
```
| Id | Operation              | Name      | Cost |
|----|------------------------|-----------|------|
|  0 | SELECT STATEMENT       |           |      |
|  1 |  SORT GROUP BY         |           |      |
|  2 |   TABLE ACCESS FULL    | EMPLOYEES |      |
```

Simple aggregate — GROUP BY on (event_day, emp_id).

### Step 5: Optimize
```sql
CREATE INDEX emp_day_idx ON employees(event_day, emp_id);
```

With this covering index, Oracle can answer the query without touching the table at all (INDEX FULL SCAN vs TABLE FULL SCAN) because all needed columns (event_day, emp_id, in_time, out_time) are either in the index or computed.

### Step 6: Test
Edge cases:
- **Null in_time or out_time**: SUM ignores NULLs, but out_time - in_time with NULL gives NULL — need NVL or COALESCE
- **in_time > out_time**: Negative time — possible typo; should flag as error
- **Multiple entries same day**: SUM adds them all (correct — summing durations)
- **Zero time (in = out)**: Contributes 0 to SUM

```sql
-- With NULL handling
SELECT TO_CHAR(event_day, 'YYYY-MM-DD') AS day,
       emp_id,
       SUM(NVL(out_time, 0) - NVL(in_time, 0)) AS total_time
  FROM employees
 GROUP BY event_day, emp_id
 ORDER BY day, emp_id;
```

### Company Evaluation
- **Microsoft**: T-SQL uses CAST for date formatting. Discuss GROUP BY with computed columns.
- **Oracle**: Format dates with TO_CHAR. Discuss NVL for NULL handling.
- **Amazon**: At scale, discuss partitioning by date and pre-aggregated materialized views.

---

## Problem 3: Game Play Analysis I (LC SQL 511) — Google

### Interview Scenario
"Google interview: The Activity table logs player event dates. Find the first login date for each player."

### The Problem
Table **Activity**: player_id (NUMBER), device_id (NUMBER), event_date (DATE), games_played (NUMBER). PK is (player_id, event_date). Write a query to report the first login date for each player. Return player_id, first_login_date. Order by player_id.

### Step 1: Understand Schema
- Multiple rows per player (different dates)
- Need MIN date per player
- games_played is irrelevant to this query

Sample data:
| player_id | device_id | event_date | games_played |
|-----------|-----------|------------|--------------|
| 1         | 2         | 2016-03-01 | 5            |
| 1         | 2         | 2016-05-02 | 6            |
| 2         | 3         | 2017-06-25 | 1            |
| 3         | 1         | 2016-03-02 | 0            |
| 3         | 4         | 2018-07-03 | 5            |

### Step 2: Think Aloud
Straightforward GROUP BY with MIN(event_date). Could also use FIRST_VALUE window function.

### Step 3: Write the Query
```sql
-- GROUP BY approach (simplest)
SELECT player_id,
       MIN(event_date) AS first_login
  FROM activity
 GROUP BY player_id
 ORDER BY player_id;
```

```sql
-- Window function approach (useful if we need other columns too)
SELECT player_id,
       event_date AS first_login
  FROM (
    SELECT player_id,
           event_date,
           ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY event_date) AS rn
      FROM activity
  )
 WHERE rn = 1
 ORDER BY player_id;
```

### Step 4: Execution Plan
For GROUP BY:
```
| Id | Operation          | Name     | Cost |
|----|--------------------|----------|------|
|  0 | SELECT STATEMENT   |          |      |
|  1 |  SORT GROUP BY     |          |      |
|  2 |   TABLE ACCESS FULL| ACTIVITY |      |
```

### Step 5: Optimize
```sql
CREATE INDEX act_player_date_idx ON activity(player_id, event_date);
```

This covering index allows Oracle to do a GROUP BY INDEX (INDEX FAST FULL SCAN or INDEX RANGE SCAN with sort) instead of scanning the full table.

### Step 6: Test
Edge cases:
- **Player with only one event**: That event_date is the first login
- **Multiple events same day**: PK prevents duplicates on (player_id, event_date), so MIN returns that date
- **No events**: Player not in table — not returned
- **NULL event_date**: Should not exist (PK), but if so, MIN ignores NULL? No — MIN skips NULLs

```sql
-- Verify with window function
SELECT player_id,
       event_date AS first_login,
       games_played AS first_day_games
  FROM (
    SELECT player_id, event_date, games_played,
           ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY event_date) AS rn
      FROM activity
  )
 WHERE rn = 1;
```

### Company Evaluation
- **Google**: Gamification analytics — discuss cohort analysis extensions.
- **Oracle**: GROUP BY optimization, MIN performance on indexed columns.
- **Amazon**: At scale, discuss storing pre-computed first login in a separate table with ETL jobs.
