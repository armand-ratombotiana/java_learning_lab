# Performance

## Compaction
```sql
OPTIMIZE delta./path ZORDER BY (customer_id, product_id);
```

## Partitioning
```java
data.write().partitionBy("year","month").format("delta").save("...");
```

## File Sizing
```java
spark.conf().set("spark.sql.files.maxPartitionBytes", "134217728");
```
