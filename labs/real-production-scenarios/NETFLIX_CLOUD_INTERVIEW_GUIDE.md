# Netflix Cloud Engineering Interview Guide — Real Production Scenarios Academy

## Interview Process for Cloud Engineering Roles

### Rounds
1. **Phone Screen (45 min)**: Systems design + chaos engineering discussion. Expect "what would you do if..." scenarios.
2. **Onsite (4-5 rounds, 45 min each)**:
   - **System Design Round 1**: Design a reliable, resilient system at Netflix scale (video transcoding pipeline, recommendation service)
   - **System Design Round 2**: Chaos engineering — design a chaos experiment, discuss blast radius, detection, mitigation
   - **Coding Round**: Medium algorithm with fault-tolerant code patterns
   - **Cloud/Infrastructure Round**: AWS deep dive, Netflix OSS stack (Eureka, Hystrix, Zuul, Ribbon)
   - **Behavioral/Cultural**: "Freedom and Responsibility" culture, high-performance team norms

### Cloud Engineering-Specific Expectations
- Netflix invented chaos engineering — expect deep discussion of Chaos Monkey, Simian Army, failure injection
- Cloud-native mindset — Netflix runs 100% on AWS (EC2, S3, DynamoDB, Aurora, ElastiCache, CloudFront)
- Microservices architecture at extreme scale — 1B+ streaming hours/month
- "Freedom and Responsibility" — individual engineers own their decisions and their impacts
- Must understand Netflix OSS: Eureka (service discovery), Hystrix (circuit breaker), Zuul (gateway), Ribbon (load balancing), Atlas (monitoring), Spinnaker (CD)
- "Rapid experimentation" culture — everything is tested in production via A/B testing

### Round Breakdown
- System Design (Resilience): 35% — chaos experiment design, fault tolerance
- Cloud Architecture: 25% — AWS deep dive, Netflix OSS
- Coding: 20% — fault-tolerant code
- Behavioral: 20% — Netflix culture

### Netflix OSS Stack Deep Dive

Netflix interviewers expect you to know their open-source stack inside-out:

**Eureka (Service Discovery)**:
- Each service registers with Eureka on startup and sends heartbeats every 30 seconds
- Clients cache the registry locally and refresh every 30 seconds
- Eureka prioritizes availability over consistency (AP from CAP theorem) — it keeps serving stale registry even if all peers are down
- Key metrics: `eureka.registry.cache.size`, `eureka.heartbeat.rate`, `eureka.renewal.threshold`

**Hystrix (Circuit Breaker)**:
- Three states: CLOSED (normal), OPEN (fail fast), HALF_OPEN (test recovery)
- Thresholds: `circuitBreaker.requestVolumeThreshold` (min requests in window), `circuitBreaker.errorThresholdPercentage`, `circuitBreaker.sleepWindowInMilliseconds`
- Two isolation strategies: THREAD (runs in separate thread), SEMAPHORE (runs in caller thread with semaphore guard)
- Key metrics: `hystrix.circuit.breaker.status`, `hystrix.latency.p99`, `hystrix.error.percentage`

**Zuul (API Gateway)**:
- Pre-filters (routing), Route-filters (proxy), Post-filters (response)
- Uses Eureka for dynamic route discovery
- Handles 100k+ requests/second per instance
- Common filter types: rate limiting, authentication, header injection, response transformation

**Ribbon (Client-side Load Balancing)**:
- Load balancing strategies: RoundRobin, WeightedResponseTime, ZoneAvoidance, Random
- Integrates with Eureka for server list
- Supports retry with configurable backoff

**Archaius (Configuration Management)**:
- Dynamic configuration with polling from ZooKeeper
- Properties can be changed at runtime without restart
- Used for: feature flags, rate limits, circuit breaker thresholds, cache TTLs

**Atlas (Monitoring)**:
- Time-series monitoring with dimensional metrics
- Supports counters, gauges, timers, histograms, meters
- Used for: dashboards, alerting, capacity planning, A/B test analysis

**Spinnaker (Continuous Delivery)**:
- Pipeline-based deployment with stages: Bake, Deploy, Canary, Rollback
- Supports: canary deployments, blue-green, rolling red/black
- Automated rollback based on canary analysis

## Top Incidents Aligned to Netflix Cloud Engineering Focus

