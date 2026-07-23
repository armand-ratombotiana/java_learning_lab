# Company Behavioral Guide — SRE / Production Engineering

## How to Use This Guide

For each company, this guide provides: SRE-specific behavioral questions, STAR answers framed around the 15 incident-response labs, on-call experience framing, incident command examples, and questions to ask as a candidate. Each STAR template maps to specific labs so you can practice with real production scenarios.

---

## Section 1: Universal SRE Behavioral Questions (All Companies)

### Q1: "Tell me about the worst production incident you handled."

**STAR Template (use any lab as your scenario):**

| Component | Example (Lab 04 — Connection Pool Exhaustion) |
|-----------|-----------------------------------------------|
| **S**ituation | "Our payment gateway was timing out on 30% of transactions during Black Friday peak at 5M requests per minute. Customers couldn't complete purchases." |
| **T**ask | "I was the primary on-call SRE for the payment platform. My responsibility was to restore service and minimize revenue impact." |
| **A**ction | "1) Acknowledged the page within 2 minutes. 2) Checked dashboards: CPU normal, memory normal, error rate spiking. 3) Found connection pool exhausted — all 50 connections were in-use with 200 requests queued. 4) Identified a deployment 10 minutes prior added a database call in a hot path without proper connection management. 5) Rolled back the deployment. 6) Restarted the service to clear stuck connections. 7) Added missing try-with-resources to ensure connections are returned. 8) Verified with load test." |
| **R**esult | "Error rate dropped from 30% to 0.1% within 5 minutes. Post-mortem added mandatory try-with-resources lint check to CI/CD. No recurrence in 6 months." |

**Alternative labs for this answer:** Lab 01 (memory leak), Lab 02 (thread deadlock), Lab 03 (high CPU), Lab 06 (deployment rollback), Lab 07 (circuit breaker), Lab 08 (cache stampede), Lab 13 (Kafka consumer lag)

### Q2: "How do you balance feature velocity with reliability?"

**STAR Template:**

| Component | Example |
|-----------|---------|
| **S**ituation | "Our team was under pressure to ship a new checkout experience for Q4. The feature required significant database schema changes." |
| **T**ask | "I needed to ensure we could ship fast without compromising the 99.99% availability SLO." |
| **A**ction | "1) Proposed an error budget policy: 30% of budget for experiments, 70% reserved for production. 2) Required feature flags for the new checkout flow (kill switch). 3) Mandated canary deployment: 1% → 10% → 25% → 100% with automated rollback gates. 4) Added SLO monitoring for the new feature separately. 5) Ran a load test to establish baseline before release." |
| **R**esult | "Shipped on time. Experienced one near-miss (P99 latency spiked at 25% traffic — auto-rollback triggered in 2 minutes). Error budget spent: 12% (well within budget). Incident count: 0 for the new feature." |

**Lab connection:** Lab 06 (deployment rollback), Lab 07 (circuit breaker), Lab 14 (rate limiting)

### Q3: "What do you do when you don't know how to fix an issue?"

**Expected answer structure:**
1. **Don't panic** — acknowledge the page, declare severity
2. **Mitigate first** — rollback, feature flag, scale up (buy time)
3. **Escalate** — pull in subject matter experts
4. **Research** — check runbooks, similar incidents, vendor docs
5. **Collaborate** — pair with a colleague, share screen, think aloud
6. **Learn** — update runbook after resolution

**Lab connection:** Every lab's `CHECKLIST.md` provides the runbook for that specific incident type.

### Q4: "How do you handle on-call burnout?"

**Key points:**
- Track toil percentage (< 50% target)
- Automate repetitive pages (auto-remediation runbooks)
- Follow-the-sun rotation to minimize night shifts
- Post-incident decompression time
- Incident review: is this alert necessary? Can we improve?
- Blameless culture means it's OK to escalate

---

## Section 2: Company-Specific Behavioral Questions

### Google SRE

**Common Questions:**
1. "Tell me about a time you made a system more reliable."
2. "How do you prioritize reliability work vs feature work?"
3. "Describe a conflict you had with a product team about reliability."
4. "What's your philosophy on error budgets?"
5. "Tell me about a post-mortem you led."

