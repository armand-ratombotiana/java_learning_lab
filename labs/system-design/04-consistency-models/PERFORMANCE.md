# Consistency Models - PERFORMANCE

## Performance by Consistency Model

| Model | Read Latency | Write Latency | Throughput |
|-------|-------------|--------------|-----------|
| Eventual | 1-5ms | 1-5ms | Very High |
| Read-Your-Writes | 1-10ms | 1-5ms | High |
| Causal | 5-20ms | 5-15ms | Medium |
| Sequential | 10-30ms | 10-30ms | Medium-Low |
| Linearizable | 15-100ms | 15-100ms | Low |

## Quorum Performance Trade-offs

### Write-Heavy Workload
```
W = 1, R = N  → Fast writes, slow reads
W = 2, R = 2  → Balanced (with N=3)
W = N, R = 1  → Slow writes, fast reads
```

### Latency vs Durability
```java
// Write with different durability guarantees
// W=1: Fast, but data loss on single failure
session.execute(insert.withConsistency(ConsistencyLevel.ONE));

// W=ALL: Slow, but durable across all replicas
session.execute(insert.withConsistency(ConsistencyLevel.ALL));
```

## Synchronous Replication Overhead

### Performance Impact
```
Sync write latency = local_write + network_to_replica + replica_confirm
```

For cross-region sync replication:
```
NY → London: ~70ms RTT → write latency = 5ms + 70ms + 5ms = 80ms (vs 10ms async)
```

### Mitigation
- Use semi-sync (wait for 1 replica, not all)
- Locate replica in same availability zone
- Use batching (group commits)

## Consensus Performance

### Raft Write Latency
```
T_raft_write = T_local + T_majority_network + T_commit
```

With 3 nodes: Majority = 2 nodes (leader + 1 follower network hop).

### Raft Optimization
```yaml
# Batching: Group multiple entries into one append
raft:
  batch-size: 100
  batch-timeout: 10ms

# Pipeline: Send next entry before previous is committed
raft:
  pipelining: true
```

## CRDT Performance

### Operation-Based CRDTs
- Send only the operation (small payload)
- No coordination needed
- Throughput: scales linearly with nodes

### State-Based CRDTs
- Send entire state (large payload)
- Simpler but bandwidth intensive
- Use delta-CRDTs for smaller payloads

### Convergence Overhead
```
CRDT convergence cost:
- Same datacenter: < 1ms
- Cross-region: ~100ms (depends on replication strategy)
- Conflict-free: no reconciliation cost
```
