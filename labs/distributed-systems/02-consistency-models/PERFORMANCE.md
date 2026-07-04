# Consistency Models: Performance

## Performance Characteristics

| Model            | Read Latency | Write Latency | Throughput | Scalability |
|------------------|--------------|---------------|------------|-------------|
| Linearizable     | 50-100ms     | 100-200ms     | Low        | Low         |
| Sequential       | 30-80ms      | 80-150ms      | Medium     | Medium      |
| Causal           | 20-50ms      | 50-100ms      | Medium-High| High        |
| Eventual         | 1-10ms       | 1-10ms        | Very High  | Very High   |

## Optimization Techniques
1. **Read-your-writes**: Client-side write-through cache
2. **Monotonic reads**: Version tracking with local cache
3. **Causal consistency**: Reduce metadata overhead with interval-based clocks
4. **Strong consistency**: Use consensus groups for smaller fault domains