**STAR Answer — "Tell me about a post-mortem you led":**

| Component | Example (Lab 10 — Security Incident Response) |
|-----------|-----------------------------------------------|
| **S**ituation | "A security vulnerability was exposed through our CDN. Customer data was potentially accessible for 4 hours before detection." |
| **T**ask | "I was responsible for leading the post-mortem and ensuring we learned from the incident without blaming individuals." |
| **A**ction | "1) Scheduled post-mortem within 48 hours. 2) Emphasized blameless culture — we're analyzing systems not people. 3) Created a timeline from logs, deploys, and alerts. 4) Used 5 Whys: Why was data exposed? → CDN rule misconfigured. → Why wasn't config reviewed? → No automated validation. → Why no validation? → Config as code not implemented. 5) Generated 5 action items with owners and deadlines. 6) Presented to engineering all-hands." |
| **R**esult | "All 5 action items completed within 2 weeks. Config as code now required for all CDN changes. Automated validation catches misconfigurations. 0 similar incidents in 12 months." |

**Questions to ask Google SRE:**
- "What does your error budget policy look like? How is it enforced?"
- "How do you balance SRE ownership vs dev team ownership of services?"
- "What's your on-call rotation like? What's the page frequency?"
- "How do you handle the 'SRE is a bottleneck' problem?"

### Meta (Facebook) ProdEng

**Common Questions:**
1. "Tell me about the hardest production bug you've ever debugged."
2. "How do you handle pressure during a SEV1 incident?"
3. "Describe a time you automated away a repetitive operational task."
4. "How do you debug a system you've never seen before?"
5. "What's your approach to learning a new technology stack quickly?"

**STAR Answer — "The hardest production bug you've ever debugged":**

| Component | Example (Lab 03 — High CPU with misleading symptoms) |
|-----------|------------------------------------------------------|
| **S**ituation | "P99 latency jumped from 50ms to 5 seconds. CPU was at 95%. Initial assumption was CPU-bound computation." |
| **T**ask | "I needed to find the root cause — was CPU the cause or a symptom?" |
| **A**ction | "1) Checked timeline: P99 spiked before CPU, so CPU was symptom of queuing. 2) Took thread dump: 200 threads blocked on a single database connection. 3) Checked database side: query was waiting on a row lock from a long-running transaction. 4) Killed the blocking transaction. 5) Identified missing index causing full table scan in the transaction. 6) Added index, added query timeout." |
| **R**esult | "Latency returned to baseline (P99 50ms) within 3 minutes. CPU dropped to 30% after request queue drained. Added slow query alerting to catch future missing indexes." |

**Questions to ask Meta:**
- "How does the PE team interact with feature engineering teams?"
- "What's your debugging toolchain for production issues?"
- "How do you handle the scale of Meta's infrastructure?"
- "What does a typical on-call shift look like?"

### Amazon (Systems Engineer / SDE)

**Common Questions (with Leadership Principle mapping):**
1. "Tell me about a time you delivered results under difficult circumstances." (Deliver Results)
2. "Describe a time you had to dive deep into a technical problem." (Dive Deep)
3. "Tell me about a time you disagreed with your manager." (Have Backbone; Disagree and Commit)
4. "Describe how you've owned a problem end-to-end." (Ownership)
5. "Tell me about a time you improved a process." (Learn and Be Curious)

**STAR Answer — "Dive deep" (Lab 05 — Slow Query / Deadlock Resolution):**

| Component | Example |
|-----------|---------|
| **S**ituation | "A critical financial batch job went from 1 hour to 8 hours. The business couldn't close the books." |
| **T**ask | "I needed to find why the execution plan regressed and restore performance." |
| **A**ction | "1) Compared AWR reports between normal and slow runs. 2) Found the execution plan changed — the optimizer chose a full table scan instead of an index scan. 3) Checked statistics: they were stale (last updated 30 days ago, data changed 40%). 4) Refreshed statistics. 5) The query still chose the bad plan — added an index hint as temporary fix. 6) Root cause: auto-stats job was disabled during maintenance. 7) Re-enabled auto-stats, added plan baseline to prevent regression." |
| **R**esult | "Batch job returned to 1-hour runtime. Plan baseline prevents recurrence. Added alert on stale statistics (> 7 days without refresh)." |

