# Lab 15 — Disaster Recovery Failover: Interview Questions

**Q1: What is the difference between RTO and RPO?**

**Answer:** RTO (Recovery Time Objective): maximum acceptable downtime. How long until the service must be restored. RPO (Recovery Point Objective): maximum acceptable data loss. How much data can we lose? Example: RTO of 1 hour means service must be back within 1 hour. RPO of 15 minutes means we can lose at most 15 minutes of data. RTO drives infrastructure decisions (hot standby vs. cold standby). RPO drives backup frequency.

**Q2: Describe active-active vs. active-passive disaster recovery architectures.**

**Answer:** Active-active: both regions serve traffic simultaneously. Traffic load-balanced across regions. If one region fails, traffic shifts to remaining region(s). Pros: zero RTO for some failure modes, better resource utilization. Cons: complex (data consistency across regions), more expensive. Active-passive: one region serves traffic, the other is on standby. Failover requires switching DNS. Pros: simpler, no data consistency issues. Cons: slower failover (minutes), wasted standby capacity. Choose: active-active for critical, latency-sensitive services; active-passive for cost-sensitive, non-critical services.

**Q3: How do you test a disaster recovery failover?**

**Answer:** 1) Scheduled drills: quarterly failover exercises. 2) Runbook-driven: follow documented steps, measure RTO/RPO. 3) Chaos engineering: inject failures (kill instances, block network, corrupt data) and verify recovery. 4) Game days: simulate disaster scenarios with the team. 5) Automated validation: after failover, run health checks, data integrity verifications, performance tests. 6) Document: actual vs. expected RTO/RPO, gaps found, improvements needed. 7) Surprise drills: unannounced failover tests to simulate real disasters.

**Q4: What services should be prioritized in a disaster recovery plan?**

**Answer:** Classify services into tiers: Tier 1 (Critical): customer-facing APIs, authentication, payment processing, database. Must recover within RTO (e.g., 1 hour). Tier 2 (Important): reporting, admin panels, internal APIs. Recover within 4-8 hours. Tier 3 (Non-critical): analytics, batch processing, internal tools. Recover within 24-48 hours. Prioritize recovery order based on business impact. Tier 1 gets hot or warm standby. Tier 3 can use cold standby or rebuild from backups.

**Q5: How do you handle database failover with minimal data loss?**

**Answer:** 1) Synchronous replication within region: data committed on primary and replica before acknowledging to client (zero data loss within region). 2) Asynchronous replication across regions: data loss limited to replication lag. 3) Automated failover: use managed database services with multi-AZ (RDS Multi-AZ: automatic DNS failover, ~60-120s downtime). 4) For self-managed: use database-native replication (MySQL Group Replication, PostgreSQL Streaming Replication) with automated failover tool (Patroni, Orchestrator). 5) Verify: test failover drills, measure actual RPO (replication lag), adjust configurations.

**Q6: Your primary region is down. Walk through the failover process.**

**Answer:** 1) Declare regional disaster (SEV1). 2) Verify primary region is unrecoverable within RTO. 3) Initiate failover to secondary region. 4) Database: promote secondary to primary (if active-passive). 5) DNS: update Route53 to point to secondary region (TTL should be 60s or less). 6) Scale up secondary region for increased load. 7) Verify: health checks pass, data integrity confirmed, error rate normal. 8) Announce failover to stakeholders. 9) Begin post-mortem and restoration plan for primary region.

**Q7: How do data consistency and eventual consistency affect disaster recovery?**

**Answer:** Eventual consistency: changes in primary may not be fully replicated to secondary when failover occurs. Some data loss (RPO) is expected. Strong consistency: all writes must be replicated synchronously before acknowledging to client — increases latency but zero data loss. For failover: eventual consistency systems can lose recent writes but recover faster. Strong consistency systems preserve all data but failover may be slower. Choose based on: can your application tolerate lost writes? Can it handle stale reads? (e.g., social media can tolerate eventual consistency; financial transactions cannot.)

**Q8: What is the difference between backup and disaster recovery?**

**Answer:** Backup: protecting data. Regular copies of data (files, databases, configurations) that can be restored. DR: protecting the entire service. Infrastructure, application, data, networking, DNS, etc. Restoring from backup may take hours (download backup, restore data, rebuild infrastructure). DR failover takes minutes (switch to pre-provisioned infrastructure). Backups are the last resort when DR fails. Both are needed: backups for data-level recovery (corruption, accidental deletion), DR for service-level recovery (region outage, disaster).

**Q9: Design a disaster recovery plan for a cloud-native microservice application.**

**Answer:** 1) Multi-region deployment: primary + secondary region (minimum 500km apart). 2) Database: cross-region replication (Aurora Global Database, Spanner, Cosmos DB multi-master). 3) Storage: cross-region replication for S3/GCS/Azure Blob. 4) Container registry: replicated across regions. 5) Infrastructure as Code: Terraform/CloudFormation for region recreation. 6) CI/CD: deployment pipeline replicates to both regions. 7) DNS: Route53/CloudDNS with health checks and failover. 8) DR runbook: step-by-step failover procedure, tested quarterly. 9) Monitoring: cross-region health dashboards.

**Q10: Tell me about a disaster recovery drill that revealed a gap. (STAR)**

**Answer:** Situation: During a quarterly DR drill, we simulated a full region failure. We followed the runbook: promoted database, switched DNS, verified health checks. Task: Test our DR readiness. Action: The failover completed in 8 minutes (within RTO). But when we tested the application, users couldn't upload files because the CDN distribution in the secondary region was misconfigured and didn't have the upload endpoint enabled. Result: We fixed the CDN configuration, added CDN verification to our DR checklist, and now every DR drill includes a full end-to-end functional test (not just infrastructure checks). Also added automated DR verification with synthetic monitoring.
