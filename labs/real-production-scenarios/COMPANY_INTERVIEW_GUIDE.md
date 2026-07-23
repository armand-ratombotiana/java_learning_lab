# Company Interview Guide — SRE / Production Engineering / Infrastructure

## How to Use This Guide

This guide maps interview processes at 13 top tech companies for SRE/Production Engineering roles. Each entry includes the exact interview format, coding vs debugging breakdown, on-call simulation expectations, and compensation data. Cross-reference each company's focus areas with the 15 incident-response labs in this academy.

---

## Google — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | System design or troubleshooting scenario |
| On-site R1 | 45 min | System Design | Design a large-scale distributed system |
| On-site R2 | 45 min | Troubleshooting | Debug a broken production system |
| On-site R3 | 45 min | Coding | Algorithm or automation problem |
| On-site R4 | 45 min | Behavioral | Past incidents, leadership, learning |

### Coding vs Debugging vs System Design Split: 25% / 35% / 40%

### On-Call Simulation Expectations

Google SRE on-site includes a "troubleshooting round" that simulates receiving a page. You are given a scenario (e.g., "Users report 500 errors, CPU is low, memory normal") and must walk through your diagnostic process step by step. The interviewer expects systematic hypothesis elimination using metrics, logs, and traces.

**Practice with:** Lab 03 (high CPU — CPU was symptom, not cause), Lab 04 (connection pool exhaustion — low CPU, normal memory, errors spiking)

### Incident Response Round

The troubleshooting round doubles as an incident response test. Key evaluation criteria:
- Do you declare severity and assemble response team?
- Do you mitigate before fully understanding root cause?
- Do you communicate status clearly?
- Do you follow up with post-mortem actions?

### Infrastructure/Networking Round Content

Google SRE expects deep knowledge of:
- **Networking:** BGP, TCP/IP, HTTP/2, QUIC, DNS resolution, load balancing (L4 vs L7)
- **Storage:** Distributed file systems, replication, erasure coding, fsync semantics
- **Compute:** Borg/Omega (Kubernetes equivalents), cgroups, namespaces, container isolation
- **Distributed Systems:** Paxos/Raft consensus, quorum, leader election, distributed consensus

### Interview Stories from SRE Candidates

> "The interviewer asked me to design a global load balancer. I started drawing DNS, anycast, L4 LB, L7 LB. He kept adding failure scenarios: 'What if a datacenter goes dark?', 'What if health checks are wrong?' The key insight was understanding tradeoffs: DNS TTL vs failover speed, active-active vs active-passive." — Sr. SRE at Google

### Compensation Data (2024-2025)

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| L4 (SRE II) | $150-180K | 15% | $200-400K | $220-280K |
| L5 (SRE III) | $180-220K | 15% | $400-700K | $310-410K |
| L6 (Staff SRE) | $220-280K | 20% | $700-1.2M | $440-580K |

### Study Timeline: 12 weeks

Weeks 1-4: Complete Labs 01-05 (JVM, concurrency, CPU, DB). Weeks 5-8: Labs 06-10 (deployment, circuit breaker, cache, disk, security). Weeks 9-10: System design + LeetCode. Weeks 11-12: Mock interviews + behavioral prep.

---

## Meta — Production Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Coding + System Design | Algorithms + basic design |
| On-site R1 | 45 min | Coding | Algorithms (medium/hard) |
| On-site R2 | 45 min | Production Debugging | Simulated live debugging |
| On-site R3 | 45 min | System Design | Distributed system design |
| On-site R4 | 45 min | Behavioral | Past experience, leadership |

### Coding vs Debugging vs System Design Split: 40% / 30% / 30%

### On-Call Simulation Expectations

Meta's production debugging round uses a simulated dashboard with multiple noisy charts (CPU, memory, request rate, error rate, latency P50/P99). You must identify which chart is signal vs noise. The classic trick: latency P99 spikes before CPU, so CPU is a symptom, not cause. The root cause is often a slow database call consuming threads.

**Practice with:** Lab 02 (thread deadlock — threads blocked), Lab 03 (high CPU — misleading symptom), Lab 04 (connection pool — thread exhaustion), Lab 05 (slow query — root cause)

