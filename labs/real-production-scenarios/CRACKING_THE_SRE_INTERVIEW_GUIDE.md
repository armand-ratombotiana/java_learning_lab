# CRACKING THE SRE INTERVIEW GUIDE

## Google SRE Interview Process

### Phone Screen (45 min)
- Technical phone screen with an SRE manager or senior engineer
- Topics: Linux, networking, basic coding (Python/Go/Java), system design fundamentals
- Typical question: "Design a load balancer" or "Debug a slow web server"
- **Pass criteria:** Clear communication, systematic debugging approach, distributed systems awareness

### On-Site Interview (4-5 rounds × 45 min)

| Round | Focus | Example Question |
|-------|-------|-----------------|
| System Design | Distributed systems, failure modes | "Design a globally distributed key-value store" |
| Troubleshooting | Debug a broken system given symptoms | "P99 latency jumped from 10ms to 5s — what do you check?" |
| Coding/Algorithm | Data structures, automation | "Write a rate limiter" or "Implement a consistent hash ring" |
| Behavioral/Leadership | Past incidents, team dynamics | "Tell me about a time you resolved a SEV1 incident" |
| SRE Technical Deep Dive | SRE-specific concepts | "How would you set SLOs for a new service?" |

### Team Match (30-45 min)
- Less technical, more about team fit
- Discuss what SRE teams you're interested in
- Be prepared: "What kind of on-call rotation do you want?" "What scale of systems excite you?"

### What Google SRE Evaluates
1. **Technical competence** — Can you solve the problem?
2. **Communication** — Do you explain clearly with appropriate detail?
3. **Collaboration** — Do you ask clarifying questions?
4. **Leadership** — Do you drive the conversation?
5. **Adaptability** — How do you handle curveballs?

### Key Resources for Google SRE Prep
- Google SRE Book (free): https://sre.google/sre-book/table-of-contents/
- Google SRE Workbook: https://sre.google/workbook/table-of-contents/
- System design interview prep books (DDIA, Alex Xu)
- LeetCode Medium/Hard (focus on concurrency, distributed systems)

---

## Amazon AWS Interview Process

### Leadership Principles (LPs) — The Core of Every Interview
Amazon evaluates every answer against its Leadership Principles. For SRE/DevOps, key LPs:

| LP | How to Demonstrate in SRE Context |
|----|-----------------------------------|
| **Customer Obsession** | "I minimized customer impact by rolling back within 3 minutes" |
| **Ownership** | "I owned the incident from page to post-mortem, including follow-up actions" |
| **Dive Deep** | "I traced the root cause through 5 layers: network -> OS -> JVM -> app -> config" |
| **Deliver Results** | "I reduced MTTR from 45 min to 8 min by building an automated runbook" |
| **Have Backbone; Disagree and Commit** | "I disagreed with the senior architect's approach, but committed once a decision was made" |
| **Learn and Be Curious** | "After the incident, I learned Chaos Engineering and ran a Game Day" |
| **Insist on the Highest Standards** | "I added production-readiness checks that caught 3 pre-prod issues" |
| **Think Big** | "I proposed a multi-region active-active architecture" |
| **Bias for Action** | "I rolled back the deployment within 2 minutes of seeing the error spike" |
| **Frugality** | "I optimized our AWS spend by 40% through right-sizing and reserved instances" |

### Interview Rounds

1. **Phone Screen (60 min):** LP + technical questions, usually focused on networking or Linux
2. **On-site Loop (5 rounds):**
   - **System Design (60 min)** — Design a highly available system on AWS
   - **Troubleshooting/Deep Dive (60 min)** — Past incident deep dive + hypothetical
   - **Coding (60 min)** — Algorithm + production scripting
   - **Bar Raiser (60 min)** — LP-heavy, looks for "Amazonian" traits
   - **Manager Round (45 min)** — Team fit, career, ownership

### Bar Raiser Role
- An experienced Amazonian who has veto power over hiring decisions
- Evaluates whether you raise the bar for the team
- Usually asks the hardest LP questions
- Looks for: long-term thinking, ability to say no, high standards

### STAR Method for Amazon
```
Situation:  "Our payment gateway was timing out on 30% of transactions during Black Friday"
Task:       "I was the primary on-call SRE responsible for payment platform reliability"
Action:     "1) Paged within 2 min. 2) Checked dashboards — connection pool exhausted.
             3) Found a deployment 10 min prior added a DB call without connection management.
             4) Rolled back in 3 min. 5) Added try-with-resources. 6) Load-tested to verify."
Result:     "Error rate dropped from 30% to 0.1%. Post-mortem led to mandatory linting.
             No recurrence in 6 months. MTTR reduced by 80%."
```

