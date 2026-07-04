# Consistency Models: Internals

## Implementation Mechanisms

### Strong Consistency
- **Single leader**: All writes go through a single node
- **Quorum reads/writes**: R + W > N ensures overlap
- **Distributed transactions**: 2PC or Paxos-based commit
- **Synchronization**: Distributed locks or leases

### Causal Consistency
- **Vector clocks**: Track causal dependencies
- **Version vectors**: Track replica versions
- **Dependency tracking**: Explicit causal metadata

### Eventual Consistency
- **Gossip protocols**: Background replication
- **Read repair**: Detect and fix inconsistencies on read
- **Anti-entropy**: Periodic full synchronization
- **CRDTs**: Conflict-free data types

## Metadata Overhead
- Linearizability: No per-operation metadata
- Causal consistency: O(N) metadata (vector clock size)
- Eventual consistency: O(1) metadata per replica
