# CAP Theorem: Performance Considerations

## Tradeoff Impact

### CP Systems
- **Higher latency**: Must wait for quorum responses
- **Lower throughput**: Synchronous coordination overhead
- **Better during partitions**: Data remains consistent
- **Worse availability**: May reject requests

### AP Systems
- **Lower latency**: Respond immediately from local node
- **Higher throughput**: No coordination required
- **Worse during partitions**: Data divergence risk
- **Better availability**: Always respond

## Benchmark Metrics
| Metric        | CP System | AP System |
|---------------|-----------|-----------|
| Read Latency  | ~50ms     | ~5ms      |
| Write Latency | ~100ms    | ~10ms     |
| Write Success | 99.9%     | 99.99%    |
| Consistency   | Strong    | Eventual  |

## Optimization Strategies
- Use read-repairs in AP systems to reduce inconsistency window
- Implement quorum flexibility (R + W > N)
- Leverage gossip protocols for background reconciliation
