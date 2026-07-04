# Performance

## Partitioning
```java
data.repartition(col("country")).sortWithinPartitions("order_date");
```

## Caching
```java
spark.sqlContext().cacheTable("dim_country");
```

## Broadcast Joins
```java
largeDF.join(smallDF.hint("broadcast"), "key");
```

## Adaptive Query
```
spark.sql.adaptive.enabled=true
spark.sql.adaptive.coalescePartitions.enabled=true
```
