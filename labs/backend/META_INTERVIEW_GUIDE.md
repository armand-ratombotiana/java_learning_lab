# Meta Interview Guide — Backend Academy

## Interview Process for Backend Roles

Meta's backend interview process typically spans 4-6 weeks with 4-5 rounds:

1. **Phone Screen (45 min)** — Coding and algorithms with a backend engineer. Expect a medium-difficulty LeetCode problem with a backend context (e.g., designing a logger, implementing a rate limiter).
2. **Coding Round 1 (45 min)** — Algorithms and data structures. Meta focuses on arrays, strings, trees, and recursion. Problems are often variations of LeetCode mediums.
3. **Coding Round 2 (45 min)** — Second coding round, may include a system design component or a more complex algorithmic problem. Meta likes real-world scenarios like "design a function to merge calendar events."
4. **System Design Round (60 min)** — Design a backend system. Meta focuses on data modeling, real-time features, and handling scale. Examples: design a news feed, a messaging system, or a real-time comments system.
5. **Behavioral Round (45 min)** — Focus on Meta's values: Move Fast, Be Open, Build Social Value, Focus on Impact. Expect questions about conflict resolution, cross-team collaboration, and product impact.

Meta's backend interviews emphasize practical engineering, real-time systems, and data-intensive applications. Expect less focus on pure algorithms and more on system design and coding clarity.

## Top Problems by Backend Topic

### Topic: Real-Time Data Processing

#### Problem: Design a Real-Time Comments System
- **LC/System Design ref**: System Design: Live Comments
- **Problem statement**: Design a backend system for a live video stream that supports real-time comments with 100ms delivery latency, 1M concurrent viewers, and moderation filtering.
- **Interview walkthrough**: Discuss WebSocket vs SSE vs polling. For real-time bidirectional communication, WebSocket is preferred. Use a fan-out pattern: when a comment is posted, publish to a Redis Pub/Sub channel or Kafka topic. Each server instance subscribes and pushes to its connected WebSocket clients. For moderation, use a separate service that scans comments asynchronously and flags inappropriate content. For persistence, use Cassandra for time-series comment data.
- **Solution**: WebSocket server cluster behind a load balancer with sticky sessions (or use a global connection registry in Redis). When a comment arrives, the receiving server validates, persists to Cassandra, and publishes to a Redis Pub/Sub channel. All server instances subscribe to the channel and push to their connected clients. For moderation, send comments to a moderation queue (Kafka) and update the comment status asynchronously.
- **Java/Spring code**:
```java
@Controller
public class CommentWebSocketHandler implements WebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final RedisTemplate<String, String> redis;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        sessions.put(userId, session);
        redis.subscribe(listener(), "comments:" + getStreamId(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Comment comment = objectMapper.readValue(message.getPayload(), Comment.class);
        commentService.save(comment);
        redis.convertAndSend("comments:" + comment.streamId(), message.getPayload());
    }

    private MessageListener listener() {
        return (msg, pattern) -> {
            String payload = new String(msg.getBody());
            sessions.values().forEach(s -> {
                try { s.sendMessage(new TextMessage(payload)); }
                catch (IOException e) { log.error("Send failed", e); }
            });
        };
    }
}
```

- **What Meta evaluates**: Real-time system design, WebSocket management, fan-out at scale, and moderation pipeline design.
- **Follow-ups**: How would you handle a viral stream with 10M concurrent viewers? How would you implement rate limiting for comments? How would you support threaded replies?