### Incident: Circuit Breaker Open — Cascading Failure (Lab 07)
#### Problem Scenario
Netflix's recommendation service experiences a 10x latency increase in its content metadata dependency. The Hystrix circuit breaker opens after 50% of requests timeout. Fallback returns cached recommendations, quality degrades, user engagement drops 15%.

#### Interview Walkthrough
**Step 1 — Check Hystrix dashboard**: Shows `MetadataService` command with `CircuitBreaker: OPEN`. `ErrorPercentage` is 75% (threshold 50%).

**Step 2 — Check the fallback**: Returns stale cached data without freshness check. Multiple downstream services each have circuit breakers that open simultaneously — coordinated cache miss storm.

**Step 3 — Root cause**: The metadata service's cache node failed. All traffic goes to the database, which is overwhelmed. Timeout (500ms) is too short for database queries under load.

**Step 4 — Fix**: Implement a dedicated Hystrix command for cache (short timeout) and a separate command for actual call. Add local in-process cache as second fallback.

**What Netflix evaluates**: Hystrix internals; circuit breaker pattern mastery; fallback strategy design; cascading failure prevention.

#### Solution
```java
@HystrixCommand(commandKey = "metadata-cache",
    fallbackMethod = "getLocalCache",
    commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "50"),
        @HystrixProperty(name = "circuitBreaker.forceClosed", value = "true")
    })
public Metadata getMetadata(String contentId) {
    return remoteCache.get(contentId);
}

private Metadata getLocalCache(String contentId) {
    Metadata cached = localCache.getIfPresent(contentId);
    if (cached != null) return cached;
    throw new MetadataUnavailableException(contentId);
}

@HystrixCommand(commandKey = "metadata-backend",
    fallbackMethod = "getStaleMetadata",
    commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
    })
public Metadata getFromBackend(String contentId) {
    return metadataClient.fetch(contentId);
}
```

**Post-mortem**: Add Hystrix metrics to Atlas monitoring. Set up composite alerts across the call graph. Chaos experiment: kill cache node and verify circuit breaker isolation.

### Incident: Kafka Consumer Lag — Real-Time Recommendations (Lab 13)
#### Problem Scenario
Netflix's real-time recommendation pipeline uses Apache Kafka. Consumer lag grows from 100ms to 5 minutes over 30 minutes. The pipeline processes 500k events/second.

#### Interview Walkthrough
**Step 1 — Check consumer lag**: `./kafka-consumer-groups.sh --bootstrap-server kafka.prod.netflix.com:9092 --group recommendation-engine --describe`. LAG = 15M messages.

**Step 2 — Check partition distribution**: Partition 5 has LAG = 12M. Others < 500k. Hot partition problem.

**Step 3 — Analyze partition 5 data**: All events are for "Stranger Things" — new season release causing traffic spike for one show.

**Step 4 — Root cause**: Producer uses `show_id` as Kafka partition key. All "Stranger Things" events go to partition 5. Consumer processes sequentially per partition.

**Step 5 — Fix**: Use composite key (`show_id + user_id`) to distribute load. For recommendations, out-of-order processing is acceptable.

**What Netflix evaluates**: Kafka partitioning strategy; event ordering tradeoffs; handling traffic spikes at Netflix scale.

#### Solution
```java
// Before: single show_id partition key
public void sendEvent(UserEvent event) {
    String key = event.getShowId();
    producer.send(new ProducerRecord<>(TOPIC, key, event));
}

// After: composite key for better distribution
public void sendEvent(UserEvent event) {
    String key = event.getShowId() + ":" + event.getUserId();
    producer.send(new ProducerRecord<>(TOPIC, key, event));
}
```

### Incident: Cache Stampede — Content Catalog (Lab 08)
#### Problem Scenario
Netflix's content catalog cache (ElastiCache Redis) handles 2M requests/second. When a popular title's TTL expires, all concurrent requests miss simultaneously, causing 50x Aurora database load spike.

#### Interview Walkthrough
**Step 1 — Identify pattern**: Atlas shows `cache.hit.ratio` dropping from 99% to 60% every 5 minutes. `aurora.cpu` spikes from 20% to 90%.

**Step 2 — Root cause**: All entries have same TTL with no cache-warming lock. No mutual exclusion during regeneration.

**Step 3 — Fix**: Probabilistic early expiration (XFetch). Use Redis `SET NX` for stampede prevention. TTL jitter.

