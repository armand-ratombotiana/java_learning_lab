# 🎬 NETFLIX INTERVIEW PACK
## Distributed Systems + Real-Time Streaming at Scale

**Difficulty**: Very Hard  
**Duration**: 10-12 weeks of preparation  
**Total Problems**: 20+  
**Expected Success Rate**: 25-30%  

---

## 📊 Netflix Interview Overview

### Company Profile
- **Mission**: "Entertain the world" - 260M+ subscribers, 190+ countries
- **Tech Challenge**: Stream 1.3M requests/second with 99.99% uptime
- **Scale**: 800M movie views/day, petabytes of data, millisecond response times
- **Tech Stack**: Java, Scala, Python, Go, Node.js, Cassandra, Kafka, Spring
- **Hiring Focus**: Distributed systems mastery, fault tolerance, performance optimization

### What Netflix Values
1. **Distributed Systems Expertise** - "Can you design for failure?"
2. **Real-Time Streaming** - Latency matters (every 100ms is money)
3. **Fault Tolerance** - "How does your system survive datacenter failure?"
4. **Performance Obsession** - "Can we optimize this 10%?"
5. **Data-Driven Decisions** - Metrics, observability, A/B testing

### Interview Structure
```
Round 1: System Design (90 min)
├─ Design Netflix streaming architecture
├─ Discuss failure scenarios
├─ Network bottlenecks
└─ Real-world constraints

Round 2: Coding + System (75 min)
├─ Hard algorithm problem
├─ Distributed aspect
├─ Trade-offs and scaling

Round 3: Distributed Systems Deep Dive (60 min)
├─ Consensus algorithms
├─ Replication strategies
├─ Failure detection
└─ Recovery mechanisms

Round 4: Infrastructure & Ops (60 min)
├─ Monitoring and alerting
├─ Incident response
├─ Chaos engineering
└─ Cost optimization
```

---

## 🎯 Recommended Study Path

### Week 1-2: Concurrency Fundamentals
1. **Module 05: Concurrency** (critical for Netflix!)
2. **Thread pools and scheduling**
3. **Lock-free algorithms**
4. **Atomic operations and CAS**

### Week 3-4: Algorithms & Data Structures
1. **Module 03-04**: Collections, Streams
2. **Graph algorithms**: Dijkstra, BFS, DFS
3. **Consistent hashing**
4. **Bloom filters and sketch algorithms**

### Week 5-6: Distributed Systems Theory
1. **CAP theorem and PACELC**
2. **Consensus algorithms**: Paxos, Raft, Zookeeper
3. **Event sourcing and CQRS**
4. **Eventual consistency patterns**

### Week 7-8: Real-Time Streaming
1. **Kafka architecture** (producer/consumer/replication)
2. **Stream processing**: Spark, Flink
3. **Message deduplication**
4. **Exactly-once semantics**

### Week 9-10: Fault Tolerance
1. **Circuit breaker pattern**
2. **Bulkhead isolation**
3. **Request timeout and retry logic**
4. **Graceful degradation**

### Week 11-12: Netflix Specifics
1. **Hystrix and Resilience4j**
2. **Eureka service discovery**
3. **Zuul API gateway**
4. **Mock interviews (5-6 total)**

---

## 💎 20+ Interview Problems

### LEVEL 1: Warm-up (3 problems)

#### Problem 1: Designing a Distributed Rate Limiter
**Time**: 45 minutes  
**Difficulty**: Hard  
**Netflix Context**: Protect services from traffic spikes

