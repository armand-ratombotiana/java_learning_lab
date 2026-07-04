# Performance

## Spark Tuning
```java
SparkSession.builder()
    .config("spark.sql.shuffle.partitions", "200")
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .getOrCreate();
```

## Broadcast Joins
```java
largeDF.join(smallDF.hint("broadcast"), "key");
```

## Partition Pruning
```java
dataset.write().partitionBy("year","month","day").parquet("s3://data/");
```
