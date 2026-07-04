# CQRS - Command Query Responsibility Segregation

## Overview
CQRS separates the read and write operations of a data store. Commands handle mutations (inserts, updates, deletes) while queries handle reads. This separation allows each side to be optimized independently.

## Topics Covered
- Separate read/write models
- Command and query separation
- Event store integration
- Materialized views
- Read model projections
- CQRS with event sourcing
- Consistency models

## Java/Spring Stack
- Axon Framework for CQRS/ES
- Spring Data JPA for write model
- Spring Data JDBC/MongoDB for read models
- Kafka for event streaming
- Projection libraries
- Test containers for integration tests

## Prerequisites
- Java 17+
- Spring Boot 3.x
- Understanding of DDD concepts
- Familiarity with event-driven patterns
