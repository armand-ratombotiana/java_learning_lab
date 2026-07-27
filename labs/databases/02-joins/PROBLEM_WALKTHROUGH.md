# SQL Problem Walkthrough: 02-joins

## Problem 1: Department Top Three Salaries (LC SQL 185) — Oracle/Amazon

### Interview Scenario
"You're interviewing at Oracle for a senior developer role. They give you the classic 'Department Top Three Salaries' problem: find employees who earn the top three unique salaries in each department."

### The Problem
Tables:
- **Employee**: id (NUMBER PK), name (VARCHAR2), salary (NUMBER), departmentId (NUMBER FK)
- **Department**: id (NUMBER PK), name (VARCHAR2)

Write a query to find employees who earn the top three unique salaries in each department. Return department name, employee name, and salary. Order by department name, then salary descending.

### Step 1: Understand Schema
- Employee.departmentId → Department.id relationship
- Salaries can have ties — need dense rank (unique salary values)
- A department with < 3 employees should return all of them

Sample data:
| Employee id | name  | salary | departmentId |
|-------------|-------|--------|--------------|
| 1           | Joe   | 85000  | 1            |
| 2           | Max   | 90000  | 1            |
| 3           | Randy | 85000  | 1            |
| 4           | Will  | 68000  | 1            |
| 5           | Janet | 69000  | 2            |

| Department id | name     |
|---------------|----------|
| 1             | IT       |
| 2             | Sales    |

### Step 2: Think Aloud
"Top N per group" problems require window functions. DENSE_RANK() assigns ranks without gaps, so ties get the same rank and the next rank is consecutive. ROW_NUMBER() would arbitrarily break ties, which isn't what we want.

Approach 1: DENSE_RANK() with PARTITION BY departmentId ORDER BY salary DESC
Approach 2: Correlated subquery counting distinct salaries (slower, older Oracle style)
Approach 3: LATERAL join with subquery (Oracle 12c+)

### Step 3: Write the Query
```sql
SELECT d.name AS department,
       e.name AS employee,
       e.salary
  FROM (
    SELECT name,
           salary,
           departmentId,
           DENSE_RANK() OVER (
             PARTITION BY departmentId
             ORDER BY salary DESC
           ) AS salary_rank
      FROM employee
  ) e
  JOIN department d
    ON e.departmentId = d.id
 WHERE e.salary_rank <= 3
 ORDER BY d.name, e.salary DESC;
```

Alternative using Oracle's LATERAL (12c+):
```sql
SELECT d.name AS department,
       e.name AS employee,
       e.salary
  FROM department d
  CROSS JOIN LATERAL (
    SELECT name, salary
      FROM employee
     WHERE departmentId = d.id
     ORDER BY salary DESC
     FETCH FIRST 3 ROWS WITH TIES
  ) e
 ORDER BY d.name, e.salary DESC;
```

### Step 4: Execution Plan
```sql
EXPLAIN PLAN FOR
SELECT d.name, e.name, e.salary
  FROM (SELECT name, salary, departmentId,
               DENSE_RANK() OVER (PARTITION BY departmentId ORDER BY salary DESC) AS sal_rank
          FROM employee) e
  JOIN department d ON e.departmentId = d.id
 WHERE e.sal_rank <= 3;
```

Expected plan:
1. **WINDOW SORT** — sorts employee rows by departmentId, salary DESC for the DENSE_RANK() computation
2. **VIEW** — inline view
3. **HASH JOIN** — between the ranked view and department
4. **TABLE ACCESS FULL** on department
5. **TABLE ACCESS FULL** on employee

Cost drivers: The WINDOW SORT is the heaviest operation. With an index on (departmentId, salary DESC), Oracle can avoid the full sort.

### Step 5: Optimize
```sql
CREATE INDEX emp_dept_sal_idx ON employee(departmentId, salary DESC);
```

With this composite index:
- Oracle can use INDEX RANGE SCAN for each department
- The window sort becomes cheaper because data arrives pre-sorted
- For the LATERAL approach, the index is used for repeated range scans per department

At massive scale, consider:
- Partitioning employee by departmentId
- Materialized view pre-computing the top N salaries per department
```sql
CREATE MATERIALIZED VIEW LOG ON employee WITH ROWID (departmentId, salary) INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW dept_top_salaries
REFRESH FAST ON COMMIT
AS
SELECT departmentId, salary
  FROM employee;
```

