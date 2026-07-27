# SQL Problem Walkthrough: 01-sql-basics

## Problem 1: Find Employees Hired in Last N Days (LC SQL 595) — Oracle

### Interview Scenario
"You're interviewing at Oracle for a database developer role. The interviewer asks you to write a query that returns all employees hired within the last 90 days from the employees table."

### The Problem
Write a SQL query to find all employees who were hired in the last 90 days. Return employee_id, first_name, last_name, hire_date, and department_id. Order results by hire_date descending.

### Step 1: Understand Schema
- **employees** table: employee_id (NUMBER PK), first_name (VARCHAR2), last_name (VARCHAR2), email (VARCHAR2), phone_number (VARCHAR2), hire_date (DATE), job_id (VARCHAR2), salary (NUMBER), commission_pct (NUMBER), manager_id (NUMBER), department_id (NUMBER)
- hire_date stores the date when the employee was hired
- SYSDATE returns the current date and time from the Oracle server

Sample data:
| employee_id | first_name | last_name | hire_date  | department_id |
|-------------|------------|-----------|------------|---------------|
| 100         | Steven     | King      | 17-JUN-03  | 90            |
| 101         | Neena      | Kochhar   | 21-SEP-05  | 90            |
| 200         | Jennifer   | Whalen    | 17-SEP-03  | 10            |

### Step 2: Think Aloud
The core condition is filtering rows where hire_date falls within a window starting 90 days ago and ending today. In Oracle, DATE arithmetic is straightforward: SYSDATE - 90 gives the date 90 days ago. The WHERE clause becomes `hire_date >= SYSDATE - 90`.

Consider edge cases: What if hire_date contains a time component? Oracle DATE includes time by default, so comparing with `>=` is safe. If we wanted just dates without time, we could use `TRUNC(hire_date) >= TRUNC(SYSDATE) - 90`.

### Step 3: Write the Query
```sql
SELECT employee_id,
       first_name,
       last_name,
       hire_date,
       department_id
  FROM employees
 WHERE hire_date >= SYSDATE - 90
 ORDER BY hire_date DESC;
```

Comments on each part:
- `SYSDATE - 90` performs date arithmetic — Oracle subtracts 90 days from the current date
- The WHERE clause filters for hires on or after that cutoff
- ORDER BY DESC puts most recent hires first

### Step 4: Execution Plan
```sql
EXPLAIN PLAN FOR
SELECT employee_id, first_name, last_name, hire_date, department_id
  FROM employees
 WHERE hire_date >= SYSTIMESTAMP - 90
 ORDER BY hire_date DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);
```

Expected plan:
- **FULL TABLE SCAN** on employees if no index exists (cost ~3-4 for a small HR schema)
- **SORT ORDER BY** to order by hire_date DESC

For a 100-row table, a full scan is acceptable. At scale, Oracle would likely choose an index range scan.

### Step 5: Optimize
Create an index on hire_date to avoid full table scan:
```sql
CREATE INDEX emp_hire_date_idx ON employees(hire_date DESC);
```

With this index:
- Oracle performs an **INDEX RANGE SCAN** (cost drops from ~3 to ~1)
- The index is already sorted DESC, so the SORT ORDER BY step is eliminated
- For larger tables (millions of rows), partitioning by date range is recommended:
```sql
CREATE TABLE employees_part
  PARTITION BY RANGE (hire_date)
  (PARTITION p_old VALUES LESS THAN (DATE '2020-01-01'),
   PARTITION p_mid VALUES LESS THAN (DATE '2025-01-01'),
   PARTITION p_recent VALUES LESS THAN (MAXVALUE))
AS SELECT * FROM employees;
```

### Step 6: Test
Edge cases:
- **NULL hire_date**: These rows would be excluded (NULL comparisons yield FALSE/UNKNOWN). Decide whether to include them with `OR hire_date IS NULL`.
- **No hires in last 90 days**: Returns empty result set — valid behavior.
- **Future hire_date**: Included if `hire_date > SYSDATE`. May need `AND hire_date <= SYSDATE` to exclude future dates.
- **SYSDATE vs SYSTIMESTAMP**: SYSDATE is DATE precision (seconds); SYSTIMESTAMP includes fractional seconds and timezone.

```sql
-- Test with NULL handling
SELECT employee_id, first_name, last_name, hire_date, department_id
  FROM employees
 WHERE (hire_date >= SYSDATE - 90 OR hire_date IS NULL)
 ORDER BY hire_date DESC;
```

Performance at scale: With 10M rows and an index on hire_date, the index range scan returns results almost instantly because Oracle reads only the relevant index blocks and does a table access by rowid for matching rows only.

### Company Evaluation
- **Oracle**: Focus on DATE arithmetic, SYSDATE function, and execution plan reading. Mention TRUNC for time-portion removal.
- **Amazon**: Emphasize partition pruning if the table is date-partitioned. Discuss DynamoDB alternatives.
- **Google**: In Spanner, use `TIMESTAMP_SUB(CURRENT_TIMESTAMP(), INTERVAL 90 DAY)` — Oracle uses date arithmetic, other dialects differ.
- **Microsoft**: T-SQL uses `GETDATE()` and `DATEADD(day, -90, GETDATE())`.

---

## Problem 2: Big Countries (LC SQL 595) — Amazon

### Interview Scenario
"You're at an Amazon interview. The interviewer wants you to write a query to find all countries that are 'big' according to a specific definition: a country is big if it has an area of at least 3,000,000 km² or a population of at least 25,000,000."

### The Problem
Table `World` has columns: name (VARCHAR2), continent (VARCHAR2), area (NUMBER), population (NUMBER), gdp (NUMBER). Write a query to find the name, population, and area of big countries. Return the table ordered by name.

