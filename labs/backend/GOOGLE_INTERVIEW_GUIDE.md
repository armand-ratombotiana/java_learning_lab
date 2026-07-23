# Google Interview Guide — Backend Academy

## Interview Process for Backend Roles

Google's backend interview process typically spans 6-8 weeks with 5-7 rounds:

1. **Phone Screen (45 min)** — Technical phone screen with a backend engineer covering data structures, algorithms, and system design basics. Expect a live-coding session in a shared doc.
2. **Coding Round 1 (45 min)** — Algorithms and data structures with backend context (e.g., designing a rate limiter, implementing a distributed counter).
3. **Coding Round 2 (45 min)** — More complex algorithmic problem, often involving graph traversal or dynamic programming applied to a backend scenario like request routing or dependency resolution.
4. **System Design Round (60 min)** — Design a large-scale backend system. Google emphasizes scalability, fault tolerance, and clean abstractions.
5. **Googliness / Leadership Round (45 min)** — Behavioral questions focused on leadership, ambiguity, and cross-functional collaboration.
6. **Backend Deep Dive (45 min)** — Based on your resume, deep dive into past backend projects, architecture decisions, and trade-offs.

Google expects backend engineers to be strong in distributed systems, concurrency, and API design. The interview bar is high for scalability thinking.

## Top Problems by Backend Topic

### Topic: Distributed Systems & Consensus

#### Problem: Design a Distributed Unique ID Generator
- **LC/System Design ref**: System Design: Design a Distributed ID Generator (Snowflake-like)
- **Problem statement**: Design a backend service that generates unique, time-ordered 64-bit IDs across multiple data centers without a central coordinator.
- **Interview walkthrough**: Start by clarifying requirements — IDs must be unique, roughly sortable by time, and fit in 64 bits. Discuss approaches: UUID (not sortable, 128-bit), database auto-increment (single point of failure), Snowflake approach. Walk through the Snowflake layout: timestamp (41 bits), data center ID (5 bits), machine ID (5 bits), sequence (12 bits). Discuss clock skew handling, sequence overflow, and high availability.
- **Solution**: Use a Snowflake-inspired ID generator. Each service instance gets a unique worker ID via ZooKeeper or Redis. The ID comprises timestamp + worker ID + sequence number. Handle clock drift by tracking last timestamp and waiting if clock moves backward. Use sequence reset on each millisecond.
- **Java/Spring code**:
```java
@Component
public class SnowflakeIdGenerator {
    private final long datacenterId;
    private final long machineId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(@Value("${datacenter.id}") long datacenterId,
                                 @Value("${machine.id}") long machineId) {
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset > 5000) throw new RuntimeException("Clock moved backwards >5s");
            Thread.sleep(offset + 1);
            timestamp = System.currentTimeMillis();
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 4095;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << 22)
             | (datacenterId << 17)
             | (machineId << 12)
             | sequence;
    }
}
```

#### Problem: Design a Distributed Rate Limiter
- **LC/System Design ref**: System Design: Rate Limiter
- **Problem statement**: Design a rate-limiting service that enforces per-user API rate limits across multiple backend services and data centers.
- **Interview walkthrough**: Clarify whether it's client-side or server-side, per-user or per-API. Discuss token bucket vs sliding window vs leaky bucket. For distributed environments, discuss using Redis with sorted sets for sliding window or Lua scripts for atomic token bucket operations. Talk about consistency vs availability trade-offs.
- **Solution**: Use Redis with a sliding window log algorithm. Each user has a sorted set with timestamps as scores. On each request, remove timestamps older than the window, count remaining entries, and allow if under limit. Use Redis Lua scripting for atomicity. Fall back to local rate limiting if Redis is unavailable.
- **Java/Spring code**:
```java
@Component
public class RateLimiter {
    private final StringRedisTemplate redis;
    private final int maxRequests = 100;
    private final long windowMs = 60_000;

    public boolean allowRequest(String userId) {
        String key = "ratelimit:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - windowMs;

        return redis.execute((RedisCallback<Boolean>) conn -> {
            conn.multi();
            conn.zRemRangeByScore(key.getBytes(), 0, windowStart);
            conn.zAdd(key.getBytes(), now, String.valueOf(now).getBytes());
            conn.expire(key.getBytes(), 120);
            conn.zCard(key.getBytes());
            List<Object> results = conn.exec();
            return (Long) results.get(results.size() - 1) <= maxRequests;
        });
    }
}
```

