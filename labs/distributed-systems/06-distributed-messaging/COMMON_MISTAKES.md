# Common Mistakes with Distributed Messaging

## 1. At-Least-Once Without Idempotency
Processing a message twice causes duplicate data. Consumers must be idempotent.

## 2. Blocking on Poll
Long message processing blocks Kafka consumer heartbeats, causing rebalance.

## 3. Too Many Partitions
Each partition adds overhead. 1000+ partitions can cause performance issues.

## 4. Ignoring Message Ordering
Order only guaranteed within a partition. Global ordering requires single partition (limits throughput).

## 5. Not Handling Poison Messages
Malformed messages that crash consumers can stall the queue.

## 6. Incorrect Ack Configuration
- acks=0: Fast but may lose messages
- acks=1: Good durability, leader fail may lose messages
- acks=all: Max durability, slowest