**What Netflix evaluates**: Caching strategies at Netflix scale; Redis expertise; EVCache understanding.

#### Solution
```java
public Content getContent(String contentId) {
    String key = "content:" + contentId;
    Content content = redisClient.get(key);
    if (content == null) return recomputeAndCache(contentId, key);
    Long ttl = redisClient.ttl(key);
    if (ttl != null && ttl < 60) {
        Double random = Math.random();
        if (random < 0.1) {
            redisClient.del(key);
            return recomputeAndCache(contentId, key);
        }
    }
    return content;
}

private Content recomputeAndCache(String contentId, String key) {
    String lockKey = "lock:" + contentId;
    Boolean locked = redisClient.setnx(lockKey, "1", Duration.ofSeconds(5));
    if (locked) {
        try {
            Content fresh = database.fetchContent(contentId);
            int ttl = 300 + ThreadLocalRandom.current().nextInt(60);
            redisClient.set(key, fresh, Duration.ofSeconds(ttl));
            return fresh;
        } finally {
            redisClient.del(lockKey);
        }
    }
    Thread.sleep(10);
    Content cached = redisClient.get(key);
    if (cached != null) return cached;
    return recomputeAndCache(contentId, key);
}
```

### Incident: Deployment Rollback — Content Encoding Pipeline (Lab 06)
#### Problem Scenario
A deployment introduces a new codec configuration. After deployment to 20% of fleet, encoding failures increase from 0.1% to 8%.

#### Interview Walkthrough
**Step 1 — Detect**: Atlas shows `encoding.error.rate` at 8% on new version vs 0.1% on baseline.

**Step 2 — Check error logs**: "Unsupported pixel format: yuv444p10le" — the new codec uses a format not supported by older encoder hardware.

**Step 3 — Root cause**: Fleet is heterogeneous — some instances have hardware encoders, some don't. No fallback path testing on older hardware.

**Step 4 — Fix**: Rollback via Spinnaker. Add hardware capability detection. Add canary testing across all hardware types.

#### Solution
```java
public Encoder selectEncoder(String pixelFormat) {
    if (isHardwareEncoderAvailable()) {
        HardwareCapabilities caps = detectHardwareCapabilities();
        if (caps.supportsPixelFormat(pixelFormat)) {
            return new HardwareEncoder(caps);
        }
    }
    if (softwareEncoder.supportsFormat(pixelFormat)) {
        return new SoftwareEncoder(pixelFormat);
    }
    throw new UnsupportedFormatException("No encoder for: " + pixelFormat);
}
```

### Incident: Thread Deadlock — Recommendation Engine (Lab 02)
#### Problem Scenario
Recommendation service experiences complete throughput drop for 30 seconds. Thread dump shows 200 threads in `BLOCKED` state.

#### Interview Walkthrough
**Step 1 — Capture thread dump**: `jstack -l <pid>`. Shows deadlock between Thread A (holds UserProfileCache, waiting for RecommendationModel) and Thread B (holds RecommendationModel, waiting for UserProfileCache).

**Step 2 — Root cause**: New feature (real-time model update) calls `cache.invalidateAll()` while holding model lock. Recommendation path calls `model.predict()` while holding cache lock. Classic lock ordering deadlock.

**Step 3 — Fix**: Establish consistent lock ordering (cache → model). Use `ReentrantReadWriteLock` for read-write separation.

#### Solution
```java
private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
private final ReadWriteLock modelLock = new ReentrantReadWriteLock();

public Recommendation getRecommendations(String userId) {
    cacheLock.readLock().lock();
    try {
        UserProfile profile = profileCache.get(userId);
        cacheLock.readLock().unlock();
        modelLock.readLock().lock();
        try { return model.predict(profile); }
        finally { modelLock.readLock().unlock(); }
    } finally {
        if (cacheLock.readLock().isHeldByCurrentThread())
            cacheLock.readLock().unlock();
    }
}
```

### Incident: High CPU — JSON Parsing in Zuul (Lab 03)
#### Problem Scenario
Netflix's Zuul API gateway experiences 90% CPU utilization. Request latency increases 3x.

#### Interview Walkthrough
**Step 1 — Profile**: async-profiler shows `ObjectMapper.readValue()` consuming 45% of CPU.

