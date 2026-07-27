# Mock Interview: Architecture Patterns

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Staff Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a migration from monolith to microservices for a growing e-commerce platform.

---

## Transcript

**Interviewer**: "Our client is a mid-size e-commerce company with a monolithic application serving 5M MAU. They're experiencing scaling issues during flash sales. Design the migration to microservices. Where do you start?"

**Candidate**: "Let me clarify requirements first. What's the current tech stack? What specific scaling issues? Any timeline constraints?"

**Interviewer**: "Java/Spring monolith, PostgreSQL. Cart times out during flash sales. Payment processing has 5% failure rate under load. No strict timeline — but they'd like incremental value."

**Candidate**: "Great. I'd start with the Strangler Fig pattern — identify bounded contexts and extract one service at a time. The highest impact would be extracting the Cart service first since it's the most visible pain point."

**Interviewer**: "Walk me through the Cart extraction."

**Candidate**: "Step 1: Define the Cart service API interface. `GET /cart/{id}`, `POST /cart/{id}/items`, `DELETE /cart/{id}/items/{itemId}`. Step 2: Create a new microservice with its own Redis + DynamoDB. Step 3: Add a router/API gateway that routes `/cart/*` to the new service. Step 4: The monolith code for cart becomes a proxy to the new service. Step 5: Remove the old cart code from monolith."

**Interviewer**: "How do you handle data consistency during the transition?"

**Candidate**: "Dual-write phase: the new service writes to its own DB AND the monolith's DB during migration. A reconciliation job runs to identify and fix inconsistencies. Once validated, we stop dual-writes and remove the monolith's cart tables."

**Interviewer**: "How do you handle the transaction that spans cart and order creation?"

**Candidate**: "We don't try to maintain distributed transactions. Instead, we use the Saga pattern. When cart checkout is initiated, the Cart service publishes an event. Order service picks it up and creates the order. If order creation fails, it publishes a failure event, and Cart service rolls back the reservation. Compensation actions are idempotent."

**Interviewer**: "Good. How do you test this migration?"

**Candidate**: "Three testing phases: 1) Dark launch — run traffic to both old and new systems, compare responses. 2) Canary — route 5% of users to new system, monitor metrics. 3) Gradual rollout — increase to 25%, 50%, 100%. Each phase has automated rollback triggers."

---

## Key Takeaways

- **Strangler Fig**: Extract services incrementally, never big-bang
- **Saga pattern**: Orchestrate distributed transactions via events
- **Dual-writes**: Temporary write amplification during migration
- **Dark launches**: Validate correctness without user impact
- **Must discuss**: How to handle shared database schemas during migration
