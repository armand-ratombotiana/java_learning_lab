# Company Interview Guide — System Design Roles

> Interview process for system design roles at top tech companies.
> Covers levels, rounds, timelines, evaluation criteria, real stories, and prep resources.

---

## Table of Contents

1. [Google (L3-L7)](#google-l3-l7)
2. [Amazon (SDE I-Principal)](#amazon-sde-i---principal-engineer)
3. [Meta/Facebook (E3-E7)](#meta-aka-facebook-e3-e7)
4. [Microsoft (59-69)](#microsoft-59-69)
5. [Apple (ICT2-ICT5)](#apple-ict2-ict5)
6. [Netflix (L3-L6+)](#netflix-l3-l6)
7. [Uber (SE I-Staff)](#uber-se-i---staff-engineer)
8. [Stripe](#stripe)
9. [Twitter/X](#twitterx)
10. [DoorDash](#doordash)
11. [TikTok](#tiktok)

---

## Google (L3-L7)

### Levels Overview

| Level | Title | Years Exp | System Design Expectation |
|-------|-------|-----------|--------------------------|
| L3 | SWE II | 0-2 | No system design (coding + Googleyness) |
| L4 | SWE III | 2-5 | 1 system design round, design small-medium systems |
| L5 | Senior SWE | 5-8 | 2 system design rounds, design large systems independently |
| L6 | Staff SWE | 8+ | 3+ system design rounds, cross-team/cross-org design |
| L7 | Senior Staff | 12+ | 4+ rounds, organization-wide technical vision |

### Interview Rounds

- **Phone screen**: 45 min coding
- **Onsite (virtual)**: 4-6 rounds
  - 2-3 coding rounds (CoderPad, usually Python/Java/C++)
  - 1-2 system design rounds (Google Docs)
  - 1 Googleyness/LP round
  - 1 additional round (varies by level)
- **Timeline**: 2-6 weeks from application to offer

### System Design Evaluation Criteria (L4+)

| Criterion | Weight | What They Look For |
|-----------|--------|-------------------|
| Requirements gathering | 15% | Clarity questions, edge cases, scope definition |
| System architecture | 30% | High-level diagram, component identification |
| Scalability planning | 20% | Estimation, bottlenecks, scaling strategy |
| Data storage design | 15% | Schema, indexing, sharding, replication |
| Trade-off analysis | 20% | Explicit trade-off articulation, justification |

### Hiring Bar

- **L4**: Must demonstrate ability to design systems with guidance
- **L5**: Must independently design and defend a large system
- **L6**: Must show cross-functional leadership and ambiguous problem solving
- **Hiring committee**: 3+ senior engineers review all feedback independently
- **Compensation committee**: Reviews offer after HC approval

### Compensation (2024 estimates)

| Level | Total Comp (USD) | Sign-on | Notes |
|-------|-----------------|---------|-------|
| L3 | $150K-$200K | $10K-$30K | 85% base, 15% equity |
| L4 | $200K-$350K | $30K-$75K | Mix of base, bonus, RSUs |
| L5 | $350K-$550K | $50K-$100K | Significant RSU component |
| L6 | $500K-$800K | $100K+ | Heavy equity weighting |

### Real Stories

**Story 1: L5 System Design — Design YouTube**
> "I spent 15 min on requirements (uploads, transcoding, playback, search, recommendations), 10 min on estimation (500h/min upload, 1B users), then designed the architecture. The interviewer kept asking 'what happens when a transcoding node fails?' I discussed job replay, dead letter queues, and graceful degradation."

**Story 2: L4 — Design Google Search**
> "Focus was entirely on the search serving path (crawling was explicitly out of scope). I designed: query understanding → spell correction → document retrieval → ranking → snippet generation. The deep dive was on ranking — how PageRank works and its limitations for personalized search."

### Prep Resources

| Resource | Use |
|----------|-----|
| DDIA (Kleppmann) | Chapters 1-9 for distributed systems theory |
| System Design Alex Xu Vol 1 & 2 | Practice all 24 designs |
| LeetCode System Design | Practice structured thinking |
| Google Engineering Blog | Real Google infrastructure cases |
| Google SRE Book | Reliability patterns Google uses |

---

## Amazon (SDE I - Principal Engineer)

### Levels Overview

| Level | Title | Years Exp | System Design Expectation |
|-------|-------|-----------|--------------------------|
| SDE I | Entry/Jr | 0-2 | No SD round |
| SDE II | Mid | 2-5 | 1-2 SD rounds, design small-medium systems |
| SDE III | Senior | 5-8 | 2 SD rounds, lead system design for team |
| Principal | Principal | 10+ | 3-4 SD rounds, cross-organization design |

### Interview Rounds

- **Phone screen**: 45-60 min coding (usually Amazon-focused question)
- **Onsite**: 4-6 rounds
  - 2 coding rounds (whiteboard/CoderPad)
  - 1-2 system design rounds
  - 1-2 Leadership Principles (LP) rounds
  - 1 Bar Raiser round (always present, can be any type)
- **Timeline**: 2-8 weeks (Amazon can be slower)

### System Design Evaluation Criteria

| Criterion | Weight | What They Look For |
|-----------|--------|-------------------|
| API design | 25% | RESTful interfaces, idempotency, versioning |
| Data modeling | 25% | SQL vs NoSQL, schema, partitioning |
| Scalability & reliability | 20% | Fault tolerance, multi-AZ, auto-scaling |
| Operational excellence | 15% | Monitoring, deployment, rollback |
| Frugality & cost | 15% | Cost-aware decisions, right-sizing |

### Leadership Principles (LP) for System Design

| LP | How It Appears in SD |
|----|---------------------|
| Customer Obsession | "Design this for customer needs, not technical purity" |
| Ownership | "What happens when your service fails at 3AM?" |
| Frugality | "This cache cluster costs $50K/month — justify it" |
| Dive Deep | "How exactly does the indexing work under the hood?" |
| Think Big | "How would this scale to all Amazon's 300M+ customers?" |

### Compensation (2024 estimates)

| Level | Total Comp (USD) | Sign-on | Notes |
|-------|-----------------|---------|-------|
| SDE I | $130K-$180K | $10K-$25K | Mostly base |
| SDE II | $200K-$350K | $30K-$80K | 2-yr front-loaded RSUs |
| SDE III | $350K-$600K | $50K-$150K | Heavy stock component |
| Principal | $600K-$1.2M | $100K-$300K | Large multi-year grants |

### Real Stories

**Story 1: SDE II — Design Shopping Cart**
> "Bar Raiser asked: 'The shopping cart shows wrong quantity after network partition — why? How do you fix it?' This was about eventual consistency in DynamoDB. I discussed session affinity, conditional writes, and DynamoDB transactions."

**Story 2: SDE III — Design S3-like System**
> "Interviewer asked about garbage collection for deleted objects. I discussed mark-and-sweep style GC with compaction, reference counting, and cost implications. LP 'Frugality' came up when discussing storage overhead of metadata vs GC frequency."

---

## Meta (aka Facebook) (E3-E7)

### Levels Overview

| Level | Title | Years Exp | System Design Expectation |
|-------|-------|-----------|--------------------------|
| E3 | Software Engineer | 0-2 | No SD, coding + behavioral |
| E4 | Software Engineer | 2-5 | 1 SD round |
| E5 | Senior SE | 5-8 | 2 SD rounds, cross-functional |
| E6 | Staff SE | 8+ | 2-3 SD rounds, impact at org level |
| E7 | Principal SE | 12+ | 3+ SD rounds, company-wide impact |

### Interview Rounds

- **Phone screen**: 45 min coding (LeetCode medium)
- **Onsite**: 4-5 rounds
  - 2 coding rounds (CoderPad)
  - 1-2 system design rounds
  - 1 behavioral round
- **Timeline**: 1-2 weeks (Meta is fast)

### System Design Evaluation Criteria

| Criterion | Weight | What They Look For |
|-----------|--------|-------------------|
| Speed of design | 20% | Can they produce a solid design in 40 min? |
| Requirements scoping | 15% | Narrows ambiguity, asks right questions |
| Design quality | 30% | Architecture, data flow, component interaction |
| Trade-off awareness | 20% | Clearly states pros/cons |
| Collaboration | 15% | Takes hints, conversational |

### What Meta Evaluates Differently

- **Move Fast culture**: A 60% solution delivered is better than a perfect design that takes forever
- **Full-stack thinking**: Design should consider client-side implications (mobile especially)
- **Growth mindset**: How does design enable product experimentation (A/B testing)?
- **Data-driven**: All design decisions should tie to measurable metrics

### Compensation (2024 estimates)

| Level | Total Comp (USD) | Sign-on | Notes |
|-------|-----------------|---------|-------|
| E3 | $160K-$200K | $10K-$30K | |
| E4 | $200K-$350K | $30K-$75K | |
| E5 | $350K-$600K | $50K-$100K | |
| E6 | $600K-$1M | $100K+ | |

### Real Stories

**Story 1: E5 — Design News Feed**
> "The interviewer asked 'How would you A/B test a new ranking algorithm?' I discussed shadow evaluation, online interleaving, and counterbalancing. Key insight: Meta cares deeply about the experimentation infrastructure."

**Story 2: E4 — Design Instagram Stories**
> "I designed the media processing pipeline. The interviewer redirected to the viewer side — how do you track who viewed a story? I proposed a Write-Audience-Read pattern: writer polls read-receipts from a viewership service."

---

## Microsoft (59-69)

### Levels Overview

| Level | Title | Years Exp | System Design Expectation |
|-------|-------|-----------|--------------------------|
| 59 | SDE I | 0-2 | Coding only |
| 60-61 | SDE II | 2-5 | 1 SD round |
| 62-63 | Senior SDE | 5-8 | 2 SD rounds |
| 64-65 | Principal SDE | 8-12 | 2-3 SD rounds |
| 66-67 | Partner | 12+ | 3+ SD rounds |
| 68-69 | Distinguished | 15+ | Organization-wide |

### Interview Rounds

- **Phone screen**: 45 min coding or SD
- **Onsite**: 4-5 rounds
  - 2 coding rounds (whiteboard or Teams + OneNote)
  - 1-2 system design rounds
  - 1 behavioral + "ASAP" (Microsoft's bar raiser equivalent)
  - 1 cross-functional (varies)
- **Timeline**: 3-8 weeks (can be slower)

### System Design Evaluation

- **Design completeness**: Covers full lifecycle (dev, deploy, maintain)
- **Testing**: How would you test this system before production?
- **Enterprise awareness**: Multi-tenancy, compliance, data residency
- **Growth mindset**: How do you take feedback during interview?
- **Azure ecosystem**: Bonus if design leverages Azure services

### Real Stories

**Story 1: L63 — Design Azure IoT Hub**
> "Focus was on device registration, telemetry ingestion, and command delivery at millions of devices. The interviewer asked about protocol gateway for MQTT/AMQP/HTTPS. Key topic: device twin pattern for state synchronization."

**Story 2: L65 — Design Teams at Scale**
> "We discussed geo-redundant deployment, SIP gateway for PSTN calling, and compliance features like eDiscovery. The deep dive was on how Teams handles media routing for optimal call quality."

---

## Apple (ICT2-ICT5)

### Levels Overview

| Level | Title | Years Exp | System Design Expectation |
|-------|-------|-----------|--------------------------|
| ICT2 | Engineer I | 0-3 | Coding + basic design |
| ICT3 | Engineer II | 3-5 | 1 SD round |
| ICT4 | Senior Engineer | 5-8 | 2 SD rounds |
| ICT5 | Architect | 8-12 | 2-3 SD rounds |

### Interview Rounds

- **Phone screen**: 45-60 min technical
- **Onsite**: 5-7 rounds (Apple has more rounds than most)
  - 2-3 coding rounds
  - 1-2 system design rounds
  - 1-2 domain-specific rounds
  - 1 behavioral/hiring manager
- **Timeline**: 3-12 weeks (Apple is slow, deliberate)

### System Design Evaluation

- **Privacy-first**: Data minimization, on-device processing, encryption
- **Product intuition**: How does the design impact user experience?
- **Simplicity**: Clean, maintainable, non-over-engineered
- **Integration**: How does this interact with existing Apple ecosystem?

### Real Stories

**Story 1: ICT3 — Design iCloud Sync**
> "I designed a CRDT-based sync engine for notes. The interviewer pressed on conflict resolution for concurrent edits. I discussed last-writer-wins for text and merge strategies for different data types."

**Story 2: ICT4 — Design APNs**
> "Focus was on battery-efficient persistent connections. We discussed push notification coalescing, connection draining for network changes, and privacy-preserving notification delivery without revealing user patterns."

---

## Netflix (L3-L6+)

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| L3 | Software Engineer | 2-5 |
| L4 | Senior SE | 5-8 |
| L5 | Staff SE | 8+ |
| L6 | Senior Staff | 12+ |

### Interview Rounds

- **Phone screen**: 45 min
- **Onsite**: 4-5 rounds
  - 1-2 system design
  - 1-2 coding/algorithm
  - 1 cultural (freedom & responsibility)
- **Timeline**: 2-4 weeks

### Unique Approach

- **No LeetCode-style problems**: Focus on real engineering scenarios
- **Cultural fit is critical**: "Freedom & Responsibility" — are you self-directed?
- **Deep expertise**: Expect 45 min deep dive on a single topic
- **Chaos engineering mindset**: Interviewers look for resilience thinking

### Real Stories

**Story 1: Senior — Design Video Pipeline**
> "The interview was entirely about resilience during CDN failure. I proposed a multi-CDN strategy with automatic failover, regional caches, and dropping to lower bitrates before buffering."

**Story 2: Staff — Design Open Connect CDN**
> "Discussion covered capacity planning for new releases (50x traffic spikes), ISP peering agreements, and how to decide which content to pre-populate vs fetch on demand."

---

## Uber (SE I - Staff Engineer)

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| L3 | SE I | 0-2 |
| L4 | SE II | 2-5 |
| L5 | Senior SE | 5-8 |
| L6 | Staff SE | 8+ |

### Interview Rounds

- **Phone screen**: 45 min
- **Onsite**: 4-5 rounds
  - 2 coding
  - 1-2 system design
  - 1 behavioral (Uber values)
- **Timeline**: 2-4 weeks
- **Format**: CoderPad + verbal

### Focus Areas

- Real-time systems
- Geospatial data (H3, quad-tree, map-matched ETAs)
- Marketplace dynamics (surge, dispatching)
- Distributed systems (Kafka, Cassandra, HDFS)

---

## Stripe

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| L1 | Engineer | 0-2 |
| L2 | Engineer II | 2-5 |
| L3 | Senior Engineer | 5-8 |
| L4 | Staff Engineer | 8+ |

### Interview Rounds

- **Phone screen**: 60 min (coding + system design hybrid)
- **Onsite**: 4-5 rounds
  - 1-2 system design (API-heavy focus)
  - 1 coding
  - 1 debugging
  - 1 behavioral
- **Timeline**: 2-4 weeks

### Unique Focus

- **API design**: Stripe interviews heavily emphasize clean API surfaces
- **Debugging**: Must debug a complex distributed system scenario
- **Idempotency**: Expect deep questions on exactly-once processing
- **Stripe values**: "Wonder" (intellectual curiosity), "Care" (customer empathy)

---

## Twitter/X

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| SWE | L4 | 2-5 |
| Senior | L5 | 5-8 |
| Staff | L6 | 8+ |

### Interview Rounds

- **Phone screen**: 45-60 min
- **Onsite**: 4 rounds
  - 1-2 system design
  - 1 coding
  - 1 cross-functional/behavioral
- **Timeline**: 2-4 weeks

### Focus

- Timeline generation (fanout, ranking)
- Real-time search (Earlybird)
- Trending topics (burst detection, time-decay)
- Ads platform

---

## DoorDash

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| E3 | Software Engineer I | 0-2 |
| E4 | Software Engineer II | 2-5 |
| E5 | Senior SE | 5-8 |
| E6 | Staff SE | 8+ |

### Interview Rounds

- **Phone screen**: Coding
- **Onsite**: 4-5 rounds
  - 1-2 coding (including system design coding hybrid)
  - 1-2 system design
  - 1 behavioral
- **Timeline**: 2-4 weeks

### Focus

- Marketplace design (3-sided: consumer, merchant, dasher)
- Real-time logistics (dispatch, routing, tracking)
- Scalable search (Elasticsearch)
- Operational metrics (DASI: delivery time, accuracy, satisfaction)

---

## TikTok

### Levels

| Level | Title | Years Exp |
|-------|-------|-----------|
| 1-1 | Engineer | 0-2 |
| 1-2 | Engineer II | 2-5 |
| 2-1 | Senior Engineer | 5-8 |
| 2-2 | Staff Engineer | 8+ |

### Interview Rounds

- **Phone screen**: 45 min coding
- **Onsite**: 4-5 rounds
  - 1-2 coding
  - 1-2 system design
  - 1 behavioral
- **Timeline**: 2-6 weeks

### Focus

- Video processing pipelines
- Recommendation systems (For You Page)
- Content moderation at scale
- Global infrastructure (data localization, CDN)
- Growth engineering (viral loops, notifications)

---

## Summary Comparison Table

| Company | SD Rounds | Format | Unique Focus | Timeline |
|---------|-----------|--------|--------------|----------|
| Google | 1-3 | Google Docs | Scale, distributed systems | 2-6 weeks |
| Amazon | 1-3 | Whiteboard/Chime | APIs, LPs, ops excellence | 2-8 weeks |
| Meta | 1-2 | CoderPad | Real-time, social graph | 1-2 weeks |
| Microsoft | 1-2 | Teams + OneNote | Enterprise, testing | 3-8 weeks |
| Apple | 1-2 | In-person | Privacy, UX, simplicity | 3-12 weeks |
| Netflix | 2-3 | Whiteboard | Resilience, CDN | 2-4 weeks |
| Uber | 1-2 | CoderPad | Geo, marketplace | 2-4 weeks |
| Stripe | 1-2 | CoderPad | API design, idempotency | 2-4 weeks |
| Twitter | 1-2 | CoderPad | Timeline, real-time | 2-4 weeks |
| DoorDash | 1-2 | Whiteboard/CoderPad | 3-sided marketplace | 2-4 weeks |
| TikTok | 1-2 | CoderPad | Video, recommendation | 2-6 weeks |
