# Module 70: Enterprise Capstone Project - Edge Cases & Pitfalls

---

## Pitfall 1: Integration Paralysis

### ❌ Wrong
Attempting to build all four microservices (Identity, Trading, Market, Ledger), configure Kafka, write Kubernetes YAML, and set up Istio simultaneously before testing if a basic HTTP request works. This leads to an un-debuggable mess where nothing works and the root cause is hidden behind 7 layers of abstraction.

### ✅ Correct
Build iteratively. Start by building a single, monolithic Spring Boot app with H2. Verify the business logic. Then, split it into two microservices communicating via plain REST. Then, containerize them. Then, replace REST with Kafka. Then, deploy to Kubernetes. Validate the system end-to-end at every single step.

---

## Pitfall 2: Over-Engineering the Domain

### ❌ Wrong
Implementing Event Sourcing, CQRS, and Saga Orchestrators for the `Identity Context` simply to update a user's email address.

### ✅ Correct
Apply complex architectural patterns only where the business requirements demand them. The `Trading Context` handles high-frequency, financially critical transactions; it requires CQRS and Event Sourcing. The `Identity Context` handles low-frequency, standard CRUD operations; a simple REST API backed by a standard PostgreSQL table is the correct, pragmatic engineering choice.

---

## Pitfall 3: Failing the Chaos Test

### ❌ Wrong
Writing perfect "Happy Path" code. When the evaluator runs a Chaos script that artificially injects 5 seconds of latency into the Redis cache, the entire Trading Engine threads block, the system runs out of memory, and the platform crashes globally.

### ✅ Correct
Assume everything will fail. Wrap all cross-network calls (Redis, Postgres, external APIs) in Circuit Breakers (`Resilience4j`). Implement aggressive timeouts. Configure Kubernetes Readiness probes so traffic isn't routed to dead pods. The system must degrade gracefully (e.g., displaying "Market Data Unavailable" on the UI while still allowing users to view their past portfolio).