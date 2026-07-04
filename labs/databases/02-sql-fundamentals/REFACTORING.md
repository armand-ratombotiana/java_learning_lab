# Refactoring: SQL Queries

## Before: Nested Subquery

```sql
SELECT name, salary
FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees)
ORDER BY salary DESC;
```

## After: Window Function

```sql
SELECT name, salary
FROM (
    SELECT name, salary, AVG(salary) OVER () AS avg_salary
    FROM employees
) sub
WHERE salary > avg_salary
ORDER BY salary DESC;
```

Both work, but the window version avoids scanning the table twice.

## Before: Correlated Subquery

```sql
SELECT e.name, e.salary
FROM employees e
WHERE e.salary = (SELECT MAX(salary) FROM employees WHERE dept_id = e.dept_id);
```

## After: Window Function

```sql
SELECT name, salary, dept_id
FROM (
    SELECT name, salary, dept_id,
           RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rank
    FROM employees
) ranked
WHERE rank = 1;
```

## Before: Self-Join for Running Total

```sql
SELECT e1.id, e1.salary, SUM(e2.salary) AS running_total
FROM employees e1
JOIN employees e2 ON e2.id <= e1.id
GROUP BY e1.id, e1.salary;
```

## After: Window Function

```sql
SELECT id, salary, SUM(salary) OVER (ORDER BY id) AS running_total
FROM employees;
```

## Before: Multiple Queries in Loop

```java
// N+1: one query per department
List<Department> depts = departmentRepo.findAll();
for (Department d : depts) {
    List<Employee> emps = employeeRepo.findByDepartmentId(d.getId());
    // process ...
}
```

## After: Single Query with JOIN

```java
@Query("SELECT d FROM Department d JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

## Before: UNION for Conditional Counts

```sql
SELECT 'active' AS status, COUNT(*) FROM employees WHERE active = true
UNION ALL
SELECT 'inactive', COUNT(*) FROM employees WHERE active = false;
```

## After: Conditional Aggregation

```sql
SELECT
    COUNT(*) FILTER (WHERE active) AS active_count,
    COUNT(*) FILTER (WHERE NOT active) AS inactive_count
FROM employees;
```
