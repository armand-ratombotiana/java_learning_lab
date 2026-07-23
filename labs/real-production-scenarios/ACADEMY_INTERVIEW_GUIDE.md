# Real Production Scenarios Academy — Interview Guide

## How Production Scenarios Are Tested in Interviews

### Google SRE Interview: System Design Troubleshooting

Google SRE interviews test your ability to design, debug, and maintain production systems. The interview format includes:

**System Design Round (45 min)**
- Design a distributed system and explain failure modes
- Example: "Design a global load balancer. What happens when a datacenter goes down?"
- Focus on: Network, Storage, Compute failure domains

**Troubleshooting Round (45 min)**
- Given a production incident scenario, walk through diagnosis
- Example: "Users report 500 errors. CPU is low, memory is normal. Where do you look?"
- Focus on: systematic elimination, log analysis, metrics-driven debugging

**Technical Deep-Dive Topics for Google SRE:**

| Domain | Common Questions | Key Concepts |
|--------|-----------------|--------------|
| Network | "How does DNS resolution work end-to-end?" | BGP, TCP/IP, HTTP/2, QUIC |
| Storage | "Design a distributed file system. How do you handle data durability?" | Replication, erasure coding, fsync |
| Compute | "What happens when a Borg task fails and reschedules?" | Kubernetes equivalents, cgroups, namespaces |
| Distributed Systems | "Explain Paxos/Raft in plain English. What are the failure modes?" | Consensus, quorum, leader election |
| Capacity | "How do you estimate capacity for a service growing 10x?" | Utilization, headroom, throttling |
| Incident Response | "Walk me through a real incident you handled from page to post-mortem." | Incident command, communication, blameless culture |

**Sample Google SRE Interview Question:**

> "Your service has a P50 latency of 10ms but P99 latency of 5 seconds. The team cannot find the cause. What systematic approach do you take?"

Expected approach:
1. Check if P99 latency correlates with specific request types, regions, or times
2. Look for periodic events (GC, cron jobs, batch processing)
3. Profile CPU, memory, I/O, network simultaneously
4. Use distributed tracing to identify the slowest service in the chain
5. Check for head-of-line blocking in thread pools or connection pools
6. Examine lock contention and GC pause times
7. Consider external dependencies (database, cache, downstream services)

### Microsoft Azure Interview: Production Incident Response

Microsoft Azure interviews emphasize incident response methodology and Azure-specific knowledge.

**Key Azure Interview Topics:**

| Topic | Questions | Preparation |
|-------|-----------|-------------|
| Azure Well-Architected Framework | "How does reliability differ between Azure and AWS?" | Study Azure Well-Architected pillars |
| ARM Templates / Bicep | "How do you manage infrastructure as code at scale?" | Practice ARM template debugging |
| Azure DevOps | "Describe your CI/CD pipeline. How do you handle failed deployments?" | Know Azure Pipelines, rollback strategies |
| Azure Monitor | "How do you set up monitoring for a new service?" | Log Analytics, App Insights, alerts |
| Incident Response | "You receive a page at 2 AM. Walk through your response." | Incident management process |
| Azure SQL / Cosmos DB | "How do you troubleshoot Azure SQL performance?" | Query store, DTU monitoring, indexing |

**Sample Microsoft Azure Interview Question:**

> "A critical Azure App Service becomes unresponsive after a deployment. How do you diagnose and resolve?"

Response framework:
1. Check Azure Monitor for metrics (CPU, memory, requests, errors)
2. Review Application Insights for telemetry
3. Enable diagnostic logs for the App Service
4. Check deployment slots — roll back to previous slot
5. Scale out the App Service Plan
6. Examine Kudu / advanced tools for process dump
7. Check Azure SQL Database DTU utilization if applicable
8. Review recent deployment changes via Azure DevOps

### Amazon AWS Interview: Operational Excellence

AWS interviews focus on the Well-Architected Framework, especially the Operational Excellence and Reliability pillars.

**Amazon Leadership Principles in SRE Context:**

| Principle | Application in Incident Response |
|-----------|----------------------------------|
| Customer Obsession | "How did you minimize customer impact during an outage?" |
| Ownership | "Tell me about a time you owned a production issue end-to-end." |
| Have Backbone; Disagree and Commit | "What if your senior disagreed with your fix? How did you handle it?" |
| Dive Deep | "Walk through your root cause analysis process in detail." |
| Deliver Results | "How did you ensure the fix was complete and verified?" |
| Learn and Be Curious | "What post-mortem actions did you take to prevent recurrence?" |

**AWS Well-Architected Framework — Operational Excellence Pillar Questions:**

