# Debugging: Distributed Cache

## Common Issues

### Cache miss rate suddenly increases
- Node may have restarted (lost in-memory data)
- Eviction threshold may be too aggressive (check maxmemory config)
- TTL values may be too short
- New node added -> key ranges shifted -> cold cache

### High latency on SET/GET
- Check if eviction is triggering frequently (maxmemory too low)
- Replication may be blocking writes (make async)
- GC pauses may be affecting response times
- Network congestion between cluster nodes

### Node marked as dead but still alive
- Gossip suspicion timeout too low
- Network latency between nodes > suspicion timeout
- Firewall blocking gossip UDP packets
- Clock skew between nodes

### Inconsistent reads
- Replication lag: write to primary, read from stale replica
- Check if W+R > N for quorum reads
- Monitor replica ACK times