### Step 1: Understand Schema
- **World**: name (PK), continent, area (in km²), population, gdp
- Condition: area >= 3,000,000 OR population >= 25,000,000

Sample data:
| name        | continent | area     | population | gdp         |
|-------------|-----------|----------|------------|-------------|
| Afghanistan | Asia      | 652230   | 25500100   | 20343000000 |
| Albania     | Europe    | 28748    | 2831741    | 12960000000 |
| Algeria     | Africa    | 2381741  | 37100000   | 188681000000|

### Step 2: Think Aloud
This is a straightforward SELECT with a WHERE clause combining two conditions with OR. The problem tests basic SQL syntax and understanding of comparison operators. No joins, no aggregation needed.

### Step 3: Write the Query
```sql
SELECT name,
       population,
       area
  FROM world
 WHERE area >= 3000000
    OR population >= 25000000
 ORDER BY name;
```

### Step 4: Execution Plan
Full table scan is expected. Since there's no filtering selectivity (both conditions are broad), Oracle will likely do a full scan regardless of indexes. With an index on area, if most rows have area < 3M, Oracle might do an INDEX FAST FULL SCAN.

### Step 5: Optimize
Create a composite index:
```sql
CREATE INDEX world_area_pop_idx ON world(area, population);
```

For Oracle, consider a function-based index or IOT if this is the primary query pattern:
```sql
-- Index Organized Table for fast lookup
CREATE TABLE world_iot (
  name VARCHAR2(50),
  continent VARCHAR2(50),
  area NUMBER,
  population NUMBER,
  gdp NUMBER,
  PRIMARY KEY (name)
) ORGANIZATION INDEX;
```

### Step 6: Test
Edge cases:
- **Area exactly 3,000,000**: Included (>=)
- **Population exactly 25,000,000**: Included
- **Area NULL**: NULL >= 3M is UNKNOWN, but OR condition may still include the row if population qualifies
- **Duplicate names**: Not possible (PK)

```sql
-- Verify with test data
SELECT name, population, area,
       CASE
         WHEN area >= 3000000 THEN 'Large area'
         ELSE 'Large population'
       END AS reason
  FROM world
 WHERE area >= 3000000 OR population >= 25000000
 ORDER BY name;
```

### Company Evaluation
- **Amazon**: Focus on scalability — how would this query perform on a world population dataset with billions of rows?
- **Oracle**: Emphasize index choices and execution plan analysis.

---

## Problem 3: Find Customer Referee (LC SQL 584) — Google

### Interview Scenario
"Google interview: You have a Customer table. Given a condition about a referee_id column, find customers who are not referred by customer with id = 2."

### The Problem
Table `Customer`: id (NUMBER PK), name (VARCHAR2), referee_id (NUMBER, nullable FK to Customer.id). Find the names of customers that are not referred by the customer with id = 2. Return names in any order.

### Step 1: Understand Schema
- **Customer**: id is primary key, referee_id can be NULL (means no referral)
- Need to exclude rows where referee_id = 2
- NULL referee_id means the customer was not referred — they SHOULD be included

Sample data:
| id | name | referee_id |
|----|------|------------|
| 1  | Will | NULL       |
| 2  | Jane | NULL       |
| 3  | Alex | 2          |
| 4  | Bill | NULL       |
| 5  | Zack | 1          |

### Step 2: Think Aloud
The tricky part is NULL handling. `referee_id != 2` evaluates to NULL when referee_id is NULL, and NULL rows are excluded by default in SQL. The correct approach uses `referee_id != 2 OR referee_id IS NULL`.

### Step 3: Write the Query
```sql
SELECT name
  FROM customer
 WHERE referee_id != 2
    OR referee_id IS NULL;
```

Alternative using NVL:
```sql
SELECT name
  FROM customer
 WHERE NVL(referee_id, -1) != 2;
```

Or using DECODE:
```sql
SELECT name
  FROM customer
 WHERE DECODE(referee_id, 2, 1, 0) = 0;
```

### Step 4: Execution Plan
Full table scan. For a small reference table, this is fine. At scale with millions of customers, a bitmap index on referee_id would help:
```sql
CREATE BITMAP INDEX cust_referee_bmx ON customer(referee_id);
```
Oracle bitmap indexes handle NULLs and low-cardinality columns efficiently.

### Step 5: Optimize
If referee_id has high cardinality, use a B-tree index:
```sql
CREATE INDEX cust_referee_idx ON customer(referee_id);
```

But note: B-tree indexes don't include NULL entries (by default). The `IS NULL` predicate would still require a full scan unless we use a function-based index:
```sql
CREATE INDEX cust_referee_nvl_idx ON customer(NVL(referee_id, -1));
```

### Step 6: Test
Edge cases:
- **All customers have referee_id = 2**: Returns empty set
- **All have NULL**: Returns all names (correct)
- **Mix of NULL and 2**: Only non-2 and NULLs returned

```sql
-- Comprehensive test
WITH test_data AS (
  SELECT 1 AS id, 'Alice' AS name, NULL AS referee_id FROM dual UNION ALL
  SELECT 2, 'Bob', 2 FROM dual UNION ALL
  SELECT 3, 'Carol', 1 FROM dual UNION ALL
  SELECT 4, 'Dave', 2 FROM dual UNION ALL
  SELECT 5, 'Eve', NULL FROM dual
)
SELECT name
  FROM test_data
 WHERE referee_id IS NULL OR referee_id != 2;
```

### Company Evaluation
- **Google**: Focus on NULL semantics in SQL. Emphasize three-valued logic (TRUE/FALSE/UNKNOWN).
- **Oracle**: Discuss NVL, DECODE, and CASE alternatives — idiomatic Oracle.
- **Amazon**: Discuss how this query would be handled in a distributed DB.