### Amazon SRE Technical Focus Areas
- **AWS Services:** EC2, ELB, Route53, S3, RDS, DynamoDB, CloudWatch, Lambda
- **Well-Architected Framework:** Operational Excellence, Reliability, Performance, Security, Cost
- **Scaling:** Auto Scaling groups, ELB, read replicas, caching (ElastiCache)
- **Deployment:** CodeDeploy, CodePipeline, canary deployments, feature flags

---

## Microsoft Azure Interview Process

### Interview Structure

1. **Phone Screen (45-60 min):** Recruiter + technical screen with a senior engineer
2. **On-site (4-5 rounds):**
   - **System Design (45 min)** — Azure-specific architecture
   - **Techno-Functional Round (45 min)** — Both technical + product understanding
   - **Behavioral/Leadership (45 min)** — Microsoft competencies
   - **Role-Specific Deep Dive (45 min)** — Based on the specific role
   - **ASAP/ASAPE (45 min)** — Microsoft's Bar Raiser equivalent

### Microsoft Competencies (What They Evaluate)
| Competency | What They Look For |
|------------|-------------------|
| **Customer Focus** | Understanding of customer impact, empathy |
| **Technical Excellence** | Deep knowledge, continuous learning |
| **Collaboration** | Cross-team work, influence without authority |
| **Adaptability** | Handling ambiguity, changing priorities |
| **Growth Mindset** | Learning from failures, feedback receptiveness |

### Azure-Specific Technical Topics
- **Azure Well-Architected Framework** (reliability, security, cost optimization, operational excellence, performance efficiency)
- **Azure Monitor, Log Analytics, Application Insights** — monitoring and observability
- **Azure DevOps** — CI/CD pipelines, release gates, rollback strategies
- **ARM/Bicep/Terraform** — Infrastructure as Code
- **Azure Kubernetes Service (AKS)** — container orchestration
- **Azure SQL, Cosmos DB** — database performance troubleshooting
- **Azure Functions, Logic Apps** — serverless
- **Azure Networking** — VNet, ExpressRoute, Traffic Manager, Load Balancer

### Sample Azure Interview Question
> "A critical Azure App Service becomes unresponsive after a deployment. Walk through your response."

Response framework:
1. Check Azure Monitor metrics (CPU, memory, requests, errors)
2. Review Application Insights for telemetry and dependencies
3. Check deployment slots — swap back to previous slot
4. Enable diagnostic logs if not already enabled
5. Scale out the App Service Plan
6. Check Kudu for process dumps and log streaming
7. Review Azure SQL DTU utilization if app uses SQL
8. Check recent deployment changes via Azure DevOps
9. Review Azure Advisor recommendations

---

## Netflix SRE Interview Process

### Culture First — "Freedom & Responsibility"
Netflix's culture deck is famous. For SRE roles, they look for:

| Principle | SRE Application |
|-----------|----------------|
| **Judgment** | Knowing when to automate vs. when to manual-fix |
| **Communication** | Clear incident updates, post-mortem writing |
| **Impact** | Reducing toil, improving reliability |
| **Curiosity** | Chaos engineering, learning new tech |
| **Innovation** | Building tools, automating toil |
| **Courage** | Saying "no" to unreliable features |
| **Passion** | Caring deeply about production systems |
| **Honesty** | Blameless post-mortems, sharing failures |
| **Selflessness** | Helping teammates during incidents |

### Interview Rounds

1. **Phone Screen (45 min):** Culture fit + technical basics
2. **Technical Phone (60 min):** System design or coding focused on distributed systems
3. **On-site (4-5 rounds):**
   - **System Design** — Netflix specific (e.g., "Design the Netflix API gateway")
   - **Debugging/Chaos Engineering** — Walk through a chaos experiment
   - **Coding** — Algorithms, concurrency, automation
   - **Cultural Interview** — "Tell me about a time you disagreed with your manager"
   - **SRE Deep Dive** — SLOs, error budgets, capacity planning