### Incident Response Round

The production debugging simulation is the closest Meta comes to an incident response round. You are expected to:
- Triage symptoms in order of severity
- Isolate root cause by eliminating variables
- Propose a mitigation strategy
- Communicate your findings clearly under time pressure

### Infrastructure/Networking Round Content

Meta's PE role expects:
- **Linux internals:** Process scheduling, memory management, file systems, cgroups
- **Networking:** TCP/IP stack, HTTP, load balancing, CDN architecture
- **Storage:** MySQL, Memcached, TAO (graph), RocksDB internals
- **Caching:** Cache invalidation, consistent hashing, cache stampede prevention

### Interview Stories

> "The interviewer gave me a dashboard showing P99 latency at 5 seconds and CPU at 95%. Most candidates would say 'high CPU is the problem.' The correct answer was 'P99 spiked first, then CPU followed — so CPU is a symptom of more work queuing up. The real problem is what's making requests slow.' The root cause was a Memcached cluster with a cold cache after a deployment." — PE at Meta

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| IC4 (PE) | $160-200K | 10% | $400-600K | $270-360K |
| IC5 (Sr PE) | $200-250K | 15% | $600-1M | $380-510K |
| IC6 (Staff PE) | $250-310K | 20% | $1-1.5M | $530-700K |

### Study Timeline: 10 weeks

Focus on Linux internals, MySQL performance, caching strategies. Labs 02-05, 07-08 are most relevant. LeetCode medium/hard algorithms essential.

---

## Amazon — Systems Engineer / SDE (Infrastructure)

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 60 min | Technical + LP | Coding + Leadership Principles |
| On-site R1 | 60 min | Coding | Algorithms, data structures |
| On-site R2 | 60 min | System Design | Distributed systems, scalability |
| On-site R3 | 60 min | Bar Raiser (LP) | Leadership Principles deep dive |
| On-site R4 | 60 min | Operational Excellence | Incident response, operations |

### Coding vs Debugging vs System Design Split: 30% / 20% / 50%

### On-Call Simulation Expectations

Amazon's "Operational Excellence" round simulates on-call scenarios. You might be asked: "You're paged at 2 AM for DynamoDB throttling on a critical API. Walk through your response." They evaluate systematic triage, escalation judgment, and customer focus.

**Practice with:** Lab 04 (connection pool exhaustion), Lab 09 (disk space — capacity planning), Lab 14 (API rate limiting — throttling scenarios)

### Incident Response Round

Amazon includes incident response explicitly in the Operational Excellence round. Key areas:
- **Detection:** How do you know something is wrong? (CloudWatch alarms, user reports, metrics)
- **Triage:** SEV1 vs SEV2 vs SEV3 classification
- **Mitigation:** Rollback, scale up, traffic shift, degrade feature
- **Root Cause:** 5 Whys, deep dive into systems
- **Prevention:** What action items prevent recurrence?

### Infrastructure/Networking Round Content

Amazon expects:
- **AWS Well-Architected Framework:** All 6 pillars (Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, Sustainability)
- **Networking:** VPC, Route53, CloudFront, ELB, Direct Connect
- **Storage:** S3, EBS, EFS, RDS, DynamoDB — failure modes and recovery
- **Compute:** EC2, Lambda, ECS, EKS — auto-scaling, capacity planning

### Interview Stories

> "I was asked to handle a DynamoDB throttling incident. The interviewer wanted to hear: first check if it's hot-key or capacity related. Hot keys need partition redesign; capacity needs scaling. 'What if scaling doesn't help?' Implement exponential backoff, SQS buffering, then local cache. They also wanted to hear I'd investigate what traffic pattern caused the throttle." — SRE at AWS

### Compensation Data

| Level | Base Salary | Bonus (1st 2yr) | RSU (5yr) | Total Comp |
|-------|-------------|-----------------|-----------|------------|
| L5 (SDE/SysDE) | $140-180K | $40-80K signing | $200-400K | $200-300K |
| L6 (Sr SDE/SysDE) | $180-240K | $60-100K signing | $400-800K | $320-460K |
| L7 (Principal) | $240-300K | $100-150K signing | $800-1.5M | $480-650K |

