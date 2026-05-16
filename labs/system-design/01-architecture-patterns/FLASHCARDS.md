# Architecture Patterns - FLASHCARDS

## Flashcard Set Overview
This flashcard set contains 40 cards covering key concepts from all four architecture patterns.

---

## Layered Architecture Flashcards (1-10)

### Flashcard 1
**Term**: Layered Architecture
**Definition**: A software architecture pattern that organizes code into horizontal layers, each with specific responsibilities. The most common is the 3-tier model: Presentation, Business, Data.

---

### Flashcard 2
**Term**: Presentation Layer
**Definition**: The top layer that handles user interface and HTTP requests/responses. Contains controllers, REST endpoints, and UI components.

---

### Flashcard 3
**Term**: Business Layer
**Definition**: The middle layer that contains core business logic, rules, and domain logic. Also called the service layer or domain layer.

---

### Flashcard 4
**Term**: Data Layer
**Definition**: The bottom layer responsible for data access and storage. Contains repositories, DAOs, and ORM mappings.

---

### Flashcard 5
**Term**: Strict Layering
**Definition**: A principle where each layer can only access the layer directly below it, ensuring clean separation and maintainability.

---

### Flashcard 6
**Term**: DTO (Data Transfer Object)
**Definition**: A simple object used to transfer data between layers, especially between presentation and business layers. Contains no business logic.

---

### Flashcard 7
**Term**: Repository Pattern
**Definition**: An abstraction layer in the Data Layer that provides a collection-like interface for accessing domain entities.

---

### Flashcard 8
**Term**: Anemic Domain Model
**Definition**: An anti-pattern where domain objects only have getters/setters with no business logic, usually caused by putting all logic in services.

---

### Flashcard 9
**Term**: Cross-Cutting Concern
**Definition**: Functionality that affects multiple parts of the application, such as logging, security, and transaction management.

---

### Flashcard 10
**Term**: Transaction Script
**Definition**: A simple procedural approach to organizing business logic around use cases, common in layered architectures.

---

## Microservices Flashcards (11-20)

### Flashcard 11
**Term**: Microservices Architecture
**Definition**: An architectural style that structures an application as a collection of small, autonomous services, each owning a business capability.

---

### Flashcard 12
**Term**: Service Discovery
**Definition**: A mechanism that allows services to find each other's network locations dynamically, typically using a service registry like Eureka or Consul.

---

### Flashcard 13
**Term**: API Gateway
**Definition**: A server that acts as the single entry point for a set of microservices, handling routing, authentication, rate limiting, and cross-cutting concerns.

---

### Flashcard 14
**Term**: Circuit Breaker
**Definition**: A design pattern that prevents cascading failures by stopping requests to a failing service and providing fallback behavior.

---

### Flashcard 15
**Term**: Database per Service
**Definition**: A principle where each microservice owns its data and has its own database, preventing tight coupling through shared data.

---

### Flashcard 16
**Term**: Service Mesh
**Definition**: A dedicated infrastructure layer for handling service-to-service communication, providing features like load balancing, security, and observability.

---

### Flashcard 17
**Term**: Sidecar Pattern
**Definition**: Deploying auxiliary components alongside a service to handle cross-cutting concerns like monitoring, logging, and configuration.

---

### Flashcard 18
**Term**: BFF (Backend for Frontend)
**Definition**: A pattern where a separate service is created for each frontend type (mobile, web) to optimize API responses for that specific client.

---

### Flashcard 19
**Term**: Distributed Tracing
**Definition**: A technique to track requests across multiple services, assigning a unique ID that flows through all services for debugging and monitoring.

---

### Flashcard 20
**Term**: Container Orchestration
**Definition**: Automated management of containerized microservices, including deployment, scaling, and networking (e.g., Kubernetes, Docker Swarm).

---

## Event-Driven Flashcards (21-30)

