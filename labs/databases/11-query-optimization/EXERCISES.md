# Exercises: Query Optimization

## Exercise 1 – EXPLAIN Reading
Given a schema with `orders(user_id, status, created_at, total)`:
- Write a query filtering by status and sorting by created_at
- Run `EXPLAIN ANALYZE` before and after adding an index
- Compare cost, actual time, rows

## Exercise 2 – N+1 Detection
- Create entities `Author` and `Book` with `@OneToMany`
- Query all authors and access their book count
- Enable SQL logging and count queries
- Fix with `@EntityGraph` and verify query count drops to 1

## Exercise 3 – Index Strategy
- Create a table with 1M rows
- Design indexes for these query patterns:
  - `WHERE status = 'ACTIVE' AND created_at > '2024-01-01'`
  - `WHERE email = 'test@example.com'`
  - `WHERE name ILIKE '%search%'`
- Verify each with EXPLAIN ANALYZE

## Exercise 4 – Query Rewriting
Take these slow queries and rewrite them:
- `SELECT * FROM orders WHERE EXTRACT(YEAR FROM created_at) = 2024`
- `SELECT * FROM users WHERE CONCAT(first_name, ' ', last_name) = 'John Doe'`
- `SELECT COUNT(*) FROM (SELECT DISTINCT user_id FROM orders) sub`

## Exercise 5 – JPA Performance
- Create a service that retrieves 100 orders with their items
- Measure execution time using JDBC batch vs JPA findAll
- Compare query count and total time
- Optimize to minimize database round trips
