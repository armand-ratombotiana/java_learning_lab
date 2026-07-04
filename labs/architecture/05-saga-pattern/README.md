# Saga Pattern

## Overview
The Saga pattern manages distributed transactions across microservices by breaking them into a series of local transactions with compensating actions. Sagas ensure data consistency without requiring distributed transactions (2PC).

## Topics Covered
- Saga orchestration (central coordinator)
- Saga choreography (event-driven)
- Distributed transaction management
- Compensating transactions
- Saga state machines
- Failure handling and recovery
- Idempotency in sagas

## Java/Spring Stack
- Spring Boot for service implementation
- Axon Framework for saga orchestration
- Kafka/RabbitMQ for choreography
- Camunda/Zeebe for workflow orchestration
- Eventuate Tram for saga coordination
- Resilience4j for retry and circuit breaker

## Prerequisites
- Java 17+
- Understanding of distributed systems
- Event-driven architecture basics
- Microservices concepts
