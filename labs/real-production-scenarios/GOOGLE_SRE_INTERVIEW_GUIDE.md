# Google SRE Interview Guide — Real Production Scenarios Academy

## Interview Process for SRE Roles

### Rounds
1. **Phone Screen (45 min)**: Resume deep-dive, basic systems knowledge, SRE philosophy, one coding problem (arrays/strings)
2. **Onsite (5 rounds, 45 min each)**:
   - **Algorithm & Data Structures**: LeetCode medium/hard, focus on distributed systems variants
   - **Systems Design**: Design a reliable distributed system (e.g., distributed key-value store, load balancer)
   - **SRE Technical**: Debugging production incidents, SLO/SLI/burn-rate calculations, incident response
   - **Coding for Reliability**: Write code with error handling, retries, circuit breakers — not just raw logic
   - **Behavioral (SRE-specific)**: SLO tradeoffs, incident command, blameless post-mortems, toil reduction

### SRE-Specific Expectations
- Error budgets and SLO culture is central — every design decision ties back to reliability targets
- Coding rounds focus on production-quality code with observability, retries, health checks
- Debugging rounds simulate real on-call scenarios with logs, metrics, traces
- Google values "systematic debugging" over intuition — follow the scientific method
- Must understand the Google Production Environment: Borg/Omega, Stubby (RPC framework), Monarch (monitoring), Chubby (locking)

### Round Breakdown
- Coding: 35% — must produce compilable, production-quality code
- Debugging/Incident: 35% — log analysis, RCA, remediation steps
- System Design: 20% — reliability-focused design
- Behavioral: 10% — SRE culture fit

## Top Incidents Aligned to Google SRE Focus

### Incident: Java Heap Space (Memory Leak) (Lab 01)
#### Problem Scenario
A critical microservice handling user profile data shows increasing heap usage over 72 hours. Pods are OOMKilled every 6 hours. GC logs show Full GCs running every 15 minutes with heap not reclaiming. Service P50 latency rises from 5ms to 200ms before OOM. SLO for p99 latency (500ms) is being violated. The burn rate is 2x the budget.

#### Interview Walkthrough
**Step 1 — Verify the symptom**: Check monitoring dashboards. Google uses Monarch. Plot heap usage vs time. Check if trend is linear or exponential. Linear suggests a slow leak (e.g., growing cache). Exponential suggests a fast leak (e.g., forgotten iterator).

**Step 2 — Gather data**: Run `jmap -heap <pid>` to check heap configuration. Run `jstat -gcutil <pid> 5s` to watch GC behavior. Look for Old Gen growing while Survivor spaces are underutilized. Use `jmap -histo:live <pid>` to find classes with most instances.

**Step 3 — Take a heap dump**: `jmap -dump:live,format=b,file=heap.hprof <pid>`. Analyze with Eclipse MAT or `jhat`. Look for:
   - Objects with `byte[]` or `char[]` dominating retained heap
   - Thread-local caches that never clear
   - Long-lived request scopes holding references

**Step 4 — Root cause**: A thread-local `HashMap<UUID, UserSession>` in a request filter never calls `remove()` after request completes. Each request adds a session entry but only clears on explicit logout. Since users rarely log out, entries accumulate.

**Step 5 — Remediate**: Fix the leak by adding a `finally` block to remove the session entry. Add a size-bounded `LinkedHashMap` with LRU eviction as a secondary measure.

**What Google evaluates**: Systematic elimination of hypotheses; use of monitoring data; understanding of JVM internals; knowing when to take a heap dump vs just restarting the pod.

#### Solution
```java
// Before: Memory leak
public class UserSessionFilter implements Filter {
    private static final ThreadLocal<Map<UUID, UserSession>> sessions =
        ThreadLocal.withInitial(HashMap::new);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        UUID sessionId = UUID.randomUUID();
        UserSession session = new UserSession(sessionId, extractUser(request));
        sessions.get().put(sessionId, session);
        try {
            chain.doFilter(request, response);
        } finally {
            sessions.get().remove(sessionId);  // FIX: Ensure cleanup
        }
    }
}
```

