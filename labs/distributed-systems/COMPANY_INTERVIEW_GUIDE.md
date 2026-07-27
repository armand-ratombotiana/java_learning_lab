# Company Interview Guide: Distributed Systems Roles

> Complete interview process breakdown for distributed systems engineering roles at top tech companies.

---

## Table of Contents
1. [Google (SRE + SWE)](#google-sre--swe)
2. [Amazon (Systems + SDE)](#amazon-systems--sde)
3. [Meta (Production Engineer)](#meta-production-engineer)
4. [Microsoft (Azure)](#microsoft-azure)
5. [Apple (iCloud)](#apple-icloud)
6. [Netflix (Cloud)](#netflix-cloud)
7. [Uber (Infrastructure)](#uber-infrastructure)
8. [Stripe (Infrastructure)](#stripe-infrastructure)
9. [LinkedIn (Infrastructure)](#linkedin-infrastructure)
10. [Confluent (Kafka)](#confluent-kafka)
11. [Databricks (Spark)](#databricks-spark)

---

## Google (SRE + SWE)

### Interview Process

**SWE Track (5 rounds + 1 phone screen)**
1. **Phone Screen** (45 min): Coding + basic system design
2. **Coding Round 1** (45 min): Algorithms, data structures
3. **Coding Round 2** (45 min): More complex algorithms
4. **System Design** (45 min): Distributed system design
5. **Googleyness** (45 min): Leadership, ambiguity, culture
6. **Additional Round**: Could be another coding or design depending on level

**SRE Track (6 rounds)**
1. **Phone Screen**: Coding + Linux troubleshooting
2. **Coding**: Algorithms + scripting
3. **System Design**: Infrastructure design, not product design
4. **Debugging/Incident Response**: Debug a broken system
5. **SRE Technical**: Automation, monitoring, capacity planning
6. **Googleyness**: Leadership principles

### Timeline
- **Recruiter Reachout**: Day 0
- **Phone Screen**: Week 1-2
- **Onsite/Virtual**: Week 3-6
- **Hiring Committee**: Week 6-8
- **Offer**: Week 8-10

### Expectations for Distributed Systems Roles
- Deep knowledge of at least 2 Google systems (GFS, Bigtable, Spanner, Borg)
- Can explain Paxos at protocol level
- Understands tradeoffs between consistency models
- Experience operating systems at scale (production experience valued over academic knowledge for SRE)

### Real Interview Story: Google SWE L5
> "The phone screen was a standard LeetCode medium (Find all anagrams in a string). The onsite had two coding rounds (LRU Cache + Word Search II), system design (Design a globally distributed key-value store), and Googleyness (Describe a time you resolved a technical disagreement). For the system design, the interviewer pressed hard on how I would handle cross-region replication consistency, which led to a detailed discussion of Spanner's TrueTime and Paxos."

### Compensation (2024-2025, USD)
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| L3 (SWE II) | $120-140k | $25k | $150-200k | $185-240k |
| L4 (SWE III) | $150-180k | $35k | $250-400k | $250-350k |
| L5 (Senior) | $180-220k | $50k | $500-800k | $350-500k |
| L6 (Staff) | $220-280k | $75k | $800k-1.2M | $500-700k |

---

## Amazon (Systems + SDE)

### Interview Process

**SDE Track (4-5 rounds)**
1. **Phone Screen** (60 min): Coding + Leadership Principles
2. **Coding Round** (60 min): Algorithms, OOD
3. **System Design** (60 min): Large-scale system design
4. **Bar Raiser** (60 min): Leadership Principles deep dive
5. **Manager Round** (45 min): Team fit, scope, career

**Systems Engineer Track**
1. **Phone Screen**: Linux troubleshooting, networking, scripting
2. **Systems Design**: Infrastructure design for AWS services
3. **Troubleshooting**: Debugging distributed systems
4. **Bar Raiser**: Leadership + deep technical
5. **Manager Round**: Team alignment

### Timeline
- **Application → Phone Screen**: 1-3 weeks
- **Phone → Onsite**: 2-4 weeks
- **Onsite → Decision**: 3-5 business days (fast!)
- **Offer → Start**: 3-6 weeks

### Expectations for Distributed Systems Roles
- Leadership Principles embedded in every answer
- DynamoDB internals (consistent hashing, gossip, hinted handoff, Merkle trees)
- AWS service architecture familiarity
- Scale awareness (millions of customers, billions of requests)
- "Bias for Action" - pragmatic design decisions

### Real Interview Story: Amazon SDE L6
> "My phone screen was a straightforward Tree problem (Lowest Common Ancestor) but the interviewer spent the last 20 minutes digging into a DynamoDB design decision. The onsite system design was 'Design Amazon's Recommendation Engine' - they wanted to see me reason about batch vs real-time, handle the cold start problem, and discuss A/B testing at scale. The Bar Raiser focused on 'Have Backbone' - I had to disagree with the interviewer on a design choice and defend my position."

### Compensation
| Level | Base | Bonus (Y1/Y2) | RSU (5yr) | TC Range |
|-------|------|---------------|-----------|----------|
| L4 (SDE I) | $120-150k | $30k/25k | $100-200k | $180-250k |
| L5 (SDE II) | $150-190k | $50k/40k | $200-400k | $250-400k |
| L6 (SDE III) | $180-240k | $75k/60k | $400-800k | $400-600k |
| L7 (Principal) | $220-300k | $100k | $800k-1.5M | $600-900k |

---

## Meta (Production Engineer)

### Interview Process

**Production Engineer (5 rounds)**
1. **Phone Screen** (45 min): Coding + Systems
2. **Coding** (45 min): Algorithms
3. **Systems** (45 min): Linux, networking, distributed systems
4. **System Design** (45 min): Large-scale infrastructure
5. **Behavioral** (45 min): Meta culture, impact

**SWE Infrastructure (6 rounds)**
1-2. **Coding** (2 x 45 min)
3. **System Design** (45 min)
4. **Distributed Systems** (45 min) - specific deep dive
5. **Behavioral** (45 min)
6. **Additional** based on level

### Timeline
- **Recruiter Screen**: Week 1
- **Phone Interview**: Week 2-3
- **Onsite**: Week 4-6
- **Deboarding Committee**: Week 6-7
- **Offer**: Week 7-8

### Expectations for Distributed Systems Roles
- Know Facebook's stack: TAO, Haystack, Presto, Scuba
- Scaling from millions to billions
- Real-time systems experience
- "Move Fast" - pragmatic engineering decisions
- Deep understanding of graph-based storage systems

### Real Interview Story: Meta PE (Production Engineer)
> "The phone screen asked me to implement a rate limiter. The systems round focused on debugging a kernel panic in production. The system design was 'Design Facebook's Photo Storage' - I explained Haystack in detail. The behavioral was intense - they asked 10 questions in 45 minutes about how I've handled incidents, conflicts, and failures."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| E3/IC3 | $120-140k | $15k | $100-180k | $165-230k |
| E4/IC4 | $150-180k | $25k | $200-350k | $250-350k |
| E5/IC5 | $180-220k | $40k | $400-700k | $350-500k |
| E6/IC6 | $220-280k | $60k | $700k-1.2M | $500-750k |

---

## Microsoft (Azure)

### Interview Process

**Azure SDE (4-5 rounds)**
1. **Phone Screen** (45 min): Coding
2. **Coding/Design** (60 min): Algorithms or system design
3. **System Design** (60 min): Azure-scale design
4. **Behavioral** (45 min): Microsoft culture, growth mindset
5. **Manager/Apr** (45 min): Team alignment

**Azure Infrastructure**
1. **Phone**: Distributed systems + networking
2. **Systems Design**: Azure storage/compute design
3. **Coding**: Go/C#/Java
4. **Behavioral**: Growth mindset, collaboration
5. **Deep dive**: Specific component (Azure SQL, Cosmos DB)

### Timeline
- **Apply → Interview**: 2-4 weeks (slower than Amazon)
- **Phone → Onsite**: 3-5 weeks
- **Decision**: 1-2 weeks
- **Offer → Start**: 3-6 weeks

### Expectations for Distributed Systems Roles
- Deep Azure platform knowledge (Storage, Compute, Networking)
- Understanding of consistency models (Cosmos DB levels)
- Experience with large-scale data systems
- Design for enterprise (security, compliance, SLAs)
- "Growth Mindset" - willingness to learn new technologies

### Real Interview Story: Azure SDE II
> "The phone screen was 'Design a thread-safe LRU cache' in C#. The system design was 'Design Azure Cosmos DB' - they expected deep knowledge of multi-master replication, consistency levels, and partitioning. Behavioral was 'Describe a time you took a risk and failed' - they wanted to see growth mindset and how I learned from it."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| L59/60 | $110-140k | $20k | $100-180k | $160-230k |
| L61/62 | $140-170k | $30k | $180-300k | $230-330k |
| L63/64 | $170-210k | $40k | $300-600k | $330-480k |
| L65/66 | $200-260k | $60k | $600k-1M | $450-650k |

---

## Apple (iCloud)

### Interview Process

**iCloud/iOS Infrastructure (6-7 rounds)**
1. **Recruiter Screen** (30 min): Background, role fit
2. **Phone Screen** (60 min): Coding + distributed systems
3. **Coding** (60 min): Algorithms in Swift/Obj-C/Java
4. **System Design** (60 min): Cloud service design
5. **Security** (45 min): Distributed security, encryption
6. **Behavioral** (45 min): Apple culture, quality focus
7. **Manager/Hiring** (30 min): Team overview

### Timeline
- **Recruiter → Phone**: 1-2 weeks
- **Phone → Onsite**: 3-6 weeks
- **Onsite → Decision**: 1-2 weeks (slower for Apple)
- **Offer → Start**: 2-4 weeks

### Expectations for Distributed Systems Roles
- End-to-end encryption understanding
- Privacy-first system design
- Apple ecosystem integration (iCloud, iOS, macOS)
- High reliability + low latency requirements
- "Quality" - attention to detail in design

### Real Interview Story: iCloud Engineer
> "The phone screen was a distributed caching problem. The onsite system design was 'Design iCloud Keychain sync' - the focus was on encryption, conflict resolution, and device-to-device sync. They asked about CRDTs for conflict resolution. Security round was intense - they wanted to understand how I'd design a system that even Apple couldn't read user data."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| ICT3 | $120-150k | $15k | $100-200k | $170-260k |
| ICT4 | $150-190k | $25k | $200-400k | $250-400k |
| ICT5 | $190-240k | $40k | $400-800k | $400-600k |
| ICT6 | $230-300k | $60k | $800k-1.5M | $550-800k |

---

## Netflix (Cloud)

### Interview Process

**Cloud Infrastructure (5-6 rounds)**
1. **Recruiter Screen** (30 min): Role fit, compensation
2. **Technical Phone** (45-60 min): Coding + systems
3. **System Design 1** (60 min): Large-scale infrastructure
4. **System Design 2** (60 min): Chaos engineering focus
5. **Behavioral/Culture** (60 min): Freedom & Responsibility
6. **Hiring Manager** (45 min): Team alignment

### Timeline
- **Recruiter → Phone**: 1 week (fast)
- **Phone → Onsite**: 2-3 weeks
- **Decision**: 1 week
- **Offer → Start**: 2-4 weeks

### Expectations for Distributed Systems Roles
- Deep cloud infrastructure knowledge (AWS)
- Chaos engineering experience
- Microservices architecture at scale
- "Freedom and Responsibility" culture fit
- Willingness to take bold technical decisions

### Real Interview Story: Netflix Cloud Engineer
> "The phone screen was 'Design a distributed counter' - they wanted to see how I'd handle the billions-like count problem. System design 1 was 'Design Open Connect CDN'. System design 2 was 'Design a chaos engineering platform' - they wanted me to design Chaos Monkey. The behavioral was the hardest - they asked about times I'd made controversial decisions and how I'd handled failures."

### Compensation
| Level | Base | Bonus (cash) | RSU (4yr) | TC Range |
|-------|------|-------------|-----------|----------|
| IC3 | $150-200k | 0 (all cash) | $150-350k | $200-350k |
| IC4 | $200-300k | 0 (all cash) | $350-700k | $350-600k |
| IC5 | $250-400k | 0 (all cash) | $700k-1.5M | $500-800k |
| IC6 | $350-500k | 0 (all cash) | $1.5M-3M | $700k-1.2M |

---

## Uber (Infrastructure)

### Interview Process

**Infrastructure Engineer (5 rounds)**
1. **Recruiter Screen** (30 min)
2. **Coding** (45 min): Algorithms
3. **System Design** (60 min): Distributed infrastructure design
4. **Domain Specific** (45 min): Kafka, Spark, data infrastructure
5. **Behavioral** (45 min): Uber culture, customer obsession
6. **Manager** (30 min): Team alignment

### Timeline
- **Recruiter → Phone**: 1-2 weeks
- **Phone → Onsite**: 2-4 weeks
- **Decision**: 1 week
- **Offer → Start**: 2-4 weeks

### Expectations
- Real-time data processing expertise
- H3 geospatial understanding
- Distributed state management (Schemaless, Ringpop)
- Kafka + Spark experience
- "Customer Obsession" - reliability focus

### Real Interview Story
> "System design was 'Design Uber's real-time pricing engine' - they wanted to see how I'd handle supply-demand curves, surge pricing triggers, and marketplace efficiency. The domain round was about 'Design a fault-tolerant job scheduler' - they wanted to understand how Peloton works."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| L3a | $120-140k | $15k | $100-200k | $160-230k |
| L4a | $140-170k | $25k | $200-350k | $230-330k |
| L5a | $170-210k | $40k | $350-700k | $330-500k |
| L6a | $200-260k | $60k | $700k-1.2M | $500-750k |

---

## Stripe (Infrastructure)

### Interview Process

**Infrastructure Engineer (5 rounds)**
1. **Recruiter Screen** (30 min)
2. **Coding** (60 min): Algorithms + design
3. **System Design** (60 min): Payment infrastructure design
4. **Debugging** (45 min): Debug distributed payment system
5. **Behavioral** (45 min): Stripe culture, craft
6. **Manager** (30 min): Team fit

### Timeline
- Fast process: 2-3 weeks total
- Stripe is known for efficient hiring
- Decision within 1 week typically

### Expectations
- Idempotency, retry mechanics, exactly-once processing
- Payment domain knowledge (ledger, reconciliation)
- Multi-currency, multi-provider payment architecture
- Financial correctness (no data loss allowed)
- "Craft" - code quality + system design elegance

### Real Interview Story
> "System design was 'Design Stripe's payment processing system' - they wanted to see how I'd design the idempotency layer, payment state machine, and ledger. The debugging round was the most unique - they gave me a broken payment pipeline in code and asked me to find the race condition."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| L2 | $130-160k | $20k | $150-250k | $200-300k |
| L3 | $160-200k | $30k | $250-500k | $300-450k |
| L4 | $200-260k | $50k | $500-900k | $450-650k |
| L5 | $250-350k | $75k | $900k-1.5M | $600-900k |

---

## LinkedIn (Infrastructure)

### Interview Process

**Infrastructure Engineer (5 rounds)**
1. **Recruiter Screen** (30 min)
2. **Coding** (45 min): Algorithms
3. **System Design** (60 min): LinkedIn-scale design
4. **Domain** (45 min): Kafka, distributed storage
5. **Behavioral** (45 min): LinkedIn culture
6. **Manager** (30 min)

### Timeline
- 3-5 weeks typical
- Decision in 1 week

### Expectations
- Kafka ecosystem proficiency (MirrorMaker, Kafka Connect)
- Social graph understanding (LIquid, TAO-like systems)
- Search infrastructure (Galene, Lucene)
- Real-time personalization
- "Professional" - enterprise reliability focus

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| Senior | $160-200k | $30k | $300-500k | $300-450k |
| Staff | $200-260k | $50k | $500-900k | $450-650k |
| Senior Staff | $250-320k | $75k | $900k-1.5M | $600-850k |

---

## Confluent (Kafka)

### Interview Process

**Kafka Infrastructure (5 rounds)**
1. **Recruiter** (30 min)
2. **Coding** (45 min): Java/Scala algorithms
3. **System Design 1** (60 min): Kafka infrastructure design
4. **System Design 2** (60 min): Stream processing design
5. **Behavioral** (45 min): Open source culture
6. **Kafka Deep Dive** (45 min): Protocol-level Kafka knowledge

### Timeline
- 2-3 weeks
- Confluent moves fast for strong Kafka candidates

### Expectations
- Kafka internals: partition rebalancing, ISR, leader election, log compaction
- Kafka Streams / ksqlDB: state stores, exactly-once, queryable state
- KRaft (Kafka without ZooKeeper): Raft consensus
- Schema Registry: Avro/Protobuf, compatibility
- Open source contribution experience preferred

### Real Interview Story
> "System design 1 was 'Design a multi-cluster Kafka replication system'. System design 2 was 'Design exactly-once semantics in a stream processing system'. They wanted protocol-level understanding of transaction coordinators, producer ID assignment, and commit markers. The deep dive round asked me to trace a message through Kafka's full lifecycle - from producer to broker ISR replication to consumer."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| IC3 | $130-160k | $15k | $150-250k | $200-290k |
| IC4 | $160-200k | $25k | $250-500k | $300-450k |
| IC5 | $200-260k | $40k | $500-900k | $450-650k |
| IC6 | $250-350k | $60k | $900k-1.5M | $600-900k |

---

## Databricks (Spark)

### Interview Process

**Infrastructure/Software Engineer (6 rounds)**
1. **Recruiter Screen** (30 min)
2. **Coding Round 1** (45 min): Algorithms in Scala/Python/Java
3. **Coding Round 2** (45 min): Spark-related distributed coding
4. **System Design** (60 min): Data infrastructure design
5. **Distributed Systems Deep Dive** (45 min): Spark execution
6. **Behavioral** (45 min): Databricks culture
7. **Manager** (30 min)

### Timeline
- 2-4 weeks
- May include a take-home challenge

### Expectations
- Spark internals: DAG scheduler, Catalyst, Tungsten, AQE
- Delta Lake: ACID on data lake, time travel, schema enforcement
- ML infrastructure: MLflow, feature stores
- Distributed computing deep knowledge
- Scala/Python/R proficiency

### Real Interview Story
> "Coding round 2 was 'Implement a distributed word count in Spark' - they wanted to see if I understood how partitions, shuffles, and stages work. System design was 'Design a multi-tenant Spark cluster manager' - resource isolation, dynamic allocation, preemption. The deep dive asked me to 'Trace through what happens when a Spark job is submitted' - from SQL parsing to DAG planning to stage scheduling to task execution."

### Compensation
| Level | Base | Bonus | RSU (4yr) | TC Range |
|-------|------|-------|-----------|----------|
| IC3 | $130-160k | $15k | $150-250k | $200-290k |
| IC4 | $160-200k | $25k | $300-500k | $300-450k |
| IC5 | $200-270k | $40k | $500-900k | $450-650k |
| IC6 | $250-350k | $60k | $900k-1.5M | $600-900k |

---

## Interview Preparation Summary

### Must-Know Distributed Systems Papers
1. **Dynamo**: Amazon's Highly Available Key-value Store
2. **Bigtable**: A Distributed Storage System for Structured Data
3. **Spanner**: Google's Globally-Distributed Database
4. **The Google File System**
5. **Kafka: A Distributed Messaging System**
6. **Raft: In Search of an Understandable Consensus Algorithm**
7. **Paxos Made Simple**
8. **TAO: Facebook's Distributed Data Store for Social Graph**
9. **Resilience in Netflix: Chaos Engineering**
10. **Delta Lake: High-Performance ACID Table Storage**

### Common Themes Across All Companies
1. **CAP Theorem understanding with real-world examples** (every company)
2. **Consistency vs Availability vs Latency tradeoffs** (every company)
3. **Failure handling**: Partition tolerance, retry, idempotency (every company)
4. **Scale estimation**: Ability to compute QPS, storage, bandwidth (most companies)
5. **System design**: Pick 2-3 patterns and apply them (most companies)
6. **Production experience**: Operating distributed systems (expected at senior levels)

### Tips for Each Role Level

**New Grad / L3-4 / Junior**
- Focus on DS fundamentals: CAP, replication, partitioning
- Code fluency in one language
- Basic system design for single-service problems
- Show potential and learning ability

**Mid-Level / L5 / Senior**
- Deep experience with 1-2 distributed systems in production
- Can design moderately complex distributed systems
- Explains tradeoffs confidently
- Shows operational maturity (monitoring, deployment, rollback)

**Senior / L6-7 / Staff**
- Multiple distributed system designs built and operated
- Can resolve design disagreements and drive decisions
- Understands organizational impact of system choices
- Mentorship and technical leadership

---

## Quick Reference: Company → Focus

| Company | Primary Focus | Key Tech | Interview Emphasis |
|---------|--------------|----------|-------------------|
| Google | Global infrastructure | Spanner, Borg, GFS | Consensus, consistency |
| Amazon | Cloud services | DynamoDB, S3, Lambda | Partitioning, cost |
| Meta | Social graph | TAO, Haystack, Presto | Caching, graph stores |
| Microsoft | Enterprise cloud | Cosmos DB, Azure SQL | Replication, security |
| Apple | User devices + cloud | iCloud, CloudKit | Privacy, CRDTs |
| Netflix | Streaming infra | Open Connect, Eureka | CDN, chaos engineering |
| Uber | Real-time dispatch | H3, Ringpop, Kafka | Geospatial, streaming |
| Stripe | Financial infra | Payment processors | Idempotency, ledgers |
| LinkedIn | Professional network | Kafka, Espresso, Galene | Graph, search |
| Confluent | Event streaming | Apache Kafka | Stream processing |
| Databricks | Data + AI | Apache Spark, Delta | Data infrastructure |

---

> **Strategy**: Pick 2-3 target companies. Deep dive into their specific distributed systems architecture. Practice their unique interview style. Tailor your stories to their culture.