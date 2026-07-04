# Kafka Streams Theory

## Stream-Table Duality
- **KStream**: Append-only log of facts
- **KTable**: Changelog representing current state
- Either can be derived from the other

## Processing Guarantees
- At-most-once: No retries
- At-least-once: Retry with duplicates
- Exactly-once: Kafka transactions + idempotent writes

## State Stores
- RocksDB: Embedded KV store (default)
- InMemory: Pure in-memory
