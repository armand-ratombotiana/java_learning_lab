# Kafka Streams Module - PROJECTS.md

---

# Mini-Project: Stream Processing Application

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Kafka Streams API, KStream, KTable, Aggregation, Windowing, Join Operations

This mini-project demonstrates Kafka Streams for building real-time stream processing applications. You'll create a word count processor that reads from a text stream, counts word frequencies, and outputs results in real-time.

---

## Project Structure

```
27-kafka-streams/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── topology/
│   │   └── WordCountTopology.java
│   ├── serializer/
│   │   └── JsonSerializer.java
│   └── config/
│       └── KafkaStreamsConfig.java
└── src/main/resources/
    └── application.properties
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>kafka-streams-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <kafka.version>3.6.0</kafka.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-streams</artifactId>
            <version>${kafka.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Stream Processing Topology

```java
// topology/WordCountTopology.java
package com.learning.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

@Configuration
public class WordCountTopology {
    
    @Bean
    public KStream<String, Long> wordCountStream(StreamsBuilder streamsBuilder) {
        // Define input and output topics
        String inputTopic = "text-input";
        String outputTopic = "word-count-output";
        
        // Create KStream from input topic
        KStream<String, String> textStream = streamsBuilder.stream(
            inputTopic,
            Consumed.with(Serdes.String(), Serdes.String())
        );
        
        // Process: split lines into words, group by word, count occurrences
        KStream<String, Long> wordCounts = textStream
            .flatMapValues(value -> {
                // Split on whitespace and filter empty strings
                return Arrays.asList(value.toLowerCase().split("\\s+"))
                    .stream()
                    .filter(word -> !word.isBlank())
                    .toList();
            })
            .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
            .count(Materialized.as("word-counts-store"))
            .toStream();
        
        // Write to output topic
        wordCounts.to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        
        // Also print to console for debugging
        wordCounts.foreach((word, count) -> 
            System.out.println("Word: " + word + " -> Count: " + count)
        );
        
        return wordCounts;
    }
}
```

```java
// config/KafkaStreamsConfig.java
package com.learning.config;

import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaStreamsConfig {
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    @Bean
    public StreamsConfig streamsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
            org.apache.kafka.common.serialization.Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 
            org.apache.kafka.common.serialization.Serdes.String().getClass());
        config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        
        return new StreamsConfig(config);
    }
    
    @Bean
    public NewTopic inputTopic() {
        return new NewTopic("text-input", 3, (short) 1);
    }
    
    @Bean
    public NewTopic outputTopic() {
        return new NewTopic("word-count-output", 3, (short) 1);
    }
}
```

---

## Step 3: Main Application

```java
// Main.java
package com.learning;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Properties;

@SpringBootApplication
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public CommandLineRunner run(KafkaStreams kafkaStreams) {
        return args -> {
            System.out.println("=== Kafka Streams Word Count Demo ===");
            System.out.println("Starting Kafka Streams application...");
            
            kafkaStreams.start();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
            
            System.out.println("Kafka Streams started. Send messages to text-input topic.");
        };
    }
}
```

---

## Step 4: Produce Test Data

```java
// Test producer to send messages
package com.learning;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import java.util.Scanner;

public class TestProducer {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            "org.apache.kafka.common.serialization.StringSerializer");
        
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter text (or 'quit' to exit):");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("quit".equalsIgnoreCase(line)) break;
            
            // Send to text-input topic
            producer.send(new ProducerRecord<>("text-input", null, line));
            System.out.println("Sent: " + line);
        }
        
        producer.close();
    }
}
```

---

## Build Instructions

```bash
# Start Kafka (using Docker)
docker run -p 2181:2181 -p 9092:9092 -e KAFKA_ADVERTISED_HOST_NAME=localhost \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 confluentinc/cp-kafka

cd 27-kafka-streams
mvn clean compile
mvn spring-boot:run

