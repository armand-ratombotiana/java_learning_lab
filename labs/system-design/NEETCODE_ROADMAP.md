# NeetCode Roadmap — System Design Academy

> Map the Academy to NeetCode System Design content with study plans and per-company focus guides.

---

## Table of Contents

1. [NeetCode System Design vs Academy Labs](#1-neetcode-system-design-vs-academy-labs)
2. [Design Patterns per Company](#2-design-patterns-per-company)
3. [Study Plans](#3-study-plans)
4. [Company-Specific Lab Focus](#4-company-specific-lab-focus)
5. [Books and Resources](#5-books-and-resources)

---

## 1. NeetCode System Design vs Academy Labs

NeetCode organizes system design into ~20 core design problems. Here's how each maps to Academy labs:

### NeetCode Roadmap

| # | NeetCode Problem | Academy Lab | Key Concepts |
|---|-----------------|-------------|--------------|
| 1 | Design URL Shortener | 12-design-url-shortener | Base62, key gen, caching, redirects |
| 2 | Design Pastebin | 12-design-url-shortener + 05-caching | TTL, raw storage, syntax highlighting |
| 3 | Design Twitter Feed | 13-design-chat-system + 10-real-time-collaboration | Fanout, timeline, push/pull |
| 4 | Design Instagram | 05-caching + 15-design-video-streaming | Media pipeline, feed, CDN |
| 5 | Design Dropbox | 06-messaging + 12-event-sourcing | Sync, conflict resolution, block storage |
| 6 | Design Messenger/Chat | 13-design-chat-system | WebSocket, presence, message ordering |
| 7 | Design YouTube | 15-design-video-streaming | Transcoding, DASH/HLS, CDN |
| 8 | Design LinkedIn | 04-databases + 06-messaging | Graph search, feed, notifications |
| 9 | Design Tinder | 02-scalability + 11-distributed-locks | Geo-indexing, swipe, matching |
| 10 | Design Uber | 02-scalability + 01-architecture-patterns | Geospatial, dispatch, ETA, surge |
| 11 | Design WhatsApp | 13-design-chat-system | E2E encryption, group chat, XMPP |
| 12 | Design Facebook Feed | 13-design-chat-system + 05-caching | Fanout, ranking, timeline |
| 13 | Design Yelp/Nearby | 09-distributed-database-design | Geohash, spatial DB, quad-tree |
| 14 | Design Ticketmaster | 07-transactions + 06-messaging | Seat reservation, queueing, optimistic locking |
| 15 | Design Google Maps | 09-distributed-database-design + 02-scalability | Routing, spatial index, traffic |
| 16 | Design API Rate Limiter | 11-rate-limiting-design | Token bucket, sliding window, Redis |
| 17 | Design E-Commerce Store | 04-databases + 07-api-design | Cart, catalog, order, inventory |
| 18 | Design Digital Wallet | 14-design-payment-system | Idempotency, ledger, reconciliation |
| 19 | Design Search Engine | 04-consistency-models + 04-databases | Crawling, indexing, PageRank |
| 20 | Design Web Crawler | 07-transactions + 06-messaging | BFS, URL frontier, rate limiting |
| 21 | Design Stock Exchange | 07-transactions + 08-consensus | Order book, matching engine, price-time |
| 22 | Design Notification System | 06-messaging + 08-observability | Push/email/SMS, templates, delivery |
| 23 | Design Key-Value Store | 09-distributed-database-design + 04-consistency-models | LSM tree, consistent hashing, quorum |
| 24 | Design Object Storage | 09-distributed-database-design + 07-transactions | Erasure coding, metadata, multi-tenancy |

### Foundation Lab Mappings

| Foundation Lab | NeetCode Concepts |
|---------------|-------------------|
| 01-architecture-patterns | System decomposition, layering, microservices |
| 02-scalability | Load balancing, horizontal scaling, QPS estimation |
| 03-availability | SLA, redundancy, failover, disaster recovery |
| 04-consistency-models | CAP, ACID vs BASE, consistency levels |
| 05-caching | Cache strategies, Redis, CDN |
| 06-messaging | Pub-sub, Kafka, RabbitMQ |
| 07-api-design | REST, GraphQL, gRPC |
| 08-observability | Monitoring, metrics, tracing, logging |

### Advanced Lab Mappings

| Advanced Lab | NeetCode Concepts |
|-------------|-------------------|
| 09-distributed-database-design | Sharding, replication, consistent hashing, quorum |
| 10-real-time-collaboration | CRDT, OT, conflict resolution, WebSocket |
| 11-rate-limiting-design | Token bucket, sliding window, distributed rate limiting |
| 12-design-url-shortener | Base62, key generation, redirection |
| 13-design-chat-system | WebSocket, message ordering, presence |
| 14-design-payment-system | Idempotency, double-entry ledger, reconciliation |
| 15-design-video-streaming | CDN, adaptive bitrate, DASH/HLS |

---

## 2. Design Patterns per Company

### Google
- **Pattern focus**: Distributed storage, indexing, web-scale systems
- **Top patterns**: Inverted index, consistent hashing, Paxos/Raft, MapReduce
- **Design philosophy**: Scale to billions, simplicity over complexity
- **Key labs**: 04-consistency-models, 09-distributed-database-design, 12-design-url-shortener

### Amazon
- **Pattern focus**: API-first microservices, data stores, fault isolation
- **Top patterns**: Event-driven, CQRS, saga pattern, leader election
- **Design philosophy**: Clean APIs, operational excellence, frugality
- **Key labs**: 07-api-design, 06-messaging, 14-design-payment-system

### Meta (Facebook)
- **Pattern focus**: Social graph, real-time, timeline generation
- **Top patterns**: Fanout (push/pull/hybrid), TAO graph, consistent hashing
- **Design philosophy**: Real-time social interaction, data-driven decisions
- **Key labs**: 13-design-chat-system, 05-caching, 10-real-time-collaboration

### Microsoft
- **Pattern focus**: Enterprise, Azure ecosystem, identity, hybrid cloud
- **Top patterns**: CQRS, event sourcing, leader election, geo-redundancy
- **Design philosophy**: Enterprise-grade, compliance, backward compatibility
- **Key labs**: 07-transactions, 12-event-sourcing, 04-databases

### Apple
- **Pattern focus**: Privacy-first, client-side processing, hardware-software
- **Top patterns**: Differential sync, CRDT, encryption, on-device processing
- **Design philosophy**: Privacy, UX simplicity, hardware co-design
- **Key labs**: 10-real-time-collaboration, 04-consistency-models, 05-caching

### Netflix
- **Pattern focus**: Fault tolerance, CDN, content delivery, resilience
- **Top patterns**: Circuit breaker, bulkhead, chaos engineering, sagas
- **Design philosophy**: Resilience through chaos, CDN-first, data-driven
- **Key labs**: 05-caching, 03-availability, 08-observability

### Uber
- **Pattern focus**: Real-time logistics, marketplace, geospatial
- **Top patterns**: CQRS, event sourcing, geo-indexing, surge pricing
- **Design philosophy**: Real-time marketplace, geospatial-first, reliability
- **Key labs**: 02-scalability, 09-distributed-database-design, 06-messaging

### Stripe
- **Pattern focus**: Payment processing, API design, financial systems
- **Top patterns**: Idempotency, saga pattern, event-driven, double-entry
- **Design philosophy**: API as product, data integrity, security
- **Key labs**: 14-design-payment-system, 07-api-design, 07-transactions

### Twitter/X
- **Pattern focus**: Real-time timeline, search, trends
- **Top patterns**: Fanout, inverted index, time-decay counting
- **Design philosophy**: Real-time information spread, platform health
- **Key labs**: 13-design-chat-system, 05-caching, 04-consistency-models

### DoorDash
- **Pattern focus**: Logistics, dispatch, marketplace
- **Top patterns**: Constraint optimization, real-time tracking, Elasticsearch
- **Design philosophy**: Three-sided marketplace, logistics-driven
- **Key labs**: 06-messaging, 02-scalability, 09-distributed-database-design

### TikTok
- **Pattern focus**: Video processing, content recommendation, moderation
- **Top patterns**: Recommendation pipeline, multi-stage ranking, CDN
- **Design philosophy**: AI-first content discovery, global compliance
- **Key labs**: 15-design-video-streaming, 05-caching, 03-availability

---

## 3. Study Plans

### 4-Week Crash Plan (for imminently scheduled interviews)

| Week | Focus | Labs | Practice |
|------|-------|------|----------|
| 1 | Foundation | 01 (architecture), 02 (scalability), 05 (caching), 07 (API design) | Estimation drills, sketch 2 systems |
| 2 | Data & Messaging | 04 (consistency), 06 (messaging), 09 (dist DB) | Deep dive CAP, sharding, pub-sub |
| 3 | Core Designs | 12 (URL shortener), 13 (chat), 11 (rate limiter) | Mock interview for each |
| 4 | Advanced + Review | 14 (payment), 15 (video), 10 (collaboration) | Full mock interviews, review weak areas |

### 8-Week Comprehensive Plan

| Week | Focus | Labs | Additional |
|------|-------|------|------------|
| 1 | Architecture & Scale | 01, 02 | DDIA Ch.1-4 |
| 2 | Availability & Consistency | 03, 04 | DDIA Ch.5-6 |
| 3 | Caching & Messaging | 05, 06 | Read *System Design Interview* (Alex Xu) |
| 4 | API Design & Observability | 07, 08 | Practice API design questions |
| 5 | Distributed Data | 07-transactions, 08-consensus, 09 | DDIA Ch.7-9 |
| 6 | Real-time & Rate Limiting | 10, 11 | Build rate limiter project |
| 7 | Core System Designs | 12, 13, 15 | 3 mock interviews/week |
| 8 | Complex Systems | 14, payment, ecommerce | 3-5 full mock interviews |

### 12-Week Deep Dive Plan

| Weeks | Focus | Labs | Reading |
|-------|-------|------|---------|
| 1-2 | Foundation | 01, 02, 03 | DDIA Ch.1-2, Alex Xu Vol 1 |
| 3-4 | Distributed Systems | 04, 05, 06 | DDIA Ch.3-6 |
| 5-6 | API & Observability | 07, 08 | DDIA Ch.7-8 |
| 7-8 | Advanced Data | 09, 10, 11 | DDIA Ch.9, Papers (Dynamo, Bigtable) |
| 9-10 | Applied Design | 12, 13, 14 | Alex Xu Vol 2 |
| 11-12 | Streaming & Review | 15 + Full reviews | Mock interviews, all labs |

---

## 4. Company-Specific Lab Focus

### Google Interview Prep
- **Must-master labs**: 04-consistency-models, 09-distributed-database-design, 08-observability, 02-scalability
- **NeetCode design problems**: URL Shortener, Search Engine, YouTube, Google Maps, Key-Value Store
- **Additional focus**: Distributed consensus (Raft/Paxos), Bigtable/GFS/Spanner papers
- **Critical concepts**: CAP theorem, consistent hashing, quorum, MapReduce, columnar storage

### Amazon Interview Prep
- **Must-master labs**: 07-api-design, 14-design-payment-system, 06-messaging, 04-databases
- **NeetCode design problems**: E-Commerce, Digital Wallet, Object Storage, Key-Value Store, Rate Limiter
- **Additional focus**: DynamoDB paper, S3 architecture, LPs (Customer Obsession, Ownership)
- **Critical concepts**: Idempotency, event-driven architecture, eventual consistency, partition tolerance

### Meta Interview Prep
- **Must-master labs**: 13-design-chat-system, 05-caching, 10-real-time-collaboration, 01-architecture-patterns
- **NeetCode design problems**: Facebook Feed, Messenger, Instagram, WhatsApp, Live Streaming
- **Additional focus**: TAO (Facebook's distributed graph), fanout patterns, timeline generation
- **Critical concepts**: Social graph, push vs pull, ranking ML pipeline, data center topology

### Microsoft Interview Prep
- **Must-master labs**: 07-transactions, 12-event-sourcing, 04-databases, 08-observability
- **NeetCode design problems**: E-Commerce, Pastebin, Object Storage, Notification System, Stock Exchange
- **Additional focus**: Azure architecture patterns, enterprise compliance, hybrid cloud
- **Critical concepts**: CQRS, event sourcing, geo-replication, Azure Cosmos DB consistency levels

### Uber Interview Prep
- **Must-master labs**: 02-scalability, 09-distributed-database-design, 06-messaging, 01-architecture-patterns
- **NeetCode design problems**: Uber, Nearby/Yelp, ETA, Real-time Tracking, Surge Pricing
- **Additional focus**: Geospatial indexing (H3, S2), dispatch optimization, marketplace dynamics
- **Critical concepts**: Geohash, A* routing, time-dependent routing, supply/demand equilibrium

### Netflix Interview Prep
- **Must-master labs**: 05-caching, 03-availability, 08-observability, 15-design-video-streaming
- **NeetCode design problems**: YouTube, Video Streaming, CDN Design, Recommendation System
- **Additional focus**: Chaos Monkey, Hystrix, Open Connect, evCache (Netflix caching)
- **Critical concepts**: Circuit breaker, bulkhead, graceful degradation, adaptive bitrate

---

## 5. Books and Resources

### Essential Books

| Book | Author | Key Chapters for SD Interviews |
|------|--------|-------------------------------|
| **Designing Data-Intensive Applications** | Martin Kleppmann | Ch.1-12 (entire book is essential) |
| **System Design Interview — Vol 1** | Alex Xu | All 12 design problems |
| **System Design Interview — Vol 2** | Alex Xu | All 12 advanced design problems |
| **Clean Architecture** | Robert C. Martin | Architecture patterns, dependency inversion |
| **Building Microservices** | Sam Newman | Service decomposition, integration patterns |
| **Database Internals** | Alex Petrov | LSM trees, B-trees, storage engines |
| **Streaming Systems** | Akidau, Chernyak, Lax | Windowing, watermarks, exactly-once |

### Must-Read Papers

| Paper | Why It Matters |
|-------|----------------|
| **DynamoDB** (Amazon) | Eventually consistent key-value store, consistent hashing, vector clocks |
| **Bigtable** (Google) | Column-family storage, SSTables, compaction |
| **MapReduce** (Google) | Distributed computation framework |
| **Spanner** (Google) | Globally distributed SQL DB, TrueTime, external consistency |
| **Kafka** (LinkedIn) | Distributed log, pub-sub at scale |
| **Chubby** (Google) | Distributed lock service, Paxos |
| **Raft** (Stanford) | Understandable consensus algorithm |
| **TAO** (Facebook) | Distributed graph store for social graph |
| **S4** (Twitter) | Real-time stream processing |
| **Google File System** | Distributed file system, master architecture |
| **Amazon S3** | Object storage, erasure coding, multi-tenancy |
| **ZooKeeper** | Distributed coordination service |

### Online Resources

| Resource | URL | Content |
|----------|-----|---------|
| System Design Primer | github.com/donnemartin/system-design-primer | Comprehensive SD study guide |
| NeetCode SD Roadmap | neetcode.io/roadmap | Curated SD problem list |
| ByteByteGo | bytebytego.com | SD visual explanations |
| High Scalability | highscalability.com | Real-world architecture writeups |
| InfoQ Architecture | infoq.com/architecture | Tech talks, presentations |
| Papers We Love | github.com/papers-we-love | CS paper discussions |
| System Design Alex Xu | youtube.com/@alexxusystemdesign | SD video walkthroughs |
| Gaurav Sen | youtube.com/@gkcs | SD deep dives |

### For Each Company

| Company | Must-Read Papers | Recommended Labs |
|---------|-----------------|-----------------|
| Google | GFS, Bigtable, MapReduce, Spanner, Chubby | 04-consistency-models, 09-dist-db |
| Amazon | DynamoDB, S3, 6-page memo on LPs | 07-api-design, 14-payment |
| Meta | TAO, Cassandra (origin), Haystack (photo store) | 13-chat, 05-caching |
| Microsoft | Cosmos DB consistency, Orleans virtual actors | 07-transactions, 04-databases |
| Apple | FoundationDB papers | 10-collaboration, 04-consistency |
| Netflix | Hystrix, Eureka, Chaos Engineering papers | 03-availability, 08-observability |
| Uber | H3 geospatial, DocStore (Schemaless) | 09-dist-db, 02-scalability |
| Stripe | Idempotency blog, API versioning strategy | 14-payment, 07-api-design |
| Twitter | Earlybird, Manhattan (KV store) | 13-chat, 05-caching |
| DoorDash | Dispatch optimization blog series | 02-scalability, 06-messaging |
| TikTok | Monolith to microservices case study | 15-video, 03-availability |
