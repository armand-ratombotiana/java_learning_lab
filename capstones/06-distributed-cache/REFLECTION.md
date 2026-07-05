# Reflection: Distributed Cache

## What I Learned
- Consistent hashing mechanics and virtual node optimization
- Gossip protocol design for decentralized cluster management
- Cache eviction algorithms and their performance characteristics
- Replication strategies and consistency trade-offs
- Building a custom binary protocol with Netty

## Challenges
- Implementing lock-free LRU with ConcurrentLinkedDeque under high throughput
- Tuning gossip suspicion timeout to avoid false positives vs slow detection
- Debugging split-brain scenarios during network partitions
- Balancing write performance with replication consistency guarantees

## What I'd Do Differently
- Implement the binary protocol with Netty ChannelHandler from the start
- Add persistence earlier (data loss on restart is painful for testing)
- Build the client library with automatic failover first
- Use more thorough property-based testing for consistent hashing
