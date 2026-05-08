# 34 - RabbitMQ Learning Module

## Overview
RabbitMQ is a message broker implementing the AMQP protocol. This module covers message queuing patterns with Spring Boot.

## Module Structure
- `rabbitmq-messaging/` - Spring AMQP implementation

## Technology Stack
- Spring Boot 3.x
- Spring AMQP (RabbitMQ)
- RabbitMQ Java Client
- Maven

## Prerequisites
- RabbitMQ server running on `localhost:5672`
- Management UI: `http://localhost:15672` (guest/guest)

## Key Features
- AMQP 0-9-1 protocol support
- Multiple exchange types (direct, fanout, topic, headers)
- Queue bindings and routing
- Message acknowledgments
- Dead letter queues
- Clustering and federation

## Build & Run
```bash
cd rabbitmq-messaging
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `5672`
- Username: `guest`
- Password: `guest`
- Virtual host: `/`

## Related Modules
- 32-redis (caching comparison)
- 27-kafka-streams (streaming comparison)