| Question | What Interviewers Look For |
|----------|---------------------------|
| "How do you determine what metrics to monitor?" | Understanding of SLIs, SLOs, error budgets |
| "How do you handle deployment failures?" | Rollback strategy, canary deployments, feature flags |
| "How do you improve operational processes?" | Post-mortem-driven improvement, automation |
| "How do you respond to operational events?" | Incident management, communication, escalation |
| "How do you design for failure?" | Chaos engineering, fault tolerance, graceful degradation |

**Sample AWS Interview Question:**

> "Design a highly available e-commerce checkout system on AWS. What happens when DynamoDB throttles your write requests?"

Key points to cover:
- Multi-AZ deployment with Route53 failover
- DynamoDB Auto Scaling with provisioned capacity
- Exponential backoff and retry with jitter
- SQS queue for decoupling checkout requests
- Circuit breaker to DynamoDB with fallback to S3 + batch processing
- CloudWatch alarms on DynamoDB throttling events
- Read replicas for read-heavy operations

### Meta Interview: Production Debugging Under Pressure

Meta (Facebook) interviews include a production debugging round that simulates on-call pressure.

**Meta Production Debugging Format:**
- Given a live-ish system (simulated), diagnose a production issue
- Time-pressured: 45 minutes to identify root cause
- Multiple simultaneous symptoms (noise)
- Some diagnostic tools available (logs, metrics, thread dumps)

**Common Meta Debugging Scenarios:**

| Scenario | Symptoms | Root Cause |
|----------|----------|------------|
| Web server latency spike | P50 normal, P99 10x increase | GC pause, lock contention, slow DB query |
| Mobile push failures | Push delivery drops to 0% | Certificate expiry, queue backpressure |
| News Feed regression | Users report stale content | Cache invalidation bug, TTL misconfiguration |
| Ads delivery degradation | Revenue drops 30% | ML model deployment bug, A/B test interaction |
| Video upload failures | Uploads fail silently | CDN configuration, DNS resolution, SSL handshake |

**Sample Meta Interview Question:**

> "Facebook Feed is showing stale content for 10% of users in Europe. All other regions are fine. Walk through the debugging process."

Diagnostic approach:
1. Is it a specific user population? (App version, browser, OS, network)
2. Check feature flags — any recent A/B test changes for Europe
3. Check CDN cache invalidations for European edge nodes
4. Compare cache hit ratios between Europe and other regions
5. Check regional database replicas for replication lag
6. Check DNS resolution for European DNS servers
7. Examine recent deployments that affected European-only services
8. Look for timezone-related bugs in content freshness logic

### Apple Interview: Reliability Engineering

Apple interviews focus on hardware-software integration, reliability at scale, and security.

**Key Apple Interview Topics:**

| Topic | Focus Area |
|-------|-----------|
| System Reliability | "How do you ensure a service achieves 99.99% uptime?" |
| Performance | "How do you optimize for battery life vs. performance?" |
| Security | "How do you handle security incidents in production?" |
| Privacy | "How do you design systems that collect minimal data?" |
| Integration | "How do you test hardware-software interactions?" |
| Resilience | "What happens when a push notification service goes down?" |

**Sample Apple Interview Question:**

> "Apple Push Notification Service (APNS) is failing to deliver notifications to iOS devices. How do you triage?"

Triage approach:
1. Is it all devices or specific versions?
2. Check APNS certificate expiry (most common cause)
3. Check TLS termination and certificate trust chain
4. Verify token database is not corrupted
5. Check feedback service for expired tokens
6. Examine push queue depth and consumer lag
7. Check device-side connectivity (do carrier networks work?)
8. Test with a known-good device in the same region

### Oracle Interview: Enterprise Production Support

Oracle interviews emphasize deep database knowledge and enterprise support scenarios.

**Key Oracle Interview Topics:**

| Topic | Questions |
|-------|-----------|
| Database Performance | "How do you troubleshoot a slow Oracle query?" |
| Backup and Recovery | "Walk through a point-in-time recovery scenario." |
| RAC and High Availability | "How do you diagnose a RAC node eviction?" |
| Exadata | "What Exadata features improve query performance?" |
| Enterprise Support | "A critical financial batch job fails. How do you respond?" |
| PL/SQL | "How do you optimize a PL/SQL batch process?" |

**Sample Oracle Interview Question:**

> "An end-of-day financial batch job that normally takes 1 hour is now taking 8 hours. What could cause this?"

