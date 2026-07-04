# Consistency Models - INTERVIEW

## Common Interview Questions

### Q1: Explain the CAP theorem.
**Answer**: In a distributed system, you can only guarantee 2 of 3: Consistency (all reads see latest write), Availability (every request gets a response), Partition Tolerance (system works despite network failures). Since partitions are inevitable, the real choice is between CP and AP.

### Q2: What's the difference between strong and eventual consistency?
**Answer**: Strong consistency ensures all reads see the latest write immediately. Eventual consistency guarantees that if no new writes are made, all replicas will converge to the same value. Strong costs latency, eventual risks stale reads.

### Q3: How does Raft ensure consistency?
**Answer**: Raft uses a single leader, log replication with majority commit, and leader election with randomized timeouts. Safety is guaranteed: at most one leader per term, log entries are only committed if replicated to a majority, and state machines apply entries in the same order.

### Q4: When would you use causal consistency?
**Answer**: Social feeds, comment threads, collaborative editing. Events have cause-effect relationships (reply depends on post), but total ordering of all events is unnecessary. Causal consistency preserves causal relationships while allowing concurrent independent events to be reordered.

### Q5: What are CRDTs and when would you use them?
**Answer**: Conflict-Free Replicated Data Types are data structures that can be updated concurrently on different replicas and automatically merge without conflicts. Use for: collaborative editing (Google Docs), shopping carts, user presence indicators.

### Q6: Explain PACELC.
**Answer**: Extension of CAP: During a Partition (P), choose between Availability and Consistency (A/C). Else (E), in the absence of partitions, choose between Latency (L) and Consistency (C). Most systems optimize for low latency during normal operation.

## System Design Problem: Design a Distributed Shopping Cart

### Requirements
- Global scale, millions of concurrent users
- Items must not be lost
- Users must see their own adds immediately
- Other users can see eventually

### Proposed Solution
- **CRDT-based cart**: Merge concurrent adds without conflicts
- **Session tokens**: Ensure read-your-writes consistency
- **Cassandra with LOCAL_QUORUM**: Fast writes with local consistency
- **Cache cluster**: Redis with session affinity for low-latency reads