### Netflix SRE Technical Focus
- **Chaos Engineering:** Chaos Monkey, Chaos Kong, Litmus
- **Cloud-native:** AWS multi-region, microservices, CDN
- **Streaming:** Video encoding, CDN, adaptive bitrate
- **Java/Spring Boot:** Microservices ecosystem
- **Hystrix/Resilience4j:** Circuit breakers, bulkheads
- **Zuul/Eureka:** API gateway, service discovery
- **Cassandra, EVCache:** NoSQL, caching

---

## Apple Reliability Engineering Interview

### Privacy-First, Hardware-Software Integration
Apple interviews focus on reliability at the intersection of hardware and software, with strong emphasis on privacy and security.

### Interview Rounds
1. **Phone Screen (45 min):** Technical + behavioral
2. **On-site (5-6 rounds):**
   - **System Design** — e.g., "Design Apple Push Notification Service at scale"
   - **Debugging** — e.g., "APNS is failing delivery — what do you check?"
   - **Coding** — Swift, Objective-C, or Go algorithms
   - **Hardware-Software Integration** — e.g., "How would you test a new sensor?"
   - **Behavioral** — Apple values (privacy, design, quality)
   - **Manager Round** — Team fit, vision

### Apple SRE Technical Focus
- **APNS:** Push notification architecture, certificate management
- **iCloud:** Distributed storage, sync, conflict resolution
- **Privacy Engineering:** Differential privacy, on-device processing, minimal data collection
- **Performance:** Battery life vs. performance tradeoffs
- **Hardware integration:** Testing across device types, OS versions
- **Security:** Secure enclave, encryption key management

### Sample Apple Question
> "Apple Push Notification Service is failing to deliver notifications to iOS devices. How do you triage?"

1. Is it all devices or specific versions (iOS, watchOS)?
2. Check APNS certificate expiry — #1 cause
3. Check TLS termination and certificate trust chain
4. Verify token database is not corrupted
5. Check feedback service for expired tokens
6. Examine push queue depth and consumer lag
7. Check device-side connectivity (carrier networks)
8. Test with a known-good device in the same region

---

## Oracle Enterprise Support Interview

### Deep Technical Troubleshooting + Communication
Oracle values deep database and infrastructure knowledge combined with customer communication skills.

### Interview Rounds
1. **Phone Screen (45 min):** Database fundamentals, Linux
2. **Technical Round (60 min):** Deep Oracle database troubleshooting
3. **Communication Round (45 min):** Mock customer call — walk through a production issue
4. **Manager Round (45 min):** Enterprise support experience, escalation handling

### Oracle Technical Focus
- **Oracle Database:** RAC, ASM, Data Guard, RMAN, AWR, ADR
- **Performance Tuning:** SQL tuning, indexing, optimizer statistics
- **Backup & Recovery:** Point-in-time recovery, Flashback, Data Pump
- **Exadata:** Smart Scan, Storage Indexes, HCC
- **Enterprise Support:** Severity management, escalation, customer management
- **PL/SQL:** Batch processing, performance optimization

### Sample Oracle Question
> "An end-of-day financial batch job that normally takes 1 hour now takes 8 hours. Diagnose."

1. Check if previous run failed — cleanup issue?
2. Compare AWR reports between normal and slow runs
3. Look for execution plan changes (plan regression)
4. Check for stale optimizer statistics
5. Look for missing indexes on recently modified tables
6. Check for blocking locks from other sessions
7. Examine temp tablespace usage (hash join spill)
8. Check for I/O contention or storage degradation

---

## System Design for SRE: Designing Reliable Systems

### Key Design Tenets

| Tenet | Question to Ask |
|-------|----------------|
| **Failure is inevitable** | "What happens when every component fails?" |
| **Defense in depth** | "What's the second line of defense?" |
| **Tradeoffs** | "Consistency vs. availability? Cost vs. reliability?" |
| **Bounded complexity** | "Can a single engineer understand this system?" |
| **Observability by default** | "Can we detect and diagnose every failure mode?" |

### System Design Framework for SRE

1. **Understand requirements**
   - Functional: What does the system do?
   - Non-functional: Latency P99 < 100ms, 99.99% availability, 10K req/s
   - Constraints: Budget, team size, timeline

2. **High-level design**
   - DNS → Load Balancer → Web Servers → App Servers → Database
   - Add caching layer (CDN, Redis/Memcached)
   - Add queue for async processing

3. **Failure mode analysis**
   - Database failure → Read replicas, multi-AZ, backup
   - Cache failure → Circuit breaker, stale data serving
   - Load balancer failure → DNS failover, health checks
   - Network partition → Fallback to local processing

