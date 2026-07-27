# Time & Ordering - Interview Preparation

> Key interview questions about time, ordering, and clocks in distributed systems.

---

## Core Interview Questions

### Q1: Explain Lamport Clocks vs Vector Clocks
**Answer**: Lamport clocks: each node has counter; increment on event, send with message; receiver updates to max(local, received) + 1. Captures happens-before (causality). Vector clocks: each node has array of counters (one per node). Can detect conflicting concurrent updates. Used in DynamoDB for conflict detection.

### Q2: What is the difference between physical and logical clocks?
**Answer**: Physical clocks (wall clock, TrueTime): real time, subject to drift, require NTP. Logical clocks (Lamport, Vector): count events, no drift, provide ordering but not real time. TrueTime combines both: GPS + atomic clocks with bounded uncertainty.

### Q3: How does Google TrueTime work?
**Answer**: Uses GPS receivers + atomic clocks in each datacenter. Exposes uncertainty interval [earliest, latest] around the true time. Spanner uses TT.now() to get interval, waits out the uncertainty before committing. Provides external consistency (linearizability with real time).

### Q4: What is the difference between total and partial order?
**Answer**: Total order (consensus): every operation has a defined sequence (Kafka partitions). Partial order (vector clocks): some operations are ordered, some are concurrent. Distributed systems generally provide partial order unless consensus is involved.

### Q5: How does hybrid logical clock (HLC) work?
**Answer**: Combines physical + logical clocks. HLC = max(physical clock, last physical, last logical). Provides near-real-time wall clock values while preserving causality. Used in CockroachDB. Upper bound approximation of physical time.

## Company-Specific Focus

| Company | Time/Ordering Focus |
|---------|--------------------|
| Google | "TrueTime in Spanner - how it achieves external consistency" |
| Amazon | "Vector clocks in DynamoDB for conflict detection" |
| CockroachDB | "Hybrid Logical Clocks" |
| Confluent | "Kafka partition ordering vs total ordering" |

## LeetCode Connections

| Problem | # | Time/Ordering Concept |
|---------|---|---------------------|
| Merge Intervals | 56 | Clock merging |
| Meeting Rooms II | 253 | Resource scheduling |
| Insert Interval | 57 | Clock insertion ordering |
| Time Based KV Store | 981 | Versioned time queries |
| Car Pooling | 1094 | Resource timeline |

## System Design Connections

- **Design a Database with MVCC**: Timestamp-based versioning
- **Design a Distributed Log**: Total order within partitions
- **Design a Sequencer**: Lamport clock for ordering
- **Design a Conflict Detection System**: Vector clocks

> **Key Insight**: Know when to use each clock type: Lamport for causal ordering, Vector for conflict detection, TrueTime/HLC for external consistency.