```java
// Challenge: Rate limit across multiple servers
// - 1M requests/second globally
// - Different rate limits per user
// - Handle distributed clock skew

class DistributedRateLimiter {
    // Approach 1: Token Bucket with Redis
    
    private RedisClient redis;
    private String keyPrefix = "rate_limit:";
    private long tokensPerSecond = 100;
    private long maxTokens = 100;
    
    public boolean allowRequest(String userId) {
        String key = keyPrefix + userId;
        long now = System.currentTimeMillis();
        
        // Get current token count and last refill time
        TokenBucket bucket = redis.get(key, TokenBucket.class);
        
        if (bucket == null) {
            bucket = new TokenBucket(maxTokens, now);
        }
        
        // Add tokens based on time elapsed
        long timePassed = now - bucket.lastRefill;
        long tokensToAdd = (timePassed / 1000) * tokensPerSecond;
        bucket.tokens = Math.min(bucket.tokens + tokensToAdd, maxTokens);
        bucket.lastRefill = now;
        
        if (bucket.tokens >= 1) {
            bucket.tokens--;
            redis.set(key, bucket);
            return true;
        }
        
        return false;
    }
    
    // Challenge: What if Redis goes down?
    // Answer: Use local token bucket with sync
    
    // Challenge: User in US vs India (clock skew)?
    // Answer: Use server timestamp, not client
}

// Approach 2: Sliding Window with Redis
class SlidingWindowRateLimiter {
    public boolean allowRequest(String userId, int limit) {
        String key = "requests:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - 1000;  // 1 second window
        
        // Remove old entries
        redis.zremRangeByScore(key, 0, windowStart);
        
        // Count current window
        long count = redis.zcard(key);
        
        if (count < limit) {
            redis.zadd(key, now, UUID.randomUUID().toString());
            redis.expire(key, 2);  // Expire after 2 seconds
            return true;
        }
        
        return false;
    }
    
    // Pros: Accurate request counting
    // Cons: More memory, higher latency
}

// Netflix Asks:
// - Cost? O(1) per request with token bucket
// - Accuracy? 99.99% with token bucket
// - Failure recovery? Watch for sync delays
```

---

#### Problem 2: Event Deduplication in Distributed System
**Time**: 35 minutes  
**Difficulty**: Hard  
**Netflix Context**: Exactly-once processing

```java
class EventDeduplicator {
    // Challenge: Multiple Kafka partitions, possibly duplicates
    // Solution: Idempotency keys + local cache
    
    private Map<String, Event> deduplicationCache;
    private RedisClient redis;
    
    public void processEvent(Event event) {
        String idempotencyKey = event.getIdempotencyKey();
        
        // Check local cache first (fast)
        if (deduplicationCache.containsKey(idempotencyKey)) {
            return;  // Already processed
        }
        
        // Check Redis (distributed cache)
        if (redis.exists(idempotencyKey)) {
            deduplicationCache.put(idempotencyKey, event);
            return;
        }
        
        // Process event
        try {
            handleEvent(event);
            
            // Mark as processed
            redis.setex(idempotencyKey, 86400, event.getId()); // 24 hours TTL
            deduplicationCache.put(idempotencyKey, event);
            
        } catch (Exception e) {
            // If processing fails, don't mark as processed
            throw e;
        }
    }
    
    // Netflix Considerations:
    // - TTL should match retention policy
    // - Handle Redis failures gracefully
    // - Monitor duplicate rate
}
```

---

#### Problem 3: Consistent Hashing for Load Distribution
**Time**: 40 minutes  
**Difficulty**: Hard  

```java
class ConsistentHash {
    private SortedMap<Long, String> ring = new TreeMap<>();
    private int replicas = 3;
    
    public void addNode(String nodeId) {
        for (int i = 0; i < replicas; i++) {
            long hash = md5(nodeId + ":" + i);
            ring.put(hash, nodeId);
        }
    }
    
    public void removeNode(String nodeId) {
        for (int i = 0; i < replicas; i++) {
            long hash = md5(nodeId + ":" + i);
            ring.remove(hash);
        }
    }
    
    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        
        long hash = md5(key);
        SortedMap<Long, String> tailMap = ring.tailMap(hash);
        
        if (tailMap.isEmpty()) {
            return ring.get(ring.firstKey());
        }
        
        return tailMap.get(tailMap.firstKey());
    }
    
    private long md5(String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        return ByteBuffer.wrap(digest).getLong();
    }
    
    // Netflix Uses: Consistent hashing for:
    // - Cache partitioning
    // - Video encoding task distribution
    // - Recommendation engine sharding
}
```

---

### LEVEL 2: Core Distributed Problems (8 problems)

#### Problem 4: Design Kafka-like Message Queue
**Time**: 60 minutes  
**Difficulty**: Very Hard  

