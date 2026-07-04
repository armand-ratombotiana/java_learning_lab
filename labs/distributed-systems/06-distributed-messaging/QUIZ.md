# Distributed Messaging: Quiz

## Questions
1. What is the difference between a queue and a topic?
2. How does Kafka achieve high throughput?
3. What is a consumer group?
4. What does acks=all mean in Kafka?
5. What is a dead letter queue?
6. How does Pulsar differ from Kafka?
7. What is AMQP?
8. What are RabbitMQ exchange types?
9. What is consumer lag?
10. How do you achieve exactly-once semantics in Kafka?

## Answers
1. Queue: one consumer per message. Topic: all subscribers get all messages
2. Sequential disk I/O, batching, partitioning, zero-copy
3. Group of consumers that divide topic partitions among themselves
4. Producer waits for all in-sync replicas to acknowledge
5. Queue for messages that couldn't be processed
6. Pulsar separates compute (broker) from storage (BookKeeper)
7. Advanced Message Queuing Protocol
8. Direct, Topic, Fanout, Headers
9. Difference between produced and consumed messages
10. Idempotent producer + transactions + idempotent consumer
