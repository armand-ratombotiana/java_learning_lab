# SQL Problem Walkthrough: 09-query-optimization

## Problem 1: Optimize a Slow-Running Join Query (LC SQL 181 variants) — Oracle/Amazon

### Interview Scenario
"Oracle interview: The following query for 'Employees Earning More Than Their Managers' is running slowly on a table with 10 million employees. Optimize it."

### The Problem
Original slow query:
```sql
SELECT e1.name AS employee
  FROM employee e1, employee e2
 WHERE e1.manager_id = e2.id
   AND e1.salary > e2.salary;
```

The table has 10M rows. This query takes 45 seconds. Optimize it and explain why.

### Step 1: Understand Schema
- Self-referencing table: employee(id, name, salary, manager_id)
- manager_id references id (self-FK)
- 10M employees, ~100K managers (assuming ~100 reports per manager)
- Indexes: PK on id, no index on manager_id

### Step 2: Think Aloud
The current query uses an old-style join (comma-separated). Oracle may choose a HASH JOIN or NESTED LOOPS. Without an index on manager_id, it's almost certainly a full scan of both tables followed by a HASH JOIN.

Key optimizations:
1. Add index on manager_id
2. Add index on (id, salary) covering index for the inner lookup
3. Use NOT EXISTS if semantically equivalent (anti-join)
4. Consider parallel query

### Step 3: Write the Optimized Query
```sql
-- Modern ANSI join with proper indexing
SELECT e1.name AS employee
  FROM employee e1
  JOIN employee e2
    ON e1.manager_id = e2.id
 WHERE e1.salary > e2.salary;
```

### Step 4: Execution Plan Analysis
Before optimization:
```
-------------------------------------------------------------------
| Id | Operation           | Name     | Rows    | Cost    | Time   |
-------------------------------------------------------------------
|  0 | SELECT STATEMENT    |          | 100     | 98K     | 45s    |
|  1 |  HASH JOIN          |          | 100     | 98K     |        |
|  2 |   TABLE ACCESS FULL | EMPLOYEE | 10M     | 49K     |        |
|  3 |   TABLE ACCESS FULL | EMPLOYEE | 10M     | 49K     |        |
-------------------------------------------------------------------
```

Two full table scans of 10M rows each = 20M rows read. The hash join builds a hash table on the smaller side (but both are 10M).

After creating indexes:
```sql
CREATE INDEX emp_mgr_id_idx ON employee(manager_id);
CREATE INDEX emp_id_sal_idx ON employee(id, salary);
```

```
-------------------------------------------------------------------
| Id | Operation                    | Name        | Rows | Cost  |
-------------------------------------------------------------------
|  0 | SELECT STATEMENT             |             | 100  | 1.2K  |
|  1 |  NESTED LOOPS                |             |      |       |
|  2 |   TABLE ACCESS FULL          | EMPLOYEE e1 | 10M  | 49K   |
|  3 |   TABLE ACCESS BY INDEX ROWID| EMPLOYEE e2 | 1    | 1     |
|  4 |    INDEX UNIQUE SCAN         | SYS_C001234 | 1    |       |
-------------------------------------------------------------------
```

The inner access is now a single-row lookup by id via the PK. But this is still a full scan of e1.

Better: Parallel query with full scan:
```sql
SELECT /*+ PARALLEL(e1 4) */ e1.name AS employee
  FROM employee e1
  JOIN employee e2 ON e1.manager_id = e2.id
 WHERE e1.salary > e2.salary;
```

### Step 5: Optimize Further

1. **Materialized view** pre-joining employees with their manager info:
```sql
CREATE MATERIALIZED VIEW emp_mgr_salaries
REFRESH COMPLETE ON DEMAND
AS
SELECT e1.id AS emp_id,
       e1.name AS emp_name,
       e1.salary AS emp_salary,
       e2.salary AS mgr_salary
  FROM employee e1
  JOIN employee e2 ON e1.manager_id = e2.id;
```

2. **Partitioning**: Partition employee by department or manager range for partition-wise joins.

3. **Function-based index** on salary comparison:
```sql
CREATE INDEX emp_mgr_comp_idx ON employee(
  manager_id,
  (SELECT salary FROM employee e2 WHERE e2.id = manager_id)
);
-- (This requires a rewrite using virtual columns)
```

4. **Bitmap index** on salary ranges if salary has low cardinality:
```sql
CREATE BITMAP INDEX emp_salary_bmx ON employee(
  CASE WHEN salary > 50000 THEN 'high' ELSE 'low' END
);
```