### Step 6: Test
Edge cases:
- **Ties at rank boundary**: DENSE_RANK handles correctly — if 3 employees tie at rank 2, all 3 are included
- **Department with 1 employee**: Returns that one (rank 1 <= 3)
- **Duplicate salaries**: All with the same salary get the same rank
- **NULL salaries**: DENSE_RANK treats NULLs as the largest value by default (ASC) or smallest (DESC). Need to handle if present.

```sql
-- Test case with ties
WITH emp_test AS (
  SELECT 1 AS id, 'A' AS name, 100 AS salary, 1 AS dept_id FROM dual UNION ALL
  SELECT 2, 'B', 100, 1 FROM dual UNION ALL
  SELECT 3, 'C', 90, 1 FROM dual UNION ALL
  SELECT 4, 'D', 90, 1 FROM dual UNION ALL
  SELECT 5, 'E', 80, 1 FROM dual
)
SELECT name, salary,
       DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk
  FROM emp_test
 WHERE rnk <= 3;
-- Returns A(100,1), B(100,1), C(90,2), D(90,2), E(80,3)
```

### Company Evaluation
- **Oracle**: Prefer DENSE_RANK or LATERAL + FETCH WITH TIES. Discuss execution plan interpretation.
- **Amazon**: Emphasize scalable approach — partition-aware querying. Discuss how this translates to Redshift.
- **Google**: In Spanner, use ARRAY_AGG with LIMIT in subqueries.

---

## Problem 2: Customers Who Never Order (LC SQL 183) — Amazon

### Interview Scenario
"Amazon interview: Find all customers who have never placed an order. You have two tables: Customers and Orders."

### The Problem
Tables: **Customers** (id NUMBER PK, name VARCHAR2), **Orders** (id NUMBER PK, customerId NUMBER FK). Write a query to find all customers who never placed an order. Return name only.

### Step 1: Understand Schema
- Customers.id ⟶ Orders.customerId
- Some customers may have zero orders
- Orders.customerId can reference non-existent customers? No (FK constraint assumed)

Sample data:
| Customers id | name |
|--------------|------|
| 1            | Joe  |
| 2            | Henry|
| 3            | Sam  |
| 4            | Max  |

| Orders id | customerId |
|-----------|------------|
| 1         | 3          |
| 2         | 1          |

### Step 2: Think Aloud
Three approaches:
1. **LEFT JOIN / IS NULL** — most common, efficient
2. **NOT IN** — careful with NULLs in subquery
3. **NOT EXISTS** — safest, handles NULLs correctly, often most efficient in Oracle

### Step 3: Write the Query
```sql
-- Approach 1: LEFT JOIN (preferred for readability)
SELECT c.name
  FROM customers c
  LEFT JOIN orders o
    ON c.id = o.customerId
 WHERE o.id IS NULL;

-- Approach 2: NOT EXISTS (best for performance)
SELECT c.name
  FROM customers c
 WHERE NOT EXISTS (
   SELECT 1
     FROM orders o
    WHERE o.customerId = c.id
 );

-- Approach 3: NOT IN (caution: NULLs in subquery break this)
SELECT c.name
  FROM customers c
 WHERE c.id NOT IN (
   SELECT customerId FROM orders WHERE customerId IS NOT NULL
 );
```

### Step 4: Execution Plan
For the LEFT JOIN approach:
```
| Id | Operation           | Name      | Rows | Cost |
|----|---------------------|-----------|------|------|
|  0 | SELECT STATEMENT    |           |      |    4 |
|  1 |  HASH JOIN ANTI     |           |    2 |    4 |
|  2 |   TABLE ACCESS FULL | CUSTOMERS |    4 |    3 |
|  3 |   TABLE ACCESS FULL | ORDERS    |    2 |    3 |
```

Oracle's HASH JOIN ANTI is an anti-join — it finds rows in customers that don't match any row in orders. This is very efficient.

### Step 5: Optimize
```sql
CREATE INDEX orders_customer_idx ON orders(customerId);
```

With the index, NOT EXISTS becomes:
```
| Id | Operation                   | Name                | Rows |
|----|-----------------------------|---------------------|------|
|  0 | SELECT STATEMENT            |                     |      |
|  1 |  FILTER                     |                     |      |
|  2 |   TABLE ACCESS FULL         | CUSTOMERS           |    4 |
|  3 |   INDEX RANGE SCAN (UNIQUE) | ORDERS_CUSTOMER_IDX |    1 |
```

