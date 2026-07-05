# Performance: Distributed Cache

## Throughput Targets
- Single node: 100k+ ops/s (SET/GET 1KB values)
- Cluster 5 nodes: 500k+ ops/s
- Replication latency: < 1ms P99 (same datacenter)
- P99 latency: < 500us (client -> primary -> response)

## Bottlenecks
- **ConcurrentHashMap contention**: High thread count leads to CAS failures. Use striped segments.
- **Network IO**: Netty event loop must be tuned (1 event loop per core).
- **Eviction overhead**: Moving nodes in LRU deque on every access creates memory order contention.
- **Serialization/deserialization**: Java serialization is slow. Use custom binary protocol.

## Optimization Strategies
- Use striped ConcurrentHashMap (default: 16 stripes) to reduce contention
- Off-heap memory storage via DirectByteBuffer for large values
- Zero-copy serialization with Netty's ChannelBuffer
- Batch eviction: collect N candidates before scanning
- Use Epoch-based reclamation for lock-free LRU
- TCP_NODELAY for inter-node replication
