# Amazon Distributed Systems Interview Guide

> Complete preparation guide for distributed systems roles at Amazon.

---

## How Amazon Tests Distributed Systems

Amazon's interview process uniquely emphasizes Leadership Principles (LPs) through the lens of distributed systems. Every technical answer should demonstrate LP alignment.

### Interview Rounds That Test DS

1. **System Design (1-2 rounds)**: The primary DS evaluation. Usually 60 minutes.
2. **Bar Raiser (1 round)**: Deep dive into DS decisions + leadership.
3. **Coding (1-2 rounds)**: Often includes DS-adjacent problems.
4. **Manager Round**: Team fit + system ownership discussion.

### Amazon's Unique DS Focus

- **Operational Excellence**: Every system design must include monitoring, deployment, rollback.
- **Cost Optimization**: AWS services are metered; designs must account for cost. Interviewers will ask "How much does this cost?"
- **Fault Isolation**: Cell-based architecture (blast radius limitation).
- **Leadership Principles**: "Have Backbone" - defend your design decisions with data.
- **DynamoDB Deep Dive**: Consistent hashing, gossip, hinted handoff, Merkle trees.

### Amazon's Systems You Should Know

| System | Key Concepts |
|--------|-------------|
| DynamoDB | Consistent hashing, NWR, gossip, vector clocks |
| S3 | Object storage, CRUSH placement, eventually -> strongly consistent |
| SQS | At-least-once, polling vs push, dead letter queues |
| Kinesis | Sharded stream, retention, replay |
| Lambda | Event-driven, cold start, execution environment |
| Route 53 | DNS-based routing, health checks, failover |
| CloudFront | CDN, edge locations, origin shielding |
| CloudWatch | Metrics, logs, alarms, dashboards |

---

## Top 15 Distributed Systems Questions at Amazon

### Question 1: Design DynamoDB
**Difficulty**: Very Hard | **Frequency**: Very High

**Answer Framework**:
```
Partitioning: Consistent hashing on partition key
- Ring distributed across nodes (range [0, 2^128-1])
- Virtual nodes for load balancing (each physical node = 100 virtual nodes)
- Partition splitting for hot partitions

Replication: NWR Quorum model
- N = 3 (default), W = 2 (write quorum), R = 2 (read quorum)
- Write: write to W replicas before acknowledging
- Read: read from R replicas, return latest version
- Strongly consistent: R + W > N

Failure Handling:
- Hinted handoff: write to another node if primary unavailable
- Merkle trees for anti-entropy (compare hash trees between nodes)
- Gossip protocol for membership changes
- Read repair: fix stale replicas during reads

Consistency:
- Eventually consistent reads: R + W <= N
- Strongly consistent reads: R + W > N
- Vector clocks for causality tracking
- Last-writer-wins for conflict resolution

Storage:
- Write-ahead log (commit log) -> in-memory memtable -> SSTable on disk
- Bloom filters for efficient key lookup
- Compaction for merging SSTables
```

### Question 2: Design Amazon S3
**Difficulty**: Very Hard | **Frequency**: Very High

**Answer Framework**:
```
Requirements:
- Unlimited object storage (0 bytes to 5TB per object)
- 11 9's durability (99.999999999%)
- Eventual consistency (now strong after 2020 update)

Architecture:
- FrontEnd: authentication, rate limiting, request routing
- Metadata Store: object -> partition mapping
- Partition Store: actual data placement
- Storage Nodes: data replication across AZs

Data Model:
- Bucket: namespace for objects
- Object Key: full path (partition key for sharding)
- Object: data + metadata + access control

Placement:
- CRUSH-like algorithm for deterministic placement
- Replication across 3+ Availability Zones
- Erasure coding for storage efficiency

Failure Handling:
- Auto-healing: background data integrity checks
- Cross-region replication for DR
- Multi-part upload for large objects
```

### Question 3: Design Shopping Cart
**Difficulty**: Medium | **Frequency**: High

