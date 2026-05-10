# CDC Projects - Module 64

This module covers Change Data Capture, Debezium, and Kafka Connect for real-time data synchronization.

## Mini-Project: Debezium MySQL CDC (2-4 hours)

### Overview
Set up Debezium for capturing database changes from MySQL and streaming them to Kafka.

### Project Structure
```
cdc-demo/
├── debezium/
│   ├── docker-compose.yml
│   ├── mysql.json
│   └── connect-distributed.properties
├── src/main/java/com/learning/cdc/
│   ├── CdcApplication.java
│   └── consumer/ChangeEventConsumer.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>cdc-demo</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
</project>

// docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: debezium
      MYSQL_USER: mysql
      MYSQL_PASSWORD: mysql
    ports:
      - "3306:3306"
    command: --server-id=1 --log-bin=mysql-bin --binlog-format=ROW

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  connect:
    image: debezium/connect:2.4
    ports:
      - "8083:8083"
    environment:
      BOOTSTRAP_SERVERS: kafka:9092
      GROUP_ID: debezium-group
      CONFIG_STORAGE_TOPIC: connect-configs
      OFFSET_STORAGE_TOPIC: connect-offsets

# MySQL Debezium connector config
{
  "name": "mysql-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "debezium",
    "database.server.id": "1",
    "database.server.name": "dbserver1",
    "database.include.list": "inventory",
    "table.include.list": "inventory.customers",
    "database.history.kafka.bootstrap.servers": "kafka:9092",
    "include.schema.changes": "true",
    "transforms": "unwrap",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState"
  }
}
```

---

## Real-World Project: Multi-Database CDC Pipeline (8+ hours)

### Overview
Build a comprehensive CDC pipeline that captures changes from multiple databases (MySQL, PostgreSQL, MongoDB) and streams them through Kafka to various sinks.

### Project Structure
```
enterprise-cdc/
├── debezium-configs/
│   ├── mysql-connector.json
│   ├── postgres-connector.json
│   └── mongo-connector.json
├── kafka-streams/
│   └── src/main/java/com/learning/cdc/
├── sinks/
│   └── elasticsearch-sink/
├── src/main/java/com/learning/cdc/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>enterprise-cdc</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-streams-avro-serde</artifactId>
            <version>7.5.0</version>
        </dependency>
    </dependencies>
</project>

// CdcEventProcessor.java
package com.learning.cdc;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CdcEventProcessor {
    
    private final Map<String, KafkaStreams> streamProcessors = new ConcurrentHashMap<>();
    
    public void processChangeEvents() {
        StreamsBuilder builder = new StreamsBuilder();
        
        KStream<String, String> changeEvents = builder.stream("dbserver1.inventory.customers");
        
        changeEvents
            .filter((key, value) -> value != null && !value.isEmpty())
            .mapValues(this::transformToEnrichedEvent)
            .peek((key, value) -> System.out.println("Processed: " + key + " -> " + value))
            .to("enriched-customers");
        
        KTable<String, String> aggregated = builder.table(
            "orders-changes",
            Materialized.as("orders-store")
        );
        
        aggregated
            .groupBy((key, value) -> Map.entry(extractUserId(value), value))
            .aggregate(
                () -> new AggregatedOrders(),
                (key, newValue, aggregate) -> aggregate.addOrder(newValue),
                (key, oldValue, newValue) -> aggregate.updateOrder(newValue),
                Materialized.as("user-orders-store")
            )
            .toStream()
            .to("aggregated-orders");
        
        KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfig());
        streams.start();
        
        streamProcessors.put("main", streams);
    }
    
    private String transformToEnrichedEvent(String cdcEvent) {
        return cdcEvent + " | enriched:" + System.currentTimeMillis();
    }
    
    private String extractUserId(String value) {
        return "user-1";
    }
    
    private Map<String, Object> getStreamsConfig() {
        return Map.of(
            "application.id", "cdc-processor",
            "bootstrap.servers", "localhost:9092",
            "default.key.serde", Serdes.String().getClass().getName(),
            "default.value.serde", Serdes.String().getClass().getName()
        );
    }
}

class AggregatedOrders {
    private int count;
    private double total;
    
    public AggregatedOrders addOrder(String order) {
        count++;
        total += Math.random() * 1000;
        return this;
    }
    
    public AggregatedOrders updateOrder(String order) {
        return this;
    }
}

// ChangeEventConsumer.java
package com.learning.cdc.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class ChangeEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ChangeEventConsumer.class);
    
    @KafkaListener(topics = "dbserver1.inventory.orders", groupId = "cdc-consumer-group")
    public void consumeOrderChange(String message) {
        logger.info("Received order change: {}", message);
        
        try {
            Map<String, Object> event = parseEvent(message);
            processOrderChange(event);
        } catch (Exception e) {
            logger.error("Error processing order change", e);
        }
    }
    
    @KafkaListener(topics = "dbserver1.inventory.customers", groupId = "cdc-consumer-group")
    public void consumeCustomerChange(String message) {
        logger.info("Received customer change: {}", message);
        
        try {
            Map<String, Object> event = parseEvent(message);
            processCustomerChange(event);
        } catch (Exception e) {
            logger.error("Error processing customer change", e);
        }
    }
    
    private Map<String, Object> parseEvent(String message) {
        return Map.of("raw", message);
    }
    
    private void processOrderChange(Map<String, Object> event) {
        logger.info("Processing order: {}", event.get("orderId"));
    }
    
    private void processCustomerChange(Map<String, Object> event) {
        logger.info("Processing customer: {}", event.get("customerId"));
    }
}
```

### Build and Run
```bash
# Start all services
docker-compose up -d

# Register MySQL connector
curl -i -X POST -H "Accept:application/json" \
  -H "Content-Type:application/json" \
  http://localhost:8083/connectors/ \
  -d @debezium-configs/mysql-connector.json

# Verify connector
curl http://localhost:8083/connectors/mysql-connector/status

# Make database changes
mysql -h localhost -u root -pdebezium -e "INSERT INTO inventory.customers VALUES(...)"

# Consume from Kafka
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic dbserver1.inventory.customers \
  --from-beginning

# Run Java consumer
mvn spring-boot:run
```

### Learning Outcomes
- Set up Debezium for CDC
- Configure multiple database connectors
- Process change events with Kafka Streams
- Build event-driven consumers
- Handle schema changes
- Monitor CDC pipeline