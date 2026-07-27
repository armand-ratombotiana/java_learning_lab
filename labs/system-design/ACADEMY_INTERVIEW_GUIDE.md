# System Design Academy — Comprehensive Interview Guide

> Per-company system design interview prep for 14 top tech companies.
> Covers round formats, top problems, evaluation criteria, design philosophy, and real interview stories.

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
9. [Twitter/X](#twitterx)
10. [LinkedIn](#linkedin)
11. [Spotify](#spotify)
12. [DoorDash](#doordash)
13. [TikTok](#tiktok)
14. [Universal Solution Frameworks](#universal-solution-frameworks)

---

## Google

### Interview Format
- **Round type**: Google Docs (shared doc) + whiteboard-style verbal discussion
- **Duration**: 45 min per round, typically 2-4 system design rounds
- **Levels**: L3 (SWE II) through L7 (Staff)
- **Structure**: Requirements → estimation → design → trade-off analysis
- **Focus**: Scalability, data consistency, distributed systems depth

### Top 5 System Design Problems

#### 1. Design Google Search
**Framework**: Crawl → Index → Rank → Serve
- Requirements: 100B+ pages, <100ms latency, 10B+ daily searches
- Estimations: 100M queries/day → ~1,200 QPS peak, storage in exabytes
- Design: Distributed crawler (URL frontier), inverted index (sharded by term), PageRank, query serving with spelling correction
- Trade-offs: Freshness vs index rebuild cost, precision vs recall, crawling politeness vs coverage

#### 2. Design YouTube
**Framework**: Upload → Transcode → Store → Serve
- Requirements: 500h upload/min, 1B+ users, 4K/8K support
- Design: Chunked upload, transcoding pipeline (resolution pyramid), CDN distribution, adaptive bitrate streaming
- Trade-offs: Transcoding cost vs storage cost, latency vs quality, pre-encode vs on-demand

#### 3. Design Google Maps
**Framework**: Collect → Process → Route → Render
- Requirements: Real-time traffic, 200+ countries, turn-by-turn navigation
- Design: Quad-tree spatial indexing, road network graph, Dijkstra/A* with traffic weights, tile-based map rendering
- Trade-offs: Pre-computed routes vs real-time computation, data freshness vs bandwidth

#### 4. Design Gmail
**Framework**: Accept → Store → Index → Retrieve → Push
- Requirements: 1.5B+ users, 15GB/user, <1s search, real-time delivery
- Design: Distributed storage (Colossus-like), inverted index per mailbox, IMAP/Push protocol, spam filtering pipeline
- Trade-offs: Storage per user cap vs premium tiers, search latency vs index freshness

#### 5. Design Google Drive
**Framework**: Upload → Sync → Version → Share
- Requirements: 1B+ users, conflict resolution, real-time sync
- Design: Delta sync, Merkle tree for conflict detection, chunked storage with deduplication, ACL-based sharing
- Trade-offs: Delta size vs CPU cost, consistency vs availability for sync

### Evaluation Criteria
- **Scalability mindset**: Can the design handle 10x-100x growth?
- **Data consistency**: Understands CAP trade-offs, chooses appropriate consistency model
- **Resource estimation**: Accurate QPS, storage, bandwidth calculations
- **Depth of knowledge**: Deep understanding of distributed systems fundamentals
- **Communication**: Clear articulation of design decisions and trade-offs

### Design Philosophy
Google prioritizes **scale above all else**. Designs must work at planetary scale. Engineers are expected to:
- Use redundant, fault-tolerant architectures
- Prefer simple, proven solutions over complex novel approaches
- Quantify everything (latency, throughput, cost)
- Consider data locality and minimize cross-datacenter traffic

### Real Interview Stories

**Story 1: L5 Interview — Design Google Search**
The candidate started with web crawling but the interviewer redirected to focus on the query serving path. Key moment: candidate estimated 100KB average page size, then calculated index size assuming 10% compression ratio. Interviewer pushed on how to handle trending queries (cache miss storm). Candidate proposed a hierarchical cache with ML-based pre-warming.

**Story 2: L4 Interview — Design YouTube**
Candidate spent too long on CDN design. Interviewer wanted more focus on the upload pipeline — specifically idempotency and resumable uploads. The candidate recovered by discussing chunked upload with SHA-256 checksums and a simple state machine for upload status.

**Story 3: L6 Interview — Design Google Maps ETA Prediction**
The interviewer was interested in real-time traffic data fusion — how to combine historical patterns with live sensor data. Candidate proposed a weighted Kalman filter approach. The discussion focused on handling sparse data in low-traffic areas and cold-start for new routes.

---

## Amazon

### Interview Format
- **Round type**: Whiteboard or whiteboard-style (in-person was standard, now virtual via Chime + shared document)
- **Duration**: 60 min per round, 2-5 system design rounds
- **Levels**: SDE I through Principal Engineer
- **Structure**: Requirements (with LP focus) → design → API specification → deep dive
- **Focus**: APIs, data modeling, operational excellence, frugality

### Top 5 System Design Problems

#### 1. Design Amazon Shopping Cart
**Framework**: Browse → Add → Store → Checkout → Order
- Requirements: 300M+ active users, 1B+ products, <500ms add-to-cart
- Design: Session-based cart (pre-login), persistent cart (post-login), DynamoDB for cart state, event-driven inventory check
- Trade-offs: Save-for-later vs cart retention, price locking duration, inventory reservation timeout

#### 2. Design Amazon DynamoDB
**Framework**: Partition → Replicate → Query → Scale
- Requirements: Single-digit ms latency, any scale, fully managed
- Design: Consistent hashing, three-way replication, quorum-based reads, LSM-tree storage
- Trade-offs: Read vs write optimization, strong vs eventual consistency, throughput vs storage cost

#### 3. Design Amazon S3
**Framework**: Bucket → Object → Replicate → Retrieve
- Requirements: 11 9s durability, infinite scale, global namespace
- Design: Multi-tenant metadata store, erasure coding (12+4), front-end fleet, back-end storage nodes
- Trade-offs: Latency vs durability, consistency vs availability, storage efficiency vs recovery time

#### 4. Design Amazon Prime Video Streaming
**Framework**: Ingest → Process → Package → Deliver
- Requirements: 200M+ subscribers, 4K HDR, multi-device
- Design: Adaptive bitrate (HLS/DASH), CDN hierarchy, device-aware encoding, DRM
- Trade-offs: Bitrate vs quality, CDN cost vs buffer time, pre-roll vs mid-roll ads

#### 5. Design Amazon Recommendation Engine
**Framework**: Collect → Profile → Match → Rank → Serve
- Requirements: Personalization for 300M+ users, real-time updates, cold-start handling
- Design: Collaborative filtering (item-to-item), user profile embeddings, real-time clickstream processing (Kinesis)
- Trade-offs: Real-time vs batch personalization, exploration vs exploitation, model freshness vs computation cost

### Evaluation Criteria
- **API-first thinking**: Clear interface design, RESTful principles, idempotency
- **Data modeling**: Relational vs NoSQL decisions, schema design, indexes
- **Fault tolerance**: Handles failures gracefully, retries with exponential backoff
- **Operational excellence**: Monitoring, deployment, rollback strategy
- **Leadership principles**: Customer obsession, ownership, frugality, dive deep

### Design Philosophy
Amazon prioritizes **API-driven design with operational rigor**. The "two-pizza team" philosophy means services must have clean interfaces. Key principles:
- APIs are a product — design them carefully
- Everything fails, design for failure
- Frugality drives cost-aware design
- Operational excellence is feature #1

### Real Interview Stories

**Story 1: SDE II — Design Shopping Cart**
Candidate described a monolithic cart service. Interviewer asked how to handle Prime Day traffic (100x spike). Candidate re-designed with cart sharded by customer_id and cart-async writes with DynamoDB DAX cache. The LP "Frugality" came up when discussing cache TTL vs DynamoDB read costs.

**Story 2: SDE III — Design S3-like System**
Candidate started with single-region design, interviewer pushed on multi-region replication. Key insight: candidate discussed CRDT-based conflict resolution for concurrent puts to the same key. The Bar Raiser asked about billing metering accuracy at scale.

**Story 3: Principal Engineer — Design Recommendation Platform**
Interview focused heavily on operational excellence. Candidate described canary deployments of ML models, A/B testing framework, and automated rollback when CTR drops. The "Customer Obsession" LP was probed through discussion of handling recommendation quality degradation.

---

## Meta

### Interview Format
- **Round type**: CoderPad (shared editor) + verbal
- **Duration**: 45 min per round, 2-3 system design rounds
- **Levels**: E3 (New Grad) through E7 (Staff)
- **Structure**: Conversational — requirements → estimation → key design decisions → scale
- **Focus**: Real-time systems, social graph, data consistency

### Top 5 System Design Problems

#### 1. Design Facebook News Feed
**Framework**: Produce → Rank → Filter → Deliver
- Requirements: 2B+ DAU, 1,500 stories/day/user, <500ms load time
- Design: Fanout-on-write (celebrities: fanout-on-read), ML ranking pipeline, feed storage (TAO), real-time aggregation
- Trade-offs: Fanout-on-write vs fanout-on-read, ranking latency vs freshness, cross-platform vs unified feed

#### 2. Design Facebook Messenger
**Framework**: Send → Route → Deliver → Store → Sync
- Requirements: 1B+ users, <100ms delivery, multi-device sync
- Design: WebSocket-based real-time connection, MQTT-like protocol, distributed message store (Cassandra), encryption
- Trade-offs: At-least-once vs exactly-once delivery, message ordering vs latency, online vs offline message handling

#### 3. Design Instagram
**Framework**: Capture → Process → Store → Distribute → Discover
- Requirements: 1B+ users, 300M+ daily stories, photo/video sharing
- Design: Feed via fanout-on-write, media pipeline (resize, filter, compress), CDN for media, graph API for discover
- Trade-offs: Image quality vs bandwidth, feed freshness vs computational cost, chronological vs algorithmic ranking

#### 4. Design Facebook Live Streaming
**Framework**: Ingest → Transcode → Distribute → Play
- Requirements: 2B+ viewers, <1s latency, interactive comments
- Design: RTMP/WebRTC ingestion, low-latency DASH, distributed transcoding, real-time comment fanout
- Trade-offs: Viewer latency vs transcoding quality, comment ordering consistency vs throughput, geographic distribution vs edge cost

#### 5. Design WhatsApp
**Framework**: Register → Connect → Send → Deliver → Store
- Requirements: 2B+ users, 100B+ messages/day, e2e encryption
- Design: XMPP protocol with extensions, message store (HBase/Eyrie), e2e encryption (Signal protocol), group chat with sender fanout
- Trade-offs: Delivery guarantees vs latency, message history retention vs privacy, group fanout vs server load

### Evaluation Criteria
- **Speed vs quality balance**: Can they deliver a solid design in 45 min?
- **Data system design**: Understands graph data, timeline, real-time constraints
- **Trade-off articulation**: Explicitly states trade-offs and justifies choices
- **Collaboration**: Responds well to hints, conversational style
- **Product sense**: Design choices consider user experience impact

### Design Philosophy
Meta prioritizes **real-time social interaction at massive scale**. Core principles:
- Move fast with infrastructure (iterate quickly on system design)
- Social graph is the fundamental data structure
- Optimize for user growth and engagement
- Data-driven decisions (everything is measured)

### Real Interview Stories

**Story 1: E5 — Design News Feed**
Candidate proposed fanout-on-write for everyone. Interviewer asked about celebrities (100M+ followers). Candidate pivoted to hybrid: fanout-on-write for regular users (<5K followers), fanout-on-read for celebrities. Hot discussion on cold start for new users — candidate suggested seeding feed with trending content.

**Story 2: E4 — Design Instagram Stories**
Candidate focused on media pipeline but interviewer wanted more on the stories-specific delete-after-24h mechanism. Candidate discussed TTL-based expiry with background garbage collection and lazy deletion. Product sense question: "How would you measure story engagement?"

### Design YouTube

**Framework**: Upload → Transcode → Store → Serve → Recommend

**Requirements**:
- 1B+ users, 500 hours of video uploaded per minute
- Support 4K, HDR, 60fps content
- <200ms playback start, global delivery

**Estimation**:
- Storage: 500h/min × 60min × 500MB/h = ~15TB/min new video, ~22PB/day
- CDN bandwidth: 1B users × 30min/day × 10Mbps = ~200Tbps peak
- Transcoding compute: 500h/min × 10min/h transcoding = 5,000 compute-minutes/min

**Design**:
- Upload: Chunked resumable uploads via HTTP, metadata in Cloud SQL
- Transcode: Job queue with priority (popular content first), resolution ladder (144p-4K)
- Store: Blob storage (Colossus-like), replicated across regions, cold storage for old content
- Serve: CDN hierarchy (edge → regional → origin), adaptive bitrate (HLS/DASH), chunked download
- Recommend: Click-through history → embeddings → candidate generation → ranking

**Trade-offs**:
- Pre-transcode all resolutions vs on-demand: Storage cost vs user wait time
- Single CDN vs multi-CDN: Simplicity vs reliability
- Store raw vs compressed: Quality vs cost
- Global vs regional catalogs: Latency vs licensing complexity

---

## Microsoft

### Interview Format
- **Round type**: Microsoft Teams + whiteboard/OneNote
- **Duration**: 45 min per round, 3-5 rounds (including "ASAP" — Microsoft's version of bar raiser)
- **Levels**: 59-69 (SDE to Partner)
- **Structure**: Requirements → design → component discussion → testing
- **Focus**: Azure ecosystem, enterprise integration, testing

### Top 5 System Design Problems

#### 1. Design Azure Blob Storage
**Framework**: Namespace → Partition → Replicate → Serve
- Requirements: 11 9s durability, any scale, geo-redundancy
- Design: Multi-tenant Front-End, partition layer (range partitioning), storage layer (erasure coding), geo-replication (async/sync)
- Trade-offs: Locally redundant vs geo-redundant storage access tiers (hot/cool/archive)

#### 2. Design Microsoft Teams
**Framework**: Chat → Call → Collaborate → Integrate
- Requirements: 300M+ users, 1:1/group chat, video calls, file sharing
- Design: Real-time SIP/WebRTC for calls, chat stored in Exchange, files in SharePoint, graph API for presence, TURN servers for NAT traversal
- Trade-offs: Federated vs native calling, message retention vs compliance, audio quality vs bandwidth

#### 3. Design Azure Active Directory
**Framework**: Authenticate → Authorize → Federate → Audit
- Requirements: 500M+ identities, <10ms auth latency, 99.99% SLA
- Design: Multi-tenant directory store, OAuth2/OIDC protocol handling, token caching, federation gateway (ADFS)
- Trade-offs: Cloud-only vs hybrid deployment, passwordless vs MFA, token lifetime vs security

#### 4. Design Outlook/Exchange
**Framework**: Accept → Route → Store → Index → Serve → Sync
- Requirements: 500M+ mailboxes, <1s delivery, 100GB/user
- Design: Transport pipeline (SmtpIn → Categorizer → Mailbox), database (Extensible Storage Engine), MAPI/HTTP protocol, Full-text search index
- Trade-offs: Server-side vs client-side rules, mailbox size limits, online vs cached mode

#### 5. Design Xbox Live
**Framework**: Sign-in → Session → Gameplay → Social → Store
- Requirements: 100M+ active users, <5ms server hit latency for multiplayer
- Design: Xbox Live Compute Platform, Session Directory (Dynamo-like), matchmaking service, party chat (RTP), achievement pipeline
- Trade-offs: Match quality vs wait time, peer-to-peer vs relayed voice, cloud compute vs client compute

### Evaluation Criteria
- **Design completeness**: Covers full system lifecycle including deployment
- **Testing mindset**: How would you test this system before shipping?
- **Enterprise awareness**: Considers multi-tenancy, compliance, enterprise SLAs
- **Growth mindset**: Open to feedback and iteration during interview
- **Deep dives**: Can go deep on specific components (storage engine, replication)

### Design Philosophy
Microsoft focuses on **enterprise-grade reliability with Azure integration**:
- Hybrid cloud capability (on-prem + cloud)
- Enterprise compliance and security from day one
- Backward compatibility obsession
- Dogfooding: eat your own dog food

### Real Interview Stories

**Story 1: L63 — Design Azure Cosmos DB**
Interviewer asked about multi-master write conflict resolution. Candidate discussed LWW (last-writer-wins) and CRDT-based types (counters, sets). The deeper discussion focused on how Cosmos DB handles multi-region writes with different consistency levels. Follow-up: how to choose between bounded staleness and session consistency.

**Story 2: L65 — Design Teams at scale**
Candidate focused on chat architecture. Interviewer redirected to "what happens when a data center goes down?" Candidate discussed geo-redundant deployment with active-passive failover for chat, including session state management and how users would be reconnected.

---

## Apple

### Interview Format
- **Round type**: In-person/in-room whiteboard (Apple favors physical whiteboarding)
- **Duration**: 45-60 min per round, 4-6 rounds with cross-functional
- **Levels**: ICT2-ICT5 (SDE to Architect)
- **Structure**: Conversation-driven, product-focused, less formulaic
- **Focus**: Privacy, user experience, hardware-software integration

### Top 5 System Design Problems

#### 1. Design iCloud
**Framework**: Sync → Store → Version → Recover
- Requirements: 1B+ devices, 5GB free, 2TB+ paid, real-time sync
- Design: File coordination (NSFileCoordinator), differential sync, chunked storage with encryption, conflict resolution (versions)
- Trade-offs: Client-side vs server-side conflict resolution, full sync vs selective sync, encryption key management

#### 2. Design Apple Maps
**Framework**: Collect → Fuse → Route → Render
- Requirements: Privacy-first (on-device processing), turn-by-turn, real-time traffic
- Design: Vector tile rendering, on-device routing engine, privacy-preserving traffic data (randomized identifiers), POI database
- Trade-offs: On-device vs cloud computation, privacy vs service quality, navigation detail vs battery life

#### 3. Design App Store
**Framework**: Submit → Review → Distribute → Update → Verify
- Requirements: 2M+ apps, 500M+ weekly visitors, global CDN
- Design: App review pipeline (automated + manual), binary storage with delta updates, receipt verification service, discoverability/top charts
- Trade-offs: Review speed vs quality, human review vs automated scanning, delta update size vs complexity

#### 4. Design Apple Push Notification Service (APNs)
**Framework**: Register → Authenticate → Route → Deliver → Acknowledge
- Requirements: 100B+ notifications/day, <100ms delivery, 1B+ devices
- Design: Persistent TCP connections with TLS, per-device routing, priority queues, feedback service (delivery receipts)
- Trade-offs: Guaranteed vs best-effort delivery, priority levels (critical vs regular), notification persistence vs freshness

#### 5. Design iMessage
**Framework**: Register → Send → Encrypt → Route → Deliver
- Requirements: 1B+ users, e2e encryption, multi-device sync
- Design: Identity service (key directory), message routing (Apple Push + direct), attachment storage, e2e encryption per device pair
- Trade-offs: Key transparency vs privacy, server-side message storage vs delivery-only, iCloud sync vs on-device only

### Evaluation Criteria
- **Privacy-first thinking**: Data minimization, on-device processing, encryption
- **Product intuition**: How does design impact user experience?
- **Cross-functional awareness**: How does this interact with hardware, OS, battery?
- **Simplicity**: Clean, non-over-engineered solutions
- **Attention to detail**: Security, error states, edge cases

### Design Philosophy
Apple prioritizes **user experience and privacy above raw scale**:
- Privacy is a human right (data minimization, on-device where possible)
- Design for delight (latency, fluidity, reliability)
- Hardware-software co-design
- Simple, focused, opinionated product decisions

### Real Interview Stories

**Story 1: ICT3 — Design iCloud Sync Engine**
Interviewer focused on conflict resolution when user edits the same file on iPhone and MacBook simultaneously. Candidate proposed version-based approach with manual conflict resolution (similar to Dropbox). Apple interviewer pushed on making it invisible to users — discussed CRDT-based merging for common file types (text, photos, notes).

**Story 2: ICT4 — Design APNs**
Key challenge: privacy-preserving notification routing. Apple recently introduced notification compression and relevance ranking. The discussion centered on how to deliver notifications without revealing user behavior patterns to third-party app servers.

---

## Netflix

### Interview Format
- **Round type**: Whiteboard or shared document, with deep emphasis on fault tolerance
- **Duration**: 45-90 min per round, 4-6 system design rounds
- **Levels**: L3-L6+ (Senior to Principal)
- **Structure**: Requirements → architecture → fault tolerance → operations
- **Focus**: Fault tolerance, chaos engineering, CDN, distributed systems

### Top 5 System Design Problems

#### 1. Design Netflix Video Streaming
**Framework**: Ingest → Process → Store → Deliver → Play
- Requirements: 240M+ subscribers, 100M+ streams simultaneously, 4K HDR
- Design: Content ingestion pipeline, encoding ladder (resolution/bitrate pairs), Open Connect CDN, adaptive bitrate (DASH), device-specific packaging
- Trade-offs: Bitrate ladder density vs encoding cost, open connect vs cloud CDN, pre-fetch vs on-demand loading

#### 2. Design Netflix Recommendation System
**Framework**: Collect → Filter → Rank → Explain
- Requirements: Personalized for 240M+ users, real-time updates, A/B testable
- Design: Online (real-time) + offline (batch) processing, collaborative filtering + content-based + DNN embeddings, multi-armed bandit for exploration
- Trade-offs: Model complexity vs serving latency, personalization depth vs data freshness, exploration vs exploitation

#### 3. Design Netflix Content Delivery (Open Connect)
**Framework**: Plan → Deploy → Route → Serve → Monitor
- Requirements: 200Tbps+ peak traffic, global edge, ISP-embedded
- Design: ISP-partnered appliance deployment, DNS-based routing, pre-population (popular content), on-demand caching (long-tail)
- Trade-offs: Appliance capacity vs cache hit rate, pre-population bandwidth vs on-demand fetch latency

#### 4. Design Netflix Chaos Engineering Platform
**Framework**: Inject → Observe → Mitigate → Learn
- Requirements: Simulate failures in production without user impact
- Design: Chaos Monkey (instance termination), Latency Monkey, Conformity Monkey, monitoring-driven automated rollback
- Trade-offs: Experiment scope vs blast radius, automation vs human-in-the-loop, test coverage vs operational risk

#### 5. Design Netflix API Gateway
**Framework**: Route → Aggregate → Transform → Throttle → Monitor
- Requirements: 1M+ requests/second, 1B+ devices, polyglot clients
- Design: Zuul gateway, Hystrix circuit breakers, Eureka service discovery, Ribbon load balancing, fallback responses
- Trade-offs: Aggregation vs granularity, circuit breaker thresholds vs false positives, synchronous vs asynchronous fallbacks

### Evaluation Criteria
- **Resilience obsession**: Every component must handle failure gracefully
- **Trade-off articulation**: Clear cost-benefit analysis of design choices
- **Operational maturity**: Monitoring, alerting, rollback, capacity planning
- **Depth vs breadth**: Knows when to go deep vs stay high-level
- **Innovation culture**: Open to new ideas, challenges status quo

### Design Philosophy
Netflix prioritizes **resilience through controlled chaos**:
- Build for failure from day one (Chaos Monkey)
- Freedom and responsibility (engineers own their services)
- Data-driven decision making (everything is measured)
- CDN-first content delivery (Open Connect)

### Real Interview Stories

**Story 1: Senior Engineer — Design Video Streaming Pipeline**
Interviewer focused on resilience during CDN failure. Candidate discussed fallback CDN strategy and adaptive bitrate that drops to lower quality before buffering. The deep dive: "What happens when the origin store is unreachable?" Candidate proposed stale-while-revalidate for metadata and graceful degradation with cached manifests.

**Story 2: Senior Engineer — Design Open Connect**
Discussion focused on capacity planning: how many appliances per ISP, how to handle new season of Stranger Things (50x traffic spike). Candidate proposed predictive pre-population based on release calendar and pre-release metadata.

---

## Uber

### Interview Format
- **Round type**: CoderPad + whiteboard discussion
- **Duration**: 60 min per round, 3-5 system design rounds
- **Levels**: L3-L6 (SE I to Staff)
- **Structure**: Product-focused design → scaling discussion → trade-offs
- **Focus**: Real-time systems, geospatial data, marketplace dynamics

### Top 5 System Design Problems

#### 1. Design Uber Ride Matching
**Framework**: Locate → Search → Match → Dispatch → Track
- Requirements: 25M+ trips/day, 50+ cities, <500ms match time
- Design: Geospatial indexing (H3 hex grid, quad-tree), dispatch engine (nearest driver + surge), ETAs, real-time location tracking (MQTT/WebSocket)
- Trade-offs: Match radius vs ETA accuracy, surge pricing aggression vs rider/driver satisfaction, pre-computed vs real-time ETAs

#### 2. Design Uber ETA
**Framework**: Locate → Route → Traffic → Estimate → Update
- Requirements: Real-time traffic aware, <200ms response, multi-modal
- Design: Road network (OSM, map-matching), routing engine (time-dependent A*), traffic model (real-time + historical), ETAs with uncertainty bounds
- Trade-offs: Accuracy vs response time, route simplicity vs travel time, individual vs system-optimal routing

#### 3. Design Uber Surge Pricing
**Framework**: Monitor → Detect → Price → Communicate → Settle
- Requirements: Real-time supply/demand tracking, pricing elasticity, fair
- Design: Demand/supply heatmaps, elasticity curve fitting, price floor/cap, rider notification, driver incentive alignment
- Trade-offs: Surge multiplier aggressiveness vs user retention, pricing transparency vs competitiveness, temporal granularity (minutes vs blocks)

#### 4. Design Uber Driver Allocation
**Framework**: Predict → Position → Dispatch → Rebalance
- Requirements: 1M+ active drivers, minimize pick-up time, maximize utilization
- Design: Driver supply prediction (ML), idle driver heat map, dispatch with assignment constraints, position-based rebalancing
- Trade-offs: Rider wait time vs driver idle time, centralized vs decentralized dispatch, fairness vs efficiency

#### 5. Design Uber Trip History & Analytics
**Framework**: Capture → Store → Analyze → Present
- Requirements: 15B+ completed trips, 10-year retention, privacy-compliant
- Design: Event ingestion pipeline (Kafka), time-series DB (Cassandra), trip store (HBase), analytics (Hive/Spark), data anonymization
- Trade-offs: Hot vs cold storage, real-time vs batch analytics, data retention vs compliance

### Evaluation Criteria
- **Product understanding**: Can they design for real marketplace dynamics?
- **Real-time systems**: Understands streaming data, stateful services
- **Geospatial awareness**: Familiar with spatial indexing, map data
- **Scalability thinking**: Handles spikes (NYE, concerts, disasters)
- **Economic awareness**: Design considers business constraints

### Design Philosophy
Uber focuses on **real-time marketplace orchestration**:
- Real-time everything (location, pricing, matching)
- Marketplace dynamics require balanced design (supply + demand)
- Geospatial data is the primary data structure
- Reliability directly impacts revenue and user trust

### Real Interview Stories

**Story 1: L4 — Design Ride Matching**
Candidate designed a simple nearest-driver algorithm. Interviewer asked "What happens when 1000 people leave a concert at the same time?" Candidate enhanced with queued dispatch, batch grouping, surge pre-emption. The scalability discussion focused on sharding by geohash and hot-spot handling.

**Story 2: L5 — Design Surge Pricing**
Interviewer wanted to see both ML and systems design. Candidate discussed price elasticity curves and proposed a microservice that computes supply/demand ratios per hex cell, updates every minute, and triggers price changes. Follow-up: fairness concerns when poor neighborhoods get constant surge.

---

## Stripe

### Interview Format
- **Round type**: CoderPad + API design emphasis
- **Duration**: 60 min per round, 3-4 system design rounds
- **Structure**: Requirements → API design → data flow → failure modes
- **Focus**: API design, idempotency, data consistency, security

### Top 5 System Design Problems

#### 1. Design Payment Processing System
**Framework**: Authorize → Capture → Settle → Reconcile → Dispute
- Requirements: 99.999% uptime, idempotent operations, global multi-currency
- Design: Payment gateway → processor adapter → ledger → reconciliation pipeline, idempotency keys, event-driven settlement
- Trade-offs: Synchronous vs async settlement, retry strategy, duplicate detection

#### 2. Design Stripe's API Infrastructure
**Framework**: Receive → Validate → Authenticate → Route → Respond
- Requirements: 50M+ API requests/day, <100ms p99, idempotent mutations
- Design: Front-end (API gateway), request validation, idempotency cache, rate limiting per API key, response caching
- Trade-offs: Idempotency window length vs storage, rate limit granularity vs fairness

#### 3. Design Fraud Detection System
**Framework**: Collect → Feature → Model → Score → Act
- Requirements: Real-time (<500ms) scoring, 99.9% fraud capture, <0.1% false positive
- Design: Event stream → feature extraction (velocity, amount, geography) → ML model (gradient boost/neural net) → risk score → action (block/review/allow)
- Trade-offs: Sensitivity vs false positives, real-time vs batch features, model complexity vs latency

#### 4. Design Subscription Billing System
**Framework**: Plan → Prorate → Invoice → Collect → Reconcile
- Requirements: Supports complex billing (usage, tiered, prorated), dunning management
- Design: Subscription state machine, billing engine (proration calculation), invoice generation, payment collection with retry logic, dunning workflow
- Trade-offs: Billing model flexibility vs code complexity, exact vs estimated proration, retry frequency vs customer experience

#### 5. Design Multi-Currency Ledger
**Framework**: Record → Convert → Store → Audit → Report
- Requirements: Double-entry bookkeeping, 50+ currencies, audit trail
- Design: Transaction service, exchange rate service, ledger database (append-only), audit log, real-time balance computation
- Trade-offs: Append-only vs mutable state, real-time vs batch reconciliation, precision (decimal vs float)

### Evaluation Criteria
- **API design**: Clean, consistent, developer-friendly API surfaces
- **Data integrity**: Idempotency, transaction isolation, exactly-once processing
- **Security mindset**: Encryption, tokenization, PCI compliance, least privilege
- **Edge cases**: Handles network failures, duplicate requests, partial failures
- **Developer experience**: Designs for the end-user developer

### Design Philosophy
Stripe prioritizes **developer experience with bulletproof data integrity**:
- APIs as the product (clean, intuitive, consistent)
- Data correctness above all (double-entry, idempotency)
- Infrastructure for the internet (global, multi-currency)
- Design for failure (retries, idempotency, idempotency, idempotency)

### Real Interview Stories

**Story 1: Engineer — Design Payment Processing**
Interviewer emphasized idempotency. Candidate discussed idempotency keys stored in a database with unique constraint. Follow-up: what happens when the same idempotent request arrives simultaneously? Candidate proposed using optimistic locking with version numbers.

**Story 2: Engineer — Design Stripe Connect**
The platform fee model was the focus. Candidate designed a multi-party payment flow with destination charges. Interviewer pressed on failure recovery — what if the payment succeeds but the platform fee transfer fails? Candidate discussed a compensation transaction pattern.

---

## Twitter/X

### Interview Format
- **Round type**: CoderPad + whiteboard
- **Duration**: 45-60 min per round, 3-5 system design rounds
- **Structure**: Requirements → timeline → data model → scaling
- **Focus**: Real-time event processing, timeline generation, caching

### Top 5 System Design Problems

#### 1. Design Twitter Timeline
**Framework**: Tweet → Fanout → Rank → Serve
- Requirements: 500M+ tweets/day, 330M+ MAU, <5s propagation
- Design: Fanout-on-write for regular users, fanout-on-read for celebrities (pull model), timeline cache (Redis), ranking (ML-based relevance)
- Trade-offs: Fanout cost vs read latency, in-network vs out-of-network tweets, reverse chronological vs algorithmic

#### 2. Design Twitter Search
**Framework**: Index → Query → Rank → Serve
- Requirements: Full-text search on 1T+ tweets, real-time indexing, trending topics
- Design: Inverted index (sharded by time), Earlybird (Lucene-based real-time index), Blended search (popularity + recency + relevance)
- Trade-offs: Index freshness vs performance, full-text vs prefix search, result diversity vs relevancy

#### 3. Design Twitter Trends
**Framework**: Extract → Normalize → Count → Burst → Display
- Requirements: Identify trending topics in real-time, geo-specific, algorithmically surfaced
- Design: Tokenization/NLP pipeline, time-decayed counting (HyperLogLog), burst detection algorithm, trend deduplication and clustering
- Trade-offs: Global vs local trends, algorithmic vs editorial, spam filtering vs noise

#### 4. Design Twitter Ads
**Framework**: Target → Bid → Serve → Measure → Optimize
- Requirements: 100M+ ad impressions/day, real-time bidding, <50ms decision
- Design: User targeting (profile + interest graph), ad server (Cassandra-backed), real-time bidder, CTR prediction, budget pacing
- Trade-offs: Targeting precision vs user privacy, CPM vs CPC vs CPA, ad load vs user experience

#### 5. Design Twitter Media Pipeline
**Framework**: Upload → Process → Transcode → Distribute
- Requirements: 6+ million images/day, video up to 10min, 140MB limit
- Design: Media service (async upload), image processing pipeline (resize, compress), video transcoding (resolution ladder), CDN delivery
- Trade-offs: Image quality vs size, video transcoding latency vs quality, pre-processing vs on-the-fly

### Evaluation Criteria
- **Timeline design**: Deep understanding of feed generation at scale
- **Caching strategy**: Multi-layer caching, cache invalidation
- **Real-time processing**: Streaming, event processing at scale
- **Search expertise**: Inverted index, relevance ranking
- **Performance optimization**: Latency-critical design decisions

### Design Philosophy
Twitter focuses on **real-time information spread at global scale**:
- Speed of information (seconds matter)
- Public conversation by default
- Relevance through engagement signals
- Platform health vs free expression balance

### Real Interview Stories

**Story 1: Senior Engineer — Design Timeline**
Candidate proposed fanout-on-write. Interviewer asked about handling Lady Gaga (100M followers). Candidate added fanout-on-read for celebrities. The deep dive: "How do you decide the threshold?" Answer depends on follower count, tweet frequency, and server capacity.

**Story 2: Staff Engineer — Design Search**
Interview focus was Earlybird (Twitter's real-time search index). Candidate needed to understand inverted index fundamentals, real-time indexing, and how to handle tweet deletion. Follow-up on trending topics — algorithms for detecting burst vs steady state.

---

## LinkedIn

### Interview Format
- **Round type**: Whiteboard or CoderPad
- **Duration**: 45-60 min per round, 3-5 system design rounds
- **Structure**: Data model → API → system design → scaling
- **Focus**: Social graph, search, professional identity, data privacy

### Top 5 System Design Problems

#### 1. Design LinkedIn Feed
**Framework**: Produce → Aggregate → Rank → Deliver
- Requirements: 700M+ members, professional content, multi-format
- Design: Fanout-on-write with importance weighting, feed ranking (engagement signals), topic-based channels, sponsored content insertion
- Trade-offs: Professional tone vs engagement, chronological vs relevance, content diversity vs filter bubble

#### 2. Design LinkedIn Search
**Framework**: Index → Query → Rank → Filter → Serve
- Requirements: 700M+ profiles searchable, boolean search, faceted filters
- Design: Inverted index per entity type (people, jobs, companies, groups), Galene (LinkedIn's search engine), social distance weighting
- Trade-offs: Search index size vs freshness, ranking factors (recency vs relevance vs connections)

#### 3. Design LinkedIn "People You May Know"
**Framework**: Extract → Connect → Score → Recommend
- Requirements: 1B+ daily recommendations, connection suggestions, network growth
- Design: Graph traversal (mutual connections, school, company), collaborative filtering, ML model (Random Forest/Gradient Boosted Tree)
- Trade-offs: Relevance vs diversity, strong vs weak ties, privacy vs personalization

#### 4. Design LinkedIn Messaging (InMail)
**Framework**: Compose → Route → Filter → Deliver → Archive
- Requirements: Message by permission (InMail credits), real-time delivery
- Design: Permissions service, routing queue -> real-time push (WebSocket/APNs), spam/abuse filter, search archive
- Trade-offs: Open vs permission-based messaging, reply rate vs spam, notification volume vs user experience

#### 5. Design LinkedIn Skill Endorsements
**Framework**: Suggest → Display → Endorse → Aggregate → Verify
- Requirements: 5B+ endorsements, credibility scoring, spam detection
- Design: Skill extraction from profiles (NLP), endorsement requests, skill strength (weight by endorser seniority, recency), credibility model
- Trade-offs: Endorsement quality vs quantity, manual vs ML verification, skill taxonomy maintenance

### Evaluation Criteria
- **Graph data modeling**: Relationships, traversal at scale
- **Search expertise**: Multi-entity search, faceted search, social search
- **Privacy-aware design**: GDPR, data portability, member control
- **Growth mindset**: Design for network effects
- **Scalable recommendations**: ML pipeline for personalization

### Design Philosophy
LinkedIn focuses on **professional identity and network effects**:
- Data is the moat (profile data, connection graph, engagement signals)
- Member trust is paramount (privacy, data controls)
- Economic opportunity through connection
- B2B + B2C hybrid design

---

## Spotify

### Interview Format
- **Round type**: Whiteboard or shared document
- **Duration**: 60 min per round, 3-5 system design rounds
- **Structure**: Product → data model → system design → scaling
- **Focus**: Real-time streaming, recommendation, audio processing

### Top 5 System Design Problems

#### 1. Design Spotify Music Streaming
**Framework**: Catalog → Encode → Store → Stream → Cache
- Requirements: 100M+ tracks, 400M+ users, <200ms start time
- Design: Ogg Vorbis/MP3 encoding, CDN + P2P (Spotify desktop hybrid), client-side caching, adaptive bitrate streaming
- Trade-offs: Audio quality vs bandwidth, DRM vs user freedom, local cache vs streaming from cloud

#### 2. Design Spotify Recommendation (Discover Weekly)
**Framework**: Listen → Profile → Embed → Match → Rank → Explain
- Requirements: Personalized weekly playlist, 100M+ active users, fresh suggestions
- Design: Collaborative filtering (matrix factorization), audio feature extraction (NLP for audio), playlist sequencing, novelty + diversity + relevance scoring
- Trade-offs: Serendipity vs accuracy, global vs regional content, user control vs algorithmic curation

#### 3. Design Spotify Playlist Sync
**Framework**: Create → Edit → Store → Sync → Resolve
- Requirements: Real-time multi-device sync, offline mode, collaboration
- Design: CRDT-based sync for playlist editing, conflict resolution (last-writer-wins for adds, union for deletes), offline queue with sync on reconnect
- Trade-offs: Real-time vs offline-first, conflict resolution strategy, sync granularity (track-level vs playlist-level)

#### 4. Design Spotify Podcast Platform
**Framework**: Upload → Process → Distribute → Monetize
- Requirements: 5M+ podcasts, video podcasts, ad insertion
- Design: RSS ingestion pipeline, audio processing (normalization, chapters), dynamic ad insertion (DAI) at stream, analytics
- Trade-offs: Hosted vs RSS-based distribution, open vs walled garden, ad revenue split transparency

#### 5. Design Spotify Live (Social Listening)
**Framework**: Create → Invite → Queue → Play → React
- Requirements: Real-time synchronized playback, 50+ listeners, chat/react
- Design: Session service (leader election), sync protocol (NTP-based), queue voting mechanism, real-time reactions (WebSocket)
- Trade-offs: Sync precision vs network latency, democracy vs DJ control, chat moderation vs real-time

### Evaluation Criteria
- **Audio engineering awareness**: Codecs, streaming protocols, latency
- **Recommendation system design**: ML pipeline, feature engineering
- **Client-server trade-offs**: Local storage vs streaming, offline support
- **Personalization depth**: Beyond simple collaborative filtering
- **Product-System alignment**: Design choices tie back to user value

### Design Philosophy
Spotify focuses on **personalized audio experience at scale**:
- Audio as a platform (music, podcasts, audiobooks)
- Personalization is the killer feature
- Freemium monetization (ad-supported + premium)
- Client intelligence (thick client for UI, playback, caching)

---

## DoorDash

### Interview Format
- **Round type**: CoderPad + whiteboard
- **Duration**: 60 min per round, 3-4 system design rounds
- **Structure**: Product scenario → system design → deep dive → operations
- **Focus**: Marketplace design, real-time logistics, location services

### Top 5 System Design Problems

#### 1. Design DoorDash Order Placement
**Framework**: Browse → Cart → Order → Prepare → Dispatch → Deliver
- Requirements: 500K+ orders/day, real-time tracking, <30min delivery
- Design: Restaurant catalog (Elasticsearch), cart service (Redis), order service, dispatch engine, Dasher assignment, real-time tracking
- Trade-offs: Promise time accuracy vs Dasher flexibility, order batching vs speed, high-volume vs high-margin orders

#### 2. Design DoorDash Dispatch Engine
**Framework**: Order → Locate → Estimate → Assign → Route
- Requirements: Assign orders to Dashers optimally, minimize wait times
- Design: Multi-objective optimization (wait time, Dasher proximity, restaurant prep time), constraint solving (batching, capacity), real-time rerouting
- Trade-offs: Dasher utilization vs customer wait time, single-order vs batched delivery, algorithmic vs human dispatcher

#### 3. Design DoorDash Search & Discovery
**Framework**: Query → Filter → Rank → Personalize → Serve
- Requirements: 500K+ restaurants, personalized results, <200ms
- Design: Elasticsearch for text search, geo-spatial filtering, ML ranking (order history, cuisine pref, price sensitivity), query understanding (fuzzy, synonyms)
- Trade-offs: Relevance vs speed, personalization depth vs cold start, search vs browse experience

#### 4. Design DoorDash Real-Time Tracking
**Framework**: Start → Update → Estimate → Visualize → Notify
- Requirements: 10M+ concurrent tracking sessions, <5s location update, accurate ETA
- Design: GPS ingestion (MQTT/Kafka), location processing (map-matching, snap-to-road), ETA service (ML model), tracking visualization (vector tiles)
- Trade-offs: Location update frequency vs battery consumption, GPS accuracy vs server load, ETA precision vs privacy

#### 5. Design DoorDash Pricing & Promotions
**Framework**: Define → Calculate → Apply → Validate → Settle
- Requirements: Dynamic pricing (surge), promotions (discounts, free delivery), commission calculation
- Design: Pricing engine (rules-based + ML), promotion service (coupon application, fraud detection), settlement pipeline (multi-party payout)
- Trade-offs: Surge vs customer retention, promotion generosity vs margin, commission simplicity vs flexibility

### Evaluation Criteria
- **Marketplace design**: Managing supply (Dashers) and demand (customers)
- **Real-time logistics**: Route optimization, dispatch algorithms, ETAs
- **Distributed transactions**: Order state management across services
- **Operational visibility**: Monitoring DASI metrics (delivery time, accuracy)
- **Product launch thinking**: New city expansion, new verticals (grocery, convenience)

### Design Philosophy
DoorDash focuses on **three-sided marketplace orchestration**:
- Marketplace is the product (consumer + merchant + Dasher)
- Logistics as competitive advantage (dispatch, routing, tracking)
- Data-driven expansion (new cities, new verticals)
- Operational excellence (delivery accuracy, time, reliability)

---

## TikTok

### Interview Format
- **Round type**: CoderPad + whiteboard
- **Duration**: 45-60 min per round, 3-5 system design rounds
- **Structure**: Use case → architecture → deep dive → scale
- **Focus**: Video processing, recommendation algorithms, global CDN, content moderation

### Top 5 System Design Problems

#### 1. Design TikTok Feed (For You Page)
**Framework**: Upload → Analyze → Index → Rank → Serve
- Requirements: 1B+ users, 10B+ video views/day, <200ms load time
- Design: Video pipeline (upload, processing, storage), content analysis (tags, transcripts, audio fingerprint), user embedding, candidate generation (graph-based), multi-stage ranking (recall → precision → re-rank)
- Trade-offs: User control vs algorithmic curation, consumption diversity vs engagement optimization, real-time vs batch personalization

#### 2. Design TikTok Video Upload & Processing
**Framework**: Upload → Chunk → Process → Store → Distribute
- Requirements: 500+ videos/second, support 60s video, real-time filters
- Design: Resumable chunked upload, transcoding pipeline (multiple resolutions, formats), CDN distribution, ML-based content moderation (pre-filter)
- Trade-offs: Upload speed vs video quality, pre-moderation vs post-moderation, filter complexity vs processing speed

#### 3. Design TikTok Live Streaming
**Framework**: Start → Ingest → Process → Distribute → Moderate
- Requirements: 10M+ concurrent live streams, <1s latency, virtual gifts
- Design: RTMP/WebRTC ingestion, edge-based transcoding for low latency, real-time comment stream, gift/coin transaction pipeline, automated content moderation (real-time)
- Trade-offs: Low latency vs scale, gift monetization vs user experience, moderation accuracy vs real-time requirement

#### 4. Design TikTok Search
**Framework**: Query → Understand → Retrieve → Rank → Blend → Serve
- Requirements: Full-text + video + user search, mixed result types
- Design: Multi-modal search (text, audio, visual features), Elasticsearch for text, vector DB for embeddings, blended ranking (tags, engagement, freshness), query understanding (tokenization, stemming, transliteration)
- Trade-offs: Search vs discovery, content type blending, result freshness vs relevance

#### 5. Design TikTok Content Moderation System
**Framework**: Upload → Scan → Analyze → Decide → Appeal
- Requirements: Real-time moderation of 10B+ uploads, multi-language, multi-region
- Design: Multi-stage pipeline: hash matching (known bad content), ML classifiers (image, video, text, audio), human review queue, region-specific policy engine, appeals system
- Trade-offs: Automated vs human review, false positive vs false negative, speed vs accuracy, regional vs global policy

### Evaluation Criteria
- **Video processing expertise**: Encoding, transcoding, CDN delivery
- **ML system design**: Recommendation pipeline, model serving, feature store
- **Global infrastructure**: Multi-region, data sovereignty, latency optimization
- **Content moderation**: ML + human review pipeline, policy engine
- **Growth systems**: Viral loops, retention mechanics, notification systems

### Design Philosophy
TikTok focuses on **AI-first content discovery and engagement**:
- Algorithm is the product (For You Page)
- Content discovery > social graph (vs Meta/LinkedIn)
- Vertical video optimized (mobile-first)
- Regional compliance (data localization, content policies)

### Real Interview Stories

**Story 1: Engineer — Design For You Page**
Interviewer focused on the recommendation pipeline: recall (Millions → Thousands) → ranking (Thousands → Hundreds) → re-ranking (Diversity + Freshness). Deep dive on handling cold-start for new users with zero history. Candidate discussed popularity-based seeding and onboarding preference selection.

**Story 2: Staff Engineer — Design Content Moderation**
The complexity of multi-language, multi-modal moderation at 10B+ uploads. Candidate proposed a tiered approach: 1) hash/deduplication to catch 60%, 2) ML classifiers for another 30%, 3) human review for edge cases. Discussion focused on recall vs precision trade-offs for different content categories.

---

## Universal Solution Frameworks

### Framework 1: URL Shortener

**Requirements**:
- Generate short alias for long URLs
- 100M+ URLs created/month
- Redirect with <10ms overhead
- Analytics (clicks, referrers, geo)
- Custom aliases (optional)

**Estimation**:
- 100M/month → ~35 QPS write, 10K QPS read (30:1 read:write)
- Storage: 100M × 500B = 50GB/month → 600GB/year
- Cache: 80% of reads hit cache → ~2TB Redis cluster

**Design**:
- Key generation: Base62 encoding with unique ID (snowflake-style), or hash (MD5/SHA1 truncated) + collision handling
- Write path: POST → generate key → store in DB (key → URL + metadata) → return short URL
- Read path: GET → check cache (Redis) → if miss, check DB → cache miss row → redirect (301/302)
- Analytics: Log events → Kafka → streaming processor → aggregate store

**Trade-offs**:
- Hash vs sequential key generation: Collision handling vs predictability
- 301 vs 302 redirect: Cache (SEO) vs analytics accuracy
- Single-region vs multi-region: Simplicity vs latency
- SQL vs NoSQL: ACID vs scalability

### Framework 2: Design Uber

**Requirements**:
- Riders request rides, drivers accept/complete trips
- 25M+ trips/day, 50+ cities
- <500ms matching, <5s ETA
- Real-time location tracking

**Estimation**:
- 25M trips/day → ~300 QPS peak (trips), 50K QPS (location updates)
- Storage: Trips metadata → 25M × 1KB = 25GB/day; Location → 5B pings/day × 100B = 500GB/day
- Cache: Driver locations in memory (1M drivers × 500B = 500MB)

**Design**:
- Location Service: Driver GPS → MQTT ingestion → geospatial index (H3 grid)
- Dispatch Service: Match algorithm → nearest driver with constraints → assignment
- Trip Service: State machine (requesting → accepted → in_progress → completed → billed)
- ETA Service: Road network + real-time traffic + historical patterns → travel time estimation
- Surge Service: Supply/demand ratio per H3 cell → price multiplier

**Trade-offs**:
- Matching radius vs ETA accuracy: Tight radius = fast pickup, but fewer matches
- Centralized vs decentralized dispatch: Optimization vs resilience
- GPS sampling frequency: Battery vs accuracy
- Surge pricing granularity: Block-level vs neighborhood-level

### Framework 3: Design Twitter

**Requirements**:
- Users post tweets, follow others, see timeline
- 500M tweets/day, 330M MAU
- Tweet < 280 chars, media support
- Timeline load < 500ms

**Estimation**:
- 500M tweets/day → ~5,800 QPS write, 290K QPS timeline reads (50:1 read:write)
- Storage: 500M × 300B (text + metadata) = 150GB/day → 55TB/year
- Cache: Timeline cache per user = 330M × 10KB = 3.3TB in Redis

**Design**:
- Tweet Service: Receive → validate → store → fanout
- Fanout: Write → user's followers → insert tweet ID into each follower's timeline cache
- Timeline Service: Read timeline (cache) → hydrate tweets (cache + DB) → rank → return
- Search Service: Inverted index (Lucene/Earlybird) → real-time indexing

**Trade-offs**:
- Fanout-on-write vs fanout-on-read: Write cost vs read latency
- Pure pull vs pure push vs hybrid
- Timeline cache eviction policy: LRU vs TTL
- Search index freshness: Near-real-time vs exact

### Framework 4: Design YouTube

**Framework**: Upload → Transcode → Store → Serve → Recommend

**Requirements**:
- 1B+ users, 500 hours of video uploaded per minute
- Support 4K, HDR, 60fps content
- <200ms playback start, global delivery

**Estimation**:
- Storage: 500h/min × 60min × 500MB/h = ~15TB/min new video, ~22PB/day
- CDN bandwidth: 1B users × 30min/day × 10Mbps = ~200Tbps peak
- Transcoding compute: 500h/min × 10min/h transcoding = 5,000 compute-minutes/min

**Design**:
- Upload: Chunked resumable uploads via HTTP, metadata in Cloud SQL
- Transcode: Job queue with priority (popular content first), resolution ladder (144p-4K)
- Store: Blob storage (Colossus-like), replicated across regions, cold storage for old content
- Serve: CDN hierarchy (edge → regional → origin), adaptive bitrate (HLS/DASH), chunked download
- Recommend: Click-through history → embeddings → candidate generation → ranking

**Trade-offs**:
- Pre-transcode all resolutions vs on-demand: Storage cost vs user wait time
- Single CDN vs multi-CDN: Simplicity vs reliability
- Store raw vs compressed: Quality vs cost
- Global vs regional catalogs: Latency vs licensing complexity

### Framework 5: Design WhatsApp

**Requirements**:
- 2B+ users, 100B+ messages/day
- End-to-end encryption
- <1s delivery, multi-device
- Group chat, media sharing

**Estimation**:
- 100B messages/day → ~1.2M QPS writes, ~12M QPS reads
- Storage: 100B × 100B = 10TB/day → 3.6PB/year (message only)
- Connections: 2B users, 500M concurrent → 500M persistent TCP connections

**Design**:
- Connection Manager: Persistent TCP (custom XMPP), connection pooling, per-user connection routing
- Message Router: Send → look up recipient server → forward to connection manager → deliver
- Message Store: Per-user message store (HBase/Eyrie), temporary storage until delivered
- Group Chat: Sender fanout — sender sends once, server duplicates to each group member
- E2E Encryption: Signal protocol (Double Ratchet), per-device key exchange, media encryption

**Trade-offs**:
- Server-side message history vs pure relay: Feature depth vs privacy
- At-least-once vs exactly-once: Delivery guarantee vs complexity
- Online vs offline message handling: Store-and-forward vs missed messages
- Group fanout: Sender-side vs server-side duplication

### Framework 6: Design Dropbox

**Requirements**:
- 500M+ users, file sync across devices
- Conflict resolution, version history
- 2GB free, 2TB+ paid, sharing
- <1s propagation for small files

**Estimation**:
- 500M users × 2GB = 1EB total storage capacity
- 500M users × 100 files/user/day = 50B file operations/day
- Metadata: 500M users × 10K files/user × 200B = 1PB metadata

**Design**:
- Client: File system watcher → block splitter → sync engine → upload/download manager
- Sync Server: Metadata service (file tree, versions), content service (block storage), notification service (sync changes)
- Block Storage: File split into 4MB blocks, deduplicated (content-addressable), stored in S3/GCS, erasure coded
- Conflict Resolution: Last-writer-wins for simple files, version branching for complex conflicts (create conflicted copy)

**Trade-offs**:
- Block size: 4MB vs variable — dedup efficiency vs computational cost
- Delta sync vs full file sync: Bandwidth vs computation
- Client-side vs server-side deduplication: Privacy vs efficiency
- Polling vs push for change notification: Real-time vs server load

### Framework 7: Design Instagram

**Requirements**:
- 1B+ users, photo/video sharing
- 300M+ daily stories, feed, explore
- Image filters and editing
- <500ms feed load, global

**Estimation**:
- 100M+ photos/day, 5M+ videos/day (15s each)
- Storage: 100M × 500KB = 50TB/day (photos), 5M × 10MB = 50TB/day (videos)
- Feed reads: 1B users × 300 feeds/day × 200KB = 60PB/day transfer

**Design**:
- Media Pipeline: Upload → resize (thumb, standard, full) → filter → compress → store
- Feed: Fanout-on-write (inject new posts into followers' feed queues), ranked by recency + relevance
- Stories: Separate pipeline with 24h TTL, pre-compute story roll, expire via background GC
- Explore: Graph-based discovery (liked photos → similar photos from similar users), computer vision embedding

**Trade-offs**:
- Pre-compute all image sizes vs on-demand: Storage vs latency
- Fanout: All followers vs active-only vs sampling
- Stories: Pull for viewer vs push to all followers
- Explore ranking: Serendipity vs engagement

### Framework 8: Design Tinder

**Requirements**:
- 75M+ users, swipe-based matching
- Real-time matching notification
- Location-based discovery
- <200ms swipe response

**Estimation**:
- 75M users, 1.5B swipes/day → 17K QPS
- Geo queries: 1B+/day location-based queries
- Matches: 30M/day → 350 QPS

**Design**:
- User Profile Service: Profile data (photos, bio, preferences) → served via CDN for static content
- Discovery Service: Geospatial index (H3 grid, S2 cells) → find nearby users → filter by preferences → return card deck
- Swipe Service: Record swipe (like/nope) → check mutual like → if match → push notification
- Match Service: Match notification via WebSocket/push, match inbox for messaging
- Recommendation: ELO/Glicko score, recency scoring, preference optimization

**Trade-offs**:
- Discovery radius vs user density: Urban vs rural optimization
- Deck freshness vs cache hit rate: Real-time vs batch replenishment
- ELO vs engagement-based matching: "Hotness" vs compatibility
- Geolocation precision vs privacy: Exact vs fuzzy location

### Framework 9: Design Slack

**Requirements**:
- 30M+ DAU, real-time messaging
- Channels, threads, reactions, search
- 10K+ messages/channel, 1M+ workspace
- <100ms message delivery

**Estimation**:
- 30M+ DAU, 1B+ messages/day → ~12K QPS
- Storage: 1B × 500B = 500GB/day → 180TB/year
- Search index: 180TB text/year → ~500TB index

**Design**:
- Gateway: WebSocket termination, connection management, sticky routing by workspace
- Message Service: Receive → persist (Cassandra per workspace) → fanout to channel members → WebSocket push
- Channel Service: Membership management, unread counts, channel history
- Search Service: Inverted index per workspace (Elasticsearch), real-time indexing pipeline
- File Service: Upload → scan → store → CDN → share with channel

**Trade-offs**:
- Per-workspace DB vs global DB: Data isolation vs operational complexity
- Message fanout: Server-side vs client-side polling vs push
- Search index: Per-workspace vs shared index: Isolation vs hardware utilization
- Threading: Inline vs sidebar vs dedicated view

### Framework 10: Design E-commerce Platform

**Requirements**:
- 300M+ products, 100M+ customers
- Shopping cart, checkout, payments, orders
- Inventory management, search
- 99.99% uptime during sales events

**Estimation**:
- 100M daily visitors, 10M daily orders → 10% conversion
- 1B+ search queries/day, 10M concurrent sessions during Black Friday
- Order data: 10M × 2KB = 20GB/day

**Design**:
- Product Catalog: Elasticsearch for search, sharded by category, product data in distributed SQL (Spanner/CockroachDB)
- Cart Service: Session cart (Redis, ephemeral) + User cart (Cassandra/DynamoDB, persistent)
- Order Service: State machine (pending → confirmed → paid → shipped → delivered → returned)
- Inventory Service: SKU-level count, real-time reservation (optimistic locking), allocation at checkout
- Payment Service: Idempotent API, PSP adapter pattern, payment state machine

**Trade-offs**:
- Synchronous vs async inventory reservation: Accuracy vs performance
- Cart merge strategy (guest → logged in): Replace vs merge
- Search vs browse: Structured queries vs faceted navigation
- Inventory accuracy vs scalability: Pessimistic vs optimistic locking

### Framework 11: Design Payment System

**Requirements**:
- 99.999% uptime, global multi-currency
- Idempotent operations, audit trail
- Fraud detection in real-time
- Integration with 200+ payment methods

**Estimation**:
- $10B+ monthly processing, 50M+ transactions/day
- 50M × 1KB = 50GB/day transaction data
- 1B+ API calls/day to payment service

**Design**:
- Payment Gateway: Unified API → payment method routing → retry logic → idempotency middleware
- Core Processing: Payment state machine (init → auth → capture → settle → complete/failed)
- Ledger Service: Double-entry accounting (append-only), credits = debits at all times
- Reconciliation: Settlement data from banks → transaction matching → exception handling
- Fraud Detection: Real-time scoring (rule engine + ML), risk-based actions (block/review/allow)

**Trade-offs**:
- Synchronous vs async settlement: User experience vs reliability risk
- Idempotency window length: 24h vs 7d vs infinite
- Transaction isolation: Serializable vs snapshot vs read-committed
- Payment retry vs duplicate detection: At-most-once vs exactly-once

### Framework 12: Design Ticketmaster

**Requirements**:
- Handle millions of concurrent users for popular events
- Fair ticket allocation, anti-scalping
- Real-time seat selection/availability
- 99.99% uptime during on-sale

**Estimation**:
- 10M+ concurrent users for top events (Taylor Swift, Super Bowl)
- 100K+ seats released in batches, sold in seconds
- 1M+ QPS during peak on-sale

**Design**:
- Queueing System: Virtual waiting room (randomized position in queue), rate-limited admission
- Ticketing Engine: Seat selection → optimistic lock → temporary hold (5min timer) → payment → confirmation
- Inventory Service: Real-time seat map, status per seat (available/held/sold), batch release
- Anti-Scalping: CAPTCHA, purchase limits, verified fan pre-registration, transfer restrictions
- Event Management: Artist/venue/event catalog, pricing tiers, schedule management

**Trade-offs**:
- Queue position vs lottery fairness: First-come vs equal chance
- Hold duration vs inventory utilization: 5min vs 10min vs 15min
- Seat selection vs best-available algorithm: User choice vs optimization
- Real-time vs batch seat release: Fairness vs technical feasibility

---

> Continue to `COMPANY_INTERVIEW_GUIDE.md` for detailed per-company process guides,
> `SYSTEM_DESIGN_CHEATSHEET.md` for estimation formulas and decision trees,
> and per-company guides in `*_GUIDE.md` files.
