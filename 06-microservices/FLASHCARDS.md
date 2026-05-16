# Microservices Flashcards

## Card 1: Microservices
- **Q:** What is a microservice?
- **A:** Small, independent, deployable service with its own database

---

## Card 2: Bounded Context
- **Q:** What is a bounded context in DDD?
- **A:** Clear boundary where a specific domain model applies

---

## Card 3: Database per Service
- **Q:** What does Database per Service mean?
- **A:** Each service owns its data and exposes it via API

---

## Card 4: CQRS
- **Q:** What is CQRS?
- **A:** Command Query Responsibility Segregation - separate read and write models

---

## Card 5: Saga Pattern
- **Q:** What is the Saga pattern?
- **A:** Distributed transactions via sequence of local transactions with compensation

---

## Card 6: Service Registry
- **Q:** What is a Service Registry?
- **A:** Central database of available service instances

---

## Card 7: Client-Side Discovery
- **Q:** What is client-side discovery?
- **A:** Client looks up services from registry and chooses one

---

## Card 8: Server-Side Discovery
- **Q:** What is server-side discovery?
- **A:** Load balancer or router looks up services on behalf of clients

---

## Card 9: API Gateway
- **Q:** What is an API Gateway?
- **A:** Single entry point for client requests, handles routing, auth, rate limiting

---

## Card 10: Circuit Breaker
- **Q:** What does circuit breaker do?
- **A:** Prevents cascading failures by failing fast when service is down

---

## Card 11: Fallback
- **Q:** What is a fallback in circuit breaker?
- **A:** Default response when circuit is open or operation fails

---

## Card 12: Rate Limiting
- **Q:** What is rate limiting?
- **A:** Controlling number of requests per client in a time period

---

## Card 13: Service Mesh
- **Q:** What is a service mesh?
- **A:** Infrastructure layer for service-to-service communication

---

## Card 14: Distributed Tracing
- **Q:** What is distributed tracing?
- **A:** Tracking requests across multiple services via correlation IDs

---

## Card 15: Event-Driven
- **Q:** What is event-driven communication?
- **A:** Services communicate via events/messages, not direct calls

---

## Card 16: Orchestration
- **Q:** What is orchestration in Saga?
- **A:** Central coordinator directs the flow of the transaction

---

## Card 17: Choreography
- **Q:** What is choreography in Saga?
- **A:** Services react to events without central coordinator

---

## Card 18: Eventual Consistency
- **Q:** What is eventual consistency?
- **A:** Data becomes consistent across services over time (not immediately)

---

## Card 19: Service Mesh Proxy
- **Q:** What is a sidecar proxy?
- **A:** Lightweight proxy that handles service communication (e.g., Envoy)

---

## Card 20: Centralized Configuration
- **Q:** What is centralized configuration?
- **A:** All services get configuration from a central config server

---

## Quick Reference

| Pattern | Description |
|---------|-------------|
| Service Decomposition | Split monolith into services |
| Database per Service | Each service owns its data |
| API Composition | Aggregate data from multiple services |
| CQRS | Separate read/write models |
| Saga | Distributed transactions with compensation |
| Service Discovery | Dynamic service location |
| API Gateway | Single entry point |
| Circuit Breaker | Fault tolerance |
| Service Mesh | Infrastructure for service communication |
| Distributed Tracing | Track requests across services |
| Centralized Config | Configuration from config server |
| Event Sourcing | Store events not current state |