### Study Timeline: 10 weeks

Prioritize AWS Well-Architected Framework and Leadership Principles. Labs 04, 05, 09, 14, 15 are most relevant. Prepare 5 STAR stories mapped to Leadership Principles.

---

## Microsoft — Azure SRE

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical + Azure | Azure fundamentals + coding |
| On-site R1 | 45 min | System Design | Cloud-native architecture |
| On-site R2 | 45 min | Coding/Algorithm | Data structures, algorithms |
| On-site R3 | 45 min | Incident Response | Production debugging scenario |
| On-site R4 | 45 min | Behavioral + Azure | Past experience + Azure depth |

### Coding vs Debugging vs System Design Split: 25% / 35% / 40%

### On-Call Simulation Expectations

Azure SRE interviews often include a "production incident response" round where you're given an Azure-specific scenario (e.g., "App Service becomes unresponsive after deployment"). You must walk through Azure Monitor, App Insights, Kudu diagnostics, and deployment slot rollback.

**Practice with:** Lab 06 (deployment rollback — Azure scenario), Lab 11 (TLS cert expiry), Lab 12 (Kubernetes crashloop — AKS context)

### Incident Response Round

Microsoft emphasizes the Azure Well-Architected Framework's Reliability pillar:
- **Azure Monitor:** Metrics, logs, alerts, action groups
- **Application Insights:** Distributed tracing, dependency mapping, smart detection
- **Azure DevOps:** CI/CD pipelines, release gates, rollback strategies
- **ARM/Bicep:** Infrastructure as Code at scale, what-if deployment validation

### Infrastructure/Networking Round Content

- **Azure Networking:** VNET, NSG, Azure DNS, Traffic Manager, Front Door, WAF
- **Azure Compute:** VMSS, App Service, AKS, Azure Functions
- **Azure Storage:** Blob, Table, Queue, Files — redundancy and failover
- **Azure Database:** SQL DB, Cosmos DB — performance tuning, replication

### Interview Stories

> "A deployment to Azure App Service caused 500 errors. I was on the phone with the mock on-call engineer. I asked: 'All regions or one? Can you access Kudu? What do App Insights show? Can we swap slots back?' The interviewer wanted methodical thinking: isolate, diagnose, mitigate, fix. Candidates who jump to fixing without isolating blast radius fail." — Azure Consultant

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| 59-60 (SRE II) | $120-160K | 10-20% | $100-200K | $160-220K |
| 61-62 (Sr SRE) | $160-210K | 15-25% | $200-400K | $240-340K |
| 63-64 (Principal) | $210-280K | 20-30% | $400-800K | $360-520K |

### Study Timeline: 8 weeks

Focus on Azure services and Well-Architected Framework. Labs 06, 11, 12, 15. Study ARM templates and Azure DevOps pipelines.

---

## Apple — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | Systems + coding |
| On-site R1 | 45 min | System Design | Reliable systems design |
| On-site R2 | 45 min | Coding | Algorithms, data structures |
| On-site R3 | 45 min | Debugging | Production troubleshooting |
| On-site R4 | 45 min | Behavioral + Security | Privacy, security incidents |

### Coding vs Debugging vs System Design Split: 30% / 30% / 40%

### On-Call Simulation Expectations

Apple focuses on hardware-software integration scenarios. Example: "Apple Push Notification Service (APNS) is failing to deliver notifications. How do you triage?" They test ability to reason about certificate chains, token databases, TLS termination, and device-side connectivity.

**Practice with:** Lab 11 (TLS cert expiry — APNS scenario), Lab 10 (security breach)

### Incident Response Round

Apple emphasizes:
- **Security-first incident response:** How do you respond to a breach involving customer data?
- **Privacy-preserving debugging:** How do you debug without accessing PII?
- **Hardware-software integration:** How does a firmware bug manifest as a service outage?

### Infrastructure/Networking Round Content