4. **Reliability mechanisms**
   - Retry with exponential backoff + jitter
   - Circuit breakers (Hystrix, Resilience4j)
   - Bulkheads (thread pool isolation)
   - Timeouts (connect, read, write)
   - Rate limiting (token bucket, sliding window)
   - Graceful degradation

5. **Observability**
   - Metrics: RED (Rate, Errors, Duration) or Golden Signals
   - Logs: Structured logging, centralized (ELK, Loki)
   - Traces: Distributed tracing (Jaeger, Zipkin, OpenTelemetry)

6. **Operational readiness**
   - Runbooks for every failure mode
   - Chaos engineering experiments
   - Load testing with production traffic patterns
   - Deployment automation with canary + rollback

### Common System Design Questions for SRE

| Question | What They Test |
|----------|----------------|
| "Design a global load balancer" | DNS, anycast, L4/L7 LB, health checks, failover |
| "Design a monitoring system for 10K services" | Metrics pipeline, aggregation, alerting, deduplication |
| "Design a deployment system" | Canary, blue-green, feature flags, rollback |
| "Design a logging pipeline for 100TB/day" | Collection, buffering, indexing, retention, search |
| "Design a rate limiter" | Token bucket, sliding window, distributed vs. local |
| "Design a distributed caching system" | Consistent hashing, replication, failure handling |
| "Design an incident management system" | Alert routing, escalation, on-call scheduling |

---

## Incident Management Interview Questions

### Common Questions

| Question | What They Test |
|----------|----------------|
| "Walk me through your incident response process" | Do you have a structured approach? |
| "Tell me about the worst SEV1 you've handled" | Can you handle pressure? What did you learn? |
| "How do you communicate during an incident?" | Stakeholder management, clarity under pressure |
| "How do you decide when to roll back vs. fix forward?" | Risk assessment, time-to-resolve vs. time-to-mitigate |
| "How do you run a post-mortem?" | Blameless culture, action-oriented, thorough |
| "How do you improve incident response over time?" | Automation, runbooks, chaos engineering, game days |
| "How do you handle multiple simultaneous incidents?" | Prioritization, delegation, escalation |

### STAR Stories to Prepare

Prepare 5 incident stories:

1. **Memory/performance issue** — Memory leak, CPU spike, thread deadlock
2. **Database issue** — Connection pool exhaustion, slow query, deadlock
3. **Network issue** — DNS failure, TLS certificate, DDoS
4. **Deployment failure** — Bad deploy, rollback, feature flag save
5. **Security incident** — Breach, unauthorized access, DDoS

### The Incident Commander (IC) Role

Interviewers often ask you to play IC in a hypothetical scenario:

**What ICs do:**
- Triage severity and impact
- Assign roles (Ops Lead, Comms Lead, Scribe)
- Make escalation decisions
- Communicate to stakeholders
- Ensure the team stays focused on mitigation

**IC Pitfalls to avoid:**
- Getting into debugging yourself (stay at command level)
- Not communicating early/often
- Failing to escalate when stuck
- Ignoring the scribe/timeline
- Not asking for help

---

## Automation and Tooling Questions

### Common Questions

| Question | What They Test |
|----------|----------------|
| "How would you automate a manual deployment process?" | CI/CD pipeline design, testing strategy |
| "How do you determine what to automate?" | Toil reduction, frequency, reliability |
| "Design a tool to auto-remediate a database connection pool exhaustion" | Monitoring → Detection → Mitigation → Verification |
| "How do you ensure automation doesn't make things worse?" | Safety checks, gradual rollout, kill switch |
| "Design a self-healing system for a web service" | Health checks, auto-restart, traffic drain, auto-scale |

### Automation Frameworks

**Watch → Decide → Act loop:**
1. **Watch:** Monitoring detects anomaly (e.g., error rate > 1%)
2. **Decide:** Rule engine evaluates severity and selects action
3. **Act:** Execute mitigation (rollback, restart, scale up)
4. **Verify:** Confirm resolution, or escalate if unsuccessful

**Safety patterns for automation:**
- Canary execution (apply to 1% first)
- Human-in-the-loop for critical actions
- Rate limiting on automatic actions
- Circuit breaker on auto-remediation (stop if worsening)
- Full audit trail of every automated action

---

## Capacity Planning and Forecasting Questions

### Common Questions