**Monitoring fix**: Create a custom Micrometer gauge `user_session_cache.size` to alert when cache exceeds threshold. Set SLO p99 latency < 500ms with error budget burn rate alert.

**Post-mortem**: Add a `remove()` call in `finally` block. Add bounded cache as defense-in-depth. Write integration test that simulates 10k concurrent requests and asserts heap stays stable.

#### Follow-ups
- **At Google scale**: Use a distributed session store (Spanner) instead of thread-local state. Use `com.google.common.cache.CacheBuilder` with maximum size and soft values.
- **What if the leak is in a dependency JAR?**: Wrap the problematic library and use `WeakReference` or SoftReference to prevent hard references. Add monitoring to detect the pattern automatically.
- **Burn-rate alerting**: Set a multi-window, multi-burn-rate alert (1h at 10x burn, 6h at 2x burn) to catch fast and slow leaks.

### Incident: Thread Deadlock (Lab 02)
#### Problem Scenario
A distributed lock service experiences sudden latency spikes. Thread dumps show 40 threads in `BLOCKED` state waiting on the same monitor. Service throughput drops to zero for 15 seconds before the health check kills and restarts the pod. This happens 3-4 times daily, usually during peak traffic.

#### Interview Walkthrough
**Step 1 — Capture thread dump**: `jstack -l <pid> > threaddump.txt`. Google's SREs would script this to auto-capture on latency spike. Look for locks in `BLOCKED` state.

**Step 2 — Analyze with `fastthread.io` or `jstack`**: Search for "waiting for" and "locked". The pattern shows `ThreadPoolExecutor worker` threads trying to acquire `ReentrantLock` on a shared resource while the thread holding the lock is itself waiting to submit a task to the same thread pool — a classic thread starvation deadlock.

**Step 3 — Confirm deadlock**: `jstack -l` output includes "Found one Java-level deadlock" with thread IDs and lock owners. In this case, the deadlock involves:
   - Thread A holds Lock1, waiting for Lock2
   - Thread B holds Lock2, waiting for Lock1

**Step 4 — Root cause**: A synchronous RPC call within a `CompletableFuture` chain uses `forkJoinPool.commonPool()`. The caller thread blocks waiting for the future result, but the future runs on the same pool. When all pool threads are blocked on the same call, no thread is available to complete the future — self-deadlock.

**Step 5 — Fix**: Use a separate thread pool for RPC calls with a bounded queue. Never block on asynchronous operations within the same pool.

#### Solution
```java
// Problem: self-deadlock in ForkJoinPool
public UserProfile fetchProfile(String userId) {
    CompletableFuture<Profile> future = CompletableFuture
        .supplyAsync(() -> userService.getProfile(userId));  // runs in commonPool
    return future.get();  // BLOCKS commonPool thread — DEADLOCK
}

// Fix: dedicated thread pool
private final ExecutorService rpcPool = Executors.newFixedThreadPool(32,
    new ThreadFactoryBuilder().setNameFormat("rpc-pool-%d").build());

public UserProfile fetchProfile(String userId) {
    CompletableFuture<Profile> future = CompletableFuture
        .supplyAsync(() -> userService.getProfile(userId), rpcPool);
    try {
        return future.get(5, TimeUnit.SECONDS);  // timeout to prevent hang
    } catch (TimeoutException e) {
        throw new RpcTimeoutException("Profile fetch timed out", e);
    }
}
```

**Post-mortem**: Add thread pool monitoring (active threads, queue depth, rejected tasks). Set alert for thread pool utilization > 80%. Add deadlock detection script that captures thread dump automatically on latency spike.

#### Follow-ups
- **At Google scale**: Use Borg health checks with liveness probes that detect deadlocked threads. Use `jstack` auto-collection integrated with Stackdriver (Google Cloud Ops).
- **Multiple resource deadlock**: Use lock ordering to prevent circular waits. Document lock hierarchy.
- **Monitor for deadlock-prone patterns**: Use Error Prone static analysis to detect `get()` on a `CompletableFuture` supplied to `commonPool()`.

