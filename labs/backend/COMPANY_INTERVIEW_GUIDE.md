# Company Interview Guide — Wave 6

> Target: 300+ lines covering interview processes, Spring/Java depth, system design focus, and compensation for top tech companies hiring for Spring/Microservices/EDA roles

---

## 1. Google (Cloud Java, Backend Infrastructure)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Phone Screen | Coding (DSA) | 45 min |
| 2 | Phone Screen | System Design or Backend | 45 min |
| 3-4 | Onsite: Coding | Data structures & algorithms | 45 min each |
| 5 | Onsite: System Design | Scalable distributed system | 45 min |
| 6 | Onsite: Googleyness | Leadership, cultural fit | 45 min |
| 7 | Onsite: Java/Spring Depth (for backend roles) | Spring Framework, DI, AOP, Testing | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Example Question |
|-------|-------|-----------------|
| **IoC/DI** | Deep | "Explain bean lifecycle, circular deps, proxy modes" |
| **Concurrency** | Very deep | "Virtual threads vs platform threads, ForkJoinPool" |
| **JVM Internals** | Deep | "Garbage collection tuning, heap analysis, JIT compilation" |
| **Testing** | High | "How to test at scale, fakes vs mocks vs stubs" |
| **Spring Boot** | Medium | Auto-configuration understanding |
| **Kubernetes** | High | "How would you deploy Spring Boot on GKE?" |

### System Design Focus

Google emphasizes:
- **Scale**: Design systems for Google-level traffic (billions of users)
- **Consistency Models**: Strong vs Eventual consistency trade-offs
- **Distributed Systems**: Paxos/Raft, leader election, distributed consensus
- **Storage**: Bigtable, Spanner, Colossus (file system)
- **Example questions**: "Design Google Docs", "Design YouTube", "Design Google Search"

### Compensation (2025-2026, US)

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| L3 (SWE II) | $150k-$200k | Fresh grad |
| L4 (SWE III) | $200k-$350k | 2-5 years |
| L5 (Senior) | $350k-$550k | 5-10 years |
| L6 (Staff) | $500k-$800k | 10+ years |

### Key Preparation Tips

- **Practice LeetCode** — Google is the most DSA-heavy of all big tech
- **Understand Google's internal tech**: Borg, Omega, Pregel, Dremel
- **API design**: Protocol Buffers, gRPC, strict versioning
- **Spring is secondary to system design** but still tested for Java roles
- **Leadership (Googleyness)**: Ambiguity, collaboration, intellectual humility

---

## 2. Amazon (SDE, AWS SDK/Spring Integration)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Online Assessment | Coding (2 problems) | 90 min |
| 2-3 | Phone Screen | Coding + LP (Leadership Principles) | 60 min |
| 4-6 | Onsite: Coding | 2 coding rounds (DSA) | 45 min each |
| 7 | Onsite: System Design | Distributed system design | 60 min |
| 8 | Onsite: Bar Raiser | LP deep dive + System Design | 60 min |

### Leadership Principles (LP) — Tested in EVERY round

Amazon expects 2-3 LPs per answer using the **STAR method** (Situation, Task, Action, Result).

| LP | What They Look For |
|----|-------------------|
| **Customer Obsession** | "Start with the customer and work backwards" |
| **Bias for Action** | "Speed matters in business" |
| **Ownership** | "You build it, you run it" |
| **Learn and Be Curious** | Deep dive into new technologies |
| **Deliver Results** | "Done" is better than "perfect" |
| **Dive Deep** | Go to root cause, don't stop at surface |

### Spring/Java Depth Expected

| Topic | Depth | Example Question |
|-------|-------|-----------------|
| **Spring Boot** | Practical | "How do you configure DataSource, override auto-config?" |
| **Transactional** | Very deep | "Propagation, isolation, pitfalls, self-invoke" |
| **Microservices** | Very deep | "Saga patterns, circuit breakers, service discovery" |
| **Docker/K8s** | High | "How to containerize Spring Boot, health checks" |
| **Caching** | Practical | "When to use Redis, how to invalidate cache" |
| **Database** | Deep | "Sharding, replication, read replicas, optimistic locking" |

### System Design Focus

Amazon asks realistic system design:
- "Design Amazon's order processing system"
- "Design a product catalog search"
- "Design a recommendation engine"
- "Design a distributed logging system"
- "Design a rate limiter"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| L4 (SDE I) | $130k-$180k | Fresh grad |
| L5 (SDE II) | $200k-$350k | 2-5 years |
| L6 (SDE III) | $350k-$500k | 5-8 years |
| L7 (Principal) | $500k-$800k | 10+ years |

### Key Preparation Tips

