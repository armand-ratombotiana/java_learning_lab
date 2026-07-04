# Why Replication Matters

## Business Impact
- **Uptime**: N-1 node failures tolerated without downtime
- **Performance**: Read throughput scales linearly with replicas
- **Data Protection**: Survive disk, server, rack, and even datacenter failures
- **Global Reach**: Serve users from nearest datacenter

## Technical Impact
- Read capacity scales with number of replicas
- Write capacity limited (leader bottleneck)
- Replication lag causes stale reads
- Consistency vs performance tradeoffs

## Key Insight
Replication is the foundation of distributed storage. Without it, redundancy and fault tolerance are impossible.
