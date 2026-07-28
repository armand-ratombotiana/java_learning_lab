# Lab 05: Batch Optimization with Apache Spark

## Overview

Optimize Apache Spark batch jobs for performance — shuffle tuning, memory management, data partitioning, and executor configuration.

## Learning Objectives

- Understand Spark's memory model (execution, storage, reserved)
- Optimize shuffle operations (broadcast vs sort-merge joins)
- Tune parallelism and partitioning for skewed data
- Diagnose and fix common performance issues

## Key Concepts

- **Shuffle**: Sort, spill, merge phases; tuning `spark.sql.shuffle.partitions`
- **Join Strategies**: Broadcast hash join, sort-merge join, skewed join
- **Memory Tuning**: Spark memory fractions, off-heap, Kryo serialization
- **AQE**: Adaptive Query Execution (coalescing, skew join optimization)
- **File Formats**: Parquet vs ORC vs Avro for batch processing