Diagnostic approach:
1. Check if it failed during the previous run (cleanup issue?)
2. Compare AWR reports between normal and slow runs
3. Look for execution plan changes (plan regression)
4. Check for stale optimizer statistics
5. Look for missing indexes on recently modified tables
6. Check for blocking locks from other sessions
7. Examine temp tablespace usage (hash join spill)
8. Check for I/O contention or storage performance degradation

### Real Interview Stories from SRE/DevOps Interviews

**Story 1: Google SRE On-Site — The Load Balancer Question**
"The interviewer asked me to design a global load balancer. I started drawing boxes: DNS, anycast, L4 LB, L7 LB. He kept adding failure scenarios: 'What if a datacenter goes dark?', 'What if your health checks are wrong?', 'What if traffic shifts faster than capacity provisions?' The key insight was understanding tradeoffs: DNS TTL vs failover speed, active-active vs active-passive, how many nines each design choice costs." — Senior SRE at Google

**Story 2: Amazon — The DynamoDB Throttling Incident**
"I was asked to handle an incident where DynamoDB was throttling a critical API. I explained I'd first check if throttling was hot-key related or capacity related. Hot keys need partition redesign; capacity needs scaling. The interviewer asked 'What if scaling doesn't help?' I said I'd implement exponential backoff, then SQS buffering, then a local cache as last resort. They wanted to hear that I'd also investigate the traffic pattern causing the throttle." — SRE at AWS

**Story 3: Microsoft Azure — The Deployment Gone Wrong**
"A deployment to Azure App Service caused 500 errors. I was on the phone with the mock 'on-call engineer'. I asked: 'Is it all regions or one? Can you access Kudu? What do App Insights show? Can we swap slots back?' The interviewer wanted methodical thinking: isolate, diagnose, mitigate, fix. The mistake most candidates make is jumping to fix without isolating the blast radius." — Azure Consultant

**Story 4: Meta — The Real-Time Debugging Simulation**
"The interviewer gave me a simulated dashboard with multiple charts: CPU, memory, request rate, error rate, latency P50/P99. The charts were deliberately noisy. I had to figure out which chart was the signal and which were noise. The trick was: latency P99 spiked before CPU. So CPU was a symptom, not the root cause. The root cause was a slow database call that consumed threads and made everything queue up." — Production Engineer at Meta

**Story 5: Netflix — The Chaos Engineering Interview**
"The entire interview was about chaos engineering. They asked me to design a 'Chaos Monkey' for a payment system. I had to think about: what could fail? (database, cache, downstream API, network, disk full). What's the blast radius? (failing payments silently vs. failing loudly). How do you monitor? (transaction success rate, latency, fraud alerts). How do you roll back? (feature flags, traffic shifting)." — Cloud Engineer at Netflix

### How to Prepare for "Tell Me About a Time You Fixed a Production Issue"

**The STAR Method for Production Incidents:**

| Component | What to Include | Example |
|-----------|----------------|---------|
| **S**ituation | Context: service, traffic, severity, time of day | "Our payment gateway was timing out on 30% of transactions during Black Friday peak at 5M TPS." |
| **T**ask | Your role and responsibility | "I was the primary on-call SRE responsible for the payment platform." |
| **A**ction | Step-by-step actions: detection, diagnosis, mitigation, fix | "1) I acknowledged the page within 2 minutes. 2) Checked dashboards: CPU normal, memory normal, connection pool exhausted. 3) Identified a deployment 10 minutes prior added a database call without connection management. 4) Rolled back the deployment. 5) Added missing try-with-resources. 6) Verified fix with load test." |
| **R**esult | Quantified outcome | "Error rate dropped from 30% to 0.1% within 5 minutes of rollback. Post-mortem led to mandatory try-with-resources linting. No recurrence in 6 months." |

**What Interviewers Evaluate:**

| Criteria | What They Look For |
|----------|-------------------|
| Technical depth | Understanding of the technology stack involved |
| Methodical approach | Systematic elimination of hypotheses, not random guesses |
| Communication | Clear status updates, appropriate detail level |
| Ownership | Did you own the issue end-to-end? |
| Learning | What did you (or the team) do differently afterward? |
| Business impact awareness | Do you understand the customer/business impact? |
| Blameless culture | Focus on system improvements, not blame |

**Common Mistakes to Avoid:**
- Taking too long to describe the context before getting to actions
- Not mentioning the business impact
- Blaming others or external factors
- Not describing what was learned from the incident
- Claiming you fixed it without describing the fix
- Using too much jargon without explaining

### Site Reliability Engineering (SRE) Interview Patterns

**Core SRE Concepts to Know:**