**Step 2 — Root cause**: A Zuul filter parses the full request body (up to 1MB for video uploads) as JSON just to validate a single `contentType` field. At 10k req/s, this is extremely expensive.

**Step 3 — Fix**: Check Content-Type header first. Use streaming `JsonParser` to read only the needed field.

#### Solution
```java
public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    HttpServletRequest request = ctx.getRequest();
    String contentType = request.getContentType();
    if (contentType == null || !contentType.startsWith("application/json")) {
        return null;
    }
    try (JsonParser parser = new JsonFactory().createParser(request.getInputStream())) {
        while (!parser.isClosed()) {
            JsonToken token = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(token) && "contentType".equals(parser.getCurrentName())) {
                parser.nextToken();
                validateContentType(parser.getValueAsString());
                break;
            }
        }
    }
    return null;
}
```

### Incident: DR Failover — Regional CDN Outage (Lab 15)
#### Problem Scenario
AWS us-east-1 experiences an EC2 API failure, preventing auto-scaling and new instance launches. Netflix's streaming control plane in us-east-1 can't scale to handle peak evening traffic (8PM ET). Streaming failures increase for East Coast users.

#### Interview Walkthrough
**Step 1 — Assess the situation**: EC2 auto-scaling in us-east-1 is down. The streaming control plane has 80% capacity utilization and growing. Traffic is 50% above normal due to a new show release.

**Step 2 — Activate failover**: Netflix uses a multi-region active-active architecture. The control plane in us-west-2 has spare capacity. Use Eureka service discovery to redirect traffic to us-west-2 instances.

**Step 3 — Update DNS**: Use Route 53 with latency-based routing. East Coast users start resolving to us-west-2 endpoints. The latency increase is acceptable (40ms → 80ms for East Coast — within streaming tolerance).

**Step 4 — Verify streaming quality**: Check Atlas monitoring: `streaming.startup.success`, `buffering.ratio`, `playback.failure`. All metrics remain within SLO.

**Step 5 — Root cause**: AWS regional API failure. Netflix's active-active design and multi-region architecture provided automatic resilience.

**What Netflix evaluates**: Multi-region architecture; chaos engineering mindset; understanding of Netflix's Open Connect CDN.

#### Solution
```java
// Eureka service discovery with multi-region failover
public class RegionAwareLoadBalancer {
    private final DiscoveryClient discoveryClient;

    public List<InstanceInfo> getInstances(String vipAddress) {
        List<InstanceInfo> instances = discoveryClient.getInstancesByVipAddress(
            vipAddress, false);
        // Prefer local region, fallback to other regions
        if (instances.stream().noneMatch(i -> i.getRegion().equals(LOCAL_REGION))) {
            logger.warn("No local instances for {}, using cross-region", vipAddress);
        }
        return instances;
    }
}
```

#### Follow-ups
- **At Netflix scale**: Netflix's Open Connect CDN has edge appliances at ISP locations — these are not affected by AWS regional outages. The control plane in AWS handles only session management, not content delivery.
- **Chaos experiment**: "Kill us-east-1 control plane during peak traffic and measure streaming success rate."

### Incident: API Rate Limiting — Streaming Requests (Lab 14)
#### Problem Scenario
A new device type sends 10x more streaming requests than expected. The session management service rate-limits legitimate traffic.

#### Interview Walkthrough
**Step 1 — Identify the source**: New device (Smart TV model X100) accounts for 60% of traffic. Each TV polls the session endpoint every 5 seconds instead of every 60 seconds.

**Step 2 — Root cause**: The device firmware has a bug causing aggressive polling. Rate limiter treats all devices equally.

**Step 3 — Fix**: Implement per-device-type rate limiting. Contact TV manufacturer for firmware fix. Add adaptive rate limiting based on device behavior patterns.

#### Solution
```java
// Per-device-type rate limiting
public boolean isRateLimited(String deviceType, String deviceId) {
    RateLimiter limiter = rateLimiters.computeIfAbsent(
        deviceType, k -> RateLimiter.create(100));  // 100 req/s per type
    return !limiter.tryAcquire();
}
```

## System Design for Reliability

### Design Question 1: Design Netflix's Video Encoding Pipeline
Design a video transcoding pipeline processing 100k hours of video daily. Discuss job distribution, retry strategies, handling corrupted source files.

**Key points**: Job queue with SQS. Priority tiers (new releases vs catalog). Retry with exponential backoff and dead-letter on corruption. Auto-scaling encoding workers based on queue depth.