#### Problem: Design a Distributed Task Scheduler
- **LC/System Design ref**: System Design: Distributed Job Scheduler
- **Problem statement**: Build a cron-like distributed task scheduler that can handle millions of scheduled jobs with second-level precision, supports retries, and survives node failures.
- **Interview walkthrough**: Start with single-node Quartz scheduler, then discuss scaling challenges. Introduce a leader-election pattern using ZooKeeper or etcd. Discuss partitioning jobs across workers using consistent hashing. Talk about storing job definitions in a database with status tracking. For high-frequency jobs, use a time-wheel in-memory with a persistent store for durability.
- **Solution**: Use a leader-worker architecture. The leader partitions the time horizon and assigns windows to workers. Each worker maintains a in-memory priority queue of upcoming jobs. Jobs are persisted in PostgreSQL with status (SCHEDULED, RUNNING, COMPLETED, FAILED). A reconciliation process handles missed jobs on leader failover. Use Spring's TaskScheduler for local execution and a distributed lock (Redis/MySQL) for job ownership.
- **Java/Spring code**:
```java
@Service
public class DistributedJobScheduler {
    private final JobRepository jobRepository;
    private final TaskScheduler taskScheduler;
    private final RedisLockRegistry lockRegistry;

    @Scheduled(fixedRate = 5000)
    public void processScheduledJobs() {
        List<Job> dueJobs = jobRepository.findByStatusAndScheduledAtBefore(
            JobStatus.SCHEDULED, Instant.now());
        for (Job job : dueJobs) {
            lockRegistry.obtain("job:" + job.getId()).runWithinLock(() -> {
                job.setStatus(JobStatus.RUNNING);
                jobRepository.save(job);
                taskScheduler.schedule(() -> executeJob(job), job.getScheduledAt());
            });
        }
    }

    private void executeJob(Job job) {
        try {
            // job logic
            job.setStatus(JobStatus.COMPLETED);
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setError(e.getMessage());
        }
        jobRepository.save(job);
    }
}
```

- **What Google evaluates**: Handling clock skew, fault tolerance, idempotency, and clean concurrency models.
- **Follow-ups**: How do you handle backpressure? What if ZooKeeper is down? How do you monitor scheduler lag?

### Topic: API Design & REST

