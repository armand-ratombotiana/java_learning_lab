# Mock Interview: Availability

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Site Reliability Engineer Interviewer  
**Candidate Level**: Senior SDE (L5)  
**Problem**: Design a highly available payment processing system with 99.99% uptime.

---

## Transcript

**Interviewer**: "We need a payment processing system with 99.99% uptime. Walk me through the architecture and how you achieve that SLA."

**Candidate**: "99.99% is ~53 minutes of downtime per year. That means we need redundancy at every layer: multi-AZ deployment, zero single points of failure, automated failover, and graceful degradation."

**Interviewer**: "Start with the high-level architecture."

**Candidate**: "Multi-region active-active deployment. Each region has: 1) API Gateway with rate limiting, 2) Payment Service (stateless, auto-scaled), 3) Ledger Service (stateful, with synchronous replication), 4) Queue for async settlement. DNS-based traffic routing with health check-driven failover."

**Interviewer**: "How do you handle database failover without data loss?"

**Candidate**: "Ledger uses synchronous replication across 3 AZs in a region. We use a consensus-based database (like CockroachDB or Spanner-style). If the primary AZ fails, the system automatically elects a new leader from the remaining replicas. No data loss because writes are acknowledged only after replication to all replicas."

**Interviewer**: "What about a full region failure?"

**Candidate**: "DNS-based routing switches traffic to the secondary region. The secondary region must have the latest data — we use CDC (Change Data Capture) from the primary region's database to replicate changes asynchronously. A small amount of data loss is possible here (last few seconds). To minimize: synchronous replication within region for most data, async across regions for worst-case DR."

**Interviewer**: "How do you test this?"

**Candidate**: "Chaos engineering: regular GameDay exercises where we kill AZs, networks, and services. Automated canary deployments with health-check-based rollback. And always have a 'chaos monkey' randomly terminating instances in production during low-traffic periods."

**Interviewer**: "How do you handle a downstream dependency failing?"

**Candidate**: "Circuit breaker pattern. If payment processor A is having issues, we fail fast and retry through payment processor B. If both fail, we queue the payment for retry and notify the user. The system degrades gracefully: synchronous payment → 2-hour async settlement → queued for next-day processing."

---

## Key Takeaways

- **99.99% means 53 min/year downtime — design accordingly**
- **Multi-AZ + Multi-region**: Redundancy at every level
- **Synchronous replication**: Within region for zero data loss
- **Async replication**: Cross-region for disaster recovery
- **Circuit breakers**: Graceful degradation when dependencies fail
- **Chaos engineering**: Validate failure modes proactively
