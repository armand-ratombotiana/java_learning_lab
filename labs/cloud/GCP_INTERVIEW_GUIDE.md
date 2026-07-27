# GCP Interview Guide

## Overview
Comprehensive preparation guide for Google Cloud Platform technical interviews — Cloud Architect, data/AI engineer, SRE, and DevOps roles.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| Cloud Architect | L4-L6 | Enterprise architecture, migration, GCP best practices |
| SRE | L5-L7 | Reliability, SLIs/SLOs, error budgets, automation, incident response |
| Cloud Engineer | L4-L5 | Compute Engine, GKE, Cloud Storage, IAM, networking |
| Data Engineer | L5-L6 | BigQuery, Dataflow, Pub/Sub, Dataproc, Looker |
| Security Engineer | L5-L7 | Cloud CUD, BeyondCorp, VPC Service Controls, Chronicle |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, GCP experience, roles |
| Phone Screen | 45-60 min | Algorithms/Data structures (coding) |
| Onsite Loop | 4-5 x 45 min | Googleyness, system design, coding, technical deep dive, (SRE: troubleshooting) |

## Core Technical Topics

### Compute
- **Compute Engine**: Machine families (E2, N2, N2D, C2, C2D, M1, M2, G2), sole-tenant nodes, committed use discounts, preemptible VMs, instance groups (managed, unmanaged), instance templates, OS images, Shielded VMs
- **GKE**: Autopilot vs Standard, node pools, cluster autoscaler, VPC-native clusters, workload identity, Istio/Anthos Service Mesh, Cloud Run for Anthos, Binary Authorization
- **Cloud Run**: Fully managed vs Anthos, container runtime contract, max instances, concurrency, Cloud Run Jobs, eventarc triggers
- **Cloud Functions**: 1st gen vs 2nd gen (Cloud Run-based), event-driven, HTTP triggers, background functions, CloudEvents
- **App Engine**: Standard vs Flexible environments, automatic scaling, services/versions, traffic splitting, cron jobs

### Storage
- **Cloud Storage**: Storage classes (Standard, Nearline, Coldline, Archive), Dual-Region, Multi-Region, object versioning, lifecycle management, object holds, retention policies, Pub/Sub notifications, Object Change Notification
- **Persistent Disk**: Standard, Balanced, SSD, Extreme, performance, snapshots (regional), machine images, disk resize
- **Filestore**: Basic, High Scale, Enterprise tiers, NFSv3, performance, backups

### Database
- **Cloud SQL**: MySQL, PostgreSQL, SQL Server, High Availability (regional), cross-region replicas, automated backups, point-in-time recovery, Private Service Connect, Cloud SQL Proxy, Database Migration Service
- **Cloud Spanner**: Regional vs multi-region, interleaved tables, secondary indexes, commit timestamps, hot-spot prevention, storage optimized vs throughput optimized
- **Bigtable**: HBase-compatible, replication, cluster resizing, performance considerations (row key design, locality groups, garbage collection)
- **Firestore**: Native mode vs Datastore mode, real-time updates, security rules, subcollections, composite indexes, transactions, Cloud Firestore triggers
- **Memorystore**: Redis vs Memcached, persistence, failover, VPC peering, scaling

### Networking
- **VPC**: Projects, networks (auto, custom, legacy), subnets (auto, custom), firewall rules (VPC firewall, firewall policies), routes, VPC peering, Shared VPC, VPC Network Peering, Private Google Access, Private Services Access, Cloud NAT, VPC Flow Logs, Packet Mirroring
- **Cloud Load Balancing**: Global external HTTP(S), global external SSL Proxy, global external TCP Proxy, regional external, regional internal, cross-region internal
- **Cloud CDN**: Cache modes, cache keys, signed URLs, signed cookies, origin, content-based routing
- **Cloud Armor**: WAF rules (OWASP, SQLi, XSS), IP allowlist/denylist, rate limiting, edge security, DDoS protection
- **Cloud DNS**: Public zones, private zones, forwarding zones, managed zones, DNSSEC, routing policies (geo, latency, failover, weighted)
- **Dedicated Interconnect**: VLAN attachments, capacity (10, 100, 200, 400 Gbps), Partner Interconnect, Cross-Cloud Interconnect

