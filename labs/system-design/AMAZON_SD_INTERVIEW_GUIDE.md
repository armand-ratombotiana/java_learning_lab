# Amazon System Design Interview Guide

> Comprehensive guide to system design interviews at Amazon.
> Covers top problems, solution frameworks, evaluation criteria, and design philosophy.

---

## Table of Contents

1. [Amazon's Interview Process](#1-amazons-interview-process)
2. [Top 5 System Design Problems](#2-top-5-system-design-problems)
3. [Detailed Solution Frameworks](#3-detailed-solution-frameworks)
4. [Evaluation Criteria & Leadership Principles](#4-evaluation-criteria--leadership-principles)
5. [Amazon's Design Philosophy](#5-amazons-design-philosophy)
6. [Real Interview Stories](#6-real-interview-stories)
7. [Prep Strategy](#7-prep-strategy)

---

## 1. Amazon's Interview Process

### Rounds
- **Phone screen**: 45-60 min coding (typically LeetCode medium, Amazon-specific questions)
- **Onsite (virtual via Chime)**: 4-6 rounds
  - 2 coding rounds (whiteboard-style, often focused on OOD and data structures)
  - 1-2 system design rounds
  - 1-2 Leadership Principles (LP) rounds
  - 1 Bar Raiser round (typically an experienced engineer from another org)

### System Design Specifics
- **Format**: Shared document (Amazon uses an internal tool similar to Google Docs)
- **Duration**: 60 min per round (Amazon gives more time than Google/Meta)
- **Level**: SDE II = 1 SD round; SDE III+ = 2 SD rounds
- **Structure**: Requirements (10 min) → API design (10 min) → Data model (10 min) → Architecture (15 min) → Deep dive (15 min)

### Bar Raiser Round
- Always present for SDE II and above
- Evaluates whether you meet the "bar" for Amazon hiring standards
- Can be any type (coding, system design, or LP)
- Bar Raiser has veto power over hiring decisions
- Often asks the hardest questions across multiple dimensions

---

## 2. Top 5 System Design Problems

| # | Problem | Frequency | Difficulty | Key Concepts |
|---|---------|-----------|------------|-------------|
| 1 | Design Amazon Shopping Cart | Very High | Medium | Session state, DynamoDB, idempotency |
| 2 | Design Amazon DynamoDB | Very High | Hard | Consistent hashing, quorum, LSM tree |
| 3 | Design Amazon S3 | High | Hard | Erasure coding, metadata, multi-tenancy |
| 4 | Design Prime Video | High | Hard | CDN, adaptive bitrate, DRM |
| 5 | Design Recommendation Engine | Medium | Hard | ML pipeline, collaborative filtering, real-time |

### Other Problems Amazon Asks
- Design a product catalog / search system
- Design Amazon's logistics/warehouse system
- Design a delivery tracking system
- Design Amazon Go (just walk out)
- Design Alexa voice service
- Design an inventory management system
- Design Amazon's advertising system

---

## 3. Detailed Solution Frameworks

### Problem 1: Design Shopping Cart

**Requirements**:
- 300M+ active customers
- Add/remove/modify items
- Save for later, multi-device sync
- Price locking at add-to-cart time
- Handle 100x spike on Prime Day

**Estimation**:
- 300M customers, 10 cart actions/day/customer = 3B actions/day
- Peak: 3B / 86400 × 5 = ~175K QPS
- Cart item: user_id + item_id + quantity + price_locked + added_at = ~200B
- Cart per user: avg 10 items, 50M active carts at peak

**API Design**:
```
POST /v1/carts/{customerId}/items
  Request: { itemId, quantity, priceLockDuration }
  Response: 201 { cartItemId, itemId, quantity, lockedPrice }
  Idempotency: idempotencyKey in header (prevents double-add)

DELETE /v1/carts/{customerId}/items/{cartItemId}

GET /v1/carts/{customerId}
  Response: { items: [...], subtotal, saveForLater: [...] }

PATCH /v1/carts/{customerId}/items/{cartItemId}
  Request: { quantity, moveToSaveForLater }
```

**Design**:

```
API Gateway (rate limiting, auth)
    │
    ▼
┌──────────────┐
│ Cart Service  │  Stateless, scales horizontally
│              │
│ Redis Cache  │  Active cart session (TTL: 24h, extend on activity)
│              │
│ DynamoDB     │  Persistent cart storage
│ table:       │  PK: customer_id (hash) + SK: item_id (sort)
│              │  Attributes: quantity, locked_price, added_at
│              │  TTL: 30 days on inactive carts
└──────────────┘

Integration:
- Price Service: Validates locked price on checkout
- Inventory Service: Reserves stock on checkout
- Promotion Service: Applies discounts
```

**Key Decisions**:
- **Redis + DynamoDB**: Redis for hot path (read-heavy, low latency), DynamoDB for persistence
- **Session cart → Customer cart**: Guest users get session cart (Redis, ephemeral). On login, merge with customer cart in DynamoDB
- **Price locking**: Price is locked at add-to-cart time, valid for checkout within lock window (configurable, typically 15-30 min)
- **Prime Day scaling**: DynamoDB auto-scaling, Redis cluster with read replicas, throttling at API gateway with customers grouped by priority

**Trade-offs**:
| Decision | Option A | Option B | Amazon's Choice |
|----------|----------|----------|----------------|
| Cart merge strategy | Replace session cart with persistent cart | Merge both | Merge (add items from session cart to persistent) |
| Price lock duration | Short (5min) | Long (24h) | Medium (15-30min, configurable per category) |
| Inventory reservation | On add-to-cart | On checkout start | On checkout (prevents cart abandonment from hoarding inventory) |

### Problem 2: Design DynamoDB

**Requirements**:
- Single-digit millisecond latency at any scale
- Fully managed, multi-region
- Strong and eventual consistency
- Auto-scaling, pay-per-request

**Design Principles (from Dynamo paper)**:
- Incremental scalability: Add one node at a time
- Symmetry: Every node has same responsibilities
- Decentralization: No single point of failure
- Heterogeneity: Different nodes can have different capacity

**Architecture**:
- **Request routing**: Request Coordination layer → find node responsible for key
- **Partitioning**: Consistent hashing with virtual nodes
- **Replication**: N-way (default 3) with preference list
- **Consistency**: Quorum-based (R + W > N)
- **Storage**: LSM-tree based (SSTables, MemTable, compaction)

### Problem 3: Design S3

**Requirements**:
- 99.999999999% (11 9s) durability
- Virtually unlimited storage
- Global namespace (unique bucket names)
- Strong consistency (since Dec 2020)

**Design**:
- **Front-end**: Request routing, authentication, rate limiting
- **Metadata**: Multi-tenant metadata store (key: bucket+object → location, metadata)
- **Storage**: Erasure coding (12+4 scheme — split into 12 data fragments + 4 parity fragments, any 12 of 16 suffice)
- **Durability**: 11 9s via erasure coding across multiple availability zones

### Problem 4: Design Prime Video

**Requirements**:
- 200M+ subscribers globally
- 4K HDR, Dolby Atmos
- Multi-device (smart TV, mobile, web, tablet)
- Offline downloads

**Design**:
- **Content pipeline**: Source mastering → Transcode encoding ladder → Package (DASH/HLS) → CDN distribution
- **Adaptive bitrate**: Start with lowest resolution, step up based on bandwidth detection
- **CDN**: Amazon CloudFront + ISP caches
- **DRM**: Widevine (Android), FairPlay (iOS), PlayReady (Xbox)

### Problem 5: Design Recommendation Engine

**Requirements**:
- Personalized for 300M+ customers
- Real-time updates based on browsing behavior
- Cold-start for new users and new products
- A/B testing infrastructure

**Design**:
- **Offline**: Batch processing (Spark) → user-item interaction matrix → matrix factorization → embeddings
- **Nearline**: Streaming updates from clickstream (Kinesis) → real-time feature computation → model update
- **Online**: User request → retrieve user embedding → nearest neighbor search (FAISS) → candidate generation → ML ranking → blended results

---

## 4. Evaluation Criteria & Leadership Principles

### Technical Evaluation (70%)

| Criterion | Weight | Amazon Expectation |
|-----------|--------|-------------------|
| API Design | 25% | Clean RESTful interfaces, idempotency, versioning |
| Data Modeling | 25% | Schema, index strategy, SQL vs NoSQL decisions |
| Scalability & Reliability | 15% | Fault tolerance, multi-AZ, auto-scaling |
| Operational Excellence | 15% | Monitoring, deployment, rollback, SLOs |
| Cost Awareness | 10% | Frugality — right-size resources, justify costs |
| Security | 10% | Least privilege, encryption, compliance |

### Leadership Principles Evaluation (30%)

| Principle | How It's Assessed |
|-----------|------------------|
| Customer Obsession | "How does this design benefit the customer?" |
| Ownership | "What happens when this system fails at 3AM?" |
| Frugality | "Can you justify this $50K/month solution?" |
| Dive Deep | "How does the index actually work under the hood?" |
| Think Big | "How would this scale to all Amazon customers?" |
| Insist on Highest Standards | "What SLAs does this system have?" |
| Are Right, A Lot | "How do you know this is the right choice?" |
| Learn and Be Curious | "What new technologies did you consider?" |

### LP Phrases to Use in System Design

- **Customer Obsession**: "Let's think about what the customer experiences..."
- **Ownership**: "If I own this service, I'd ensure..."
- **Bias for Action**: "We have 70% of the information — I recommend we proceed with..."
- **Frugality**: "To minimize cost while maintaining performance, I'd..."
- **Have Backbone**: "I disagree with that approach because..."

---

## 5. Amazon's Design Philosophy

### Core Principles
1. **API-first design**: Everything has a well-defined API. Services communicate through APIs, not shared databases.
2. **Operational excellence**: You build it, you run it. Every engineer is responsible for operations.
3. **Frugality**: Cost is a feature. Design with cost in mind — don't over-provision.
4. **Two-pizza teams**: Services should be owned by small teams (6-10 people).
5. **Single-threaded owners**: Each significant initiative has a dedicated leader.

### Amazon's Service-Oriented Architecture Rules
- All inter-service communication must be via API calls (no shared databases)
- Services must be independently deployable
- Each service has its own data store
- Teams communicate via API contracts

### Technologies You Should Know
| Area | Technologies |
|------|-------------|
| Compute | EC2, Lambda, ECS/EKS (generally ask about your exp with these) |
| Storage | S3, EBS, EFS |
| Databases | DynamoDB, Aurora/RDS, ElastiCache |
| Messaging | SQS, SNS, Kinesis |
| Networking | Route53, CloudFront, API Gateway |

---

## 6. Real Interview Stories

### Story 1: SDE II — Design Shopping Cart
> **Bar Raiser question**: "Your cart shows wrong quantity. Why and how do you fix it?"
>
> **Root cause analysis**: Eventual consistency in DynamoDB — read after write to a different replica showed stale data.
>
> **Fix**: Strongly consistent reads for cart operations (since cart is a low-volume, high-importance data store). Conditional writes using DynamoDB's optimistic locking with version numbers.
>
> **LP moment**: When asked about cost, candidate calculated: strongly consistent reads cost the same as eventually consistent reads in DynamoDB, so there's no cost trade-off. This showed Dive Deep and Frugality.

### Story 2: SDE III — Design S3-like System
> **Challenge**: Multi-tenancy and noisy neighbors.
>
> **Solution**: Request throttling per tenant with burst capacity. Each tenant's operations are tracked in a token bucket. Burst credits based on historical usage. During peak, tenants are smoothly throttled rather than abruptly rejected.
>
> **LP focus**: Customer Obsession (fairness), Frugality (resource utilization), Highest Standards (performance isolation).

---

## 7. Prep Strategy

### 8-Week Prep Plan for Amazon

**Weeks 1-2: API Design & Data Modeling**
- Master RESTful API design: HATEOAS, idempotency, pagination, versioning
- Study DynamoDB: partitioning, query patterns, GSI/LSI, DAX caching
- Lab focus: 07-api-design, 09-distributed-database-design

**Weeks 3-4: Core Systems**
- Practice: Shopping cart, product catalog, order processing
- Study security patterns: IAM, encryption, least privilege
- Lab focus: 14-design-payment-system, 06-messaging

**Weeks 5-6: Amazon-Specific Problems**
- Study DynamoDB paper and S3 architecture
- Practice: DynamoDB, S3, Prime Video, Recommendations
- Mock interviews with LP integration (mention LPs during design)

**Weeks 7-8: Polish LPs & Full Mocks**
- 15 LP stories prepared (5 fully detailed STAR)
- 5+ full system design mock interviews
- Know your resume systems in detail — Amazon asks about experience

### Must-Read Before Interview
- DynamoDB paper (Amazon's 2007 Dynamo paper)
- AWS Well-Architected Framework whitepaper
- Amazon Leadership Principles (memorize all 16)
- *System Design Interview* by Alex Xu

### Common Amazon Interview Mistakes
1. **Not mentioning Leadership Principles**: Weave LPs naturally into your answers
2. **Weak API design**: Amazon interviewers care deeply about clean APIs
3. **No cost awareness**: Frugality matters — reference cost in trade-offs
4. **Can't explain "why"**: Amazon wants the reasoning behind every decision
5. **Not asking about scale**: Always clarify scale/requirements before designing