**Questions to ask Amazon:**
- "How do you apply the Operational Excellence pillar in day-to-day work?"
- "What's the on-call rotation like for this team?"
- "How does the team use error budgets to balance velocity and reliability?"
- "What's your post-mortem culture like?"

### Microsoft (Azure SRE)

**Common Questions:**
1. "Describe an incident where you used Azure tools to diagnose and resolve."
2. "How do you ensure high availability for cloud-native applications?"
3. "Tell me about a time you automated infrastructure deployment."
4. "How do you handle legacy systems that can't be easily migrated?"
5. "Describe your experience with Azure DevOps and CI/CD pipelines."

**STAR Answer — "Using Azure tools to diagnose" (Lab 12 — K8s CrashLoop with AKS):**

| Component | Example |
|-----------|---------|
| **S**ituation | "A microservice on AKS went into CrashLoopBackOff after a deployment. All replicas were restarting every 30 seconds." |
| **T**ask | "I needed to diagnose why the pod was crashing and restore service using Azure-native tools." |
| **A**ction | "1) Checked Azure Monitor for AKS — CPU/memory looked normal. 2) Ran `kubectl describe pod` — liveness probe was failing. 3) Checked Application Insights — the app was throwing a NullPointerException on startup. 4) Reviewed Azure DevOps deployment — a config value was missing from the new version's ConfigMap. 5) Rolled back to the previous deployment slot. 6) Verified via Azure Monitor that all pods returned to Running state. 7) Root cause: environment-specific config wasn't included in the Helm chart." |
| **R**esult | "Service restored in 8 minutes. Added config validation to CI/CD pipeline — deployment is blocked if required config values are missing." |

**Questions to ask Microsoft:**
- "How does the SRE team interact with Azure product teams?"
- "What's your approach to incident management across Azure services?"
- "How do you handle multi-cloud or hybrid scenarios?"
- "What's the on-call rotation like? Follow-the-sun?"

### Apple (Site Reliability Engineer)

**Common Questions:**
1. "Tell me about a time you handled a security incident."
2. "How do you design systems that are both reliable and privacy-preserving?"
3. "Describe a time you debugged a hardware-software integration issue."
4. "How do you handle certificate management at scale?"
5. "What's your approach to incident communication?"

**STAR Answer — "Handling a security incident" (Lab 11 — TLS Certificate Expiry):**

| Component | Example |
|-----------|---------|
| **S**ituation | "Push notifications stopped delivering to iOS devices. Users reported missing alerts for critical apps." |
| **T**ask | "I needed to restore push delivery and understand why certificates failed." |
| **A**ction | "1) Checked APNS certificate — expired 2 hours ago. 2) Immediate mitigation: deployed a new certificate from our CA. 3) Verfied push delivery resumed. 4) Root cause: the certificate renewal automation had a bug — it didn't account for the 3-year validity and missed the renewal window. 5) Fixed automation to check 30/14/7 days before expiry. 6) Added monitoring for certificate expiry (Prometheus blackbox exporter + cert expiration metrics). 7) Ran a post-mortem and added certificate expiry to the weekly operations review." |
| **R**esult | "Push delivery restored within 15 minutes. Certificate monitoring now catches expiry across all internal and external certificates. Zero renewal misses in 18 months." |

**Questions to ask Apple:**
- "How does SRE work with hardware engineering teams?"
- "What's your approach to privacy-preserving monitoring?"
- "How do you handle certificate lifecycle management at Apple's scale?"
- "What does the on-call rotation look like for critical services?"

### Netflix (Cloud Engineer)

**Common Questions:**
1. "Tell me about a time you designed a chaos engineering experiment."
2. "How do you balance autonomy with reliability?"
3. "Describe a time you implemented a circuit breaker or bulkhead pattern."
4. "How do you handle a service that degrades gracefully?"
5. "Tell me about a time you automated recovery."

