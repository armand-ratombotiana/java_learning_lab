# Interview Questions: Apache Iceberg

### Table Format
**Q**: Explain Iceberg's table format layers.
**A**: Catalog → Table Metadata (JSON) → Snapshot List → Manifest List → Manifest Files → Data Files (Parquet/Avro/ORC). Each layer provides discoverability and consistency.

### Partition Evolution
**Q**: How does partition evolution work?
**A**: Iceberg stores partition spec per snapshot. New data uses new spec. Old data remains in old layout. Queries read both seamlessly. No data rewrite needed.

### Hidden Partitioning
**Q**: What is hidden partitioning?
**A**: Users don't specify partition columns in queries. Iceberg automatically derives partitions from filter values. SQL like WHERE event_ts >= '2024-01-01' correctly prunes partitions.

### Compaction
**Q**: How does Iceberg compaction work?
**A**: RewritingDataFiles action merges small files into larger ones, sorts by specified columns, and produces new snapshot. Old files retained until snapshot expiration.
