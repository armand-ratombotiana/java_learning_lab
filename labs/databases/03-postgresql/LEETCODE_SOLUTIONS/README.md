# LEETCODE_SOLUTIONS — PostgreSQL

## PostgreSQL-Specific Features

| Feature | LeetCode Use Case | PostgreSQL Syntax |
|---------|------------------|-------------------|
| DISTINCT ON | Top-N per group | `SELECT DISTINCT ON (col) ... ORDER BY col, ...` |
| GENERATE_SERIES | Date series, number sequences | `GENERATE_SERIES(start, stop, step)` |
| ARRAY_AGG | Aggregate names per group | `ARRAY_AGG(name ORDER BY col)` |
| UNNEST | Convert array to rows | `UNNEST(arr)` |
| ARRAY | Column aggregations | `ARRAY[...]` |
| JSONB | Query JSON columns | `data @> '{"key": "val"}'` |
| LATERAL | Subquery per row | `LEFT JOIN LATERAL ... ON TRUE` |
| ON CONFLICT | UPSERT | `INSERT ... ON CONFLICT DO UPDATE` |
| FULLTEXT | Text search | `TO_TSVECTOR @@ PLAINTO_TSQUERY` |

### Example: Top 2 Products per Category
```sql
SELECT c.name, p.name, p.price
FROM categories c
LEFT JOIN LATERAL (
    SELECT name, price FROM products
    WHERE category_id = c.id
    ORDER BY price DESC LIMIT 2
) p ON TRUE;
```
