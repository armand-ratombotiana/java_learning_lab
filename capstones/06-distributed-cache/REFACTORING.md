# Refactoring: Distributed Cache

## Current Pain Points
- Single-threaded eviction scanner doesn't scale with memory
- Full membership gossip creates O(N) traffic per round
- No persistence (all data lost on restart)
- Client library has no connection pooling
- No multi-get pipelining optimization

## Suggested Improvements
- Partition eviction: each partition has its own eviction thread
- Implement delta gossip: only send changes since last exchange
- Add optional persistence (RDB snapshots, AOF log)
- Implement Netty-based connection pooling in client library
- Add command pipelining for batch operations (MSET, MGET)
- Add TLS support for inter-node and client communication
- Implement read replicas for scaling read throughput
