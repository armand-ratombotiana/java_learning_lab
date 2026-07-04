# Distributed Consensus: Performance

## Performance Characteristics

| Algorithm | Latency | Throughput | Network Messages |
|-----------|---------|------------|------------------|
| Classical Paxos | 2 RTT | Low | 3N-3 per round |
| Multi-Paxos | 1 RTT | Medium | 2N-2 per entry |
| Raft | 1 RTT | Medium-High | 2N-2 per entry |
| Zab | 1 RTT | Medium-High | 2N per entry |
| EPaxos | 1 RTT | High | N per entry |

## Optimization Techniques
1. **Batching**: Group multiple entries into one RPC
2. **Pipelining**: Send new entries without waiting for previous acks
3. **Parallel consensus groups**: Shard operations across groups
4. **Read-only optimization**: Leaders can serve reads without consensus

## Bottlenecks
- Leader CPU (serialization point)
- Network bandwidth (log replication)
- Disk I/O (persistent log)
