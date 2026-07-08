# Why Apache Iceberg Matters

- **Performance**: O(1) planning avoids listing files on object store
- **Partition Evolution**: Change partitioning without rewriting data
- **Hidden Partitioning**: Users don't need to know partition columns
- **Engine Agnostic**: Works with Spark, Flink, Trino, Hive, Presto, Dremio
- **ACID**: Serializable snapshot isolation for consistent reads
- **Time Travel**: Query and rollback to any snapshot
- **Open Format**: Apache-licensed, no vendor lock-in
