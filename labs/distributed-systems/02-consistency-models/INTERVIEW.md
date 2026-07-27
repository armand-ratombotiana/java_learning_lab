# Consistency Models - Interview Preparation

> Key interview questions about consistency models in distributed systems.

---

## Core Interview Questions

### Q1: Compare strong consistency, eventual consistency, and causal consistency
**Answer**: Strong consistency (linearizability): all operations appear atomic, every read sees the latest write. Eventual consistency: if no new writes, all replicas converge. Causal consistency: operations are ordered by causality, concurrent operations may be seen in different orders. Strong is hardest and slowest, eventual is fastest but confusing.

### Q2: What is "read-your-writes" consistency?
**Answer**: A consistency guarantee that once a client writes, subsequent reads from that client see the write. Implemented via session tokens, monotonic reads, or affinity routing. Used in systems like Amazon S3 after their strong consistency update.

### Q3: Explain linearizability vs serializability
**Answer**: Linearizability is about single-object operations appearing atomic in real time. Serializability is about multi-object transactions appearing atomic (total order). Together they form "strict serializability" - the gold standard (Spanner achieves this).

### Q4: When would you use eventual consistency?
**Answer**: Social media feeds, CDN content, DNS records, leaderboard scores, view counts. Use case: when stale data < 1 second is acceptable and availability is critical.

### Q5: How does MongoDB implement causal consistency?
**Answer**: MongoDB uses "sessions" with logical clocks. Each session tracks operation order. Reads within a session are guaranteed to see previous writes from that session. Cross-session operations may be causally inconsistent.

## Company-Specific Focus

| Company | How They Test Consistency |
|---------|--------------------------|
| Google | "Spanner's external consistency via TrueTime" |
| Amazon | "DynamoDB's tunable consistency (eventually vs strongly consistent reads)" |
| Confluent | "Kafka's exactly-once semantics" |
| Apple | "iCloud sync - causal consistency for document edits" |

## LeetCode Connections

| Problem | # | Consistency Concept |
|---------|---|-------------------|
| Time Based KV Store | 981 | Multi-version consistency (MVCC) |
| Merge Intervals | 56 | Merge semantics = eventual consistency |
| Insert Interval | 57 | Causal order insertion |
| Meeting Rooms II | 253 | Resource consistency |

## System Design Connections

- **Design a Global Database**: Choose consistency model per-region
- **Design a Shopping Cart**: Read-your-writes required
- **Design a News Feed**: Eventual consistency OK
- **Design a Payment System**: Linearizability required

## Key Terms to Know

- **Linearizability**: Single-object, real-time ordering
- **Serializability**: Multi-object transaction ordering
- **Strict Serializability**: Both combined (Spanner)
- **Snapshot Isolation**: Read consistent snapshot at a point in time
- **Monotonic Reads**: No read sees older data after seeing newer data
- **Monotonic Writes**: Writes by a process are seen in order
- **Read-your-writes**: Client sees own writes
- **Writes-follow-reads**: Writes respect observed order

> **Key Insight**: Master the consistency spectrum: Strong -> Causal -> Eventual -> Weak. Know which Google/Amazon systems use which level.