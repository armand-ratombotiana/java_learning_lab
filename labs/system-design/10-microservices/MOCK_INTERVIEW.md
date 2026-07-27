# Mock Interview: Microservices

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Software Architect Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a microservices decomposition for an e-commerce platform.

---

## Transcript

**Interviewer**: "Our e-commerce monolith has 50 engineers working on it. Deployments take 2 weeks, and code conflicts are constant. Design the microservices decomposition."

**Candidate**: "I'd decompose by business capability. Each microservice owns a specific business domain and has its own data store. Services communicate via APIs or events."

**Interviewer**: "What services do you identify?"

**Candidate**: "Core services: 1) Product Catalog — manages product info, inventory, categories, 2) Cart — shopping cart per user, 3) Order — order processing state machine, 4) Payment — payment processing, 5) Shipping — fulfillment and tracking, 6) User — authentication and profiles, 7) Notification — email, SMS, push. Each owned by a 6-8 person team."

**Interviewer**: "How do services communicate?"

**Candidate**: "Two patterns: 1) Synchronous (REST/gRPC) for query operations and real-time needs. Example: Cart service calls Product service to get current price. 2) Asynchronous (events via Kafka) for state changes. Example: Order created → event published → Payment service picks it up → event published → Shipping picks it up."

**Interviewer**: "How do you handle shared data?"

**Candidate**: "Each service owns its data exclusively. If another service needs it, they go through the owning service's API. Example: Order service needs product name for order history. It doesn't access the product database directly. Instead, it stores denormalized product data at order time (product name, price snapshot). This prevents coupling."

**Interviewer**: "How do you handle transactions that span services?"

**Candidate**: "No distributed transactions. Use the Saga pattern with choreography. Example checkout saga: 1) Order service creates pending order, publishes OrderCreated event, 2) Cart service clears the cart (listens to event), 3) Payment service processes payment, publishes PaymentCompleted event, 4) Shipping service creates shipment. If payment fails, the saga publishes PaymentFailed, and Order service cancels the order."

**Interviewer**: "How do you test across microservices?"

**Candidate**: "Testing strategy: 1) Unit tests — each service tested in isolation (mocks for dependencies), 2) Contract tests — Pact-style consumer-driven contract tests ensure API compatibility, 3) Integration tests — test real service interactions, 4) End-to-end tests — critical user journeys tested in a staging environment. Each service has CI/CD pipeline that runs unit + contract tests. E2E tests run nightly."

**Interviewer**: "How do you handle the migration from monolith?"

**Candidate**: "Strangler Fig pattern: 1) Extract one service at a time, 2) Create a virtual IP/router that forwards requests to the new service or the monolith, 3) Dual-write during migration (write to both old and new), 4) Compare results from old and new (tolerance checks), 5) Switch traffic to new service, 6) Remove old code. Each extraction takes 4-8 weeks."

---

## Key Takeaways

- **Decompose by business capability**: Each service = one business domain
- **Synchronous for queries, async for events**: Choose the right communication pattern
- **Data ownership**: Each service owns its data, accessible only through APIs
- **Saga pattern**: Distributed transactions via choreographed events
- **Contract testing**: Consumer-driven contracts ensure compatibility
- **Strangler Fig**: Incremental migration, one service at a time