Oracle does a NESTED LOOPS ANTI — for each customer, it probes the index. Fast for small customers, large orders.

### Step 6: Test
Edge cases:
- **All customers ordered**: Returns empty set
- **No orders exist**: Returns all customers (they never ordered)
- **NULL in Orders.customerId**: NOT IN approach would fail silently; NOT EXISTS and LEFT JOIN handle it correctly

```sql
-- Test NULL scenario
INSERT INTO orders VALUES (3, NULL);

-- LEFT JOIN: Works correctly — the NULL customerId creates no match
-- NOT IN: Returns empty set — breaks!
-- NOT EXISTS: Works correctly
```

### Company Evaluation
- **Amazon**: Discuss anti-join patterns. Relate to DynamoDB — scan + filter vs query.
- **Oracle**: Execution plan differences between ANTI and NESTED LOOPS ANTI.
- **Google**: Spanner uses ANTI JOIN syntax. Discuss distributed anti-join challenges.

---

## Problem 3: Employees Earning More Than Their Managers (LC SQL 181) — Microsoft

### Interview Scenario
"Microsoft interview: The Employee table has a managerId column. Find employees who earn more than their own managers."

### The Problem
Table **Employee**: id (NUMBER PK), name (VARCHAR2), salary (NUMBER), managerId (NUMBER, nullable FK). Write a query to find employees whose salary exceeds their manager's salary. Return name as 'Employee'.

### Step 1: Understand Schema
- Self-referencing relationship: employee.managerId → employee.id
- CEO has managerId = NULL
- Compare employee.salary with manager.salary

Sample data:
| id | name  | salary | managerId |
|----|-------|--------|-----------|
| 1  | Joe   | 70000  | 3         |
| 2  | Henry | 80000  | 4         |
| 3  | Sam   | 60000  | NULL      |
| 4  | Max   | 90000  | NULL      |

### Step 2: Think Aloud
This is a classic self-join. Join Employee to itself where E1.managerId = E2.id, then filter where E1.salary > E2.salary.

### Step 3: Write the Query
```sql
SELECT e1.name AS employee
  FROM employee e1
  JOIN employee e2
    ON e1.managerId = e2.id
 WHERE e1.salary > e2.salary;
```

### Step 4: Execution Plan
```
| Id | Operation          | Name     | Rows | Cost |
|----|--------------------|----------|------|------|
|  0 | SELECT STATEMENT   |          |      |    6 |
|  1 |  HASH JOIN         |          |    1 |    6 |
|  2 |   TABLE ACCESS FULL| EMPLOYEE |    4 |    3 |
|  3 |   TABLE ACCESS FULL| EMPLOYEE |    4 |    3 |
```

Oracle reads the employee table twice and performs a HASH JOIN on the managerId = id condition.

### Step 5: Optimize
```sql
CREATE INDEX emp_mgr_idx ON employee(managerId);
CREATE INDEX emp_id_sal_idx ON employee(id, salary);
```

With both indexes, Oracle can use NESTED LOOPS — for each employee, probe the manager's salary via the id index.

### Step 6: Test
Edge cases:
- **CEO (NULL managerId)**: Excluded by JOIN (ON condition fails for NULL)
- **Multiple employees with same manager**: Each compared independently
- **Manager earns more than all reports**: Empty result set
- **Circular reference**: Unlikely with FK constraint, but would cause issues

```sql
-- Test data
WITH emp_test AS (
  SELECT 1 AS id, 'Alice' AS name, 50000 AS salary, NULL AS mgr_id FROM dual UNION ALL
  SELECT 2, 'Bob', 60000, 1 FROM dual UNION ALL
  SELECT 3, 'Carol', 40000, 1 FROM dual
)
SELECT e1.name AS employee
  FROM emp_test e1
  JOIN emp_test e2 ON e1.mgr_id = e2.id
 WHERE e1.salary > e2.salary;
-- Returns Bob (60000 > 50000)
```

### Company Evaluation
- **Microsoft**: Discuss T-SQL vs Oracle syntax. In T-SQL, same query works. Discuss recursion with CTEs for org hierarchy.
- **Oracle**: Emphasize self-join optimization. Consider hierarchical queries (CONNECT BY) for deeper org analysis.
- **Amazon**: Scale consideration — with millions of employees, partitioning by department improves self-join performance.