- **Interview is very LP-heavy** — prepare 3-4 stories per LP
- **STAR format is mandatory** — practice verbal delivery
- **AWS knowledge helps** — S3, DynamoDB, Lambda, API Gateway
- **You build it, you run it** — expect questions about operations (on-call, troubleshooting)
- **Amazon-specific**: PR/FAQ writing is a unique internal practice

---

## 3. Meta (Backends at Scale)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1-2 | Phone Screen | Coding (LeetCode medium/hard) | 45 min |
| 3-4 | Onsite: Coding | 2 coding rounds | 45 min each |
| 5 | Onsite: System Design | Distributed system | 45 min |
| 6 | Onsite: Behavioral | Leadership, product sense | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Java** | Deep | Concurrency, memory model, performance |
| **Spring** | Low-Medium | Meta doesn't use Spring extensively (uses Hack/PHP, Python, C++) |
| **Microservices** | High | Internal RPC frameworks (Thrift, gRPC) |
| **Distributed Systems** | Very deep | Consistency, consensus, replication |

### System Design Focus

- "Design Facebook News Feed"
- "Design Messenger/Chat system"
- "Design a real-time notification system"
- "Design a distributed cache"
- "Design a graph database (like TAO)"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| E3 | $150k-$200k | Fresh grad |
| E4 | $200k-$350k | 2-4 years |
| E5 | $350k-$550k | 5-7 years |
| E6 | $500k-$800k | 8+ years |

### Key Preparation Tips

- **DSA is king** — Meta is the most LeetCode-heavy interview
- **Spring knowledge is not required** for Meta Java roles
- **Focus on system design at scale**
- **Behavioral**: "What's the hardest technical challenge you faced?"

---

## 4. Microsoft (Azure Spring Apps, Microservices on Azure)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Phone Screen | Coding + experience | 45 min |
| 2-3 | Tech Screen | Design + Coding | 60 min |
| 4-5 | Onsite: Coding | 2 rounds | 45 min each |
| 6 | Onsite: System Design | Azure-focused design | 60 min |
| 7 | Onsite: Behavioral/ASAP | Leadership | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Example Question |
|-------|-------|-----------------|
| **Spring Boot on Azure** | Very deep | "How to deploy Spring Boot to Azure Spring Apps?" |
| **Spring Cloud** | High | "Config Server, Service Discovery with Azure" |
| **Azure Services** | High | "Cosmos DB, Service Bus, Event Hubs, SQL Database" |
| **Identity** | High | "OAuth2, Azure AD, Managed Identities in Spring" |

### System Design Focus

- "Design a distributed database (Cosmos DB-like)"
- "Design a message queue service (Service Bus-like)"
- "Design a CI/CD pipeline for microservices on Azure"
- "Design a cloud-native monitoring system"
- Azure-specific services and their trade-offs

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| 59/60 | $120k-$180k | Fresh grad |
| 61/62 | $180k-$280k | 2-5 years |
| 63/64 | $280k-$400k | 5-8 years |
| 65+ | $400k-$600k | 10+ years |

### Key Preparation Tips

- **Azure Spring Apps knowledge** is a huge differentiator
- **Understand Cosmos DB** (consistency levels, partitioning)
- **Microservices patterns** on Azure (Service Fabric, AKS, Spring Cloud)
- **System design questions are very practical**, not Google-scale

---

## 5. Netflix (Cloud Platform, Spring Cloud Netflix)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Recruiter Screen | Experience + expectations | 30 min |
| 2 | Tech Screen (Hiring Manager) | System Design + Architecture | 60 min |
| 3-4 | Onsite: Coding | Problem-solving, not DSA puzzles | 45 min each |
| 5 | Onsite: System Design | Deep dive | 60 min |
| 6 | Onsite: Culture/Leadership | Freedom & Responsibility | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Spring Boot** | Expert level | Netflix invented Spring Cloud ecosystem |
| **Reactive** | Very deep | WebFlux, Reactor, project Reactor internals |
| **Chaos Engineering** | Very deep | They literally invented Chaos Monkey |
| **Microservices** | Expert level | Original practitioners |
| **Hystrix/Resilience4j** | Deep | Know why Hystrix was deprecated |

### System Design Focus

- "Design Netflix's content delivery network"
- "Design a failover system across AWS regions"
- "Design a chaos engineering platform"
- "Design a recommendation pipeline"
- "Design a video transcoding pipeline"
- "Design a service mesh sidecar"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| Mid | $250k-$400k | 3-5 years |
| Senior | $400k-$600k | 5-8 years |
| Staff | $600k-$900k | 8+ years |

### Key Preparation Tips

- **Netflix values pragmatism** — they want engineers who can make trade-offs
- **"Freedom & Responsibility"** culture: you own your code end-to-end
- **Chaos engineering** domain knowledge is a big plus
- **Spring Cloud Gateway** over Zuul (know the migration story)
- **Coding rounds are practical** (not LeetCode puzzles) — refactoring, API design