#### Problem: Design a Real-Time Notification System
- **LC/System Design ref**: System Design: Notification Service
- **Problem statement**: Design a notification system that delivers push notifications, emails, and in-app notifications to 1B+ users with <500ms delivery latency.
- **Interview walkthrough**: Discuss the notification pipeline: API gateway receives notification request, validates, enriches with user preferences, and publishes to a Kafka topic. Downstream consumers handle each channel (push via FCM/APNs, email via SendGrid, in-app via WebSocket). For deduplication, use a Bloom filter or Redis set with TTL. For rate limiting per user, use a sliding window counter. Discuss handling of undelivered notifications and retry queues.
- **Solution**: Use a tiered approach. A notification service receives requests and writes to a PostgreSQL outbox. A relay reads the outbox and publishes to Kafka partitioned by user_id. Each channel consumer (push, email, in-app) reads from its own Kafka topic. For push notifications, use Firebase Cloud Messaging with a connection pool. For in-app, use WebSocket with a registry of active connections. Use a dead-letter queue for failed deliveries with a retry scheduler.
- **Java/Spring code**:
```java
@Service
public class NotificationRelay {
    private final KafkaTemplate<String, NotificationEvent> kafka;

    @Transactional
    @Scheduled(fixedRate = 1000)
    public void relayOutbox() {
        List<OutboxEvent> events = outboxRepository.findTop100ByStatus(OutboxStatus.PENDING);
        for (OutboxEvent event : events) {
            kafka.send("notifications", event.getPartitionKey(), event.toEvent());
            event.setStatus(OutboxStatus.PUBLISHED);
        }
        outboxRepository.saveAll(events);
    }
}

@Component
public class PushNotificationConsumer {
    @KafkaListener(topics = "notifications-push", concurrency = "4")
    public void sendPush(ConsumerRecord<String, Notification> record) {
        Notification notification = record.value();
        if (deduplicationService.isDuplicate(notification.id())) return;
        try {
            fcmClient.send(notification.userToken(), notification.payload());
            deduplicationService.markProcessed(notification.id());
        } catch (Exception e) {
            throw new RetryableException("Push failed, will retry", e);
        }
    }
}
```

- **What Meta evaluates**: Real-time system design, fan-out patterns, deduplication, and channel-specific delivery strategies.
- **Follow-ups**: How would you handle notification preferences (opt-in/out)? How would you implement scheduled notifications? How would you A/B test notification copy?

### Topic: Data Modeling & Storage

#### Problem: Design a Social Graph Storage System
- **LC/System Design ref**: System Design: Social Graph
- **Problem statement**: Design a storage system for a social graph with 2B users, supporting friend relationships, follow relationships, and feed generation with low latency.
- **Interview walkthrough**: Discuss graph database vs relational vs NoSQL. For social graphs, consider using a graph DB (Neo4j) or a specialized social graph store. For Meta's scale, a custom solution like TAO (The Associations and Objects) is used. Discuss adjacency lists in MySQL with vertical partitioning. For feed generation, use a fan-out-on-write approach for active users and fan-out-on-read for inactive users.
- **Solution**: Use a relational database with an adjacency list for relationships. Table: `relationships(follower_id, followee_id, type, created_at)` with composite index on (followee_id, created_at). For feed, use a hybrid approach: pre-compute feeds for active users (fan-out-on-write) and generate on-demand for inactive users. Use Redis sorted sets for feed storage with post timestamp as score.
- **Java/Spring code**:
```java
@Entity
@Table(name = "relationships", indexes = {
    @Index(name = "idx_followee", columnList = "followee_id"),
    @Index(name = "idx_follower", columnList = "follower_id")
})
public class Relationship {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private UUID followerId;
    private UUID followeeId;
    private String type; // FOLLOW, FRIEND
    private Instant createdAt;
}

@Service
public class FeedGenerator {
    private final RelationshipRepository relationshipRepository;
    private final StringRedisTemplate redis;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNewPost(PostCreatedEvent event) {
        List<UUID> followers = relationshipRepository.findFollowerIds(event.getAuthorId());
        for (UUID followerId : followers) {
            String key = "feed:" + followerId;
            redis.opsForZSet().add(key, event.getPostId().toString(), event.getCreatedAt().toEpochMilli());
            redis.expire(key, 7, TimeUnit.DAYS);
        }
    }
}
```

- **What Meta evaluates**: Graph data modeling, fan-out strategies, and trade-offs between read-time and write-time work.
- **Follow-ups**: How would you handle a celebrity user with 100M followers? How would you implement friend suggestions? How would you detect and remove fake accounts?