**STAR Answer — "Designing a chaos engineering experiment" (Lab 07 — Circuit Breaker):**

| Component | Example |
|-----------|---------|
| **S**ituation | "Our recommendation service had a cascading failure when the downstream ML scoring service became slow. One slow service brought down the entire recommendations stack." |
| **T**ask | "I needed to run a chaos experiment to validate that our circuit breaker implementation would prevent cascading failures." |
| **A**ction | "1) Defined steady state: P99 latency < 200ms, error rate < 0.1%. 2) Hypothesis: injecting 2s latency into ML scoring will trigger circuit breaker within 30s, limiting blast radius to 5% of requests. 3) Used Chaos Monkey to inject latency into the ML scoring service (5% of traffic initially). 4) Observed circuit breaker opened after 15 50x errors in 30s window. 5) Confirmed blast radius: only 3% of recommendations degraded, rest served from cache. 6) Increased experiment to 25% — circuit breaker held. 7) Documented results and added to CI/CD gating." |
| **R**esult | "Experiment validated the circuit breaker works as designed. Added automated chaos experiments to the release pipeline. Reduced MTTR for downstream failures from 45 minutes to near-zero (auto-circuit break)." |

**Questions to ask Netflix:**
- "How do you decide which services need chaos experiments?"
- "What's your approach to blast radius during experiments?"
- "How do you balance developer autonomy with platform reliability?"
- "What does gameday look like at Netflix?"

### Uber (Production Engineer)

**Common Questions:**
1. "Tell me about a time you debugged a distributed systems issue."
2. "How do you handle Kafka consumer lag?"
3. "Describe a time you improved the reliability of a high-traffic system."
4. "How do you approach capacity planning?"
5. "Tell me about a time you mentored a junior engineer."

**STAR Answer — "Debugging Kafka consumer lag" (Lab 13 — Kafka Consumer Lag):**

| Component | Example |
|-----------|---------|
| **S**ituation | "Our real-time trip tracking system had a 15-minute delay. Riders couldn't see their driver's location in real-time." |
| **T**ask | "I needed to identify why consumer lag increased from 10ms to 15 minutes and restore real-time tracking." |
| **A**ction | "1) Checked Kafka consumer lag metrics — lag was 15 minutes and growing. 2) Checked consumer group status — one consumer was rebalancing repeatedly. 3) Identified the rebalance was caused by a consumer timing out during processing. 4) Found the consumer had a long GC pause (10 seconds) due to heap pressure. 5) Increased heap from 2GB to 4GB and reduced `max.poll.interval.ms`. 6) Verified rebalancing stopped and lag drained. 7) Added consumer lag alerting (threshold: 10 seconds)." |
| **R**esult | "Lag returned to 10ms within 5 minutes. Consumer stability improved — no rebalances in 3 months. Added lag dashboard to team's monitoring." |

**Questions to ask Uber:**
- "How do you handle Kafka at Uber's scale?"
- "What's your approach to multi-region deployments?"
- "How does on-call work for critical infrastructure?"
- "What's your error budget policy?"

### Stripe (Infrastructure Engineer)

**Common Questions:**
1. "Tell me about a time you ensured exactly-once processing."
2. "How do you design idempotent APIs?"
3. "Describe a time you handled a data integrity issue."
4. "How do you approach incident communication with external customers?"
5. "Tell me about a time you designed a system for payment reliability."

**STAR Answer — "Handling a data integrity issue" (Lab 05 — Database Deadlock):**

| Component | Example |
|-----------|---------|
| **S**ituation | "A database deadlock was causing payment transactions to fail. The deadlock victim was rolled back automatically, but clients received no confirmation." |
| **T**ask | "I needed to detect and resolve deadlocks without losing payment data." |
| **A**ction | "1) Enabled deadlock logging in PostgreSQL. 2) Analyzed deadlock graph — two transactions were updating the same rows in different order. 3) Standardized lock ordering across all transaction code. 4) Added retry logic with exponential backoff for deadlock victims. 5) Added idempotency keys — if a transaction is retried after deadlock, it's detected as duplicate and returns the original result. 6) Added monitoring for deadlock events." |
| **R**esult | "Deadlocks reduced to zero. Retry logic ensures 0 data loss during transient deadlocks. Idempotency keys prevent duplicate charges." |

