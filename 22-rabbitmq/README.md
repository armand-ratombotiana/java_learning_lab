# RabbitMQ Module

<div align="center">

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-FF6600?style=for-the-badge)
![Spring AMQP](https://img.shields.io/badge/Spring%20AMQP-Integration-6DB33F?style=for-the-badge)

**Master Message-Driven Architecture with RabbitMQ**

</div>

---

## Overview

This module covers RabbitMQ messaging patterns, exchange types, queue configurations, and production-ready message handling. You'll learn how to build robust asynchronous communication systems.

---

## Topics Covered

### 1. RabbitMQ Fundamentals
- Exchanges, queues, and bindings
- Exchange types (Direct, Fanout, Topic, Headers)
- Message properties and delivery modes
- Connection management

### 2. Producer Patterns
- Message serialization
- Publisher confirms
- Mandatory flag and returns
- Message templates

### 3. Consumer Patterns
- Annotation-based listeners
- Manual acknowledgment
- Prefetch and concurrency
- Consumer recovery

### 4. Routing Patterns
- Topic routing with wildcards
- Dead letter queues
- Message priorities
- Delayed messages

### 5. Production Patterns
- Cluster federation
- Shovel plugin
- High availability
- Monitoring and alerting

---

## Module Structure

```
22-rabbitmq/
├── README.md                      # This file
├── PROJECTS.md                    # Hands-on projects
├── PEDAGOGIC_GUIDE.md            # Teaching guide
├── EXERCISES.md                  # Practice exercises
└── src/main/java/com/learning/   # Source code
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- RabbitMQ (Docker)

### Start RabbitMQ
```bash
docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Management UI: http://localhost:15672
Username: guest / Password: guest

### Run Examples
```bash
cd 22-rabbitmq
mvn clean compile
mvn spring-boot:run
```

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         RabbitMQ Cluster                             │
│                                                                      │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐        │
│  │   Direct     │     │   Fanout     │     │   Topic     │        │
│  │  Exchange    │     │   Exchange   │     │   Exchange   │        │
│  └──────┬───────┘     └──────┬───────┘     └──────┬───────┘        │
│         │                    │                    │                   │
│    routing.key             (all)           *.orders.#             │
│         │                    │                    │                   │
│         ▼                    ▼                    ▼                   │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐        │
│  │ order.queue  │     │ email.queue  │     │notification │        │
│  │              │     │              │     │    .queue   │        │
│  │  + DLQ       │     │              │     │              │        │
│  └──────────────┘     └──────────────┘     └──────────────┘        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Key Concepts

### Exchange Types

```java
// Direct - route by exact key
@Bean
public DirectExchange directExchange() {
    return new DirectExchange("order.exchange");
}

// Fanout - route to all bound queues
@Bean
public FanoutExchange fanoutExchange() {
    return new FanoutExchange("notification.exchange");
}

// Topic - route by pattern matching
@Bean
public TopicExchange topicExchange() {
    return new TopicExchange("event.exchange");
}
```

### Queue Configuration with DLQ

```java
@Bean
public Queue orderQueue() {
    return QueueBuilder.durable("order.queue")
        .withArgument("x-dead-letter-exchange", "dlx.exchange")
        .withArgument("x-dead-letter-routing-key", "order.failed")
        .build();
}

@Bean
public Queue orderDLQ() {
    return QueueBuilder.durable("order.dlq").build();
}
```

---

## Production Patterns

1. **Publisher Confirms**: Ensure message delivery to broker
2. **Dead Letter Queues**: Handle failed messages
3. **Message Priorities**: Process critical messages first
4. **Delayed Messages**: Schedule future processing
5. **Cluster Federation**: Geographic distribution

---

## Next Steps

After completing this module, proceed to:
- [08-messaging](../08-messaging) - Multi-messaging systems
- [16-apache-camel](../16-apache-camel) - Integration patterns

---

## Resources

- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [Spring AMQP Reference](https://docs.spring.io/spring-amqp/reference/html/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)

