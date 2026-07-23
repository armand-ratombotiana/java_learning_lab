# Backend Academy — Company Behavioral Guide

<div align="center">

![Behavioral](https://img.shields.io/badge/Behavioral_Guide-4285F4?style=for-the-badge)
![STAR](https://img.shields.io/badge/STAR_Method-6DB33F?style=for-the-badge)
![Backend Focus](https://img.shields.io/badge/Backend_Focus-FF6F00?style=for-the-badge)

**Behavioral prep for backend engineers — company-specific questions, STAR answers, leadership principles, system design trade-offs, and questions to ask**

</div>

---

## Table of Contents

1. [Backend-Specific Behavioral Questions by Company](#1-backend-specific-behavioral-questions-by-company)
2. [STAR Answer Frameworks for Backend Engineers](#2-star-answer-frameworks-for-backend-engineers)
3. [System Design Trade-Offs as Behavioral Answers](#3-system-design-trade-offs-as-behavioral-answers)
4. [Leadership Principles Mapped to Backend Experience](#4-leadership-principles-mapped-to-backend-experience)
5. [Questions to Ask the Interviewer](#5-questions-to-ask-the-interviewer)
6. [How to Discuss Failures (Outages, Regressions, Security Incidents)](#6-how-to-discuss-failures)

---

## 1. Backend-Specific Behavioral Questions by Company

### Google

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you designed a scalable API" | Versioning, rate limiting, pagination, backward compatibility | Lab 02, Lab 17 |
| "Describe a distributed systems problem you solved" | Consistency models, partition tolerance, replication | Lab 16, Lab 23 |
| "How did you handle a significant increase in traffic?" | Auto-scaling, caching, connection pooling, circuit breakers | Lab 13, Lab 24 |
| "Tell me about a time you made a technical decision with incomplete info" | Database choice, framework selection, architecture trade-offs | All labs |
| "How do you ensure reliability in a distributed system?" | Circuit breakers, bulkheads, health checks, graceful degradation | Lab 16 |

### Microsoft

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you migrated a system to the cloud" | Azure migration, lift-and-shift vs re-architect | Lab 16 (Cloud patterns) |
| "Describe a project where you integrated with legacy systems" | Anti-corruption layer, strangler fig, event-driven integration | Lab 07, Lab 23 |
| "How do you handle cross-team API contracts?" | OpenAPI specs, consumer-driven contracts (CDC), Pact testing | Lab 11, Lab 12 |
| "Tell me about a production incident you resolved" | Root cause analysis, blameless postmortem, runbook creation | Lab 11 |

### Amazon

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you dove deep into a technical problem" | Performance profiling, thread dump analysis, SQL query optimization | Lab 24 |
| "Describe the most architecturally significant decision you made" | Monolith vs microservices, database choice, caching strategy | All labs |
| "How did you handle a disagree and commit situation?" | Architecture disagreement -> data-driven A/B test -> commit | Lab 11 |
| "Tell me about a time you simplified a complex system" | Service consolidation, removing unnecessary abstractions | Lab 16, Lab 26 |

### Meta

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you shipped a high-impact backend feature quickly" | Iterative development, feature flags, phased rollout | Lab 01, Lab 16 |
| "How do you handle conflicting priorities in a fast-paced environment?" | Technical debt triage, API versioning strategy, incremental improvements | Lab 17 |
| "Describe a time you had to make a trade-off between speed and quality" | When to write tests, when to refactor, when to ship | Lab 11 |
| "Tell me about a backend system you're proud of building" | Detailed system design, performance metrics, business impact | Any lab |

### Apple

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "How do you design a system with privacy as a core requirement?" | Data minimization, encryption at rest/transit, differential privacy | Lab 20 |
| "Tell me about a time you optimized backend performance" | Profiling with JFR, GC tuning, connection pool sizing | Lab 24 |
| "Describe how you ensure data integrity across services" | Distributed transactions (SAGA), idempotency keys, outbox pattern | Lab 05, Lab 07 |
| "How do you handle backward compatibility in your APIs?" | Versioning strategies, deprecation policies, graceful migration | Lab 17 |

### Netflix

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you implemented a chaos engineering practice" | Fault injection, blast radius, resilience testing | Lab 11, Lab 16 |
| "Describe how you would design a resilient microservice" | Circuit breakers, bulkheads, retries with jitter, graceful degradation | Lab 16 |
| "Tell me about a time you had to be candid about a technical problem" | Admitting design flaws, proposing migration, convincing stakeholders | - |
| "How do you build for high availability?" | Redundancy, failover, multi-region deployment, health checks | Lab 16, Lab 24 |

### Uber

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you built a real-time data pipeline" | Kafka streams, Flink, CQRS, event-driven architecture | Lab 07, Lab 23 |
| "How do you handle geospatial data at scale?" | Geohashing, quadtrees, H3 hexagons, spatial indexing | Lab 04, Lab 21 |
| "Describe a time you operated a high-scale system" | Kafka lag management, DB connection pool tuning, caching strategy | Lab 13, Lab 24 |
| "Tell me about a time you made a data-driven product decision" | A/B testing framework, metrics tracking, statistical significance | Lab 11 |

### Stripe

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you designed an idempotent API" | Idempotency keys, at-least-once semantics, dedup | Lab 02, Lab 05 |
| "How do you ensure financial correctness in your systems?" | Double-entry ledger, transactional integrity, reconciliation | Lab 05 |
| "Describe a time you dealt with a complex API integration" | Third-party API design, error handling, retry patterns | Lab 07 |
| "Tell me about a time you improved developer experience" | API design, documentation, SDK generation, internal tools | Lab 12 |

### DoorDash

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you optimized a real-time logistics system" | Route optimization, dispatch algorithms, real-time tracking | Lab 19, Lab 24 |
| "How do you handle multi-sided marketplace dynamics?" | Supply-demand matching, surge pricing, driver/rider experience | Lab 23 |
| "Describe a time you scaled a database to handle growth" | Sharding, read replicas, connection pooling, query optimization | Lab 04, Lab 24 |
| "Tell me about a time you made a decision that improved latency" | Caching strategy, CDN, query optimization, async processing | Lab 13 |

### Slack

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you built a real-time collaboration feature" | WebSocket scaling, presence, typing indicators, message ordering | Lab 15, Lab 19 |
| "How do you design systems for workspace-level isolation?" | Multi-tenancy strategies, data isolation, resource quotas | Lab 21 |
| "Describe a time you improved system reliability" | Health checks, graceful shutdown, retry policies | Lab 11, Lab 16 |

### Confluent

| Question | Backend Angle | Lab Reference |
|----------|---------------|---------------|
| "Tell me about a time you worked with event streaming" | Kafka consumer/producer, partition strategy, exactly-once | Lab 07 |
| "How do you ensure data durability in distributed systems?" | Replication, ISR, ACK configuration, compaction | Lab 07, Lab 05 |

---

## 2. STAR Answer Frameworks for Backend Engineers

### STAR Template Structure

```
S -> Situation  (Context — what system, team, scale)
T -> Task       (What needed to be done — specific backend goal)
A -> Action     (What YOU did — architecture decisions, code, technologies)
R -> Result     (Measurable outcome — latency, throughput, reliability)
```

### Template 1: "Design a Scalable API"

```
S -> I was on the orders team at [Company]. We had a monolithic REST API
     that handled 5K RPM, but the business was growing 3x year-over-year.
     The API had no versioning, no caching, and degraded to 5-second
     p95 latency under peak load.

T -> Design and implement a v2 API that could handle 50K RPM, maintain
     <200ms p99 latency, and support backward compatibility.

A -> I led the design:
     - URI-based versioning (`/api/v2/orders`) for clear separation (Lab 17)
     - Cursor-based pagination to handle infinite scroll while avoiding
       offset drift in DynamoDB (Lab 02)
     - Redis cache-aside with TTL-based invalidation for order status
       lookups — achieved 85% cache hit rate (Lab 13)
     - Idempotency keys on POST /orders to prevent duplicate charges (Lab 05)
     - Gradual migration: v2 accepted both old and new formats via
       content negotiation; shadow traffic for 2 weeks before cutover (Lab 11)
     - Rate limiting with token bucket per API key (Lab 06)

R -> V2 API handles 200K+ RPM (4x target), p99 latency of 120ms, 0
     incidents during migration. Cutover completed in 2 weeks with zero
     downtime. Onboarded 12 new integration partners the next quarter.
```

### Template 2: "Handle a Production Outage"

```
S -> At [Company], our payment processing service returned 5xx errors at
     2PM on Black Friday. Error rate jumped from 0.01% to 15% in 5
     minutes. We were processing $50K/min in orders.

T -> Restore service immediately and identify root cause to prevent
     recurrence.

A -> As the on-call backend engineer:
     - Checked Grafana dashboards: database connection pool was exhausted
       (100 connections in use, 500+ waiting)
     - Killed long-running queries (new reporting query full-table scanning
       the orders table, 50M rows)
     - Scaled connection pool from 100 to 300, added PgBouncer (Lab 05, 24)
     - Redirected read traffic to read replicas for mitigation
     - Root cause: added SQL review process (Lab 04, 11), query timeout
       (`spring.datasource.hikari.connection-timeout: 5000`), @Query timeouts
     - Wrote blameless postmortem, added alert for pool saturation at 70%

R -> Service restored in 22 minutes. Zero financial loss (orders queued
     via Kafka, processed post-recovery). Regression prevented via
     automated SQL analysis in CI/CD (Lab 11).
```

### Template 3: "Database Migration / Schema Change"

```
S -> MySQL database for user service had 50M rows. Needed to add a column
     and backfill from external service — zero-downtime required.

T -> Add column without locking the table or causing downtime.

A -> I proposed and implemented:
     - pt-online-schema-change (Percona Toolkit) — triggers on shadow
       table, then swap (zero blocking writes)
     - Spring Batch backfill: 10K users per chunk, restartable via job
       repository (Lab 18)
     - Feature flag to gate new column usage: 1% -> 10% -> 50% -> 100%
       rollout (Lab 16)
     - Post-rollout: removed trigger and shadow table

R -> Zero downtime migration of 50M rows. New column used by 3 services
     within 1 week. Feature flags allowed instant rollback (none needed).
```

### Template 4: "Microservices Decomposition"

```
S -> 3-year-old Spring Boot monolith, 20 engineers, one codebase. Deploys
     took 4 hours and broke 3x/week. Banking logic tightly coupled with
     notification, reporting, and user management.

T -> Decompose into microservices without halting feature development.

A -> Strangler Fig pattern (Lab 16):
     - Identified bounded contexts: Orders, Payments, Users, Notifications
     - Spring Cloud Gateway routing layer — intercept HTTP, route to new
       services (Lab 16)
     - Extracted Notifications first (lowest risk): new Spring Boot service
       consuming Kafka events (Lab 07) — old code writes to outbox_events
       table, Debezium CDC captures to Kafka
     - Each extraction: copy data, validate, dual-write 2 weeks, cut over
     - Eureka for service discovery, Config Server for centralized config

R -> Fully decomposed into 6 services in 9 months. Deploy time: 4hrs -> 15
     min. Breaking changes reduced by 90%. Team velocity increased 3x.
```

---

## 3. System Design Trade-Offs as Behavioral Answers

### Monolith vs Microservices

**Behavioral question:** "Tell me about a time you had to choose an architecture."

**Backend framing:**
```
"I chose a modular monolith over microservices for a SaaS product with
3 engineers. Key trade-offs:
- Monolith: faster dev speed, simpler deployment, no network latency
- Microservices: would add 10x complexity for benefits we didn't need yet

The modular monolith used package-level boundaries and Spring Boot
profiles to separate domains. At 10 engineers, we extracted the billing
service first using Strangler Fig (Lab 16, Lab 23).
```

### SQL vs NoSQL

**Behavioral question:** "Tell me about a time you chose a database technology."

**Backend framing:**
```
"For an e-commerce order service, I chose PostgreSQL over DynamoDB:
- Needed ACID transactions for order + inventory + payment (Lab 05)
- Complex JOINs for reporting (Lab 04)
- Predictable traffic patterns (no need for DynamoDB's unlimited scaling)

DynamoDB would have required denormalization and application-level
consistency — 3x development time for no benefit at our scale.
```

### Synchronous vs Async Communication

**Behavioral question:** "Tell me about a time you decoupled services."

**Backend framing:**
```
"I introduced async messaging between order and notification services:
- Previously: order service called notification service REST API — if
  notification was down, order failed (tight coupling, Lab 02)
- Changed to: order service publishes OrderPlaced event to Kafka,
  notification service consumes independently (loose coupling, Lab 07)
- Added dead-letter queue for failed notifications, retry policy
- Result: order success rate went from 98% to 99.99%
```

### Cache or Not to Cache

**Behavioral question:** "Tell me about a time caching was critical."

**Backend framing:**
```
"Our product catalog API had 500ms p95 latency — users complained.
- Added Redis cache-aside (Lab 13): cache on read, invalidate on write
- Cache hit rate: 92%, p95 latency dropped to 15ms
- Trade-off: stale data possible for up to 5 minutes (TTL)
- Solution: for pricing (needs freshness), we bypassed cache; for product
  descriptions (rarely changes), cache was fine
```

---

## 4. Leadership Principles Mapped to Backend Experience

### Google: Googleyness

| Principle | Backend Manifestation |
|-----------|----------------------|
| **Intellectual humility** | "My initial architecture was wrong — I discovered through load testing that the database choice was suboptimal and we migrated to a sharded solution" |
| **Ambiguity** | "I had to build the notification system before the requirements were finalized — I built a configurable pipeline that could adapt to changing event types" |
| **Collaboration** | "I worked with 4 teams on API contracts — created OpenAPI specs that all teams agreed to, used consumer-driven contract tests" |

### Amazon: Leadership Principles

| Principle | Backend STAR Hook |
|-----------|-------------------|
| **Customer Obsession** | "I noticed our API p99 latency was 2s for international users. Added CloudFront CDN for API responses (Lab 13), reducing to 200ms." |
| **Dive Deep** | "Investigated intermittent 5xx errors — discovered thread pool exhaustion in Tomcat. Analyzed thread dumps, tuned `server.tomcat.threads.max`, added metrics." |
| **Disagree and Commit** | "Argued against using MongoDB for our billing system (needed ACID). After presenting data, team agreed to PostgreSQL. Once decided, I committed fully." |
| **Bias for Action** | "During a critical outage, I manually failed over to the read replica while the team investigated root cause — restored service in 5 minutes." |
| **Deliver Results** | "Reduced infrastructure costs by 40% by migrating from EC2 to ECS Fargate, right-sizing containers, and implementing auto-scaling (Lab 25)." |

### Meta: Meta Values

| Value | Backend Story |
|-------|---------------|
| **Move Fast** | "Shipped a new API in 2 weeks by using Spring Boot's auto-configuration and existing service templates — ran A/B tests alongside v1 for safety." |
| **Focus on Impact** | "Identified that the search endpoint consumed 60% of DB resources. Optimized the query with a composite index, added Redis cache (Lab 13) — reduced DB CPU from 80% to 30%." |
| **Be Open** | "When I broke the CI pipeline with a migration script, I immediately posted in the team channel with my mistake, rollback, and fix. Created a runbook for future migrations." |

### Netflix: Freedom & Responsibility

| Value | Backend Story |
|-------|---------------|
| **Judgment** | "Chose to build a custom load testing tool instead of buying LoadRunner — better integration with our Spring Boot stack, 80% cost savings." |
| **Communication** | "When I identified a design flaw in our event schema (missing partition key), I created a RFC document, got buy-in from 3 teams, and migrated over 2 weeks with zero data loss." |
| **Curiosity** | "Investigated why our Kafka consumers had increasing lag — discovered a rebalance storm. Fixed by increasing partitions, pre-allocating consumer group members." |

---

## 5. Questions to Ask the Interviewer

### Backend Architecture Questions

| Question | What It Reveals |
|----------|-----------------|
| "How does your team handle the trade-off between shipping quickly and maintaining architectural quality?" | Team's tech debt culture |
| "What's your current approach to API versioning and backward compatibility?" | API maturity |
| "How do you handle database migrations with zero downtime?" | DevOps maturity |
| "What's your incident response process? How do you conduct postmortems?" | Reliability culture |
| "How are microservices boundaries determined — by DDD bounded contexts or organizational structure?" | Conway's Law awareness |
| "What's your strategy for handling eventual consistency in user-facing features?" | Distributed systems maturity |

### Technology & Tooling Questions

| Question | What It Reveals |
|----------|-----------------|
| "Are you using Spring Boot? What version and what's your upgrade cadence?" | Tech stack modernity |
| "How do you handle service discovery and configuration in production?" | Infrastructure maturity |
| "What's your observability stack — logging, metrics, tracing?" | Ops maturity |
| "Do you use Kafka? How do you handle schema evolution?" | Event-driven maturity |
| "What's your testing strategy for backend services — unit, integration, contract, end-to-end?" | Quality culture |

### Career & Culture Questions

| Question | What It Reveals |
|----------|-----------------|
| "What's the most technically challenging backend problem the team has solved recently?" | Technical depth |
| "How do you approach on-call rotation and incident management?" | Team burnout awareness |
| "What does career growth look like for a backend engineer on this team?" | Promotion paths |
| "How do you balance feature work vs infrastructure improvements vs tech debt?" | Engineering culture |
| "What's the review process for architecture decisions?" | Rigor vs bureaucracy |

---

## 6. How to Discuss Failures

### Framework for Discussing Backend Failures

```markdown
1. NAME the failure type explicitly
2. DIAGNOSE the root cause (be specific, technical)
3. TAKE OWNERSHIP (use "I" not "we")
4. DESCRIBE the fix (architecture change, code change, process change)
5. SHARE the learning (system improvement, personal growth)
```

### Example 1: Performance Regression

```markdown
Q: "Tell me about a time a backend change caused a performance regression."

A: "I introduced an N+1 query problem that degraded API latency.
   - Situation: Added a new endpoint returning order history with line items.
   - My mistake: Used `@OneToMany(fetch = FetchType.EAGER)` on a large
     dataset without profiling (Lab 04, Lab 24).
   - Impact: p95 latency jumped from 50ms to 3s. High CPU on DB.
   - Diagnosis: Used Spring Boot actuator metrics + Hibernate stats logging
     to identify 10,000 SQL queries for 100 orders.
   - Fix: Changed to `@EntityGraph` with a JOIN FETCH query — 1 SQL query.
     Added `spring.jpa.properties.hibernate.generate_statistics=true`
     to CI/CD to catch future N+1 issues.
   - Result: p95 back to 50ms. Added JMeter performance tests in CI/CD
     (Lab 11) to prevent regression.
   - What I learned: Never trust `FetchType.EAGER` by default. Always
     profile before shipping."
```

### Example 2: Outage

```markdown
Q: "Tell me about a time you caused or contributed to an outage."

A: "I mistakenly enabled a database migration without a proper rollback plan.
   - Situation: Rolling schema change for the users table (add column).
   - My mistake: Ran `ALTER TABLE` directly (no pt-online-schema-change)
     which locked the table for 30 seconds during peak traffic.
   - Impact: 30 seconds of read/write downtime, 20K failed requests.
   - Immediate fix: Killed the ALTER TABLE query, traffic resumed.
   - Root cause: No runbook for safe schema migrations, no review process.
   - Long-term fix:
     a) Mandated pt-online-schema-change for all prod migrations (Lab 04)
     b) Created a database change review checklist in our PR template
     c) Added migration dry-run in staging environment
     d) Wrote a blameless postmortem shared company-wide
   - Result: 0 schema-related incidents in 18 months since.
   - What I learned: Any change to production databases requires a
     rollback-first mindset and the right tooling."
```

### Example 3: Security Incident

```markdown
Q: "Tell me about a time you found a security vulnerability in your code."

A: "I discovered that our API was leaking internal infrastructure details.
   - Situation: Our error handling returned full stack traces in API
     responses, exposing internal package names and framework versions.
   - discovery: A penetration test revealed `/error` endpoints with
     Spring Boot's default Whitelabel Error Page enabled.
   - Fix: Configured `server.error.include-stacktrace=never` in prod,
     created a custom `@ControllerAdvice` error handler returning
     consistent JSON errors (Lab 06, Lab 02).
   - Process fix: Added security review checklist for all new endpoints,
     OWASP dependency check in CI/CD.
   - Impact: Eliminated information disclosure vector, improved API
     consistency, passed security audit on next review."
```

### Safe Failure Discussion Checklist

- [ ] **Own the failure** — no blaming tools, teammates, or external factors
- [ ] **Show technical depth** — explain the exact root cause (be specific about queries, configs, or code)
- [ ] **Emphasize learning** — what specific systems/processes changed as a result
- [ ] **Quantify impact** — how much downtime, $ loss, user impact, then improvement
- [ ] **Demonstrate systemic fix** — not just patching the symptom but changing the process/tooling
- [ ] **Stay blameless** — focus on system failure, not individual blame

### Phrases to Use vs Avoid

| Use | Avoid |
|-----|-------|
| "I introduced a bug when..." | "The code was wrong" (passive) |
| "The system failed because of [specific technical reason]" | "The system failed" (vague) |
| "I learned to always [process change]" | "It was a learning experience" (vague) |
| "Latency increased from 50ms to 3s (60x)" | "It was bad" (unquantified) |
| "I created a runbook for future migrations" | "We should be more careful" (no action) |

---

## Quick Reference: Backend Behavioral Prep Per Company

| Company | Key Themes | Best STAR Story Types |
|---------|------------|----------------------|
| Google | Distributed systems, data-driven, collaboration | Design scalable API, handle traffic spike |
| Microsoft | Cloud migration, enterprise patterns, integration | Cloud migration (Azure), legacy integration |
| Amazon | Customer obsession, dive deep, deliver results | Outage resolution, cost optimization, performance fix |
| Meta | Move fast, impact-driven, be open | Fast shipping with safety, A/B testing story |
| Apple | Privacy-first, craftsmanship, quality | Security/privacy design, database migration |
| Netflix | Candor, resilience, high-performance | Circuit breaker implementation, chaos engineering |
| Uber | Real-time systems, marketplace, data-driven | Real-time pipeline, data-driven decision |
| Stripe | Idempotency, correctness, developer experience | Idempotent API design, financial correctness |
| DoorDash | Logistics, marketplace, latency optimization | Logistics optimization, database scaling |
| Slack | Real-time collaboration, multi-tenancy | WebSocket scaling, multi-tenancy design |
| Confluent | Event streaming, distributed systems | Exactly-once semantic, Kafka partition strategy |

---

<div align="center">

**Behavioral questions are not about the right answer — they're about how you think, communicate, and grow.**

</div>