| Concept | Definition | Interview Relevance |
|---------|-----------|-------------------|
| SLO | Service Level Objective — target reliability | "How do you set SLOs for a new service?" |
| Error Budget | 100% - SLO = allowed error rate | "How do error budgets drive engineering decisions?" |
| Toil | Manual, repetitive work that scales linearly | "How do you identify and reduce toil?" |
| SLI | Service Level Indicator — measured reliability | "What SLIs matter for a database service?" |
| Burn Rate | How fast error budget is consumed | "How do you set up burn rate alerts?" |
| Post-mortem | Blameless analysis of incidents | "Walk me through your ideal post-mortem process." |
| Incident Command | Structured incident response | "What's the role of an Incident Commander?" |

**Google SRE Interview Rounds:**

1. **Phone Screen (45 min):** System design or troubleshooting scenario
2. **On-site Round 1 — System Design (45 min):** Design a distributed system
3. **On-site Round 2 — Troubleshooting (45 min):** Debug a broken system
4. **On-site Round 3 — Coding (45 min):** Algorithm or automation problem
5. **On-site Round 4 — Behavioral (45 min):** Past incidents, leadership, learning

**For each round, Google evaluates:**
- Technical competence (can they solve the problem?)
- Communication (do they explain clearly?)
- Collaboration (do they ask clarifying questions?)
- Leadership (do they drive the conversation?)
- Adaptability (do they handle curveballs well?)

### On-Call Rotation Interview Questions

**Common On-Call Interview Questions:**

| Question | What They're Testing |
|----------|---------------------|
| "What's the worst page you've ever received?" | Handling pressure, incident management |
| "How do you balance on-call with regular work?" | Burnout prevention, sustainability |
| "How do you improve a bad on-call experience?" | Operational excellence, automation mindset |
| "What do you do when you don't know how to fix an issue?" | Escalation, collaboration, learning |
| "How do you hand off an incident to the next shift?" | Communication, documentation |
| "What on-call metrics do you track?" | Operational maturity (MTTR, MTTD, page frequency) |
| "How do you run an effective post-mortem?" | Blameless culture, systems thinking |

**On-Call Metrics to Know:**

| Metric | Definition | Target |
|--------|-----------|--------|
| MTTD | Mean Time To Detect | < 5 minutes |
| MTTA | Mean Time To Acknowledge | < 2 minutes |
| MTTR | Mean Time To Resolve | < 1 hour (SEV1) |
| Page Frequency | Alerts per shift per engineer | < 2 per shift |
| Incident Count | Number of confirmed incidents per week | Decreasing trend |
| Toil Percentage | Time spent on manual/repetitive tasks | < 50% |

### Incident Command System Knowledge

**Incident Command Structure for IT:**

```
Incident Commander (IC)
├── Operations Lead
│   ├── Debugging Team
│   └── Mitigation Team
├── Communications Lead
│   ├── Internal Communications
│   └── External / Customer Communications
└── Planning Lead
    ├── Timeline Documentation
    └── Resource Coordination
```

**Roles and Responsibilities:**

| Role | Responsibility |
|------|---------------|
| Incident Commander | Overall coordination, decision-making, stakeholder updates |
| Operations Lead | Technical diagnosis, mitigation, and resolution |
| Communications Lead | Status updates, stakeholder communication, documentation |
| Scribe | Timeline logging, action item tracking, evidence preservation |
| Subject Matter Expert | Deep technical expertise for the affected system |

**IC Checklist:**
1. [ ] Acknowledge the page / alert
2. [ ] Declare severity level (SEV1/SEV2/SEV3)
3. [ ] Assemble incident response team
4. [ ] Assign roles (IC, Ops Lead, Comms Lead, Scribe)
5. [ ] Establish communication channel (Slack channel, bridge call)
6. [ ] Initial assessment: what's affected, blast radius, customer impact
7. [ ] Send initial status update (internal + stakeholders)
8. [ ] Begin diagnosis: symptoms, changes, metrics, logs, traces
9. [ ] Mitigate: rollback, feature flag, scale up, traffic shift
10. [ ] Verify mitigation: confirm customer impact resolved
11. [ ] Send resolution status update
12. [ ] Begin post-mortem data collection
13. [ ] Schedule post-mortem meeting within 72 hours
14. [ ] Complete incident report in tracking system

**Sample Incident Timeline:**

