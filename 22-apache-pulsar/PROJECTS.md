# Apache Pulsar Projects

This directory contains hands-on projects using Apache Pulsar, a distributed messaging and streaming platform. Pulsar provides pub-sub messaging, streaming, and queuing with strong ordering guarantees.

## Project Overview

Apache Pulsar combines the simplicity of modern cloud-native architecture with powerful messaging features. This module covers two projects demonstrating different Pulsar use cases.

---

# Mini-Project: Message Queue (2-4 Hours)

## Project Description

Build a message queue system using Apache Pulsar for asynchronous communication between services. This project demonstrates Pulsar's producer, consumer, and topic management.

## Technologies Used

- Apache Pulsar Client 3.1.0
- Java 21
- Maven
- Spring Boot

## Implementation Steps

### Step 1: Create Project Structure

```bash
mkdir pulsar-messaging
cd pulsar-messaging
mkdir -p src/main/java/com/learning/pulsar/{config,producer,consumer,model}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>pulsar-messaging</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Pulsar Messaging</name>
    <description>Message queue with Apache Pulsar</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <pulsar.version>3.1.0</pulsar.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.pulsar</groupId>
            <artifactId>pulsar-client</artifactId>
            <version>${pulsar.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.pulsar</groupId>
            <artifactId>pulsar Springs</artifactId>
            <version>${pulsar.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Message Model

```java
package com.learning.pulsar.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String type;
    private String payload;
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;
    private int priority;
    
    public Notification() {
        this.timestamp = LocalDateTime.now();
    }
    
    public Notification(String id, String type, String payload, String sender, String receiver) {
        this();
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.sender = sender;
        this.receiver = receiver;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}
```

### Step 4: Create Pulsar Configuration

```java
package com.learning.pulsar.config;

import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.auth.AuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class PulsarConfig {
    
    @Value("${pulsar.service-url:pulsar://localhost:6650}")
    private String serviceUrl;
    
    @Value("${pulsar.admin-url:http://localhost:8080}")
    private String adminUrl;
    
    @Value("${pulsar.auth-token:}")
    private String authToken;
    
    @Bean
    public PulsarClient pulsarClient() throws Exception {
        ClientBuilder clientBuilder = PulsarClient.builder()
            .serviceUrl(serviceUrl);
        
        if (!authToken.isEmpty()) {
            clientBuilder.authentication(new AuthenticationToken(authToken));
        }
        
        return clientBuilder.build();
    }
    
    @Bean
    public Producer<byte[]> notificationProducer(PulsarClient client) throws Exception {
        return client.newProducer()
            .topic("persistent://learning/notifications")
            .compressionType(CompressionType.LZ4)
            .batchingMaxMessages(100)
            .batchingMaxBytes(4096)
            .batchingMaxPublishDelay(10, java.util.concurrent.TimeUnit.MILLISECONDS)
            .enableChunking(true)
            .build();
    }
    
    @Bean
    public Consumer<byte[]> notificationConsumer(PulsarClient client) throws Exception {
        return client.newConsumer()
            .topic("persistent://learning/notifications")
            .subscriptionName("notification-subscription")
            .ackTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .negativeAckRedeliveryDelay(60, java.util.concurrent.TimeUnit.SECONDS)
            .subscriptionType(SubscriptionType.Shared)
            .build();
    }
}
```

### Step 5: Create Message Producer

```java
package com.learning.pulsar.producer;

import com.learning.pulsar.model.Notification;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationProducer {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    
    private final Producer<byte[]> producer;
    private final ObjectMapper objectMapper;
    
    public NotificationProducer(Producer<byte[]> producer) {
        this.producer = producer;
        this.objectMapper = new ObjectMapper();
    }
    
    public String send(Notification notification) throws Exception {
        notification.setId(UUID.randomUUID().toString());
        
        byte[] data = objectMapper.writeValueAsBytes(notification);
        
        Message<byte[]> msg = producer.newMessage()
            .key(notification.getReceiver())
            .value(data)
            .property("type", notification.getType())
            .property("sender", notification.getSender())
            .property("receiver", notification.getReceiver())
            .send();
        
        log.info("Sent notification: {} to topic", notification.getId());
        
        return notification.getId();
    }
    
    public CompletableFuture<MessageId> sendAsync(Notification notification) {
        notification.setId(UUID.randomUUID().toString());
        
        return producer.newMessage()
            .key(notification.getReceiver())
            .value(notification.getId().getBytes())
            .property("type", notification.getType())
            .sendAsync();
    }
    
    public void sendBatch(java.util.List<Notification> notifications) throws Exception {
        for (Notification notification : notifications) {
            send(notification);
        }
    }
    
    public void close() throws Exception {
        producer.close();
    }
}
```

### Step 6: Create Message Consumer

```java
package com.learning.pulsar.consumer;

