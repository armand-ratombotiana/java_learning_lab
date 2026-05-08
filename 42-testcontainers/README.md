# 42 - Testcontainers Learning Module

## Overview
Testcontainers is a Java library that provides lightweight, disposable containers for integration testing with real databases and services.

## Module Structure
- `testcontainers-framework/` - Integration test implementations

## Technology Stack
- Spring Boot 3.x
- JUnit 5 with Jupiter
- Testcontainers Java library
- Maven

## Prerequisites
- Docker running locally
- JUnit 5 tests

## Key Features
- Database container support (PostgreSQL, MySQL, MongoDB, etc.)
- Service containers (Kafka, RabbitMQ, etc.)
- Reusable containers with fixed ports
- Automatic container lifecycle management
- Docker Compose integration
- Network isolation per test class

## Build & Run Tests
```bash
cd testcontainers-framework
mvn clean test
# Each test class gets fresh containers
```

## Default Configuration
- Docker socket: `/var/run/docker.sock`
- Test class isolation per container
- Container reuse enabled

## Supported Containers
- PostgreSQL, MySQL, MariaDB, Oracle
- MongoDB, Redis, Elasticsearch
- Kafka, RabbitMQ, Consul
- Generic containers

## Related Modules
- 33-postgresql (database)
- 50-junit5 (testing)