```
2024-06-15 02:13 UTC  — PagerDuty alert: Error rate > 5% on payment service
2024-06-15 02:15 UTC  — On-call acknowledges and declares SEV1
2024-06-15 02:18 UTC  — IC assigned, Ops Lead begins investigation
2024-06-15 02:22 UTC  — Initial assessment: payment failures, all regions
2024-06-15 02:25 UTC  — Internal status update sent
2024-06-15 02:30 UTC  — Root cause identified: database connection pool exhausted
2024-06-15 02:32 UTC  — Mitigation: rollback deployment, restart connection pool
2024-06-15 02:38 UTC  — Error rate returns to normal
2024-06-15 02:40 UTC  — Resolution status update sent
2024-06-15 02:45 UTC  — Stakeholder summary sent
2024-06-17 10:00 UTC  — Post-mortem meeting held
```

### Study Plan and Resources

**Week 1-2: Foundations**
- Read Google SRE Book (free online): Chapters 1-10
- Study AWS Well-Architected Framework: Operational Excellence pillar
- Practice incident response: set up a home lab with monitoring
- Review Java memory management, GC tuning, thread dumps

**Week 3-4: Deep Dive**
- Read Google SRE Workbook: Chapters on monitoring, alerting, on-call
- Study Google SRE Book Chapters 11-20 (distributed systems)
- Practice: write runbooks for common failure scenarios
- Review: SQL query tuning, connection pooling, caching

**Week 5-6: Interview Preparation**
- Review STAR stories: prepare 5 production incident stories
- Practice system design: design distributed systems with failure modes
- Mock interviews with peers or use interviewing platforms
- Review algorithms relevant to production debugging (rate limiting, circuit breakers)

**Essential Reading:**

| Resource | Why It Matters |
|----------|---------------|
| Google SRE Book | Foundational SRE concepts, incident management |
| Google SRE Workbook | Practical exercises, monitoring, alerting |
| AWS Well-Architected Framework | Operational excellence design patterns |
| The Phoenix Project | IT/DevOps transformation case study |
| Incident Management for Operations | Runbooks, escalation, on-call best practices |
| Web Operations by Allspaw | Operations engineering fundamentals |
| Chaos Engineering by Principles of Chaos | Fault tolerance testing methodology |
| Effective DevOps by Jennifer Davis | Culture, collaboration, measurement |

**Hands-On Practice:**

| Resource | How to Use |
|----------|-----------|
| This Academy (labs 01-15) | Complete each lab's exercises and interview prep |
| PagerDuty Training | Free incident response simulation exercises |
| AWS Well-Architected Tool | Review your architecture against best practices |
| Kubernetes Chaos Mesh | Practice chaos engineering on containerized services |
| Grafana + Prometheus | Set up monitoring dashboards and alerts |
| Jaeger / OpenTelemetry | Practice distributed tracing |
| OWASP WebGoat | Security incident response practice |

**Study Groups and Communities:**
- SRE Slack community
- r/sre (Reddit)
- Google SRE mailing list
- AWS Well-Architected community
- USENIX SREcon conferences (recordings available online)

### Sample ANSWERS for Key Questions

**"What's the difference between monitoring, alerting, and observability?"**

Monitoring is collecting metrics, logs, and traces from a system. Alerting is the process of notifying humans when monitoring data indicates a problem. Observability is the ability to understand the internal state of a system based on its external outputs (logs, metrics, traces, events). Monitoring tells you something is wrong; observability tells you what and why.

**"How do you handle a SEV1 incident?"**

1. Acknowledge within 2 minutes
2. Declare SEV1 and assemble the incident response team
3. Assign incident commander, ops lead, comms lead, scribe
4. Send initial status update to stakeholders
5. Focus on mitigation first (rollback, feature flag, scale up)
6. Document timeline and actions as they happen
7. Once mitigated, transition to root cause investigation
8. Schedule post-mortem within 72 hours
9. Complete incident report and track action items

**"What's your approach to post-mortems?"**

Blameless, thorough, and action-oriented. I focus on systemic causes, not individual mistakes. The five questions: What happened? Why did it happen? What was the impact? What prevented faster detection/resolution? What systems changes prevent recurrence? Every post-mortem produces specific, assigned, tracked action items with deadlines.

**"How do you ensure a deployment doesn't cause an outage?"**

Defense in depth: 1) Code review with production checklist, 2) Automated testing (unit, integration, load), 3) Canary deployment (1% → 10% → 100%), 4) Feature flags for immediate kill switch, 5) Automated rollback on error budget burn, 6) Post-deployment monitoring for 30 minutes, 7) Runbook for common deployment failures.

**"Explain the difference between MTTR and MTTD."**

MTTD (Mean Time to Detect) is how long between when an incident starts and when someone is notified. MTTR (Mean Time to Resolve) is how long from detection to resolution. Both matter: reduce MTTD with better monitoring and alerting; reduce MTTR with runbooks, automation, and practice.
