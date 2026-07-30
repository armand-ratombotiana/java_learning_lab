# System Design Cheatsheet

## Key Concepts
- **Horizontal vs Vertical Scaling**: Add more machines vs upgrade a single machine.
- **Load Balancing**: Round robin, least connections, consistent hashing.
- **Caching**: Redis, Memcached, CDN, cache-aside, write-through.
- **Database Sharding**: Horizontal partitioning, consistent hashing.
- **Replication**: Leader-follower, multi-leader, quorum.
- **CAP Theorem**: Consistency, Availability, Partition tolerance (pick 2).
- **Consistency Models**: Strong, eventual, causal, read-your-writes.
- **Message Queues**: Kafka, RabbitMQ, SQS (decoupling, buffering).

## Design Patterns
- **CQRS**: Separate read/write models.
- **Event Sourcing**: Store all state changes as events.
- **Saga Pattern**: Distributed transactions with compensating actions.
- **Circuit Breaker**: Fail fast, graceful degradation.
- **Rate Limiting**: Token bucket, leaky bucket, sliding window.
- **Idempotency**: Dedup via idempotency keys.

## System Design Steps
1. **Requirements**: Functional + non-functional (latency, throughput, durability).
2. **Estimation**: QPS, storage, bandwidth.
3. **Data Model**: Schema, indexing, partitioning key.
4. **High-Level Design**: Components, API, data flow.
5. **Deep Dive**: Scaling bottlenecks, consistency, caching strategy.

## Example Estimations
- Twitter: ~500M tweets/day → ~6000 writes/sec → ~100TB storage/year.
- URL Shortener: ~100M URLs/year → ~3.17 writes/sec → ~1TB storage/10yr.
- Chat System: ~1.5B users, ~100 msg/user/day → ~1.7M writes/sec.

## Common Systems to Know
- URL Shortener (tinyurl)
- Chat System (WhatsApp, Messenger)
- Social Feed (Twitter, Instagram, Facebook)
- Video Streaming (YouTube, Netflix)
- Ride Sharing (Uber, Lyft)
- E-commerce (Amazon, eBay)
- Distributed File System (HDFS, GFS)
- Distributed Cache (Redis Cluster, Memcached)
