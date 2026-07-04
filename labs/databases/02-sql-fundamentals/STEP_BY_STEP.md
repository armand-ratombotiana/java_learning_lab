# Step by Step: Writing an Optimized SQL Query

## Goal
Find the top 5 departments with the highest average salary, where the department has at least 10 employees.

## Step 1: Start with the base data
```sql
SELECT d.name, e.salary
FROM departments d
JOIN employees e ON d.id = e.dept_id;
```

## Step 2: Add WHERE filter (optional)
```sql
-- (skip if no filtering needed)
```

## Step 3: Group by department
```sql
SELECT d.name AS department, AVG(e.salary) AS avg_salary
FROM departments d
JOIN employees e ON d.id = e.dept_id
GROUP BY d.name;
```

## Step 4: Filter groups (HAVING)
```sql
SELECT d.name AS department, AVG(e.salary) AS avg_salary, COUNT(e.id) AS emp_count
FROM departments d
JOIN employees e ON d.id = e.dept_id
GROUP BY d.name
HAVING COUNT(e.id) >= 10;
```

## Step 5: Order and limit
```sql
SELECT d.name AS department, AVG(e.salary) AS avg_salary, COUNT(e.id) AS emp_count
FROM departments d
JOIN employees e ON d.id = e.dept_id
GROUP BY d.name
HAVING COUNT(e.id) >= 10
ORDER BY avg_salary DESC
LIMIT 5;
```

## Step 6: Add window function (bonus)
```sql
SELECT d.name AS department,
       AVG(e.salary) AS avg_salary,
       COUNT(e.id) AS emp_count,
       RANK() OVER (ORDER BY AVG(e.salary) DESC) AS rank
FROM departments d
JOIN employees e ON d.id = e.dept_id
GROUP BY d.name
HAVING COUNT(e.id) >= 10
ORDER BY avg_salary DESC
LIMIT 5;
```

## Step 7: Verify with EXPLAIN
```sql
EXPLAIN ANALYZE <query>;
```

## Step 8: Add indexes if needed
```sql
CREATE INDEX idx_employees_dept_id ON employees(dept_id);
CREATE INDEX idx_employees_salary ON employees(salary DESC);
```

## Step 9: Convert to JPA
```java
@Query("""
    SELECT d.name AS department, AVG(e.salary) AS avgSalary, COUNT(e.id) AS empCount
    FROM Department d
    JOIN d.employees e
    GROUP BY d.name
    HAVING COUNT(e.id) >= :minEmployees
    ORDER BY AVG(e.salary) DESC
""")
List<Object[]> findTopDepartments(@Param("minEmployees") long minEmployees,
                                   Pageable pageable);
```
