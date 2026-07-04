# Microservices Flashcards

## Q: What is a microservice?
**A:** An independently deployable service that owns its domain logic and data, communicating via lightweight protocols.

## Q: What is an API Gateway?
**A:** A single entry point that routes requests to appropriate services, handles auth, rate limiting, and request aggregation.

## Q: What is service discovery?
**A:** A mechanism where services register themselves and discover others via a service registry (Eureka, Consul).

## Q: What is the Circuit Breaker pattern?
**A:** A resilience pattern that detects failures and prevents cascading by failing fast when a service is unhealthy.

## Q: What is distributed tracing?
**A:** Tracking a request's path across multiple services using trace IDs and span IDs.

## Q: What is the Strangler Fig pattern?
**A:** Incrementally replacing monolith functionality with microservices by routing traffic piece by piece.

## Q: What is an idempotency key?
**A:** A unique identifier for requests that allows safe retry without duplicate side effects.

## Q: What is the bulkhead pattern?
**A:** Isolating resources (thread pools, connections) per service to prevent one service from exhausting shared resources.

## Q: What is the saga pattern?
**A:** A sequence of local transactions with compensating actions to maintain data consistency across services.

## Q: What is Conway's Law?
**A:** Organizations design systems that mirror their communication structure; microservices aligns teams to services.
