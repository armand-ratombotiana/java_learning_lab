# Code Deep Dive: Star Schema Build

## Dimension Table
```java
spark.sql("CREATE TABLE dim_customer (customer_key BIGINT IDENTITY, "
    + "customer_id VARCHAR(50), name VARCHAR(200), email VARCHAR(200), "
    + "effective_date DATE, is_current BOOLEAN) USING DELTA");
```

## SCD Type 2
```java
spark.sql("MERGE INTO dim_customer t USING src s ON t.customer_id = s.customer_id "
    + "WHEN MATCHED AND t.name != s.name THEN UPDATE SET "
    + "t.end_date = CURRENT_DATE, t.is_current = FALSE "
    + "WHEN NOT MATCHED THEN INSERT *");
```
