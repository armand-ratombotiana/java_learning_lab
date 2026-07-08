# Code Deep Dive: Apache Iceberg

See Java source files in src/main/java/com/dataeng/fourteen/ for:
- IcebergTableManager.java: Catalog operations, table CRUD, partition management
- IcebergMaintenance.java: Compaction, snapshot expiration, orphan cleanup

Key patterns:
```java
// Create Iceberg table
spark.sql("CREATE TABLE events (id BIGINT, ts TIMESTAMP, data STRING)" +
    "USING iceberg " +
    "PARTITIONED BY (days(ts))");

// Partition evolution
spark.sql("ALTER TABLE events SET PARTITION SPEC (months(ts))");

// Time travel
spark.read().option("snapshot-id", 12345L).table("events");

// Incremental read
spark.read().option("start-snapshot-id", 12345L)
    .option("end-snapshot-id", 12346L).table("events");

// Compaction
spark.sql("CALL system.rewrite_data_files(table => 'events')");

// Expire snapshots
spark.sql("CALL system.expire_snapshots(table => 'events', older_than => '2024-01-01')");
```
