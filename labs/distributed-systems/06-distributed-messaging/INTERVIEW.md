# Distributed Messaging: Interview Questions

## Q1: How does Kafka achieve high throughput?
**A**: Sequential disk I/O (append-only log), zero-copy transfer (sendfile), batching (batch.size, linger.ms), message compression, partitioning for parallel consumption.

## Q2: What happens when a Kafka consumer crashes?
**A**: Consumer group rebalance partitions to remaining consumers. Consumer starts from committed offset or configurable auto.offset.reset.

## Q3: Compare Kafka, Pulsar, and RabbitMQ.
**A**: Kafka: best throughput, log-based, pull model. Pulsar: cloud-native, separate compute/storage, multi-tenancy. RabbitMQ: lowest latency, push model, rich routing, lower throughput.

## Q4: How do you guarantee message ordering?
**A**: Use a single partition per ordered key (e.g., orderId → same partition). Kafka guarantees order within a partition.

## Q5: What is backpressure and how do you handle it?
**A**: Consumer can't keep up with producer. Solutions: increase partitions/consumers, slow down producers, use bounded queues, apply flow control.