- **Apple Infrastructure:** iCloud, APNS, CDN, edge caching
- **Security:** Certificate transparency, mutual TLS, HSM, Secure Enclave
- **Performance:** Battery-aware computing, thermal throttling, energy-efficient networking

### Interview Stories

> "The APNS question test was about systematic triage: Is it all devices or specific versions? Check APNS certificate expiry (most common cause). Check TLS termination and trust chain. Verify token database. Check feedback service for expired tokens. Examine push queue depth. Check device-side connectivity." — SRE at Apple

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| ICT3 (SRE) | $140-180K | 10% | $200-400K | $200-280K |
| ICT4 (Sr SRE) | $180-240K | 15% | $400-800K | $320-460K |
| ICT5 (Staff) | $240-300K | 20% | $800-1.5M | $480-660K |

### Study Timeline: 8 weeks

Focus on certificate management, security incident response, and Apple ecosystem knowledge. Labs 10, 11.

---

## Netflix — Cloud Engineer / Cloud Infrastructure

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical Screen | Cloud infrastructure + coding |
| On-site R1 | 45 min | System Design | Chaos engineering, resilience |
| On-site R2 | 45 min | Coding/Algorithm | Automation, tooling |
| On-site R3 | 45 min | Chaos Engineering Deep Dive | Fault injection, blast radius |
| On-site R4 | 45 min | Behavioral + Culture | Freedom & Responsibility |

### Coding vs Debugging vs System Design Split: 20% / 30% / 50%

### On-Call Simulation Expectations

Netflix's entire interview is chaos engineering focused. You may be asked to design a "Chaos Monkey" for a payment system. They want to hear about: what could fail? (database, cache, downstream, network, disk), blast radius control, monitoring during experiment, rollback plan.

**Practice with:** Lab 07 (circuit breaker — the foundation of chaos engineering), Lab 08 (cache stampede), Lab 15 (disaster recovery)

### Incident Response Round

Netflix culture decentralizes incident response. They expect:
- **Autonomous decision-making:** You own your services during incidents
- **Blast radius awareness:** How do you minimize impact during experiments?
- **Automated recovery:** Self-healing systems, automated rollbacks

### Infrastructure/Networking Round Content

- **AWS at scale:** Multi-region, multi-AZ, Spinnaker CD
- **Networking:** Zuul (gateway), EVCache, SPS (Sharded Priority Queue)
- **Container orchestration:** Titus (container platform), Kubernetes
- **CDN:** Open Connect Appliance, adaptive bitrate streaming

### Interview Stories

> "The entire interview was about chaos engineering. They asked me to design a Chaos Monkey for a payment system. I had to think about: what could fail? (database, cache, downstream API, network, disk full). What's the blast radius? (failing payments silently vs failing loudly). How do you monitor? (transaction success rate, latency, fraud alerts)." — Cloud Engineer at Netflix

### Compensation Data

| Level | Base Salary | Bonus (target) | RSU (4yr) | Total Comp |
|-------|-------------|----------------|-----------|------------|
| L4 (SE) | $160-200K | 10-20% | $300-500K | $250-350K |
| L5 (Sr SE) | $200-270K | 15-25% | $500-1M | $390-540K |
| L6 (Principal) | $270-350K | 20-40% | $1-2M | $580-850K |

### Study Timeline: 8 weeks

Focus on chaos engineering, circuit breakers, fault tolerance. Labs 07, 08, 15. Read "Chaos Engineering" by Principles of Chaos.

---

## Uber — Production Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Coding + Systems | Algorithms + Linux/Networking |
| On-site R1 | 45 min | Coding | Algorithms (medium/hard) |
| On-site R2 | 45 min | System Design | Distributed systems at Uber scale |
| On-site R3 | 45 min | Debugging | Production incident analysis |
| On-site R4 | 45 min | Behavioral | Ownership, impact |

### Coding vs Debugging vs System Design Split: 35% / 30% / 35%

### On-Call Simulation Expectations

Uber's debugging round often involves real production incident post-mortems. You're given a timeline of events and asked to identify missteps, faster mitigation options, and systemic improvements.