**Answer Framework**:
```
Requirements:
- Session management for authenticated + anonymous users
- Cart merge on login
- Persistent across devices

Design:
- Session store: DynamoDB with TTL
- Cart key: user_id (authenticated) or session_id (anonymous)
- Cart items: JSON blob or separate items table
- Optimistic locking for concurrent edits

Consistency:
- Strong consistency for reads (critical)
- Conditional writes for stock validation

Failure Handling:
- Cart replication across AZs
- Graceful degradation: locally cached cart if DynamoDB unavailable
```

### Question 4: Design Rate Limiter
**Difficulty**: Medium | **Frequency**: Very High

**Answer Framework**:
```
Algorithm: Token bucket or sliding window
- Per-user, per-API, per-region limits
- Hierarchical rate limits

Distributed Coordination:
- Redis cluster with Lua scripts for atomic operations
- Consistent hashing for rate limit distribution
- Local counters with periodic sync

Response: HTTP 429 with Retry-After header
- Exponential backoff for retrying clients

Monitoring: Rate limit hit rates, abuse detection
```

### Question 5: Design Recommendation Engine
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Offline Pipeline**: Spark/MapReduce for collaborative filtering
- **Online Serving**: ML model served from DynamoDB cache
- **Real-time Signals**: Item interaction tracking via Kinesis
- **Cold Start**: Content-based recommendations for new users/items
- **A/B Testing**: Split traffic for model evaluation

### Question 6: Design Distributed Locking Service
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **DynamoDB-based**: conditional writes (attribute_not_exists)
- **Lease-based**: lock with TTL, periodic renewal
- **Fencing Tokens**: monotonically increasing token on each lock acquisition
- **Failure Handling**: TTL expiry releases lock automatically

### Question 7: Design Event-Driven Architecture
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Event Source**: SQS, SNS, Kinesis, EventBridge
- **Processing**: Lambda, Step Functions, ECS
- **DLQ**: Failed message handling
- **Idempotency**: deduplication IDs
- **Ordering**: partition-based within Kinesis shard

### Question 8: Design Global Deployment System
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Cell-Based Architecture**: each cell serves a partition of users
- **Canary Deployments**: 1% -> 5% -> 20% -> 100%
- **Traffic Shifting**: Route 53 weight-based routing
- **Deployment Pipeline**: CodePipeline + CodeDeploy
- **Rollback**: automated rollback on alarm breach

### Question 9: Design Metrics Aggregation
**Difficulty**: Medium | **Frequency**: Medium

**Answer Components**:
- **Collection**: CloudWatch agent, put-metric-data API
- **Storage**: time-series DB, dimensional metrics
- **Aggregation**: pre-aggregated statistics (avg, p50, p99, p999)
- **Alarms**: threshold-based, anomaly detection

### Question 10: Design Key Management System
**Difficulty**: Hard | **Frequency**: Medium-Low

**Answer Components**:
- **Key Hierarchy**: master key -> key encryption key -> data encryption key
- **HSM**: hardware security module for master keys
- **Rotation**: automatic key rotation with versioning
- **Audit**: CloudTrail logging for key usage

### Question 11: Design Content Delivery Network
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Edge Locations**: POPs close to users
- **Origin Shield**: protect origin from traffic storms
- **Cache Policy**: TTL, cache key, query string handling
- **Invalidation**: file-specific or wildcard invalidation

### Question 12: Design a Distributed Counter
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Pre-Sharded Counters**: N shards per counter, each with atomic increment
- **Batching**: buffer writes locally, batch update DynamoDB
- **CRDTs**: conflict-free counters for multi-leader deployments
- **Read Path**: sum across all shards

### Question 13: Design Distributed Session Management
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Session Store**: DynamoDB with DAX caching
- **Session Key**: session token (opaque string)
- **TTL**: session expiry with auto-cleanup
- **Stickiness**: optional (consistent hashing if needed)