### Incident: High CPU — Infinite Loop (Lab 03)
#### Problem Scenario
A recommendation engine service spikes to 100% CPU on one core. Service latency increases 10x. Auto-scaling kicks in and launches 3 more replicas, but all new replicas also spike to 100% CPU. The incident escalates to the SRE team within 5 minutes.

#### Interview Walkthrough
**Step 1 — Identify the hot thread**: Run `top -H -p <pid>` to find threads using the most CPU. Note the native thread ID.

**Step 2 — Convert to Java thread ID**: `printf "0x%x\n" <native_tid>` to get hex. Run `jstack <pid> | grep -A 30 "<hex>"` to find the thread stack.

**Step 3 — Analyze the stack**: The hot thread is in `RecommendationService.generateSuggestions()` calling `StringBuilder.append()` in a tight loop. The stack shows `StringBuilder` growing massive (triggering array copy) and `HashMap.computeIfAbsent()`.

**Step 4 — Root cause**: A `while` loop with a never-met termination condition. The code builds a response string in a loop, but a counter variable is shadowed — the loop variable `i` is re-declared inside, so the outer loop never advances.

**Step 5 — Fix**: Remove the shadowed variable declaration. Add a safety counter with a max iteration limit. Use profiler (async-profiler) to find hot methods.

**What Google evaluates**: Profiling skills; understanding of JIT compiler behavior; ability to read assembly output (`-XX:+PrintAssembly`) if needed; systematic approach to finding hot spots.

#### Solution
```java
// Before: infinite loop with shadowed variable
public String generateSuggestions(List<Item> items) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < items.size(); i++) {
        Item item = items.get(i);
        for (int j = 0; j < item.getSubItems().size(); j++) {  // j++, not i++
            sb.append(item.getSubItems().get(j).getName());
        }
    }
    return sb.toString();
}

// Fix: correct loop variable + guard
public String generateSuggestions(List<Item> items) {
    StringBuilder sb = new StringBuilder(1024);  // Pre-size
    int maxIterations = 1_000_000;
    int count = 0;
    for (int i = 0; i < items.size() && count < maxIterations; i++) {
        for (Item sub : items.get(i).getSubItems()) {
            sb.append(sub.getName());
            count++;
        }
    }
    return sb.toString();
}
```

#### Follow-ups
- **At Google scale**: Use `async-profiler` with `-e cpu` to generate flame graphs. Integrate with Google's profiler service for continuous profiling.
- **Prevention**: Add CPU profiling to CI/CD pipeline. Use static analysis to detect loop-bound shadowing. Add max-iteration guards in all production loops.

### Incident: Connection Pool Exhaustion (Lab 04)
#### Problem Scenario
A checkout service shows increasing checkout failures. Alerts fire for downstream timeout when connecting to the payment gateway's database. The service currently handles 500 TPS. Connection pool is configured at 100 connections.

#### Interview Walkthrough
**Step 1 — Check connection pool metrics**: HikariCP metrics show `Active: 100, Idle: 0, Pending: 45`. All connections are active.

**Step 2 — Check slow queries**: MySQL `SHOW PROCESSLIST` shows 80 threads in `Sleep` state with no query, 20 in `Query` state running slow `INSERT` statements.

**Step 3 — Root cause**: A transaction that opens a database connection but never closes it in a `finally` block. When the downstream service returns an error, the connection is leaked. Each request takes 1 connection. At 500 TPS with 5-second timeout, the pool (100) is saturated in 200ms.

**Step 4 — Fix**: Use try-with-resources. Add connection timeout to the pool (30s max lifetime). Implement connection leak detection.