### Step 6: Test the Optimizations

Test with EXPLAIN PLAN:
```sql
EXPLAIN PLAN SET STATEMENT_ID 'OPTIMIZED' FOR
SELECT e1.name AS employee
  FROM employee e1
  JOIN employee e2 ON e1.manager_id = e2.id
 WHERE e1.salary > e2.salary;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY(statement_id => 'OPTIMIZED', format => 'ALL'));
```

Check cardinality estimates — they drive optimizer choices. Gather fresh stats:
```sql
EXEC DBMS_STATS.GATHER_TABLE_STATS('HR', 'EMPLOYEE', cascade => TRUE);
```

Edge cases at scale:
- **Skewed data**: A few managers with millions of direct reports — need histogram on manager_id
- **NULL manager_id**: CEO and top-level — excluded by inner join, use outer join if needed
- **Salary ties**: The > vs >= matters; > may return fewer rows

```sql
-- Check for skew
SELECT manager_id, COUNT(*)
  FROM employee
 GROUP BY manager_id
 ORDER BY COUNT(*) DESC
 FETCH FIRST 10 ROWS ONLY;
```

### Company Evaluation
- **Oracle**: Execution plan reading is critical. Understand HASH JOIN vs NESTED LOOPS vs MERGE JOIN.
- **Amazon**: At Amazon scale (petabyte tables), discuss Redshift distribution keys and sort keys.
- **Google**: Spanner uses distributed joins — interleaved tables reduce join cost.

---

## Problem 2: Fix a Query with Bad Cardinality Estimates (LC SQL 1965 variant)

### Interview Scenario
"Oracle interview: A query that joins Orders, Customers, and Products runs fine for small dates but runs for hours on date ranges with many orders. The optimizer is choosing a suboptimal plan."

### The Problem
Slow query:
```sql
SELECT c.name, o.order_date, p.product_name
  FROM orders o
  JOIN customers c ON o.customer_id = c.id
  JOIN products p ON o.product_id = p.id
 WHERE o.order_date BETWEEN DATE '2024-01-01' AND DATE '2024-12-31'
   AND c.status = 'ACTIVE';
```

Table sizes: Orders 500M, Customers 50M, Products 10M.
The query runs in 2 seconds for January but 2+ hours for December.

### Step 1: Understand Schema
- orders: order_date, customer_id (FK), product_id (FK)
- customers: status (VARCHAR2, skewed — 90% ACTIVE, 10% INACTIVE)
- Histograms may be missing on status

### Step 2: Think Aloud
The optimizer uses the predicate `c.status = 'ACTIVE'` to estimate cardinality. If it assumes uniform distribution (50%), it expects 25M customers. The actual ACTIVE count is 45M. This leads the optimizer to choose a HASH JOIN with products as the inner table (small), but after joining with 45M customers, it should have chosen Orders as the driving table with date-based partition pruning.

### Step 3: Write the Optimized Query
```sql
-- Gather histograms first
EXEC DBMS_STATS.GATHER_TABLE_STATS('SH', 'CUSTOMERS', method_opt => 'FOR COLUMNS STATUS SIZE 254');

-- Then run the query with hints to guide the optimizer
SELECT /*+ LEADING(o c p)
           USE_HASH(c)
           USE_HASH(p)
           FULL(o)
           FULL(c)
           FULL(p) */
       c.name, o.order_date, p.product_name
  FROM orders o
  JOIN customers c ON o.customer_id = c.id
  JOIN products p ON o.product_id = p.id
 WHERE o.order_date BETWEEN DATE '2024-01-01' AND DATE '2024-12-31'
   AND c.status = 'ACTIVE';
```

### Step 4: Execution Plan Analysis
Bad plan (no histograms):
```
----------------------------------------------------------------------
| Id | Operation             | Name     | Rows  | Cost   | Time     |
----------------------------------------------------------------------
|  0 | SELECT STATEMENT      |          | 1M    | 850K   | 2+ hrs   |
|  1 |  HASH JOIN            |          | 1M    | 850K   |          |
|  2 |   HASH JOIN           |          | 1M    | 500K   |          |
|  3 |    TABLE ACCESS FULL  | CUSTOMERS| 25M   | 200K   |          |
|  4 |    TABLE ACCESS FULL  | ORDERS   | 100M  | 300K   |          |
|  5 |   TABLE ACCESS FULL   | PRODUCTS | 10M   | 50K    |          |
----------------------------------------------------------------------
```

