# Theory: SQL Fundamentals

## SQL Statement Categories

| Category | Examples |
|---|---|
| DDL | CREATE, ALTER, DROP, TRUNCATE |
| DML | SELECT, INSERT, UPDATE, DELETE |
| DCL | GRANT, REVOKE |
| TCL | COMMIT, ROLLBACK, SAVEPOINT |

## SELECT Clause Order of Execution

```
FROM → JOIN → WHERE → GROUP BY → HAVING → 
WINDOW → SELECT → DISTINCT → UNION → ORDER BY → LIMIT/OFFSET
```

Understanding this order is critical for debugging queries.

## JOIN Types (Venn Diagram Logic)

| JOIN | Result |
|---|---|
| INNER | Only matching rows from both tables |
| LEFT | All left + matching right, NULL where no match |
| RIGHT | All right + matching left |
| FULL | All rows from both, NULL where no match |
| CROSS | Cartesian product (every row paired with every other) |

## Aggregate Functions
```sql
COUNT, SUM, AVG, MIN, MAX, STRING_AGG, ARRAY_AGG, BOOL_AND, BOOL_OR
```

## Window Functions
```sql
-- ROW_NUMBER: unique sequential number per partition
ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC)

-- RANK: same rank for ties, skips next
RANK() OVER (ORDER BY score DESC)

-- DENSE_RANK: same rank for ties, no skip
DENSE_RANK() OVER (ORDER BY score DESC)

-- NTILE: divides rows into N buckets
NTILE(4) OVER (ORDER BY sales DESC)  -- quartiles

-- LAG/LEAD: access previous/next row
LAG(salary) OVER (PARTITION BY dept_id ORDER BY hire_date)
```

## Subqueries
```sql
-- Scalar subquery (returns single value)
SELECT name, (SELECT AVG(salary) FROM employees) AS avg_salary;

-- Row subquery (returns single row)
SELECT * FROM employees
WHERE (dept_id, salary) = (SELECT dept_id, MAX(salary) FROM employees GROUP BY dept_id LIMIT 1);

-- Table subquery (returns set of rows)
SELECT * FROM (SELECT * FROM employees WHERE salary > 100000) AS high_earners;

-- EXISTS subquery
SELECT * FROM departments d
WHERE EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.id);
```

## JDBC Query Execution
```java
try (PreparedStatement ps = conn.prepareStatement(
        "SELECT e.name, d.name AS dept FROM employees e " +
        "JOIN departments d ON e.dept_id = d.id WHERE d.name = ?")) {
    ps.setString(1, "Engineering");
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name") + " - " + rs.getString("dept"));
        }
    }
}
```
