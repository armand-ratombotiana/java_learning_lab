# Backend Academy — Interview Preparation Guide

<div align="center">

![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Microservices](https://img.shields.io/badge/Microservices-FF6F00?style=for-the-badge&logo=google-cloud&logoColor=white)
![System Design](https://img.shields.io/badge/System_Design-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Land your dream backend engineering role with targeted Spring Boot & microservices preparation**

</div>

---

## Table of Contents

1. [Company-Specific Preparation](#company-specific-preparation)
2. [System Design Questions (Spring Boot Focus)](#system-design-questions-spring-boot-focus)
3. [Microservices Interview Patterns](#microservices-interview-patterns)
4. [Database Integration Interview Questions](#database-integration-interview-questions)
5. [Study Plans](#study-plans)
6. [Resources](#resources)

---

## Company-Specific Preparation

### Google — Distributed Systems, API Design, Microservices, gRPC

**Key Labs to Review:** Lab 22 (GraphQL DGS), Lab 23 (CQRS/Axon), Lab 16 (Spring Cloud)

**Focus Areas:**
- Distributed systems fundamentals: consistency models, CAP theorem, Paxos/Raft
- gRPC vs REST vs GraphQL — when to use each
- API design at scale: versioning, backward compatibility, deprecation strategies
- Microservices decomposition: domain-driven design, bounded contexts
- Lab 23 CQRS pattern: command/query separation, event sourcing, eventual consistency
- Lab 22 DGS framework: data loaders for N+1 prevention, federated GraphQL gateways

**Sample Interview Question:**
> "Design a distributed notification system that handles 10M+ events/day using Spring Boot."
> **Approach:** Discuss Kafka for event ingestion (Lab 07), Spring Cloud Stream for binding, WebSocket/SSE for real-time delivery (Lab 19), Redis for deduplication (Lab 13), and CQRS for separating write/read models (Lab 23).

**gRPC-Specific Topics:**
- Protocol Buffers schema design
- Unary, server streaming, client streaming, bidirectional streaming
- gRPC with Spring Boot (grpc-spring-boot-starter)
- Performance comparison: gRPC vs REST (up to 10x faster)
- How gRPC uses HTTP/2 multiplexing

**GraphQL (Meta) Topics:**
- Schema-first vs code-first approaches
- DataLoader pattern to batch queries (Lab 22 DGS)
- Federated GraphQL for microservices
- Subscription operations for real-time data

### Microsoft — Azure Spring Cloud, .NET Integration, Microservices Architecture

**Focus Areas:**
- Azure Spring Apps (formerly Azure Spring Cloud) — managed Spring Boot on Azure
- Azure Service Bus vs Kafka for messaging
- Azure SQL Database vs JPA/Hibernate
- .NET interop: REST APIs consumed by .NET clients, gRPC cross-platform
- Azure Kubernetes Service (AKS) for Spring Boot deployment
- Azure Cosmos DB for globally distributed data
- Azure Functions vs Spring Cloud Function for serverless

**Sample Interview Question:**
> "How would you migrate a monolithic Spring Boot app to Azure Spring Apps with microservices?"
> **Approach:** Decompose by bounded context, use Spring Cloud Config Server for configuration, Spring Cloud Service Registry (Eureka) for discovery, Azure Spring Apps for managed deployment, Azure Monitor for observability.

### Amazon (AWS) — ECS/EKS/Lambda, High-Scale REST APIs

**Focus Areas:**
- AWS ECS (Fargate) vs EKS (Kubernetes) for Spring Boot deployment
- AWS Lambda with Spring Cloud Function and SnapStart
- Amazon RDS (Aurora) vs DynamoDB for backend data
- Amazon ElastiCache (Redis) — Lab 13 caching patterns
- Amazon SQS/SNS for async messaging — Lab 07 patterns
- API Gateway + Spring Boot — throttling, caching, authentication
- Elastic Beanstalk vs ECS vs EKS decision framework

**Sample Interview Question:**
> "Design a high-scale REST API on AWS that handles 100K TPS with Spring Boot."
> **Approach:** Auto-scaling groups with ALB target groups, ElastiCache for hot data, DynamoDB for writes + Aurora for reads, CloudFront CDN, API Gateway throttling, Spring WebFlux for non-blocking I/O (Lab 15).

### Meta — GraphQL, API Performance, Backend for Mobile

**Focus Areas:**
- GraphQL schema design for mobile backend (Lab 22 DGS)
- API performance: response time optimization, pagination, batching
- DataLoader pattern for efficient batch loading
- GraphQL subscriptions for real-time mobile updates
- Connection pagination (Relay spec) for infinite scroll
- Backend for mobile: offline support, sync strategies, delta queries

**Sample Interview Question:**
> "Design a GraphQL API for a social media feed serving 500M users."
> **Approach:** DGS framework for schema-first, DataLoader for N+1 prevention, Redis caching for popular feeds, CQRS for write-heavy operations, pagination with cursors, subscription for live updates.

### Apple — Security, Privacy, Backend Performance at Scale

**Focus Areas:**
- Security-first design: encryption at rest and in transit (Lab 06, Lab 20)
- Privacy: data minimization, anonymization, differential privacy
- Backend performance: low-latency responses (Lab 24)
- Rate limiting, throttling, DDoS protection (Lab 20)
- End-to-end encryption considerations
- Data residency and localization for global deployment

**Sample Interview Question:**
> "How would you design a secure backend for Apple Health that handles 1B+ records?"
> **Approach:** End-to-end encryption, data minimization, differential privacy for analytics, schema-per-tenant multi-tenancy (Lab 21), horizontal sharding for scale (Databases Lab 13), secure enclaves for key management.

### Oracle — Helidon, MicroProfile, Oracle DB, Jakarta EE

**Focus Areas:**
- Helidon SE vs MP: lightweight microservices framework (Lab 09)
- MicroProfile: Config, Fault Tolerance, JWT, OpenAPI, Rest Client
- Oracle Database: advanced SQL, PL/SQL, Oracle-specific optimizations
- Jakarta EE vs Spring Boot comparison — architectural differences
- Oracle Cloud (OCI) for Spring Boot deployment
- Oracle Database 23c: JSON-relational duality views, AI Vector Search

**Jakarta EE vs Spring Boot Comparison Table:**

| Aspect | Spring Boot | Jakarta EE (Helidon/Quarkus) |
|--------|------------|------------------------------|
| DI Framework | Spring DI | CDI (Contexts & Dependency Injection) |
| Persistence | Spring Data JPA | JPA + Hibernate |
| REST | Spring MVC / WebFlux | JAX-RS / Jersey / RESTEasy |
| Security | Spring Security | Jakarta Security, JWT |
| Reactive | Project Reactor | MicroProfile Reactive Streams |
| Cloud | Spring Cloud | MicroProfile specs (Config, FT, Health) |
| Native | Spring Native / GraalVM | Built for GraalVM (Quarkus, Helidon) |

**Sample Interview Question:**
> "Compare Spring Boot and Helidon for building a microservice. When would you choose each?"
> **Approach:** Spring Boot for rich ecosystem and developer tooling; Helidon for low-memory environments, startup time critical apps, and MicroProfile standards compliance.

---

## System Design Questions That Test Spring Boot Knowledge

### Question 1: Design a URL Shortener (like Bitly) with Spring Boot

**Key Components:**
- `POST /shorten`: Create short URL → Service layer with hashing strategy
- `GET /{code}`: Redirect → Redis cache for hot URLs
- `GET /analytics/{code}`: Access stats → CQRS with Axon (Lab 23)
- Database: RDBMS (PostgreSQL) for mapping + Cassandra for analytics

**Spring Boot Specifics:**
- Rate limiting with Bucket4j (Lab 20)
- Circuit breaker for analytics service (Lab 16)
- Spring Cloud Gateway for routing (Lab 16)
- Caffeine/Redis caching (Lab 13)
- Async event publishing for click tracking

### Question 2: Design an E-Commerce Checkout System

**Key Components:**
- Cart service, Order service, Payment service, Inventory service
- Saga pattern for distributed transactions (Lab 23 Axon)
- Event-driven communication via Kafka (Lab 07)
- API Gateway: Spring Cloud Gateway (Lab 16)

**Spring Boot Specifics:**
- `@Transactional` for local transactions
- `@Retryable` for payment calls
- Outbox pattern with `@TransactionalEventListener`
- Axon Saga orchestrator for multi-step checkout

### Question 3: Design a Real-Time Chat Application

**Key Components:**
- WebSocket for real-time messaging (Lab 19 SSE, Lab 15 WebFlux)
- Message persistence with Cassandra
- Presence detection with Redis
- Message queues for offline delivery

**Spring Boot Specifics:**
- Spring WebSocket / RSocket
- Spring Data Cassandra for messaging
- Redis pub/sub for horizontal scaling
- Spring Security for authentication & authorization

### Question 4: Design a Notification System

**Key Components:**
- Notification preferences, templating, delivery (email, SMS, push)
- Kafka for event ingestion (Lab 07)
- Spring Cloud Stream for binding
- Retry and dead-letter queues

**Spring Boot Specifics:**
- Spring Mail for email delivery
- Twilio/SNS integration for SMS
- Firebase Cloud Messaging for push
- Resilience4J for external API calls

### Question 5: Design a Rate Limiting System

**Key Components:**
- Distributed rate limiter with Redis
- Token bucket vs sliding window algorithms
- Per-user, per-API, per-tier limiting

**Spring Boot Specifics:**
- Spring interceptor for rate limiting
- Redis Lua scripts for atomic operations
- Spring Cloud Gateway filter for distributed limiting

---

## Microservices Interview Patterns

### Decomposition Patterns
1. **Decompose by Business Capability:** Order Management, Inventory, Shipping
2. **Decompose by Subdomain:** DDD bounded contexts
3. **Strangler Fig Pattern:** Gradually replace monolith with microservices
4. **Feature Toggle:** Spring Cloud Feature Flags for gradual rollout

### Communication Patterns
1. **Synchronous (REST/gRPC):** Simple request-response, tight coupling
2. **Asynchronous (Messaging):** Event-driven (Spring Cloud Stream, Kafka)
3. **Event Sourcing:** Axon Framework (Lab 23)
4. **Saga Pattern:** Choreography vs Orchestration (Axon Saga)
5. **API Gateway:** Spring Cloud Gateway (Lab 16)
6. **Service Mesh:** Istio, Linkerd for cross-cutting concerns

### Reliability Patterns
1. **Circuit Breaker:** Resilience4J (Lab 16)
2. **Retry:** `@Retryable` with exponential backoff
3. **Timeout:** Configurable via `@Timeout`
4. **Bulkhead:** Thread pool isolation per service (Resilience4J)
5. **Cache-Aside:** Redis cache (Lab 13)

### Observability Patterns
1. **Distributed Tracing:** Spring Cloud Sleuth / Micrometer Tracing
2. **Centralized Logging:** ELK Stack (Elasticsearch, Logstash, Kibana)
3. **Metrics:** Micrometer + Prometheus + Grafana
4. **Health Checks:** Spring Actuator + readiness/liveness probes

### Data Patterns
1. **Database per Service:** Each microservice owns its data
2. **Shared Database:** Only for legacy monolith extraction
3. **Saga Pattern:** Distributed transaction management (Lab 23)
4. **CQRS:** Separate read/write models (Lab 23)
5. **Event Sourcing:** Event store as source of truth (Lab 23)

### Deployment Patterns
1. **Blue-Green Deployment:** Spring Cloud + K8s
2. **Canary Release:** Gradual traffic shifting
3. **Sidecar:** Spring Cloud Sidecar for polyglot services
4. **Service Mesh:** Istio integration with Spring Boot

---

## Database Integration Interview Questions

### JPA & Hibernate

**Basic:**
- What is the difference between `EAGER` and `LAZY` fetching?
- Explain the entity lifecycle: `new` → `managed` → `detached` → `removed`
- What is the N+1 query problem? How do you solve it? (`@EntityGraph`, `JOIN FETCH`)
- What are JPA cascade types? When would you use `CascadeType.ALL` vs `CascadeType.PERSIST`?

**Intermediate:**
- How does the first-level cache work? How is it different from second-level cache?
- What is a `PersistenceContext` and how does it relate to `EntityManager`?
- Explain optimistic vs pessimistic locking in JPA. When would you use each?
- What is the `@Version` annotation used for?
- How do you map inheritance hierarchies in JPA? (Single Table, Joined, Table Per Class)

**Advanced:**
- How would you handle batch inserts with Hibernate?
- Explain Hibernate second-level cache with Redis (Lab 13)
- How does Hibernate's `StatelessSession` differ from `Session`?
- What is Hibernate `6.x` SQM (Simple Query Model)?
- How do you optimize JPQL queries for pagination at the database level?
- Explain Hibernate multi-tenancy strategies (Lab 21)

### Transactions

**Basic:**
- What does `@Transactional` do? How does it work via AOP proxies?
- Explain `REQUIRED`, `REQUIRES_NEW`, `NESTED` propagation levels.
- What is the default propagation and isolation level in Spring?

**Intermediate:**
- How does `@Transactional` work with proxy-based AOP? What happens with self-invocation?
- What is the `TransactionSynchronizationManager`?
- How do you manage transactions across multiple datasources? (XA/JTA vs ChainedTransactionManager)
- What is the difference between `rollbackFor` and `noRollbackFor`?

**Advanced:**
- How would you implement the outbox pattern with Spring transactions?
- How do you handle distributed transactions in microservices without 2PC? (Saga pattern, Lab 23)
- Explain Hibernate transaction management in a reactive context (Lab 15, Lab 09 R2DBC)
- How does Spring's `@TransactionEventListener` work with transaction phases?

---

## Study Plans

### 4-Week Intensive Plan

| Week | Focus | Labs | Daily Time |
|------|-------|------|------------|
| 1 | Spring Boot Core | 01-06 | 4-5 hrs |
| 2 | Alternative Frameworks + Databases | 08-10, Databases 01-07 | 4-5 hrs |
| 3 | Microservices + Cloud | 16, 22, 23, 24 | 4-5 hrs |
| 4 | System Design + Mock Interviews | All MOCK_INTERVIEW.md files | 4-5 hrs |

**Resources:**
- Cracking the Coding Interview
- Grokking the System Design Interview
- Spring in Action (Chapters 1-12)

### 8-Week Balanced Plan

| Week | Focus | Labs | Supplementary |
|------|-------|------|--------------|
| 1 | Spring Boot Basics | 01, 02, 03 | REST API design books |
| 2 | Data Access | 04, 05 | JPA/Hibernate docs |
| 3 | Security | 06, 20 | Spring Security in Action |
| 4 | Messaging + Async | 07, 19 | Kafka: The Definitive Guide |
| 5 | Alternative Frameworks | 08, 09, 10 | MicroProfile specs |
| 6 | Cloud + Microservices | 16, 22, 23 | Microservices Patterns (Richardson) |
| 7 | Performance + Testing | 11, 24, 25 | Performance testing tools |
| 8 | Internals + System Design | 26, System Design Prep | System Design Interview (Alex Xu) |

### 12-Week Thorough Plan

| Week | Focus | Labs | Deliverable |
|------|-------|------|-------------|
| 1-2 | Spring Boot Foundation | 01-04 | Build REST API |
| 3 | Transactions + Security | 05-06 | Secure endpoints |
| 4 | Messaging | 07, 19 | Async processing |
| 5 | Alternative Frameworks | 08-10 | Compare frameworks |
| 6 | Testing + Documentation | 11-12 | Test suite + API docs |
| 7 | Caching + Scheduling | 13-14 | Caching strategy |
| 8 | Reactive + Cloud | 15-16 | Cloud-native service |
| 9 | API Design + Security | 17, 20 | API security audit |
| 10 | Advanced Patterns | 21-23 | Event-driven service |
| 11 | Performance + Native | 24-25 | Performance optimization |
| 12 | Internals + Interview Prep | 26, MOCK_INTERVIEWs | Mock interviews |

---

## Resources

### Books
| Title | Author | Covers |
|-------|--------|--------|
| Spring in Action (6th Ed.) | Craig Walls | Labs 01-06, 13, 14 |
| Microservices Patterns | Chris Richardson | Labs 16, 23 |
| Building Microservices (2nd Ed.) | Sam Newman | Labs 16, 22, 23 |
| Cloud Native Java | Josh Long | Labs 08, 10, 16 |
| Reactive Spring | Josh Long | Lab 15 |
| Spring Security in Action | Laurentiu Spilca | Labs 06, 20 |
| JPA/Hibernate in Action | Christian Bauer | Labs 04, 05 |
| System Design Interview | Alex Xu | All system design |
| Designing Data-Intensive Applications | Martin Kleppmann | Labs 23, 24 |

### Courses
| Platform | Course | Labs Covered |
|----------|--------|-------------|
| Udemy | Spring Boot Microservices (in28minutes) | Labs 01-06, 16 |
| Udemy | System Design Interview (Alex Xu) | All system design |
| Coursera | Software Design and Architecture (Alberta) | Architecture patterns |
| Oracle University | Oracle Java SE/EE | Labs 09, Jakarta EE |

### Practice Platforms
| Platform | Focus |
|----------|-------|
| LeetCode | Algorithmic + SQL problems |
| Pramp | Free mock interviews |
| Interviewing.io | Anonymous technical interviews |
| System Design Lounge | System design practice |
| Tech Interview Handbook | Comprehensive prep |

### Cheat Sheets
| Topic | Resource |
|-------|----------|
| Spring Boot Annotations | [springboot-annotations.md](26-spring-boot-internals/REFERENCE_CARD.md) |
| Hibernate/JPA | [jpa-hibernate-cheat-sheet.md](../databases/07-spring-data/README.md) |
| System Design | [system-design-patterns.md](../architecture/system-design-patterns/) |
| Kafka | [kafka-cheat-sheet.md](../distributed-systems/kafka/REFERENCE_CARD.md) |

---

<div align="center">

**"The key to acing a backend interview is not just knowing Spring Boot — it's understanding when to use each pattern and why."**

---

[Back to Academy](#backend-academy--interview-preparation-guide)

</div>
