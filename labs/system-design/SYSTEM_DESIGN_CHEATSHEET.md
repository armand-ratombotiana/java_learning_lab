# System Design Cheat Sheet

> Master reference for system design interviews: estimation, decision trees, component deep dives, and interview response templates.

---

## Table of Contents

1. [Estimation Cheat Sheet](#1-estimation-cheat-sheet)
2. [Design Decision Trees](#2-design-decision-trees)
3. [Component Deep Dives](#3-component-deep-dives)
4. [Interview Response Templates](#4-interview-response-templates)

---

## 1. Estimation Cheat Sheet

### QPS Calculation

| Metric | Formula | Example |
|--------|---------|---------|
| QPS (avg) | DAU × actions_per_user / 86400 | 100M DAU × 10 tweets/day / 86400 = ~11,574 QPS |
| QPS (peak) | QPS_avg × peak_factor | 11,574 × 3 = ~34,722 QPS |
| Peak factor | Typical range: 2-5x average | Social apps: 3-5x, Utilities: 2-3x |

### Storage Estimation

| Item | Formula | Example |
|------|---------|---------|
| Annual storage | Daily_volume × size_per_item × 365 | 500M tweets × 300B × 365 = ~55TB/year |
| Total with replication | Storage × replication_factor | 55TB × 3 = 165TB |
| Size per row | fixed_fields + variable_fields | 200B (ID, user, timestamp) + 140B (text) + 60B (metadata) = 400B |

### Common Size Estimates

| Item | Size |
|------|------|
| Unique ID (UUID) | 36B |
| Timestamp (ISO 8601) | 24B |
| Tiny URL key | 6-7B |
| Tweet text (max) | 280B → 560B (Unicode) |
| Standard image (compressed) | 200-500KB |
| HD image | 2-5MB |
| Short video (15s, 720p) | 5-10MB |
| Song (3min MP3 128kbps) | ~3MB |
| Song (3min FLAC) | ~20MB |
| User profile record | ~1-2KB |
| 1 year of GPS data (1/min) | ~1.5MB |
| HTTP request header | ~1KB |

### Bandwidth Estimation

| Metric | Formula | Example |
|--------|---------|---------|
| Ingress (write) | QPS_write × item_size | 5,800 QPS × 300B = 1.7MB/s |
| Egress (read) | QPS_read × response_size | 290K QPS × 10KB = 2.9GB/s |
| CDN bandwidth | Views/day × avg_duration × bitrate | 1B × 30min × 5Mbps = ~1.1Ebps |

### Cache Sizing

| Metric | Formula | Example |
|--------|---------|---------|
| Working set | daily_reads × item_size × hit_ratio | 10B timeline reads × 10KB × 0.8 = 80TB |
| Cache memory | working_set × (1 - cold_ratio) | 80TB × 0.2 = 16TB (unlikely, need tiering) |
| Redis cluster | cache_memory / avg_node_memory + replication | 100GB / 32GB + 1 replica = 6-8 nodes |

### Power of Two Reference

| Value | Approximate |
|-------|------------|
| 2^10 | 1,024 (~1K) |
| 2^20 | 1,048,576 (~1M) |
| 2^30 | 1,073,741,824 (~1B, 1GB) |
| 2^40 | ~1TB |
| 2^50 | ~1PB |
| 1 million seconds | ~11.5 days |
| 1 billion seconds | ~31.7 years |

### Latency Numbers Every Engineer Should Know (2024)

| Operation | Time |
|-----------|------|
| L1 cache reference | 0.9ns |
| Branch mispredict | 3ns |
| L2 cache reference | 7ns |
| Mutex lock/unlock | 25ns |
| Main memory reference | 100ns |
| Compress 1KB with Zippy | 3,000ns (3μs) |
| Send 1KB over 1Gbps network | 10,000ns (10μs) |
| Read 1MB sequentially from SSD | 250,000ns (250μs) |
| Disk seek | 1,000,000ns (1ms) |
| Read 1MB sequentially from HDD | 2,000,000ns (2ms) |
| Round trip within same datacenter | 500,000ns (500μs) |
| Round trip CA → Netherlands | 150,000,000ns (150ms) |

---

## 2. Design Decision Trees

### SQL vs NoSQL Decision Tree

```
Is data highly relational with complex joins?
├── YES → SQL (PostgreSQL, MySQL)
├── NO → Is data structured with predefined schema?
│   ├── YES → Is ACID compliance critical?
│   │   ├── YES → SQL
│   │   └── NO → Do you need horizontal scaling?
│   │       ├── YES → NewSQL (Spanner, CockroachDB) or NoSQL
│   │       └── NO → SQL
│   └── NO → What's the access pattern?
│       ├── Document-style (JSON) → MongoDB, DynamoDB, Couchbase
│       ├── Key-value (simple lookups) → Redis, DynamoDB, Memcached
│       ├── Column-family (wide rows) → Cassandra, HBase, Scylla
│       ├── Graph (relationships) → Neo4j, Amazon Neptune, JanusGraph
│       ├── Time-series → InfluxDB, TimescaleDB, ClickHouse
│       └── Search → Elasticsearch, Solr, Algolia
```

### Synchronous vs Asynchronous Decision Tree

```
Does the client need immediate response?
├── YES → Can the operation complete in <100ms?
│   ├── YES → Synchronous (direct API call)
│   └── NO → Is it critical to return success only on full completion?
│       ├── YES → Sync with optimistic response → async reconcile
│       └── NO → Async (queue it, poll/callback later)
└── NO → Async (event-driven, message queue)
```

### Caching Decision Tree

```
Is data read frequently (>80% reads)?
├── NO → Caching not beneficial
├── YES → Is data mutable?
│   ├── NO → Static data → CDN + browser cache + long TTL
│   └── YES → What consistency level required?
│       ├── Strong → Cache-aside with immediate invalidation
│       ├── Eventual → Write-through or write-behind
│       └── Session → Local cache + distributed cache
```

### Consistency Level Decision Tree

```
Is the system globally distributed?
├── NO → What's the CAP priority?
│   ├── CP (Consistency + Partition tolerance)
│   │   ├── Strong consistency → Single-master, quorum reads
│   │   └── Linearizability → Spanner, ZK, ETCD
│   └── AP (Availability + Partition tolerance)
│       ├── Eventual → CRDTs, multi-master (Cassandra)
│       ├── Causal → Vector clocks
│       └── Read-your-writes → Session consistency
└── YES → Is there a single global leader?
    ├── YES → Strong consistency possible (limited by latency)
    └── NO → Eventual + CRDTs
```

### Storage Decision Tree

```
Is data primarily file/object based?
├── YES → Is it accessed via filesystem API?
│   ├── YES → Block storage (EBS, local SSD)
│   └── NO → Object storage (S3, GCS, Azure Blob)
└── NO → Is it large datasets for analytics?
    ├── YES → HDFS, data lake
    └── NO → Database
```

### Search Decision Tree

```
Is search a primary feature?
├── NO → RDBMS full-text search (PostgreSQL tsvector)
├── YES → Is it simple keyword search on small data?
│   ├── YES → RDBMS full-text or simple LIKE with index
│   └── NO → Do you need advanced features (faceted, fuzzy, ML ranking)?
│       ├── YES → Elasticsearch/Solr
│       └── NO → What's the data type?
│           ├── Text → Inverted index (Lucene)
│           ├── Geo → Geospatial index (Elasticsearch geo, S2, H3)
│           ├── Vector → Vector DB (Pinecone, Milvus, Qdrant)
│           └── Graph → Graph DB
```

---

## 3. Component Deep Dives

### 3.1 Databases

#### Sharding Strategies

| Strategy | Description | Pros | Cons | Use Case |
|----------|------------|------|------|----------|
| Range sharding | Partition by key range | Simple, range queries efficient | Hotspots, uneven distribution | Time-series data |
| Hash sharding | Hash key % N | Even distribution | Range queries scatter | Key-value, user data |
| Directory sharding | Lookup table | Flexible routing | Single point of failure, lookup overhead | Dynamic rebalancing |
| Geo sharding | Partition by geography | Data locality, compliance | Skewed distribution | Multi-region apps |

#### Replication Strategies

| Strategy | Description | Durability | Read Scale | Consistency |
|----------|------------|-----------|------------|-------------|
| Single-master | One writer, N readers | N replicas | High (async) | Strong (sync), Eventual (async) |
| Multi-master | N writers | N replicas | Very high | Conflict risk |
| Quorum (R+W > N) | N replicas, W writes, R reads | Tw for durability | Configurable | Configurable |
| Chain replication | Linear chain | High | Moderate | Strong |

#### Indexing Strategies

| Index Type | Best For | Trade-offs |
|------------|----------|-----------|
| B-Tree | Range scans, equality | Insert overhead |
| LSM-Tree | Write-heavy workloads | Read amplification |
| Hash index | Point lookups | No range queries |
| Bitmap | Low cardinality columns | Write cost |
| Inverted index | Full-text search | Storage cost |
| Geospatial (R-Tree) | Location queries | Complexity |
| Vector index (HNSW) | Similarity search | Memory cost |

### 3.2 Caches

#### Cache Patterns

| Pattern | Read Strategy | Write Strategy | Stale Data | Complexity |
|---------|--------------|---------------|------------|------------|
| Cache-aside | Check cache → miss → load DB → populate | App manages invalidation | Possible | Low |
| Read-through | Cache loads from DB on miss | App manages invalidation | Possible | Medium |
| Write-through | — | Write to cache + DB synchronously | No | Medium |
| Write-around | — | Write to DB, invalidate cache | Possible (brief) | Low |
| Write-behind | — | Write to cache, async write to DB | Risk of loss | High |
| Refresh-ahead | Predictive refresh before expiry | — | No (refreshed early) | High |

#### Redis vs Memcached

| Feature | Redis | Memcached |
|---------|-------|-----------|
| Data structures | Strings, Lists, Sets, Sorted Sets, Hashes, Bitmaps, HyperLogLog, Streams | Strings only |
| Persistence | RDB snapshots, AOF logs | None |
| Replication | Master-slave, Cluster | No built-in |
| Transactions | Optimistic (WATCH) + Lua scripts | None |
| Lua scripting | Yes | No |
| Pub/Sub | Yes | No |
| Max key size | 512MB | 1MB |
| Threading | Single-threaded (v6+ threaded I/O) | Multi-threaded |
| Use case | Rich caching, session store, rate limiter, pub/sub, leaderboard | Simple key-value cache with large objects |

#### Cache Invalidation Strategies

| Strategy | Description | Best For |
|----------|------------|----------|
| TTL | Expire after time-to-live | Most cases |
| Write-through | Update cache on write | Consistency-critical |
| Event-based | Publish invalidation events | Distributed caches |
| Version-based | Increment version on write | Optimistic concurrency |
| Stale-while-revalidate | Serve stale, update async | Read-heavy, stale-tolerant |

### 3.3 Load Balancers

#### Algorithms

| Algorithm | Description | Best For |
|-----------|------------|----------|
| Round Robin | Sequential distribution | Equal-capacity servers |
| Weighted RR | Capacity-weighted distribution | Heterogeneous servers |
| Least Connections | Send to least loaded server | Varying request durations |
| Least Response Time | Fastest responder | Latency-sensitive |
| IP Hash | Hash client IP → stickiness | Session persistence |
| URL Hash | Hash request URL → cache affinity | Cache efficiency |

#### Layer 4 vs Layer 7

| Feature | Layer 4 (TCP/UDP) | Layer 7 (HTTP/HTTPS) |
|---------|------------------|---------------------|
| Decision basis | IP + port | URL, headers, cookies, body |
| Routing | Per-connection | Per-request |
| Performance | Fast (kernel-level) | Slower (application-level) |
| Features | NAT-based | Content routing, SSL termination, caching |
| Examples | HAProxy (TCP mode), AWS NLB, F5 | HAProxy (HTTP mode), AWS ALB, Nginx |
| Use case | TCP, UDP, WebSocket | HTTP/HTTPS APIs, microservices |

#### Session Persistence

| Method | Description | Limitation |
|--------|------------|-----------|
| Cookie-based (sticky) | LB sets cookie → route by cookie | Cookie can be blocked, load imbalance |
| IP hash | Hash source IP to server | NAT issues, sticky on IP change |
| Session store | Shared session store (Redis) | Extra hop for session fetch |
| Client token | JWT/Token carries session info | Token size, security |

### 3.4 Message Queues

#### Comparison Table

| Feature | Apache Kafka | RabbitMQ | Amazon SQS | Apache Pulsar |
|---------|-------------|----------|------------|---------------|
| Model | Log-based, pub-sub, partitioned | AMQP, broker-based | Pull-based queue | Log + broker hybrid |
| Ordering | Within partition | Per-queue | Best-effort (FIFO available) | Per-partition |
| Retention | Configurable (time/size) | Acknowledged = deleted | Configurable (14d max) | Configurable (time/size) |
| Delivery | At-least-once | At-least-once (acks), At-most-once | At-least-once | At-least-once, Effectively-once |
| Latency | ~10ms (no batching) | ~μs-ms (in-memory) | ~10-100ms | ~10ms |
| Throughput | Millions/sec | 10K-100K/sec | Unlimited (scaling) | Millions/sec |
| Persistence | Disk (page cache) | Disk (optional) | Disk (S3-backed) | Tiered (bookie + S3) |
| Exactly-once | Transactional API | Idempotent consumer | Idempotent consumer, FIFO | Effectively-once with dedup |
| Partition model | Topic → partitions | — | Queue | Topic → partitions → segments |
| Consumer model | Pull (offset commit) | Push/Pull (AMQP) | Pull (long polling) | Pull (cursor) |
| Multi-tenancy | Cluster-level | Virtual hosts | AWS account | Namespaces |
| Geo-replication | MirrorMaker, Cluster Linking | Shovel, Federation | Built-in | Built-in |
| Use cases | Event streaming, log aggregation, data pipeline | Task queues, RPC, microservices | Decoupled microservices, scaling | Unified streaming + queuing |

### 3.5 APIs

#### REST vs GraphQL vs gRPC

| Feature | REST | GraphQL | gRPC |
|---------|------|---------|------|
| Data fetching | Multiple endpoints, fixed response | Single endpoint, client-specified | Service-defined methods |
| Over-fetching | Common | Solved | No (proto-defined) |
| Under-fetching | Common (N+1) | Solved | No |
| Protocol | HTTP/1.1, HTTP/2 | HTTP/2 (typically) | HTTP/2 |
| Serialization | JSON, XML, others | JSON | Protocol Buffers (binary) |
| Schema | OpenAPI (YAML) | Schema Definition Language | .proto files |
| Caching | HTTP caching (ETag, Cache-Control) | Limited (tooling growing) | No built-in HTTP caching |
| Performance | Moderate (JSON) | Moderate (JSON, query cost) | Fast (binary, efficient) |
| Streaming | SSE (Server-Sent Events) | Subscriptions | Bidirectional streaming |
| Tooling | Mature everywhere | Strong (Apollo, Relay) | Good (protoc, grpcurl) |
| Browser support | Native | Yes (HTTP/2) | Needs gRPC-web proxy |
| Use case | Public APIs, web services | Complex data requirements, mobile | Microservices, real-time, high-performance |

#### Pagination Strategies

| Strategy | Description | Pros | Cons |
|----------|------------|------|------|
| Offset/Limit | `?offset=20&limit=10` | Simple, skippable | Inconsistent if data changes |
| Cursor-based | `?cursor=abc123&limit=10` | Consistent, stable | Complex, no random access |
| Keyset | `?after_id=123&limit=10` | Fast on indexed column | Requires sortable unique key |
| Page-based | `?page=3&size=10` | User-friendly | Same as offset problems |

#### Rate Limiting Strategies

| Strategy | Description | Space | Accuracy | Complexity |
|----------|------------|-------|----------|------------|
| Token bucket | Refill tokens over time | O(1) | Medium | Low |
| Leaky bucket | Request queue processed at fixed rate | O(queue) | Medium | Low |
| Fixed window | Reset counter every window | O(1) | Low (burst at boundary) | Low |
| Sliding window log | Timestamp log per window | O(window_requests) | High | Medium |
| Sliding window counter | Sub-window counters | O(windows) | High | Medium |

### 3.6 Storage Systems

#### Block vs Object vs File Storage

| Feature | Block Storage | Object Storage | File Storage |
|---------|--------------|----------------|--------------|
| Unit | Block (512B-64KB) | Object (file + metadata) | File (hierarchical) |
| Protocol | SCSI, iSCSI, NVMe-oF | HTTP (S3, GCS, Azure Blob) | NFS, SMB/CIFS |
| Modification | Random read/write | Full object replace | Random read/write |
| Consistency | Strong | Eventual (typically) | Strong |
| Performance | Low latency, high IOPS | Variable (latency/throughput) | Moderate |
| Use case | DB storage, VM disks | Backup, archive, data lake, media | Shared filesystems, home dirs |
| Examples | EBS, local SSD, SAN | S3, GCS, Azure Blob, MinIO | EFS, FSx, NFS server |

#### S3 vs HDFS vs Local SSD

| Feature | S3 (Object) | HDFS (File) | Local SSD (Block) |
|---------|------------|-------------|-------------------|
| Durability | 99.999999999% | 99.99% (default 3x) | Disk reliability |
| Scaling | Virtually infinite | Limited (NameNode) | Fixed (machine capacity) |
| Performance | Varies (GET ~TPUT) | High (data locality) | Highest |
| Cost | Per-GB + per-request | Compute cost | Included in instance |
| Consistency | Strong (since Dec 2020) | Strong | Strong |
| Best for | Data lake, backups, media | Batch processing, large files | DB, real-time, low-latency |

### 3.7 Search

| Component | Description | Options |
|-----------|------------|---------|
| Inverted index | Map term → document list | Lucene, Elasticsearch |
| Tokenizer | Split text into tokens | Standard, N-gram, custom |
| Analyzer | Tokenization + normalization | Language-specific analyzers |
| Ranking | Score documents by relevance | BM25 (default), TF-IDF, custom |
| Query types | Search operations | Match, phrase, fuzzy, regex, wildcard |
| Aggregation | Faceted search, metrics | Terms, date histogram, geo, stats |

---

## 4. Interview Response Templates

### Template 1: "Design X" — 4-Part Framework

```
FRAMEWORK: Requirements → Estimation → Design → Trade-offs

PART 1: REQUIREMENTS (2-3 min)
"Let me start by clarifying the requirements. I'll group them into functional and non-functional."

Functional:
- F1: Users can [action 1]
- F2: Users can [action 2]
- F3: System supports [capability]

Non-functional:
- NF1: [Latency target] p99 latency under [X]ms
- NF2: [Availability] [X]% uptime (X nines)
- NF3: [Scale] [X]M DAU, [X]M operations/day
- NF4: [Consistency] Strong/Eventual/Causal

Out of scope:
- [Feature 1] (we can discuss if time)
- [Feature 2]

PART 2: ESTIMATIONS (2-3 min)
"Let me estimate scale to understand resources needed."

QPS: (DAU × actions/user) / 86400 × peak_factor = [X] QPS
Storage: (items/day × size × days) × replication = [X] TB
Bandwidth: (QPS_read × response_size) = [X] GB/s
Cache: (QPS_read × item_size × hit_ratio) = [X] GB

PART 3: DESIGN (15-20 min)
"Here's my proposed architecture."

[Draw system diagram]

Components:
1. [Service 1]: Handles [responsibility]
   - API: [endpoints]
   - Data: [storage]
   - Scaling: [strategy]

2. [Service 2]: Handles [responsibility]
   - API: [endpoints]
   - Data: [storage]
   - Scaling: [strategy]

3. [Data Store]: [type] database, [sharding strategy]
   - Schema: [key fields]
   - Indexes: [indexes]

4. [Cache]: [cache type], [sizing]
   - Strategy: [cache pattern]
   - Invalidation: [TTL/event-based]

5. [Message Queue]: [queue type]
   - Topic: [purposes]
   - Producers: [services]
   - Consumers: [services]

Data flow:
1. User [action] → [Service] → [Data flow description]
2. [Processing step] → [Queue] → [Consumer] → [Write to DB]
3. [Read path] → [Cache] → [DB] → [Response]

PART 4: TRADE-OFFS & DEEP DIVES (10-15 min)

Trade-off 1: [Choice A] vs [Choice B]
- A pros: [pros]
- A cons: [cons]
- Decision: [X] because [reason]

Trade-off 2: [Choice C] vs [Choice D]
- C pros: [pros]
- C cons: [cons]
- Decision: [X] because [reason]

[Interviewer may ask deep dives on specific components...]
```

### Template 2: Scalability Discussion

```
SCALE: Current → 10x → 100x

Current scale:
- [X] QPS, [Y] GB storage
- Monolith + single DB → works fine

10x scale:
- [X]K QPS, [Y] TB
- Solution: Add [caching], [read replicas], [partitioning concern]

100x scale:
- [X]M QPS, [Y] PB
- Solution: [Shard DB], add [CDN], [async processing], [multi-region]

Bottleneck analysis:
1. [DB] → [shard/index/delegate to read slaves]
2. [Application server] → [horizontal scaling + load balancer]
3. [Monolith service] → [microservices decomposition]
```

### Template 3: Failure Handling

```
FAILURE MODE ANALYSIS

Q: "What happens when [component] fails?"

Detection: [How do we know?] → [Health check, metrics, alert]
Impact: [What breaks?] → [Partial vs complete outage]
Mitigation:
1. [Retry] → [Exponential backoff, jitter]
2. [Failover] → [Active-passive, multi-region]
3. [Degradation] → [Serve stale, limited functionality]
4. [Graceful shutdown] → [Circuit breaker, load shed]

Prevention:
1. [Redundancy] → [N+1 deployment, multi-AZ]
2. [Testing] → [Chaos engineering, load testing]
3. [Monitoring] → [Dashboards, alerts, runbooks]

Recovery:
1. [Revert] → [Rollback deployment]
2. [Rebuild] → [Replicate from healthy node]
3. [Replay] → [Re-process from message queue]
```

### Template 4: API Design

```
API: [Service Name]

Endpoints:

POST /resource
- Purpose: [action]
- Request: { [fields] }
- Response: 201 { id, status }
- Idempotency: [key in header]

GET /resource/:id
- Purpose: [action]
- Response: 200 { [resource fields] }
- Error: 404 if not found

GET /resources
- Purpose: [list/query]
- Params: pagination, filters
- Response: 200 { data: [...], next_cursor, total }

PATCH /resource/:id
- Purpose: [partial update]
- Request: { [fields to update] }
- Response: 200 { updated resource }

Design principles:
- RESTful resource naming (nouns, not verbs)
- Idempotent mutations (POST/PUT with idempotency key)
- Consistent error format: { error: { code, message, details } }
- Versioning: [URL prefix /v1/ or header Accept-Version]
```

### Template 5: Database Schema Design

```
SCHEMA: [Entity]

Table: [table_name]
Columns:
- id: UUID (PK, shard key)
- [field1]: VARCHAR(255) [NOT NULL, INDEX]
- [field2]: BIGINT [NOT NULL]
- created_at: TIMESTAMP [INDEX]
- updated_at: TIMESTAMP

Indexes:
- PRIMARY KEY (id)
- INDEX idx_field1 (field1)
- INDEX idx_created (created_at, field1)

Partitioning: [hash(id) % N] or [range(created_at)]

Denormalization:
- [field repeated from other table] → [reason]

Caching:
- [entity key] in [Redis] → [TTL] → [invalidation strategy]

Read path:
SELECT * FROM [table] WHERE id = ?  →  cache check → DB

Write path:
INSERT INTO [table] (...) VALUES (...) → cache invalidation
```

### Template 6: Consistency Model Discussion

```
CONSISTENCY DECISION

Requirement: [description of consistency need]

Option 1: Strong consistency
- How: [read-after-write, quorum, single-master]
- Cost: [higher latency, lower availability during partition]
- Suitability: [payments, inventory, user profile]

Option 2: Eventual consistency
- How: [async replication, CRDTs, multi-master]
- Cost: [stale reads, conflict resolution needed]
- Suitability: [social feed, analytics, content delivery]

Option 3: Causal consistency
- How: [vector clocks, session tokens]
- Cost: [metadata overhead, implementation complexity]
- Suitability: [collaboration, messaging]

Decision: [Option X] because [reason]
```

### Template 7: Performance Optimization

```
PERFORMANCE DEEP DIVE

Current state: [p99 latency] = [X]ms at [Y] QPS

Bottleneck: [identified bottleneck]

Optimization 1: [caching]
- What: [cache layer description]
- Impact: [expected improvement]
- Cost: [memory, complexity]

Optimization 2: [query optimization]
- What: [index, database tuning]
- Impact: [expected improvement]
- Cost: [write impact, storage]

Optimization 3: [async processing]
- What: [queue + worker pattern]
- Impact: [improved throughput]
- Cost: [eventual consistency, complexity]

Expected result: p99 latency = [Y]ms at [Z] QPS
```
