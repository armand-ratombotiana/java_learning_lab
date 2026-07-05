# Refactoring: Event Streaming

## Current Pain Points
- Single-threaded log compaction blocks producer writes
- Consumer group coordination is synchronous (blocking during rebalance)
- No tiered storage (all data on fast but expensive local SSD)
- Offsets committed inline with consume (not transactional)

## Suggested Improvements
- Background compaction thread with incremental cleanup
- Implement Cooperative Sticky Partition Assignment (incremental rebalance)
- Add tiered storage: hot (SSD), warm (HDD), cold (S3) with configurable thresholds
- Implement transactional producer for exactly-once semantics
- Add KRaft mode (remove ZooKeeper dependency for metadata management)
- Add leader rebalance for distribution of partition leadership