### Design Question 2: Design a Chaos Experiment Platform
Design a system for safe failure injection. Discuss blast radius control, auto-rollback, customer impact detection, Chaos Monkey vs Chaos Kong.

**Key points**: Blast radius limited to 1% of traffic. Automated rollback on error rate > 1%. Atlas monitoring for detection. Chaos Kong for region-level testing. Experiment metadata for post-analysis.

### Design Question 3: Design Netflix Streaming Control Plane
Design the system orchestrating streaming sessions for 200M subscribers. Discuss CDN failures, adaptive bitrate switching, regional outages.

**Key points**: Open Connect CDN appliances at ISP edges. Adaptive bitrate with multi-CDN failover. Session state in EVCache. Graceful degradation on CDN failure.

## Incident Command Behavioral

### Question 1: Tell me about a time you applied chaos engineering. (Curiosity)
**STAR**: I ran a Chaos Monkey experiment killing a cache node (Lab 08) and discovered our circuit breaker didn't handle the cache miss storm. We redesigned the fallback hierarchy with local caching.

### Question 2: Describe a high-stakes decision made quickly. (Judgment)
**STAR**: During the encoding pipeline deployment (Lab 06), I saw 8% error rate in the canary. I rolled back via Spinnaker immediately, preventing bad codec from reaching 100% of fleet.

### Question 3: How do you handle speed vs reliability tension? (Courage)
**STAR**: After the circuit breaker incident (Lab 07), the product team wanted to remove the breaker. I argued the breaker was correct — the fallback needed improvement. I implemented multi-level fallback and kept the breaker.

### Question 4: Describe owning a problem end-to-end. (Ownership)
**STAR**: The Kafka consumer lag (Lab 13) impacted recommendation freshness. I didn't just fix the partition key — I updated monitoring, wrote runbook, and ran a chaos experiment to validate under load.

### Question 5: How do you influence without authority? (Impact)
**STAR**: I noticed several teams had the same cache stampede problem (Lab 08). I built a shared `StampedeSafeCache` library with probabilistic early expiration and onboarded 5 teams. Cache incidents dropped 80%.

### Question 6: Describe a time you failed and what you learned. (Self-Improvement)
**STAR**: I once deployed a Zuul filter change without profiling (Lab 03). The full-body JSON parsing caused 90% CPU. I learned to always profile before deploying, and added profiling to our CI pipeline.

### Question 7: How do you design a system to handle 10x traffic growth?
**STAR**: For the content catalog service (Lab 08), I designed with EVCache for caching, DynamoDB with auto-scaling, and Spinnaker for canary deployments. When traffic grew 10x, the system auto-scaled without changes.

### Question 8: Describe a time you used data to drive a reliability improvement.
**STAR**: I analyzed Hystrix circuit breaker metrics from Atlas (Lab 07) and found that 30% of circuit breaker opens were from a single dependency. I worked with that team to improve their p99 latency from 2s to 200ms.

### Question 9: How do you handle the tradeoff between cost and reliability?
**STAR**: The recommendation team wanted full active-active across 5 AWS regions. I proposed active-active in 3 regions with warm standby in 2. This saved 40% in infrastructure costs while maintaining the same availability SLO through proper failover design.

### Question 10: Tell me about a time you ran a successful chaos experiment.
**STAR**: I designed a Chaos Kong experiment: terminate an entire AWS region's control plane (Lab 15). We verified that East Coast users fail over to West Coast within 30 seconds with < 5% streaming failure rate during the transition.

### Question 11: How do you design experiments that are safe for production?
**STAR**: Every chaos experiment follows our "safe experiment" framework: (1) blast radius limited to 1% of users, (2) automated rollback at 1% error rate increase, (3) 15-minute max experiment duration, (4) all experiments must be approved via code review, (5) experiments run during low-traffic windows first.

### Question 12: Describe a time you used the Hystrix circuit breaker pattern in practice.
**STAR**: The recommendation service circuit breaker (Lab 07) was my first Hystrix production use. I learned that the default fallback (empty response) was worse than stale data. I implemented a multi-level fallback: cache → local cache → degraded response. Engagement recovered from 15% drop to 2% drop.

