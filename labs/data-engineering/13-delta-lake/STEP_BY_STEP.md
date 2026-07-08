# Step-by-Step: Working with Delta Lake

1. Add Delta Lake package to Spark: --packages io.delta:delta-core_2.12:x.y.z
2. Configure SparkSession with Delta extensions and catalog
3. Write DataFrame: df.write().format("delta").save("/path")
4. Explore _delta_log/ with ls and cat on JSON files
5. Time travel: spark.read().format("delta").option("versionAsOf", N).load("/path")
6. Upsert: MERGE INTO delta.`/path` t USING ... ON key WHEN MATCHED/WHEN NOT MATCHED
7. Optimize: OPTIMIZE delta.`/path` ZORDER BY (columns)
8. Vacuum: VACUUM delta.`/path` RETAIN 168 HOURS
9. Enable Change Data Feed: ALTER TABLE SET TBLPROPERTIES (delta.enableChangeDataFeed=true)
