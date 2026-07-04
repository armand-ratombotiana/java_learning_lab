# Distributed Messaging: Internals

## Kafka Internals

### Partition Architecture
- Each topic has N partitions
- Partitions are ordered, immutable log segments
- Each partition has a leader (handles reads/writes)
- Followers replicate from leader

### Storage
- Messages written to segment files on disk
- Index files for fast offset lookup
- Configurable retention (time or size)
- Compaction for key-based retention

### Consumer Groups
- Each partition assigned to one consumer in group
- Rebalance on consumer join/leave
- Sticky assignment for minimal disruption

## Pulsar Internals

### Separation of Concerns
- **Broker**: Stateless serving layer
- **BookKeeper**: Stateful storage layer
- Each can scale independently

### Segment-Based Storage
- Messages organized into segments (ledgers)
- Segments distributed across BookKeeper nodes
- Read from any replica for high throughput

## RabbitMQ Internals

### Exchange Types
- Direct: Exact routing key match
- Topic: Pattern matching routing key
- Fanout: Broadcast to all queues
- Headers: Match based on headers
