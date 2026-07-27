# Mock Interview: Database Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Data Infrastructure Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a distributed SQL database for a multi-tenant SaaS application.

---

## Transcript

**Interviewer**: "Design a database for a multi-tenant SaaS platform. Tenants have 1GB to 1TB of data. Need to support SQL queries, ACID transactions within a tenant, and horizontal scaling."

**Candidate**: "Multi-tenancy with varying sizes is tricky. I'd use a hybrid approach: small tenants share a database node, large tenants get dedicated nodes. We need a proxy layer that routes queries based on tenant ID."

**Interviewer**: "What's the high-level architecture?"

**Candidate**: "A query proxy/router that parses the tenant ID from the connection (or query). It maps tenant → shard via a configuration database. Each shard is a PostgreSQL instance (or compatible) running on a container. The proxy handles: 1) Routing, 2) Connection pooling, 3) Query rewriting for tenant isolation, 4) Rate limiting."

**Interviewer**: "How do you migrate a growing tenant from shared to dedicated?"

**Candidate**: "When a tenant's data exceeds a threshold (e.g., 50GB), we schedule migration. Steps: 1) Snapshot the tenant's schema and data, 2) Restore to a dedicated PostgreSQL instance, 3) Start CDC replication from shared to dedicated, 4) Catch up replication, 5) Cut over traffic to dedicated instance. Downtime is a few seconds during cutover."

**Interviewer**: "How do you handle hot tenants that overload their shard?"

**Candidate**: "Per-tenant resource limits at the query proxy layer: max connections, max QPS, max query complexity. If a tenant exceeds limits, their queries get queued or rejected with 429. Automatic detection: if a tenant's load impacts shard-level metrics (CPU > 80%, I/O > threshold), we flag them for dedicated migration or further sharding."

**Interviewer**: "What about cross-tenant queries?" 

**Candidate**: "Isolation principle: cross-tenant queries are not allowed by default. If needed (for admin reporting), we run a separate analytics cluster that consolidates data via data pipeline. The operational database is never queried across tenants to ensure performance isolation."

---

## Key Takeaways

- **Multi-tenant requires resource isolation**: Share small, isolate large
- **Proxy-based routing**: Tenant → shard mapping with connection management
- **Live migration**: Snapshot + CDC for zero-downtime tenant moves
- **Resource governance**: Per-tenant limits prevent noisy neighbor
- **Analytics isolation**: Cross-tenant queries go through separate pipeline
