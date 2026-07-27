# Company Behavioral Guide — System Design Interviews

> Behavioral prep for system design interviews using STAR framework.
> Covers scaling decisions, failure handling, trade-off analysis, and company-specific leadership principles.

---

## Table of Contents

1. [The STAR Framework for SD](#1-the-star-framework-for-sd)
2. [Tell Me About a System You Designed](#2-tell-me-about-a-system-you-designed)
3. [Handling Conflicting Requirements](#3-handling-conflicting-requirements)
4. [Company-Specific Leadership Principles](#4-company-specific-leadership-principles)
5. [Common Behavioral Questions with Answers](#5-common-behavioral-questions-with-answers)

---

## 1. The STAR Framework for SD

STAR is the standard behavioral interview format. For system design, adapt it to describe technical decisions and their outcomes.

### STAR Breakdown

| Element | For System Design | Example Language |
|---------|------------------|------------------|
| **S**ituation | What was the context? System scale, team size, constraints | "We were building a payment processing system processing $10M/day with a team of 5 engineers" |
| **T**ask | What needed to be designed or solved? | "We needed a fraud detection system with <500ms latency at 99.9% accuracy" |
| **A**ction | What did you propose and why? How did you evaluate alternatives? | "I proposed a two-stage pipeline: rule-based filters for 80% of cases, ML model for remaining 20%. I chose Gradient Boost over Neural Nets for explainability" |
| **R**esult | What was the outcome? Metrics, learnings, impact | "Fraud capture improved from 75% to 94%, false positives dropped to 0.8%, system handled $50M/day after scaling" |

### SD-STAR Integration Template

```
S: "At [Company], our [system] was processing [X] requests/day with [constraints]. 
    The system was experiencing [problem] leading to [impact]."

T: "I was responsible for designing the [solution]. Key requirements were 
    [R1], [R2], [R3]. The trade-off was between [Option A] and [Option B]."

A: "I evaluated [alternatives]. I chose [design] because [reasoning]. 
    Key design decisions:
    1. [Decision 1] — [rationale]
    2. [Decision 2] — [rationale]
    3. [Decision 3] — [rationale]
    
    I led [implementation details: team coordination, timeline, rollout]."

R: "The system achieved [metrics]. 
    Business impact: [revenue saved, latency reduced, reliability improved].
    Lessons learned: [what would you do differently]."
```

---

## 2. Tell Me About a System You Designed

This is the most common behavioral question in system design interviews. Use this framework:

### Framework: 10-Minute Answer Structure

**Phase 1: Context (2 min)**
- What was the system? (payment processing, recommendation engine, data pipeline)
- Scale: users, QPS, data volume
- Team size and your role (lead, contributor, reviewer)

**Phase 2: Requirements & Constraints (1 min)**
- Functional requirements (what it needed to do)
- Non-functional requirements (latency, availability, consistency)
- Business constraints (timeline, budget, team expertise)

**Phase 3: Key Design Decisions (3 min)**
- Architecture choice (monolith vs microservices)
- Database selection (why NoSQL vs SQL — be specific)
- Caching strategy (what, where, why)
- Message queue usage (sync vs async, protocol choice)
- 2-3 specific trade-offs you made

**Phase 4: Challenges & Failures (2 min)**
- Biggest technical challenge and how you solved it
- A decision that was wrong and how you fixed it
- Production incident and what you learned

**Phase 5: Results & Impact (2 min)**
- Measurable outcomes (latency p99, availability %, revenue impact)
- System evolution (how it grew from v1 to v2+)
- What you'd do differently today

### Example Answer (Designing a Rate Limiter)

```
S: "At my previous company, our public API was being abused by a few customers, 
    causing degradation for everyone. We processed 10M API calls/day with spikes 
    to 5x normal during product launches."

T: "I was tasked with designing a distributed rate limiter that could:
    - Handle 50K QPS peak
    - Support multiple rate limit strategies (per-user, per-endpoint, per-second)
    - Operate across 3 data centers
    - Add <1ms p99 latency overhead"

A: "I evaluated:
    1) In-memory token bucket — fast but not distributed
    2) Redis-based sliding window counter — distributed but added network hop
    3) Local + Redis hybrid — best balance
    
    I chose option 3: local token buckets for 95% of requests (with periodic sync),
    Redis for coordinating burst detection. Key decisions:
    - Token bucket over sliding window for simpler memory management
    - Async Redis writes to minimize latency impact
    - Configurable rate limit policies per API key stored in ZooKeeper"

R: "The system handled 50K QPS with 0.8ms p99 overhead. 
    Abuse-related incidents dropped by 98%. 
    The design was later adopted by two other teams at the company.
    
    What I'd do differently: I'd add a circuit breaker for the Redis dependency 
    to prevent cascading failures during Redis cluster issues."
```

---

## 3. Handling Conflicting Requirements

### The Framework

**Step 1: Acknowledge the conflict**
> "I recognize there's a real tension between [requirement A] and [requirement B]."

**Step 2: Frame as trade-offs**
> "This is a classic trade-off between [dimension 1] and [dimension 2]."

**Step 3: Propose evaluation criteria**
> "To make the decision, I'd consider: [criteria 1], [criteria 2], [criteria 3]."

**Step 4: Present options with pros/cons**

| Option | Pros | Cons |
|--------|------|------|
| Option A | Pro 1, Pro 2 | Con 1, Con 2 |
| Option B | Pro 1, Pro 2 | Con 1, Con 2 |

**Step 5: Make a recommendation**
> "I recommend [Option X] because [reasoning tied to business priorities]."

### Common Conflict Scenarios

**Scenario 1: Speed vs Quality**
> "We need to ship this in 2 weeks but it won't be production-ready."
- Frame: "This is a classic build-vs-buy, MVP-vs-perfect decision"
- Approach: Ship a well-constrained MVP with feature flags, iterate after launch
- Example: "Ship the rate limiter for just the top 10 API keys first, learn, then expand"

**Scenario 2: Cost vs Performance**
> "The ideal solution costs $100K/month in AWS resources."
- Frame: "Cost optimization vs user experience"
- Approach: Right-size, use reserved instances, tiered caching (hot/warm/cold)
- Example: "Use S3 Standard for hot data, S3 Glacier for 90-day-old data"

**Scenario 3: Consistency vs Availability**
> "We need strong consistency but this will reduce availability during partitions."
- Frame: "CAP theorem trade-off"
- Approach: Decide based on business impact of inconsistency vs unavailability
- Example: "Payments need strong consistency; user profiles can be eventually consistent"

**Scenario 4: Features vs Technical Debt**
> "Product wants 10 new features but we need to refactor the database."
- Frame: "Short-term velocity vs long-term sustainability"
- Approach: Quantify the debt cost, propose incremental refactoring alongside features
- Example: "We'll refactor the payment schema this sprint while building the new invoice feature"

---

## 4. Company-Specific Leadership Principles

### Amazon

| LP | How to Demonstrate in SD |
|----|-------------------------|
| Customer Obsession | Start design by defining customer needs, include customer feedback loop |
| Ownership | Take responsibility for the full lifecycle, including failures |
| Invent and Simplify | Propose novel solutions that reduce complexity |
| Are Right, A Lot | Use data to back up design decisions |
| Learn and Be Curious | Reference recent tech you've learned and applied |
| Hire and Develop the Best | Mention mentoring junior engineers on design |
| Insist on the Highest Standards | Set SLOs, monitor them, don't accept regressions |
| Think Big | Design for 10x-100x growth, not just current needs |
| Bias for Action | Make decisions with 70% of information |
| Frugality | Cost-aware design, right-size resources |
| Dive Deep | Know the details of your system at every layer |
| Have Backbone; Disagree and Commit | Push back on wrong requirements, commit after decision |
| Deliver Results | Focus on measurable outcomes, ship milestones |

**Amazon SD Behavioral Question Examples:**
- "Tell me about a time you designed a system and it failed in production."
- "Describe a situation where you had to choose between a quick solution and a correct solution."
- "How have you ensured your system design was cost-effective?"

### Google

| Attribute | How to Demonstrate |
|-----------|-------------------|
| Googleyness | Collaboration, humility, curiosity |
| Problem-solving | Structured approach to ambiguous problems |
| Cognitive ability | Learn on the fly, adapt to new information |
| Leadership | Without authority — influence through technical insight |

**Google SD Behavioral Question Examples:**
- "Tell me about a system that didn't scale. How did you fix it?"
- "Describe a time you had to persuade your team to use a different technology."
- "How have you handled a situation where requirements changed mid-design?"

### Meta

| Value | How to Demonstrate |
|-------|-------------------|
| Move Fast | Iterative design, practical trade-offs |
| Focus on Impact | Design decisions tied to business metrics |
| Be Open | Seek feedback, collaborate during design |
| Build Social Value | Consider broader impact of technology choices |

**Meta SD Behavioral Question Examples:**
- "Tell me about a time you had to make a design decision with incomplete data."
- "How do you decide when to refactor vs when to build new features?"
- "Describe a time you disagreed with a product requirement and how you handled it."

### Netflix

| Value | How to Demonstrate |
|-------|-------------------|
| Freedom & Responsibility | You own your design decisions and their consequences |
| Context over Control | Share design rationale, trust team execution |
| Highly Aligned, Loosely Coupled | Clean interfaces, independent services |
| Inclusion | Diverse perspectives in design review |
| Impact | Design for measurable business outcomes |

**Netflix SD Behavioral Question Examples:**
- "Tell me about a system where you proactively identified and fixed a potential failure mode."
- "Describe a time you had to make a decision about a system that you knew would cause controversy."
- "How have you handled a situation where a system you designed caused a production incident?"

---

## 5. Common Behavioral Questions with Answers

### Q1: "Tell me about a time you had to scale a system"

**STAR Answer:**
> **S**: "Our user-facing API was experiencing 3s p99 latency during peak hours. We were processing 5K QPS on a monolithic deployment with a single PostgreSQL database."
>
> **T**: "I needed to reduce p99 latency to <200ms and support 50K QPS for upcoming product launch."
>
> **A**: "I proposed a 3-phase approach:
> 1. **Immediate** (1 week): Add Redis cache for read-heavy endpoints → reduced DB load by 70%
> 2. **Short-term** (1 month): Implement read replicas and shard the database by customer_id
> 3. **Medium-term** (3 months): Extract the high-traffic endpoint into a separate service with its own scaling
>
> I built consensus by showing latency waterfall charts and projected growth numbers."
>
> **R**: "P99 latency dropped from 3s to 180ms. The system handled 50K QPS during launch with no degradation. The sharding strategy was adopted by two other teams."

### Q2: "Describe a failure in a system you designed"

**STAR Answer:**
> **S**: "My rate limiter service had a Redis cluster dependency. During a major AWS us-east-1 outage, the Redis cluster became unreachable."
>
> **T**: "Without rate limiting, backend services were overwhelmed. I needed to ensure graceful degradation."
>
> **A**: "I implemented a circuit breaker pattern: when Redis is unreachable after 3 retries with 100ms timeout, fall back to local in-memory rate limiting with best-effort accuracy. I also added health check endpoints and latency monitoring."
>
> **R**: "During the next AWS incident, the system gracefully fell back to local limiting. 97% of requests were still rate-limited correctly. Post-mortem, we added multi-region Redis replicas."

### Q3: "How do you handle competing priorities from stakeholders?"

> "In my previous role, product wanted a new recommendation feature with 2-week timeline, while infrastructure wanted a database migration that would take 4 weeks.
>
> I facilitated a cross-functional meeting where we quantified the impact of each:
> - Product delay: $200K revenue loss
> - Migration delay: $50K/month extra DB cost + increasing performance risk
>
> We agreed on a hybrid approach: ship a simplified version of the recommendation feature (using existing schema) in 2 weeks, then do the migration while iterating on recommendations. This required the product team to accept a launch with 70% of the desired features.
>
> Both teams were 80% satisfied, and the release hit its target metrics."

### Q4: "Tell me about a time you had to learn a new technology for a design"

> "Our team needed to build a real-time data pipeline. I had extensive experience with batch processing (Spark) but no streaming experience (Flink/Kafka Streams).
>
> I spent 2 weeks: reading the Confluent documentation, building a small proof-of-concept for our use case, and benchmarking throughput.
>
> I presented my findings: Kafka Streams was simpler for our stateless transformations, while Flink would be better for stateful windowed aggregations. We started with Kafka Streams for Phase 1.
>
> The pipeline processed 1M events/min with <100ms latency. I later mentored two junior engineers on streaming concepts."

### Q5: "How do you ensure your design is maintainable?"

> "I follow three principles:
> 1. **Document decisions**: Every significant design choice is documented in ADRs (Architecture Decision Records) with context, options, and rationale.
> 2. **Simple by default**: I prefer well-understood patterns (request-response, pub-sub) over exotic architectures unless there's a clear need.
> 3. **Incremental improvement**: Design for today's scale + 6 months. Don't build a distributed system for what a monolith can handle.
>
> At [Company], I championed ADRs for our payment system. When a new engineer joined, they could understand why we chose Kafka over SQS within 10 minutes of reading. This reduced onboarding time by 40% and prevented repeated debates about past decisions."

### Q6: "Describe a time you changed someone's mind about a design"

> "A senior engineer insisted on using microservices for our new notification system. I believed a modular monolith would be more appropriate given our 4-person team and 6-month timeline.
>
> Instead of directly opposing, I proposed we both write a one-page design document with our approaches, evaluating each against: time to ship, operational burden, scaling needs, and team expertise.
>
> After review, 3 of 4 criteria favored the modular monolith. We agreed to start with the monolith but structure the code with clear bounded contexts so microservices extraction would be straightforward later.
>
> Two years later, the notification system was still a monolith serving 500M notifications/day. We never needed to split."

### Q7: "Tell me about a design trade-off you made"

> **Trade-off**: Redis vs database for session storage
>
> **Option A (Redis)**: 1ms read/write, ephemeral, 100GB cluster needed
> **Option B (PostgreSQL)**: 5ms read/write, durable, no extra infrastructure
>
> **Decision**: I chose Redis for active sessions (TTL 24h) with PostgreSQL as the source of truth for long-lived sessions. Reasoning:
> - 80% of session lookups are within 6 hours of creation → Redis covers the hot path
> - Session loss tolerance: if Redis is lost, users just re-login
> - Cost: Redis cluster was $500/month vs the scalability benefit
>
> **Result**: Login latency dropped from 12ms to 2ms. Zero session-related incidents in 18 months. The Redis cluster handled 200K concurrent sessions at peak."
