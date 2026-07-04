# Debugging

## Slow Queries
```sql
EXPLAIN ANALYZE SELECT region, SUM(amount) FROM fact_sales GROUP BY region;
```

## Orphan Facts
```sql
SELECT COUNT(*) FROM fact_sales f LEFT JOIN dim_customer c
ON f.customer_key = c.customer_key WHERE c.customer_key IS NULL;
```
