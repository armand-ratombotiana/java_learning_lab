# Performance

## Materialized Views
```java
spark.sql("CREATE MATERIALIZED VIEW mv_monthly AS "
    + "SELECT product_key, DATE_TRUNC('month', date) as month, "
    + "SUM(amount) as revenue FROM fact_sales GROUP BY product_key, month");
```

## Partition Pruning
```sql
SELECT ... FROM fact_sales WHERE year=2024 AND month=6;
```