### Question 13: Tell me about a time you had to say no to a product feature for reliability reasons.
**STAR**: A product manager wanted to add real-time collaborative playlist editing without caching. I showed that the feature would add 20ms to every streaming session's p99 latency (exceeding SLO). We compromised on eventual consistency with 5-second cache TTL.

### Question 14: How do you measure the blast radius of a failure?
**STAR**: For the Kafka hot partition (Lab 13), I measured blast radius by: (1) number of affected users (all users of "Stranger Things" recommendations), (2) impact on engagement (12% drop in recommendation click-through), (3) dependency cascade (2 downstream services experienced circuit breaker opens).

### Question 15: Describe your philosophy on testing in production.
**STAR**: Netflix tests in production because synthetic tests don't capture real-world conditions. But we do it safely: canary everything, monitor aggressively, rollback automatically. The chaos experiment (Lab 15) only ran after the fix was validated in canary for 1 hour at 1% traffic.

### Question 16: How do you design a system for graceful degradation?
**STAR**: The recommendation service (Lab 07) is designed to degrade gracefully: if the personalization model is unavailable, serve trending content. If trending is unavailable, serve recently added content. If the content catalog is unavailable, serve from CDN cache with stale data. Every dependency has a defined degraded mode.

### Question 17: Describe a time you had to rebuild a service from scratch for reliability.
**STAR**: The notification fan-out service had recurring issues with hot partitions (Lab 13). I redesigned it with: (1) composite partition keys, (2) separate topics for high-volume senders, (3) backpressure with Hystrix, (4) automatic partition rebalancing. The new design handled 2x traffic with zero incidents.

### Question 18: How do you handle dependencies that are unreliable?
**STAR**: The content metadata service had a 5% failure rate from a downstream provider. I implemented Hystrix with a multi-level fallback (Lab 07): local cache (5ms), remote cache (50ms), degraded response (stale data). The circuit breaker isolates failures so the provider's issues don't cascade.

### Question 19: Tell me about a time you reduced infrastructure costs without reducing reliability.
**STAR**: I analyzed our EC2 utilization and found 30% waste from over-provisioned instances. I moved to right-sized instances with auto-scaling and spot instances for batch processing. This saved $2M/year while maintaining the same p99 latency SLO.

### Question 20: How do you measure the success of a reliability improvement?
**STAR**: I track: (1) error budget consumption before/after, (2) MTTR for related incidents, (3) pager reduction for the related alert, (4) customer-facing availability SLO. For the circuit breaker fix (Lab 07), error budget consumption dropped from 30% to 5% per month.

### Question 21: How does Netflix handle the "no SSH" policy?
**STAR**: Netflix doesn't allow SSH access to production — all changes go through deployment pipelines. Debugging uses: (1) Atlas metrics for system health, (2) centralized logging, (3) distributed tracing, (4) thread dumps captured and served via a web endpoint, (5) heap dumps captured on OOM and stored in S3.

### Question 22: Describe a time you used canary analysis to prevent a bad deployment.
**STAR**: The encoding pipeline canary (Lab 06) showed 8% error rate in Atlas. Spinnaker's canary analysis detected the regression and automatically rolled back. I then fixed the hardware detection and the next canary passed with 0% regression.

### Question 23: How do you handle data consistency across regions?
**STAR**: Netflix uses EVCache cross-region replication for caching (eventually consistent). For critical state (user profiles, payment info), we use DynamoDB global tables with last-writer-wins conflict resolution. We accept eventual consistency for most use cases to prioritize availability.

### Question 24: Tell me about a time you optimized a Spinnaker pipeline.
**STAR**: Our deployment pipeline took 45 minutes per deploy — too slow for fast iteration. I optimized: parallel bake + test stages (15 min saved), caching of Docker layers (10 min saved), and reduced canary observation window from 30 min to 15 min based on data showing most regressions appear within 5 minutes.

### Question 25: How do you ensure chaos experiments don't cause customer impact?
**STAR**: Every chaos experiment follows strict safety rules: (1) blast radius limited to 1% of traffic, (2) automated rollback on 1% error rate increase, (3) experiments run during off-peak hours, (4) rollback plan approved before experiment starts, (5) experiments are terminated after 15 minutes regardless of results.

### Netflix's Approach to Capacity and Cost Management

Understanding Netflix's unique approach helps differentiate your answers:

**Immutable Infrastructure**: Netflix treats servers as disposable. No SSH access in production — all changes go through deployment pipelines. AMIs are baked, not configured at runtime. This prevents configuration drift and ensures consistent deployments.

