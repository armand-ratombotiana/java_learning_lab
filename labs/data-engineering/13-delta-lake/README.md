# Delta Lake

## Overview
Delta Lake is an open-source storage layer that brings ACID transactions to Apache Spark and big data workloads. It provides time travel, schema enforcement, and unified batch/streaming on data lakes.

## Key Concepts
- **ACID Transactions**: Atomicity, Consistency, Isolation, Durability on data lake storage
- **Transaction Log**: JSON-based commit log in _delta_log/ directory tracking all changes
- **Time Travel**: Query and restore historical versions of data using version or timestamp
- **Schema Enforcement**: Prevents corrupt data writes by validating against table schema
- **Merge/Upsert**: Efficient INSERT, UPDATE, DELETE operations via MERGE INTO
- **Optimize & Vacuum**: File compaction with Z-ordering and cleanup of unreferenced files

## Learning Objectives
1. Understand Delta Lake's transaction log protocol and ACID guarantees
2. Implement time travel queries for data auditing and recovery
3. Use MERGE INTO for upsert operations with conflict resolution
4. Configure schema enforcement and evolution for production pipelines
5. Run OPTIMIZE with Z-ordering for query performance
6. Manage storage lifecycle with VACUUM and retention policies
