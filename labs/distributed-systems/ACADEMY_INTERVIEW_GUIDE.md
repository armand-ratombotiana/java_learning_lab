# Distributed Systems Academy - Interview Preparation Guide

> Comprehensive interview prep for distributed systems roles at top tech companies.

---

## Table of Contents
1. [Google](#google)
2. [Amazon](#amazon)
3. [Meta](#meta)
4. [Microsoft](#microsoft)
5. [Apple](#apple)
6. [Netflix](#netflix)
7. [Uber](#uber)
8. [Stripe](#stripe)
9. [LinkedIn](#linkedin)
10. [Confluent](#confluent)
11. [Databricks](#databricks)

---

## Google

### How Google Tests Distributed Systems Knowledge

Google tests distributed systems extensively across all SWE and SRE interviews. Their approach:

- **System Design Rounds**: 2-3 rounds focused on large-scale distributed system design. Interviewers expect knowledge of Google's internal systems (Spanner, Bigtable, Borg, Omega) and how they solve specific problems.
- **SRE-Specific Rounds**: Infrastructure design, incident response, capacity planning, and automation.
- **Googleyness & Leadership**: Distributed systems projects often come up in behavioral rounds.

### Top 10 DS Questions at Google

1. **Design Google File System (GFS)**
   - Frame: Single master, chunk servers, 64MB chunks, append-heavy workloads
   - Answer: Describe master for metadata, chunkservers for data, lease-based mutation order, heartbeat for failure detection, shadow masters for read scaling

2. **Design Google Spanner**
   - Frame: Globally distributed SQL database, TrueTime API, 2PC, Paxos
   - Answer: Use TrueTime for external consistency, Paxos for replication within zones, 2PC across zones, synchronous replication, automatic failover

3. **Design Google Bigtable**
   - Frame: Wide-column NoSQL, SSTables, GFS, Chubby
   - Answer: Tablet servers, SSTable + Memtable structure, compactions, Bloom filters, Chubby for leader election

4. **Design YouTube**
   - Frame: Video upload/streaming at global scale
   - Answer: Upload servers, transcoding pipeline, CDN distribution, geo-replicated storage, recommendation system

5. **Design Google Search**
   - Frame: Crawling, indexing, serving at web scale
   - Answer: Crawler fleet, inverted index sharding, MapReduce for index building, replica sets for serving, query rewriting

6. **Design Google Maps**
   - Frame: Global mapping with real-time traffic
   - Answer: Tile-based rendering, quadtree indexing, real-time data pipelines, ETA computation via graph partitioning

7. **Design a Distributed Queue (Google Pub/Sub)**
   - Frame: At-least-once delivery, ordered messages
   - Answer: Pull/push models, message partitioning, acknowledgment deadlines, dead letter queues

8. **Design a Distributed Cache**
   - Frame: Memcache at Google scale
   - Answer: Consistent hashing, replica sets for fault tolerance, client-side caching with invalidation, hot key handling

9. **Design Google's Load Balancer**
   - Frame: Global and regional load balancing
   - Answer: DNS-based GSLB, health checks, flow-based consistent hashing for sticky sessions, circuit breakers

10. **Design a Monitoring System (Borgmon/Monarch)**
    - Frame: Alerting at google scale
    - Answer: Time-series database, query language (Monarch), alert rules, hierarchical aggregation

### System Design Frameworks

**Google's Preferred Framework**: The "Caching/CAP/Chat" approach modified:
1. **Clarify requirements**: Scale (QPS, storage), consistency needs, latency SLOs
2. **Estimate scale**: Compute DAU, QPS, storage per day, bandwidth
3. **Define data model**: Schema, storage choice (Bigtable vs Spanner vs Colossus)
4. **High-level design**: Components, data flow
5. **Deep dive**: Partitioning, replication, consistency, fault tolerance
6. **Trade-offs**: CAP tradeoffs, cost analysis, operational complexity

---

## Amazon

### How Amazon Tests Distributed Systems Knowledge

Amazon's interview process emphasizes Leadership Principles through the lens of distributed systems:

- **Bar Raiser**: Ensures distributed systems depth
- **System Design Focus**: Scalability, fault isolation, cost optimization
- **AWS Knowledge**: Expect familiarity with Dynamo, S3, EC2 architecture

### Top 10 DS Questions at Amazon

1. **Design Amazon DynamoDB**
   - Frame: Consistent hashing, vector clocks, hinted handoff
   - Answer: Ring-based partitioning, NWR quorum model, Merkle trees for anti-entropy, gossiping for membership

2. **Design Amazon S3**
   - Frame: Eventually consistent object storage (now strongly consistent)
   - Answer: Object key => partition mapping, replication across AZs, metadata store, CRUSH for placement

3. **Design Amazon's Shopping Cart**
   - Frame: Session management at global scale
   - Answer: Session storage in DynamoDB, multi-AZ replication, optimistic locking for concurrent edits

4. **Design a Rate Limiter**
   - Frame: API rate limiting for AWS services
   - Answer: Token bucket, leaky bucket, distributed counters with Redis, hierarchical rate limiting

5. **Design Amazon's Recommendation Engine**
   - Frame: Item-to-item collaborative filtering
   - Answer: Offline computation via Spark/MapReduce, online serving via DynamoDB, A/B testing framework

6. **Design a Distributed Locking Service**
   - Frame: Amazon S3/ DynamoDB-based locks
   - Answer: Lease-based locks with TTL, fencing tokens, optimistic concurrency via conditional writes

7. **Design an Event-Driven Architecture**
   - Frame: SQS + SNS + Lambda
   - Answer: Decoupled services, DLQ handling, idempotent consumers, at-least-once delivery

8. **Design a Global Deployment System**
   - Frame: Multi-region deployment
   - Answer: CloudFormation, canary deployments, cell-based architecture, traffic shifting

9. **Design a Metrics Aggregation System**
   - Frame: CloudWatch-like service
   - Answer: Sharded time-series DB, aggregation pipelines, percentile computation, alarm evaluation

10. **Design Amazon's Key Management System**
    - Frame: HSM-backed encryption at scale
    - Answer: Key hierarchy (master keys, data keys), envelope encryption, key rotation

### Amazon's Unique Evaluation Criteria

- **Leadership Principles**: "Have Backbone" - defend design decisions. "Bias for Action" - pragmatic tradeoffs
- **Scale Awareness**: Always quantify (millions of customers, petabytes of data)
- **Cost Optimization**: AWS services are metered; design must account for cost
- **Operational Excellence**: Monitoring, alarming, deployment, rollback

---

## Meta

### How Meta Tests Distributed Systems Knowledge

Meta focuses on empirically-proven systems at massive scale:

- **System Design**: 2 dedicated rounds
- **Production Engineering**: Infrastructure, automation, reliability
- **Real-world Scale**: Discuss Facebook/Meta's actual systems (TAO, Haystack, Presto)

### Top 10 DS Questions at Meta

1. **Design News Feed**
   - Frame: Fan-out on write vs pull
   - Answer: Hybrid approach - fan-out to active users, pull for inactive, ranking service, notification system

2. **Design Facebook Messenger/Chat**
   - Frame: Real-time messaging at billions
   - Answer: Long-polling -> WebSocket migration, message storage (InnoDB/MyRocks), presence service, end-to-end encryption

3. **Design Facebook's Photo Storage (Haystack)**
   - Frame: Blob storage for billions of photos
   - Answer: Haystack stores multiple photos per file, in-memory index mapping, minimized metadata overhead

4. **Design Instagram Feed**
   - Frame: Media-heavy timeline
   - Answer: Fan-out on write, ranked timeline via machine learning, caching via Redis/Memcached

5. **Design a Distributed Graph (TAO)**
   - Frame: Social graph storage
   - Answer: TAO - cache-first architecture, association lists, read-through/write-through cache, fault-tolerant cache tier

6. **Design WhatsApp**
   - Frame: Billion-user messaging
   - Answer: Erlang/FreeBSD, custom MQTT-like protocol, in-memory sessions, offline message storage (7 days)

7. **Design Facebook Search**
   - Frame: Unicorn - entity search at scale
   - Answer: Inverted index per entity type, real-time indexing pipeline, early termination for performance

8. **Design a Video Recommendation System**
   - Frame: Watch next
   - Answer: Collaborative filtering + content-based features, feature store, candidate generation, ranking, re-ranking

9. **Design a Distributed Counter**
   - Frame: Likes, shares, comments
   - Answer: Pre-sharded counters, batching writes, async reconciliation, CRDTs for consistency

10. **Design Live Video Streaming**
    - Frame: Facebook Live
    - Answer: RTMP ingest, transcoding pipeline, CDN distribution, adaptive bitrate, chat via IRC-style fan-out

### Meta's Evaluation Criteria

- **Speed Matters**: 2-3 design rounds in 45 mins each
- **Empiricism**: Know what Meta actually built
- **Scalability First**: Design for billions of users
- **Pragmatism**: Tradeoffs only matter at scale

---

## Microsoft

### Top 10 DS Questions at Microsoft

1. **Design Azure Cosmos DB**
   - Answer: Multi-master, global distribution, consistency levels (Eventual, Consistent Prefix, Session, Bounded Staleness, Strong), partitioning via hash/range

2. **Design Azure Storage**
   - Answer: StreamLayer (NTFS-based), PartitionLayer (Range partitioning), FrontEnd layer, geo-redundant storage

3. **Design Azure Active Directory**
   - Answer: Multi-tenant, geo-distributed, AD FS federation, token caching

4. **Design Teams Backend**
   - Answer: Chat via Azure SignalR, file sharing via SharePoint/OneDrive, meetings via Skype/Media Stack

5. **Design Outlook/Exchange Online**
   - Answer: MAPI over HTTP, mailbox sharding across databases, replication via continuous replication, failover clustering

6. **Design Azure SQL Database**
   - Answer: SQL Server + Fabric layer, geo-replication, automatic tuning, intelligent insights

7. **Design a Distributed Job Scheduler**
   - Answer: Azure Scheduler - partition tickets, clock skew handling, retry policies, exponential backoff

8. **Design a Monitoring Service**
   - Answer: Azure Monitor - metrics ingestion pipeline, log analytics, alerting engine, action groups

9. **Design a Key-Value Store**
   - Answer: Azure Table Storage - partition key + row key, partition migration for load balancing

10. **Design CDN**
    - Answer: Azure CDN - edge nodes, origin shielding, dynamic site acceleration, rules engine

---

## Apple

### Top 10 DS Questions at Apple

1. **Design iCloud**
   - Frame: Personal cloud storage
   - Answer: Zones for geo-distribution, CKS (CloudKit) for record storage, file chunking for efficiency, end-to-end encryption

2. **Design iMessage**
   - Frame: Secure messaging at device scale
   - Answer: APNs for push, CloudKit for message sync, end-to-end encryption, sender/receiver device sync

3. **Design Apple Maps**
   - Frame: Privacy-first mapping
   - Answer: Tile serving via CDN, vector tiles for performance, differential privacy for data collection

4. **Design App Store Infrastructure**
   - Frame: Global app delivery
   - Answer: CDN for large binaries, phased rollouts, account-based geo-restrictions

5. **Design Siri Backend**
   - Frame: Voice processing at scale
   - Answer: ASR pipeline, NLP service mesh, on-device vs cloud processing split, differential privacy

6. **Design Photos Sync**
   - Frame: Across iCloud devices
   - Answer: Asset upload, thumbnail generation, face detection pipeline, metadata in CloudKit

7. **Design Apple Pay**
   - Frame: Payment processing
   - Answer: Tokenization, HSM for secure element, transaction log, fraud detection

8. **Design Find My Network**
   - Frame: Crowdsourced device finding
   - Answer: Bluetooth mesh, encrypted crowdsourcing, offline finding network

9. **Design HomeKit Hub**
   - Frame: Smart home control
   - Answer: Thread/BLE protocol bridge, HomePod/Apple TV as hub, end-to-end encryption

10. **Design iTunes Match**
    - Frame: Music library sync
    - Answer: Audio fingerprinting for matching, DRM wrapping for matched songs, upload for unmatched

---

## Netflix

### Top 10 DS Questions at Netflix

1. **Design Netflix CDN (Open Connect)**
   - Frame: ISP-embedded CDN
   - Answer: OCA appliances at ISP peering points, predictive prefetching, adaptive bitrate (ABR) algorithms

2. **Design Netflix Recommendation System**
   - Frame: ML-driven content discovery
   - Answer: Offline (Spark) + online (Elasticsearch) hybrid, A/B testing at scale, bandit algorithms

3. **Design Netflix's Content Pipeline**
   - Frame: Studio to screen
   - Answer: Content ingestion, transcoding profiles, QC automation, packaging into DASH/HLS

4. **Design Chaos Engineering Platform**
   - Frame: Failure injection at scale
   - Answer: Chaos Monkey, Simian Army, fault injection proxies, blast radius controls

5. **Design Netflix's Video Encoding**
   - Frame: Per-title encoding optimization
   - Answer: Dynamic optimizer, per-title bitrate ladder, VMAF-based quality metric

6. **Design Netflix's Data Platform**
   - Frame: Genie, Spinnaker, Atlas
   - Answer: Workflow orchestration, continuous delivery, time-series monitoring

7. **Design a Global Traffic Management System**
   - Frame: DNS-based routing
   - Answer: Route53-based GSLB, region failover, canary deployment via traffic steering

8. **Design Zuul - API Gateway**
   - Frame: Edge service architecture
   - Answer: Filter chain architecture, dynamic routing, rate limiting, metrics collection

9. **Design Hystrix Circuit Breaker**
   - Frame: Resilience at API level
   - Answer: Circuit breaker pattern, bulkhead isolation, fallback mechanisms, metrics for health

10. **Design Eureka Service Discovery**
    - Frame: Registry at cloud scale
    - Answer: Peer-to-peer registration, heartbeats, TTL-based eviction, load-aware routing

---

## Uber

### Top 10 DS Questions at Uber

1. **Design Ride Matching**
   - Frame: Real-time dispatch
   - Answer: Geohashing for nearby drivers, bipartite matching (Hungarian), supply-demand forecasting, surge pricing

2. **Design Uber's Geospatial Index**
   - Frame: H3 hex grid system
   - Answer: Hierarchical hexagons, dynamic resolution, aggregation polygons for pricing

3. **Design Uber's Real-time Map**
   - Frame: Driver location tracking
   - Answer: WebSocket connections, Ringpop for consistent hashing, Kafka for location event stream

4. **Design Uber Eats**
   - Frame: Food delivery
   - Answer: Restaurant discovery, order dispatch via FIFO/scheduling, preparation time prediction, Dasher assignment

5. **Design Uber's Payment Platform**
   - Frame: Multi-currency, multi-provider
   - Answer: Payment provider abstraction, idempotency keys, split payments, refund handling

6. **Design Uber's Trip Service**
   - Frame: Trip state machine
   - Answer: Schemaless datastore (MySQL + DocStore), state machine via Apache Camel, event-driven updates

7. **Design Uber's Pricing Engine**
   - Frame: Dynamic pricing
   - Answer: Supply-demand curves, geo-quota balancing, threshold-based surge, marketplace efficiency optimization

8. **Design Uber's Notification Service**
   - Frame: Multi-channel (push, SMS, email)
   - Answer: Priority queue, channel preference management, templating engine, delivery guarantees

9. **Design Uber's Fraud Detection System**
   - Frame: Real-time risk
   - Answer: Feature extraction pipeline, ML model serving, rule engine overlay, risk tiers per action

10. **Design Uber's OLAP Platform**
    - Frame: Presto/Hive for analytics
    - Answer: Query federation, columnar storage (Parquet), Hive metastore, Presto coordinator/worker architecture

---

## Stripe

### Top 10 DS Questions at Stripe

1. **Design a Payment Processing System**
   - Frame: Idempotency, retries, idempotency keys
   - Answer: Idempotency key on API layer, payment state machine, idempotent replay, double-entry ledger

2. **Design a Fraud Detection System**
   - Frame: Real-time ML risk scoring
   - Answer: Feature engineering pipeline, model ensemble, rule engine, adaptive thresholds

3. **Design a Webhook System**
   - Frame: At-least-once delivery
   - Answer: Delivery queues per endpoint, exponential backoff, signature verification, dead letter queues

4. **Design a Billing System**
   - Frame: Subscription management
   - Answer: Usage metering, invoice generation, payment collection via recurring schedules, proration

5. **Design a Rate Limiter**
   - Frame: API key-based
   - Answer: Sliding window counters in Redis, hierarchical rate limits per key/user/organization

6. **Design a Global Payment Network**
   - Frame: Multi-currency, multi-method
   - Answer: Payment method abstraction, FX layer, settlement optimization, routing to cheapest processor

7. **Design a Ledger System**
   - Frame: Double-entry accounting
   - Answer: Immutable log, balance computation via log scanning, consistency checks, audit trails

8. **Design a Tax Computation System**
   - Frame: Multi-jurisdiction tax
   - Answer: Tax rule engine, address normalization, product taxability, exemption handling

9. **Design Stripe Connect**
   - Frame: Platform payment facilitation
   - Answer: Merchant onboarding via KYC, split payments, payout scheduling, dispute handling

10. **Design a Reconciliation System**
    - Frame: Matching internal + bank records
    - Answer: Batch matching, fuzzy matching for minor differences, exception handling, settlement reporting

---

## LinkedIn

### Top 10 DS Questions at LinkedIn

1. **Design LinkedIn Feed**
   - Frame: Professional content relevance
   - Answer: Fan-out on write for VIPs, pull for regular users, ranking via ML (feed relevance), content moderation

2. **Design Connections Graph**
   - Frame: Social graph at scale
   - Answer: Graph DB (LIquid), adjacency list in KV store, path queries via BFS with pruning

3. **Design LinkedIn Search**
   - Frame: People, jobs, companies
   - Answer: Galene - Lucene-based distributed search, real-time indexing, faceted search, relevance scoring

4. **Design LinkedIn Messaging**
   - Frame: InMail system
   - Answer: Conversation indexing, thread storage in Espresso (KV store), read receipts, typing indicators

5. **Design LinkedIn's Recruiter Products**
   - Frame: Search + CRM
   - Answer: Indexed profile search, candidate tracking, InMail automation, pipeline management

6. **Design Kafka at LinkedIn**
   - Frame: Event streaming
   - Answer: Topic/partition model, offset management, consumer groups, replication between brokers

7. **Design Skills Graph**
   - Frame: Skills inference
   - Answer: Entity extraction from profiles, skill co-occurrence graph, skill endorsement propagation

8. **Design A/B Testing Platform**
   - Frame: Experimentation at scale
   - Answer: Assignment service via consistent hashing, metric computation pipeline, statistical significance

9. **Design Identity Graph**
   - Frame: Cross-device/property identity
   - Answer: Deterministic matching (email/phone), probabilistic matching (device graph), privacy controls

10. **Design LinkedIn Notifications**
    - Frame: Multi-channel delivery
    - Answer: Preference service, digest grouping, priority ordering, push/SMS/email delivery

---

## Confluent

### Top 10 DS Questions at Confluent

1. **Design Kafka's Log Compaction**
   - Frame: Topic with latest value semantics
   - Answer: Segmented log, cleaner deletes duplicate keys, tombstone records, offset mapping

2. **Design Kafka's Partition Rebalancing**
   - Frame: Consumer group rebalance
   - Answer: Cooperative sticky rebalancing, incremental rebalance, partition assignment strategy

3. **Design Kafka's Exactly-Once Semantics**
   - Frame: Idempotent producer + transactional
   - Answer: Producer ID + sequence number, transaction coordinator, commit/abort markers in log

4. **Design Kafka Connect**
   - Frame: Source/sink connectors
   - Answer: Connector abstraction, offset management, rebalancing, single message transforms

5. **Design Kafka Streams**
   - Frame: Stream processing library
   - Answer: Task parallelism, state stores (RocksDB), exactly-once processing, interactive queries

6. **Design Schema Registry**
   - Frame: Schema evolution
   - Answer: Avro/Protobuf/JSON Schema, backward/forward/full compatibility, schema reference management

7. **Design KRaft (Kafka without ZooKeeper)**
   - Frame: Self-managed consensus
   - Answer: Raft-based metadata quorum, controller epoch, metadata log replay

8. **Design ksqlDB**
   - Frame: Streaming SQL
   - Answer: SQL parser -> logical plan -> physical plan, persistent vs transient queries, pull queries

9. **Design Kafka's Replication Protocol**
   - Frame: Leader-follower with ISR
   - Answer: In-Sync Replica set, leader election via ISR, unclean leader election tradeoffs, fetch requests

10. **Design Kafka Monitoring Platform**
    - Frame: Cruise Control
    - Answer: Metric collection from brokers, goal-based rebalance proposals, anomaly detection

---

## Databricks

### Top 10 DS Questions at Databricks

1. **Design Spark's Execution Engine**
   - Frame: DAG scheduler, query optimizer
   - Answer: Catalyst optimizer, Tungsten execution, whole-stage codegen, adaptive query execution

2. **Design Delta Lake**
   - Frame: ACID transactions on data lake
   - Answer: Transaction log (delta log), optimistic concurrency, time travel, schema enforcement/evolution

3. **Design Spark Shuffle**
   - Frame: Data redistribution
   - Answer: Sort-based shuffle (Tungsten-sort), hash shuffle, push-based shuffle, shuffle service

4. **Design Spark Structured Streaming**
   - Frame: Stream processing on Spark
   - Answer: Micro-batch execution, event-time processing, watermarking, state management

5. **Design a Distributed File System for ML (DBFS)**
   - Frame: POSIX-like on cloud storage
   - Answer: FUSE mount, metadata caching, block-level caching, cloud storage abstraction

6. **Design MLflow**
   - Frame: ML lifecycle management
   - Answer: Experiment tracking, model registry, project packaging, MLflow serving

7. **Design a Multi-Tenant Cluster Manager**
   - Frame: Resource isolation
   - Answer: Gang scheduling for Spark, dynamic allocation, resource profiles (min/max), preemption

8. **Design Databricks SQL Analytics**
   - Frame: SQL warehouse
   - Answer: Photon execution engine, Delta caching, auto-scaling clusters, serverless SQL

9. **Design Unity Catalog**
   - Frame: Data governance
   - Answer: Three-level namespace (catalog.schema.table), RBAC, lineage tracking, metastore federation

10. **Design AutoML Platform**
    - Frame: Automated ML pipeline
    - Answer: Feature engineering automation, algorithm selection, hyperparameter tuning via Bayesian optimization

---

## Cross-Company Question Mapping

| Question | Google | Amazon | Meta | Microsoft | Netflix |
|----------|--------|--------|------|-----------|---------|
| Design KV Store | Yes | DynamoDB | TAO | Cosmos DB | EVCache |
| Design File Store | GFS | S3 | Haystack | Azure Storage | - |
| Design Queue | Pub/Sub | SQS | Kafka | Service Bus | - |
| Design Monitoring | Borgmon | CloudWatch | Scuba | Azure Monitor | Atlas |
| Design Cache | Memcache | DAX | Memcached | Redis Cache | EVCache |
| Design Consensus | Paxos | Multi-master | Paxos | Paxos | Raft |

---

## General Interview Strategy

### For SWE Roles
1. Master CAP theorem tradeoffs with real-world examples
2. Build mental models for at least 3 design patterns (Leader election, Quorum, Gossip)
3. Practice 8+ system design problems
4. Know at least one Paxos/Raft flow end-to-end

### For SRE/Infrastructure Roles
1. Deep understanding of failure modes in distributed systems
2. Incident response process (SEV -> Mitigation -> Root Cause -> Prevention)
3. Monitoring and observability design
4. Capacity planning methodology

### For Data Infrastructure Roles
1. Deep Kafka/Spark streaming knowledge
2. Consistency and ordering guarantees
3. Batch vs streaming tradeoffs
4. Data storage format knowledge (Parquet, Avro, ORC)

---

## Preparation Timeline

### 4-Week Sprint
- **Week 1**: CAP theorem, consistency models, replication, partitioning
- **Week 2**: Consensus algorithms, distributed transactions, caching
- **Week 3**: System design practice (4 problems/week)
- **Week 4**: Company-specific prep, mock interviews

### 8-Week Standard
- **Weeks 1-2**: All distributed systems fundamentals (18 labs)
- **Weeks 3-4**: System design patterns
- **Weeks 5-6**: Company-specific deep dives + LeetCode
- **Weeks 7-8**: Mock interviews, behavioral prep, resume prep

### 12-Week Comprehensive
- **Weeks 1-4**: DS fundamentals + LeetCode patterns
- **Weeks 5-8**: System design deep dives (8+ problems)
- **Weeks 9-10**: Company research + mock interviews
- **Weeks 11-12**: Final preparation, compensation negotiation

---

## Resources Quick Reference

| Topic | Resource | Company Focus |
|-------|----------|---------------|
| CAP Theorem | DDIA Chapter 7 | All companies |
| Paxos/Raft | Raft Paper, MIT 6.824 | All companies |
| Consistent Hashing | DDIA Chapter 6 | Amazon, Google |
| Transaction Patterns | DDIA Chapter 9 | Google, Apple |
| Replication | DDIA Chapter 5 | All companies |
| Gossip Protocol | Dynamo Paper | Amazon, LinkedIn |
| CRDTs | Riak Paper | Apple, Confluent |
| Stream Processing | Kafka Papers | Confluent, LinkedIn |
| Spark Execution | Spark Papers | Databricks |

---

## Behavioral Questions by Company

| Company | Theme | Sample Question |
|---------|-------|-----------------|
| Google | Leadership | "Tell me about a distributed system you designed" |
| Amazon | Ownership | "Describe when you owned a system end-to-end" |
| Meta | Impact | "What's the most impactful infrastructure change you made" |
| Microsoft | Growth | "When did you learn a new technology for a project" |
| Apple | Quality | "Describe a time you caught a subtle distributed systems bug" |
| Netflix | Freedom/Responsibility | "When did you take an unpopular technical decision" |

---

## Final Notes

1. **Always define your assumptions**: Explicitly state your scale assumptions before designing
2. **Visualize**: Draw diagrams for system design - use components, data flow arrows
3. **Verbalize tradeoffs**: Every design choice has tradeoffs - show you understand them
4. **Know your resume projects**: Be ready to discuss distributed systems you've built in depth
5. **Practice out loud**: Record yourself doing system design within time limits

> Remember: Distributed systems interviews test your ability to think about systems at scale, handle failure, and make reasoned tradeoffs. Depth > breadth in any single interview.