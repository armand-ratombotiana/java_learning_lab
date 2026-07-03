# 27 - Kafka Streams

Stream processing with Kafka Streams using Spring Boot. Covers Kafka Streams topology definition, stream processing DSL, stateful operations, KTable and KStream abstractions, and Spring Boot integration for Kafka Streams applications.

## Prerequisites

- Java 11+
- Maven 3.x
- Apache Kafka cluster
- Spring Boot

## Key Concepts

- Kafka Streams topology: source processors, stream processors, sink processors
- KStream: record stream abstraction for stateless/stateful transformations
- KTable: changelog stream for table-like operations
- State stores for stateful processing (aggregations, joins, windowing)
- Exactly-once semantics in stream processing
- Interactive queries for querying state stores
- Spring Boot Kafka Streams configuration

## Module Structure

- `kafka-streams-learning/` - Kafka Streams Spring Boot application

## Learning Objectives

- Build Kafka Streams topologies for stream processing
- Implement stateful stream operations with KTable and state stores
- Integrate Kafka Streams with Spring Boot

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 27-kafka-streams
mvn clean package
```

Run the Spring Boot application (requires Kafka):

```bash
cd kafka-streams-learning
mvn spring-boot:run
```