### Flashcard 21
**Term**: Event-Driven Architecture (EDA)
**Definition**: An architecture where components communicate by producing and consuming events rather than making direct calls.

---

### Flashcard 22
**Term**: Domain Event
**Definition**: An event that represents a significant business occurrence, such as "OrderPlaced" or "PaymentCompleted".

---

### Flashcard 23
**Term**: Event Broker
**Definition**: Middleware that handles event routing between producers and consumers, examples include Kafka, RabbitMQ, and AWS SNS.

---

### Flashcard 24
**Term**: Pub/Sub Pattern
**Definition**: A messaging pattern where publishers broadcast events to multiple subscribers without knowing who is listening.

---

### Flashcard 25
**Term**: Event Carried State Transfer
**Definition**: A pattern where consumers maintain their own copy of data by consuming events, reducing dependency on the producer.

---

### Flashcard 26
**Term**: Eventual Consistency
**Definition**: A consistency model where updates propagate over time, so different parts of the system may be temporarily inconsistent.

---

### Flashcard 27
**Term**: Idempotency
**Definition**: The property that an operation can be applied multiple times without changing the result beyond the first application, crucial for reliable event processing.

---

### Flashcard 28
**Term**: Event Sourcing
**Definition**: A pattern that stores all state changes as a sequence of events rather than storing the current state directly.

---

### Flashcard 29
**Term**: CQRS
**Definition**: Command Query Responsibility Segregation - a pattern that separates read and write operations into different models, allowing independent optimization.

---

### Flashcard 30
**Term**: Command
**Definition**: An instruction to perform a specific action that modifies state. Unlike queries, commands return void or a result indicating success/failure.

---

## CQRS Flashcards (31-40)

### Flashcard 31
**Term**: Command Side
**Definition**: The write side of CQRS that handles all commands (Create, Update, Delete), validates business rules, and produces events.

---

### Flashcard 32
**Term**: Query Side
**Definition**: The read side of CQRS that handles all read operations and returns data optimized for consumption.

---

### Flashcard 33
**Term**: Read Model
**Definition**: A data model optimized for reading, separate from the write model. Can be denormalized and stored in a different database.

---

### Flashcard 34
**Term**: Write Model
**Definition**: The data model optimized for writes, typically normalized and following domain-driven design principles.

---

### Flashcard 35
**Term**: Projection
**Definition**: A component that builds and maintains read models by processing events from the write model.

---

### Flashcard 36
**Term**: Event Store
**Definition**: A database that stores events in the order they occurred, serving as the source of truth in event sourcing.

---

### Flashcard 37
**Term**: Materialized View
**Definition**: A pre-computed query result stored for fast reading, built from the write model or events.

---

### Flashcard 38
**Term**: Synchronous Update
**Definition**: A CQRS pattern where the read model is updated immediately after the write completes, ensuring strong consistency.

---

### Flashcard 39
**Term**: Asynchronous Update
**Definition**: A CQRS pattern where events are published and read models are updated later, accepting eventual consistency.

---

### Flashcard 40
**Term**: Saga Pattern
**Definition**: A pattern for managing distributed transactions across microservices using a sequence of local transactions and compensating actions.

---

## Quick Reference Card

| Pattern | Key Concept | Use When |
|---------|-------------|----------|
| Layered | Horizontal layers | Simple apps, small teams |
| Microservices | Independent services | Large apps, multiple teams |
| Event-Driven | Async events | High throughput, loose coupling |
| CQRS | Separate read/write | Complex queries, different loads |

---

## Study Tips

1. **Spaced Repetition**: Review flashcards daily, increasing interval as you master each card
2. **Active Recall**: Try to explain each concept in your own words before checking the definition
3. **Group by Pattern**: Study flashcards by pattern to build mental frameworks
4. **Make Connections**: Link concepts across patterns (e.g., event-driven microservices using CQRS)
5. **Test Yourself**: Cover definitions and try to recall terms from examples