#### Problem: Design a Paginated, Filterable Search API
- **LC/System Design ref**: System Design: Search API
- **Problem statement**: Design a REST API for searching a product catalog with filtering, sorting, and cursor-based pagination. The catalog has 10M+ products.
- **Interview walkthrough**: Discuss cursor-based vs offset pagination — offset breaks under high write load. Use cursor (last seen ID or sort value) for stable pagination. For filtering, use query parameter composition. Discuss indexing strategy in PostgreSQL and when to introduce Elasticsearch for full-text search. Talk about API versioning strategy (URL prefix vs header).
- **Solution**: RESTful API with cursor-based pagination. Use composite indexes on (category, price, id) for sorted queries. For full-text search, integrate Elasticsearch with a write-through cache. Return opaque cursor tokens (base64-encoded last sort value). API versioned via Accept header or URL prefix.
- **Java/Spring code**:
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductSearchController {
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<PageResponse<Product>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {

        Pageable pageable = PageRequest.of(0, limit + 1);
        List<Product> products;

        if (cursor != null) {
            String decoded = new String(Base64.getDecoder().decode(cursor));
            String[] parts = decoded.split(":");
            String lastId = parts[0];
            BigDecimal lastPrice = new BigDecimal(parts[1]);
            products = productRepository.findByCursor(category, minPrice, lastId, lastPrice, pageable);
        } else {
            products = productRepository.findWithFilters(category, minPrice, pageable);
        }

        boolean hasMore = products.size() > limit;
        if (hasMore) products.remove(products.size() - 1);

        String nextCursor = null;
        if (hasMore) {
            Product last = products.get(products.size() - 1);
            String raw = last.getId() + ":" + last.getPrice();
            nextCursor = Base64.getEncoder().encodeToString(raw.getBytes());
        }

        return ResponseEntity.ok(new PageResponse<>(products, nextCursor));
    }
}
```

- **What Google evaluates**: Clean API design, understanding of database indexing, pagination stability, and versioning strategy.
- **Follow-ups**: How would you add faceted search? How do you handle partial updates? How would you design for GraphQL?

### Topic: Concurrency & Threading

#### Problem: Design a Thread-Safe Cache with TTL
- **LC/System Design ref**: LeetCode 146 (LRU Cache variant)
- **Problem statement**: Implement a thread-safe in-memory cache with time-to-live eviction, maximum size, and least-recently-used eviction policy. The cache must be highly concurrent.
- **Interview walkthrough**: Discuss ConcurrentHashMap vs synchronized maps. For LRU, combine ConcurrentHashMap with a ConcurrentLinkedDeque or use a ReadWriteLock. For TTL, use a scheduled executor to evict expired entries or check lazily on access. Discuss trade-offs of striped locking vs CAS operations.
- **Solution**: Use ConcurrentHashMap as the backing store. Each value wraps the actual value with an expiration timestamp. On get, check expiration and remove if stale. For LRU, maintain a ConcurrentLinkedDeque of keys; on access, move key to front. Use a background scheduled thread to purge expired entries every minute. For higher concurrency, use striped locks (striped of 16 locks) for eviction.
- **Java/Spring code**:
```java
@Component
public class ConcurrentCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<K> accessOrder = new ConcurrentLinkedDeque<>();
    private final int maxSize;
    private final long ttlMs;

    public ConcurrentCache(@Value("${cache.max-size:10000}") int maxSize,
                           @Value("${cache.ttl-ms:300000}") long ttlMs) {
        this.maxSize = maxSize;
        this.ttlMs = ttlMs;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::evictExpired, 1, 1, TimeUnit.MINUTES);
    }

    public V get(K key) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() > entry.expiresAt()) {
            map.remove(key);
            return null;
        }
        accessOrder.remove(key);
        accessOrder.addFirst(key);
        return entry.value();
    }

    public void put(K key, V value) {
        map.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMs));
        accessOrder.remove(key);
        accessOrder.addFirst(key);
        if (map.size() > maxSize) {
            K oldest = accessOrder.removeLast();
            map.remove(oldest);
        }
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        map.entrySet().removeIf(e -> now > e.getValue().expiresAt());
    }

    private record CacheEntry<V>(V value, long expiresAt) {}
}
```

- **What Google evaluates**: Thread safety, lock-free techniques, understanding of eviction policies, and memory management.
- **Follow-ups**: How would you add metrics? How would you support write-through to a database? How would you handle cache stampede?

### Topic: Database Design & Transactions

#### Problem: Design a Blog Platform Schema with Concurrent Writes
- **LC/System Design ref**: System Design: Blogging Platform
- **Problem statement**: Design the database schema for a blogging platform where multiple authors can collaborate on posts, with version history and concurrent edit detection.
- **Interview walkthrough**: Discuss normalization vs denormalization. For concurrent edits, use optimistic locking with a version column. For version history, use a separate `post_versions` table. Discuss indexing strategy for feed queries (author_id, created_at). Talk about read replicas for scaling reads and write master for consistency.
- **Solution**: Use PostgreSQL with optimistic locking. Schema: `posts(id, title, content, author_id, version, created_at, updated_at)`, `post_versions(id, post_id, content, edited_by, version, created_at)`. Use `@Version` annotation in JPA for optimistic locking. For feed queries, use a materialized view or denormalized cache. Use database-level foreign keys with indexes on (author_id, created_at DESC).
- **Java/Spring code**:
```java
@Entity
@Table(name = "posts")
public class Post {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private String title;
    @Lob @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne(fetch = LAZY)
    private User author;
    @Version
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostVersionRepository versionRepository;

    public Post updatePost(UUID postId, String newContent, User editor) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        PostVersion version = new PostVersion(post, post.getContent(), editor, post.getVersion());
        versionRepository.save(version);
        post.setContent(newContent);
        return postRepository.save(post); // @Version triggers optimistic lock
    }
}
```

- **What Google evaluates**: Transaction management, optimistic locking, schema design for versioning, and indexing strategy.
- **Follow-ups**: How would you handle hot rows? How would you migrate schema without downtime? How would you implement soft deletes?

### Topic: Caching & Performance

#### Problem: Design a Multi-Layer Cache for a News Feed
- **LC/System Design ref**: System Design: News Feed
- **Problem statement**: Design a caching strategy for a personalized news feed that serves 100M daily active users with sub-200ms latency.
- **Interview walkthrough**: Discuss cache hierarchy: L1 (local memory cache), L2 (Redis cluster), L3 (CDN for static assets). For personalized feeds, cache user feed IDs in Redis sorted sets (score = timestamp). Pre-generate feeds for active users via a background worker. Discuss cache invalidation: write-through for author posts, TTL-based expiry for less active users. Talk about cache warming for returning users.
- **Solution**: Use a three-tier cache. L1: Caffeine cache per pod for hot user feeds (TTL 30s). L2: Redis cluster with sharding by userId. Store feed as a sorted set of post IDs. L3: PostgreSQL with materialized views for feed generation. A feed worker pre-computes feeds for users with high activity scores. Cache-aside pattern: on miss, generate feed from followers graph and store in Redis.
- **Java/Spring code**:
```java
@Service
public class FeedService {
    private final Cache<UUID, List<Post>> localCache;
    private final StringRedisTemplate redis;
    private final PostRepository postRepository;

