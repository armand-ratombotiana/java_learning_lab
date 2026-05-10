# Testcontainers Solution

## Overview
This module covers container-based testing with Testcontainers.

## Key Features

### Database Containers
- PostgreSQL
- MySQL
- MongoDB

### Messaging Containers
- Kafka
- RabbitMQ
- NATS

### Custom Containers
- Generic containers
- Docker image configuration
- Port mapping

## Usage

```java
TestcontainersSolution solution = new TestcontainersSolution();

// PostgreSQL
PostgreSQLContainer postgres = solution.createPostgresContainer();
postgres.start();
String jdbcUrl = solution.getJdbcUrl(postgres);

// Kafka
KafkaContainer kafka = solution.createKafkaContainer();
kafka.start();

// Generic
GenericContainer nginx = solution.createGenericContainer("nginx");
nginx.start();
```

## Dependencies
- Testcontainers Java
- JUnit 5
- Database drivers