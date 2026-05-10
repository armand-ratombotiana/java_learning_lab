# Kafka - Projects

This document contains two complete projects demonstrating Apache Kafka: a mini-project for learning producers/consumers and a real-world project implementing production-grade streaming.

## Project 1: Kafka Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Kafka concepts including producers, consumers, and basic messaging.

### Project Structure

```
kafka-basics/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── kafka/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>kafka-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <kafka.version>3.6.1</kafka.version>
    </properties>
</project>
```

### ProducerExample.java

```java
package com.learning.kafka;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class ProducerExample {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test-topic", "key", "message");
            producer.send(record);
        }
    }
}
```

### ConsumerExample.java

```java
package com.learning.kafka;

import org.apache.kafka.clients.consumer.*;
import java.util.*;

public class ConsumerExample {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("test-topic"));
            
            while (true) {
                var records = consumer.poll(100);
                for (var record : records) {
                    System.out.println(record.value());
                }
            }
        }
    }
}
```

## Project 2: Production Kafka Streaming

### OrderProducer.java

```java
package com.learning.kafka;

import org.apache.kafka.clients.producer.*;
import java.util.*;

public class OrderProducer {
    
    private final KafkaProducer<String, String> producer;
    
    public OrderProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        producer = new KafkaProducer<>(props);
    }
    
    public void sendOrder(String orderId, String orderData) {
        ProducerRecord<String, String> record = new ProducerRecord<>("orders", orderId, orderData);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error: " + exception.getMessage());
            }
        });
    }
    
    public void close() {
        producer.close();
    }
}
```

### OrderConsumer.java

```java
package com.learning.kafka;

import org.apache.kafka.clients.consumer.*;
import java.util.*;

public class OrderConsumer {
    
    private final KafkaConsumer<String, String> consumer;
    
    public OrderConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "order-processor");
        props.put("auto.offset.reset", "earliest");
        
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("orders"));
    }
    
    public void process() {
        while (true) {
            var records = consumer.poll(100);
            for (var record : records) {
                System.out.println("Processing: " + record.value());
            }
        }
    }
}
```

### Build and Run

```bash
cd kafka-production

# Start Kafka
docker run -p 9092:9092 -p 2181:2181 confluent/kafka

# Create topic
kafka-topics --create --topic orders --bootstrap-server localhost:9092

# Run producer
java -cp target/kafka-production.jar com.learning.kafka.OrderProducer

# Run consumer
java -cp target/kafka-production.jar com.learning.kafka.OrderConsumer
```

## Summary

These projects demonstrate:
1. **Mini-Project**: Basic Kafka producer/consumer messaging
2. **Production Project**: Order processing with async producers and scalable consumers

Apache Kafka enables high-throughput distributed messaging for microservices.