#### Solution
```java
// Before: leaked connection
public void processPayment(Payment payment) {
    Connection conn = dataSource.getConnection();
    PreparedStatement stmt = conn.prepareStatement("INSERT INTO payments...");
    stmt.executeUpdate();
    // if exception thrown here, conn is never closed
}

// Fix: try-with-resources
public void processPayment(Payment payment) {
    String sql = "INSERT INTO payments (id, amount, status) VALUES (?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, payment.getId());
        stmt.setBigDecimal(2, payment.getAmount());
        stmt.setString(3, payment.getStatus());
        stmt.executeUpdate();
    }
}

// HikariCP configuration
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(100);
config.setConnectionTimeout(5000);
config.setMaxLifetime(600000);
config.setLeakDetectionThreshold(30000);  // 30s
```

#### Follow-ups
- **At Google scale**: Use Cloud Spanner with connection pooling via gRPC. Implement circuit breaker on pool exhaustion.
- **Metrics to monitor**: `hikaricp_connections_active`, `hikaricp_connections_pending`, `hikaricp_connections_timeout_total`.

### Incident: TLS Certificate Expiry (Lab 12)
#### Problem Scenario
A production alert fires at 3 AM: "TLS certificate for api.example.com expires in 7 days". The SRE on-call investigates which services depend on this certificate.

#### Interview Walkthrough
**Step 1 — Inventory dependencies**: Use `openssl s_client -connect api.example.com:443 -servername api.example.com 2>/dev/null | openssl x509 -noout -dates` to check expiry.

**Step 2 — Check all environments**: Run the same command across staging, canary, and production. Find that staging was already expired (silent failure — traffic was routed around it).

**Step 3 — Root cause**: The certificate was issued manually and never automated. No monitoring was set up for the new domain. The certificate management system didn't include `api.example.com`.

**Step 4 — Fix**: Renew and deploy. Automate with Let's Encrypt + cert-manager on Kubernetes. Set up monitoring at 30/14/7/3/1 days before expiry.

#### Solution
```bash
# Renewal check script
DOMAIN="api.example.com"
EXPIRY=$(openssl s_client -connect "$DOMAIN":443 -servername "$DOMAIN" </dev/null 2>/dev/null \
  | openssl x509 -noout -enddate 2>/dev/null \
  | cut -d= -f2)
EXPIRY_EPOCH=$(date -d "$EXPIRY" +%s)
NOW_EPOCH=$(date +%s)
DAYS_LEFT=$(( (EXPIRY_EPOCH - NOW_EPOCH) / 86400 ))

if [ "$DAYS_LEFT" -lt 7 ]; then
  echo "CRITICAL: $DOMAIN expires in $DAYS_LEFT days"
  exit 2
fi
```

#### Follow-ups
- **At Google scale**: Use Google-managed SSL certificates on GKE Ingress. Set up Certificate Transparency logging monitoring.
- **Automate renewals**: Use `cert-manager` with `ClusterIssuer` for Let's Encrypt.

### Incident: Cache Stampede (Lab 08)
#### Problem Scenario
A news feed service caches user timelines in Redis with 5-minute TTL. When the cache expires, all concurrent requests for the same key hit the database simultaneously, causing a 50x spike in database CPU to 100%.

#### Interview Walkthrough
**Step 1 — Identify the pattern**: Database CPU graph shows perfectly periodic spikes every 5 minutes. Redis `cache_hit_ratio` drops to 0% during spikes. Each spike lasts 30 seconds.

**Step 2 — Root cause**: Cache TTL is synchronized — all entries expire at the same time because they were written at the same time (cache warming after deploy). No mutual exclusion for cache regeneration.

**Step 3 — Fix**: Use jitter on TTL, implement "recompute stamp" (mutex), and use "stale-while-revalidate" pattern.

#### Solution
```java
public CompletableFuture<Timeline> getTimeline(String userId) {
    String key = "timeline:" + userId;
    Timeline cached = redis.get(key);
    if (cached != null) {
        return CompletableFuture.completedFuture(cached);
    }
    // Try to acquire lock for recomputation
    String lockKey = "lock:" + key;
    if (redis.setnx(lockKey, "1", Duration.ofSeconds(10))) {
        try {
            Timeline fresh = database.fetchTimeline(userId);
            int ttl = 300 + ThreadLocalRandom.current().nextInt(60);  // jitter
            redis.setex(key, ttl, fresh);
            return CompletableFuture.completedFuture(fresh);
        } finally {
            redis.del(lockKey);
        }
    }
    // Stale read fallback
    Timeline stale = redis.getStale(key);
    if (stale != null) return CompletableFuture.completedFuture(stale);
    // Wait briefly for the first request to complete
    Thread.sleep(50);
    return getTimeline(userId);
}
```

