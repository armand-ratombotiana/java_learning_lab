# Performance

## Sampling
```java
Dataset<Row> sample = data.sample(false, 0.01); // 1%
qualityChecks(sample);
```

## Approximate Counts
```java
data.agg(approx_count_distinct(col("id"))).show();
```

## Incremental Checks
```java
Dataset<Row> incremental = data.filter(
    col("etl_loaded_at").$greater(lastCheckTime));
```