import com.learning.pulsar.model.Notification;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;

@Service
public class NotificationConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    
    private final Consumer<byte[]> consumer;
    private final ObjectMapper objectMapper;
    private final MessageHandler handler;
    
    public NotificationConsumer(Consumer<byte[]> consumer) {
        this.consumer = consumer;
        this.objectMapper = new ObjectMapper();
        
        this.handler = (msg) -> {
            try {
                byte[] value = msg.getValue();
                
                Notification notification = objectMapper.readValue(
                    value, Notification.class);
                
                log.info("Received notification: {} from {} to {}", 
                    notification.getId(), notification.getSender(), 
                    notification.getReceiver());
                
                consumer.accept(msg);
                
            } catch (Exception e) {
                log.error("Failed to process message", e);
            }
        };
    }
    
    public void startConsuming() throws Exception {
        ((org.apache.pulsar.client.api.Consumer) consumer).messageListener(handler);
    }
    
    public void manualAck(Message msg) throws Exception {
        ((org.apache.pulsar.client.api.Consumer) consumer).acknowledge(msg);
    }
    
    public void negativeAck(Message msg) throws Exception {
        ((org.apache.pulsar.client.api.Consumer) consumer).negativeAcknowledge(msg);
    }
    
    @FunctionalInterface
    public interface MessageHandler {
        void handle(Message msg);
    }
}
```

### Step 7: Create REST Controller

```java
package com.learning.pulsar.resource;

import com.learning.pulsar.model.Notification;
import com.learning.pulsar.producer.NotificationProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {
    
    private final NotificationProducer producer;
    
    public NotificationResource(NotificationProducer producer) {
        this.producer = producer;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> send(
            @RequestBody NotificationRequest request) {
        
        try {
            Notification notification = new Notification(
                null,
                request.type(),
                request.payload(),
                request.sender(),
                request.receiver()
            );
            
            String messageId = producer.send(notification);
            
            Map<String, String> response = new HashMap<>();
            response.put("messageId", messageId);
            response.put("status", "sent");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .build();
        }
    }
    
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> sendBatch(
            @RequestBody BatchNotificationRequest request) {
        
        try {
            java.util.List<Notification> notifications = request.notifications()
                .stream()
                .map(req -> new Notification(
                    null,
                    req.type(),
                    req.payload(),
                    req.sender(),
                    req.receiver()
                ))
                .toList();
            
            producer.sendBatch(notifications);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", notifications.size());
            response.put("status", "sent");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .build();
        }
    }
    
    record NotificationRequest(String type, String payload, String sender, String receiver) {}
    record BatchNotificationRequest(java.util.List<NotificationRequest> notifications) {}
}
```

### Step 8: Configure Pulsar

Create `src/main/resources/application.yaml`:

```yaml
pulsar:
  service-url: pulsar://localhost:6650
  admin-url: http://localhost:8080
  auth-token: ""

server:
  port: 8080
```

### Step 9: Run and Test

```bash
# Build project
cd pulsar-messaging
mvn clean package

# Run Pulsar standalone
docker run -dit --name pulsar -p 6650:6650 -p 8080:8080 apachepulsar/pulsar:latest bin/pulsar standalone

# Run application
java -jar target/pulsar-messaging-1.0.0.jar

# Test API
# Send notification
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "type": "EMAIL",
    "payload": "Welcome to our service!",
    "sender": "system",
    "receiver": "user@example.com"
  }'

