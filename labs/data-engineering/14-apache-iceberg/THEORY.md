# Apache Iceberg Theory

## Table Format Hierarchy
Catalog → Table Metadata (JSON) → Snapshot List → Manifest List → Manifest Files → Data Files. Each layer is immutable or append-only. Metadata files track schema, partition spec, sort order, and properties. This layered approach enables atomic updates and snapshot isolation.

## Partition Evolution
Traditional formats lock you into initial partition choice. Iceberg stores partition spec per snapshot. When spec changes, new data uses new layout while old data remains. Queries transparently read both layouts. Example: partition by hour initially, then by day — no rewrite needed.

## Hidden Partitioning
Users write SQL with value-based filters, not partition expressions. Iceberg automatically converts WHERE event_date >= '2024-01-01' AND event_date < '2024-02-01' to partition pruning. No need to know partition column exists. Reduces user errors and query complexity.

## Manifest Files & Metadata
Manifest files (Avro) track which data files belong to which partition. Manifest lists aggregate manifests per snapshot. Metadata files (JSON) store table state, schema history, partition specs. Periodic metadata compaction prevents small-file explosion in metadata layer.
