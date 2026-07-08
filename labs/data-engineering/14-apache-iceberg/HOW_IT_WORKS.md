# How Apache Iceberg Works

1. Engines interact with catalog to discover table metadata
2. Table metadata points to current snapshot (immutable view)
3. Snapshot references manifest list which references manifest files
4. Manifest files track data files with column statistics
5. Queries use statistics for partition pruning and predicate pushdown
6. Writes create new snapshot with updated manifest references
7. Partition evolution allows changing partition spec without data rewrite
8. Maintenance operations (compaction, snapshot expiration) create new metadata versions
9. All operations are atomic via catalog's commit protocol