#### Problem: Design a Time-Series Metrics Storage
- **LC/System Design ref**: System Design: Time-Series Database
- **Problem statement**: Design a storage system for application metrics (CPU, memory, request latency) collected every 10 seconds from 100K servers, with support for rollups and ad-hoc queries.
- **Interview walkthrough**: Discuss time-series database characteristics: append-heavy, range queries, downsampling. Compare InfluxDB, TimescaleDB, and Prometheus. For custom design, use a columnar storage format with time-based partitioning. Discuss retention policies: raw data for 7 days, 1-minute rollups for 30 days, 1-hour rollups for 1 year. Use a LSM-tree for write throughput.
- **Solution**: Use a custom time-series store built on RocksDB. Each metric has a separate column family. Data is written in batches and flushed to SST files. For rollups, use a scheduled MapReduce job. For querying, support range scans with downsampling. Use Prometheus for monitoring and Grafana for dashboards.
- **Java/Spring code**:
```java
@Component
public class MetricsIngestionService {
    private final MetricsRepository metricsRepository;

    @KafkaListener(topics = "metrics", concurrency = "8")
    public void ingestMetrics(List<MetricPoint> batch) {
        Map<String, List<MetricPoint>> grouped = batch.stream()
            .collect(Collectors.groupingBy(MetricPoint::metricName));
        for (var entry : grouped.entrySet()) {
            String metricName = entry.getKey();
            List<MetricPoint> points = entry.getValue();
            metricsRepository.batchInsert(metricName, points);
        }
    }
}

@Repository
public class MetricsRepository {
    @Transactional
    public void batchInsert(String metricName, List<MetricPoint> points) {
        String sql = "INSERT INTO metrics (metric_name, timestamp, value, tags) VALUES (?, ?, ?, ?::jsonb)";
        jdbcTemplate.batchUpdate(sql, points, points.size(), (ps, point) -> {
            ps.setString(1, metricName);
            ps.setLong(2, point.timestamp().toEpochMilli());
            ps.setDouble(3, point.value());
            ps.setString(4, point.tagsAsJson());
        });
    }
}
```

- **What Meta evaluates**: Time-series data modeling, batch processing, storage efficiency, and query performance.
- **Follow-ups**: How would you handle downsampling? How would you detect anomalies? How would you store high-cardinality metrics?

### Topic: API Design & GraphQL

#### Problem: Design a GraphQL API for a Social Media Platform
- **LC/System Design ref**: System Design: GraphQL API
- **Problem statement**: Design a GraphQL API for a social media platform that supports posts, comments, likes, user profiles, and news feed. Handle N+1 queries, data loader patterns, and real-time subscriptions.
- **Interview walkthrough**: Discuss GraphQL vs REST trade-offs. For Meta, GraphQL is the standard. Talk about schema design: types (User, Post, Comment, Like), queries (feed, post, user), mutations (createPost, likePost), subscriptions (onNewComment). Discuss DataLoader for batching and caching. Discuss resolver optimization — avoid N+1 by batching database queries. Talk about pagination with Relay-style connections.
- **Solution**: Use Spring for GraphQL (graphql-spring-boot-starter). Define schema with types and resolvers. Use DataLoader for batching: for each post in a feed, batch-load the author and comments. Use @BatchMapping for efficient loading. For subscriptions, use WebSocket transport with a ReactivePublisher.
- **Java/Spring code**:
```java
@Controller
public class PostController {
    @QueryMapping
    public CompletableFuture<Connection<Post>> feed(
            @Argument int first, @Argument String after) {
        return postService.getFeed(first, after);
    }

    @BatchMapping
    public CompletableFuture<Map<Post, User>> author(List<Post> posts) {
        List<UUID> authorIds = posts.stream().map(Post::getAuthorId).distinct().toList();
        return userService.findByIds(authorIds)
            .thenApply(users -> posts.stream()
                .collect(Collectors.toMap(p -> p, p -> users.get(p.getAuthorId()))));
    }

    @SubscriptionMapping
    public Publisher<Comment> commentAdded(@Argument String postId) {
        return commentService.getCommentPublisher(postId);
    }
}
```

- **What Meta evaluates**: GraphQL schema design, resolver optimization, subscription handling, and understanding of Meta's GraphQL-first approach.
- **Follow-ups**: How would you implement pagination in GraphQL? How would you handle file uploads? How would you design a GraphQL gateway for multiple microservices?

### Topic: Caching & Performance