| Question | What They Test |
|----------|----------------|
| "How do you estimate capacity for a service growing 10x?" | Trend analysis, modeling, headroom |
| "How do you know when to scale up vs. scale out?" | Vertical vs. horizontal scaling tradeoffs |
| "How do you forecast resource needs for a new feature?" | Load testing, traffic estimation, safety margin |
| "What metrics matter for capacity planning?" | CPU, memory, disk, network, connection pools |
| "How do you handle seasonal traffic spikes?" | Pre-scaling, auto-scaling, throttling, queuing |

### Capacity Planning Framework

1. **Understand current utilization**
   - Measure peak utilization per component
   - Identify bottlenecks (CPU, memory, I/O, network)
   - Track growth rate (daily, weekly, monthly)

2. **Project future growth**
   - Business growth (users, transactions)
   - Feature growth (new features, increased usage)
   - Seasonal patterns (Black Friday, back to school)
   - Headroom requirement (e.g., 30% buffer for failover)

3. **Determine scaling strategy**
   - When to scale up (bigger instances)
   - When to scale out (more instances)
   - Auto-scaling thresholds and cooldown
   - Capacity reservation vs. on-demand

4. **Plan for failure**
   - N+1 redundancy for every component
   - Multi-region failover capacity
   - Degraded mode capacity (serve at lower quality)

### Capacity Planning Formulas

| Formula | Description |
|---------|-------------|
| `peak_req_per_sec × avg_response_time` | Concurrent connections needed |
| `total_requests_per_day / (24 × 3600 × peak_to_average_ratio)` | Peak throughput |
| `(current_capacity - used_capacity) / growth_rate` | Time until capacity exhaustion |
| `target_utilization × max_capacity` | Recommended provisioned capacity |

---

## 30/60/90 Day Study Plans

### Plan A: For Entry-Level SRE (0-2 years)

| Period | Focus | Activities |
|--------|-------|-----------|
| **Days 1-30** | Fundamentals | Read Google SRE Book ch1-10, learn Linux basics, practice Python/Go, understand HTTP/TCP |
| **Days 31-60** | Deep Dive | Read Google SRE Book ch11-20, Google SRE Workbook ch1-5, practice system design, set up monitoring (Prometheus + Grafana) |
| **Days 61-90** | Interview Prep | Mock interviews, practice 5 STAR stories, LeetCode Medium, system design practice, review weaknesses |

### Plan B: For Mid-Level SRE (2-5 years)

| Period | Focus | Activities |
|--------|-------|-----------|
| **Days 1-30** | Refresh Fundamentals | Google SRE Book, AWS Well-Architected Framework, Kubernetes basics, Celery/Distributed Systems |
| **Days 31-60** | Advanced Topics | Capacity planning, chaos engineering, incident command, advanced system design (distributed consensus, consistent hashing) |
| **Days 61-90** | Mock Interviews | 10+ mock interviews, practice with peers, study at https://pramp.com, refine STAR stories |

### Plan C: For Senior/SRE (5+ years)

| Period | Focus | Activities |
|--------|-------|-----------|
| **Days 1-30** | Advanced Distributed Systems | DDIA book, Paxos/Raft, CRDTs, advanced K8s patterns, Chaos Engineering |
| **Days 31-60** | Strategic Topics | Error budget policy design, SLO framework creation, organizational change, incident command leadership |
| **Days 61-90** | Leadership & Communication | Practice leading a mock incident, mentor junior engineers in interview prep, prepare 10+ detailed incident stories |

### Daily Study Schedule (Sample)

```
Weekdays:
- 30 min: LeetCode (1 problem)
- 30 min: System design (1 component)
- 30 min: SRE reading (Google SRE Book, blog posts)
- 30 min: Hands-on lab (K8s, Terraform, Prometheus)

Weekends:
- 2 hours: Mock interview
- 2 hours: Deep dive on one topic (e.g., Kubernetes networking)
- 1 hour: Review and refine STAR stories
- 1 hour: Build a project (monitoring setup, CI/CD pipeline)
```

---

## SRE Book References

### Must-Read

