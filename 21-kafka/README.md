# Kafka Module

<div align="center">

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Streaming-000000?style=for-the-badge)
![Confluent](https://img.shields.io/badge/Confluent-Platform-30ABAD?style=for-the-badge)

**Master Event Streaming with Apache Kafka**

</div>

---

## Overview

This module covers Apache Kafka fundamentals, stream processing, and event-driven architecture. You'll learn how to build real-time data pipelines and event streaming applications.

---

## Topics Covered

### 1. Kafka Fundamentals
- Topics, partitions, and replicas
- Producers and consumers
- Consumer groups
- Offset management

### 2. Producer Patterns
- Synchronous vs asynchronous sending
- Message serialization (JSON, Avro, Protobuf)
- Partitioning strategies
- Idempotent producers

### 3. Consumer Patterns
- Manual vs auto offset commit
- Consumer group rebalancing
- Exactly-once semantics
- Multi-threaded consumers

### 4. Kafka Streams
- Stateful stream processing
- Windowed aggregations
- Join operations
- Exactly-once processing

### 5. Production Patterns
- Schema Registry
- Dead letter queues
- Transactional outbox
- Event sourcing

---

## Module Structure

```
21-kafka/
├── README.md                      # This file
├── PROJECTS.md                    # Hands-on projects
├── PEDAGOGIC_GUIDE.md            # Teaching guide
├── EXERCISES.md                  # Practice exercises
└── src/main/java/com/learning/   # Source code
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Kafka cluster (local or Docker)

### Start Kafka (Docker)
```bash
docker run -p 9092:9092 -p 9093:9093 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka:latest
```

### Run Examples
```bash
cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Kafka Cluster                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐     │
│  │ Topic: orders   │  │ Topic: payments │  │ Topic: events   │     │
│  │ Partitions: 3   │  │ Partitions: 3   │  │ Partitions: 3   │     │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘     │
└─────────────────────────────────────────────────────────────────────┘
        ▲                       ▲                       ▲
        │                       │                       │
┌───────┴───────┐       ┌───────┴───────┐       ┌───────┴───────┐
│  Producers    │       │   Consumers   │       │   Streams     │
│  - Orders     │       │  - Analytics  │       │  - Aggregation│
│  - Payments   │       │  - Warehouse  │       │  - Joins      │
└───────────────┘       └───────────────┘       └───────────────┘
```

---

## Key Concepts

### Partitioning Strategies

```java
// By user ID - ensures user orders go to same partition
kafkaTemplate.send("orders", order.getUserId().toString(), orderJson);

// By order ID - for parallel processing
kafkaTemplate.send("orders", order.getOrderId(), orderJson);

// Round-robin - for load balancing
kafkaTemplate.send("orders", null, orderJson);
```

### Consumer Group Example

```java
@KafkaListener(topics = "orders", groupId = "order-processing")
public void processOrder(String message) {
    // Each consumer in group processes different partitions
}

@KafkaListener(topics = "orders", groupId = "analytics-group")  
public void analyzeOrder(String message) {
    // Separate consumer group gets all messages again
}
```

---

## Production Patterns

1. **Schema Registry**: Avro/Protobuf schemas with evolution
2. **Dead Letter Queues**: Failed message handling
3. **Transactional Outbox**: Reliable event publishing
4. **Event Sourcing**: Complete audit trail
5. **Exactly-Once**: End-to-end processing guarantees

---

## Next Steps

After completing this module, proceed to:
- [27-kafka-streams](../27-kafka-streams) - Advanced stream processing
- [62-data-pipeline](../62-data-pipeline) - Data pipeline design

---

## Resources

- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Confluent Blog](https://www.confluent.io/blog/)
- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)