**Red/Black Deployments**: Netflix's deployment strategy (also called blue-green). A new "red" cluster is created alongside the existing "black" cluster. Once the red cluster passes health checks, traffic is switched from black to red. Rolling back means switching back to black.

**EVCache (Ephemeral Volatile Cache)**: Netflix's distributed caching layer built on Memcached. Features: (1) cross-region replication for disaster recovery, (2) zone-aware routing, (3) transparent failover, (4) TTL-based expiration with jitter.

**Netflix's Microservice Architecture**: 1000+ microservices organized in tiers: (1) Edge services (Zuul), (2) API composition (REST API), (3) Middle-tier services (recommendations, search), (4) Data services (content metadata, user history), (5) Infrastructure services (Eureka, configuration).

**The 3-Tiered Deployment Model**: Netflix services have three tiers: (1) Canary — 1-2 instances with production traffic, (2) Cluster — 10-20% of fleet for initial rollout, (3) Production — full fleet. Each tier has automated rollback based on alert thresholds.

**Observability at Netflix**: Three pillars with specific Netflix tools: (1) Metrics → Atlas, (2) Tracing → Zipkin-compatible distributed tracing, (3) Logging → ELK stack + Scribe for high-volume log transport.

**Netflix's Approach to Capacity and Cost Management**:

Netflix manages huge infrastructure costs while maintaining reliability:

**Capacity Planning**: Netflix uses predictive models based on: (1) subscriber growth trends, (2) content catalog growth, (3) new feature launches, (4) seasonal patterns (holiday peaks). Capacity is provisioned at 1.5x predicted peak with auto-scaling for additional headroom.

**Spot Instance Strategy**: Netflix runs 50%+ of compute on EC2 Spot Instances. Services are designed to handle spot terminations gracefully using: (1) checkpoint/resume for batch jobs, (2) multi-AZ deployment to spread spot risk, (3) fallback to on-demand when spot is unavailable.

**Reserved Instance Optimization**: Baseline traffic uses 3-year Reserved Instances for 40% discount. Variable traffic uses Spot + On-Demand. Netflix uses a custom tool called "Ice" for AWS usage analysis and cost optimization.

**CDN Cost Management**: Netflix's Open Connect CDN appliances are deployed at ISP partner locations for free or reduced cost. This reduces AWS data transfer costs by 90%+ and improves streaming quality by keeping traffic local.

**Chaos Engineering for Cost**: Netflix runs "cost chaos" experiments: what happens if we reduce EC2 instance sizes by 20%? What if we reduce DynamoDB read capacity by 15%? These experiments find the minimum reliable resource configuration.

## Study Plan

### Priority Labs for Netflix Cloud Engineering
1. **Lab 07 (Circuit Breaker)** — Core Netflix pattern
2. **Lab 13 (Kafka Consumer Lag)** — Real-time streaming
3. **Lab 08 (Cache Stampede)** — Caching at scale
4. **Lab 06 (Deployment Rollback)** — Spinnaker/CD pipelines
5. **Lab 02 (Thread Deadlock)** — Concurrency patterns
6. **Lab 03 (High CPU)** — Performance optimization
7. **Lab 14 (API Rate Limiting)** — Traffic management

### Recommended Schedule
- **Week 1-2**: Labs 07, 08 (circuit breakers + caching)
- **Week 3**: Labs 13, 06 (streaming + deployment)
- **Week 4**: Labs 02, 03, 14 (concurrency + performance + rate limiting)
- **Week 5**: Chaos engineering design + mock interviews

### Lab Practice Checklist
- For each lab: (1) build a chaos experiment that would have caught this issue, (2) implement the fix using Netflix OSS patterns, (3) design an Atlas dashboard that monitors for the pattern, (4) write a Spinnaker pipeline that prevents the issue from reaching production

## Tips

### Netflix-Specific Technical Concepts to Master

**Adaptive Bitrate (ABR) Streaming**: Netflix's streaming quality is managed by ABR algorithms. Know: (1) available bandwidth detection, (2) buffer-based vs rate-based ABR, (3) per-title encoding optimization, (4) CDN selection based on latency and throughput.

**Open Connect CDN**: Netflix's custom CDN appliances at ISP locations. Know: (1) peering relationships with ISPs, (2) content pre-seeding for popular titles, (3) block request routing to nearest CDN node, (4) hardware spec (storage servers with 100TB+ SSDs).

