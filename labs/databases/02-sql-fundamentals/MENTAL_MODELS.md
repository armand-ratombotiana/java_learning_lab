# Mental Models for SQL

## 1. SQL as a Data Pipeline
Each clause is a stage in a pipeline:
```
FROM (raw data) → WHERE (filter) → GROUP BY (bucket) → 
HAVING (filter buckets) → SELECT (transform) → ORDER BY (sort)
```

## 2. JOIN as a Zipper
JOIN lines up rows from two tables based on matching keys, like zipping two sides of a zipper.

## 3. GROUP BY as a Sorting Machine
Think of GROUP BY as throwing rows into bins labeled by the grouping columns. Aggregates compute per bin.

## 4. Window Functions as Peeking Over Fences
Each row can "see" neighboring rows (partition). Unlike GROUP BY, rows are NOT collapsed.

## 5. Subqueries as Temporary Tables
A subquery is a disposable table that exists only for the outer query.

## 6. CTEs as Named Subqueries (Variables)
```sql
WITH expensive_products AS (
    SELECT * FROM products WHERE price > 100
)
SELECT * FROM expensive_products WHERE stock = 0;
```

## 7. NULL as Unknown
NULL ≠ 0, NULL ≠ empty string. NULL = unknown. Comparisons with NULL are always false (or unknown in three-valued logic).

## JDBC Mental Model
- `Connection` = phone line to database
- `Statement` = envelope with SQL message
- `PreparedStatement` = envelope with fill-in-the-blanks
- `ResultSet` = streaming cursor reading response rows