```
Architecture:
Topics (log partitions)
├─ Partition 0 (replicas on 3 nodes)
├─ Partition 1
└─ Partition N

Key Components:
1. Append-only log (fast writes)
2. Partition leadership (one writes)
3. Follower replication (durability)
4. Consumer groups (parallel processing)
5. Offset management (recovery)

Java Implementation Sketch:
class Partition {
    private List<Message> log;
    private volatile long commitOffset;
    private Map<Integer, Long> replicaOffsets;
    
    public void append(Message msg) {
        synchronized(this) {
            log.add(msg);
        }
    }
    
    public void commitTo(long offset) {
        this.commitOffset = offset;
        // Notify consumers waiting for this offset
    }
    
    public List<Message> retrieve(long from, int maxSize) {
        return log.subList((int)from, (int)Math.min(from + maxSize, log.size()));
    }
}

class ConsumerGroup {
    private String groupId;
    private Map<Topic, Long> offsets;  // Where we've consumed to
    
    public void seekToOffset(Topic topic, long offset) {
        offsets.put(topic, offset);
    }
    
    public List<Message> poll(Topic topic, int maxRecords) {
        long currentOffset = offsets.getOrDefault(topic, 0L);
        List<Message> messages = topic.retrieve(currentOffset, maxRecords);
        offsets.put(topic, currentOffset + messages.size());
        // Persist offset to broker for recovery
        return messages;
    }
}

Netflix Challenges:
- Handling follower failures
- Leader election when data center fails
- Consumer group rebalancing
- Out-of-order message delivery
```

---

#### Problem 5: Distributed Tracing System
**Time**: 50 minutes  
**Difficulty**: Hard  

```java
// Netflix Motivation: Trace requests across 100s of microservices
// Understand where latency comes from

class DistributedTracer {
    private final TraceCollector collector;
    
    public void traceRequest(HttpRequest request) {
        String traceId = request.getHeader("X-Trace-ID");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        
        Span rootSpan = new Span(traceId, "http-request", 0);
        rootSpan.start();
        
        try {
            // Metadata
            rootSpan.addTag("http.method", request.getMethod());
            rootSpan.addTag("http.path", request.getPath());
            rootSpan.addTag("http.client_ip", request.getClientIP());
            
            // Pass trace ID to downstream services
            callDownstreamService(traceId);
            
            rootSpan.finish();
            
        } catch (Exception e) {
            rootSpan.addTag("error", true);
            rootSpan.addTag("error.message", e.getMessage());
            rootSpan.finish();
            throw e;
        }
        
        // Send to collector (async)
        collector.submit(rootSpan);
    }
    
    private void callDownstreamService(String traceId) {
        // Child span for service call
        Span serviceSpan = new Span(traceId, "database-query", System.currentTimeMillis());
        serviceSpan.start();
        
        try {
            // Make service call with traceId in headers
            database.query(sql, new Headers("X-Trace-ID", traceId));
            serviceSpan.finish();
        } catch (Exception e) {
            serviceSpan.addTag("error", true);
            serviceSpan.finish();
            throw e;
        }
    }
    
    // Visualization: Waterfall view
    // GET /user/profile ────────────────────── 250ms
    //   ├─ getUserFromDB ────────────────────  150ms
    //   └─ getRecommendations ──────────────   100ms
    //       ├─ queryCache ────────────────    5ms (hit)
    //       └─ queryML ────────────────────   95ms
}

class Span {
    String traceId;
    String operationName;
    long startTime;
    long endTime;
    Map<String, String> tags = new HashMap<>();
    List<Span> children = new ArrayList<>();
    
    public void finish() {
        this.endTime = System.currentTimeMillis();
    }
    
    public long getDuration() {
        return endTime - startTime;
    }
}

// Netflix Tools: Zipkin, Jaeger, Lightstep
// Benefits:
// - Identify slow services
// - Debug distributed issues
// - Alert on latency SLO violations
```

---

#### Problem 6: Circuit Breaker Pattern
**Time**: 35 minutes  
**Difficulty**: Hard  

