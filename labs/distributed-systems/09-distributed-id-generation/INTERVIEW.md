# Distributed ID Generation - Interview Preparation

> Key interview questions about globally unique ID generation.

---

## Core Interview Questions

### Q1: Explain Twitter Snowflake ID format
**Answer**: 64-bit ID: 1 bit (sign, always 0) + 41 bits (timestamp in ms, ~69 years) + 5 bits (datacenter) + 5 bits (machine) + 12 bits (sequence, 4096 per ms). Total: 2^22 unique IDs per ms per datacenter/machine pair.

### Q2: Compare UUID v4 vs Snowflake vs database sequence
**Answer**: UUID v4: 128-bit random, no ordering, good performance, 16 bytes storage. Snowflake: 64-bit, time-ordered, compact, requires clock sync. DB Sequence: ordered, centralized, single point of failure, not suitable for distributed.

### Q3: How do you handle clock skew in Snowflake?
**Answer**: Monitor clock drift; reject requests if clock moved backwards; wait until clock catches up; use NTP with careful configuration; allow configurable epoch to reset after large jumps. Some implementations use ZooKeeper for sequence allocation.

### Q4: Generate unique IDs without a central coordinator
**Answer**: Snowflake (time + worker ID), UUID (random), UIDGenerator (pre-allocated ranges), CRDT-based counters (operation-based IDs with node ID embedded).

### Q5: What are "ZooKeeper sequential nodes" for ID generation?
**Answer**: Create ephemeral sequential zNode under a path. ZooKeeper assigns monotonically increasing sequence number. Each client gets unique ID. Limited by ZooKeeper throughput (thousands/sec vs Snowflake's millions/sec).

## Company-Specific Focus

| Company | ID Generation Focus |
|---------|--------------------|
| Twitter | Snowflake - de facto distributed ID standard |
| Meta | "How does Facebook generate unique IDs?" |
| Instagram | "ID generation for photo uploads" |
| Google | "Spanner's TrueTime-based commit timestamps" |

## LeetCode Connections

| Problem | # | ID Concept |
|---------|---|-----------|
| Encode and Decode TinyURL | 535 | ID compression |
| Fraction to Recurring Decimal | 166 | Unique ID formatting |
| Integer to Roman | 12 | ID representation |

## System Design Connections

- **Design a URL Shortener**: Base-62 encoded monotonic ID
- **Design a Photo Sharing App**: Snowflake for photo IDs
- **Design a Messaging System**: Snowflake for message IDs
- **Design a Database**: Unique primary keys across partitions

> **Key Insight**: Snowflake is the industry standard for distributed ID generation. Be prepared to implement it in an interview.