**Questions to ask Stripe:**
- "How do you handle financial reconciliation at Stripe's scale?"
- "What's your approach to idempotency and exactly-once semantics?"
- "How does the infrastructure team collaborate with product engineering?"
- "What's your incident response process for payment-related issues?"

### Datadog (SRE)

**Common Questions:**
1. "How do you design monitoring for a system you've never seen before?"
2. "Tell me about a time you reduced alert fatigue."
3. "How do you set meaningful SLOs for a new service?"
4. "Describe a time you used observability tools to debug a complex issue."
5. "How do you handle high-cardinality metrics?"

**STAR Answer — "Reducing alert fatigue" (Lab 03 + Lab 04 — monitoring design):**

| Component | Example |
|-----------|---------|
| **S**ituation | "Our team received 50+ alerts per shift. Most were noise — transient spikes that self-resolved. Engineers ignored pages." |
| **T**ask | "I needed to reduce alert fatigue so on-call engineers would respond when it really mattered." |
| **A**ction | "1) Audited all alerts: 70% were paging on single-data-point spikes. 2) Implemented multi-window multi-burn-rate alerts: 1h window + 6h window. 3) Added 'for' duration (5m) before firing. 4) Grouped related alerts into a single notification. 5) Created alert severity tiers: page-worthy vs ticket-worthy. 6) Introduced alerting on SLO burn rate instead of raw metrics." |
| **R**esult | "Page frequency dropped from 50 to 3 per shift. SEA (Signal-to-Noise ratio) improved from 0.2 to 0.9. MTTR improved because engineers actually responded to pages." |

**Questions to ask Datadog:**
- "How do you handle metrics at petabyte scale?"
- "What's your approach to metric cardinality management?"
- "How does the SRE team use Datadog's own products internally?"
- "What's your on-call experience like?"

### Cloudflare (SRE)

**Common Questions:**
1. "Tell me about a time you mitigated a DDoS attack."
2. "How do you handle global-scale network incidents?"
3. "Describe your experience with DNS and TLS."
4. "How do you test changes to the edge network?"
5. "What's your approach to zero-trust security?"

**STAR Answer — "Mitigating a network incident" (Lab 10 — Security Incident / DDoS):**

| Component | Example |
|-----------|---------|
| **S**ituation | "Our CDN was hit with a volumetric DDoS attack — 500 Gbps. Customer websites were becoming unreachable." |
| **T**ask | "I needed to mitigate the attack while minimizing false positives on legitimate traffic." |
| **A**ction | "1) Verified attack signatures in the edge logs — it was a UDP amplification attack. 2) Enabled rate limiting on the edge: 1000 req/s per IP. 3) Configured WAF rules to block known attack patterns. 4) Scaled up edge capacity to absorb the remaining traffic. 5) Monitored legitimate traffic error rates — 0.1% false positives. 6) After attack subsided, analyzed logs to add permanent mitigation rules. 7) Wrote a post-mortem and updated DDoS runbook." |
| **R**esult | "Attack mitigated within 5 minutes. 99.9% of legitimate traffic unaffected. Permanent rules added. Runbook updated with attack signature." |

**Questions to ask Cloudflare:**
- "How do you test changes to the global edge network?"
- "What's your approach to DDoS mitigation at Cloudflare's scale?"
- "How does the SRE team handle network incidents?"
- "What's your on-call rotation like for global infrastructure?"

### Databricks (SRE)

**Common Questions:**
1. "Tell me about a time you debugged a Spark job failure."
2. "How do you handle data pipeline reliability?"
3. "Describe a time you optimized a slow query."
4. "How do you approach capacity planning for data infrastructure?"
5. "What's your experience with Delta Lake / lakehouse architecture?"

**STAR Answer — "Debugging a Spark job failure" (Lab 05 — Slow Query / Lab 09 — Capacity):**

