# Kafka Streams Internals

## Task Architecture
Application splits into tasks (one per partition). Each task has its own consumer + producer.

## State Management
RocksDB state stores with changelog topics for fault tolerance. Local state + Kafka-backed recovery.

## Exactly-Once
Transactional writes to output + offset commits in same transaction.