# In another terminal, run TestProducer to send messages
# Or use kafka-console-producer:
# kafka-console-producer --bootstrap-server localhost:9092 --topic text-input
```

---

# Real-World Project: Event Processing Pipeline

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Kafka Streams, Windowed Aggregations, Table-Stream Joins, Exactly-Once Semantics, Interactive Queries, State Stores

This comprehensive project implements a complete real-time event processing pipeline for an e-commerce system that handles user events, performs session-based analysis, and integrates with inventory systems.

---

## Complete Implementation

```java
// topology/EventProcessingTopology.java
package com.learning.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.StoreType;
import org.apache.kafka.streams.errors.StreamsException;
import java.time.Duration;
import java.util.Arrays;

public class EventProcessingTopology {
    
    private static final String EVENTS_TOPIC = "user-events";
    private static final String PRODUCTS_TOPIC = "products";
    private static final String SESSIONS_TOPIC = "user-sessions";
    private static final String ALERTS_TOPIC = "alerts";
    private static final String ENRICHED_EVENTS_TOPIC = "enriched-events";
    
    @Bean
    public KStream<String, Event> processEvents(StreamsBuilder streamsBuilder) {
        // Create streams from topics
        KStream<String, Event> eventsStream = streamsBuilder.stream(
            EVENTS_TOPIC,
            Consumed.with(Serdes.String(), new EventSerde())
        );
        
        KTable<String, Product> productsTable = streamsBuilder.table(
            PRODUCTS_TOPIC,
            Consumed.with(Serdes.String(), new ProductSerde()),
            Materialized.as("products-store")
        );
        
        // Enrich events with product data (Stream-Table Join)
        KStream<String, EnrichedEvent> enrichedEvents = eventsStream
            .leftJoin(productsTable, (event, product) -> {
                if (product == null) {
                    return new EnrichedEvent(event, null);
                }
                return new EnrichedEvent(event, product);
            });
        
        enrichedEvents.to(ENRICHED_EVENTS_TOPIC, 
            Produced.with(Serdes.String(), new EnrichedEventSerde()));
        
        // Session window aggregation
        KStream<String, SessionAggregate> sessionAggregates = enrichedEvents
            .groupByKey(Grouped.with(Serdes.String(), new EnrichedEventSerde()))
            .windowedBy(SessionWindows.ofInactivityTimeoutAndGrace(
                Duration.ofMinutes(5), Duration.ofSeconds(30)))
            .aggregate(
                SessionAggregate::new,
                (key, event, aggregate) -> aggregate.add(event),
                (key, agg1, agg2) -> agg1.merge(agg2),
                Materialized.<String, SessionAggregate, WindowedStore<Bytes, byte[]>>
                    as("session-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new SessionAggregateSerde())
            )
            .toStream();
        
        sessionAggregates.to(SESSIONS_TOPIC, 
            Produced.with(Serdes.String(), new SessionAggregateSerde()));
        
        // Alert generation based on thresholds
        enrichedEvents
            .filter((key, event) -> event.getEvent().getType().equals("add_to_cart"))
            .filter((key, event) -> event.getProduct() != null 
                && event.getProduct().getStockQuantity() < 5)
            .map((key, event) -> {
                Alert alert = new Alert(
                    "LOW_STOCK",
                    "Product " + event.getProduct().getName() + " low stock: " 
                        + event.getProduct().getStockQuantity(),
                    event.getEvent().getUserId()
                );
                return KeyValue.pair(key, alert);
            })
            .to(ALERTS_TOPIC, Produced.with(Serdes.String(), new AlertSerde()));
        
        return eventsStream;
    }
}
```

---

## Domain Models

```java
// models/Event.java
package com.learning.models;

import java.time.Instant;
import java.util.Map;

public class Event {
    private String id;
    private String userId;
    private String type;
    private String productId;
    private Map<String, Object> properties;
    private Instant timestamp;
    
