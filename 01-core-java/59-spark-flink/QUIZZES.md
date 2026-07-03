# Module 59: Apache Spark & Flink - Quizzes

---

## Q1: Spark Architecture
Why is Apache Spark generally significantly faster than legacy Hadoop MapReduce?

A) Spark uses a proprietary programming language.
B) Spark writes intermediate results to physical hard drives (HDFS), which is faster.
C) Spark performs operations completely in-memory (RAM) across the cluster, eliminating the slow disk I/O bottlenecks that plagued MapReduce.
D) Spark does not support distributed processing.

**Answer**: C
**Explanation**: MapReduce forced a write to disk after the "Map" phase and before the "Reduce" phase to ensure fault tolerance. Spark uses Resilient Distributed Datasets (RDDs) which live in RAM and re-calculate lost data using Lineage graphs, keeping the pipeline entirely in-memory.

---

## Q2: Spark `collect()` Method
What is the danger of executing `rdd.collect()` on a large dataset?

A) It deletes the data from the cluster.
B) It triggers an immediate network transfer of the entire partitioned dataset from all worker nodes into the RAM of the single Driver node, almost guaranteeing an `OutOfMemoryError`.
C) It converts the data to JSON format, which is slow.
D) It turns off lazy evaluation permanently.

**Answer**: B
**Explanation**: `collect()` is an action that brings distributed data back to the central coordinating machine. It should only be used after aggressive filtering or sampling (e.g., `rdd.take(10)`).

---

## Q3: Flink Streaming Concepts
In Apache Flink, what is a "Watermark"?

A) A security feature to prevent SQL injection.
B) A marker used to track memory usage in the cluster.
C) A heuristic mechanism used in Event Time processing to indicate that no more events with a timestamp older than the watermark will arrive, allowing the system to safely close and emit the calculation for a time window.
D) A log file generated when a node crashes.

**Answer**: C
**Explanation**: In real-time streaming, network delays cause events to arrive out of order. Watermarks give Flink a threshold. If a window groups events from 12:00 to 12:05, the watermark tells the engine, "We are now at 12:06, any event arriving now stamped 12:04 is considered 'late'."