---

## 6. Spotify (Backend Services, Event-Driven)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Recruiter Screen | Background | 30 min |
| 2 | Coding (Take-home or Live) | Problem-solving | 60-90 min |
| 3 | System Design | Architecture | 60 min |
| 4 | Deep Dive (Technical) | Past projects, decisions | 60 min |
| 5 | Cultural Fit | Band values | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Java** | High | Primary backend language |
| **Event-Driven** | Very deep | Kafka-based architecture |
| **Backend for Frontend (BFF)** | High | BFF pattern per client type |
| **Testing** | High | TDD, testing in production |

### System Design Focus

- "Design a music streaming service"
- "Design a real-time collaborative playlist"
- "Design a recommendation engine"
- "Design an event pipeline for user activity"
- "Design a feature toggle system"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| Mid | $180k-$250k | 3-5 years |
| Senior | $250k-$350k | 5-8 years |
| Staff | $350k-$500k | 8+ years |

### Key Preparation Tips

- **Less LeetCode** than FAANG, more real-world problem solving
- **Event-driven architecture** knowledge is critical
- **Spotify's squad model** — autonomy, alignment, trust
- **Kafka** is core to their infrastructure
- **Testing philosophy**: test in production with gradual rollout

---

## 7. Uber (Microservices at Massive Scale)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Phone Screen | Coding | 45 min |
| 2 | Phone Screen | System Design | 45 min |
| 3-4 | Onsite: Coding | 2 rounds (medium/hard DSA) | 45 min |
| 5 | Onsite: System Design | Deep distributed systems | 60 min |
| 6 | Onsite: Behavioral/Experience | Past projects | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Spring Boot** | High | Major Spring user |
| **Distributed Systems** | Very deep | Geospatial indexing, real-time pricing |
| **Kafka** | Very deep | Core event backbone |
| **gRPC/Thrift** | High | RPC frameworks |
| **Docker/K8s** | High | Peloton (Uber's K8s) |

### System Design Focus

- "Design Uber's ride-matching system"
- "Design a real-time pricing engine (surge pricing)"
- "Design Uber Eats ordering system"
- "Design a geospatial index for location-based queries"
- "Design a distributed rate limiter"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| L3/G | $150k-$200k | Fresh grad |
| L4/G | $200k-$300k | 2-4 years |
| L5a/L5b | $300k-$450k | 4-7 years |
| L6 | $450k-$650k | 7+ years |

### Key Preparation Tips

- **Geospatial indexing** (QuadTrees, S2, H3) is unique to Uber
- **System design is very quantitative** (capacity estimation, latency calculations)
- **DSA is important** but not as heavy as Google/Meta
- **Spring knowledge helps** but distributed systems knowledge matters more

---

## 8. Stripe (API-First Development)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Recruiter Screen | Experience | 30 min |
| 2 | Coding (Debugging/Implementation) | Real-world coding | 60 min |
| 3 | System Design | API design + scale | 60 min |
| 4 | API Design | REST/gRPC API design | 60 min |
| 5 | Behavioral/Manager | Culture, collaboration | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Spring Boot** | Medium | Stripe uses Java, but not Spring-heavy |
| **API Design** | Expert level | RESTful API design, versioning, idempotency |
| **Distributed Transactions** | Very deep | Payment processing, exactly-once semantics |
| **Java Concurrency** | High | Thread safety, performance |

### System Design Focus

- "Design a payment processing system"
- "Design an idempotent API"
- "Design a rate limiter for API endpoints"
- "Design a webhook delivery system"
- "Design a fraud detection pipeline"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| L2 | $150k-$200k | Fresh grad |
| L3 | $200k-$300k | 2-4 years |
| L4 | $300k-$450k | 4-7 years |
| L5 | $450k-$650k | 7+ years |

### Key Preparation Tips

- **API design is the most important skill** for Stripe interviews
- **Idempotency, exactly-once processing, retry semantics** — know deeply
- **Stripe values communication** — clear, written, async communication
- **Less LeetCode**, more practical coding: "implement a rate limiter", "parse this API spec"

---

## 9. Apple (Backend Services)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Recruiter Screen | Background | 30 min |
| 2 | Phone Screen | Coding (DSA + practical) | 45 min |
| 3-4 | Onsite: Coding | 2 rounds | 45 min each |
| 5 | Onsite: System Design | Architecture | 45 min |
| 6 | Onsite: Behavioral/Hiring Manager | Leadership, product | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Spring Boot** | Medium | Apple uses Java but not Spring-exclusive |
| **Java** | High | Concurrency, performance, JVM tuning |
| **Security** | Very high | iCloud, Authentication, Privacy by design |

### System Design Focus

- "Design iCloud sync system"
- "Design Apple Push Notification Service (APNs)"
- "Design a scalable file storage system"
- "Design a privacy-preserving analytics system"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| ICT3 | $150k-$200k | Fresh grad |
| ICT4 | $200k-$300k | 2-5 years |
| ICT5 | $300k-$450k | 5-8 years |
| ICT6 | $450k-$650k | 8+ years |

### Key Preparation Tips

- **Secrecy**: Apple interviews are less predictable
- **Security and privacy knowledge** are huge differentiators
- **System design is practical**, not Google-scale

---

## 10. Confluent (Kafka-Focused Roles)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Recruiter Screen | Kafka experience | 30 min |
| 2 | Tech Screen (Kafka) | Deep Kafka knowledge | 60 min |
| 3-4 | Onsite: Coding | Java/Kafka implementation | 45 min each |
| 5 | System Design | Kafka-based systems | 60 min |
| 6 | Behavioral | Open source culture | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Kafka** | Expert level | Internals, performance tuning, KRaft |
| **Spring for Kafka** | High | Spring Kafka, Kafka Streams |
| **Java Memory** | Deep | Off-heap memory, zero-copy, page cache |
| **Distributed Systems** | Expert level | Raft, replication log |

### System Design Focus

- "Design a distributed commit log"
- "Design an exactly-once processing pipeline"
- "Design a schema registry"
- "Design a Kafka Connect connector"
- "Design a stream processing platform"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| SWE II | $150k-$200k | |
| SWE III | $200k-$300k | |
| Senior | $300k-$450k | |
| Staff | $450k-$600k | |

### Key Preparation Tips

- **Kafka internals**: partition rebalancing, ISR, log compaction, exactly-once
- **Contributing to open source** is a big plus
- **Java performance**: memory management, GC tuning, off-heap buffers
- **Spring knowledge is secondary** to Kafka expertise

---

## 11. Pivotal / VMware (Spring Team Roles)

### Interview Process

| Round | Type | Focus | Duration |
|-------|------|-------|----------|
| 1 | Tech Screen | Spring Framework knowledge | 60 min |
| 2 | Pair Programming | TDD, Spring implementation | 90 min |
| 3 | System Design | Cloud-native architecture | 60 min |
| 4 | Behavioral / Team Fit | Agile, pairing, open source | 45 min |

### Spring/Java Depth Expected

| Topic | Depth | Notes |
|-------|-------|-------|
| **Spring Framework** | Expert level | You're joining the Spring team! |
| **TDD** | Expert level | Pair programming, strict TDD |
| **Reactive** | Very deep | Reactor, WebFlux, RSocket |
| **Auto-Configuration** | Expert level | Creating starters, conditions |
| **GraalVM / AOT** | Deep | Native image compilation |

### System Design Focus

- "Design a cloud-native auto-configuration framework"
- "Design a reactive microservices system"
- "Design a Spring Boot starter"
- "Design a test slice framework"
- "How would you improve Spring Boot's startup time?"

### Compensation

| Level | Total Comp (TC) | Notes |
|-------|----------------|-------|
| SWE | $150k-$220k | |
| Senior | $220k-$350k | |
| Staff | $350k-$500k | |

### Key Preparation Tips

- **Deep Spring source code knowledge** — read the framework source
- **TDD pairing** is the interview format — practice pair programming
- **Open source contributions** to Spring are highly valued
- **Know the Spring Boot 2.x → 3.x migration** intimately
- **Understand AOT processing** and GraalVM native-image

---

## Summary Table

| Company | DSA Weight | Spring Weight | System Design | Unique Focus |
|---------|-----------|--------------|---------------|--------------|
| **Google** | Very high | Medium | Very high | Scale, distributed consensus |
| **Amazon** | High | Very high | High | Leadership Principles, AWS |
| **Meta** | Very high | Low | High | Real-time, feed systems |
| **Microsoft** | Medium | Very high | High | Azure, identity |
| **Netflix** | Medium | Expert | Very high | Chaos engineering, resilience |
| **Spotify** | Low-Medium | High | High | Event-driven, BFF |
| **Uber** | Medium-High | High | Very high | Geospatial, real-time |
| **Stripe** | Low-Medium | Medium | High | API design, idempotency |
| **Apple** | High | Medium | High | Security, privacy |
| **Confluent** | Medium | Medium | Very high | Kafka internals |
| **Pivotal/VMware** | Low | Expert | Medium | TDD, Spring internals |

---

> **End of COMPANY_INTERVIEW_GUIDE.md**
> Total: 11 company profiles covering interview process, depth required, system design focus, compensation
