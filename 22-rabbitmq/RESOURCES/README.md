# RabbitMQ Resources

Message broker patterns and RabbitMQ concepts.

## Contents

- [Message Patterns Diagram](./message-patterns-diagram.md) - Patterns and routing

---

## Official Documentation

| Topic | Link |
|-------|------|
| RabbitMQ Docs | https://www.rabbitmq.com/documentation.html |
| RabbitMQ Tutorials | https://www.rabbitmq.com/tutorials/ |
| AMQP Spec | https://www.amqp.org/ |

---

## Key Concepts

### Core Concepts
- **Producer** - Sends messages
- **Consumer** - Receives messages
- **Exchange** - Routes messages to queues
- **Queue** - Stores messages
- **Binding** - Links exchange to queue
- **Virtual Host** - Isolated environment

### Exchange Types
- **Direct** - Exact routing key match
- **Fanout** - Broadcast to all queues
- **Topic** - Pattern-based routing
- **Headers** - Attribute-based routing