### Question 14: Design A/B Testing Platform
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Assignment**: hash-based split, consistent across sessions
- **Metrics Pipeline**: collect, aggregate, analyze
- **Statistical Engine**: t-tests, sequential testing
- **Rollback**: automatic ramp-down on negative impact

### Question 15: Design Distributed Data Pipeline
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Ingest**: Kinesis Data Streams for real-time
- **Process**: Kinesis Data Analytics / Spark on EMR
- **Storage**: S3 data lake with Parquet format
- **Catalog**: AWS Glue Data Catalog
- **Query**: Athena, Redshift Spectrum

---

## Amazon-Specific Evaluation Criteria

| Criteria | Weight | What They Look For |
|----------|--------|-------------------|
| System Design | 35% | Scalability, fault isolation, cost |
| Coding | 25% | Clean, correct, testable code |
| Leadership Principles | 25% | Every answer shows LP alignment |
| Bar Raiser | 15% | Overall hire/no-hire signal |

### How to Embed LPs in Your Answers

| LP | How to Show It |
|---|----------------|
| Customer Obsession | "This failure impacted 10K users, so I prioritized..." |
| Ownership | "I took ownership of the entire pipeline..." |
| Bias for Action | "Rather than waiting, I deployed a quick fix..." |
| Dive Deep | "I traced the packet from LB to DB..." |
| Deliver Results | "We reduced p99 latency by 60%..." |
| Have Backbone | "I disagreed with the architect and presented data..." |

---

## Java Code Examples for Amazon Interviews

### Implementation: DynamoDB-Style Consistent Hashing

```java
public class DynamoConsistentHash {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private static final int VIRTUAL_NODES = 150;

    public void addNode(String nodeId) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            String vnodeId = nodeId + ":" + i;
            ring.put(hash(vnodeId), nodeId);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
    }

    // Consistent hashing uses MD5 in DynamoDB
    private int hash(String key) {
        return key.hashCode() & Integer.MAX_VALUE;
    }
}
```

### Implementation: Sliding Window Rate Limiter

```java
public class SlidingWindowRateLimiter {
    private final TreeMap<Long, Integer> window = new TreeMap<>();
    private final int maxRequests;
    private final long windowMs;

    public SlidingWindowRateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }

    public synchronized boolean allow() {
        long now = System.currentTimeMillis();
        long boundary = now - windowMs;
        window.headMap(boundary, true).clear();
        int current = window.values().stream().mapToInt(Integer::intValue).sum();
        if (current < maxRequests) {
            window.merge(now, 1, Integer::sum);
            return true;
        }
        return false;
    }
}
```

---

## Study Plan for Amazon DS Interviews

### Week 1-2: DynamoDB Deep Dive
- Read DynamoDB paper
- Implement consistent hashing in Java
- Study DynamoDB Internals blog posts

### Week 3-4: AWS Service Architecture
- Understand S3, SQS, SNS, Kinesis internals
- Study cell-based architecture patterns
- Practice cost-aware system design

### Week 5: Leadership Principles Integration
- Prepare 10 STAR stories with LP alignment
- Practice embedding LPs in system design discussions
- Mock interview with LP-focused feedback

### Key LeetCode Problems
| Problem | # | Amazon Relevance |
|---------|---|-----------------|
| LRU Cache | 146 | DynamoDB caching, asked weekly |
| Design HashMap | 706 | Partition key hashing |
| Number of Islands | 200 | Cell-based architecture |
| Merge Intervals | 56 | Vector clocks, consistency |
| Course Schedule II | 210 | Dependency ordering |
| Meeting Rooms II | 253 | Resource scheduling |
| Task Scheduler | 621 | Distributed scheduling |
| Sliding Window Max | 239 | Stream processing |
| Word Ladder | 127 | BFS graph replication |
| N-Queens II | 52 | Conflict resolution |

---

> **Amazon Tip**: Amazon interviewers will ask "What happens if this fails?" repeatedly. Every design choice must consider failure modes. "Bias for Action" doesn't mean skip testing - it means make pragmatic progress while managing risk.