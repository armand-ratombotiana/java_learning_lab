# Kafka Connect - Data Integration

## Overview
Kafka Connect is a framework for connecting Kafka with external systems using connectors for source and sink data pipelines.

## Key Features
- Source and Sink connectors
- Distributed and standalone modes
- Single message transformation
- Offset management
- Schema management

## Project Structure
```
55-kafka/
  kafka-connect/
    src/main/java/com/learning/kafka/connect/KafkaConnectLab.java
```

## Running
```bash
cd 55-kafka/kafka-connect
mvn compile exec:java
```

## Concepts Covered
- Source connector configuration
- Sink connector configuration
- Connect REST API
- Transformations

## Dependencies
- Kafka Connect API