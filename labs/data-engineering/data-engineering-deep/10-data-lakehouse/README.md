# Lab 10: Data Lakehouse

## Overview

Implement core lakehouse concepts — Delta Lake change data capture, Apache Iceberg table formats, Apache Hudi merge-on-read, and time travel queries.

## Learning Objectives

- Understand the lakehouse architecture (object store + ACID transactions)
- Implement Delta Lake-style merge, update, and delete operations
- Explore Iceberg's table format and partition evolution
- Implement Hudi's copy-on-write vs merge-on-read strategies
- Perform time-travel queries to access historical data snapshots

## Key Concepts

- **Delta Lake**: ACID transactions on data lake via transaction log
- **Apache Iceberg**: Open table format with partition evolution and hidden partitioning
- **Apache Hudi**: Incremental processing, upserts, and record-level indexing
- **Time Travel**: Query data as of a specific version or timestamp
- **Table Formats**: Delta/Iceberg/Hudi comparison
- **Z-Order / Clustering**: Data layout optimization