**Practice with:** All 15 labs — Uber expects breadth. Labs 01-03 (JVM/concurrency), Lab 13 (Kafka — Uber heavily uses Kafka), Lab 14 (rate limiting)

### Incident Response Round

Uber uses a structured incident management process (based on Google SRE model). They expect:
- **Incident Commander** experience
- **Communication** during incidents (status updates, stakeholder management)
- **Post-mortem** culture — blameless, action-oriented

### Infrastructure/Networking Round Content

- **Uber's Stack:** Peloton (resource isolation), Ringpop (consistent hashing), Schemaless (MySQL sharding), Cadence (workflow engine)
- **Networking:** gRPC, HTTP/2, Envoy, circuit breakers
- **Database:** MySQL at scale, sharding, replication lag handling

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| L4 (PE) | $150-190K | 10-15% | $300-500K | $240-330K |
| L5 (Sr PE) | $190-250K | 15-20% | $500-900K | $360-510K |
| L6 (Staff) | $250-320K | 20-25% | $900-1.5M | $520-720K |

### Study Timeline: 10 weeks

Breadth across all 15 labs. Focus on Kafka (Lab 13), rate limiting (Lab 14), and distributed systems design.

---

## Stripe — Infrastructure Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 60 min | Coding | Algorithms (medium) |
| On-site R1 | 45 min | Coding | Algorithms + API design |
| On-site R2 | 45 min | System Design | Reliable payment infrastructure |
| On-site R3 | 45 min | Debugging | Production issue analysis |
| On-site R4 | 45 min | Behavioral + API Design | API philosophy, incident experience |

### Coding vs Debugging vs System Design Split: 35% / 25% / 40%

### On-Call Simulation Expectations

Stripe's debugging round often involves API reliability scenarios — idempotency, retry safety, exactly-once processing failures. Given Stripe's focus on financial infrastructure, they emphasize data integrity and consistency.

**Practice with:** Lab 05 (slow query/transaction deadlock), Lab 07 (circuit breaker), Lab 14 (rate limiting), Lab 15 (DR failover)

### Incident Response Round

Stripe emphasizes systematic incident response for financial systems:
- **Idempotency** — how to retry safely
- **Exactly-once processing** — deduplication strategies
- **Data integrity** — reconciliation, audit trails
- **Incident communication** — customer-facing status updates

### Infrastructure/Networking Round Content

- **Stripe's Stack:** Mina (load balancer), Vampire (KV store), Sorbet (type checker on Ruby)
- **API Design:** Versioning, rate limiting, idempotency keys
- **Payment Infrastructure:** Multi-provider routing, fraud detection, 3DS

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| L3 (Infra Eng) | $140-180K | 10% | $300-500K | $220-320K |
| L4 (Sr Infra) | $180-240K | 15% | $500-900K | $330-480K |
| L5 (Staff) | $240-310K | 20% | $900-1.8M | $500-720K |

### Study Timeline: 8 weeks

Focus on API design, idempotency patterns, data integrity. Labs 05, 07, 14, 15.

---

## Datadog — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | Observability + monitoring philosophy |
| On-site R1 | 45 min | Coding | Algorithms + scripting |
| On-site R2 | 45 min | System Design | Monitoring at scale |
| On-site R3 | 45 min | Observability Deep Dive | Metrics, logs, traces design |
| On-site R4 | 45 min | Behavioral + Customer | Customer-facing incident communication |

### Coding vs Debugging vs System Design Split: 25% / 25% / 50%

### On-Call Simulation Expectations

Datadog interviews focus heavily on observability. You might be asked: "Design a monitoring system for 10K microservices" or "How would you detect an anomaly across 1M time series?" They expect deep knowledge of Prometheus, Datadog, Grafana internals.

**Practice with:** Lab 03 (high CPU — monitoring perspective), Lab 07 (circuit breaker — metrics design), Lab 08 (cache stampede — monitoring cache hit ratios)

### Incident Response Round

Datadog wants engineers who understand monitoring from the provider perspective:
- **Metric design:** Cardinality, resolution, retention tradeoffs
- **Alert fatigue:** Reducing false positives, multi-window burn rate alerts
- **Distributed tracing:** Sampling strategies, context propagation
- **Log management:** Indexing strategies, log volume reduction

