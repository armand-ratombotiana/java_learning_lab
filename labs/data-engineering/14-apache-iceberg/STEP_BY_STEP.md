# Step-by-Step: Working with Apache Iceberg

1. Add Iceberg to Spark: --packages org.apache.iceberg:iceberg-spark-runtime-3.4_2.12:x.y.z
2. Configure Spark with Iceberg catalog (Hive, REST, or in-memory)
3. Create table: spark.sql("CREATE TABLE ... USING iceberg PARTITIONED BY (...)")
4. Write data: df.writeTo("events").append()
5. Inspect metadata: SHOW TBLPROPERTIES events; DESCRIBE HISTORY events
6. Time travel: Read with snapshot-id or timestamp option
7. Evolve partition: ALTER TABLE SET PARTITION SPEC
8. Compact: CALL system.rewrite_data_files(table => 'events')
9. Expire snapshots: CALL system.expire_snapshots(table => 'events', older_than => ...)
