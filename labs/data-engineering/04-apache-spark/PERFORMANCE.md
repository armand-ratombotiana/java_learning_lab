# Performance

## AQE
```java
SparkSession.builder()
    .config("spark.sql.adaptive.enabled", "true")
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
    .config("spark.sql.adaptive.skewJoin.enabled", "true")
    .getOrCreate();
```

## Kryo Serialization
```java
spark.conf().set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
```

## Broadcast Joins
```java
largeDF.join(broadcast(smallDF), "key");
```