| Component | Example |
|-----------|---------|
| **S**ituation | "A nightly ETL pipeline that processes 10TB of data started failing. The Spark job was running out of memory and spilling to disk." |
| **T**ask | "I needed to identify the memory pressure root cause and stabilize the pipeline." |
| **A**ction | "1) Checked Spark UI — shuffle spill was 5x the data size. 2) Found data skew: one partition had 70% of the data. 3) Identified a new data source had a hot key with 10M records. 4) Added salting to distribute the hot key across partitions. 5) Increased executor memory from 8GB to 16GB. 6) Added adaptive query execution to handle future skew. 7) Set up alerts on shuffle spill ratio > 2x data size." |
| **R**esult | "ETL completed in 2 hours (down from failing and never finishing). No more OOM failures. Added data skew detection to pipeline validation." |

**Questions to ask Databricks:**
- "How does the SRE team handle Spark job reliability?"
- "What's your approach to multi-cloud infrastructure?"
- "How do you manage Delta Lake table maintenance at scale?"
- "What's on-call like for a data platform?"

---

## Section 3: On-Call Experience Framing

### How to Describe On-Call Experience

**Template:**
> "At my previous company, I participated in a weekly on-call rotation with 4 other SREs. Our rotation was 1 week primary, 4 weeks off. We received an average of 3-5 pages per shift, with SEV1 incidents requiring full incident response. I handled approximately 15 incidents during my on-call tenure, with an average MTTR of 25 minutes for SEV2 and 45 minutes for SEV1. The most common incident types were database connection pool exhaustion (similar to Lab 04) and deployment-related issues (similar to Lab 06)."

**Key metrics to include:**
- Team size and rotation frequency
- Page frequency per shift
- Common incident types
- Your personal MTTR
- Improvement you made to the on-call experience

### Incident Command Experience

**How to describe taking charge during an outage:**

> "During a SEV1 incident where our payment system was down, I assumed the Incident Commander role. I immediately: 1) Declared SEV1 and assembled the response team in our incident Slack channel. 2) Assigned an Operations Lead to investigate the database symptoms, a Communications Lead to draft status updates, and a Scribe to track the timeline. 3) Prioritized mitigation over root cause — directed the team to roll back the most recent deployment. 4) Communicated status updates every 15 minutes to stakeholders. 5) Once mitigated, transitioned to root cause investigation. The entire response lasted 22 minutes from page to resolution."

---

## Section 4: Post-Mortem Culture Discussion

### How to Discuss Blameless Culture

**Key talking points:**
- "I believe incidents are caused by system complexity, not individual mistakes."
- "A good post-mortem focuses on: what happened, why it happened, what prevented faster detection, and what systemic changes prevent recurrence."
- "Every post-mortem produces specific, assigned action items with deadlines."
- "I've seen post-mortems improve on-call experience by 60% just by eliminating the most common alert sources."

**Example from Lab 01 (memory leak):**
> "In the post-mortem for the ThreadLocal memory leak incident, we didn't blame the developer who didn't call remove(). Instead, we asked: Why wasn't there a try-finally pattern enforced? Why didn't our linter catch this? Why didn't monitoring alert on Metaspace growth? We added a custom linter rule, ThreadLocal usage guidelines, and Metaspace growth monitoring. The same bug hasn't recurred."

---

## Section 5: The "Are You a Developer or an Operator?" Question

**How to answer this question (common at Google, Meta, Amazon):**

> "I'm both. I write code to automate operational tasks, build tools that make systems more reliable, and design architectures that reduce toil. I also carry a pager, respond to incidents, and understand the operational reality of running production systems. The best SREs are developers who respect operations and operators who can code."

**Framing by company:**
- **Google:** "I'm an SRE — I apply software engineering to operations problems."
- **Meta:** "I'm a Production Engineer — I build systems and run them."
- **Amazon:** "I'm a Systems Engineer — I own the reliability of my services end-to-end."

---

## Section 6: Error Budget Philosophy Discussion