```java
class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }
    
    private State state = State.CLOSED;
    private long lastFailureTime;
    private int failureCount = 0;
    private int successCount = 0;
    private final int failureThreshold = 5;
    private final int successThreshold = 2;
    private final long timeout = 60000;  // 60 seconds
    
    public <T> T call(ServiceCall<T> operation) throws Exception {
        switch (state) {
            case CLOSED:
                return handleClosed(operation);
            case OPEN:
                return handleOpen(operation);
            case HALF_OPEN:
                return handleHalfOpen(operation);
        }
        return null;
    }
    
    private <T> T handleClosed(ServiceCall<T> operation) throws Exception {
        try {
            T result = operation.execute();
            failureCount = 0;  // Reset on success
            return result;
        } catch (Exception e) {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            if (failureCount >= failureThreshold) {
                state = State.OPEN;
                System.out.println("Circuit OPENED");
            }
            throw e;
        }
    }
    
    private <T> T handleOpen(ServiceCall<T> operation) throws Exception {
        long timeSinceLastFailure = System.currentTimeMillis() - lastFailureTime;
        if (timeSinceLastFailure > timeout) {
            state = State.HALF_OPEN;
            System.out.println("Circuit HALF-OPEN");
            return operation.execute();
        }
        throw new CircuitBreakerOpenException("Circuit is OPEN");
    }
    
    private <T> T handleHalfOpen(ServiceCall<T> operation) throws Exception {
        try {
            T result = operation.execute();
            successCount++;
            if (successCount >= successThreshold) {
                state = State.CLOSED;
                failureCount = 0;
                successCount = 0;
                System.out.println("Circuit CLOSED");
            }
            return result;
        } catch (Exception e) {
            state = State.OPEN;
            lastFailureTime = System.currentTimeMillis();
            System.out.println("Circuit OPENED again");
            throw e;
        }
    }
}

// Netflix Uses: Hystrix (now Resilience4j)
// Benefits:
// - Prevent cascading failures
// - Fast failure (no waiting for timeout)
// - Graceful degradation
```

---

## 📈 System Design Deep Dives

### Netflix's Video Streaming Pipeline

```
User clicks play
    ↓
DNS resolution (GeoDNS)
    ↓
CDN node selection (Edge servers in user's region)
    ↓
License verification + DRM
    ↓
Playback decision service (bitrate, codec)
    ↓
HLS manifest + video chunks download
    ↓
Client-side buffer management
    ↓ (if stalls)
Fallback to lower bitrate
    ↓
Playback complete or pause → telemetry event → Kafka

Challenges Netflix Solves:
1. Network variability (home WiFi, mobile)
   → Adaptive bitrate (DASH, HLS)
   
2. Regional licensing (different content per country)
   → License database lookup
   
3. Handling 1M concurrent streams
   → Cache + CDN heavily
   
4. Device diversity (smart TVs, phones, browsers)
   → Transcode to multiple formats
```

---

## 🎯 Netflix Interview Tips

### Technical Preparation
- ✅ Understand CAP/PACELC theorems
- ✅ Know consensus algorithms (Raft, Paxos)
- ✅ Study failure detection
- ✅ Know stream processing concepts
- ✅ Be familiar with Kafka architecture

### Soft Skills
- Discuss tradeoffs confidently
- Show you understand real constraints
- Talk about operational thinking
- Discuss incidents you've dealt with
- Ask about their infrastructure

### During Interview
1. **Understand constraints first**: "How many concurrent users? QPS?"
2. **Ask about SLOs**: "What's our latency target? Availability?"
3. **Think about failures**: "What if this fails?"
4. **Optimize gradually**: Simple → complex
5. **Discuss observability**: How do we monitor this?

### If System Design
- Draw clear architecture diagrams
- Explain each component's purpose
- Discuss failure scenarios
- Show you can handle 10x traffic
- Talk about cost optimization

---

## ✅ Pre-Interview Checklist

- [ ] Module 05 (Concurrency) fully mastered
- [ ] Understand distributed system patterns
- [ ] Can explain CAP theorem with examples
- [ ] Familiar with Kafka/event streaming
- [ ] Know consistency/availability trade-offs
- [ ] Can design for failure
- [ ] Done 5-6 system design mocks
- [ ] Understand circuit breaker pattern
- [ ] Know monitoring/alerting concepts
- [ ] Prepared for operational questions

---

## 🚀 Final Tips

> "Netflix doesn't just want scale - they want reliable scale. They need to sleep at night knowing their service won't fail."

### Remember
- ✅ Fault tolerance is feature #1
- ✅ Distributed systems are inherently complex
- ✅ Think about failure modes early
- ✅ Testability and observability matter
- ✅ Cost affects architecture decisions

### If You Get Stuck
1. Ask for clarification
2. Think out loud
3. Draw simple case first
4. Identify likely failure points
5. Propose mitigation

---

**Good luck with Netflix! 🚀 They're building the most impressive systems at scale.**

**Confidence Level**: ⭐⭐⭐ (3/5)  
**Expected Success**: 25-30% (very difficult bar)