### Infrastructure/Networking Round Content

- **Observability Stack:** Agent architecture, data ingestion pipeline, query engine design
- **Networking:** gRPC, protobuf, compression, TLS termination
- **Storage:** Time-series database design, columnar storage, compaction strategies

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| SRE 2 | $140-180K | 10% | $200-400K | $210-300K |
| Sr SRE | $180-240K | 15% | $400-800K | $320-480K |
| Staff SRE | $240-300K | 20% | $800-1.5M | $480-660K |

### Study Timeline: 8 weeks

Focus on monitoring design, time-series fundamentals, alerting theory. Labs 03, 07, 08.

---

## Cloudflare — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | Networking + security fundamentals |
| On-site R1 | 45 min | Networking Deep Dive | TCP, HTTP, DNS, DDoS mitigation |
| On-site R2 | 45 min | Coding | Algorithms + systems programming |
| On-site R3 | 45 min | System Design | CDN, edge computing |
| On-site R4 | 45 min | Behavioral + Security | Security incident response |

### Coding vs Debugging vs System Design Split: 20% / 25% / 55%

### On-Call Simulation Expectations

Cloudflare's network is the edge. Expect scenarios involving DDoS mitigation, DNS resolution failures, TLS termination issues, WAF rule conflicts. They test deep networking knowledge under pressure.

**Practice with:** Lab 10 (security incident — DDoS, WAF bypass), Lab 11 (TLS cert expiry — edge case)

### Incident Response Round

Cloudflare handles global-scale incidents affecting millions of sites:
- **DDoS response:** Mitigation strategies, scrubbing center routing, rate limiting
- **DNS incidents:** DNSSEC validation failures, cache poisoning, propagation delays
- **TLS/SSL issues:** Certificate provisioning, auto-renewal, OCSP stapling failures
- **WAF/security rules:** False positives vs false negatives, rate of change management

### Infrastructure/Networking Round Content

- **Networking:** BGP, anycast, TCP optimization (BBR, Cubic), QUIC, HTTP/3
- **Security:** DDoS mitigation, WAF, Bot Management, TLS 1.3, Zero Trust
- **Edge Computing:** Workers (V8 isolates), KV store, Durable Objects, R2 storage
- **CDN:** Caching strategies, purge mechanics, origin pull optimization

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| SRE II | $140-180K | 10% | $150-300K | $190-270K |
| Sr SRE | $180-240K | 15% | $300-600K | $290-420K |
| Staff SRE | $240-300K | 20% | $600-1.2M | $430-620K |

### Study Timeline: 8 weeks

Networking focus: TCP/IP, DNS, TLS, BGP, CDN. Labs 10, 11, 14. Know Cloudflare's architecture inside out.

---

## Databricks — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Coding | Algorithms (medium) |
| On-site R1 | 45 min | Coding + Distributed Systems | Distributed algorithms |
| On-site R2 | 45 min | System Design | Data infrastructure at scale |
| On-site R3 | 45 min | Debugging | Spark/Databricks platform debugging |
| On-site R4 | 45 min | Behavioral + Data | Big data incident handling |

### Coding vs Debugging vs System Design Split: 35% / 30% / 35%

### On-Call Simulation Expectations

Databricks focuses on data platform reliability. You may be asked to debug a Spark job failure, investigate shuffle performance, or diagnose Delta Lake consistency issues. Understanding distributed data processing is critical.

**Practice with:** Lab 05 (slow query — similar to Spark query optimization), Lab 09 (disk space — data platform capacity), Lab 13 (Kafka consumer lag — data pipeline debugging)

### Incident Response Round

Databricks platform incidents often involve:
- **Spark job failures:** Stages, tasks, shuffle, memory pressure
- **Delta Lake issues:** Transaction log conflicts, file compaction, vacuum operations
- **Cluster provisioning:** Auto-scaling delays, spot instance preemption, networking setup
- **Data pipeline delays:** Upsert failures, schema evolution issues, CDC lag

