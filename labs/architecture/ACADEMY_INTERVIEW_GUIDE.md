# Architecture Academy — Staff+ Interview Guide

> Comprehensive interview preparation for Staff/Principal/Distinguished Engineer roles at top technology companies.

---

## Table of Contents

1. [Google L6+ (Staff/Staff SE)](#google-l6-staffstaff-se)
2. [Amazon Principal (L7/L8)](#amazon-principal-l7l8)
3. [Meta E6+ (Staff/Staff SE)](#meta-e6-staffstaff-se)
4. [Microsoft Principal (Level 67–69)](#microsoft-principal-level-67-69)
5. [Netflix Senior+ (Senior/Staff)](#netflix-senior-seniorstaff)
6. [Uber Senior+ (Senior/Staff)](#uber-senior-seniorstaff)
7. [Stripe Staff+](#stripe-staff)
8. [Common Architecture Themes Across Companies](#common-architecture-themes-across-companies)

---

## Google L6+ (Staff/Staff SE)

### System Design Round Expectations

Google L6+ system design interviews evaluate your ability to design large-scale distributed systems that serve billions of users. Unlike L5, where the focus is on functional correctness, L6+ probes your understanding of trade-offs, failure modes, and operational excellence.

**Key evaluation criteria:**
- **Scale estimation**: Must demonstrate back-of-envelope calculations for QPS, storage, bandwidth, and cache sizing without hesitation. Use powers of two and round numbers. For example, 1B DAU × 10 requests/day × 30 days = 300B monthly requests.
- **Data modeling**: Show deep understanding of relational vs NoSQL choices. Discuss sharding strategies (hash-based, range-based, consistent hashing) with trade-offs. For L6+, you must explain WHY you chose a particular database technology, not just WHAT you chose.
- **Consistency models**: Explain when you need strong consistency (financial transactions, inventory) vs eventual consistency (feeds, search indexing). Discuss CAP theorem trade-offs in context of your design.
- **Fault tolerance**: Design for failure from the start. Discuss redundancy, failover, recovery mechanisms. Google expects you to assume every component will fail and design accordingly.
- **Latency optimization**: Discuss caching layers (CDN, in-memory, distributed cache), CDN strategies, edge computing, and data locality.

**Architecture deep dive requirements:**
- Must discuss at least three architectural alternatives before converging on a solution
- Must identify and mitigate at least five failure scenarios
- Must discuss monitoring, alerting, and debugging strategies
- Must address security concerns including DDoS, auth, data encryption

**Leadership principles at L6+:**
- Technical leadership: You are expected to influence without authority, drive technical decisions across teams
- Cross-functional impact: Your design decisions affect multiple teams and products
- Mentorship: How you grow other engineers through design reviews and technical guidance
- Strategic thinking: Your architectural decisions must align with 12-18 month product roadmaps

### Sample Google L6 System Design Questions

| Question | Focus Area | Scale |
|----------|-----------|-------|
| Design YouTube | Video processing, CDN, recommendation | 2B+ MAU |
| Design Google Search | Crawling, indexing, ranking | 100B+ pages |
| Design Google Drive | File sync, conflict resolution, sharing | 1B+ users |
| Design Google Maps | Geospatial indexing, routing, traffic | 1B+ users |
| Design Google Photos | Image storage, ML tagging, sharing | 500M+ users |

### L6+ Behavioral Expectations

- **Technical decisiveness**: Make clear decisions with incomplete information. "I would choose X because Y, and here's how I'd validate that decision."
- **Disagree and commit**: Show willingness to challenge decisions but also commit once a decision is made.
- **Strategic impact**: Frame your past projects in terms of business impact, not just technical complexity.
- **Conflict resolution**: Demonstrate how you've resolved technical disagreements between teams.

---

## Amazon Principal (L7/L8)

### System Design Round Expectations

Amazon Principal interviews are notorious for their intensity. The Bar Raiser ensures every candidate meets the highest standards. For L7, you need to demonstrate architecture design at the AWS/Amazon scale.

**Key evaluation criteria:**
- **Leadership Principle alignment**: Every answer must explicitly reference one or more Amazon Leadership Principles (Customer Obsession, Ownership, Insist on Highest Standards, Think Big, Dive Deep, Deliver Results).
- **Two-pizza team scope**: Your design should be implementable by a 6-10 person team but have organizational impact. Principal engineers design systems that span multiple teams.
- **Working backwards**: Start with the press release and FAQ. Frame your design around customer needs first, then technical implementation.

**Architecture deep dive requirements:**
- Discuss how your design handles Amazon-scale load (millions of requests per second)
- Demonstrate deep understanding of AWS services (S3, DynamoDB, Kinesis, Lambda, API Gateway)
- Address operational excellence through automation, self-healing, and runbooks
- Show security-first thinking: encryption at rest and in transit, IAM policies, VPC design

**L7 vs L8 distinction:**
- L7 (Principal): Designs within a domain, influences 3-5 teams, 2-5 year impact horizon
- L8 (Senior Principal): Designs across domains, influences entire organization, 3-7 year impact horizon

### Sample Amazon Principal System Design Questions

| Question | Focus Area | Leadership Principles |
|----------|-----------|----------------------|
| Design Amazon Shopping Cart | Order management, inventory, payments | Customer Obsession, Ownership |
| Design Amazon Recommendation Engine | Real-time ML, personalization, A/B testing | Dive Deep, Invent and Simplify |
| Design AWS IAM | Identity, authorization, scaling | Insist on Highest Standards |
| Design Amazon Delivery Logistics | Route optimization, real-time tracking | Deliver Results, Frugality |
| Design Amazon DynamoDB | Distributed database, replication, consistency | Think Big, Dive Deep |

### Principal Behavioral Expectations

- **Ownership**: Demonstrate end-to-end ownership of problems. "I owned this from concept to production, and here are the results."
- **Have backbone**: Show willingness to challenge decisions, even when unpopular. Provide specific examples.
- **Deliver results**: Quantify your impact. "Reduced latency by 40%, saving $2M/year in infrastructure costs."
- **Hire and develop the best**: Show how you've mentored senior engineers and raised the bar.

---

## Meta E6+ (Staff/Staff SE)

### System Design Rounds

Meta evaluates E6+ candidates on their ability to architect systems that serve billions of users with minimal latency. Meta's system design interviews are practical and focus on real Meta-scale problems.

**Key evaluation criteria:**
- **Full-stack thinking**: Your design must cover client, server, storage, and infrastructure layers. Meta values engineers who understand the complete stack.
- **Product-aware architecture**: Meta expects you to understand product requirements and design accordingly. Your technical decisions should be tied to product metrics.
- **Speed of iteration**: Meta values fast iteration and experimentation. Your architecture should support rapid A/B testing and feature rollouts.
- **Social graph awareness**: Many Meta designs involve social graphs, friend relationships, and content distribution.

**Architecture deep dive requirements:**
- Data modeling for social graphs (adjacency lists, inverse indexes)
- News feed ranking and real-time personalization algorithms
- Storage strategies for massive unstructured data (photos, videos)
- GraphQL API design for mobile clients (DataLoader, N+1 prevention)

### Sample Meta E6 System Design Questions

| Question | Focus Area | Technical Depth |
|----------|-----------|-----------------|
| Design Facebook News Feed | Ranking, real-time, personalization | ML, caching, push/pull |
| Design WhatsApp Messenger | Real-time messaging, presence, delivery | WebSockets, CRDT, exponential backoff |
| Design Instagram Stories | Ephemeral content, viewing patterns | TTL-based storage, CDN optimization |
| Design Facebook Live | Real-time video streaming, comments | HLS, WebRTC, chat architecture |
| Design Meta's Ad Platform | Real-time bidding, auction, attribution | Millisecond latency, ML serving |

### E6+ Behavioral Expectations

- **Impact at scale**: Your work has affected millions of users. Quantify everything.
- **Move fast**: Demonstrate how you've shipped quickly while maintaining quality.
- **Technical leadership**: Show how you've guided technical direction for your org.
- **Cross-functional collaboration**: Meta values engineers who work well with PMs, designers, and data scientists.

---

## Microsoft Principal (Level 67–69)

### System Design Rounds

Microsoft's Principal interviews focus on enterprise-scale architecture with a strong emphasis on Azure integration and hybrid cloud scenarios.

**Key evaluation criteria:**
- **Azure ecosystem**: Deep familiarity with Azure services (Cosmos DB, Service Bus, Event Hubs, AKS, App Service)
- **Enterprise concerns**: Multi-tenancy, compliance, governance, availability zones, disaster recovery
- **Integration architecture**: Legacy system integration, hybrid cloud, on-premise connectivity
- **Performance at scale**: Discuss partition management, throttling strategies, and capacity planning

**Architecture deep dive requirements:**
- Show understanding of Azure Well-Architected Framework (cost, performance, reliability, security, operational excellence)
- Discuss multi-region deployment with active-active and active-passive configurations
- Address identity and access management through Azure AD integration
- Demonstrate cost-aware architecture decisions

### Sample Microsoft Principal System Design Questions

| Question | Focus Area | Azure Services |
|----------|-----------|---------------|
| Design Azure DevOps Pipeline | CI/CD, multi-tenant, scale | AKS, Cosmos DB, Service Bus |
| Design Microsoft Teams | Real-time communication, meetings | SignalR, Media Services |
| Design Azure SQL Database | Distributed database, geo-replication | Cosmos DB, Traffic Manager |
| Design Enterprise SSO System | Identity federation, AD integration | Azure AD, OAuth, SAML |
| Design Azure Event Hub | Event ingestion, stream processing | Event Hubs, Stream Analytics |

### Principal Behavioral Expectations

- **Cross-group collaboration**: Microsoft values engineers who work across divisions.
- **Customer-focused**: Demonstrate deep customer empathy and understanding.
- **Growth mindset**: Show willingness to learn from failures and adapt.
- **Inclusive leadership**: Evidence of fostering diverse and inclusive teams.

---

## Netflix Senior+ (Senior/Staff)

### System Design Rounds

Netflix interviews evaluate your ability to design chaos-resilient microservices that operate at global scale. Freedom and Responsibility culture heavily influences interview expectations.

**Key evaluation criteria:**
- **Chaos engineering mindset**: Design for failure proactively. Discuss Chaos Monkey, Chaos Kong, and failure injection testing.
- **Microservices expertise**: Demonstrated experience designing, deploying, and operating microservices at scale
- **Data-driven decisions**: Netflix is data-obsessed. Every architectural decision must be backed by data.
- **Content delivery**: Deep understanding of CDN architecture, Open Connect, adaptive bitrate streaming

**Architecture deep dive requirements:**
- Netflix's microservices architecture: how teams own services end-to-end
- Distributed streaming architecture: encoding, packaging, delivery
- Recommendation system: ML at the edge, A/B testing at scale
- Global infrastructure: AWS multi-region, Spinnaker, Titus

### Sample Netflix Senior+ System Design Questions

| Question | Focus Area | Netflix Context |
|----------|-----------|----------------|
| Design Netflix CDN | Content delivery, edge caching | Open Connect, adaptive bitrate |
| Design Netflix Recommendation Engine | Real-time ML, personalization | A/B testing, contextual bandits |
| Design Netflix Video Encoding Pipeline | Transcoding, quality optimization | Per-title encoding, VMAF |
| Design Netflix Member Experience | Personalization, AB tests, device sync | 200M+ members, thousands of device types |
| Design Netflix Studio Platform | Content production, asset management | Global studio workflow |

### Senior+ Behavioral Expectations

- **Freedom and responsibility**: Show how you've handled autonomy and made high-stakes decisions
- **Context over control**: Demonstrate how you've provided context to enable team decisions
- **Highly aligned, loosely coupled**: Show ability to align goals while maintaining team autonomy
- **Candor**: Evidence of direct, respectful communication and constructive conflict

---

## Uber Senior+ (Senior/Staff)

### System Design Rounds

Uber evaluates senior+ candidates on their ability to design real-time, geo-distributed systems that handle dynamic supply-demand matching at global scale.

**Key evaluation criteria:**
- **Real-time systems**: Deep understanding of real-time data pipelines and event processing
- **Geospatial expertise**: Indexing (geohash, S2, H3), spatial queries, real-time location tracking
- **Marketplace dynamics**: Supply-demand matching, dynamic pricing, surge algorithms
- **Platform architecture**: Uber's domain-oriented microservices, domain-specific infrastructure

**Architecture deep dive requirements:**
- Discuss consistent hashing for ride-driver matching
- Address real-time location update processing (millions of GPS updates/second)
- Demonstrate understanding of idempotency and exactly-once semantics in payment processing
- Show how to design for multi-city, multi-country deployment

### Sample Uber Senior+ System Design Questions

| Question | Focus Area | Uber Context |
|----------|-----------|-------------|
| Design Uber Price Engine | Dynamic pricing, surge | Real-time, market elasticity |
| Design Uber Dispatch System | Driver-rider matching | Geospatial, real-time optimization |
| Design Uber Eats | Food delivery, multi-sided marketplace | Real-time tracking, ETAs |
| Design Uber Routing Engine | Real-time navigation, traffic | Graph algorithms, traffic prediction |
| Design Uber Payment System | Payouts, billing, fraud detection | Multi-currency, compliance |

### Senior+ Behavioral Expectations

- **Customer obsession**: Uber values deep customer empathy (both riders and drivers)
- **Bias for action**: Show how you've moved quickly in ambiguous situations
- **Ownership**: End-to-end problem ownership from idea to production
- **Data-informed**: Every decision backed by data and experimentation

---

## Stripe Staff+

### System Design Rounds

Stripe evaluates staff+ candidates on their ability to design reliable, secure financial infrastructure that processes billions of dollars in transactions.

**Key evaluation criteria:**
- **Financial correctness**: Exactly-once processing, idempotency, audit trails, reconciliation
- **Security mindset**: PCI compliance, encryption, tokenization, fraud detection
- **API design**: Developer experience, versioning, backward compatibility, rate limiting
- **Global payments**: Multi-currency, cross-border, local payment methods, compliance

**Architecture deep dive requirements:**
- Discuss idempotency key design and idempotency guarantees across services
- Address double-spend prevention and race conditions in financial transactions
- Demonstrate understanding of payment gateway integration patterns
- Show how to design for 99.999% uptime requirements

### Sample Stripe Staff+ System Design Questions

| Question | Focus Area | Stripe Context |
|----------|-----------|---------------|
| Design Stripe Payment Processing | Authorization, capture, settlement | Multi-provider, idempotency |
| Design Stripe Connect | Marketplace payments, onboarding | KYC/AML, multi-party payments |
| Design Stripe Billing | Subscription management, invoicing | Dunning, proration, metered billing |
| Design Stripe Radar | Fraud detection, ML at scale | Real-time ML, feature engineering |
| Design Stripe API | Developer platform, rate limiting | 1000+ API endpoints, versioning |

### Staff+ Behavioral Expectations

- **Technical excellence**: Demonstrated commitment to correctness and reliability
- **User empathy**: Deep understanding of developer experience and API design
- **Bias for action**: Move fast in financial infrastructure while maintaining safety
- **Stripe values**: Transparency, fairness, positivity in communication

---

## Common Architecture Themes Across Companies

### Design Process Template

```
1. Requirements Clarification (5 min)
   - Functional requirements
   - Non-functional requirements (scale, latency, consistency, durability)
   - Constraints and assumptions

2. Scale Estimation (5 min)
   - Traffic estimates (QPS, bandwidth)
   - Storage estimates
   - Cache estimates

3. Data Model Design (5 min)
   - Schema design
   - API design (REST/gRPC/GraphQL)

4. High-Level Design (10 min)
   - System components and interactions
   - Data flow

5. Deep Dive (15 min)
   - Selected components in detail
   - Trade-off analysis
   - Failure scenarios and mitigations

6. Summary (5 min)
   - Recap decisions
   - Discuss alternatives
   - Identify future improvements
```

### Must-Know Numbers for Staff+ Interviews

| Metric | Value |
|--------|-------|
| DAU for global product | 1B+ |
| Requests per second (global) | 100K-1M+ |
| Request latency (P99 target) | <100ms |
| Database read latency (P99) | <5ms |
| Cache hit ratio target | >95% |
| Availability target | 99.99%+ |
| Storage per user | 100MB-10GB |
| Global data center count | 10-30+ |

### Architecture Decision Documentation Template

```markdown
## Decision: [Title]
- **Status**: [Proposed | Accepted | Deprecated | Superseded]
- **Date**: [YYYY-MM-DD]
- **Deciders**: [Names]
- **Technical Story**: [Link to issue/story]

### Context
[Describe the problem and forces at play]

### Decision Drivers
1. [Driver 1]
2. [Driver 2]

### Considered Options
- **Option A**: [Description]
  - Pros: [...]
  - Cons: [...]
- **Option B**: [Description]
  - Pros: [...]
  - Cons: [...]

### Decision Outcome
[Chosen option and rationale]

### Consequences
[Positive and negative consequences]

### Compliance
[How will compliance be verified?]
```

---

## Company-Specific Interview Tips

### Google
- Emphasize distributed systems fundamentals: Paxos/Raft, consistent hashing, gossip protocols
- Show depth in at least one area (storage, compute, networking, ML)
- Practice writing code on Google Docs for the coding rounds

### Amazon
- Internalize 16 Leadership Principles and prepare two stories per principle
- Use STAR format rigorously: Situation, Task, Action, Result
- Quantify everything: "Reduced costs by 30%," "Improved latency by 50ms"

### Meta
- Be prepared for production engineering rounds (debugging, performance analysis)
- Understand Meta's infrastructure: TAO (graph), Unicorn (search), Presto (analytics)
- Practice system design with mobile-first thinking

### Microsoft
- Prepare for "as a service" design questions (building platforms and services)
- Understand Azure Arc and hybrid cloud scenarios
- Demonstrate enterprise-grade thinking (compliance, governance, multi-tenancy)

### Netflix
- Demonstrate chaos engineering experience or deep theoretical knowledge
- Show understanding of CDN and streaming technologies
- Be ready to discuss cost-performance trade-offs in cloud infrastructure

### Uber
- Prepare for geospatial system design questions
- Understand marketplace dynamics and real-time systems
- Show experience with high-throughput event processing

### Stripe
- Demonstrate financial systems knowledge (payments, ledgers, reconciliation)
- Show strong API design philosophy
- Be prepared for security-focused deep dives (encryption, tokenization, PCI)

---

## Preparation Timeline

### 4-8 Weeks Before
- Read system design books (DDIA, Designing Data-Intensive Applications)
- Complete all Architecture Academy labs (focus on distributed patterns)
- Practice 3-4 system design questions per week

### 2-4 Weeks Before
- Focus on company-specific guides
- Practice behavioral stories (2-3 per leadership principle)
- Conduct mock interviews with peers
- Review all Architecture Academy interview guides

### 1-2 Weeks Before
- Light review of key concepts
- Rest and mental preparation
- Prepare questions to ask interviewers

### Day Before
- Review company guides
- Prepare logistics (setup, quiet space, backup internet)
- Get adequate sleep

---

## Recommended Reading

### Books
1. *Designing Data-Intensive Applications* — Martin Kleppmann
2. *Building Microservices* — Sam Newman
3. *Clean Architecture* — Robert C. Martin
4. *Domain-Driven Design* — Eric Evans
5. *System Design Interview* — Alex Xu
6. *Software Architecture: The Hard Parts* — Neal Ford

### Architecture Academy Labs
- Complete all 20 labs in the Architecture Academy
- Focus on distributed patterns (labs 11-20) for senior+ interviews
- Practice whiteboarding designs for each pattern

### Online Resources
- [High Scalability](http://highscalability.com/)
- [Martin Fowler's Blog](https://martinfowler.com/)
- [AWS Architecture Blog](https://aws.amazon.com/architecture/)
- [Netflix Tech Blog](https://netflixtechblog.com/)
- [Uber Engineering Blog](https://eng.uber.com/)

---

## Architecture Vocabulary for Interviews

### Must-Use Terms
- Eventually consistent, strongly consistent, causal consistency
- Idempotency, commutativity, idempotency key
- Partition tolerance, availability, consistency trade-offs
- Backpressure, circuit breaker, bulkhead, retry with exponential backoff
- Leader election, quorum, split brain
- Checkpoint, snapshot, write-ahead log
- Sidecar, ambassador, adapter patterns
- Blue-green deployment, canary release, feature flags

### Anti-Patterns to Identify
- Single point of failure
- God service / distributed monolith
- Synchronous coupling between services
- Missing idempotency in critical operations
- Inconsistent data without reconciliation
- Over-optimization without measured need

---

## Appendix A: System Design Practice Problems by Difficulty

### Easy (Foundation)
1. Design a URL shortener (TinyURL)
2. Design a chat system (1:1 messaging)
3. Design a rate limiter
4. Design a key-value store (single server)
5. Design a web crawler (single machine)

### Medium (Distributed)
6. Design a distributed key-value store
7. Design a distributed cache (Memcached/Redis)
8. Design a distributed logging system
9. Design a distributed job scheduler
10. Design a notification system (push, email, SMS)

### Hard (Global Scale)
11. Design YouTube/Netflix (video streaming)
12. Design Google Search (web crawling, indexing, ranking)
13. Design Facebook News Feed (social graph, ranking)
14. Design Uber (dispatch, pricing, real-time)
15. Design WhatsApp/Messenger (real-time messaging)
16. Design Amazon Shopping Cart (e-commerce checkout)
17. Design Twitter/Facebook (social media feed)
18. Design Google Drive/Dropbox (file storage, sync)
19. Design Google Maps (geospatial, routing)
20. Design a payment system (Stripe/PayPal)

### Expert (Staff+ Level)
21. Design DynamoDB/Cassandra (distributed database)
22. Design Kafka/Pulsar (distributed messaging)
23. Design Spanner (globally distributed SQL)
24. Design a metrics/monitoring system (Prometheus)
25. Design a container orchestration system (Kubernetes)

## Appendix B: Architecture Decision Records (ADR) Examples

### ADR 001: Adopt Event-Driven Architecture for Order Processing

**Context**: The order processing system needs to handle 10,000 orders per minute with real-time updates to inventory, shipping, and analytics. Current monolith cannot scale.

**Decision**: Adopt event-driven architecture using Kafka for asynchronous communication between services.

**Rationale**:
- Decouples order processing from downstream consumers
- Enables independent scaling of order ingestion vs processing
- Provides durable event storage for replay and debugging
- Supports multiple consumers (inventory, shipping, analytics) without changing the producer

**Consequences**:
- Requires schema management for event compatibility
- Eventual consistency between services
- Additional operational complexity for Kafka cluster management

**Status**: Accepted

### ADR 002: Use PostgreSQL for Event Store

**Context**: The event sourcing system needs a reliable event store with strong consistency and transaction support.

**Decision**: Use PostgreSQL as the event store rather than purpose-built EventStoreDB or Kafka.

**Rationale**:
- Team already has PostgreSQL expertise
- No additional operational overhead for a new database
- Supports transactions needed for atomicity guarantees
- Sufficient for current scale (1000 events/second)
- Can migrate to specialized event store if scale increases 10x

**Consequences**:
- May need to migrate at higher scale
- No built-in event projection/subscription capabilities
- Additional development for event replay and snapshot management

**Status**: Accepted

## Appendix C: Staff+ Interview Success Checklist

### Before the Interview
- [ ] Research the company's architecture blog and tech stack
- [ ] Understand the company's scale (DAU, QPS, storage)
- [ ] Prepare 5-7 behavioral stories with STAR format
- [ ] Practice back-of-envelope calculations
- [ ] Review Architecture Academy labs relevant to the role

### During the Interview
- [ ] Clarify requirements before proposing solutions
- [ ] Consider at least 2-3 architectural alternatives
- [ ] Discuss trade-offs explicitly (don't just pick one option)
- [ ] Address failure scenarios proactively
- [ ] Discuss monitoring, alerting, and operational concerns
- [ ] Connect technical decisions to business outcomes
- [ ] Use the company's own terminology (AWS services for Amazon, Kubernetes for Google, etc.)

### After the Interview
- [ ] Send thank-you notes to interviewers
- [ ] Document questions you received for future preparation
- [ ] Note areas where you struggled and need more practice
- [ ] Follow up with recruiter on timeline for next steps
- [ ] Continue practicing for other interviews in the pipeline

## Appendix D: Essential Architecture Reading List

### Books
1. *Designing Data-Intensive Applications* — Martin Kleppmann (mandatory)
2. *Building Microservices* — Sam Newman (mandatory)
3. *Clean Architecture* — Robert C. Martin (recommended)
4. *Domain-Driven Design* — Eric Evans (for DDD roles)
5. *System Design Interview* — Alex Xu (practice problems)
6. *Software Architecture: The Hard Parts* — Neal Ford (advanced trade-offs)
7. *Fundamentals of Software Architecture* — Mark Richards (breadth)
8. *The Art of Scalability* — Martin Abbott (scalability patterns)

### Engineering Blogs by Company
- Google AI Blog (ai.googleblog.com)
- AWS Architecture Blog (aws.amazon.com/architecture)
- Netflix Tech Blog (netflixtechblog.com)
- Uber Engineering Blog (eng.uber.com)
- Stripe Blog (stripe.com/blog/engineering)
- Meta Engineering (engineering.fb.com)
- Microsoft Engineering (devblogs.microsoft.com)
- Spotify Engineering (engineering.atspotify.com)
- LinkedIn Engineering (engineering.linkedin.com)

### Online Courses
- Grokking the System Design Interview (DesignGurus)
- System Design Primer (GitHub — donnemartin)
- Distributed Systems for Practitioners (Udacity)
- Cloud Architecture on AWS/Azure/GCP (A Cloud Guru)

---

*This guide is part of the Architecture Academy interview preparation suite. Combine with per-company guides and per-lab mock interviews for comprehensive preparation.*