#### Problem: Design a CDN Cache Invalidation Strategy
- **LC/System Design ref**: System Design: CDN
- **Problem statement**: Design a cache invalidation strategy for a CDN serving static assets (images, CSS, JS) and API responses. When content is updated, the cache must be invalidated within 60 seconds across all edge locations.
- **Interview walkthrough**: Discuss cache invalidation methods: TTL-based (simple but stale), purge/ban (immediate but expensive), versioned URLs (cache-busting via content hash). For CDN, use surrogate keys (cache tags) to group related resources. On content update, send a purge request for the specific surrogate key. Discuss the trade-off between purge propagation delay and cache hit ratio.
- **Solution**: Use content-addressable URLs with a content hash in the filename (e.g., `style.a1b2c3.css`). This enables infinite TTL and instant invalidation by changing the URL. For API responses, use ETags and conditional requests. For CDN cache invalidation, use surrogate keys (e.g., "user:123", "post:456") and purge by key when content changes. Implement a purge queue with a worker that calls the CDN API.
- **Java/Spring code**:
```java
@Component
public class CacheInvalidationService {
    private final RestTemplate cdnClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void invalidateOnUpdate(ContentUpdatedEvent event) {
        String surrogateKey = event.entityType() + ":" + event.entityId();
        cdnClient.post("https://api.cdn.com/purge", Map.of("key", surrogateKey));
        redis.delete("cache:" + surrogateKey);
    }
}
```

- **What Meta evaluates**: Cache invalidation strategies, CDN architecture, and understanding of eventual consistency.
- **Follow-ups**: How would you handle cache stampede? How would you implement write-through vs write-behind caching?

### Topic: Database Scaling

#### Problem: Design a Database Sharding Strategy for a Social Platform
- **LC/System Design ref**: System Design: Database Sharding
- **Problem statement**: Design a sharding strategy for a social platform's user database with 1B users. Support user lookup by ID, email, and username. Handle cross-shard queries for friend relationships.
- **Interview walkthrough**: Discuss sharding key selection — shard by user_id for even distribution. For secondary indexes (email, username), maintain a separate lookup table or use Elasticsearch. Discuss cross-shard queries: for friend feeds, fan out queries to all relevant shards. Discuss rebalancing: consistent hashing with virtual nodes, or range-based sharding with a lookup service. Talk about connection pooling per shard.
- **Solution**: Shard by user_id using consistent hashing with 1024 virtual nodes. Maintain a secondary index table (email -> user_id, username -> user_id) in a separate database or use Elasticsearch. For cross-shard queries (e.g., friend feed), use a scatter-gather pattern: query all shards in parallel and merge results. Use a connection pool per shard with HikariCP. For rebalancing, use a two-phase approach: copy data to new shards, switch traffic, drop old shards.
- **Java/Spring code**:
```java
@Component
public class ShardManager {
    private final List<DataSource> shards;
    private final ConsistentHashRouter router;

    public DataSource getShard(String key) {
        return shards.get(router.getShardIndex(key));
    }
}

@Repository
public class UserRepository {
    @Autowired
    private ShardManager shardManager;

    public User findById(UUID userId) {
        DataSource shard = shardManager.getShard(userId.toString());
        return jdbcTemplate(shard).queryForObject(
            "SELECT * FROM users WHERE id = ?", userRowMapper, userId);
    }
}
```

- **What Meta evaluates**: Sharding strategies, cross-shard query handling, rebalancing, and operational complexity.
- **Follow-ups**: How would you handle shard rebalancing with zero downtime? How would you implement global secondary indexes? How would you handle hot shards?

### Topic: System Design

#### Design Facebook News Feed
- **Requirements**: Serve personalized news feed to 2B users with <200ms latency. Support photos, videos, links, and status updates.
- **Framework**: Discuss feed generation strategies: fan-out-on-write (push) vs fan-out-on-read (pull). Meta uses a hybrid approach: push for active users, pull for inactive users. For feed storage, use Redis sorted sets with post IDs and timestamps. For media, use CDN with pre-signed URLs. Discuss ranking: use a machine learning model to score posts based on affinity, recency, and content type. For ads insertion, interleave sponsored content organically.
- **Key trade-offs**: Storage cost vs feed freshness. Fan-out write amplification for celebrities. Ranking complexity vs latency.
- **Meta-specific**: Discuss TAO (graph store), Haystack (photo storage), and Unicorn (social graph indexing). Mention the use of Presto for analytics.

