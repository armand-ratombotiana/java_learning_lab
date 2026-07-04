# Architecture Patterns - INTERVIEW

## Common Interview Questions

### Q1: When would you choose microservices over monolithic?
**Answer**: When the team grows beyond 10-15 developers, when deployment independence is needed, when different services require different scaling, or when technology diversity is desired.

### Q2: Explain the Saga pattern.
**Answer**: A saga is a sequence of local transactions where each transaction publishes an event. If a transaction fails, compensating transactions undo previous changes. Two implementations: choreography (events) and orchestration (central coordinator).

### Q3: How do you handle distributed transactions?
**Answer**: Use Saga pattern with compensating transactions. Avoid two-phase commit (2PC) in distributed systems due to blocking and coordinator failure issues.

### Q4: What's the difference between CQRS and Event Sourcing?
**Answer**: CQRS separates read/write models. Event Sourcing stores state changes as events. They complement each other but are independent patterns.

### Q5: How does an API Gateway differ from a Service Mesh?
**Answer**: API Gateway handles north-south traffic (external to internal). Service Mesh handles east-west traffic (internal service-to-service) with features like mTLS, retries, and observability.

### Q6: Explain idempotency in event-driven systems.
**Answer**: An operation is idempotent if applying it multiple times produces the same result as applying it once. Use event IDs and processed-event tracking.

### Q7: What is the strangler fig pattern?
**Answer**: Incrementally replacing a monolithic system by routing specific functionality to new microservices while keeping the monolith for the rest.

## System Design Problem: Design an E-Commerce Checkout

### Requirements
- Handle 10K concurrent checkouts
- Reliable payment processing
- Inventory reservation
- Order confirmation notification

### Proposed Solution
- **API Gateway**: Single entry point
- **Order Service**: CQRS with event sourcing
- **Inventory Service**: Event-driven stock updates
- **Payment Service**: Saga with compensating transactions
- **Kafka**: Event broker between services
- **Redis**: Session and cart caching
