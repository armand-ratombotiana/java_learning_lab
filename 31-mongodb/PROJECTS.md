# MongoDB Module (31) - PROJECTS.md

---

# Mini-Project: Event Sourcing with MongoDB

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: MongoDB Change Streams, Event Sourcing, CQRS, Aggregation Framework

This mini-project demonstrates event sourcing patterns using MongoDB change streams and document storage.

---

## Project Structure

```
31-mongodb/
├── pom.xml
└── mongodb-repository/
    └── src/main/java/
        ├── Main.java
        ├── model/
        │   ├── EventStore.java
        │   └── AggregateRoot.java
        ├── repository/
        │   └── EventStoreRepository.java
        └── service/
            └── EventSourcingService.java
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
    <artifactId>mongodb-repository</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Event Store Model

```java
package com.learning.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Document(collection = "event_store")
public class EventStore {
    
    @Id
    private String id;
    
    private String aggregateId;
    private String aggregateType;
    private String eventType;
    private int version;
    private Instant timestamp;
    private Map<String, Object> payload;
    private Map<String, Object> metadata;
    
    public EventStore() {
        this.timestamp = Instant.now();
    }
    
    public static EventStore create(String aggregateId, String aggregateType, 
                                    String eventType, int version, Map<String, Object> payload) {
        EventStore event = new EventStore();
        event.aggregateId = aggregateId;
        event.aggregateType = aggregateType;
        event.eventType = eventType;
        event.version = version;
        event.payload = payload;
        return event;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
```

---

## Step 3: Event Sourcing Service

```java
package com.learning.service;

import com.learning.model.EventStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventSourcingService {
    
    private final MongoTemplate mongoTemplate;
    
    public EventSourcingService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public void appendEvent(String aggregateId, String aggregateType, 
                           String eventType, Map<String, Object> payload) {
        int version = getNextVersion(aggregateId);
        
        EventStore event = EventStore.create(aggregateId, aggregateType, 
                                              eventType, version, payload);
        
        mongoTemplate.save(event, "event_store");
    }
    
    public List<EventStore> getEventsForAggregate(String aggregateId) {
        Query query = new Query(Criteria.where("aggregateId").is(aggregateId));
        query.sort(org.springframework.data.domain.Sort.by("version"));
        return mongoTemplate.find(query, EventStore.class);
    }
    
    public List<Map> getAllAggregates() {
        return mongoTemplate.getCollection("event_store")
            .distinct("aggregateId", String.class)
            .into(List.of());
    }
    
    private int getNextVersion(String aggregateId) {
        Query query = new Query(Criteria.where("aggregateId").is(aggregateId));
        query.sort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Direction.DESC, "version"));
        query.limit(1);
        
        EventStore lastEvent = mongoTemplate.findOne(query, EventStore.class);
        return lastEvent != null ? lastEvent.getVersion() + 1 : 1;
    }
}
```

---

## Step 4: Main Application

```java
package com.learning;

import com.learning.service.EventSourcingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Map;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final EventSourcingService eventSourcingService;
    
    public Main(EventSourcingService eventSourcingService) {
        this.eventSourcingService = eventSourcingService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            String orderId = "order-123";
            
            eventSourcingService.appendEvent(orderId, "Order", "OrderCreated", 
                Map.of("customerId", "cust-1", "total", 100.00));
            
            eventSourcingService.appendEvent(orderId, "Order", "ItemAdded", 
                Map.of("itemId", "item-1", "quantity", 2));
            
            List<EventStore> events = eventSourcingService.getEventsForAggregate(orderId);
            System.out.println("Retrieved " + events.size() + " events for order: " + orderId);
            
            events.forEach(e -> System.out.println("Event: " + e.getEventType() + " v" + e.getVersion()));
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
# Start MongoDB
docker run -p 27017:27017 mongo

cd 31-mongodb/mongodb-repository
mvn spring-boot:run
```

---

# Real-World Project: Distributed Event Store with MongoDB

This comprehensive project demonstrates building a production-grade event sourcing system with MongoDB change streams, snapshots, and multi-tenant support.

---

## Complete Implementation

```java
// Snapshot Store for Performance Optimization
package com.learning.snapshot;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "snapshots")
public class Snapshot {
    @Id
    private String id;
    private String aggregateId;
    private String aggregateType;
    private int version;
    private Object state;
    private long timestamp;
    
    public static Snapshot create(String aggregateId, String aggregateType, 
                                  int version, Object state) {
        Snapshot snapshot = new Snapshot();
        snapshot.aggregateId = aggregateId;
        snapshot.aggregateType = aggregateType;
        snapshot.version = version;
        snapshot.state = state;
        snapshot.timestamp = System.currentTimeMillis();
        return snapshot;
    }
    
    // Getters
    public String getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public int getVersion() { return version; }
    public Object getState() { return state; }
}
```

---

## Build Instructions

```bash
cd 31-mongodb/mongodb-repository
mvn clean compile
mvn spring-boot:run
```

---

## Key Features

- **Event Sourcing**: Full event history with version tracking
- **Change Streams**: Real-time event processing
- **Snapshots**: Performance optimization for large aggregates
- **MongoDB Aggregation**: Complex event queries and analytics