### Design Facebook Messenger (Real-Time Chat)
- **Requirements**: Support 1B+ users, <100ms message delivery, offline message storage, read receipts, typing indicators, group chats.
- **Framework**: Discuss the architecture: client connects to a chat service via WebSocket with persistent connection. Messages are written to a distributed log (Kafka) for durability and then fanned out to recipients. For online users, push via WebSocket. For offline users, store in a mailbox (Cassandra) and deliver on reconnect. For group chats, fan-out to individual mailboxes or use a shared log. Discuss end-to-end encryption considerations.
- **Key trade-offs**: Delivery guarantees (at-least-once vs exactly-once). Online vs offline storage. Ordering guarantees across shards.
- **Meta-specific**: Discuss the use of TAO for social graph, Haystack for photo storage, and the custom WebSocket infrastructure. Mention the use of Apache Thrift for service communication.

## Behavioral Questions

### Tell me about a time you had to make a decision with incomplete information
- **Situation**: We needed to choose a message queue for a new real-time analytics pipeline. We had 2 weeks to decide, and the requirements were still evolving.
- **Task**: Make a decision that wouldn't block the team while keeping options open.
- **Action**: Researched Kafka, RabbitMQ, and Amazon SQS. Built a simple prototype with each, testing throughput, latency, and operational complexity. Chose Kafka for its durability, replay capability, and ecosystem (Kafka Streams, Connect). However, designed an abstraction layer (Spring Cloud Stream) so the messaging backend could be swapped if needed. Documented the decision with trade-offs.
- **Result**: The pipeline handled 500K events/second from day one. The abstraction layer allowed us to later add a dead-letter queue and replay capability without code changes.
- **What Meta evaluates**: Move Fast, data-driven decision making, and technical judgment.

### Tell me about a time you had a conflicting opinion with a teammate
- **Situation**: A teammate wanted to use a micro-frontend architecture for our internal admin dashboard. I believed it would add unnecessary complexity for a team of 3 developers.
- **Task**: Resolve the disagreement constructively without damaging the relationship.
- **Action**: Proposed a time-boxed spike: 2 days to build a prototype with micro-frontends and 2 days with a modular monolith. We evaluated both on developer velocity, build time, and testability. The modular monolith was faster to develop, easier to test, and had simpler deployment. We agreed to start with the monolith and extract micro-frontends later if needed.
- **Result**: The admin dashboard shipped 2 weeks early. The modular architecture allowed us to extract two micro-frontends later when the team grew to 8 people.
- **What Meta evaluates**: Move Fast, technical judgment, collaboration, and ability to disagree constructively.

### Tell me about a project that had a significant impact
- **Situation**: Our notification delivery rate was 85% — 15% of users never received push notifications due to device token expiration, app uninstalls, and rate limiting by FCM/APNs.
- **Task**: Improve delivery rate to 98%+ while reducing notification infrastructure costs.
- **Action**: Implemented a feedback loop: tracked delivery status per notification, built a token health checker that periodically validates device tokens and removes invalid ones, implemented smart retry with exponential backoff for transient failures, and added channel fallback (if push fails, send email). Used a Bloom filter to deduplicate notifications across channels.
- **Result**: Delivery rate improved from 85% to 97.5%. Infrastructure costs reduced by 20% by removing dead tokens. User engagement increased by 12%.
- **What Meta evaluates**: Focus on Impact, data-driven optimization, and cross-system thinking.

## Study Plan

Priority labs for Meta backend interviews:
1. **REST APIs / API Design** — Foundation for all Meta backend roles
2. **WebFlux / Reactive** — Real-time data processing
3. **Messaging** — Kafka, Pub/Sub for async processing
4. **Caching** — Redis, multi-layer caching
5. **Security** — Authentication, authorization, data privacy
6. **GraphQL DGS** — Meta uses GraphQL extensively
7. **SSE / WebSockets** — Real-time communication
8. **Performance** — Profiling, optimization at scale
9. **Spring Boot** — Core framework knowledge
10. **Testing** — Integration and performance testing

## Tips

