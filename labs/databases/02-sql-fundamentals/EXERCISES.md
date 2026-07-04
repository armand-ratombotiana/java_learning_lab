# Exercises: SQL Fundamentals

## Exercise 1: Basic SELECT
Given tables `employees(id, name, salary, dept_id, hire_date)` and `departments(id, name)`:

1. Find employees earning more than the average salary
2. Find the top 3 highest-paid employees per department

## Exercise 2: JOIN Practice
Write SQL to:
1. List all employees with their department names (including those without a department)
2. Find departments with no employees

## Exercise 3: Window Functions
```sql
-- Given sales(id, product_id, amount, sale_date)
-- Calculate running total of sales per product ordered by date
-- Rank products by total sales
-- Compare each sale to the previous sale for the same product (LAG)
```

## Exercise 4: CTE (Common Table Expression)
Write a recursive CTE to find all subordinates of a given manager (employee has `manager_id` column).

## Exercise 5: JDBC to JPA Migration
Convert this JDBC query to JPA:
```java
String sql = "SELECT d.name, COUNT(e.id) as emp_count " +
             "FROM departments d LEFT JOIN employees e ON d.id = e.dept_id " +
             "GROUP BY d.name HAVING COUNT(e.id) > 5";
```

## Exercise 6: Query Optimization
Given `EXPLAIN ANALYZE` output showing a sequential scan on `orders` table with 5M rows:
- What index would you create?
- How would you rewrite the query?

## Exercise 7: SQL Injection
Identify and fix the SQL injection vulnerability:
```java
public User login(String username, String password) {
    String sql = "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'";
    return jdbcTemplate.queryForObject(sql, User.class);
}
```
