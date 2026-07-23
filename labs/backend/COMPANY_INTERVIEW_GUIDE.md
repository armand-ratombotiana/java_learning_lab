# Backend Academy — Company Interview Guide

<div align="center">

![Company Guide](https://img.shields.io/badge/Company_Guide-FF6F00?style=for-the-badge)
![15 Companies](https://img.shields.io/badge/15_Companies-4285F4?style=for-the-badge)
![Backend Roles](https://img.shields.io/badge/Backend_Roles-6DB33F?style=for-the-badge)

**Interview process, expectations, compensation & preparation timelines for backend engineers**

</div>

---

## Table of Contents

1. [Google](#1-google)
2. [Microsoft](#2-microsoft)
3. [Amazon](#3-amazon)
4. [Meta](#4-meta)
5. [Apple](#5-apple)
6. [Netflix](#6-netflix)
7. [Uber](#7-uber)
8. [Stripe](#8-stripe)
9. [DoorDash](#9-doordash)
10. [Lyft](#10-lyft)
11. [Twitter/X](#11-twitterx)
12. [Slack](#12-slack)
13. [Confluent](#13-confluent)
14. [Databricks](#14-databricks)
15. [Compensation Overview](#15-compensation-overview)
16. [General Preparation Timelines](#16-general-preparation-timelines)

---

## 1. Google

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Algorithms + System Design fundamentals | 45 min |
| Technical (Backend) | On-site / Virtual | API Design, Microservices, Distributed Systems | 60 min |
| System Design | On-site / Virtual | Large-scale backend systems | 60 min |
| Googleyness | On-site / Virtual | Leadership, ambiguity, collaboration | 45 min |
| Extra (Staff+) | On-site / Virtual | Technical deep-dive, cross-functional | 60 min |

### Backend Technology Expectations
- **Distributed systems fundamentals:** CAP theorem, Paxos/Raft, consistency models
- **API design:** gRPC (Protocol Buffers), Google AIP standards, HTTP/2 streaming
- **Data layer:** Spanner, Bigtable, BigQuery, Memorystore (Redis)
- **Java expectations:** Guava, gRPC-Java, Protobuf, not necessarily Spring Boot
- **Key labs to review:** 16 (Spring Cloud — distributed patterns), 22 (GraphQL — alternative to gRPC), 23 (CQRS/Axon — event-driven), 24 (Backend Performance — scale patterns)

### System Design Round Expectations
- "Design Google Drive" — focus on chunked file upload, synchronization, conflict resolution using CRDTs
- "Design YouTube" — video processing pipeline, CDN edge caching, adaptive bitrate streaming
- "Design Google Maps" — geospatial indexing, tile generation, route optimization (Dijkstra on road graph)
- **Backend angle:** emphasize consistency trade-offs, replication strategies, fault tolerance at planetary scale

### Coding Round Expectations (Java + Backend)
- **Data structures & algorithms:** LeetCode Medium/Hard, but often framed in backend contexts
- **Concurrency:** Thread-safe cache, rate limiter, multi-threaded batch processor
- **API design:** Design a logging library API, design a configuration management interface
- **Example backend LeetCode:** "Design a search autocomplete system" (Trie + ranking)

### Real Interview Stories — Backend
> **SWE III (L4) — YouTube Backend:** "Phone screen was a medium DP problem followed by 'design a video transcoding pipeline.' On-site had: 1) Design a distributed priority queue (consistent hashing + Kafka), 2) Design YouTube's recommendation system (collaborative filtering at scale), 3) Googleyness about working with conflicting XFN priorities. Result: **Passed**."

> **Senior SWE (L5) — Cloud Spanner team:** "System design was literally 'design a globally distributed database.' I discussed Spanner's TrueTime API, atomic clocks, and GPS-based clock synchronization. Coding round was a multi-threaded web crawler — they wanted Java with proper concurrency control. Mock DB layer using ConcurrentHashMap. Result: **Passed, L5 offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| System Design Primer (GitHub) | 2 weeks | Foundation |
| DDIA Book (Chapters 1-9) | 3 weeks | Deep dive |
| LeetCode (75 problems — graphs, DP, concurrency) | 4 weeks | Coding |
| Google AIP docs + gRPC examples | 1 week | API Design |
| Labs 16, 22, 23, 24, 26 | 2 weeks | Lab review |
| Mock interviews (2-3) | 1 week | Practice |
| **Total** | **13 weeks** | |

---

## 2. Microsoft

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding + Azure familiarity | 45 min |
| Technical (Backend) | On-site / Virtual | System design (Azure services), API design | 60 min |
| System Design | On-site / Virtual | Cloud-native backend architecture | 60 min |
| Behavioral (STAR) | On-site / Virtual | Leadership principles, project deep-dive | 45 min |
| Engineering Manager | On-site / Virtual | Culture fit, technical vision | 45 min |

### Backend Technology Expectations
- **Azure ecosystem:** Azure Spring Apps, AKS, Service Bus, Cosmos DB, SQL Database
- **Java/Spring expectations:** Spring Boot on Azure, Spring Cloud + Azure integration
- **API design:** RESTful APIs, Azure API Management, OpenAPI/Swagger
- **Key labs to review:** 11 (Testing — CI/CD with Azure DevOps), 13 (Caching — Azure Redis), 07 (Messaging — Azure Service Bus vs Kafka), 16 (Spring Cloud — Azure Spring Apps)

### System Design Round Expectations
- "Design a distributed logging system" — Azure Monitor / Application Insights architecture
- "Design a real-time collaboration platform" — WebSocket scaling, operational transforms
- "Design a multi-region e-commerce platform" — Cosmos DB multi-master writes, conflict resolution
- **Backend angle:** Buy vs build decisions using Azure services, cost optimization at scale

### Coding Round Expectations
- **Coding format:** 45 minutes on a shared editor (Codility / Microsoft Teams)
- **Common patterns:** Arrays, strings, trees — less focus on esoteric algorithms
- **Backend context:** "Design a rate limiter" (token bucket), "Design a distributed counter" (atomic / CRDT)
- **Java-specific:** Candidates often use C# but Java is accepted; Spring Boot familiarity is a bonus

### Real Interview Stories — Backend
> **SDE II — Azure Spring Apps team:** "Phone screen was 'design a ticket booking system.' On-site had: 1) Design Azure Event Grid's event routing — pub/sub with filtering, 2) Design a circuit breaker for Azure Functions, 3) Behavioral: 'Tell me about a time you dealt with a production incident' — discussed a DB connection leak in a Spring Boot service. Result: **Passed, SDE II offer**."

> **Senior SDE — Microsoft Teams backend:** "System design was 'design a real-time messaging system' — I used SignalR (Azure Web PubSub), Kafka for message persistence, Cosmos DB for chat history with TTL. Backend was .NET but they appreciated my Spring Boot architecture knowledge. Result: **Passed**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Azure Fundamentals (AZ-900) — light reading | 1 week | Azure basics |
| LeetCode (50 problems — arrays, strings, trees) | 3 weeks | Coding |
| System design patterns (Cloud-native) | 3 weeks | System design |
| Azure Spring Apps documentation | 1 week | Platform |
| Labs 07, 11, 13, 16 | 2 weeks | Lab review |
| Mock interviews | 1 week | Practice |
| **Total** | **11 weeks** | |

---

## 3. Amazon (AWS)

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Bar raiser screen, coding + LP | 60 min |
| Technical 1 | On-site / Virtual | Coding (backend scenario) | 60 min |
| Technical 2 | On-site / Virtual | System design (AWS focus) | 60 min |
| System Design | On-site / Virtual | Scalable distributed system | 60 min |
| Bar Raiser | On-site / Virtual | Leadership Principles deep-dive | 60 min |
| Manager | On-site / Virtual | Technical fit, team specifics | 45 min |

### Backend Technology Expectations
- **AWS ecosystem:** ECS/EKS/Fargate, Lambda + SnapStart, DynamoDB, SQS/SNS, ElastiCache
- **Java/Spring expectations:** Spring Boot on ECS, Spring Cloud Function + Lambda, JPA + RDS
- **API design:** RESTful APIs — Amazon API Gateway, versioning, pagination patterns
- **Key labs to review:** 02 (REST APIs — API design), 07 (Messaging — SQS/SNS patterns), 13 (Caching — ElastiCache), 16 (Spring Cloud — AWS integration), 18 (File/Batch — S3 processing)

### System Design Round Expectations
- "Design Amazon's product catalog API" — DynamoDB single-table design, DAX caching, CDN
- "Design a warehouse inventory system" — event-driven (SQS + Lambda), real-time stock updates
- "Design a real-time order tracking system" — WebSocket + DynamoDB Streams + Lambda
- **Backend angle:** AWS-native solutions, cost analysis, multi-AZ availability

### Coding Round Expectations
- **Amazon's style:** Coding problems framed as AWS or e-commerce scenarios
- **Example:** "Given a list of orders, group by customer and detect anomalies" — HashMap + custom comparator
- **Concurrency:** "Implement a thread-safe cache for product inventory" — ConcurrentHashMap + read-write locks
- **API design:** "Design the API for Amazon's recommendation service" — endpoint design, pagination, rate limiting

### Real Interview Stories — Backend
> **SDE II — AWS Lambda team:** "Phone screen was a priority queue problem (merge k sorted lists) with an LP about diving deep into a customer issue. On-site had: 1) Design a serverless image processing pipeline (S3 → Lambda → DynamoDB → SQS → Email), 2) Implement a distributed counter (CRDT with DynamoDB conditional updates), 3) Bar raiser: 6 LP questions back-to-back — strict STAR format required. Result: **Passed**."

> **Senior SDE — Amazon Prime Video:** "System design was 'design a video streaming CDN.' I used CloudFront, S3 origin, Lambda@Edge for authentication, MediaConvert for transcoding. Coding was 'find the top K trending videos from a stream of views.' Used min-heap + hashmap. Result: **Passed, L6 offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Leadership Principles memorization | 1 week | Behavioral |
| AWS Developer Associate prep | 2 weeks | AWS basics |
| LeetCode (75 problems — arrays, graphs, DP) | 4 weeks | Coding |
| System design patterns (AWS-native) | 3 weeks | System design |
| Labs 02, 07, 13, 16, 18 | 2 weeks | Lab review |
| Mock interviews with LP | 1 week | Practice |
| **Total** | **13 weeks** | |

---

## 4. Meta

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding (LC Medium) | 45 min |
| Technical 1 | On-site / Virtual | Coding (LC Medium/Hard) | 45 min |
| Technical 2 | On-site / Virtual | Coding (LC Medium/Hard) | 45 min |
| System Design | On-site / Virtual | Backend system design | 45 min |
| Behavioral | On-site / Virtual | Meta's values, projects, conflict | 45 min |

### Backend Technology Expectations
- **Scale expectations:** Billions of users, data-driven backend decisions
- **Tech stack:** Hack (PHP) / Python / Java for backend, GraphQL (core), TAO (distributed cache)
- **GraphQL expertise:** Schema design, DataLoader (N+1), subscriptions, federation (Apollo)
- **Key labs to review:** 12 (API Documentation — GraphQL schema docs), 22 (GraphQL DGS — full GraphQL), 15 (WebFlux/Reactive — async data fetching with DataLoader), 13 (Caching — TAO-like distributed caching)

### System Design Round Expectations
- "Design Facebook News Feed" — feed ranking, fanout-on-write vs fanout-on-read
- "Design Facebook Messenger" — real-time messaging, presence, delivery guarantees
- "Design Instagram Stories" — ephemeral content, expiration, views counter
- **Backend angle:** GraphQL as API gateway, TAO cache for social graph, Presto for analytics

### Coding Round Expectations
- **Meta's signature:** Two coding rounds, heavily weighted on speed and correctness
- **Common topics:** Arrays, strings, trees, graphs (friendship graph problems)
- **Backend framing:** "Design and implement a rate limiter" — token bucket or sliding window
- **Java context:** Often expects multi-threaded solutions (read-write locks, atomics, ConcurrentHashMap)

### Real Interview Stories — Backend
> **E4 — Instagram Backend:** "Phone screen was 'valid parentheses' + 'design Instagram feed generation.' On-site: 1) Graph problem: 'find mutual friends' (BFS on adjacency list), 2) 'Design a distributed counter for Instagram likes' (Redis + batch writes to DB), 3) System design: 'design Instagram Stories.' I discussed GraphQL subscriptions for live viewers, Redis sorted sets for story ranking, blob storage for media. Behavioral was about moving fast and handling ambiguity. Result: **Passed, E4 offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| LeetCode (100 problems — speed focus) | 4 weeks | Coding |
| GraphQL DGS lab (Lab 22) | 2 weeks | API design |
| System design (social apps) | 3 weeks | System design |
| Caching patterns (Lab 13) | 1 week | Caching |
| Labs 12, 13, 15, 22 | 2 weeks | Lab review |
| Mock coding interviews | 2 weeks | Practice |
| **Total** | **14 weeks** | |

---

## 5. Apple

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding + fundamentals | 45 min |
| Technical 1 | On-site / Virtual | System design + coding | 60 min |
| Technical 2 | On-site / Virtual | Backend architecture | 60 min |
| Technical 3 | On-site / Virtual | Deep Java/Spring knowledge | 60 min |
| Manager + Director | On-site / Virtual | Culture, privacy, quality | 60 min |

### Backend Technology Expectations
- **Privacy-first backend design:** On-device processing, data minimization, differential privacy
- **Tech stack:** Swift + Vapor for some backend services, Java/Spring Boot for enterprise services, Kafka for streaming
- **API design:** RESTful APIs, careful versioning (backward compatibility is paramount)
- **Key labs to review:** 20 (Backend Security Deep — encryption at rest/transit, key management), 17 (API Versioning — backward compatibility), 01-26 all (broad understanding)

### System Design Round Expectations
- "Design Apple iCloud sync" — CRDT-based sync, conflict resolution, delta compression
- "Design Apple Push Notification Service (APNS)" — persistent connections, delivery guarantees, rate limiting
- "Design a privacy-preserving analytics system" — differential privacy, on-device aggregation, secure enclave
- **Backend angle:** Security and privacy as first-class architecture concerns

### Coding Round Expectations
- **Focus:** Clean, maintainable, well-typed code — Apple values craftsmanship
- **Common patterns:** Thread-safe operations, careful API design, robust error handling
- **Backend coding:** "Design a thread-safe cache with TTL and LRU eviction" — full implementation expected
- **Java-specific:** Stream APIs, Optional usage, immutable DTOs, builder pattern

### Real Interview Stories — Backend
> **SDE — Apple Cloud Services:** "Phone screen was 'design a logging framework.' On-site: 1) Implement a thread-safe LRU cache (ConcurrentHashMap + doubly linked list + locks), 2) Design iCloud Drive sync — CRDTs, CouchDB-like conflict resolution, delta sync, 3) Security design: encrypt data end-to-end, key hierarchy, Secure Enclave. They care deeply about privacy architecture. Result: **Passed, ICT4 offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Java concurrency (JCIP book) | 2 weeks | Concurrency |
| System design (sync, messaging) | 3 weeks | System design |
| Security labs (Lab 06, 20) | 2 weeks | Security |
| LeetCode (50 problems — OOD, concurrency) | 3 weeks | Coding |
| Labs 17, 20, 26 | 2 weeks | Lab review |
| **Total** | **12 weeks** | |

---

## 6. Netflix

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Recruiter Screen | Video Call | Background, expectations | 30 min |
| Technical 1 | Video Call | System design (streaming focus) | 60 min |
| Technical 2 | Video Call | Coding + Java depth | 60 min |
| Technical 3 | Video Call | Microservices architecture | 60 min |
| Culture & Values | Video Call | Freedom & Responsibility | 45 min |

### Backend Technology Expectations
- **Java + Spring Boot at scale:** Netflix pioneered microservices with Spring Boot
- **Cloud-native:** AWS (before moving to their own cloud), Chaos Engineering (Chaos Monkey, Simian Army)
- **API design:** REST with Hystrix circuit breakers, Zuul API gateway, Eureka service discovery
- **Key labs to review:** 13 (Caching — EVCache, Netflix's distributed caching layer), 16 (Spring Cloud — Netflix OSS patterns), 15 (WebFlux/Reactive — reactive streams, RSocket), 03 (Spring MVC — server-side rendering patterns)

### System Design Round Expectations
- "Design Netflix's content delivery network (Open Connect)" — ISP peering, caching hierarchy, adaptive bitrate
- "Design Netflix's recommendation system" — collaborative filtering, matrix factorization, A/B testing infrastructure
- "Design a chaos engineering platform" — fault injection, blast radius control, automated rollback
- **Backend angle:** Resilience patterns (circuit breakers, bulkheads, graceful degradation)

### Coding Round Expectations
- **Focus:** Java internals, concurrency, reactive programming
- **Common pattern:** "Implement a reactive stream from scratch" — Publisher/Subscriber with backpressure
- **Concurrency:** "Design a thread pool executor with dynamic sizing" — core Java concurrency
- **API design:** "Design the API for Netflix Studio's content management system" — microservice decomposition

### Real Interview Stories — Backend
> **Senior SWE — Content Engineering:** "First discussion with hiring manager about streaming architecture. Technical rounds: 1) Design a personalized homepage — microservice decomposition, A/B test framework, SSR vs CSR, 2) Java reactive programming — implement a non-blocking HTTP client using WebClient with retries and circuit breakers, 3) Culture round — discussed a time I disagreed with architecture decisions (they wanted evidence of candor). Result: **Passed, Senior IC offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Reactive programming (Project Reactor) | 2 weeks | Reactive |
| System design (streaming, CDN) | 3 weeks | System design |
| Java concurrency + Spring Boot | 3 weeks | Core |
| Chaos engineering concepts | 1 week | Resilience |
| Labs 13, 15, 16 | 2 weeks | Lab review |
| **Total** | **11 weeks** | |

---

## 7. Uber

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding (LC Medium) | 45 min |
| Technical 1 | On-site / Virtual | Coding (LC Medium/Hard) | 60 min |
| Technical 2 | On-site / Virtual | System design | 60 min |
| Technical 3 | On-site / Virtual | Backend architecture + Java | 60 min |
| Behavioral | On-site / Virtual | Uber values, customer obsession | 45 min |

### Backend Technology Expectations
- **Tech stack:** Java, Go, Kafka, Cassandra, Redis, Uber's own Cadence (workflow orchestration)
- **Microservices:** Domain-oriented microservices, DDD, CQRS for ride-matching
- **Key labs to review:** 07 (Messaging — Kafka for ride events), 13 (Caching — Redis for geospatial), 21 (Multi-Tenancy — region/city isolation), 23 (CQRS/Axon — command/query separation), 24 (Backend Performance — high-throughput)

### System Design Round Expectations
- "Design Uber's ride-matching engine" — geospatial indexing (quadtrees, H3 hexagons), supply-demand optimization
- "Design Uber's real-time ETA system" — Google Maps API integration, traffic prediction, ML models
- "Design Uber's surge pricing" — elasticity of demand, real-time data processing (Flink/Kafka Streams)
- **Backend angle:** Real-time data processing, geospatial indexing, CQRS patterns

### Coding Round Expectations
- **Focus:** Java, algorithms with real-world mapping to Uber's domain
- **Example:** "Given a set of drivers (lat/long) and a rider, find the nearest driver" — K-d tree, geohashing
- **Distributed systems coding:** "Design a distributed priority queue for ride requests" — consistent hashing, Kafka partitions
- **Concurrency:** "Thread-safe geospatial index for real-time driver location updates" — ReadWriteLock + R-tree

### Real Interview Stories — Backend
> **Senior SWE — Ride Matching:** "Phone screen was 'design a rate limiter' (token bucket + Redis). On-site: 1) Geospatial problem — find nearest N drivers using a quadtree, optimized for concurrent updates, 2) System design: 'Design Uber Eats dispatch' — real-time preparation time, driver proximity, batching, 3) Behavioral: 'Tell me about a time you operated a high-scale system' — discussed Kafka consumer lag incident. Result: **Passed, Senior offer**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Geospatial data structures | 2 weeks | Specialized |
| Kafka + event-driven architecture | 2 weeks | Messaging |
| LeetCode (60 problems — maps, graphs, sorting) | 4 weeks | Coding |
| System design (real-time, geo) | 3 weeks | System design |
| Labs 07, 13, 21, 23, 24 | 2 weeks | Lab review |
| **Total** | **13 weeks** | |

---

## 8. Stripe

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Recruiter Screen | Video Call | Tech background + compensation | 30 min |
| Coding | Video Call | LC Medium — API design context | 60 min |
| System Design | Video Call | Distributed financial systems | 60 min |
| Debugging | Video Call | Read code, find bugs, fix | 45 min |
| Behavioral | Video Call | Stripe values, user focus | 45 min |

### Backend Technology Expectations
- **Tech stack:** Java, Ruby, Go, Kafka, MySQL, Redis, Vitess (MySQL sharding)
- **API design:** RESTful API design is core — idempotency, pagination, versioning, idempotency keys
- **Consistency:** Strong focus on transactional guarantees, exactly-once processing
- **Key labs to review:** 01 (Spring Boot Basics — service foundations), 04 (Spring Data JPA — transactional data), 05 (Transaction Management — ACID, distributed tx), 17 (API Versioning — backward compatibility), 24 (Backend Performance — low-latency APIs)

### System Design Round Expectations
- "Design Stripe's payment API" — idempotency keys, idempotency guarantee, idempotency at read replicas
- "Design a billing system" — metering, invoicing, dunning, proration
- "Design a fraud detection system" — real-time ML inference, rule engine, rate limiting
- **Backend angle:** Financial correctness, data consistency, exactly-once semantics

### Coding Round Expectations
- **Coding:** LC Medium — often framed in payment contexts
- **Debugging round:** Given a broken codebase (Java), find the concurrency bug, fix it, write tests
- **API design:** "Design the API for a subscription management service" — endpoints, models, pagination, error handling
- **Example problem:** "Implement a payment split calculator" — complex arithmetic, rounding, currency handling

### Real Interview Stories — Backend
> **SWE — Payments Infrastructure:** "Phone screen was 'implement an automated teller machine' — clean OOD with currency denominations. On-site: 1) System design: 'Design Stripe's webhook delivery system' — exactly-once delivery, retries with exponential backoff, idempotency, dead-letter queues, 2) Debugging: Java code with a ConcurrentModificationException in a multi-threaded payment processor, 3) Behavioral: 'Tell me about a time you had to make a trade-off between speed and quality' — discussed migrating a monolith to microservices. Result: **Passed**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| API design best practices (Stripe API docs) | 1 week | API design |
| Transaction management (Lab 05) | 1 week | Transactions |
| LeetCode (40 problems — OOD, hash maps) | 3 weeks | Coding |
| System design (financial systems) | 3 weeks | System design |
| Labs 01, 04, 05, 17, 24 | 2 weeks | Lab review |
| **Total** | **10 weeks** | |

---

## 9. DoorDash

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding + system design overview | 45 min |
| Technical 1 | On-site / Virtual | Coding (LC Medium) | 60 min |
| Technical 2 | On-site / Virtual | System design (logistics focus) | 60 min |
| Technical 3 | On-site / Virtual | Backend + database design | 60 min |
| Behavioral | On-site / Virtual | DoorDash values, merchant focus | 45 min |

### Backend Technology Expectations
- **Tech stack:** Kotlin, Java, Go, MySQL, Cassandra, Kafka, Kubernetes
- **Microservices:** Service decomposition by domain (ordering, fulfillment, logistics)
- **Key labs to review:** 02 (REST APIs — ordering APIs), 04 (Spring Data JPA — order DB), 07 (Messaging — Kafka for event-driven fulfillment), 13 (Caching — Redis for real-time tracking), 18 (File/Batch — batch reporting), 19 (Server-Sent Events — real-time order tracking)

### System Design Round Expectations
- "Design DoorDash's order fulfillment system" — restaurant assignment, driver dispatch, real-time tracking
- "Design a real-time logistics optimization engine" — route optimization (traveling salesman, vehicle routing)
- "Design DoorDash's search and discovery" — Elasticsearch, geo-indexed restaurants, dynamic ranking
- **Backend angle:** Real-time multi-sided marketplace, logistics optimization, scheduling algorithms

### Coding Round Expectations
- **Focus:** Algorithms with operational research flavor
- **Example:** "Given delivery locations, find the optimal route for a driver" — Dijkstra, A*, TSP approximation
- **Concurrency:** "Thread-safe order matching engine" — matching buy/sell orders like a stock exchange
- **API design:** "Design the API for a Dasher (driver) mobile app" — RESTful endpoints for state machine transitions

### Real Interview Stories — Backend
> **SWE — Logistics:** "Phone screen was 'implement a time-based key-value store' (LC 981). On-site: 1) 'Design a system to assign orders to Dashers' — bipartite graph matching, hungarian algorithm, real-time constraints, 2) 'Design the DoorDash menu management API' — product hierarchy, modifiers, availability, vendor integration, 3) Behavioral: 'Tell me about a time you made a data-driven decision' — discussed using A/B testing to improve order accuracy. Result: **Passed**."

### Preparation Timeline
| Resource | Duration | Phase |
|----------|----------|-------|
| Graphs + shortest path algorithms | 2 weeks | Algorithms |
| System design (logistics, marketplace) | 3 weeks | System design |
| LeetCode (50 problems — graphs, maps, design) | 4 weeks | Coding |
| Labs 02, 04, 07, 13, 18, 19 | 2 weeks | Lab review |
| **Total** | **11 weeks** | |

---

## 10. Lyft

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding (LC Medium) | 45 min |
| Technical 1 | On-site / Virtual | Coding (LC Medium/Hard) | 60 min |
| Technical 2 | On-site / Virtual | System design (rides) | 60 min |
| Technical 3 | On-site / Virtual | Backend design + database | 60 min |
| Behavioral | On-site / Virtual | Lyft values, inclusivity | 45 min |

### Backend Technology Expectations
- **Tech stack:** Python, Java, Go, DynamoDB, Cassandra, Redis, Kafka, Flink
- **Microservices:** Domain-based services (riders, drivers, pricing, maps)
- **Key labs to review:** 07 (Messaging — Kafka event streams), 13 (Caching — Redis for real-time), 21 (Multi-Tenancy — city-based isolation), 24 (Backend Performance — high throughput matching)

### System Design Round Expectations
- Similar to Uber but often simpler scale: "Design Lyft's ride-matching system"
- "Design a real-time carpooling / shared ride system" — route matching, time windows
- "Design Lyft's pricing engine" — distance-based, surge, promotions
- **Backend angle:** Emphasis on fair pricing, driver/rider marketplace balance

### Real Interview Stories — Backend
> **Senior SWE — Pricing:** "Phone screen was 'find all permutations of a ride route' — graph backtracking. On-site: 1) Design real-time pricing — ML model serving, feature store, A/B platform, 2) Coding: design a thread-safe event bus for location updates (pub/sub with concurrent subscribers), 3) Behavioral: discussed building an inclusive engineering culture. Result: **Passed**."

---

## 11. Twitter/X

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding (LC Medium) | 45 min |
| Technical 1 | On-site / Virtual | System design (timeline, tweet) | 60 min |
| Technical 2 | On-site / Virtual | Coding + data modeling | 60 min |
| Technical 3 | On-site / Virtual | Backend performance + scale | 60 min |
| Behavioral | On-site / Virtual | Culture, speed of execution | 45 min |

### Backend Technology Expectations
- **Tech stack:** Java (JVM), Scala, Kafka, Manhattan (Twitter's KV store), Twemcache (Redis)
- **Key problems:** Fanout-on-write vs fanout-on-read for timeline, real-time search indexing
- **Key labs to review:** 13 (Caching — Twitter's distributed cache), 07 (Messaging — Kafka event bus), 23 (CQRS/Axon — timeline read/write separation), 24 (Backend Performance — high throughput systems)

### System Design Round Expectations
- "Design Twitter's timeline" — classic fanout-on-write (celebrities) vs fanout-on-read (regular users)
- "Design Twitter search" — inverted index (Lucene), real-time indexing, query rewriting
- "Design Twitter's trending topics" — real-time aggregation, sliding window count, anomaly detection

### Real Interview Stories — Backend
> **SWE — Timeline Service:** "Design the timeline for 500M users. Discussed hybrid fanout approach — celebrities use fanout-on-read, regular users use fanout-on-write. Kafka for event ingestion, Manhattan (KV store) for timeline caching. Coding was implement a bloom filter for URL deduplication in tweets. Result: **Passed**."

---

## 12. Slack

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding | 45 min |
| Technical 1 | On-site / Virtual | System design (messaging) | 60 min |
| Technical 2 | On-site / Virtual | Full-stack backend + DB | 60 min |
| Technical 3 | On-site / Virtual | Concurrency + performance | 60 min |
| Behavioral | On-site / Virtual | Collaboration, product sense | 45 min |

### Backend Technology Expectations
- **Tech stack:** Java, Python, Kafka, MySQL (Vitess), Memcached, S3
- **Key challenges:** Real-time messaging, workspace isolation (multi-tenancy — Lab 21)
- **Key labs to review:** 15 (WebFlux/Reactive — WebSocket scaling), 19 (Server-Sent Events — real-time), 07 (Messaging — event-driven message delivery), 21 (Multi-Tenancy — workspace data isolation)

### System Design Round Expectations
- "Design Slack's real-time messaging" — WebSocket cluster management, presence, typing indicators
- "Design Slack's search" — Elasticsearch, inverted index, workspace-scoped search
- "Design Slack's file upload service" — S3 with CDN, thumbnail generation, virus scanning

### Real Interview Stories — Backend
> **Backend SWE — Messaging:** "System design: 'Design a messaging platform for 10M concurrent users.' I covered WebSocket gateway (HAProxy → WS cluster), Redis pub/sub for cross-instance messaging, Kafka for durable message log, Vitess (MySQL shard) for history. Concurrency coding: implement a thread-safe in-memory message queue with bounded capacity and blocking. Result: **Passed**."

---

## 13. Confluent

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Kafka fundamentals + coding | 45 min |
| Technical 1 | On-site / Virtual | Kafka internals (system design) | 60 min |
| Technical 2 | On-site / Virtual | Distributed systems coding | 60 min |
| Technical 3 | On-site / Virtual | Backend + API design | 60 min |
| Behavioral | On-site / Virtual | Open source culture | 45 min |

### Backend Technology Expectations
- **Domain expertise:** Kafka internals — partitions, replication, ISR, compaction, exactly-once semantics
- **Tech stack:** Java (core Kafka), C/C++ (KRaft), Schema Registry (REST), Kafka Connect, ksqlDB
- **Key labs to review:** 07 (Messaging — Kafka deep dive), 23 (CQRS/Axon — event sourcing with Kafka), 24 (Backend Performance — Kafka performance tuning)

### System Design Round Expectations
- "Design Kafka's replication protocol" — leader election, ISR management, unclean leader election
- "Design a schema registry" — Avro/Protobuf/JSON Schema, compatibility modes (BACKWARD, FORWARD, FULL)
- "Design Kafka Connect for a database CDC pipeline" — Debezium integration, offset management

### Real Interview Stories — Backend
> **SWE — Kafka Core:** "Phone screen: 'explain how Kafka partition rebalancing works' + 'implement a producer with exactly-once semantics.' On-site: 1) Design Kafka's compaction algorithm — log cleaning, tombstone records, delete retention, 2) Java coding: implement a lock-free concurrent queue (Michael-Scott queue), 3) Behavioral: how do you make trade-offs in open-source design? Result: **Passed**."

---

## 14. Databricks

### Interview Process (Backend-Specific)
| Round | Format | Focus | Duration |
|-------|--------|-------|----------|
| Phone Screen | Video Call | Coding + distributed systems | 60 min |
| Technical 1 | On-site / Virtual | System design (data platform) | 60 min |
| Technical 2 | On-site / Virtual | Coding (hard algorithms) | 60 min |
| Technical 3 | On-site / Virtual | Distributed systems + backend | 60 min |
| Behavioral | On-site / Virtual | Data-first mindset, Apache Spark | 45 min |

### Backend Technology Expectations
- **Domain expertise:** Apache Spark, Delta Lake, MLflow, data lakehouse architecture
- **Tech stack:** Scala, Java, Python, Spark, Kubernetes, Delta Lake
- **Key labs to review:** 18 (File/Batch — Spark-like batch processing), 24 (Backend Performance — distributed computing), 25 (GraalVM Native — native compilation for data pipelines)

### System Design Round Expectations
- "Design a distributed data processing platform" — Spark architecture, cluster management, shuffle service
- "Design Delta Lake" — ACID transactions on data lake, time travel, schema enforcement
- "Design a model serving platform" — MLflow, model registry, A/B inference, autoscaling

### Real Interview Stories — Backend
> **SWE — Compute Platform:** "Phone screen: 'implement a distributed word count' (MapReduce). On-site: 1) Design Spark's shuffle service — map output tracking, external sort, reducer fetch, 2) Coding: implement a lock-free concurrent hash map (Java, suited for Spark executors), 3) Behavioral: 'tell me about a time your system processed 10TB+ of data.' Result: **Passed**."

---

## 15. Compensation Overview

### Backend Engineer Total Compensation (2024-2025, USD)
| Company | Junior (0-2yr) | Mid (3-5yr) | Senior (5-8yr) | Staff (8-12yr) | Principal (12+yr) |
|---------|---------------|-------------|----------------|----------------|-------------------|
| Google | $150-190K | $190-280K | $280-400K | $400-550K | $550-800K+ |
| Microsoft | $130-170K | $170-250K | $250-350K | $350-500K | $500-700K |
| Amazon | $140-185K | $185-275K | $275-400K | $400-550K | $550-750K |
| Meta | $160-200K | $200-320K | $320-450K | $450-600K | $600-900K+ |
| Apple | $145-190K | $190-280K | $280-400K | $400-550K | $550-800K |
| Netflix | $250-400K* | $300-500K* | $400-700K* | $500-900K* | $700-1.2M* |
| Uber | $140-180K | $180-260K | $260-380K | $380-520K | $520-750K |
| Stripe | $160-210K | $210-310K | $310-450K | $450-600K | $600-850K |
| DoorDash | $150-190K | $190-275K | $275-400K | $400-550K | $550-800K |
| Lyft | $135-175K | $175-250K | $250-350K | $350-480K | $480-700K |
| Twitter/X | $140-180K | $180-260K | $260-370K | $370-500K | $500-700K |
| Slack | $145-185K | $185-260K | $260-360K | $360-480K | $480-650K |
| Confluent | $140-180K | $180-250K | $250-350K | $350-480K | $480-700K |
| Databricks | $155-200K | $200-300K | $300-420K | $420-580K | $580-850K |

*\*Netflix typically pays 100% cash, no equity splits.*
*\*All figures include base + bonus + RSUs (vested annually). RSU appreciation not included.*
*\*Data compiled from Levels.fyi, Blind, and Glassdoor 2024-2025.*

### Compensation Negotiation Tips
- **Always get multiple offers** — competing offers increase TC by 15-30% on average
- **RSU refreshers matter** — at Meta/Google, refreshers can double your TC by year 3-4
- **Signing bonus** — negotiate for 1st year (typically $25-100K, recoupable if you leave <12mo)
- **Netflix model** — choose your own mix of cash/options; front-load your comp package
- **Stripe** — offer 409A valuations on secondary stock sales; liquidity events every 6 months

---

## 16. General Preparation Timelines

### 3-Month Sprint (Targeting a specific company)
| Week | Focus |
|------|-------|
| 1-2 | Research target company, read interview guides (this doc + Labs), identify gaps |
| 3-5 | LeetCode — 25 problems/week (top 75 for backend: trees, graphs, hashmaps, concurrency) |
| 6-7 | System design — 2 designs/week (review System Design Integration and System Design Cheatsheet) |
| 8 | Deep dive into company-specific lab modules (see per-company tables above) |
| 9-10 | Mock interviews — 2 coding + 2 system design + 1 behavioral |
| 11-12 | Final review — behavioral stories (STAR), compensation negotiation research |

### 6-Month Deep Preparation (Multiple companies)
| Phase | Duration | Activities |
|-------|----------|-----------|
| Foundation | Weeks 1-4 | Complete all 26 labs, build 2-3 Spring Boot projects |
| Algorithms | Weeks 5-12 | LeetCode 150 problems (NeetCode 150 relevant sections) |
| System Design | Weeks 13-18 | DDIA book, Grokking System Design, 12 full design exercises |
| Company Research | Weeks 19-20 | Study target companies, read backlog of engineering blogs |
| Labs Deep Review | Weeks 21-22 | Revisit labs matching target companies (see tables above) |
| Mock Interviews | Weeks 23-24 | 6-8 mock interviews, preferably with peers from this academy |
| Final Sprint | Weeks 25-26 | Behavioral prep, compensation research, company-specific final review |

### Daily Breakdown (Final 2 Weeks)
| Time | Activity |
|------|----------|
| 7:00-8:00 AM | LeetCode problem (hard, untimed) |
| 8:00-9:00 AM | System design flash cards + draw architecture |
| 12:00-1:00 PM | Behavioral story practice (STAR format) |
| 6:00-7:00 PM | Company-specific research + blog reading |
| 8:00-9:00 PM | Mock interview or review weak area |
| Weekends | Full-length mock interviews + compensation research |

### Key Resources
- **Labs:** All 26 backend labs — per-lab INTERVIEW.md / MOCK_INTERVIEW.md / LEETCODE_SOLUTIONS
- **Academy guides:** ACADEMY_INTERVIEW_GUIDE.md, CRACKING_THE_INTERVIEW_GUIDE.md, INTERVIEW_CHEATSHEET.md, SYSTEM_DESIGN_CHEATSHEET.md, SYSTEM_DESIGN_INTEGRATION.md
- **Books:** Designing Data-Intensive Applications (Kleppmann), Java Concurrency in Practice (Goetz)
- **Websites:** Levels.fyi, Glassdoor, Blind, TeamBlind, LeetCode Discuss
- **Engineering blogs:** Each company's engineering blog for real architecture deep-dives

---

<div align="center">

**Good luck with your interviews! Master the labs, practice the patterns, and own your story.**

</div>
