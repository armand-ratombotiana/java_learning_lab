# Apache Iceberg

## Overview
Apache Iceberg is an open table format for huge analytic datasets. It adds table-level semantics to data lakes with features like partitioning evolution, hidden partitioning, time travel, and schema evolution. Iceberg is designed for performance and reliability at petabyte scale.

## Key Concepts
- **Table Format**: Open specification for managing large analytic tables in data lakes
- **Partition Evolution**: Change partition layout without rewriting entire table
- **Hidden Partitioning**: Automatic partition management; users query without partition columns
- **Time Travel**: Snapshot isolation for consistent reads and historical queries
- **Compaction**: File merging and sorting for optimal read performance
- **Catalog Integration**: Hive, Nessie, JDBC, REST, Glue, DynamoDB catalog support

## Learning Objectives
1. Understand Iceberg's table format and file layout on storage
2. Use partition evolution to adapt partitioning over time
3. Query Iceberg tables with hidden partitioning for simplified queries
4. Implement time travel and snapshot isolation for consistent reads
5. Manage table maintenance (compaction, expiration, orphan cleanup)
6. Integrate Iceberg with Spark, Flink, Trino, and other engines