Good plan (with histograms):
```
----------------------------------------------------------------------
| Id | Operation                | Name     | Rows  | Cost | Time    |
----------------------------------------------------------------------
|  0 | SELECT STATEMENT         |          | 45M   | 2.5M | 30 min  |
|  1 |  HASH JOIN               |          | 45M   | 2.5M |         |
|  2 |   HASH JOIN              |          | 45M   | 2.0M |         |
|  3 |    TABLE ACCESS FULL     | ORDERS   | 100M  | 300K |         |
|  4 |    TABLE ACCESS FULL     | CUSTOMERS| 45M   | 350K |         |
|  5 |   TABLE ACCESS FULL      | PRODUCTS | 10M   | 50K  |         |
----------------------------------------------------------------------
```

The plan changes: Orders becomes the driving table (cardinality estimate for Customers doubled to 45M, making it the larger table to hash).

### Step 5: Optimize Further

1. **Partition pruning** on orders.order_date:
```sql
CREATE TABLE orders_part
  PARTITION BY RANGE (order_date)
  (PARTITION p_2024_q1 VALUES LESS THAN (DATE '2024-04-01'),
   PARTITION p_2024_q2 VALUES LESS THAN (DATE '2024-07-01'),
   PARTITION p_2024_q3 VALUES LESS THAN (DATE '2024-10-01'),
   PARTITION p_2024_q4 VALUES LESS THAN (DATE '2025-01-01'))
AS SELECT * FROM orders;
```

2. **Bitmap index** on customers.status:
```sql
CREATE BITMAP INDEX cust_status_bmx ON customers(status);
```

3. **Materialized view** with pre-joined data:
```sql
CREATE MATERIALIZED VIEW LOG ON orders WITH ROWID (customer_id, product_id, order_date) INCLUDING NEW VALUES;
CREATE MATERIALIZED VIEW LOG ON customers WITH ROWID (id, name, status) INCLUDING NEW VALUES;
CREATE MATERIALIZED VIEW LOG ON products WITH ROWID (id, product_name) INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW order_summary
REFRESH FAST ON COMMIT
AS
SELECT c.name, o.order_date, p.product_name
  FROM orders o
  JOIN customers c ON o.customer_id = c.id
  JOIN products p ON o.product_id = p.id;
```

### Step 6: Test
```sql
-- Compare execution plans with different date ranges
EXPLAIN PLAN SET STATEMENT_ID 'JAN' FOR
SELECT ... WHERE o.order_date BETWEEN DATE '2024-01-01' AND DATE '2024-01-31';

EXPLAIN PLAN SET STATEMENT_ID 'DEC' FOR
SELECT ... WHERE o.order_date BETWEEN DATE '2024-12-01' AND DATE '2024-12-31';

-- Check for adaptive cursor sharing
SELECT child_number, executions, buffer_gets, is_bind_sensitive, is_bind_aware
  FROM v$sql
 WHERE sql_text LIKE '%order_date BETWEEN%';
```

Edge cases:
- **Bind variable peeking**: Different date ranges may need different plans. Use adaptive cursor sharing or ACS.
- **Frequency histograms**: For status (2 values), frequency histogram gives exact cardinality.
- **Dynamic sampling**: For ad-hoc queries, use `/*+ DYNAMIC_SAMPLING(4) */`.

### Company Evaluation
- **Oracle**: Deep execution plan analysis, DBMS_STATS, histograms, adaptive cursor sharing.
- **Amazon**: Redshift — analyze compression, sort keys, distribution styles. VACUUM and ANALYZE.
- **Google**: Spanner — interleaved tables, secondary indexes, and stale read replicas.

---

## Problem 3: Index-Only Access Path Design (LC SQL 1978 variant)

### Interview Scenario
"Amazon interview: A reporting query against a 100M row Sales table runs for 5 minutes. The query only needs 3 columns but accesses the full table. Re-design to achieve sub-second response."

### The Problem
```sql
SELECT store_id, SUM(amount), COUNT(*)
  FROM sales
 WHERE sale_date >= SYSDATE - 30
 GROUP BY store_id;
```

Current response: 5 minutes. The table has:
- 30 columns
- 100M rows
- 1GB for the 3 needed columns, 8GB total row size

### Step 1: Understand Schema
- sales: sale_id (PK), store_id, sale_date, amount, and 25 other columns
- Only need: store_id, amount, sale_date
- 30-day window filters ~10M rows (10% of table)

