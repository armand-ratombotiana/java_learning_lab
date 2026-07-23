# Meta ProdEng Interview Guide — Real Production Scenarios Academy

## Interview Process for ProdEng Roles

### Rounds
1. **Phone Screen (45 min)**: Coding — LeetCode medium, focus on strings, arrays, hash maps. Also a brief systems discussion.
2. **Onsite (4-5 rounds, 45 min each)**:
   - **Coding Round 1**: Algorithm-focused, medium-hard, must compile
   - **Coding Round 2**: Debugging-focused — given a broken code snippet, find and fix the bug
   - **Systems Round**: Design a reliable system, often data-intensive (Facebook News Feed, Messenger, Real-time notifications)
   - **ProdEng Deep Dive**: Production incident analysis — given logs/metrics, walk through RCA
   - **Behavioral**: Meta leadership principles, "move fast with stable infra"

### ProdEng-Specific Expectations
- Debugging heavy — Meta expects you to read code like a detective
- "Move fast with stable infra" — know how to balance speed vs reliability
- Heavy focus on distributed systems at scale (billions of users)
- Hack/PHH (Meta's PHP-to-C++ transpiler) knowledge is a plus but not required
- Must understand TAO (Meta's distributed graph), Memcache, Haystack (photo storage), Scribe (logging)
- Meta's infrastructure is 100% custom — expect questions about how you'd build systems from scratch

### Round Breakdown
- Coding (Debugging + Algorithm): 50% — biggest weighting
- Systems Design: 25% — scale and data flow
- ProdEng Incident: 15% — log analysis, root cause
- Behavioral: 10% — Meta principles

### ProdEng Debugging Methodology

Meta's ProdEng interview rounds often present you with a broken code snippet and ask you to find the bug. Use this systematic approach:

**Step 1 — Understand the expected behavior**: What should this code do? Read the method signature, comments, and any test cases if provided.

**Step 2 — Trace the happy path**: Walk through the code with a simple input. Does it produce the expected output?

**Step 3 — Trace edge cases**: Empty input, null values, boundary conditions, concurrent access. These are where 90% of bugs hide.

**Step 4 — Check for resource leaks**: Are streams, connections, or locks closed in all paths (including exception paths)?

**Step 5 — Check thread safety**: Are shared mutable objects accessed without synchronization? Are there deadlock-prone lock orderings?

**Step 6 — Check performance**: Are there O(n^2) algorithms hiding in loops? Is data being recomputed unnecessarily?

**Common Meta-specific bug patterns**:
- Shadowed variable names (especially in nested loops)
- Incorrect loop variable in nested loops (using `i` instead of `j`)
- Missing `equals()` override on custom objects used in `HashSet`/`HashMap`
- `Comparator` that doesn't follow the contract (not transitive)
- Thread-local cache without cleanup
- Synchronous blocking in async context
- Expensive computation inside a stream/lambda

## Top Incidents Aligned to Meta ProdEng Focus

### Incident: High CPU — Inefficient Algorithm (Lab 03)
#### Problem Scenario
Meta's News Feed ranking service shows CPU at 98% with 500ms p99 latency. The team suspects a recent deployment introduced an inefficiency. The service processes 50k requests/second. A rollback was attempted but didn't fully resolve the issue — only improved CPU by 10%.

#### Interview Walkthrough
**Step 1 — Profile the service**: Use `perf top -p <pid>` on Linux (Meta uses CentOS-based infrastructure). Show the hottest function: `NewsFeedRanker.calculateRelevanceScore()` consuming 65% of CPU.

**Step 2 — Flame graph analysis**: Use async-profiler to generate SVG flame graph. `async-profiler -e cpu -d 60 -f profile.html <pid>`. The flame graph shows a deep call stack: `calculateRelevanceScore` → `computeUserAffinity` → `sortByScore` → `TimSort.countRunAndMakeAscending`. The reddest (hottest) frame is `compare()` in a custom `Comparator`.

**Step 3 — Examine the comparator**: The `UserAffinityComparator` recomputes `computeUserAffinity()` on every comparison. Since TimSort can call `compare()` O(n log n) times, the affinity score is recalculated thousands of times per request.

**Step 4 — Root cause**: The comparator calls `computeUserAffinity(userId, friendId)` fresh each comparison. This method does a database lookup for user interaction frequency. Pre-computation was done once per friend list but accidentally moved inside the comparator during a refactor.

**Step 5 — Fix**: Pre-compute affinity scores into a `Map<Pair<UserId>, Double>` before sorting. Use the map inside the comparator.

**What Meta evaluates**: Ability to read flame graphs; understanding of sorting algorithm internals; awareness of comparator contracts; profiling skills.

#### Solution
```java
// Before: expensive recomputation in comparator
List<Friend> ranked = friends.stream()
    .sorted((a, b) -> Double.compare(
        computeUserAffinity(userId, b.getFriendId()),  // Called per comparison
        computeUserAffinity(userId, a.getFriendId())   // O(n log n) times!
    ))
    .collect(Collectors.toList());

// Fix: pre-compute once
Map<Long, Double> affinityCache = new HashMap<>();
for (Friend f : friends) {
    affinityCache.put(f.getFriendId(),
        computeUserAffinity(userId, f.getFriendId()));
}
List<Friend> ranked = friends.stream()
    .sorted((a, b) -> Double.compare(
        affinityCache.get(b.getFriendId()),
        affinityCache.get(a.getFriendId())
    ))
    .collect(Collectors.toList());
```

**Post-mortem**: Add a performance regression test that measures sorting time. Add async-profiler to CI pipeline. Add code review checklist item: "Any expensive computation in a lambda or loop?"

#### Follow-ups
- **At Meta scale**: Use `cgroup` CPU limiting per service tier. Profile with Meta's internal profiling system (Scuba-based). Pre-compute affinity scores via a batch job and store in TAO graph.
- **Testing for performance regression**: Add a microbenchmark using JMH that runs on every PR.

### Incident: Connection Pool Exhaustion with Read Replicas (Lab 04)
#### Problem Scenario
Meta's Messenger service experiences intermittent failures. Users report messages not sending. Error rate spikes from 0.01% to 5%. The service connects to a MySQL cluster with 1 writer and 3 read replicas.

#### Interview Walkthrough
**Step 1 — Check error logs**: `grep "Connection is not available, request timed out" /var/log/messenger/*.log`. The error comes from HikariCP — connection pool exhausted.

**Step 2 — Check connection pool metrics**: Active connections = 200 (max pool size). Pending = 150. Wait time is increasing.

**Step 3 — Identify which connection type is exhausted**: Separate pools for writer (max 50) and replicas (max 150). Writer pool shows `Active: 50, Pending: 80`. Writer is exhausted, not replicas.

**Step 4 — Root cause**: A "read-after-write consistency" feature was added: after sending a message, the service reads from the writer (not the replica) to ensure the user sees the message immediately. This doubled the writer traffic. A single `getConversation()` call that should hit a replica now hits the writer for 100% of reads.

**Step 5 — Fix**: Implement a "read-your-writes" consistency rule: use the writer only for the specific user who wrote, replicas for all others. Add a `ConsistencyLevel` parameter to database queries.

#### Solution
```java
// Before: all reads go to writer after write
public Conversation getConversation(long userId, long conversationId) {
    return databaseWriter.query("SELECT * FROM conversations WHERE id = ?",
        conversationId);
}

// Fix: read-your-writes consistency
public Conversation getConversation(long userId, long conversationId) {
    long lastWriterId = getLastWriterForConversation(conversationId);
    if (userId == lastWriterId) {
        return databaseWriter.query(...);
    }
    return databaseReplica.query(...);
}
```

**Post-mortem**: Add pool utilization alerts per pool (writer vs replica). Add query tracing to track which pool each query targets.

#### Follow-ups
- **At Meta scale**: Use a proxy layer (like Meta's own database proxy) that handles read-after-write automatically.
- **Monitor pool diversity**: Ensure each query type has its own pool with proper sizing.

### Incident: Kafka Consumer Lag — Notifications (Lab 13)
#### Problem Scenario
Meta's real-time notification system (Liger) processes 2M events/second through Kafka. Consumer lag starts increasing on the "notification-delivery" topic, growing from 100ms to 30 seconds in 10 minutes.

#### Interview Walkthrough
**Step 1 — Check consumer group status**: `./kafka-consumer-groups.sh --bootstrap-server kafka01:9092 --group notification-delivery --describe`. Shows LAG = 1.5M messages and growing.

**Step 2 — Check consumer health**: Each partition has one consumer. One consumer (partition 7) shows LAG of 1.2M — disproportionate compared to others (~50k each).

**Step 3 — Root cause**: Partition 7 has data for a celebrity user with 100M followers. A single notification (celebrity posted) fan-out generates 100M notification events in one partition. The consumer processes each notification sequentially — it takes 5 seconds to fan-out 100M notifications, during which time messages keep arriving.

**Step 4 — Fix**: Parallelize the fan-out within the consumer. Use a thread pool with configurable parallelism. Implement batch processing — group 1000 notifications into one batch.

**What Meta evaluates**: Understanding of Kafka partitions; ability to identify hot partitions; knowledge of fan-out patterns at scale.

#### Solution
```java
// Before: sequential fan-out
public void processNotification(NotificationEvent event) {
    List<Long> followerIds = friendGraph.getFollowers(event.getUserId());
    for (Long followerId : followerIds) {
        notificationStore.save(followerId, event);
    }
}

// Fix: parallel fan-out with batching
private final ExecutorService fanOutPool = Executors.newFixedThreadPool(20);

public void processNotification(NotificationEvent event) {
    List<Long> followerIds = friendGraph.getFollowers(event.getUserId());
    List<List<Long>> batches = Lists.partition(followerIds, 1000);
    List<CompletableFuture<Void>> futures = batches.stream()
        .map(batch -> CompletableFuture.runAsync(() -> {
            notificationStore.saveBatch(batch, event);
        }, fanOutPool))
        .collect(Collectors.toList());
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .join();
}
```

#### Follow-ups
- **At Meta scale**: Use a separate Kafka topic for high-fan-out users. Use TAO (Meta's graph DB) fan-out service instead of direct notification store writes.
- **Prevention**: Add consumer lag alerting with partition-level granularity.

### Incident: Cache Stampede — News Feed (Lab 08)
#### Problem Scenario
Meta's News Feed cache (Memcache) TTL expires for top stories. 10k requests/second all miss the cache simultaneously and hit MySQL. DB CPU spikes from 20% to 100% in 30 seconds.

#### Interview Walkthrough
**Step 1 — Read the graphs**: Memcache `get_miss_rate` jumps from 5% to 95%. MySQL `cpu_utilization` spikes. This is a textbook cache stampede.

**Step 2 — Check TTL configuration**: All news feed entries have TTL = 300 seconds. The entries were all written during cache warming (same time), so they all expire together.

**Step 3 — Root cause**: No TTL jitter. No "dogpile prevention" mechanism. Meta's Memcache supports `add` (atomic set-if-not-exists) which can be used as a lock.

**Step 4 — Fix**: Use `Memcache.add()` with a short lock TTL (not for caching data, but as a mutex). Only one request recomputes the value; others wait and use the stale value.

#### Solution
```java
public NewsFeed getNewsFeed(long userId) {
    String key = "news_feed:" + userId;
    NewsFeed cached = memcacheClient.get(key);
    if (cached != null) return cached;
    String lockKey = key + ":lock";
    if (memcacheClient.add(lockKey, 1, 5)) {
        try {
            NewsFeed fresh = database.fetchNewsFeed(userId);
            int ttl = 300 + ThreadLocalRandom.current().nextInt(60);
            memcacheClient.set(key, fresh, ttl);
            return fresh;
        } finally {
            memcacheClient.delete(lockKey);
        }
    }
    NewsFeed stale = memcacheClient.get(key);
    if (stale != null) return stale;
    Thread.sleep(50);
    return getNewsFeed(userId);
}
```

#### Follow-ups
- **At Meta scale**: Use Meta's McDipper (Memcache sharding) with local caching. Use "lease get" mechanism for stampede prevention.
- **Generalize**: Abstract this into a `StampedeSafeCache` wrapper for all cache accesses.

### Incident: Deployment Rollback — Bad Config Push (Lab 06)
#### Problem Scenario
A configuration change was pushed to Meta's News Feed ranking service. The change was supposed to enable a new ranking signal but accidentally disabled the primary relevance signal. Within 2 minutes, user engagement (likes, comments, shares) dropped 40%.

#### Interview Walkthrough
**Step 1 — Identify the change**: Meta uses a configuration management system (similar to ZooKeeper). Check the change log: `config push by engineer X at 14:02:03`. The config file changed the `ranking_weights` section.

**Step 2 — Compare config versions**: `diff old_config.yaml new_config.yaml` — shows `relevance_signal_weight: 0.0` (was 1.0). The engineer meant to set `new_signal_weight: 0.5` but accidentally zeroed the existing signal.

**Step 3 — Rollback**: Revert the config change immediately. Meta's config system supports atomic rollback. `config rollback --revision PREVIOUS --service news-feed-ranking`.

**Step 4 — Post-mortem**: Why was there no validation? The config schema allowed `weight` to be any float including 0. Add validation that `relevance_signal_weight > 0`.

**What Meta evaluates**: Understanding of config management; speed of rollback; validation thinking; blast radius reduction.

#### Solution
```yaml
# Before: no validation
ranking_weights:
  relevance_signal_weight: 0.0
  new_signal_weight: 0.5

# After: schema validation
ranking_weights:
  type: object
  required: [relevance_signal_weight]
  properties:
    relevance_signal_weight:
      type: number
      minimum: 0.01
      exclusiveMinimum: true
    new_signal_weight:
      type: number
      minimum: 0.0
      maximum: 10.0
```

**Post-mortem**: Add canary config push (push to 1% of servers, observe metrics for 2 min). Add engagement rate alert with 2-minute window. Add config diff review step in deployment pipeline.

#### Follow-ups
- **At Meta scale**: Config changes are pushed tier-by-tier (1% → 10% → 50% → 100%). Auto-rollback triggers if engagement metric drops > 5%.
- **Config schema enforcement**: Use JSON Schema for all config files, validated in CI.

### Incident: Thread Deadlock — Async Framework (Lab 02)
#### Problem Scenario
Meta's Async framework (similar to Hack/HHVM async) has a worker pool processing incoming requests. After a deploy, certain requests hang indefinitely. The health check kills and restarts the service every 10 minutes.

#### Interview Walkthrough
**Step 1 — Capture thread dump**: Run `kill -3 <pid>` to trigger thread dump to stdout. Look for threads in `WAITING` state.

**Step 2 — Analyze the blocking chain**: Thread A is waiting for the result of a deferred computation. The deferred computation is scheduled on the same worker pool. All 40 worker threads are blocked, each waiting for another deferred computation that is queued — but no threads are available to execute them.

**Step 3 — Root cause**: The deployment added a new feature that calls `await()` on an async function from within an async function running on the same event loop group. Classic "sync call in async context" deadlock.

**Step 4 — Fix**: Use a separate thread pool for blocking operations. Never block the async worker pool.

#### Solution
```java
// Before: blocking in async context
public ListenableFuture<Feed> getFeed(User user) {
    List<Post> posts = feedService.fetchPosts(user).get();
    return Futures.immediateFuture(new Feed(posts));
}

// Fix: use separate thread for blocking
private final ListeningExecutorService blockingPool = MoreExecutors
    .listeningDecorator(Executors.newCachedThreadPool());

public ListenableFuture<Feed> getFeed(User user) {
    return blockingPool.submit(() -> {
        List<Post> posts = feedService.fetchPosts(user).get();
        return new Feed(posts);
    });
}
```

### Incident: Disk Space Full — Log Flood from Debugging (Lab 09)
#### Problem Scenario
Meta's video transcoding service fills the root disk on 50 of 200 hosts. Each host has 200GB of root storage. `du -sh /var/log/` shows 190GB consumed by transcoder debug logs.

#### Interview Walkthrough
**Step 1 — Identify pattern**: Only transcoding workers processing 4K video have the issue. Workers processing 1080p or lower are fine.

**Step 2 — Root cause**: A debug log line `log.info("Processing frame %d of %d", frameNum, totalFrames)` was accidentally left at `INFO` level. For 4K video (8M frames), this generates 8M log lines per video — ~200MB per video. At 100 videos/host/day, that's 20GB/day.

**Step 3 — Fix**: Change to `log.debug`. Add exponential frame logging (log every 1000th frame). Implement log rotation with `max-size=100MB, max-file=3`.

#### Solution
```java
// Before: logs every frame at INFO
for (int i = 0; i < totalFrames; i++) {
    processFrame(i);
    log.info("Processing frame {} of {}", i, totalFrames);
}

// After: log every 1000th frame at DEBUG
for (int i = 0; i < totalFrames; i++) {
    processFrame(i);
    if (i % 1000 == 0) {
        log.debug("Processing frame {} of {}", i, totalFrames);
    }
}
```

#### Follow-ups
- **At Meta scale**: Use Scribe (Meta's distributed logging system) with sampling. Implement log-level override at runtime via ZooKeeper config.
- **Chaos experiment**: "Fill disk to 95% and verify auto-remediation."

### Incident: Slow Query — Friend List Loading (Lab 05)
#### Problem Scenario
Meta's Graph API endpoint `GET /me/friends` suddenly takes 30 seconds to load for users with > 5000 friends. The endpoint previously loaded in 200ms. A schema migration ran the previous night.

#### Interview Walkthrough
**Step 1 — Check the query**: The endpoint queries MySQL: `SELECT * FROM friends WHERE user_id = ? ORDER BY created_at DESC`. `EXPLAIN` shows the query uses an index on `user_id` but does a `filesort` for 5000 rows.

**Step 2 — Check schema change**: The migration added a new column `interaction_score` to the `friends` table. The table size increased from 10GB to 50GB. The index is now less efficient due to increased row width.

**Step 3 — Root cause**: The migration increased the row size by 5x (new JSON column). The index on `user_id` now covers larger rows, requiring more pages to be read from disk. The `ORDER BY created_at` uses filesort because there's no composite index on `(user_id, created_at)`.

**Step 4 — Fix**: Create a covering index: `CREATE INDEX idx_user_created ON friends(user_id, created_at)`. The index includes the sort column.

#### Solution
```sql
CREATE INDEX idx_user_created ON friends(user_id, created_at);
-- This is a covering index — the query can be satisfied entirely from the index
-- without touching the table rows.

-- Verify with EXPLAIN
EXPLAIN SELECT id, friend_id FROM friends 
WHERE user_id = 12345 ORDER BY created_at DESC;
-- Should show: "Using index" (covering), "Extra: Backward index scan"
```

#### Follow-ups
- **At Meta scale**: Use TAO (Meta's graph database) instead of direct MySQL for friend list queries. TAO handles the fan-out and pagination natively.
- **Chaos experiment**: "Add a heavy index to a production table and measure query performance degradation."

## System Design for Reliability

### Design Question 1: Design News Feed for 2 Billion Users
Design Meta's News Feed with reliability in mind. How do you handle the fan-out write path (push vs pull)? How do you ensure the feed loads within 200ms p99? Discuss caching layers, pre-computation, and failure modes.

**Key points**: Hybrid push-pull model. Push for active users (write to followers' feed caches), pull for inactive/celebrity users (read on request). Memcache for hot data, TAO for graph data, MySQL for persistence. Multi-layer caching with local + distributed cache.

### Design Question 2: Real-Time Notification System
Design a notification delivery system that handles 2M events/second with < 100ms end-to-end latency. Discuss Kafka partitioning strategy, fan-out handling, and exactly-once delivery semantics.

**Key points**: Use Kafka with custom partitioner (composite key for distribution). Fan-out via parallel thread pools with batching. Idempotent consumers with deduplication. Separate high-fan-out topics for celebrities.

### Design Question 3: Design Messenger for Reliability
Design a chat system that delivers messages with < 50ms p99 latency across global regions. Discuss message ordering guarantees, offline message storage, and multi-device sync.

## Incident Command Behavioral

### Question 1: Tell me about a time you found a performance bug in production.
**STAR**: I was investigating high CPU in the ranking service (Lab 03). Using async-profiler, I found a comparator recomputing affinity scores O(n log n) times. Pre-computing the scores reduced CPU by 50% and P99 latency from 500ms to 100ms.

### Question 2: How do you handle a bad deployment that affects millions of users?
**STAR**: During a config push (Lab 06), user engagement dropped 40%. I immediately initiated a rollback (2 min), confirmed recovery via dashboards, and led a post-mortem where we added schema validation and canary config pushes.

### Question 3: Describe a time you improved system reliability.
**STAR**: I noticed our Memcache layer had periodic stampede events (Lab 08). I implemented a dogpile prevention pattern using Memcache's atomic `add()` operation, reducing DB CPU spikes from 100% to 40%.

### Question 4: How do you debug a system with no obvious error logs?
**STAR**: For the Kafka consumer lag incident (Lab 13), there were no errors, just increasing lag. I checked per-partition consumer status, found one hot partition, and identified a celebrity fan-out causing the imbalance.

### Question 5: How do you prioritize reliability work vs feature work?
**STAR**: I argue that reliability is a feature. I present data: the connection pool exhaustion (Lab 04) was causing 5% error rate and 10k lost transactions/hour. I got buy-in to allocate 20% of the sprint to fixing the leak and adding monitoring.

### Question 6: Describe a situation where you disagreed with a product decision on reliability grounds.
**STAR**: A product manager wanted to disable rate limiting to handle a traffic spike. I argued that disabling rate limiting would cause cascading failures across 10 downstream services. We compromised on a 2x increase in the rate limit with automatic re-enablement after the spike.

### Question 7: How do you handle an incident where the root cause is unclear?
**STAR**: During the thread deadlock incident (Lab 02), the initial thread dump showed blocked threads but no clear cycle. I captured 3 thread dumps over 30 seconds, compared them, and identified the lock ordering pattern. I then traced the code paths to find the inconsistent lock acquisition.

### Question 8: Tell me about a time you automated a manual process.
**STAR**: The config deployment process required 4 engineers to approve each change. I automated config validation with JSON Schema, added canary deployment with automatic rollback, and reduced approval from 4 people to automated checks (Lab 06).

### Question 9: Describe how you measure the reliability of a system.
**STAR**: For the notification delivery system (Lab 13), I track: end-to-end latency p99 (< 100ms), delivery success rate (> 99.9%), consumer lag (< 1000 messages), and dead-letter queue size (< 100). These form our SLO dashboard.

### Question 10: How do you handle competing priorities from multiple teams during an incident?
**STAR**: During the connection pool exhaustion incident (Lab 04), the infra team wanted to increase max connections, the DB team wanted to kill queries, and the product team wanted a full restart. As IC, I triaged: kill idle connections first (instant relief), add index for slow queries (2 min fix), investigate leak afterwards.

### Question 11: Describe a time you made a significant technical contribution under tight deadlines.
**STAR**: During a major product launch (Instagram Stories), the notification service had a consumer lag that would delay notifications by 30 minutes (Lab 13). I rewrote the fan-out logic to use parallel batches in 48 hours, reducing lag from 30 minutes to 2 seconds.

### Question 12: How do you evaluate new technologies for production use?
**STAR**: When evaluating a new caching technology, I built a test harness that simulated Memcache failure scenarios (Lab 08). I measured P99 latency, throughput, and fallback behavior under failure. The analysis showed the new tech had 2ms higher latency but better stampede prevention, so we adopted it.

### Question 13: Tell me about a time you had to debug a production issue in a service you didn't know well.
**STAR**: I was called to help with a high-CPU issue in a team's ranking service (Lab 03). I didn't know the codebase, but I profiled with async-profiler, read the flame graph, identified the hot comparator, and worked with the team to pre-compute affinity scores.

### Question 14: How do you ensure your team's services are ready for traffic spikes?
**STAR**: After the connection pool exhaustion (Lab 04), I created a "traffic spike readiness checklist": capacity test at 2x expected peak, connection pool sizing review, database index review, canary deployment plan, and rollback script. We run this before every major launch.

### Question 15: Describe your approach to incident documentation.
**STAR**: I follow Meta's incident documentation template: timeline, impact (users affected, revenue impact), root cause, fix applied, monitoring gaps found, action items with owners. The key is writing the timeline during the incident, not after. Every post-mortem includes a "what went well" and "what went wrong" section.

### Question 16: How would you redesign a system that frequently has incidents?
**STAR**: If a service has recurring incidents (like the Memcache stampede in Lab 08), I don't just fix each incident individually. I look for the systemic gap — missing pattern libraries, missing monitoring, missing automated testing. For the stampede, I created a shared caching library that all services use, ensuring stampede prevention is baked in by default.

### Question 17: Tell me about a time you dealt with a difficult stakeholder during an incident.
**STAR**: During the config push rollback (Lab 06), the product manager wanted to "just fix the config live" instead of rolling back. I explained that rolling back is the safest option because: (1) it's instant, (2) it's tested (previous version was working), (3) we can re-deploy after fixing the validation. They agreed when I showed the data: rollback takes 2 minutes, live fix takes 10 minutes with unknown risk.

### Question 18: How do you ensure your monitoring actually catches real issues?
**STAR**: After the connection pool exhaustion (Lab 04), I reviewed all our alerts. We had 50 alerts but only 3 fired during the incident. I implemented "alert effectiveness" tracking: we tag every pager with "true positive," "false positive," or "missed." We aim for < 5% missed rate. After 6 months, we reduced alerts from 50 to 20 and eliminated all missed incidents.

### Question 19: Describe a time you had to debug a multi-threading issue.
**STAR**: The thread deadlock in the async framework (Lab 02) was one of the hardest bugs I've debugged. I captured 3 thread dumps over 30 seconds, compared the blocking threads, identified the lock ordering inversion, and fixed the inconsistent synchronization. I also added a deadlock detection health check that automatically captures thread dumps.

### Question 20: How do you stay calm during high-severity incidents?
**STAR**: During the Kafka consumer lag incident (Lab 13), the VP of Engineering was asking for updates every 30 seconds. I appointed the incident commander, delegated the VP's communication to the deputy IC, and focused only on the technical debugging. We resolved the issue in 15 minutes. Staying calm comes from having a clear incident command structure.

### Question 21: Describe a time you used A/B testing to validate a reliability change.
**STAR**: Before deploying the cache stampede fix (Lab 08), I ran an A/B test with 5% of users on the new caching code. The test showed p99 latency improved from 500ms to 200ms with zero cache stampedes in 24 hours. The control group had 4 stampede events. We rolled out to 100% the next day.

### Question 22: How do you balance moving fast with maintaining reliability as systems grow?
**STAR**: As Meta's infrastructure grew from 500M to 2B+ users, we had to shift from "move fast and break things" to "move fast with stable infra." I championed: (1) error budgets for every service, (2) canary deployments for every change, (3) automated rollbacks for every deployment.

### Question 23: Tell me about a time you contributed to Meta's internal tooling.
**STAR**: I built a Memcache stampede prevention library after Lab 08. It wraps the Memcache client with automatic `add()` lock acquisition, TTL jitter, and probabilistic early expiration. It was adopted by 10+ teams within 3 months.

### Question 24: How do you approach learning a new codebase when debugging?
**STAR**: When debugging the ranking service high CPU (Lab 03), I didn't know the codebase. My approach: (1) profile to find hot methods, (2) read the hot method's code, (3) trace back to understand data flow, (4) identify the unnecessary computation. This works for any unfamiliar codebase.

### Question 25: What metrics do you track for on-call health?
**STAR**: I track: (1) page frequency (alerts/hour), (2) MTTR, (3) MTTD (mean time to detect), (4) false positive rate, (5) toil percentage. After implementing the improvements from Lab 04 and Lab 06, page frequency dropped from 10/day to 2/day.

## Study Plan

### Priority Labs for Meta ProdEng
1. **Lab 03 (High CPU)** — Profiling and performance debugging
2. **Lab 04 (Connection Pool)** — Database reliability and pool management
3. **Lab 06 (Deployment Rollback)** — Config management and CI/CD
4. **Lab 08 (Cache Stampede)** — Distributed caching at scale
5. **Lab 13 (Kafka Consumer Lag)** — Stream processing at Meta scale
6. **Lab 02 (Thread Deadlock)** — Async framework internals
7. **Lab 09 (Disk Space)** — Log management at scale

### Recommended Schedule
- **Week 1**: Lab 03, Lab 08 (profiling + caching)
- **Week 2**: Lab 04, Lab 13 (database + Kafka)
- **Week 3**: Lab 06, Lab 02 (deployment + async)
- **Week 4**: Lab 09 + system design + behavioral
- **Week 5**: Mock interviews with debugging focus

### Lab Practice Checklist
- For each lab, practice: (1) reproduce the incident in a local environment, (2) identify the symptom without reading the solution, (3) write the fix, (4) verify the fix with tests, (5) write monitoring/alerts for the pattern
- Time yourself: each incident should take < 15 minutes from symptom to root cause
- Record yourself explaining your debugging process — check if you sound systematic or like you're guessing

## Tips

### Additional Meta-Specific Technical Concepts

**Social Graph Traversal**: Meta designs often involve traversing the social graph. Understand BFS vs DFS for friend-of-friend queries, fan-out strategies (push vs pull), and how to handle celebrity users.

**Data Tier Architecture**: Meta's data stack includes: (1) MySQL — primary persistent store, (2) Memcache — hot cache with 100TB+ capacity, (3) TAO — graph cache for social data, (4) RocksDB — embedded key-value store, (5) HBase — wide-column store for messaging.

**Live Configuration**: Meta uses ZooKeeper/Archaius for dynamic config. Every change is: (1) validated against schema, (2) pushed to canary first, (3) monitored for error rate increase, (4) auto-rollback on regression.

**Frontend Infrastructure**: Meta serves billions of requests through: (1) L7 load balancers (Proxygen), (2) Web server layer (HHVM), (3) Service layer (Thrift services), (4) Data layer (MySQL + Memcache + TAO).

### Meta ProdEng Interview Strategies
1. **Debugging is king**: Meta's ProdEng interviews heavily feature debugging broken code. Practice reading code line-by-line and identifying subtle bugs.
2. **Know Meta's scale**: Every answer should consider 2B+ users. "Normal scale" does not exist at Meta.
3. **Talk about real incidents**: Meta values candidates who share war stories from real production incidents.
4. **Move fast — but safely**: Show you know how to balance speed with reliability. Use feature flags, rollback plans, canary releases.
5. **Know the stack**: Memcache, TAO, MySQL at scale, Haystack (photos), Scribe (logging), Scuba (analytics), Liger (notifications).
6. **Performance is a feature**: Always think about CPU, memory, latency optimization. Meta has zero tolerance for inefficiency.
7. **Practice flame graph reading**: Meta expects you to analyze flame graphs in interviews. Know how to identify hot methods.
8. **Systematic debugging**: No guessing — follow a methodical approach: metrics → logs → profiling → hypothesis → test.
9. **Read code like a detective**: Meta debugging rounds give you broken code. Read it line by line, trace the logic, find the bug.
10. **"Move fast with stable infra" mentality**: Show you embrace speed but build safety nets (feature flags, gradual rollouts, auto-rollbacks).
11. **Practice debugging on the whiteboard**: Meta debugging rounds test your ability to find bugs in unfamiliar code. Practice by reading open-source code and finding potential issues.
12. **System design at Meta scale**: Every design answer should consider 2B+ users, multi-region deployment, and fault tolerance at every layer.
13. **Memcache Mastery**: Understand Memcache explicitly (not just Redis). Know: `get`, `set`, `add` (atomic CAS), `delete`, `lease` mechanism, slab allocator, eviction policies (LRU expiry).
14. **MySQL at Meta Scale**: Know: read replicas, sharding (by user_id), Delayed Replication for disaster recovery, Online Schema Change (OSC) via gh-ost, and connection pooling.
15. **Async Programming Models**: Meta uses Hack async (similar to C# async/await) and HHVM. Understand: event loop, fiber-based concurrency, blocking vs non-blocking, and how async functions are scheduled.
16. **Thrift RPC Framework**: Meta uses Thrift for all service-to-service communication. Know: (1) Thrift IDL for service definitions, (2) connection pooling in Thrift clients, (3) timeout and retry configuration, (4) load balancing via LxLB.
17. **ZooKeeper Usage**: Meta uses ZooKeeper for: (1) service discovery for infrastructure services, (2) distributed configuration management, (3) leader election for partition assignment, (4) distributed locking for resource coordination.

### Meta ProdEng Mock Interview Questions

Practice these questions before your interview:

**Coding Debugging Practice**:
```
function findDuplicates(arr) {
    let seen = new Set();
    let duplicates = new Set();
    for (let i = 0; i < arr.length; i++) {
        if (seen.has(arr[i])) {
            duplicates.add(arr[i]);
        }
        seen.add(arr[i]);
    }
    return Array.from(duplicates);
}
// Bug: Works correctly. Now try...
// function hasDuplicates(arr) { ... } — find the bug
```

**System Design Practice**:
- Design Instagram Stories with 500M DAU
- Design Facebook Messenger with 1B+ users
- Design real-time comments on Facebook Live with 10M concurrent viewers

**Behavioral Practice**:
- "Tell me about a time you dealt with a difficult incident"
- "How do you handle a team that doesn't prioritize reliability?"