### Security
- **Cloud IAM**: Resource hierarchy (Org → Folder → Project → Resource), roles (primitive, predefined, custom), service accounts, workload identity federation, workforce identity federation, policy constraints
- **Cloud KMS**: Key rings, keys, key versions, rotation, HSM, CMEK, CSEK, key import
- **Secret Manager**: Secrets, versions, rotation, replication, access policies
- **BeyondCorp / Zero Trust**: Access Context Manager, VPC Service Controls, Context-Aware Access, Browser, Endpoint verification
- **Security Command Center**: Findings, security health analytics, Event Threat Detection, Container Threat Detection, Web Security Scanner

### Monitoring & Observability
- **Cloud Monitoring**: Workspace, metrics (agent, custom, logs-based), uptime checks, alerting policies, dashboards, Conditions (threshold, absence, change, rate of change)
- **Cloud Logging**: Log buckets, log sinks, log-based metrics, log views, Log Analytics, Error Reporting, Logging agent (OAuth 2.0, structured logging)
- **Cloud Trace**: Trace sampling, latency distributions, service mesh integration
- **Cloud Profiler**: CPU and heap profiling, agent-based, Java/Go/Python/Node
- **Cloud Debugger (deprecated)**: Snapshots, logpoints

## Googleyness / Leadership Dimensions

1. **Comfort with Ambiguity** — Works effectively in ambiguous situations, creates structure
2. **Bias to Action** — Prototypes, experiments, learns from shipping
3. **Intellectual Humility** — Willing to be wrong, learns from others
4. **Collaboration** — Works across teams, gives and receives feedback
5. **Passion** — Enthusiasm for technology, impact on users
6. **General Cognitive Ability** — Learns quickly, solves novel problems
7. **Leadership** — Influences without authority, drives initiatives

## Google SRE Principles

| Principle | Description | Interview Topics |
|-----------|-------------|------------------|
| **SLI** | Service Level Indicator — what you measure | Latency, availability, durability |
| **SLO** | Service Level Objective — target for SLI | 99.9%, 99.99%, 99.999% uptime |
| **SLA** | Service Level Agreement —承诺 to customers | Usually looser than SLO |
| **Error Budget** | 100% - SLO = acceptable failure | Balance reliability vs feature velocity |
| **Toil** | Manual, repetitive, automatable work | Target: < 50% time on toil |
| **Monitoring** | Symptoms vs causes | Latency > 95th percentile causes concern |
| **Capacity Planning** | Demand forecasting, load testing | Right-size to demand |
| **Blameless Postmortems** | No punishment for honest mistakes | Fix systems, not people |

## BigQuery Special Topics

- **Architecture**: Colossus (storage), Jupiter (network), Borg (compute), slots
- **Partitioning**: Ingestion time vs column, partition pruning, time-unit vs integer range
- **Clustering**: Sort order, cluster on frequently filtered columns
- **Best practices**: Avoid SELECT *, reduce data before JOIN, use table wildcards, use materialized views
- **Cost control**: Custom flat-rate slots (reservations, assignments), per-user quotas, BI Engine
- **Security**: Authorized views, column-level security, row-level security, classification

## Sample System Design Problems

1. **Design a data analytics platform using BigQuery and Dataflow**
2. **Design a globally distributed user-facing application with Spanner**
3. **Design a reliable microservices platform on GKE with Istio**
4. **Design a serverless event-driven application with Cloud Run + Pub/Sub**
5. **Design a hybrid/multi-cloud networking solution with Dedicated Interconnect**

## Key GCP Limits

| Service | Limit |
|---------|-------|
| Projects per organization | 500 |
| VPC per project | 100 |
| Subnets per VPC | 1000 |
| Firewall rules per VPC | 500 (1,000 with firewall policies) |
| Load balancer forwarding rules | 15 per project |
| Cloud Storage bucket per project | 5 |
| Cloud Storage object size | 5 TiB |
| GKE nodes per cluster | 15,000 |
| BigQuery datasets per project | 10,000 |
| BigQuery load jobs per project per day | 50,000 |

## Cert Path (Target: Professional Cloud Architect)

| Cert | Focus | Time |
|------|-------|------|
| Cloud Digital Leader | Fundamentals | 1-2 weeks |
| Associate Cloud Engineer | Operations | 4-6 weeks |
| Professional Cloud Architect | Architecture | 8-12 weeks |
| Professional Data Engineer | Data | 8-12 weeks |
| Professional Cloud DevOps Engineer | DevOps/SRE | 6-10 weeks |

---

*Last updated: July 2026*
