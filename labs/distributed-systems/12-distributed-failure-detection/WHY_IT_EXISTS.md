# Why Failure Detection Exists

## Problem
- In distributed systems, nodes fail silently
- Other nodes must detect failures to maintain correct operation
- Without detection: partitions persist, resources leak, operations stall

## Purpose
1. **Automated recovery**: Trigger leader election, repair replication
2. **Membership management**: Maintain accurate cluster membership
3. **Load balancing**: Redirect traffic from failed nodes
4. **Resource management**: Release resources held by failed nodes

## Use Cases
- Raft leader election (timeout-based detection)
- Cassandra/Hazelcast cluster management (gossip)
- Cloud load balancer health checks
- Service mesh circuit breakers