- Meta interviews focus on real-world engineering. Expect problems that mirror what Meta engineers work on daily: news feed, messaging, comments, notifications.
- For coding rounds, write clean, readable code. Meta values code quality over clever one-liners.
- In system design, emphasize the data model first. Meta interviewers want to see you think deeply about schema design before jumping to architecture.
- Know Meta's tech stack: TAO (graph), Haystack (photos), Presto (analytics), Thrift (RPC), and React (frontend). Mentioning these shows depth.
- For behavioral questions, use the STAR method and quantify impact. Meta loves metrics: "improved latency by 40%," "reduced costs by 30%."
- Be prepared to discuss privacy and security — Meta faces intense scrutiny on data handling. Show awareness of data protection principles.
- Practice designing for "viral" scenarios — what happens when a feature goes viral and traffic spikes 100x?
- Meta's system design interviews often start with "design a feature we already built" — know their products well.

### Topic: Storage & Databases

#### Problem: Design a Photo Storage System (Haystack-like)
- **LC/System Design ref**: System Design: Photo Storage
- **Problem statement**: Design a photo storage system that stores billions of photos, serves them with low latency, and supports upload, thumbnail generation, and sharing.
- **Interview walkthrough**: Discuss the architecture: upload service receives photos, generates thumbnails (multiple sizes), stores originals in blob storage (S3), and metadata in a database. For serving, use a CDN with cache headers. For thumbnails, use an async worker that resizes images. For deduplication, use content hashing. Discuss Meta's Haystack paper: a custom photo storage system optimized for small files with high throughput.
- **Solution**: Use a microservice architecture: Upload Service, Thumbnail Service, Metadata Service, CDN. Uploads go to a blob store (S3) with a unique content hash. A message queue triggers thumbnail generation. Metadata (user, album, timestamp, hash) is stored in a distributed database (Cassandra). For serving, use a CDN with cache-control headers. For deduplication, check content hash before storing.
- **Java/Spring code**:
```java
@Service
public class PhotoUploadService {
    private final BlobStorageClient blobStorage;
    private final PhotoMetadataRepository metadataRepository;
    private final KafkaTemplate<String, PhotoEvent> kafka;

    @Transactional
    public PhotoUploadResult upload(MultipartFile file, UUID userId) {
        String contentHash = hashFile(file);
        Optional<PhotoMetadata> existing = metadataRepository.findByContentHash(contentHash);
        if (existing.isPresent()) {
            return new PhotoUploadResult(existing.get().getId(), false);
        }
        String blobKey = "photos/" + contentHash.substring(0, 2) + "/" + contentHash;
        blobStorage.upload(blobKey, file);
        PhotoMetadata metadata = new PhotoMetadata(userId, contentHash, blobKey, file.getSize());
        metadata = metadataRepository.save(metadata);
        kafka.send("photo-events", new PhotoUploadedEvent(metadata.getId(), contentHash));
        return new PhotoUploadResult(metadata.getId(), true);
    }
}
```

- **What Meta evaluates**: Storage architecture, deduplication, async processing, and CDN integration.
- **Follow-ups**: How would you handle EXIF data extraction? How would you implement face detection for photo tagging? How would you handle photo deletion with CDN cache invalidation?

### Topic: System Design — Additional Questions

#### Design Facebook's Real-Time Search
- **Requirements**: Search posts, people, pages, and groups in real-time with <200ms latency. Support 2B+ users and 1PB+ of searchable content.
- **Framework**: Discuss the search architecture: indexing pipeline (documents -> NLP preprocessing -> inverted index -> distributed search cluster), serving layer (query parsing -> search shards -> ranking -> aggregation). For real-time indexing, use a streaming pipeline (Kafka -> Flink -> Elasticsearch). For ranking, use a machine learning model that considers relevance, recency, and user affinity. For personalization, boost results from friends and followed pages.
- **Key trade-offs**: Index freshness vs indexing cost. Search precision vs recall. Personalization vs result diversity.
- **Meta-specific**: Discuss the use of Elasticsearch or Solr for search, Presto for analytics, and the custom ranking models. Mention the Unicorn graph indexing system for social search.

#### Design Facebook's Live Video Streaming Backend
- **Requirements**: Support 1M+ concurrent live streams, <1s latency, adaptive bitrate, real-time comments and reactions.
- **Framework**: Discuss the live streaming pipeline: ingest (RTMP from broadcaster), transcoding (adaptive bitrate), packaging (HLS/DASH), delivery (CDN). For real-time comments, use WebSocket with a fan-out pattern. For latency optimization, use chunked transfer encoding and HTTP/2 server push. For reliability, use a multi-CDN strategy with failover.
- **Key trade-offs**: Latency vs buffering. Video quality vs bandwidth. Real-time comments vs moderation delay.
- **Meta-specific**: Discuss the use of TAO for social graph, Haystack for photo storage, and the custom WebSocket infrastructure. Mention the use of Apache Thrift for service communication.

