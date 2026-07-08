# Apache Flink

## Overview
Apache Flink is a distributed stream processing framework for stateful computations over unbounded and bounded data streams. This lab covers stream processing fundamentals, Flink SQL, event time processing, watermarks, checkpointing, and savepoints.

## Key Concepts
- **Stream Processing**: Continuous processing of unbounded data streams with sub-second latency
- **Event Time vs Processing Time**: Handling out-of-order events using embedded event timestamps
- **Watermarks**: Progress metric for event-time processing, tracking completeness
- **Checkpointing**: Distributed snapshots for exactly-once fault tolerance
- **Savepoints**: User-initiated snapshots for job upgrades and maintenance
- **Flink SQL**: Relational abstraction for stream processing with ANSI SQL

## Learning Objectives
1. Understand Flink's streaming-first architecture and runtime model
2. Implement event-time processing with watermarks and allowed lateness
3. Use tumbling, sliding, and session windows for time-based aggregations
4. Configure checkpointing and savepoints for fault tolerance
5. Write Flink SQL queries for streaming data
6. Manage state with different state backends (Heap, RocksDB)