| Book | Why | Chapters to Focus |
|------|-----|-------------------|
| **Google SRE Book** | The SRE Bible | 1-10 (foundations), 11-20 (distributed systems), 21-30 (practices) |
| **Google SRE Workbook** | Practical exercises | Monitoring, alerting, on-call, incident response, SLOs |
| **Seeking SRE** | Other companies' SRE practices | All chapters — real stories from LinkedIn, Spotify, Dropbox, etc. |
| **Designing Data-Intensive Applications (DDIA)** | Distributed systems fundamentals | Partition, replication, transactions, consensus |
| **System Design Interview (Alex Xu)** | System design interview prep | All chapters + practice problems |
| **The Phoenix Project** | IT/DevOps transformation novel | All — understand DevOps principles through story |

### Supplementary

| Book | Focus |
|------|-------|
| **Incident Management for Operations (Rob Schnepp)** | Incident command for IT |
| **Web Operations (John Allspaw)** | Operations fundamentals |
| **Chaos Engineering (Nora Jones, Casey Rosenthal)** | Chaos engineering methodology |
| **Effective DevOps (Jennifer Davis, Ryn Daniels)** | DevOps culture and measurement |
| **Building Secure and Reliable Systems (Google)** | Security + reliability together |
| **Kubernetes: Up and Running (Brendan Burns)** | Kubernetes fundamentals |
| **Terraform: Up and Running (Yevgeniy Brikman)** | IaC best practices |
| **Database Internals (Alex Petrov)** | Deep database knowledge |

### Blogs and Articles

- **SRE Weekly** — Curated SRE content every week
- **Google SRE Blog** — Practices, case studies, tools
- **Netflix Tech Blog** — Chaos engineering, reliability
- **AWS Architecture Blog** — Well-Architected, reliability patterns
- **Microsoft Azure Blog** — Azure reliability, DevOps
- **Uber Engineering Blog** — Distributed systems at scale
- **LinkedIn Engineering Blog** — Kafka, distributed systems
- **Cloudflare Blog** — Network reliability, security

### Hands-On Resources

| Resource | What You'll Learn |
|----------|-------------------|
| **This Academy (labs 01-15)** | Real production scenarios end-to-end |
| **Kubernetes Chaos Mesh** | Chaos engineering on K8s |
| **Prometheus + Grafana** | Monitoring, alerting, dashboards |
| **OpenTelemetry + Jaeger** | Distributed tracing |
| **Terraform + AWS/Azure/GCP** | Infrastructure as Code |
| **ELK/Loki** | Log aggregation and analysis |
| **OWASP WebGoat** | Security incident response |
| **PagerDuty Training** | Incident response simulations |

---

## Sample Interview Questions and Answers

### Coding Questions

**Q: Implement a rate limiter in Java using the token bucket algorithm.**

```java
public class TokenBucket {
    private final long capacity;
    private final double refillRate;
    private double tokens;
    private long lastRefillTime;

    public TokenBucket(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefillTime) / 1_000_000_000.0;
        if (elapsed > 0) {
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillTime = now;
        }
    }
}
```

**Q: Implement a circuit breaker.**

```java
public enum CircuitState { CLOSED, OPEN, HALF_OPEN }

public class CircuitBreaker {
    private CircuitState state = CircuitState.CLOSED;
    private int failureCount = 0;
    private final int failureThreshold;
    private final long timeoutMs;
    private Instant lastFailureTime;

    public CircuitBreaker(int failureThreshold, long timeoutMs) {
        this.failureThreshold = failureThreshold;
        this.timeoutMs = timeoutMs;
    }

    public boolean allowRequest() {
        if (state == CircuitState.OPEN) {
            if (Instant.now().isAfter(lastFailureTime.plusMillis(timeoutMs))) {
                state = CircuitState.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }

    public void onSuccess() {
        if (state == CircuitState.HALF_OPEN) {
            state = CircuitState.CLOSED;
            failureCount = 0;
        }
    }

    public void onFailure() {
        failureCount++;
        if (failureCount >= failureThreshold) {
            state = CircuitState.OPEN;
            lastFailureTime = Instant.now();
        }
    }
}
```

### System Design Questions

**Q: Design a global load balancer.**

Key components and considerations:
1. **DNS layer:** Route53, CloudDNS with latency-based routing and health checks
2. **Anycast:** BGP anycast to route users to nearest datacenter
3. **L4 Load Balancer:** Distributes TCP connections (HAProxy, Envoy, AWS NLB)
4. **L7 Load Balancer:** Routes HTTP/HTTPS traffic by path, header, cookie (nginx, AWS ALB, Google HTTP LB)
5. **Health checks:** Active (periodic probes) + passive (traffic observation)
6. **Failover:** DNS TTL management, connection draining, graceful degradation
7. **Sticky sessions:** Cookie-based or IP-hash for stateful services