### Infrastructure/Networking Round Content

- **Data Infrastructure:** Apache Spark, Delta Lake, MLflow, Unity Catalog
- **Cloud:** Multi-cloud (AWS, Azure, GCP), IAM, networking, encryption
- **Monte Carlo / Lakehouse:** Data reliability, quality checks, lineage

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| L4 (SRE) | $150-190K | 10% | $300-500K | $240-340K |
| L5 (Sr SRE) | $190-260K | 15% | $500-1M | $360-530K |
| L6 (Staff) | $260-330K | 20% | $1-2M | $540-800K |

### Study Timeline: 10 weeks

Focus on Spark internals, Delta Lake, distributed data processing. Labs 05, 09, 13.

---

## LinkedIn — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | Coding + systems fundamentals |
| On-site R1 | 45 min | Coding | Algorithms (medium) |
| On-site R2 | 45 min | System Design | Distributed systems |
| On-site R3 | 45 min | Debugging | Production incident walkthrough |
| On-site R4 | 45 min | Behavioral + Leadership | Ownership, mentoring |

### Coding vs Debugging vs System Design Split: 30% / 30% / 40%

### On-Call Simulation Expectations

LinkedIn operates at massive scale with its own infrastructure (not public cloud). You may be asked to debug issues in Espresso (NoSQL), Voldemort (KV store), or Samza (Kafka-based stream processing).

**Practice with:** Lab 02 (thread deadlock), Lab 08 (cache stampede — Voldemort context), Lab 13 (Kafka consumer lag — Samza context)

### Incident Response Round

LinkedIn follows Google SRE incident management model with emphasis on:
- **Incremental deployment:** Gradual rollouts with automated rollback
- **Feature flags:** Kill switches for immediate mitigation
- **Load shedding:** Dropping low-priority traffic during overload

### Infrastructure/Networking Round Content

- **LinkedIn Stack:** Espresso, Voldemort, Samza, Venice, Kafka
- **Networking:** Rest.li (RPC framework), HTTP/2, gRPC
- **Observability:** In-house monitoring, metrics platform, distributed tracing

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| SRE (M) | $140-180K | 10% | $200-400K | $210-310K |
| Sr SRE (Sr) | $180-240K | 15% | $400-700K | $320-460K |
| Staff SRE | $240-290K | 20% | $700-1.2M | $440-620K |

### Study Timeline: 8 weeks

Focus on Kafka, caching, distributed KV stores. Labs 02, 08, 13.

---

## Twitter/X — Site Reliability Engineer

### Interview Process

| Round | Duration | Type | Focus |
|-------|----------|------|-------|
| Phone Screen | 45 min | Technical | Systems + coding |
| On-site R1 | 45 min | Coding | Algorithms |
| On-site R2 | 45 min | System Design | High-scale social platform |
| On-site R3 | 45 min | Debugging | Production incident analysis |
| On-site R4 | 45 min | Behavioral | Incident leadership |

### Coding vs Debugging vs System Design Split: 30% / 35% / 35%

### On-Call Simulation Expectations

Twitter/X handles extreme traffic spikes (Super Bowl, elections, breaking news). Expect scenarios about cascading failures, cache stampedes during viral events, and rate limiting during API abuse.

**Practice with:** Lab 04 (connection pool — traffic spike scenario), Lab 08 (cache stampede — viral event), Lab 14 (API rate limiting)

### Incident Response Round

Twitter's incident response must handle:
- **Viral event traffic:** 10-100x normal traffic in minutes
- **Cascading failures:** One service failure cascading to others
- **Real-time constraints:** Timeline delivery in milliseconds
- **Global scale:** Ensuring consistency across data centers

### Infrastructure/Networking Round Content

- **Twitter Stack:** Finagle (RPC), Manhole (debugging), Scalab (build), Mesos/Aurora
- **Networking:** Load balancing at extreme scale, custom HTTP servers, Pelikan cache
- **Storage:** Manhattan (KV), MySQL at scale, Blobstore, EventBus

### Compensation Data

