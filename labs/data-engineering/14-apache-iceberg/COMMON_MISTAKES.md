# Common Mistakes with Apache Iceberg

1. No Compaction: Streaming writes create millions of small files; schedule regular compaction
2. Not Expiring Snapshots: Old snapshots accumulate storage; expire snapshots older than retention
3. Wrong Catalog Choice: Hive catalog has limitations; use REST catalog for multi-engine deployments
4. Large Manifest Files: Frequent writes create many manifests; rewrite manifests when too many
5. Migration Without Validation: Migrating from Parquet/Hive to Iceberg needs thorough validation of counts
