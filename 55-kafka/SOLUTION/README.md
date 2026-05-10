# Kafka Solution

## Overview
This module covers Apache Kafka streams and messaging.

## Key Features

### Producer
- Creating producer
- Sending messages
- Serialization

### Consumer
- Creating consumer
- Subscribing to topics
- Polling messages

### Configuration
- Bootstrap servers
- Consumer groups
- Serializers/Deserializers

## Usage

```java
KafkaSolution solution = new KafkaSolution();

// Create producer
KafkaProducer<String, String> producer = solution.createProducer("localhost:9092");
solution.produce(producer, "my-topic", "key", "value");
producer.close();

// Create consumer
KafkaConsumer<String, String> consumer = solution.createConsumer("localhost:9092", "my-group");
solution.subscribe(consumer, "my-topic");
// Consume messages in a loop
consumer.close();
```

## Dependencies
- Kafka Clients
- JUnit 5
- Mockito