#### Follow-ups
- **At Google scale**: Use Google Cloud Memorystore (Redis) with read replicas. Implement "probabilistic early expiration" via XFetch algorithm.

### Incident: Slow Query — Missing Index (Lab 05)
#### Problem Scenario
A Google Cloud SQL instance for a user analytics service shows high CPU and disk IO. Queries that normally take 50ms now take 5 seconds. The analytics service returns 503 errors as connection pool is exhausted.

#### Interview Walkthrough
**Step 1 — Check slow query log**: Enable Google Cloud SQL's slow query logging. Run `gcloud sql instances describe analytics-db --format=json` to check `databaseFlags`. Enable `log_min_duration_statement = 1000`.

**Step 2 — Inspect the slow query**: Run `EXPLAIN ANALYZE` on the identified query:
```sql
EXPLAIN ANALYZE SELECT user_id, event_type, COUNT(*) 
FROM user_events 
WHERE created_at > NOW() - INTERVAL '7 days' 
  AND event_type = 'purchase'
GROUP BY user_id, event_type
ORDER BY COUNT(*) DESC;
```
The plan shows a Seq Scan on `user_events` (5M rows) with a Sort using `filesort` — no index.

**Step 3 — Root cause**: No composite index on `(event_type, created_at)`. The table grew to 5M rows, and without an index every query does a full table scan. The previous data size (100k rows) didn't need an index.

**Step 4 — Fix**: Add the missing index. Enable Query Insights in Google Cloud SQL.

#### Solution
```sql
CREATE INDEX idx_user_events_type_created 
ON user_events(event_type, created_at);

-- Verify with EXPLAIN
EXPLAIN SELECT user_id, event_type, COUNT(*) 
FROM user_events 
WHERE created_at > NOW() - INTERVAL '7 days' 
  AND event_type = 'purchase'
GROUP BY user_id, event_type;
-- Should show: Index Scan using idx_user_events_type_created
```

#### Follow-ups
- **At Google scale**: Use BigQuery for analytics workloads instead of OLTP database. Use Cloud SQL Query Insights for automatic index recommendations.
- **SRE metrics to monitor**: Track `cloudsql.googleapis.com/database/postgresql/num_backend` and `cloudsql.googleapis.com/database/postgresql/transaction_id` for connection saturation.

### Incident: Deployment Rollback — Canary Failure (Lab 06)
#### Problem Scenario
A canary deployment of a new version of the Google Ads serving stack shows a 5% increase in p99 latency after 2 minutes. The canary was rolled out to 2% of production traffic. The SRE on-call receives a burn-rate alert.

#### Interview Walkthrough
**Step 1 — Verify the canary metrics**: Compare the canary version metrics with baseline. The canary shows `p99_latency: 520ms` vs baseline `p99_latency: 480ms`. The SLO is 500ms p99.

**Step 2 — Check error budget**: The burn rate is 8x over the 1-hour window. The error budget will be exhausted in 7.5 minutes at this rate.

**Step 3 — Root cause**: The new version changed the RPC timeout from 500ms to 1000ms for a downstream dependency. The dependency has a 450ms p99 latency normally, but under the higher timeout it experiences more queuing, increasing latency.

**Step 4 — Immediate fix**: Rollback the canary. `gcloud app versions stop v2-canary --service ads-serving`.

**Step 5 — Long-term fix**: Revert the timeout change. Use adaptive timeouts based on dependency health.

**What Google evaluates**: Canary analysis; burn-rate monitoring; error budget management; rollback decision speed.