Failure modes to address:
- Datacenter failure: DNS failover + anycast withdrawal
- LB failure: Active-passive pair with VRRP
- Health check false positive: Multi-probe, degraded mode
- Traffic spike: Auto-scaling + rate limiting + throttling

**Q: Design a monitoring system for 10,000 services.**

1. **Data collection:** Prometheus exporters or statsd agents on every instance
2. **Aggregation:** Thanos or Cortex for global view, streaming for real-time
3. **Storage:** Time-series database (Prometheus TSDB, VictoriaMetrics, InfluxDB)
4. **Alerting:** Alertmanager with deduplication, grouping, inhibition
5. **Dashboards:** Grafana with service-level dashboards + SLO burn-rate panels
6. **Service discovery:** Kubernetes API, Consul, or file-based targets
7. **Long-term retention:** Object storage (S3, GCS) for raw metrics, downsampling for older data
8. **Multi-tenancy:** Label-based isolation, per-team dashboards and alerts

### Troubleshooting Questions (with Answer Frameworks)

**Q: "Your service's P99 latency jumped from 10ms to 5 seconds. What do you do?"**

Systematic approach:
1. **Narrow by dimension:** Is it all endpoints or specific? All regions or one? Correlated with time/deployment?
2. **Check dashboards:** GC activity, thread pool saturation, connection pool, CPU, memory
3. **Check external dependencies:** Database query times, cache hit ratio, downstream API latency
4. **Distributed tracing:** Find the slowest span in the request chain
5. **Thread dump:** Are threads blocked, waiting, or stuck in GC?
6. **Check for contention:** Lock contention, CPU throttling, IO wait
7. **Correlate with changes:** Recent deployment, config change, traffic pattern shift

**Q: "A database connection pool is exhausted. Walk through the diagnosis and fix."**

1. **Confirm:** Check error logs for "Connection not available, request timed out"
2. **Check active connections:** HikariCP JMX metrics or SQL: SHOW STATUS LIKE 'Threads_connected'
3. **Find leaking connections:** Application logs for connections opened but not closed
4. **Triage:** Increase pool size temporarily, kill long-running queries
5. **Root cause:** Missing try-with-resources or Connection.close() in exception path
6. **Fix:** Add try-with-resources or try-finally with connection close
7. **Prevent:** Add connection leak detection threshold, pool monitoring dashboard, code review checklist

**Q: "A Kubernetes pod is in CrashLoopBackOff. How do you debug it?"**

1. `kubectl describe pod <name>` — Check events, recent state, exit codes
2. `kubectl logs -p <name>` — Logs from the previous (crashed) instance
3. `kubectl logs <name> --tail=50` — Current instance logs
4. Check exit code: 137=OOM, 139=SIGSEGV, 143=SIGTERM (graceful shutdown)
5. Check resource limits vs. usage: `kubectl top pod <name>`
6. Check ConfigMap/Secret mounts — missing or incorrect?
7. Check liveness/readiness probe configuration — too aggressive?
8. Check node resources: `kubectl describe node <node>` — disk pressure, memory pressure

## Behavioral Question Preparation

### The STAR Method Deep Dive

**Situation:** Set the context. Be specific about the service, scale, severity, and time.

**Task:** What was your responsibility? Were you on-call? Incident commander? Debugging lead?

**Action:** This is the most important part. List concrete actions in sequence:
1. Detection and triage
2. Initial mitigation (rollback, feature flag, scale up)
3. Systematic debugging (what you checked and in what order)
4. Root cause identification
5. Permanent fix implementation
6. Verification and monitoring

**Result:** Quantify the outcome. Use specific numbers:
- "Error rate dropped from 30% to 0.1% within 5 minutes"
- "MTTR reduced from 45 minutes to 8 minutes"
- "No recurrence in 6 months"
- "Team adopted new testing/review practices"

### Preparing Your 5 Stories

Have these 5 incident stories ready:

1. **Complex performance issue** — Memory leak, deadlock, CPU spike, N+1 query
2. **Deployment failure** — Bad deployment, rollback, canary saved us
3. **Infrastructure failure** — Disk full, network partition, cloud provider outage
4. **Security incident** — Breach, DDoS, credential leak, rate limiting bypass
5. **Process improvement** — Automation, monitoring, incident response transformation

