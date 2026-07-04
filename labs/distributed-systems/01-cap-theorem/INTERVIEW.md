# CAP Theorem: Interview Questions

## Senior Engineer Level

### Q1: Explain CAP theorem with a real-world analogy.
**A**: Think of a shared Google Doc. If two people edit offline and reconnect, you have a partition. Google Docs chooses AP (both edits coexist, merge later) rather than CP (block one editor).

### Q2: How does CAP apply to MongoDB?
**A**: MongoDB is CP. During primary failover, it elects a new primary and rejects writes. Reads from secondaries may return stale data, but a read from the primary after write acknowledgment is consistent.

### Q3: Design a system that requires both CP and AP.
**A**: Use CQRS pattern. Command side (writes) uses CP database. Query side (reads) uses AP cache. Accept eventual consistency between them.

### Q4: How do you detect network partitions?
**A**: Heartbeat mechanisms, gossip protocols, phi-accrual failure detectors, and majority-based quorum checks.

### Q5: What is the practical relevance of CAP?
**A**: Forces architects to make explicit tradeoffs. Guides database selection, replication strategy, and failure handling design.