#### Solution
```bash
# Rollback canary version
gcloud app versions stop v2-canary --service ads-serving
gcloud app versions migrate v1 --service ads-serving

# Verify rollback
gcloud app versions list --service ads-serving --filter="traffic_split=1.0"

# Set up automatic rollback via Cloud Monitoring
gcloud alpha monitoring policies create \
  --display-name="Canary Auto-Rollback" \
  --condition-filter='resource.type="gae_app" AND metric.type="custom.googleapis.com/opencensus/p99_latency"' \
  --condition-threshold-value=500 \
  --condition-threshold-duration=120s \
  --alert-trigger-count=1
```

#### Follow-ups
- **At Google scale**: Use progressive delivery with SLO-based promotions. Canary at 1% → 2% → 5% → 10% → 25% → 50% → 100% with burn-rate validation at each step.
- **SRE metric**: Track deployment velocity and rollback rate as a toil metric.

### Incident: Disk Space Full — GCE Persistent Disk (Lab 09)
#### Problem Scenario
A Google Compute Engine VM running a logging aggregator shows disk utilization at 100%. The VM is attached to a 500GB persistent disk. Logs are being dropped, and the on-call engineer is paged.

#### Interview Walkthrough
**Step 1 — Check disk**: `df -h` shows `/mnt/logs` is 100% full. `du -sh /mnt/logs/* | sort -rh | head -5` shows `structured_logs/` consuming 450GB.

**Step 2 — Check log retention**: `ls /mnt/logs/structured_logs/ | wc -l` — 90 days of logs. The retention policy is 30 days.

**Step 3 — Root cause**: A configuration change accidentally set log retention to 90 days instead of 30. The disk was sized for 30 days of logs (500GB / 30 days = ~16GB/day). At 90 days, the disk fills up.

**Step 4 — Immediate fix**: Remove old logs: `find /mnt/logs/structured_logs/ -mtime +30 -delete`.

**Step 5 — Long-term fix**: Restore retention configuration. Add disk usage monitoring at 80%. Use Cloud Logging for central log storage instead of local disk.

**What Google evaluates**: Disk management; retention policies; monitoring gaps; fix vs automation.

#### Solution
```bash
# Immediate cleanup — remove logs older than 30 days
find /mnt/logs/structured_logs/ -name "*.log" -mtime +30 -delete

# Verify freed space
df -h /mnt/logs

# Set up log retention cron job
cat > /etc/cron.daily/log-cleanup << 'EOF'
#!/bin/bash
find /mnt/logs/structured_logs/ -name "*.log" -mtime +30 -delete
EOF
chmod +x /etc/cron.daily/log-cleanup

# Restore log retention config
sed -i 's/log_retention_days: 90/log_retention_days: 30/' /etc/logging-agent/config.yaml
systemctl restart logging-agent
```

#### Follow-ups
- **At Google scale**: Use Cloud Logging with log sinks to BigQuery. Set up log retention via Log Router. Use Persistent Disk snapshots for disaster recovery.
- **Monitoring**: Set up Cloud Monitoring disk alert at 80% and 90% utilization with escalation paths.

## System Design for Reliability

### Design Question 1: Globally Distributed Session Store
Design a reliable session store that survives datacenter failures with strong consistency within a region and eventual consistency across regions. How do you handle the SLO tradeoff between latency and consistency? Walk through error budget calculation at Google's scale.

**Key points**: Use Spanner for strong consistency within region (Paxos-based replication). Use asynchronous replication across regions for availability. Calculate error budget based on p99 latency SLO. Multi-window burn-rate alerting for early detection.

### Design Question 2: Reliable Pub/Sub with Exactly-Once Semantics
Design a pub/sub system for Google Ads that guarantees exactly-once delivery at 1M messages/second. Discuss how you handle broker failures, consumer lag, and dead-letter queues.

**Key points**: Use Google Cloud Pub/Sub with pull subscriptions. Implement idempotent consumers with deduplication keys. Use dead-letter topics with configurable retry policies. Monitor with `subscription/backlog_bytes` and `subscription/oldest_unacked_message_age`.

### Design Question 3: Capacity Planning for a Regional Outage
Design the failover mechanism when a Google Cloud region goes down. How do you ensure the SLO is not violated? Calculate how much spare capacity you need in the remaining regions.

