# Apache Iceberg Internals

## Metadata Layer
v1.metadata.json: current table state, schema, partition spec, snapshot list. Snapshots reference manifest lists. Manifest lists reference manifest files. Manifest files reference data files with column statistics (min/max, null counts, row counts).

## Snapshot Isolation
Each write creates new snapshot (immutable view of table). Reads use snapshot at start of transaction (serializable isolation). Commit checks for conflicts (concurrent writes to same partition). On conflict, one writer commits, others retry with new snapshot.

## File Formats
Data: Parquet (default), Avro, ORC. Metadata: Avro (manifests), JSON (table metadata). Column statistics in manifests enable predicate pushdown and partition pruning without scanning data files.

## Maintenance Operations
Compaction (RewriteDataFiles): merge small files, optionally sort by columns. Expire Snapshots: remove old snapshots and unreferenced files. Remove Orphan Files: clean files not tracked in metadata. Rewrite Manifests: merge small manifest files.
