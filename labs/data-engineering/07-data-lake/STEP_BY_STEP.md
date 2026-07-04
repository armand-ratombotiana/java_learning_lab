# Step-by-Step

1. **Choose Storage**: S3, ADLS, GCS, HDFS
2. **Configure Delta**: DeltaSparkSessionExtension
3. **Bronze**: Write raw data with partitionBy(ingestion_date)
4. **Silver**: Clean, deduplicate, validate
5. **Gold**: Aggregate, denormalize for BI
6. **Optimize**: Compaction, Z-order, vacuum