| Level | Base Salary | Bonus | RSU (4yr) | Total Comp |
|-------|-------------|-------|-----------|------------|
| SRE 2 | $140-180K | 10% | $200-400K | $200-300K |
| Sr SRE | $180-240K | 15% | $400-800K | $320-460K |
| Staff SRE | $240-300K | 20% | $800-1.5M | $480-660K |

### Study Timeline: 8 weeks

Focus on caching, rate limiting, traffic management at scale. Labs 04, 08, 14.

---

## General SRE Interview Study Timeline Recommendations

### 4-Week Crash Course (Urgent Interview)

| Week | Focus | Labs | LeetCode | System Design |
|------|-------|------|----------|---------------|
| 1 | JVM & Concurrency | 01, 02, 03 | Top 5 concurrency | Monitoring basics |
| 2 | Database & Deployment | 04, 05, 06 | Top 5 medium | Reliability patterns |
| 3 | Distributed Systems | 07, 08, 13 | Top 5 hard | DR design |
| 4 | Security & Behavioral | 09, 10, 11, 12, 14, 15 | Review | Full mock |

### 8-Week Production Engineering Mastery

| Week | Focus |
|------|-------|
| 1-2 | All 15 labs — read & understand |
| 3-4 | LeetCode Top 30 SRE problems + Java templates |
| 5-6 | System design (monitoring, DR, capacity planning) |
| 7 | Mock interviews + behavioral STAR prep |
| 8 | Company-specific research + final review |

### 12-Week SRE Expert Program

| Week | Focus |
|------|-------|
| 1-3 | Deep dive labs 01-05 + implement solutions from scratch |
| 4-6 | Deep dive labs 06-10 + LeetCode SRE problems |
| 7-9 | Deep dive labs 11-15 + system design deep dive |
| 10-11 | Advanced topics (chaos engineering, observability stack) |
| 12 | Mock interviews, company research, behavioral prep |

---

## Compensation Benchmarks by Role

| Role | Total Comp Range (L4/L5 equivalent) |
|------|--------------------------------------|
| SRE (Google) | $220K-$580K |
| Production Engineer (Meta) | $270K-$700K |
| Systems Engineer (Amazon) | $200K-$650K |
| Azure SRE (Microsoft) | $160K-$520K |
| SRE (Apple) | $200K-$660K |
| Cloud Engineer (Netflix) | $250K-$850K |
| Production Engineer (Uber) | $240K-$720K |
| Infrastructure Engineer (Stripe) | $220K-$720K |
| SRE (Datadog) | $210K-$660K |
| SRE (Cloudflare) | $190K-$620K |
| SRE (Databricks) | $240K-$800K |
| SRE (LinkedIn) | $210K-$620K |
| SRE (Twitter/X) | $200K-$660K |

---

## Company Comparison: What Each Values Most

| Company | Primary Focus | Secondary | Labs Most Relevant |
|---------|--------------|-----------|-------------------|
| Google | System design + troubleshooting | Distributed systems depth | 01, 02, 03, 15 |
| Meta | Coding + production debugging | Speed under pressure | 02, 03, 04, 05 |
| Amazon | Leadership + operational excellence | Scalability | 04, 05, 09, 14, 15 |
| Microsoft | Azure depth + incident response | Cloud-native patterns | 06, 11, 12, 15 |
| Apple | Security + hardware-software | Privacy-preserving ops | 10, 11 |
| Netflix | Chaos engineering + resilience | Blast radius awareness | 07, 08, 15 |
| Uber | Breadth + Kafka/distributed | Caching | 01-15 (breadth), 13, 14 |
| Stripe | API design + data integrity | Payment reliability | 05, 07, 14, 15 |
| Datadog | Observability + monitoring | Metrics at scale | 03, 07, 08 |
| Cloudflare | Networking + security | Edge computing | 10, 11, 14 |
| Databricks | Data infrastructure + Spark | Lakehouse reliability | 05, 09, 13 |
| LinkedIn | Kafka + KV stores | Incremental deployment | 02, 08, 13 |
| Twitter/X | Caching + traffic spikes | Real-time constraints | 04, 08, 14 |
