# Code Deep Dive: Delta Lake

See Java source files in src/main/java/com/dataeng/thirteen/ for:
- DeltaLakeManager.java: Table CRUD, upsert, time travel, optimize, vacuum
- DeltaOptimizer.java: File compaction, Z-ordering, partitioning strategies

Key patterns:
```java
// Upsert via MERGE INTO
spark.sql("MERGE INTO delta.`/path` t USING updates s " +
    "ON t.id = s.id " +
    "WHEN MATCHED THEN UPDATE SET * " +
    "WHEN NOT MATCHED THEN INSERT *");

// Time travel
spark.read().format("delta").option("versionAsOf", 25).load("/path");

// Optimize with Z-order
spark.sql("OPTIMIZE delta.`/path` ZORDER BY (customer_id)");

// Vacuum
spark.sql("VACUUM delta.`/path` RETAIN 168 HOURS");
```
