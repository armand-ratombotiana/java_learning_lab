# Oracle Cloud Infrastructure Interview Guide

## Overview
Comprehensive preparation guide for Oracle Cloud Infrastructure (OCI) technical interviews — Cloud Architect, Database Specialist, and DevOps roles.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| OCI Cloud Architect | IC3-IC5 | Enterprise OCI architecture, migration, autonomous database |
| Solutions Architect | IC4-IC6 | Customer-facing, Oracle workload optimization, presales |
| OCI Cloud Engineer | IC3-IC5 | IaaS/PaaS operations, networking, automation, IaC |
| Database Cloud Specialist | IC4-IC6 | Autonomous DB, Exadata, RAC, Data Guard, GoldenGate |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, Oracle ecosystem experience |
| Technical Screen | 45-60 min | OCI architecture, database expertise |
| Onsite Loop | 4 x 45 min | System design, technical deep dive, behavioral, hands-on |

## Core Technical Topics

### OCI Compute
- **Instances**: Bare metal vs VM, shapes (standard, dense I/O, GPU, HPC), flexible shapes (adjust OCPU + memory), capacity reservations, preemptible instances, dedicated VM hosts
- **Autoscaling**: Instance pools, autoscaling configuration (metric-based, schedule-based)
- **Container Engine for Kubernetes (OKE)**: Managed Kubernetes, virtual nodes (serverless), node pools, cluster autoscaler, OCI Identity integration
- **Oracle Functions**: Fn Project-based serverless, triggers from Oracle events, OCI SDK/CLI

### OCI Storage
- **Block Volume**: Volume types (block, boot), performance (balanced, high performance, ultra high performance), volume groups, backups (full, incremental), cloning, cross-region replication
- **Object Storage**: Standard vs Infrequent Access vs Archive tiers, auto-tiering, versioning, pre-authenticated requests, object lifecycle, replication
- **File Storage**: NFSv3, mount targets, export options, snapshots, cloning, cross-region replication
- **Local NVMe**: Direct-attached NVMe drives on Dense IO shapes, temporary

### OCI Database
- **Autonomous Database (ADB)**: Serverless vs dedicated, transaction processing (ATP) vs data warehouse (ADW) vs JSON, auto-scaling, auto-tuning, auto-backup, cloning, Oracle Machine Learning
- **Exadata Cloud Service**: X8M, X9M, dedicated infrastructure, RAC, Data Guard, performance
- **Base Database Service**: VM DB system, bare metal DB system, RAC, Data Guard, patching
- **MySQL Database Service**: HeatWave, high availability, read replicas, backup, Data Lake
- **NoSQL Database**: On-demand provisioned, table design, consistency (eventual, absolute)
- **Data Guard**: Active Data Guard, Data Guard associations, broker, fast-start failover, maximum performance vs protection vs availability

### OCI Networking
- **VCN**: VCN CIDR, subnets (public, private, regional, AD-specific), route tables, security lists, Network Security Groups (NSG), internet gateway, NAT gateway, service gateway, local peering gateway (LPG)
- **DRG (Dynamic Routing Gateway)**: VCN attachments, IPSec VPN, FastConnect (virtual circuits), route import/export, transit routing
- **FastConnect**: Private and public peering, colocation and partner, bandwidth (1, 10, 100 Gbps), virtual circuits
- **Load Balancing**: Public vs private, Flexible vs 100 Mbps vs 400 Mbps, policies (round robin, least connections, IP hash), session persistence, SSL/TLS offload
- **DNS**: Zones, records, steering policies (failover, latency, geolocation)

### OCI Security
- **IAM**: Compartments, groups, dynamic groups, policies, tags, OCI Vault, Vault secrets
- **Cloud Guard**: Detectors, responders, target, managed list, problems
- **Security Zones**: Maximum security VCN posture, policy enforcement, quarantine
- **WAF**: Edge policy, rate limiting, bot management, access control
- **OS Management**: Autonomous Linux, patching, Ksplice zero-downtime patching
- **Audit**: Events, retention, API activity

### Migration Tools
- **Cloud Advisor**: Workload assessments, cost optimization, security recommendations
- **Migration**: Oracle Cloud Migration (OCM), Zero Downtime Migration (ZDM), GoldenGate, Data Pump, Application Migration Service

## Key Differentiators

- **Autonomous Database**: Fully automated database management (self-driving, self-securing, self-repairing)
- **Exadata**: Extreme performance for Oracle databases (smart scan, storage indexing, flash cache)
- **RAC**: Real Application Clusters (multi-node active-active database)
- **Data Guard**: Database-level disaster recovery (physical/logical standby)
- **GoldenGate**: Real-time data integration and replication across heterogeneous systems
- **OCI Pricing**: Lower egress costs, consistent pricing, universal credits

## Sample Interview Questions

1. **Design a DR strategy for an Oracle database**: RAC + Data Guard, RPO/RTO, failover testing
2. **Migrate an on-premises Oracle DB to OCI**: ZDM, GoldenGate, Data Pump, assessment
3. **Design a secure multi-VCN architecture**: Hub-spoke, DRG, security lists, NSG
4. **Optimize autonomous database costs**: Auto-scaling, auto-tuning, Auto Workload Repository
5. **OCI vs AWS/Azure comparison**: What differentiates OCI for enterprise workloads?

## Cert Path

| Cert | Focus | Time |
|------|-------|------|
| OCI Foundations | Fundamentals | 2 weeks |
| OCI Architect Associate | Architecture basics | 4-6 weeks |
| OCI Architect Professional | Advanced architecture | 8-12 weeks |
| OCI Data Management | Autonomous DB, Exadata | 6-10 weeks |
| OCI Security | Security services | 6-8 weeks |

---

*Last updated: July 2026*
