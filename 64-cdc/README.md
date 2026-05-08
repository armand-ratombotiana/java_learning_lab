# Debezium - Change Data Capture

## Overview
Debezium is an open-source CDC platform that captures database changes and streams them as events to Kafka or other connectors.

## Key Features
- Database support (MySQL, Postgres, MongoDB, SQL Server)
- Schema change capture
- Exactly-once delivery
- Kafka Connect integration
- Debezium Server (standalone)

## Project Structure
```
64-cdc/
  debezium-learning/
    src/main/java/com/learning/cdc/debezium/DebeziumLab.java
```

## Running
```bash
cd 64-cdc/debezium-learning
mvn compile exec:java
```

## Concepts Covered
- CDC event structure
- Connector configuration
- Debezium Server
- Event formats

## Dependencies
- Debezium API