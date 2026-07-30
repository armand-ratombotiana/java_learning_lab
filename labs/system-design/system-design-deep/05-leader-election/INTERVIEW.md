# Interview Deep-Dive: Leader Election

## Common Questions

### Q1: Why is Raft's leader election safer than the Bully algorithm?
**Answer**: Raft uses majority consensus — a leader cannot exist without majority support. Combined with randomized election timeouts and term-based log replication, Raft guarantees safety under network partitions. Bully assumes synchronous communication and can produce split-brain scenarios (two leaders) under partition.

### Q2: How do fencing tokens prevent split-brain in lease-based systems?
**Answer**: Fencing tokens are monotonically increasing sequence numbers issued by a coordination service (ZooKeeper/Etcd). Even if an old leader's lease expires but its process hasn't stopped, all requests include a token. The storage layer rejects requests with tokens older than the current leader's token. This ensures only the valid leader can mutate state.

### Q3: Describe ZooKeeper's sequential ephemeral znode election mechanism.
**Answer**: Each candidate creates an ephemeral-sequential znode under `/election`. The node with the smallest sequence number is leader. Each non-leader watches the znode with the next lower sequence number. When a leader fails (its znode disappears), the next node detects the change and becomes leader. This creates a chain of watchers.

## System Design Whiteboard

**Design a fault-tolerant leader election for a distributed task scheduler.**
- 5 nodes in the cluster
- Coordination via Etcd (3-node Etcd cluster)
- Lease-based leadership with 10-second lease, 3-second renewal
- Fencing token per lease epoch
- On leader failure: remaining nodes detect via Etcd lease expiry
- New leader election via Etcd concurrency API
- Graceful leader handoff: current leader drains tasks before stepping down

## Key Trade-offs to Discuss
- Availability vs split-brain risk
- Lease duration vs failure detection speed
- External coordination (ZooKeeper/Etcd) vs internal consensus (Raft)