## Behavioral Questions — Additional

#### Tell me about a time you had to make a trade-off between speed and quality
- **Situation**: We needed to launch a new feature for a major holiday. The ideal solution required 8 weeks of development, but we only had 4 weeks.
- **Task**: Deliver a working feature on time without compromising user experience.
- **Action**: Identified the core 80% use case and built that first. Used feature flags to hide incomplete features. Deployed to 1% of users for beta testing. Collected feedback and iterated. The remaining 20% was delivered in the next sprint.
- **Result**: Launched on time with positive user feedback. The beta testing caught 3 critical bugs before full rollout. The iterative approach became the team's standard for time-sensitive features.
- **What Meta evaluates**: Move Fast, pragmatic prioritization, and user focus.

#### Tell me about a time you had to deal with a production incident
- **Situation**: A database migration caused a 15-minute read-only outage on our primary user database during peak hours.
- **Task**: Restore service and prevent recurrence.
- **Action**: Immediately rolled back the migration to restore write capability. Analyzed the root cause: the migration was adding a column with a default value, which locked the table. Implemented a zero-downtime migration strategy using pt-online-schema-change. Added a pre-migration checklist and a canary deployment process for database changes.
- **Result**: Service was restored in 15 minutes. The new migration process was used for 20+ subsequent migrations with zero downtime. The incident was documented and shared in the engineering all-hands.
- **What Meta evaluates**: Move Fast, learning from incidents, and operational excellence.

## Study Plan — Expanded

Priority labs for Meta backend interviews:
1. **REST APIs / API Design** — Foundation for all Meta backend roles
2. **WebFlux / Reactive** — Real-time data processing
3. **Messaging** — Kafka, Pub/Sub for async processing
4. **Caching** — Redis, multi-layer caching
5. **Security** — Authentication, authorization, data privacy
6. **GraphQL DGS** — Meta uses GraphQL extensively
7. **SSE / WebSockets** — Real-time communication
8. **Performance** — Profiling, optimization at scale
9. **Spring Boot** — Core framework knowledge
10. **Testing** — Integration and performance testing
11. **API Versioning** — Backward compatibility
12. **Spring Cloud** — Service discovery, configuration

## Tips — Additional

- Meta's "Move Fast" culture means you should prioritize speed in your designs, but not at the expense of reliability. Show that you can make pragmatic trade-offs.
- For system design, always discuss how you would test the system at scale. Meta runs extensive A/B tests and canary deployments.
- Know the difference between TAO (graph store for social data) and Unicorn (social graph indexing). TAO is for real-time lookups, Unicorn is for search.
- Meta uses Hack (PHP dialect) for much of its backend, but also uses Java, Python, and Erlang. Show language flexibility.
- For behavioral questions, emphasize impact. Meta wants to see that you've made a measurable difference in your previous roles.
- Be prepared to discuss how you would handle content moderation at scale — Meta faces this challenge daily.
- Practice designing for "viral" scenarios — what happens when a feature goes viral and traffic spikes 100x?
- Meta's system design interviews often start with "design a feature we already built" — know their products well.

### Topic: Concurrency & Parallelism

#### Problem: Design a Thread-Safe Event Bus
- **LC/System Design ref**: System Design: Event Bus
- **Problem statement**: Design a thread-safe in-memory event bus that supports publish-subscribe with multiple event types, asynchronous delivery, and subscriber backpressure.
- **Interview walkthrough**: Discuss the producer-consumer pattern. Use a ConcurrentHashMap of event type to list of subscribers. For async delivery, use a thread pool executor. For backpressure, use a bounded queue with a blocking put or a reactive stream. Discuss ordering guarantees: events from the same producer should be processed in order. Discuss dead letter handling for failed event processing.
- **Solution**: Use a ConcurrentHashMap<String, CopyOnWriteArrayList<Subscriber>> for subscriber registry. For async delivery, use a ThreadPoolExecutor with a bounded queue. For ordering, use a per-partition queue (by event key). For backpressure, use a blocking queue with a configurable capacity. For dead letters, route failed events to a separate topic with TTL.
- **Java/Spring code**:
```java
@Component
public class InMemoryEventBus {
    private final ConcurrentHashMap<String, List<Subscriber>> subscribers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(16);

    public void publish(String topic, Object event) {
        List<Subscriber> subs = subscribers.get(topic);
        if (subs == null) return;
        for (Subscriber sub : subs) {
            executor.submit(() -> {
                try {
                    sub.onEvent(event);
                } catch (Exception e) {
                    log.error("Subscriber {} failed on event {}", sub.name(), event, e);
                }
            });
        }
    }
}
```

