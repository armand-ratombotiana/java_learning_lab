# Reflection: Event Streaming

## What I Learned
- The power of sequential disk I/O for high-throughput messaging
- Replication protocol and ISR management
- Consumer group coordination and rebalancing
- Log storage internals: segments, indexes, compaction
- Zero-copy data transfer from disk to network

## Challenges
- Implementing leader election without ZooKeeper (consensus is hard)
- Debugging replication protocol edge cases (truncation, log divergence)
- Managing file handles with many segments and partitions
- Tuning page cache vs application memory allocation

## What I'd Do Differently
- Start with the log storage layer before networking
- Implement the wire protocol with proper error codes from the beginning
- Add metrics and monitoring hooks before integration testing
- Use property-based testing for partition assignment algorithms
- Add replica fetcher metrics early for debugging replication issues