**Key points**: Use N+2 redundancy across regions. Each region runs at 60% capacity. Load balance with Google Cloud Load Balancer with health checks. Pre-warm clusters in failover regions. Calculate spare capacity as 1/(N-2) where N is total regions.

## Incident Command Behavioral

### Question 1: Tell me about a time you handled a production outage.
**STAR**: During a TLS expiry incident (Lab 12), I was IC. I identified the root cause within 5 minutes using `openssl`, coordinated with the security team for emergency renewal, and wrote a runbook within 2 hours. I then led the post-mortem where we automated renewals with cert-manager.

### Question 2: How do you balance feature velocity vs reliability?
**STAR**: A team wanted to deploy an experimental recommendation model that risked 5% SLO violation. Using error budget analysis, I negotiated a 2% allocation for experimentation, with an automatic rollback mechanism at 3% budget burn.

### Question 3: Describe a time you reduced toil.
**STAR**: A weekly manual certificate renewal process (Lab 12) was toil. I automated it with cert-manager and saved the team 4 hours/week, documented in a post-mortem.

### Question 4: How do you handle conflicting priorities during an incident?
**STAR**: During Lab 04 (connection pool exhaustion), the product team wanted to restart all services, but I argued for targeted fix of the leaking transaction. I explained the risk of restarting all services (lost in-flight transactions), and we applied the targeted fix first.

### Question 5: Tell me about a blameless post-mortem you led.
**STAR**: After a cache stampede (Lab 08), I led a blameless post-mortem. Instead of blaming the engineer who set the TTL, we identified the system gap — no cache regeneration locking — and added it as a standard pattern.

### Question 6: Describe a time you had to make a tradeoff between reliability and cost.
**STAR**: An analytics team wanted full replication across 5 regions for a non-critical service. I calculated the cost (3x infrastructure) vs reliability gain. We agreed on 3 regions with active-active and 2 regions as cold standby, saving 40% cost while maintaining the SLO.

### Question 7: How do you train other engineers on on-call practices?
**STAR**: I created an on-call onboarding program using the Prod Scenarios Academy labs. New SREs shadow senior engineers for Lab 01 (memory leak) and Lab 02 (deadlock) before taking primary on-call. This reduced incident MTTR for new hires by 60%.

### Question 8: How do you handle on-call fatigue?
**STAR**: After a particularly rough on-call week with 3 incidents, I analyzed our page volume and found 40% were noise (auto-recovered before engineer responded). I worked with the team to tune alert sensitivity, add auto-remediation for known patterns, and implement a tiered escalation (primary → secondary → escalation) to distribute load.

### Question 9: Describe a time you designed a system for reliability from scratch.
**STAR**: I designed a new session management service with redundancy, circuit breakers, and automatic failover. I used Spanner for multi-region replication and set up multi-window burn-rate alerts. The service has maintained 99.99% availability for 18 months.

### Question 10: How do you prioritize which reliability improvements to make?
**STAR**: I use error budget analysis to prioritize. Services with < 10% error budget remaining get immediate attention. The memory leak (Lab 01) was prioritized because it consumed 15% of error budget in 1 week. I presented the data to stakeholders and secured a 2-week reliability sprint.

### Question 11: Describe a situation where you had to work with a difficult team during an incident.
**STAR**: During a cross-team incident involving a shared database (Lab 04), the other team's SRE kept restarting the database without coordinating. I established an IC structure with clear communication channels and assigned the other SRE a specific task (monitoring replica lag). The incident resolved faster once roles were clear.

### Question 12: How do you handle SLO violations for a service you own?
**STAR**: When our service violated the p99 latency SLO (500ms → 620ms) for 2 consecutive weeks, I declared a "reliability sprint." We analyzed the top contributors (memory leak Lab 01, slow query Lab 05, cache miss rate) and fixed all three within 2 weeks. SLO compliance recovered to 100%.