### Step 2: Think Aloud
The query does a full table scan because there's no covering index. Oracle reads all 30 columns (8GB) when only 3 are needed (1GB). A covering index on (sale_date, store_id, amount) would allow an INDEX RANGE SCAN with no table access.

### Step 3: Write the Optimized Query
Same query — optimization is in the index, not the SQL:
```sql
SELECT store_id, SUM(amount), COUNT(*)
  FROM sales
 WHERE sale_date >= SYSDATE - 30
 GROUP BY store_id;
```

### Step 4: Execution Plan Comparison
Before:
```
-------------------------------------------------------------------
| Id | Operation          | Name  | Rows   | Cost   | Time     |
-------------------------------------------------------------------
|  0 | SELECT STATEMENT   |       | 10M    | 1.5M   | 5 min    |
|  1 |  SORT GROUP BY     |       | 10K    | 1.5M   |          |
|  2 |   TABLE ACCESS FULL| SALES | 100M   | 1.2M   |          |
-------------------------------------------------------------------
```

After creating the covering index:
```sql
CREATE INDEX sales_date_store_amount_idx
  ON sales(sale_date, store_id, amount) COMPRESS 2;
```

```
-------------------------------------------------------------------
| Id | Operation                   | Name                       | Cost |
-------------------------------------------------------------------
|  0 | SELECT STATEMENT            |                            |      |
|  1 |  SORT GROUP BY              |                            | 5K   |
|  2 |   INDEX RANGE SCAN          | SALES_DATE_STORE_AMOUNT_IDX| 4K   |
-------------------------------------------------------------------
```

Cost drops from 1.5M to 5K — ~300x improvement. The index with COMPRESS 2 stores duplicate sale_date and store_id values once, making the index even smaller.

### Step 5: Optimize Further

1. **Materialized view** for pre-aggregated daily totals:
```sql
CREATE MATERIALIZED VIEW daily_sales_summary
REFRESH FAST ON COMMIT
AS
SELECT TRUNC(sale_date) AS day, store_id,
       SUM(amount) AS daily_amount,
       COUNT(*) AS daily_count
  FROM sales
 GROUP BY TRUNC(sale_date), store_id;
```

Query against MV:
```sql
SELECT store_id, SUM(daily_amount), SUM(daily_count)
  FROM daily_sales_summary
 WHERE day >= TRUNC(SYSDATE - 30)
 GROUP BY store_id;
```

This reads 30 rows (one per day per store) instead of 10M rows.

2. **Partitioning** by month with partition-wise aggregation:
```sql
CREATE TABLE sales_part
  PARTITION BY RANGE (sale_date)
  INTERVAL(NUMTOYMINTERVAL(1, 'MONTH'))
  (PARTITION p_initial VALUES LESS THAN (DATE '2024-01-01'))
AS SELECT * FROM sales;
```

3. **In-Memory Column Store**:
```sql
ALTER TABLE sales INMEMORY;
ALTER TABLE sales INMEMORY MEMCOMPRESS FOR QUERY LOW;
```

Oracle IM column store stores only the accessed columns in memory, giving sub-second response for column scans.

### Step 6: Test
```sql
-- Verify index usage
EXPLAIN PLAN FOR
SELECT /*+ INDEX(sales sales_date_store_amount_idx) */
       store_id, SUM(amount), COUNT(*)
  FROM sales
 WHERE sale_date >= SYSDATE - 30
 GROUP BY store_id;

-- Check index size
SELECT segment_name, bytes/1024/1024 AS size_mb
  FROM user_segments
 WHERE segment_name = 'SALES_DATE_STORE_AMOUNT_IDX';

-- Test with different date ranges to ensure index is selective enough
SELECT COUNT(*) FROM sales WHERE sale_date >= SYSDATE - 30;
-- If > 20% of rows, full scan may still be preferred by optimizer
```

Edge cases:
- **Index selectivity**: If 30-day window covers 90% of rows, Oracle may still choose full table scan. The covering index is still faster because it's smaller.
- **Null store_id or amount**: NOT NULL constraints avoid NULL entries in index. If NULLs exist, they're stored at the end of the index.
- **Index maintenance**: DML overhead on 3-column index vs full table scans — trade-off for write-heavy tables.

### Company Evaluation
- **Oracle**: Covering indexes, index compression, materialized views, In-Memory Column Store. DBMS_XPLAN for verification.
- **Amazon**: Redshift sort keys (compound vs interleaved) and distribution styles achieve the same.
- **Google**: Spanner interleaved tables and storing indexes.