    public enum EventType {
        PAGE_VIEW,
        PRODUCT_VIEW,
        ADD_TO_CART,
        REMOVE_FROM_CART,
        CHECKOUT,
        PURCHASE
    }
    
    public Event() {}
    
    public Event(String id, String userId, EventType type, String productId) {
        this.id = id;
        this.userId = userId;
        this.type = type.name();
        this.productId = productId;
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
```

```java
// models/Product.java
package com.learning.models;

import java.math.BigDecimal;

public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stockQuantity;
    
    public Product() {}
    
    public Product(String id, String name, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
```

```java
// models/EnrichedEvent.java
package com.learning.models;

public class EnrichedEvent {
    private final Event event;
    private final Product product;
    
    public EnrichedEvent(Event event, Product product) {
        this.event = event;
        this.product = product;
    }
    
    public Event getEvent() { return event; }
    public Product getProduct() { return product; }
}
```

---

## Session Aggregation

```java
// models/SessionAggregate.java
package com.learning.models;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public class SessionAggregate {
    private String userId;
    private Instant sessionStart;
    private Instant sessionEnd;
    private List<String> events;
    private int pageViews;
    private int addToCarts;
    private double totalValue;
    
    public SessionAggregate() {
        this.events = new ArrayList<>();
    }
    
    public SessionAggregate add(EnrichedEvent event) {
        this.events.add(event.getEvent().getType());
        
        if (event.getEvent().getType().equals("PAGE_VIEW")) {
            pageViews++;
        } else if (event.getEvent().getType().equals("ADD_TO_CART")) {
            addToCarts++;
            if (event.getProduct() != null) {
                totalValue += event.getProduct().getPrice().doubleValue();
            }
        }
        
        if (sessionStart == null) {
            sessionStart = event.getEvent().getTimestamp();
        }
        sessionEnd = event.getEvent().getTimestamp();
        
        return this;
    }
    
    public SessionAggregate merge(SessionAggregate other) {
        this.events.addAll(other.events);
        this.pageViews += other.pageViews;
        this.addToCarts += other.addToCarts;
        this.totalValue += other.totalValue;
        
        if (sessionStart == null || other.sessionStart.isBefore(sessionStart)) {
            sessionStart = other.sessionStart;
        }
        if (sessionEnd == null || other.sessionEnd.isAfter(sessionEnd)) {
            sessionEnd = other.sessionEnd;
        }
        
        return this;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public Instant getSessionStart() { return sessionStart; }
    public Instant getSessionEnd() { return sessionEnd; }
    public List<String> getEvents() { return events; }
    public int getPageViews() { return pageViews; }
    public int getAddToCarts() { return addToCarts; }
    public double getTotalValue() { return totalValue; }
}
```

```java
// models/Alert.java
package com.learning.models;

public class Alert {
    private String type;
    private String message;
    private String userId;
    private Instant timestamp;
    
    public Alert() {}
    
    public Alert(String type, String message, String userId) {
        this.type = type;
        this.message = message;
        this.userId = userId;
        this.timestamp = Instant.now();
    }
    
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public Instant getTimestamp() { return timestamp; }
}
```

---

## Interactive Queries (State Store Access)

```java
// service/InteractiveQueryService.java
package com.learning.service;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.*;
import org.springframework.stereotype.Service;
import com.learning.models.*;
import java.util.List;
import java.util.Optional;

@Service
public class InteractiveQueryService {
    
    private final KafkaStreams kafkaStreams;
    
    public InteractiveQueryService(KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }
    
    public Optional<Product> getProduct(String productId) {
        ReadOnlyKeyValueStore<String, Product> store = kafkaStreams
            .store(StoreQueryParameters.fromStoreNameAndType(
                "products-store", 
                QueryableStoreTypes.keyValueStore()));
        
        return Optional.ofNullable(store.get(productId));
    }
    
    public List<Product> getAllProducts() {
        ReadOnlyKeyValueStore<String, Product> store = kafkaStreams
            .store(StoreQueryParameters.fromStoreNameAndType(
                "products-store",
                QueryableStoreTypes.keyValueStore()));
        
        return store.all().map(ReadOnlyKeyValueStore::Value).toList();
    }
    
    public long getWordCount(String word) {
        ReadOnlyKeyValueStore<String, Long> store = kafkaStreams
            .store(StoreQueryParameters.fromStoreNameAndType(
                "word-counts-store",
                QueryableStoreTypes.keyValueStore()));
        
        return store.get(word) != null ? store.get(word) : 0L;
    }
}
```

---

## Advanced Windowing Operations

```java
// topology/WindowedAggregations.java
package com.learning.topology;

import org.apache.kafka.streams.kstream.*;
import java.time.Duration;
import java.time.Instant;

public class WindowedAggregations {
    
    public static void tumblingWindowDemo(KStream<String, Event> stream) {
        // Tumbling window: non-overlapping, fixed size
        KTable<Windowed<String>, Long> counts = stream
            .groupByKey(Grouped.with(Serdes.String(), new EventSerde()))
            .windowedBy(TumblingWindows.of(Duration.ofMinutes(5)))
            .count();
        
        counts.toStream().foreach((window, count) -> 
            System.out.println("Window: " + window.window().start() + " - Count: " + count)
        );
    }
    
    public static void hoppingWindowDemo(KStream<String, Event> stream) {
        // Hopping window: overlapping, configurable advance interval
        KTable<Windowed<String>, Long> counts = stream
            .groupByKey(Grouped.with(Serdes.String(), new EventSerde()))
            .windowedBy(HoppingWindows.of(Duration.ofMinutes(10))
                .advanceBy(Duration.ofMinutes(5)))
            .count();
    }
    
    public static void sessionWindowDemo(KStream<String, Event> stream) {
        // Session window: activity-based, gaps determine sessions
        KTable<Windowed<String>, Long> counts = stream
            .groupByKey(Grouped.with(Serdes.String(), new EventSerde()))
            .windowedBy(SessionWindows.ofInactivityTimeout(
                Duration.ofMinutes(10)))
            .count();
    }
}
```

---

## Build Instructions (Real-World Project)

```bash
cd 27-kafka-streams
mvn clean compile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.kafka.bootstrap-servers=localhost:9092"

# Create topics
kafka-topics.sh --create --topic user-events --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
kafka-topics.sh --create --topic products --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
kafka-topics.sh --create --topic enriched-events --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
kafka-topics.sh --create --topic alerts --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092

# Produce test events
kafka-console-producer --topic user-events --bootstrap-server localhost:9092 < events.json
kafka-console-producer --topic products --bootstrap-server localhost:9092 < products.json

# Consume output
kafka-console-consumer --topic enriched-events --from-beginning --bootstrap-server localhost:9092
kafka-console-consumer --topic alerts --from-beginning --bootstrap-server localhost:9092
```

---

## Key Kafka Streams Concepts

| Concept | Description |
|---------|--------------|
| KStream | Immutable, append-only record stream |
| KTable | Mutable key-value table, changelog stream |
| Topology | Directed acyclic graph of processing steps |
| Stream | Continuous flow of records |
| State Store | Local fault-tolerant state backing KTable |
| Window | Time-based grouping for aggregations |
| Join | Combining streams/tables based on keys |

---

## Summary

This module covers:

1. **Basic Kafka Streams**: Word count example with KStream/KTable
2. **Windowing**: Tumbling, hopping, and session windows
3. **Stream-Table Joins**: Enriching events with reference data
4. **Interactive Queries**: Accessing state stores directly
5. **Real-World Patterns**: E-commerce event processing pipeline

The skills learned here enable building real-time analytics, monitoring systems, and event-driven applications using Kafka.