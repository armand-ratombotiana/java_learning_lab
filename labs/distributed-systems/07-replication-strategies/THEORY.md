# Replication Strategies: Theory

## Replication Topologies

### Leader/Follower (Single-Leader)
- One node accepts writes
- Followers replicate from leader
- Leader handles all writes, any node may serve reads

### Multi-Leader
- Multiple nodes accept writes
- Each leader replicates to other leaders
- Conflict resolution required

### Leaderless
- Any node accepts writes
- Read repair and anti-entropy for consistency
- Examples: Cassandra, Dynamo

## Synchronous vs Asynchronous

### Synchronous
- Leader waits for follower acknowledgment
- Stronger consistency, higher latency
- Blocking if follower fails

### Asynchronous
- Leader responds immediately
- Weaker consistency, lower latency
- Data loss risk on leader failure

## Quorum
- Read quorum (R) + Write quorum (W) > N (total replicas)
- R = W = N/2+1 for strong consistency
- Lower R/W for better performance
