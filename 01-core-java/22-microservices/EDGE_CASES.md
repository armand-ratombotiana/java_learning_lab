# Module 22: Microservices Concepts - Edge Cases & Pitfalls

---

## Pitfall 1: Shared Database

### ❌ Wrong
Creating multiple microservices but having them all connect to a single monolithic database. This creates tight coupling at the schema level and defeats the purpose of microservices.

### ✅ Correct
Apply the "Database per Service" pattern. If services need data from each other, they should request it via APIs or asynchronous messaging.

---

## Pitfall 2: Synchronous Cascading Failures

### ❌ Wrong
Having Service A call Service B synchronously (e.g., via REST), and Service B calls Service C. If Service C goes down, threads in Service A and B block, potentially taking down the entire system.

### ✅ Correct
Implement Circuit Breakers (e.g., Resilience4j) to fail fast, provide fallbacks, and prevent cascading failures. Where possible, favor asynchronous communication using event-driven architectures.

---

## Pitfall 3: Distributed Monolith

### ❌ Wrong
Splitting an application into microservices without defining proper bounded contexts. If every business feature requires updating 5 different microservices simultaneously, you have built a "Distributed Monolith" which brings all the deployment pain of microservices with none of the decoupling benefits.

### ✅ Correct
Use Domain-Driven Design (DDD) to define Bounded Contexts. Services should have high cohesion internally and loose coupling externally.