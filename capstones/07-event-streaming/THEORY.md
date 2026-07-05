# Theory: Event Streaming

## Event-Driven Architecture
Systems communicate through events rather than direct RPC calls. Services produce events to topics, and consumers subscribe to relevant topics. This enables loose coupling, asynchronous processing, and audit trails.

## Kafka Architecture
- **Topic**: Logical channel for related events
- **Partition**: Ordered, immutable sequence of records; unit of parallelism
- **Broker**: Server that stores and serves partitions
- **Producer**: Publishes events to topic partitions
- **Consumer**: Subscribes to topics, reads events from partitions
- **Consumer Group**: Set of consumers that coordinate to read partitions (each partition assigned to one consumer)

## Log Compaction
Retains the latest value for each key rather than all events. Useful for state snapshots (e.g., latest account balance). Older versions are compacted away, keeping only the most recent record per key.

## Exactly-Once Semantics
End-to-end exactly-once requires:
- Idempotent producer (no duplicates from retries)
- Transactional producer (atomic writes to multiple partitions)
- Consumer offset stored in the same transaction as processing output

## Stream Processing
Continuous processing of event streams with stateful operations (aggregations, joins, windowing). Kafka Streams provides a DSL for stream processing in Java.
