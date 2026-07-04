# Distributed Messaging: Flashcards

## Front: What is a message broker?
**Back**: Middleware that enables async communication between services.

## Front: What is pub/sub?
**Back**: Publish-subscribe - messages delivered to all subscribers of a topic.

## Front: How does Kafka store messages?
**Back**: Append-only commit log on disk, partitioned by key.

## Front: What is a consumer group?
**Back**: Group of consumers sharing partitions of a topic.

## Front: What is exactly-once delivery?
**Back**: Message delivered precisely once (idempotency + transactions).

## Front: What is Pulsar's architecture innovation?
**Back**: Separate stateless brokers from stateful BookKeeper storage.

## Front: What is RabbitMQ's routing mechanism?
**Back**: Exchanges with bindings routing messages to queues.

## Front: What is consumer lag?
**Back**: How far behind consumers are from the latest produced messages.
