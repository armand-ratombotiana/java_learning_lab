# Common Mistakes: SQL

## 1. SELECT * in Production
```sql
-- WRONG: fetches all columns, breaks if schema changes
SELECT * FROM employees;

-- RIGHT: explicit columns
SELECT id, name, email FROM employees;
```

## 2. NULL Comparison Gotchas
```sql
-- WRONG: NULL is never equal to anything
SELECT * FROM employees WHERE department_id = NULL;

-- RIGHT
SELECT * FROM employees WHERE department_id IS NULL;
```

## 3. NOT IN with NULL Subquery
```sql
-- WRONG: returns empty result if subquery contains NULL
SELECT * FROM employees WHERE id NOT IN (SELECT manager_id FROM employees);

-- RIGHT: use NOT EXISTS
SELECT * FROM employees e
WHERE NOT EXISTS (SELECT 1 FROM employees m WHERE m.manager_id = e.id);
```

## 4. Missing Indexes for JOIN/WHERE
```sql
-- Slow: seq scan on both tables
SELECT * FROM orders WHERE customer_id = 123;

-- Fix: add index
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
```

## 5. GROUP BY with Non-Aggregated Columns
```sql
-- WRONG: name not in GROUP BY or aggregate
SELECT dept_id, name, AVG(salary) FROM employees GROUP BY dept_id;

-- RIGHT: include name in GROUP BY or use aggregate
SELECT dept_id, AVG(salary) FROM employees GROUP BY dept_id;
```

## 6. Implicit Type Conversion Killing Indexes
```sql
-- WRONG: varchar column compared to int → implicit cast, index not used
SELECT * FROM products WHERE sku = 12345;

-- RIGHT: match the column type
SELECT * FROM products WHERE sku = '12345';
```

## 7. Large OFFSET for Pagination
```sql
-- WRONG: database must scan and discard rows
SELECT * FROM orders ORDER BY id LIMIT 20 OFFSET 100000;

-- RIGHT: keyset pagination
SELECT * FROM orders WHERE id > 100000 ORDER BY id LIMIT 20;
```