- **What Amazon evaluates**: Event-driven architecture, async processing, and reliability patterns.
- **Follow-ups**: How would you implement exactly-once delivery? How would you handle event ordering? How would you implement dead-letter queues?

## System Design Questions — Additional

### Design Amazon's Warehouse Management System
- **Requirements**: Manage inventory across 100+ fulfillment centers, optimize pick-pack-ship workflows, support 1M+ orders/day.
- **Framework**: Discuss the WMS architecture: inventory service, order assignment service, pick-pack-ship workflow engine. For order assignment, use a constraint solver to minimize shipping cost and time. For inventory, use real-time tracking with RFID/scanner events. For workflow, use a state machine with events (PICKED, PACKED, SHIPPED). Discuss the integration with robotics systems (Kiva/Amazon Robotics).
- **Key trade-offs**: Centralized vs decentralized inventory. Batch picking vs single-order picking. Human vs robotic picking efficiency.
- **Amazon-specific**: Discuss Amazon's use of Kiva robots, the AR/VR systems for warehouse workers, and the algorithms for optimizing pick paths.

## Behavioral Questions — Additional

#### Tell me about a time you had to make a quick decision
- **Situation**: During a Black Friday sale, our payment processing latency spiked to 10 seconds. The team was split on whether to scale up instances or optimize the code.
- **Task**: Make a quick decision to restore service while minimizing cost.
- **Action**: Analyzed the bottleneck: database CPU was at 95%. Scaled up the database instance (vertical scaling) as an immediate fix, which restored latency to 200ms within 5 minutes. Then, implemented read replicas and query optimization as a permanent solution. Documented the incident and created a runbook for future scaling events.
- **Result**: Service was restored in 5 minutes. The permanent optimizations reduced database CPU by 40%. The runbook was used by the on-call team during the next Prime Day.
- **What Amazon evaluates**: Bias for Action, ability to make quick decisions under pressure, and learning from incidents.

## Study Plan — Expanded

Priority labs for Amazon backend interviews:
1. **Spring Boot** — Foundation for all backend work
2. **REST APIs** — API design is critical at Amazon
3. **Security** — Authentication, authorization, PCI compliance
4. **Messaging** — SQS/Kafka patterns for async processing
5. **Spring Cloud** — Service discovery, configuration, gateway
6. **Transactions** — ACID, distributed transactions, sagas
7. **Caching** — Multi-layer caching, Redis, CDN
8. **Performance** — Profiling, connection pooling, JVM tuning
9. **API Versioning** — Backward compatibility, deprecation strategies
10. **CQRS/Axon** — Event sourcing for audit trails
11. **Testing** — Integration, contract, and performance testing
12. **Security Deep** — Cryptography, PCI compliance, IAM
13. **Batch Processing** — Large-scale data processing
14. **Spring Boot Internals** — Deep framework understanding

## Tips — Additional

- Amazon's "Working Backwards" culture means you should start every system design with the customer need. Frame your designs around customer impact.
- Know the difference between DynamoDB's adaptive capacity, on-demand vs provisioned mode, and when to use each.
- For system design, always discuss monitoring, alarming, and dashboards. Amazon uses CloudWatch extensively.
- The "Bar Raiser" round is a separate evaluation — the interviewer does not have a hiring stake and evaluates you against the overall bar. Be prepared for a wildcard question.
- Understand Amazon's build vs buy philosophy. Be ready to discuss when to use a managed service vs building in-house.
- For system design, always discuss monitoring, alarming, and dashboards. Amazon uses CloudWatch extensively.
- Know the difference between DynamoDB's adaptive capacity, on-demand vs provisioned mode, and when to use each.
