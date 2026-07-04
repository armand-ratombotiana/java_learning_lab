# Code Deep Dive: Delta Lake

## Operations
```java
public class DeltaLakeOps {
    // Upsert
    spark.sql("MERGE INTO delta.`/path` t USING updates s " +
        "ON t.id = s.id " +
        "WHEN MATCHED THEN UPDATE SET * " +
        "WHEN NOT MATCHED THEN INSERT *");

    // Time Travel
    spark.read().format("delta").option("versionAsOf", 25).load("/path");

    // Optimize
    spark.sql("OPTIMIZE delta.`/path` ZORDER BY (customer_id)");

    // Vacuum
    spark.sql("VACUUM delta.`/path` RETAIN 168 HOURS");
}
```