### Question 13: Tell me about a time you designed an on-call rotation.
**STAR**: I redesigned our on-call rotation to follow Google's SRE model: primary (12h shifts), secondary (24h shifts in a different timezone), and escalation (manager). We also added a "learning on-call" track where junior SREs shadow seniors before taking primary.

### Question 14: How do you measure toil in your team?
**STAR**: I track toil via: time spent on manual operations (pager responses, manual deployments, manual data recovery). We target < 50% toil. When toil exceeded 50%, we automated certificate renewals (Lab 12) and log cleanup (Lab 09), reducing toil to 30%.

### Question 15: Describe your experience with blameless post-mortems.
**STAR**: After the cache stampede (Lab 08), I led a blameless post-mortem. We identified that the root cause was the lack of cache regeneration locking — a system gap, not a people error. We added the locking pattern and created a "Production Ready Cache" checklist used across all 15 microservices.

## Study Plan

### Priority Labs for Google SRE
1. **Lab 01 (Memory Leak)** — Most common Java SRE incident
2. **Lab 02 (Thread Deadlock)** — Tests concurrency understanding
3. **Lab 03 (High CPU)** — Profiling skills are critical
4. **Lab 04 (Connection Pool)** — Database reliability is core
5. **Lab 05 (Slow Query)** — Database performance tuning
6. **Lab 08 (Cache Stampede)** — Distributed systems thinking
7. **Lab 12 (TLS Expiry)** — Infrastructure reliability

### Recommended Schedule
- **Week 1-2**: Labs 01, 02, 03 (deep debugging fundamentals)
- **Week 3**: Labs 04, 05, 08 (database, caching, distributed systems)
- **Week 4**: Lab 12 + SRE system design + error budget math
- **Week 5**: Behavioral preparation + mock interviews with incident command scenarios

### Lab Deep-Dive Checklist
- For each lab, practice: (1) identify symptom, (2) gather data, (3) form hypothesis, (4) test hypothesis, (5) fix, (6) monitor, (7) post-mortem
- Time yourself: each incident should take < 15 minutes to debug in an interview setting
- Practice explaining each step out loud — Google wants to hear your thought process

## Tips

### Google SRE Interview Strategies
1. **Know the Google SRE book**: Be fluent in SLOs, error budgets, toil, and blameless post-mortems. Read the Google SRE book thoroughly before the interview.
2. **Practice systematic debugging**: Google wants to see your thought process, not just the answer. Narrate every step in a scientific-method style: observe, hypothesize, test, conclude.
3. **Production code matters**: In coding rounds, add retries, timeouts, health checks — not just algorithm. Write code as if it runs at Google scale.
4. **SRE behaviors**: Practice incident command scenarios; Google uses IC/Deputy IC framework. Know the roles: IC, Deputy IC, Scribe, SME, Comms Lead.
5. **Study Google-specific tools**: Borg, Monarch, Stubby, Spanner, Bigtable, Chubby, Colossus concepts. Even if you haven't used them, understand the design philosophy.
6. **Expect SLO math**: Be ready to calculate error budgets, burn rates, and multi-window alerts. Practice the formula: Error Budget = 1 - SLO target. Burn Rate = error rate / (1 - SLO target).
7. **Blameless culture**: Never blame individuals in behavioral answers; focus on system improvements. Google's post-mortem philosophy is that bugs are system failures, not individual failures.
8. **Know your SRE levels**: SRE I vs II vs III — understand the scope expectations for the role you're applying for. SRE II should lead incident response; SRE III should design systems.
9. **Automation mindset**: Every manual process is toil. Show that your first instinct is to automate. For every incident fix, include how you'd prevent recurrence through automation.
10. **Toil reduction**: Google defines toil as work that is manual, repetitive, automatable, tactical, and devoid of enduring value. Show that you actively identify and eliminate toil.
11. **Practice SLO math**: Work through 10-15 error budget and burn rate calculation problems before your interview. Understand the difference between multi-window, multi-burn-rate alerts and simple threshold alerts.
12. **Leadership principles for SRE**: Google values "Technical Leadership" for SRE roles — show you can lead incident response, drive reliability improvements, and mentor other SREs.