For each story:
- Can you tell it in 2 minutes? And in 5 minutes with deep technical details?
- Can you describe the business/customer impact in numbers?
- Can you articulate what the team learned and what systemic changes were made?

### Common Behavioral Questions

| Question | What They're Really Asking |
|----------|---------------------------|
| "Tell me about a time you fixed a production issue" | Do you have a systematic approach to debugging? |
| "Tell me about a time you disagreed with a decision" | Can you advocate for reliability without being difficult? |
| "Tell me about a time you improved a process" | Do you have an automation and continuous improvement mindset? |
| "Tell me about a time you made a mistake" | Can you be honest, learn, and improve? |
| "How do you handle on-call?" | Are you reliable, methodical, and sustainable? |
| "Tell me about a time you mentored someone" | Do you invest in team growth and knowledge sharing? |
| "How do you prioritize reliability vs. feature development?" | Can you make tradeoff decisions and communicate them? |

### What NOT to Do in Behavioral Questions

- Don't blame others for the incident
- Don't say "we just restarted it" without explaining root cause
- Don't skip the business impact
- Don't be vague about actions ("I looked at logs" — which logs? what did you find?)
- Don't claim you fixed it alone if it was a team effort
- Don't skip the "what did we learn" part

## Company-Specific Interview Tips

### Google
- Expect system design + troubleshooting + coding in every on-site
- Practice explaining distributed systems concepts in plain English
- Study the Google SRE book's case studies (Chubby, Borg, Monarch)
- Be ready to discuss tradeoffs, not just solutions
- Demonstrate systematic thinking: "First I would check X, then Y, then Z"

### Amazon
- Every answer must touch at least one Leadership Principle
- Use the STAR method rigidly — interviewers are trained to evaluate STAR
- The Bar Raiser will ask about a time you said no to something
- Be ready for "Why do you want to work at Amazon?" — have a genuine answer
- Technical depth is expected but LP depth is how you get hired

### Microsoft
- Prepare Azure-specific answers (when asked about cloud)
- Demonstrate "growth mindset" — talk about learning from failures
- The techno-functional round tests your ability to communicate with non-technical stakeholders
- Understand Azure Well-Architected Framework pillars

### Netflix
- Culture questions are as important as technical questions
- Demonstrate "Freedom & Responsibility" — show you can be trusted to make decisions
- Talk about chaos engineering even if you've never done it (show curiosity)
- Be humble but assertive — "I disagree but here's my reasoning"

### Apple
- Privacy should be part of every system design answer
- Hardware-software integration knowledge is a differentiator
- Be prepared to discuss real device testing (not just cloud)
- Security and reliability are inseparable at Apple — show both

### Oracle
- Deep database knowledge is expected for support roles
- Communication and customer management are tested explicitly
- Be ready for a mock customer call scenario
- Understanding of enterprise-class support processes is valued

## Appendix: Quick Reference

### SRE Concepts Checklist

- [ ] SLI definition and measurement
- [ ] SLO setting and iteration
- [ ] Error budget policy definition
- [ ] Burn rate alerting
- [ ] Toil identification and reduction
- [ ] Incident command structure
- [ ] Blameless post-mortems
- [ ] Capacity planning
- [ ] Automation framework
- [ ] Chaos engineering methodology
- [ ] The 4 Golden Signals (Latency, Traffic, Errors, Saturation)
- [ ] The USE Method (Utilization, Saturation, Errors)
- [ ] The RED Method (Rate, Errors, Duration)

### Common Acronyms

| Acronym | Meaning |
|---------|---------|
| SRE | Site Reliability Engineering |
| SLI | Service Level Indicator |
| SLO | Service Level Objective |
| SLA | Service Level Agreement |
| MTTR | Mean Time To Resolve/Respond |
| MTTD | Mean Time To Detect |
| MTTA | Mean Time To Acknowledge |
| MTBF | Mean Time Between Failures |
| IC | Incident Commander |
| TTD | Time To Detect |
| TTR | Time To Resolve |
| TCO | Total Cost of Ownership |
| RTO | Recovery Time Objective |
| RPO | Recovery Point Objective |
| IaC | Infrastructure as Code |
| DR | Disaster Recovery |
| HA | High Availability |
| RED | Rate, Errors, Duration |
| USE | Utilization, Saturation, Errors |
| P0/P1/P2 | Priority levels (P0 = highest) |
| SEV1/SEV2 | Severity levels (SEV1 = highest) |