**Fault Tolerance Testing (FIT)**: Netflix's chaos engineering platform for automated failure injection. Know: (1) experiment definition (hypothesis, blast radius, duration), (2) integration with Spinnaker for deployment gating, (3) automatic rollback on threshold breach.

**Container Platform (Titus)**: Netflix's container management platform. Know: (1) integration with AWS (ENI-based networking), (2) resource isolation (CPU, memory, network), (3) ephemeral storage for containers, (4) integration with Netflix OSS stack (Eureka, Hystrix).

**Streaming Data Pipeline**: Netflix processes 1.5T events/day through: (1) Kafka for event ingestion, (2) Apache Spark for batch processing, (3) Flink for stream processing, (4) Elasticsearch for log analytics, (5) Cassandra for time-series data.

### Netflix Cloud Engineering Interview Strategies
1. **Chaos engineering is central**: Every design answer should include "how would you test this in failure mode?" Reference Chaos Monkey, Chaos Kong, Litmus.
2. **Know the Netflix OSS stack**: Eureka, Hystrix, Zuul, Ribbon, Archaius, Atlas, Spinnaker, EVCache.
3. **AWS-native thinking**: Netflix runs on AWS. Know EC2, S3, DynamoDB, Aurora, ElastiCache, CloudFront, SQS deeply.
4. **"Freedom and Responsibility"**: Show initiative without waiting for permission. Take ownership of outcomes.
5. **Microservices mindset**: Every answer should consider service boundaries, failure isolation, independent deployability.
6. **Blast radius**: Always think about limiting impact of any change or failure. Canary everything.
7. **Data-driven decisions**: Netflix uses A/B testing for everything. Show you measure before and after any change.
8. **High-performance culture**: Set high standards for yourself and your team. Never accept "good enough" for reliability.
9. **Test in production**: Netflix tests everything in prod. Know how to safely test in production with minimal customer impact.
10. **Resilience patterns**: Bulkheads, circuit breakers, timeouts, retries with backoff, fallbacks, health checks, graceful degradation.
11. **Study Netflix Tech Blog**: Read all Netflix Tech Blog posts about their infrastructure, especially "Chaos Engineering", "EVCache", "The Netflix API", and "Containerization at Netflix".
12. **Practice chaos experiments**: For each system design answer, describe a chaos experiment that would validate the design. What happens when a cache node fails? A database replica fails? An entire region fails?
13. **Spinnaker pipelines**: Understand Spinnaker pipeline stages (Bake, Deploy, Canary, Rollback) and how canary analysis works with Atlas metrics.
14. **Hystrix thread pool vs semaphore isolation**: Know when to use each — thread pool for network calls (timeoutable), semaphore for in-process calls (lower overhead). Understand the thread pool sizing formula.
15. **AWS multi-region architecture**: Netflix runs in 3+ AWS regions. Understand how traffic is routed (Route 53 latency routing), how data is replicated (EVCache cross-region), and how failover works (DNS TTL reduction, connection draining).
16. **Bulkhead Pattern**: Netflix uses bulkheads to isolate failures. Know: thread pool isolation (one pool per downstream dependency), semaphore isolation (for latency-tolerant calls), and how to size pool threads based on max concurrent requests and timeout.
17. **Concurrency Limits**: Netflix uses concurrency limits (adaptive or fixed) to prevent services from being overwhelmed. Know: (1) TCP congestion control-style algorithms (AIMD), (2) adaptive concurrency limits based on latency, (3) integration with Hystrix for circuit breaking when limits exceeded.

### Netflix Cloud Engineering Mock Interview Questions

Practice these questions before your interview:

**Chaos Engineering Practice**:
- Design a Chaos Monkey experiment for a cache failure scenario
- How do you safely test a region failover in production?
- What metrics do you monitor during a chaos experiment to determine success/failure?

**System Design Practice**:
- Design Netflix's recommendation pipeline for 200M subscribers
- Design the video encoding pipeline for 100k hours/day
- Design the streaming session control plane with multi-region failover

**Behavioral Practice**:
- "Describe a time a chaos experiment found an unexpected failure"
- "How do you handle a deployment that causes a 5% error rate?"
- "Tell me about a time you influenced a team to adopt reliability practices"
