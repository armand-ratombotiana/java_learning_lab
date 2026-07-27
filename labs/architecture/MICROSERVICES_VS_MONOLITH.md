# Microservices vs Monolith — Interview Guide

> When to use each, trade-offs, migration strategies — a comprehensive guide for architecture interviews.

---

## Table of Contents

1. [The Fundamental Question](#1-the-fundamental-question)
2. [Monolith Architecture](#2-monolith-architecture)
3. [Microservices Architecture](#3-microservices-architecture)
4. [The Spectrum: Not Binary](#4-the-spectrum-not-binary)
5. [Decision Framework](#5-decision-framework)
6. [Migration Strategies](#6-migration-strategies)
7. [Common Interview Questions](#7-common-interview-questions)
8. [Trade-Off Analysis Template](#8-trade-off-analysis-template)
9. [Real-World Examples](#9-real-world-examples)

---

## 1. The Fundamental Question

> "Should we build a monolith or microservices?"

This is one of the most common architecture interview questions. The correct answer is **never an absolute** — it depends on context. Interviewers want to see that you understand the real trade-offs, not that you have a religious preference.

### The Honest Answer

Most systems should start as a **modular monolith** and decompose into microservices as:
- The team grows beyond 10-15 engineers
- The domain boundaries become clear (through experience, not upfront design)
- Independent deployment and scaling become bottlenecks
- Different parts of the system have different reliability requirements

---

## 2. Monolith Architecture

### What It Is

A single deployable unit containing all application logic, data access, and often the UI layer.

### When to Choose Monolith

| Factor | Monolith Advantage |
|--------|-------------------|
| **Team size** | < 10 engineers |
| **Product stage** | Early stage, uncertain requirements |
| **Complexity** | Low to moderate domain complexity |
| **Scale** | Low to moderate traffic |
| **Deployment** | Simple, single artifact |
| **Development speed** | Fast initial development |
| **Operational overhead** | Minimal |
| **Transactionality** | Strong consistency, ACID transactions |

### Trade-Offs

**Pros:**
- Simple deployment (one artifact)
- Simple debugging (one process, one codebase)
- Strong consistency via local ACID transactions
- No network latency between components
- Easy refactoring within the monolith
- Lower operational overhead (no service discovery, no circuit breakers, no distributed tracing)
- Easier testing (integration tests don't need service orchestration)

**Cons:**
- Everything scales together (cannot independently scale hot paths)
- Technology lock-in (all components use the same stack)
- Codebase complexity grows with team size
- Merge conflicts increase with team size
- Deployment risk: a bug in one component can bring down everything
- Onboarding friction: new engineers must understand the entire codebase
- Slower development as codebase grows
- Cannot isolate failures
- Long build and test times

### The Modular Monolith

A middle ground: single deployable unit but with **well-defined modules** with clear interfaces and boundaries.

```
┌─────────────────────────────────────────────┐
│           Monolith (Deployment Unit)         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐      │
│  │ Module A│  │ Module B│  │ Module C│      │
│  │ (Orders)│  │ (Payment│  │ (Shipping│     │
│  │         │  │  Module)│  │  Module) │      │
│  └────┬────┘  └────┬────┘  └────┬────┘      │
│       │            │            │             │
│  ┌────┴────────────┴────────────┴────┐        │
│  │        Shared Infrastructure       │        │
│  │  (DB, Message Queue, Cache, etc.) │        │
│  └───────────────────────────────────┘        │
└─────────────────────────────────────────────┘
```

**Benefits**: Clear boundaries without distributed system complexity. Easy to extract modules into services later.

---

## 3. Microservices Architecture

### What It Is

Multiple independently deployable services, each owning its own data and domain logic, communicating over the network.

### When to Choose Microservices

| Factor | Microservices Advantage |
|--------|------------------------|
| **Team size** | > 15 engineers, multiple teams |
| **Product stage** | Mature product, clear domain boundaries |
| **Complexity** | High domain complexity, multiple subdomains |
| **Scale** | High traffic, different scaling requirements |
| **Deployment** | Independent deployment per team |
| **Development speed** | Teams can ship independently |
| **Reliability** | Failure isolation, blast radius containment |
| **Technology diversity** | Right tool for each job |

### Trade-Offs

**Pros:**
- Independent deployability (each service deploys on its own cadence)
- Independent scalability (scale only what needs scaling)
- Technology diversity (best language/database per service)
- Team autonomy (teams own their services end-to-end)
- Failure isolation (one service failure doesn't cascade)
- Smaller codebases (easier to understand per service)
- Faster development per service (decreased cognitive load)

**Cons:**
- Network latency between services (every call crosses the network)
- Distributed system complexity (service discovery, circuit breakers, retries, timeouts)
- Data consistency challenges (eventual consistency, sagas, compensating transactions)
- Testing complexity (integration tests need multiple services running)
- Operational overhead (monitoring, logging, tracing, deployment pipelines)
- Debugging difficulty (a request spans multiple services)
- Team coordination overhead (API contracts, schema evolution, deprecation)
- Initial development slower (need to set up infrastructure)

---

## 4. The Spectrum: Not Binary

Architecture exists on a spectrum, not a binary choice.

```
Monolith ⇄ Modular Monolith ⇄ Macroservices ⇄ Microservices ⇄ Nanoservices
  ↑                ↑                 ↑                ↑               ↑
  One blob      Clear modules    Few, larger     Many small      Extremely fine-
                  inside one       services        services       grained services
                  deployment
```

### Macroservices

A practical middle ground: fewer services (5-15), each larger (owned by multiple teams or sub-teams).

**Best for**: Organizations transitioning from monolith to microservices. Reduces distributed system complexity while providing independent deployability.

### When to Decompose

| Threshold | Indicator | Action |
|-----------|-----------|--------|
| 10-15 engineers | Merge conflicts increase, deployment slows | Extract first service |
| 2-3 teams depend on same code | Coordination overhead increases | Extract the shared domain |
| Different scaling needs | Hot path needs 10x more resources | Extract hot path as service |
| Different release cadences | UI changes weekly, backend changes monthly | Separate frontend and backend |
| Different reliability requirements | One feature needs 99.99%, another 99.9% | Isolate critical path |

---

## 5. Decision Framework

### Questions to Ask

**Organization:**
1. How many engineers? How are they organized?
2. What is the team's experience with distributed systems?
3. Do teams have end-to-end ownership of features?
4. What is the organizational structure (Conway's Law)?

**Product:**
5. What is the product maturity stage?
6. How clear are the domain boundaries?
7. Is the product/team expected to grow significantly?
8. What is the feature velocity requirement?

**Technical:**
9. What are the scalability requirements?
10. What are the consistency requirements (financial vs social)?
11. What is the current deployment frequency?
12. What is the test and build speed?

**Operational:**
13. What is the team's DevOps maturity?
14. What monitoring and observability infrastructure exists?
15. What is the incident response process?
16. Is there budget for additional infrastructure?

### Decision Matrix

| Factor | Weight | Monolith Score | Microservices Score |
|--------|--------|---------------|-------------------|
| Team size < 10 | High | +2 | -1 |
| Early stage product | High | +2 | -1 |
| Low operational maturity | Medium | +2 | -2 |
| Need independent scaling | High | -2 | +2 |
| Clear domain boundaries | Medium | +1 | +2 |
| Strong consistency needed | Medium | +2 | -1 |
| Fast deployment required | High | +1 | +2 |
| Small team distributed | Low | -1 | +1 |

**Calculate**: Sum scores. Positive → lean monolith. Negative → lean microservices.

---

## 6. Migration Strategies

### Strangler Fig Pattern (Incremental Migration)

**Best for**: Existing monolith → microservices.

```
Phase 1: Identify bounded context (e.g., "Payments")
Phase 2: Build new payments service
Phase 3: Route payments traffic to new service (feature toggle)
Phase 4: Remove payments code from monolith
Phase 5: Repeat for next bounded context
```

### Parallel Run / Dark Launch

**Best for**: High-risk migrations where correctness is critical.

```
1. Run old and new systems in parallel
2. Compare outputs for consistency
3. When confident, route real traffic to new system
4. Decommission old system
```

### Branch by Abstraction

**Best for**: Migrating core functionality that has many callers.

```
1. Create abstraction layer over the existing implementation
2. Build new implementation behind the same abstraction
3. Switch the abstraction to new implementation
4. Remove old implementation and abstractio
```

### Database Migration

**The hardest part of migration.** Options:

1. **Shared database**: New service shares DB with monolith (temporary, coupling)
2. **Database view/materialization**: New service reads from monolith DB via views
3. **Event-driven synchronization**: New service publishes events, monolith subscribes
4. **Independent database**: New service has its own DB, data migrated asynchronously

**Recommendation**: Extract data ownership incrementally. Start with data owned by the new service (no existing consumers), then migrate existing data.

---

## 7. Common Interview Questions

### Question 1: "Would you use microservices for this system?"

**Good approach:**
- Ask clarifying questions (team size, scale, stage, complexity)
- Frame response around context, not absolutes
- Discuss the decision framework
- If recommending microservices, discuss what you'd do first (communication patterns, service boundaries, data ownership, error handling)
- If recommending monolith, discuss how you'd design for future extraction (modular monolith)

### Question 2: "How would you decompose a monolith?"

**Good approach:**
- Start with domain analysis (DDD, event storming, bounded contexts)
- Identify natural split points (domains that change independently)
- Plan strangler fig migration: one domain at a time
- Discuss data decomposition strategy (hardest part)
- Address the human side: team reorganization with service ownership
- Discuss integration strategy (API gateway, event bus, synchronous vs async)

### Question 3: "What are the biggest challenges with microservices?"

**Good approach:**
- Data consistency (sagas, compensating transactions, outbox pattern)
- Distributed complexity (service discovery, circuit breakers, retries, timeouts)
- Observability (distributed tracing, centralized logging, metrics aggregation)
- Testing (integration tests, contract tests, consumer-driven contracts)
- Operational overhead (deployment, monitoring, incident response)

### Question 4: "When would you NOT use microservices?"

**Good approach:**
- Small team (<10 engineers) — overhead outweighs benefits
- Early stage product — need speed and flexibility
- Strong consistency requirements — distributed transactions are hard
- Low operational maturity — need strong DevOps foundation
- Simple application — over-engineering

### Question 5: "How do services communicate?"

**Good approach:**
- Discuss synchronous (REST, gRPC) vs asynchronous (events, messaging)
- When to use each (synchronous for queries, async for commands/events)
- API versioning strategies (URL, header, semantic versioning)
- Schema evolution (Avro/Protobuf with schema registry)

---

## 8. Trade-Off Analysis Template

Use this template in interviews when discussing microservices vs monolith:

```markdown
## Decision: Monolith vs Microservices for [System Name]

### Context
- Team: [size, structure, experience]
- Product: [stage, complexity, growth trajectory]
- Scale: [current and projected traffic]
- Constraints: [timeline, budget, operational maturity]

### Considered Options

**Option A: Modular Monolith**
- Benefits: Faster to build, simple operations, strong consistency
- Costs: Will need to extract services later, everything scales together
- Risks: Codebase may become unwieldy at 15+ engineers

**Option B: Microservices**
- Benefits: Team autonomy, independent scaling, technology diversity
- Costs: Slower initial development, operational complexity
- Risks: Distributed system failures, data consistency challenges

### Recommendation
[Chosen approach with rationale]

### Key Decisions
1. Module/Service boundaries: [How identified]
2. Communication pattern: [Sync/Async/Mixed]
3. Data ownership: [Per service DB vs shared]
4. Migration strategy: [Strangler fig/parallel run/other]
```

---

## 9. Real-World Examples

### Example: Amazon

**Architecture evolution**: Monolith → Service-oriented → Microservices

- **2001**: Monolithic codebase (CEO mandate: all teams communicate via APIs)
- **2002**: API mandate forced service boundaries
- **2004-2010**: Gradual decomposition as teams grew
- **Today**: Thousands of services, each owned by a two-pizza team

**Key lesson**: API-first culture, not technology, enabled microservices adoption.

### Example: Netflix

**Architecture evolution**: Monolith (data center) → Microservices (AWS)

- **2007**: Monolith in data center, scaling challenges
- **2008**: Major database corruption, decided to migrate to cloud
- **2009-2012**: Migrated to AWS, decomposed into microservices
- **Today**: Hundreds of services, chaos engineering culture

**Key lesson**: Migration driven by scaling and reliability needs, not fashion.

### Example: Shopify

**Architecture evolution**: Monolith → Modular monolith → Decomposition

- **2010-2015**: Monolith scaled successfully to thousands of merchants
- **2016-2020**: Modular monolith with clear Ruby module boundaries
- **2021+**: Selective extraction of services for specific scaling needs

**Key lesson**: Shopify proved you can go far with a (very well-designed) monolith. Decompose only when specific bottlenecks demand it.

---

## Summary

| Factor | Choose Monolith | Choose Microservices |
|--------|----------------|-------------------|
| Team size | < 10 engineers | > 15 engineers |
| Product stage | Early / uncertain | Mature / well-understood |
| Domain complexity | Low to moderate | High, multiple subdomains |
| Scale | Low to moderate | High or varying per component |
| Consistency needs | Strong consistency | Eventual consistency acceptable |
| Operational maturity | Low | High |
| Deployment frequency | Daily or slower | Multiple times per day |
| Technology diversity | Single stack | Polyglot |

**The golden rule**: Design for the architecture you need today, structure for the architecture you'll need tomorrow. A well-designed modular monolith is the best starting point for most systems.

---

*This guide prepares you for the monolith vs microservices debate that appears in nearly every system design interview at the staff+ level.*