**Key concepts to discuss:**
- Error budget = 100% - SLO (the allowed unreliability)
- When budget is consumed: freeze feature releases, focus on reliability
- Unused budget can be "spent" on riskier releases
- Error budgets align engineering incentives with reliability

**How to answer "What's your philosophy on error budgets?":**

> "Error budgets are the bridge between product velocity and reliability. They replace emotional arguments ('we need to be more reliable!') with data-driven discussions ('we've used 80% of our error budget this month — should we slow down releases?'). At my previous company, we used error budgets to decide when to run chaos experiments: only when > 50% budget remaining. This ensured experiments didn't risk the SLO."

---

## Section 7: Questions to Ask as an SRE Candidate

**Universal questions (ask at any company):**

| Question | What It Reveals |
|----------|----------------|
| "What does your on-call rotation look like? What's the page frequency?" | Work-life balance, operational maturity |
| "How do you define and enforce SLOs?" | Reliability culture maturity |
| "What's your post-mortem culture? Can you share an example?" | Blameless culture vs blame culture |
| "How do you handle error budget policy?" | Engineering alignment on reliability |
| "What tools are in your monitoring stack?" | Tech stack quality |
| "How does the SRE team interact with feature engineering teams?" | Org structure and collaboration |
| "What's your approach to incident management?" | Incident response maturity |
| "How do you handle toil reduction?" | Automation culture |
| "What's the career progression for SRE at this company?" | Growth opportunities |
| "Can you describe a recent SEV1 and what changed afterward?" | Learning culture |

**Company-specific questions:**

| Company | Suggested Question |
|---------|-------------------|
| Google | "How does your SRE team decide which services to onboard?" |
| Meta | "How does ProdEng differ from traditional SRE at Meta?" |
| Amazon | "How do Leadership Principles apply to operational decisions?" |
| Microsoft | "How does Azure SRE work with Azure product teams on reliability?" |
| Apple | "How do you handle privacy constraints in monitoring and debugging?" |
| Netflix | "How do you balance chaos engineering with customer-facing SLOs?" |
| Uber | "How do you handle Kafka at Uber's scale?" |
| Stripe | "How do you ensure exactly-once processing in payment infrastructure?" |
| Datadog | "How does the SRE team use DogStatsD and the Datadog agent internally?" |
| Cloudflare | "How do you test changes to the global edge network?" |
| Databricks | "How do you manage Spark cluster reliability at scale?" |
| LinkedIn | "How do you handle Kafka infrastructure at LinkedIn scale?" |
| Twitter/X | "How do you manage cache stampedes during viral events?" |

---

## Quick Reference: 15 Labs → Behavioral Answers

| Lab | STAR Scenario | Best Used For |
|-----|---------------|---------------|
| 01 — Memory Leak | Worst incident, technical depth | Google, Meta, Apple |
| 02 — Thread Deadlock | Hardest bug, debugging approach | Meta, Google, LinkedIn |
| 03 — High CPU | Misleading symptoms, systematic triage | Meta, Datadog, Google |
| 04 — Connection Pool | On-call hero story, capacity planning | Amazon, Meta, Uber |
| 05 — Slow Query | Deep dive, database expertise | Amazon, Oracle, Stripe |
| 06 — Deployment Rollback | Process improvement, CI/CD | Microsoft, all companies |
| 07 — Circuit Breaker | Chaos engineering, resilience | Netflix, Stripe, Google |
| 08 — Cache Stampede | Caching strategy, design improvement | Meta, Twitter, LinkedIn |
| 09 — Disk Space | Capacity planning, automation | Amazon, Google, Databricks |
| 10 — Security Breach | Security incident handling | Apple, Cloudflare, all |
| 11 — TLS Certificate | Certificate management | Cloudflare, Apple, Microsoft |
| 12 — K8s CrashLoop | Container orchestration | Microsoft, Google, all |
| 13 — Kafka Consumer Lag | Streaming platform reliability | LinkedIn, Uber, Confluent |
| 14 — Rate Limiting | API reliability, DDoS | Cloudflare, Stripe, Twitter |
| 15 — DR Failover | Disaster recovery design | Amazon, Microsoft, Netflix |