# Send batch
curl -X POST http://localhost:8080/api/notifications/batch \
  -H "Content-Type: application/json" \
  -d '{
    "notifications": [
      {"type": "SMS", "payload": "Message 1", "sender": "system", "receiver": "user1"},
      {"type": "SMS", "payload": "Message 2", "sender": "system", "receiver": "user2"}
    ]
  }'
```

## Expected Output

- Message producer with batching
- Message consumer with ack
- Topic-based routing
- Async messaging support

---

# Real-World Project: Event Streaming Platform (8+ Hours)

## Project Description

Build an event streaming platform using Apache Pulsar with multiple topics, partitions, and stream processing. Supports event sourcing and real-time analytics.

## Architecture

```
┌──────────────┐     ┌──────────────┐
│  Producers  │────►│   Topics     │
│  (Services) │     │(Partitions) │
└──────────────┘     └──────────────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
    ┌────▼────┐        ┌────▼────┐        ┌──▼────┐
    │ Stream  │        │ Stream  │        │ Stream│
    │ Processor 1    │ Processor 2    │Processor 3 │
    └─────────┘      └─────────┘       └───────┘
```

## Implementation Steps

### Step 1: Create Stream Processing Project

```bash
mkdir pulsar-event-streaming
cd pulsar-event-streaming
mkdir -p src/main/java/com/learning/streaming/{config,source,sink,processor,model}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>pulsar-event-streaming</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Pulsar Event Streaming</name>
    <description>Event streaming platform with Apache Pulsar</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <pulsar.version>3.1.0</pulsar.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.pulsar</groupId>
            <artifactId>pulsar-client</artifactId>
            <version>${pulsar.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.pulsar</groupId>
            <artifactId>pulsar-functions</artifactId>
            <version>${pulsar.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Event Models

```java
package com.learning.streaming.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

public class Event implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String eventId;
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private String payload;
    private Map<String, String> metadata;
    private LocalDateTime timestamp;
    private long version;
    
    public Event() {
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }
    
    public Event(String eventType, String aggregateType, String aggregateId, String payload) {
        this();
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
    }
    
    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
}
```

```java
package com.learning.streaming.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClickEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String userId;
    private String pageUrl;
    private String elementId;
    private String elementText;
    private int duration;
    private LocalDateTime timestamp;
    
    public ClickEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ClickEvent(String sessionId, String userId, String pageUrl) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
        this.pageUrl = pageUrl;
    }
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    
    public String getElementId() { return elementId; }
    public void setElementId(String elementId) { this.elementId = elementId; }
    
    public String getElementText() { return elementText; }
    public void setElementText(String elementText) { this.elementText = elementText; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
```

### Step 4: Create Event Producer

```java
package com.learning.streaming.source;

import com.learning.streaming.model.ClickEvent;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import java.util.UUID;

@Service
public class EventStreamProducer {
    
    private final Producer<byte[]> clickEventProducer;
    private final Producer<byte[]> analyticsProducer;
    private final ObjectMapper objectMapper;
    private final Random random;
    
    public EventStreamProducer(PulsarClient client) throws Exception {
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
        
        this.clickEventProducer = client.newProducer()
            .topic("persistent://analytics/click-events")
            .compressionType(CompressionType.LZ4)
            .enableChunking(true)
            .build();
        
        this.analyticsProducer = client.newProducer()
            .topic("persistent://analytics/aggregated")
            .build();
    }
    
    public String publishClickEvent(ClickEvent event) throws Exception {
        byte[] data = objectMapper.writeValueAsBytes(event);
        
        MessageId msgId = clickEventProducer.newMessage()
            .key(event.getSessionId())
            .value(data)
            .property("eventType", "CLICK")
            .property("userId", event.getUserId())
            .send();
        
        return msgId.toString();
    }
    
    public String publishAggregatedEvent(String aggregatedData) throws Exception {
        MessageId msgId = analyticsProducer.newMessage()
            .value(aggregatedData.getBytes())
            .property("eventType", "AGGREGATED")
            .send();
        
        return msgId.toString();
    }
    
    public void close() throws Exception {
        clickEventProducer.close();
        analyticsProducer.close();
    }
}
```

### Step 5: Create Stream Processor

```java
package com.learning.streaming.processor;

import com.learning.streaming.model.ClickEvent;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClickStreamProcessor {
    
    private static final Logger log = LoggerFactory.getLogger(ClickStreamProcessor.class);
    
    private final Consumer<byte[]> clickConsumer;
    private final Producer<byte[]> aggregatedProducer;
    private final ObjectMapper objectMapper;
    private final Map<String, PageViewStats> pageStats;
    private final Map<String, UserSessionStats> userSessions;
    
    public ClickStreamProcessor(PulsarClient client) throws Exception {
        this.objectMapper = new ObjectMapper();
        this.pageStats = new ConcurrentHashMap<>();
        this.userSessions = new ConcurrentHashMap<>();
        
        this.clickConsumer = client.newConsumer()
            .topic("persistent://analytics/click-events")
            .subscriptionName("click-processor")
            .subscriptionType(SubscriptionType.Shared)
            .ackTimeout(30, TimeUnit.SECONDS)
            .build();
        
        this.aggregatedProducer = client.newProducer()
            .topic("persistent://analytics/aggregated")
            .build();
        
        startProcessing();
    }
    
    private void startProcessing() {
        new Thread(() -> {
            while (true) {
                try {
                    Message<byte[]> msg = clickConsumer.receive();
                    processClickEvent(msg);
                    clickConsumer.acknowledge(msg);
                } catch (Exception e) {
                    log.error("Error processing click event", e);
                }
            }
        }).start();
    }
    
    private void processClickEvent(Message<byte[]> msg) throws Exception {
        ClickEvent event = objectMapper.readValue(
            msg.getValue(), ClickEvent.class);
        
        updatePageStats(event);
        updateUserSession(event);
        publishAggregatedStats(event.getPageUrl());
    }
    
    private void updatePageStats(ClickEvent event) {
        String pageUrl = event.getPageUrl();
        
        pageStats.compute(pageUrl, (key, stats) -> {
            if (stats == null) {
                stats = new PageViewStats(pageUrl);
            }
            stats.incrementClickCount();
            
            if (event.getDuration() > 0) {
                stats.addDuration(event.getDuration());
            }
            
            return stats;
        });
    }
    
    private void updateUserSession(ClickEvent event) {
        String sessionId = event.getSessionId();
        
        userSessions.compute(sessionId, (key, session) -> {
            if (session == null) {
                session = new UserSessionStats(sessionId);
            }
            session.incrementPageViews();
            
            return session;
        });
    }
    
    private void publishAggregatedStats(String pageUrl) throws Exception {
        PageViewStats stats = pageStats.get(pageUrl);
        
        if (stats != null) {
            String aggregated = objectMapper.writeValueAsString(stats);
            
            aggregatedProducer.newMessage()
                .key(pageUrl)
                .value(aggregated.getBytes())
                .send();
        }
    }
    
    public Map<String, PageViewStats> getPageStats() {
        return new HashMap<>(pageStats);
    }
    
    public Map<String, UserSessionStats> getUserSessions() {
        return new HashMap<>(userSessions);
    }
    
    public static class PageViewStats {
        private String pageUrl;
        private long clickCount;
        private List<Integer> durations;
        private LocalDateTime lastUpdated;
        
        public PageViewStats(String pageUrl) {
            this.pageUrl = pageUrl;
            this.durations = new ArrayList<>();
            this.lastUpdated = LocalDateTime.now();
        }
        
        public void incrementClickCount() {
            clickCount++;
            lastUpdated = LocalDateTime.now();
        }
        
        public void addDuration(int duration) {
            durations.add(duration);
        }
        
        public double getAverageDuration() {
            return durations.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
        }
        
        // Getters
        public String getPageUrl() { return pageUrl; }
        public long getClickCount() { return clickCount; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
    }
    
    public static class UserSessionStats {
        private String sessionId;
        private long pageViews;
        private LocalDateTime lastActivity;
        
        public UserSessionStats(String sessionId) {
            this.sessionId = sessionId;
            this.lastActivity = LocalDateTime.now();
        }
        
        public void incrementPageViews() {
            pageViews++;
            lastActivity = LocalDateTime.now();
        }
        
        public String getSessionId() { return sessionId; }
        public long getPageViews() { return pageViews; }
        public LocalDateTime getLastActivity() { return lastActivity; }
    }
}
```

### Step 6: Create Event Subscriptions

```java
package com.learning.streaming.sink;

import com.learning.streaming.model.ClickEvent;
import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;

@Service
public class AnalyticsEventSink {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventSink.class);
    
    private final Consumer<byte[]> consumer;
    private final ObjectMapper objectMapper;
    
    public AnalyticsEventSink(PulsarClient client, AnalyticsEventSink.EventHandler handler) 
            throws Exception {
        this.objectMapper = new ObjectMapper();
        
        this.consumer = client.newConsumer()
            .topic("persistent://analytics/aggregated")
            .subscriptionName("analytics-dashboard")
            .subscriptionType(SubscriptionType.Failover)
            .build();
        
        ((Consumer) consumer).messageListener((msg) -> {
            try {
                handler.handle(msg);
                ((Consumer) consumer).acknowledge(msg);
            } catch (Exception e) {
                log.error("Error in analytics event handler", e);
            }
        });
    }
    
    @FunctionalInterface
    public interface EventHandler {
        void handle(Message msg) throws Exception;
    }
}
```

### Step 7: Create REST API

```java
package com.learning.streaming.resource;

import com.learning.streaming.model.ClickEvent;
import com.learning.streaming.source.EventStreamProducer;
import com.learning.streaming.processor.ClickStreamProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/streaming")
public class StreamResource {
    
    private final EventStreamProducer producer;
    private final ClickStreamProcessor processor;
    
    public StreamResource(EventStreamProducer producer, ClickStreamProcessor processor) {
        this.producer = producer;
        this.processor = processor;
    }
    
    @PostMapping("/click")
    public ResponseEntity<Map<String, String>> publishClick(
            @RequestBody ClickEventRequest request) {
        
        try {
            ClickEvent event = new ClickEvent(
                request.sessionId(),
                request.userId(),
                request.pageUrl()
            );
            event.setElementId(request.elementId());
            event.setElementText(request.elementText());
            event.setDuration(request.duration());
            
            String messageId = producer.publishClickEvent(event);
            
            Map<String, String> response = new HashMap<>();
            response.put("messageId", messageId);
            response.put("status", "published");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/stats/pages")
    public ResponseEntity<Map<String, Object>> getPageStats() {
        return ResponseEntity.ok(
            new HashMap<>(processor.getPageStats())
        );
    }
    
    @GetMapping("/stats/sessions")
    public ResponseEntity<Map<String, Object>> getUserSessions() {
        return ResponseEntity.ok(
            new HashMap<>(processor.getUserSessions())
        );
    }
    
    @GetMapping("/stats/pages/{pageUrl}")
    public ResponseEntity<Object> getPageStats(
            @PathVariable String pageUrl) {
        
        return ResponseEntity.ok(
            processor.getPageStats().get(pageUrl)
        );
    }
    
    record ClickEventRequest(
        String sessionId,
        String userId,
        String pageUrl,
        String elementId,
        String elementText,
        int duration
    ) {}
}
```

### Step 8: Run and Test

```bash
# Build project
cd pulsar-event-streaming
mvn clean package

# Run Pulsar
docker run -dit --name pulsar -p 6650:6650 -p 8080:8080 apachepulsar/pulsar:latest bin/pulsar standalone

# Run application
java -jar target/pulsar-event-streaming-1.0.0.jar

# Test streaming
# Publish click events
curl -X POST http://localhost:8080/api/streaming/click \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "sess-001",
    "userId": "user-123",
    "pageUrl": "/products",
    "elementId": "buy-button",
    "elementText": "Buy Now",
    "duration": 5000
  }'

# Get page statistics
curl http://localhost:8080/api/streaming/stats/pages

# Get session statistics
curl http://localhost:8080/api/streaming/stats/sessions
```

## Expected Output

- Event streaming with partitions
- Real-time stream processing
- Click analytics aggregation
- Session tracking

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Setup project | Maven | 15 min |
| Create producers | Implementation | 1.5 hours |
| Create processors | Stream processing | 2 hours |
| Create consumers | Event handling | 1.5 hours |
| Create API | REST endpoints | 1 hour |
| Testing | Integration | 3 hours |

Total estimated time: 8-10 hours