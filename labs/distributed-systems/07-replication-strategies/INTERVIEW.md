# Replication Strategies - Interview Preparation

> Key interview questions about data replication in distributed systems.

---

## Core Interview Questions

### Q1: Compare leader-follower, multi-leader, and leaderless replication
**Answer**: Leader-follower: one leader accepts writes, followers replicate passively. Strongly consistent reads possible, but leader bottleneck. Multi-leader: multiple leaders accept writes, conflict resolution needed. Good for multi-region. Leaderless (Dynamo-style): any node accepts writes, quorum-based, no leader bottleneck.

### Q2: How does synchronous vs asynchronous replication differ?
**Answer**: Synchronous: leader waits for all followers to acknowledge; strong consistency, higher latency (risk of unavailability if follower fails). Asynchronous: leader responds immediately; lower latency, weaker consistency, data loss risk on leader failure.

### Q3: Explain read-after-write consistency in replication
**Answer**: Ensures client sees its own writes. Solutions: read from leader (always most recent), use timestamps/version numbers, session readers pin to replica that has seen the write, wait for replication acknowledgment.

### Q4: What is a replication lag and how do you handle it?
**Answer**: Lag = time between write on leader and appearing on follower. Monitoring via seconds_behind_master. Mitigation: remove dependency on replica reads for critical paths, use replica for read-only queries, set replication priority.

### Q5: How does multi-leader conflict resolution work?
**Answer**: Conflict avoidance (route user writes to same leader), last-writer-wins (LWW using timestamps), CRDTs (merge operations), custom conflict handlers (application logic), version vectors with sibling resolution.

## Company-Specific Focus

| Company | Replication Focus |
|---------|------------------|
| Amazon | "DynamoDB's NWR quorum replication" |
| Google | "Spanner's Paxos-based synchronous replication" |
| Meta | "TAO's read-through/write-through caching" |
| Netflix | "Multi-region Cassandra replication" |

## LeetCode Connections

| Problem | # | Replication Concept |
|---------|---|--------------------|
| Clone Graph | 133 | Graph replication |
| Copy List with Random Pointer | 138 | State copy |
| Same Tree | 100 | Replica verification |
| Merge Intervals | 56 | Multi-leader merge |

## System Design Connections

- **Design a Global Database**: Multi-leader for cross-region, leader-follower per region
- **Design a Chat System**: Multi-leader for active-active regions
- **Design a User Profile Store**: Leader-follower with read replicas
- **Design an Analytics Pipeline**: Leaderless for write-heavy workloads

> **Key Insight**: Always discuss replication lag, conflict resolution, and consistency guarantees. Know which replication model each major system uses.