    public FeedService() {
        this.localCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();
    }

    public List<Post> getFeed(UUID userId, int page, int size) {
        String cacheKey = "feed:" + userId + ":" + page;
        List<Post> local = localCache.getIfPresent(userId);
        if (local != null) return local;

        String redisKey = "feed:" + userId;
        Set<String> postIds = redis.opsForZSet().reverseRange(redisKey, page * size, (page + 1) * size - 1);
        if (postIds != null && !postIds.isEmpty()) {
            List<Post> posts = postRepository.findAllById(postIds.stream().map(UUID::fromString).toList());
            localCache.put(userId, posts);
            return posts;
        }

        List<Post> posts = generateFeed(userId, page, size);
        cacheFeed(userId, posts);
        return posts;
    }
}
```

- **What Google evaluates**: Multi-layer caching strategy, cache invalidation, trade-off analysis between consistency and performance.
- **Follow-ups**: How would you personalize the feed? How do you handle cache warming for new users? How would you A/B test feed ranking?

## System Design Questions

### Design YouTube (Video Platform Backend)
- **Requirements**: Upload, transcode, stream, search, recommend. 500 hours of video uploaded per minute.
- **Framework**: Start with requirements estimation (storage: 500h * 60min * ~500MB/h = 150TB/day). Discuss video processing pipeline: upload service writes to blob store (GCS), pushes message to Pub/Sub, transcoding workers pick up jobs, update metadata in Spanner. For streaming, use CDN with adaptive bitrate. For search, use inverted index in Spanner + custom indexing service. For recommendations, use a collaborative filtering pipeline with TensorFlow.
- **Key trade-offs**: Consistency vs availability for view counts. Synchronous vs async transcoding. Hot vs cold storage tiers.
- **Google-specific**: Emphasize Spanner for global consistency, Borg for orchestration, and Colossus for storage.

### Design Google Docs (Collaborative Real-Time Editor)
- **Requirements**: Real-time collaboration, conflict resolution, offline support, 1M+ concurrent users per document.
- **Framework**: Discuss CRDTs vs OT (Operational Transformation). Google Docs uses OT. Walk through the architecture: client sends ops to a frontend server, which forwards to a document service. The document service maintains the authoritative document state and broadcasts ops to other connected clients via a push channel (WebSocket or SSE). For persistence, use a distributed log (like Spanner) to store op history. Discuss how to handle conflicts with OT: transform incoming ops against concurrent ops using a transformation function.
- **Key trade-offs**: OT complexity vs CRDT storage overhead. Google chose OT for predictability. Discuss how to handle undo, cursor synchronization, and reconnection.
- **Google-specific**: Emphasize understanding of Paxos/Raft for consensus, Spanner for global consistency, and the Chubby lock service.

### Design Google Search (Web Crawler & Indexer)
- **Requirements**: Crawl billions of pages, index them, serve search results in <200ms.
- **Framework**: Discuss the three-stage pipeline: crawling, indexing, serving. Crawling: URL frontier managed by a priority queue, politeness policies (robots.txt, rate limiting), distributed crawler workers. Indexing: MapReduce-style pipeline to build inverted index, term frequency, document frequency. Serving: shard the index across many machines, use a scatter-gather approach for queries. Discuss PageRank as a link analysis algorithm run on the web graph.
- **Key trade-offs**: Freshness vs crawl budget. Index size vs query latency. Precision vs recall in ranking.
- **Google-specific**: Discuss Google File System (GFS), Bigtable, MapReduce, and Borg. Emphasize the scale — billions of documents, sub-second query latency.

## Behavioral Questions

### Tell me about a time you improved a system's reliability
- **Situation**: Our payment processing service had 99.5% uptime, below the 99.99% SLO. Failures were caused by database connection pool exhaustion under traffic spikes.
- **Task**: Identify root causes and implement a solution to reach 99.99% uptime without a full rewrite.
- **Action**: Introduced a connection pool with HikariCP configured with max-lifetime validation, added a circuit breaker (Resilience4j) around database calls, implemented a health check endpoint for the load balancer, and added a bulkhead pattern to isolate payment processing from other services. Also added structured logging and metrics for connection pool utilization.
- **Result**: Uptime improved to 99.995%. P99 latency dropped from 1200ms to 180ms. The circuit breaker prevented cascading failures during two subsequent database incidents.
- **What Google evaluates**: Systematic debugging, measurable impact, and understanding of production systems.

### Tell me about a time you had to make a trade-off
- **Situation**: We needed to ship a new search feature in 6 weeks. The ideal solution involved building a new indexing pipeline with Elasticsearch, but that would take 12 weeks.
- **Task**: Deliver value to users within the deadline while setting up for the long-term solution.
- **Action**: Implemented a hybrid approach — used PostgreSQL full-text search (tsvector) for the initial launch, while parallelizing the Elasticsearch migration. Added database indexes on tsvector columns, used materialized views for common search patterns, and designed the API contract to be search-engine agnostic. The Elasticsearch migration was completed 4 weeks later with zero API changes.
- **Result**: Shipped on time with 80% of the search quality. Post-migration, search latency dropped from 500ms to 50ms and relevance improved by 35%.
- **What Google evaluates**: Pragmatic trade-off thinking, ability to ship incrementally, and long-term architectural vision.

### Tell me about a complex bug you resolved
- **Situation**: A production issue caused intermittent 5-second latency spikes in our API gateway every few minutes. The issue was hard to reproduce.
- **Task**: Find and fix the root cause without impacting availability.
- **Action**: Added distributed tracing with OpenTelemetry, correlated the spikes with full GC pauses. Discovered that a logging library was allocating a large char array on every request, triggering GC pressure. Replaced the logging pattern with async logging and fixed a thread pool leak in the HTTP client. Used JMH benchmarks to validate the fix.
- **Result**: P99 latency dropped from 5s to 200ms. GC pause time reduced by 90%. The fix was applied to all services in the organization.
- **What Google evaluates**: Deep debugging skills, use of observability tools, and systematic root cause analysis.

### Describe a project where you designed for scale
- **Situation**: Our notification service sent 10M push notifications/day but was struggling with delivery latency and duplicate notifications.
- **Task**: Redesign the service to handle 100M/day with idempotent delivery and <100ms processing per notification.
- **Action**: Implemented an outbox pattern with a transactional outbox table in PostgreSQL. A separate relay process reads the outbox and publishes to a Kafka topic. Downstream consumers (push, email, SMS) read from Kafka with idempotent processing using a deduplication key (notification_id). Used Spring Cloud Stream with Kafka binder. Added circuit breakers per channel type.
- **Result**: Throughput increased 10x to 100M notifications/day. Idempotent delivery eliminated duplicate notifications. P99 processing time dropped to 45ms.
- **What Google evaluates**: End-to-end system thinking, idempotency patterns, and reliability engineering.

## Study Plan

Priority labs for Google backend interviews:
1. **Spring Boot Internals** — Google digs deep into framework internals
2. **Distributed Systems / Spring Cloud** — Microservices, service discovery, circuit breakers
3. **Security Deep** — Google cares deeply about security architecture
4. **Performance** — Profiling, benchmarking, JVM tuning
5. **Testing** — Unit, integration, contract testing
6. **API Design / REST APIs** — Foundation for all backend roles
7. **Caching** — Multi-layer caching strategies
8. **Messaging** — Kafka, Pub/Sub patterns
9. **GraalVM** — Native compilation, performance optimization
10. **Spring Boot Internals** — Auto-configuration, conditionals, bean lifecycle

## Tips

- Google expects you to think aloud. Never code in silence — narrate your approach, trade-offs, and assumptions.
- For system design, start with requirements clarification and estimation before jumping to architecture. Google interviewers heavily weight the scoping phase.
- Know Google's internal technologies: Spanner, Bigtable, Borg, Colossus, Pub/Sub, and how they compare to open-source alternatives.
- Backend depth matters: be ready to discuss JVM memory model, garbage collection tuning, and network protocols (gRPC, HTTP/2).
- Google values "intellectual honesty" — admit when you don't know something and reason from first principles.
- Practice the "what happens when you type a URL" style tracing question — Google loves end-to-end understanding.
- For system design, always discuss monitoring, alerting, and incident response. Google SRE culture means reliability is everyone's responsibility.
- Know Google's Site Reliability Engineering (SRE) principles: SLIs, SLOs, error budgets, and how they apply to backend services.
