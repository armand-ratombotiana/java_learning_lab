# Performance Optimization for Apache Iceberg

## File Sizing
Target 256MB-1GB data files. Use spark.sql.shuffle.partitions to control output size. Compact regularly with target-file-size-bytes.

## Manifest Management
Rewrite manifests when count exceeds threshold. Periodic metadata compaction. Balance between metadata size and query planning time.

## Partition Pruning
Filter on partition columns for maximum pruning. Use hidden partitioning for time-based queries. Monitor pruning via explain() output.

## Catalog Optimization
Use REST catalog for multi-engine access. Enable catalog caching. Configure table statistics collection.
