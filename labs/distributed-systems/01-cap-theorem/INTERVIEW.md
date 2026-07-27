# CAP Theorem - Interview Preparation

> Key interview questions about the CAP theorem for distributed systems roles.

---

## Core Interview Questions

### Q1: Explain the CAP theorem in detail
**Answer**: CAP theorem states that a distributed system can only provide two of three guarantees simultaneously: Consistency (every read gets the latest write), Availability (every request gets a non-error response), and Partition Tolerance (system continues operating despite network failures). In practice, partition tolerance is mandatory in distributed systems, so the choice is between CP and AP.

### Q2: Give real-world examples of CP vs AP systems
**Answer**: CP Systems: Google Spanner (strong consistency via TrueTime), ZooKeeper (consensus before responding), HBase (consistent reads). AP Systems: Amazon DynamoDB (eventual consistency), Cassandra (tunable consistency), DNS (eventually consistent). CDN systems are typically AP for performance.

### Q3: How does Google Spanner achieve CP while being globally distributed?
**Answer**: Spanner uses TrueTime API (GPS + atomic clocks) to provide external consistency. It runs Paxos for synchronous replication within zones and 2PC across zones. Writes commit only after TrueTime ensures linearizability.

### Q4: Is the CAP theorem still relevant with modern systems?
**Answer**: Yes, but the PACELC extension adds nuance: during Partition (P), tradeoff between Availability (A) and Consistency (C); Else, tradeoff between Latency (L) and Consistency (C). Modern systems like Cassandra offer tunable consistency.

### Q5: Design a system that chooses AP over CP. Why?
**Answer**: A social media news feed is AP: users can still see cached posts during network partitions, even if they're stale. Consistency can be sacrificed for availability, and eventual consistency converges quickly.

## Company-Specific Focus

| Company | How They Test CAP |
|---------|------------------|
| Google | "How does Spanner achieve strong consistency globally?" |
| Amazon | "Design DynamoDB - why did you choose AP?" |
| Meta | "How does TAO handle consistency vs availability?" |
| Netflix | "How does Chaos Monkey test your CAP choices?" |

## LeetCode Connections

| Problem | # | CAP Concept |
|---------|---|------------|
| Merge Intervals | 56 | Consistency merge after partition |
| Network Delay Time | 743 | Partition detection latency |
| Redundant Connection | 684 | Partition heal detection |

## System Design Connections

- **Design a Distributed Database**: Choose CP (Spanner-like) vs AP (Dynamo-like)
- **Design a Social Feed**: AP - stale data acceptable, availability critical
- **Design a Payment System**: CP - strong consistency required
- **Design a CDN**: AP - stale cached data acceptable for performance

## Sample Answer Framework

When asked about CAP in system design:
1. Identify the consistency requirement (strong, causal, eventual)
2. Determine availability SLA (99.9%, 99.99%, 99.999%)
3. Assume partitions will happen (always choose P)
4. Make explicit tradeoff: "This system will prioritize consistency over availability because..."
5. Justify with business requirements

> **Key Insight**: Always start with "P is mandatory in distributed systems." Then discuss C vs A based on use case.