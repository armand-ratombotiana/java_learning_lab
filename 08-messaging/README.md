# 08 - Messaging Systems

Message-oriented middleware integration with Java. Covers Apache Kafka (topics, partitions, producers, consumers, consumer groups, delivery semantics), RabbitMQ (AMQP, exchanges, queues, bindings), ActiveMQ (JMS), and NATS (lightweight messaging).

## Prerequisites

- Java 11+
- Maven 3.x
- Docker (for running message brokers)

## Key Concepts

- Kafka: topics/partitions, producers (acks, key-based partitioning), consumers (poll, offset commit, consumer groups), delivery semantics (at-least-once, at-most-once, exactly-once)
- RabbitMQ: AMQP model, exchanges (direct, topic, fanout), queues, bindings, routing keys
- ActiveMQ: JMS API, queues, topics, message listeners
- NATS: publish-subscribe, request-reply, jetstream

## Module Structure

- `01-kafka/` - Apache Kafka fundamentals
- `02-rabbitmq/` - RabbitMQ messaging
- `03-activemq/` - ActiveMQ JMS integration
- `04-nats/` - NATS lightweight messaging

## Learning Objectives

- Produce and consume messages with Kafka
- Configure RabbitMQ exchanges, queues, and bindings
- Understand messaging patterns across different brokers

## Estimated Time

- 5-8 hours across all submodules

## How to Build

```bash
cd 08-messaging
mvn clean package
```

Run individual submodules (requires the corresponding broker):

```bash
cd 01-kafka
mvn compile exec:java -Dexec.mainClass="com.learning.messaging.Lab"
```
