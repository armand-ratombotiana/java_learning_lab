# Distributed Caching: Performance

## Performance Metrics

| Operation  | Local Cache | Redis | Hazelcast | Database |
|------------|-------------|-------|-----------|----------|
| Read       | <1μs        | <1ms  | <1ms      | 10-50ms  |
| Write      | <1μs        | 1-5ms | 1-5ms     | 10-50ms  |
| Throughput | 1M+ ops/s   | 100K  | 100K      | 1K-10K   |

## Optimization
1. **Near caching**: Local cache + distributed cache combination
2. **Batching**: Pipeline multiple operations
3. **Connection pooling**: Reuse connections
4. **Serialization**: Use efficient formats (Protocol Buffers, Kryo)

## Memory Planning
- Estimate working set size
- Plan for 150% of working set
- Monitor eviction rates
