# System Design Cheatsheet — Cloud Architecture

## Table of Contents

1. [AWS Architecture Patterns](#1-aws-architecture-patterns)
2. [Azure Architecture Patterns](#2-azure-architecture-patterns)
3. [GCP Architecture Patterns](#3-gcp-architecture-patterns)
4. [Multi-Cloud Architecture Patterns](#4-multi-cloud-architecture-patterns)
5. [Cost Optimization Strategies](#5-cost-optimization-strategies)
6. [Common Design Problems with Cloud Solutions](#6-common-design-problems-with-cloud-solutions)

---

## 1. AWS Architecture Patterns

### Core Services Reference

| Service | Category | When to Use | When NOT to Use |
|---------|----------|-------------|-----------------|
| EC2 | Compute | Full OS control, custom AMIs, GPU workloads, legacy apps | Stateless apps that can use Lambda, containerized apps with no OS requirements |
| Lambda | Compute | Event-driven, short-lived functions, API backends, cron jobs | Long-running processes (>15 min), stateful workloads, GPU/ML training |
| ECS | Compute | Container orchestration with tight AWS integration, simplicity over K8s | Multi-cloud portability, complex networking, service mesh |
| EKS | Compute | Kubernetes-native orchestration, portability, complex microservices | Simple container deployments that fit ECS, cost-sensitive |
| Fargate | Compute | Serverless containers, no cluster management | GPU workloads, bare metal performance requirements |
| S3 | Storage | Static files, backups, data lakes, static website hosting, image/video storage | Database workloads, high IOPS, frequent overwrites of small files |
| S3 Standard-IA | Storage | Long-lived but infrequently accessed data | Data accessed frequently (use Standard), data accessed rarely (use Glacier) |
| S3 Glacier | Storage | Archival data, compliance, backups older than 90 days | Data that needs immediate retrieval (use Standard or Standard-IA) |
| S3 Intelligent-Tiering | Storage | Unknown or changing access patterns | Known stable access patterns (use specific tier for cost) |
| EBS | Storage | EC2 block storage, databases, stateful apps | Serverless workloads, multi-attach (use EFS), ephemeral data (use instance store) |
| EFS | Storage | Shared file system across EC2/Lambda, Linux workloads, CMS | Windows workloads (use FSx), single-instance (use EBS), high IOPS database |
| RDS | Database | Relational data, ACID transactions, complex queries, standard SQL | Non-relational data, massive scale (use DynamoDB), caching (use ElastiCache) |
| DynamoDB | Database | High-scale key-value, document, session state, gaming, IoT | Complex joins, hierarchical data, analytics (use RDS or Redshift) |
| Aurora | Database | MySQL/PostgreSQL compatibility with better performance and availability | Cost-sensitive applications, simple workloads (use standard RDS) |
| ElastiCache | Database | Caching, session store, real-time analytics, pub/sub | Persistent data (use RDS/DynamoDB), relational queries |
| Redshift | Database | Data warehousing, analytics, BI reporting | OLTP workloads, real-time transactions, small datasets |
| Route 53 | Networking | DNS management, health checks, routing policies, domain registration | Traffic shaping between regions (use Global Accelerator) |
| CloudFront | Networking | CDN, global content delivery, DDoS protection, SSL termination | Single-region internal apps, low-traffic sites without global users |
| VPC | Networking | Isolated network, subnets, routing, NAT, VPN, Direct Connect | Simple single-instance setups (use default VPC) |
| ALB | Networking | HTTP/HTTPS traffic, path-based routing, container targets | Non-HTTP traffic, simple round-robin (use NLB) |
| NLB | Networking | TCP/UDP traffic, extreme performance, static IPs | Applications needing HTTP features (use ALB) |
| API Gateway | Networking | REST/WebSocket API management, throttling, auth, versioning | Internal service-to-service communication (use ALB) |
| IAM | Security | User/role management, permissions, policies, federation | Temporary credentials for apps (use Cognito) |
| KMS | Security | Encryption key management, envelope encryption | Application-level encryption logic (use AWS Encryption SDK) |
| WAF | Security | Web application firewall, SQL injection, XSS, IP blocking | DDoS at network layer (use Shield), API security (use API Gateway) |
| Shield | Security | DDoS protection, L3/L4 attacks | Application-layer attacks (use WAF) |
| GuardDuty | Security | Threat detection, anomaly detection, finding insights | Real-time blocking (use WAF/Shield), compliance reporting |
| CloudWatch | Monitoring | Metrics, logs, alarms, dashboards, embedded metric format | Tracing (use X-Ray), infrastructure monitoring (use AWS Distro for OpenTelemetry) |
| CloudTrail | Monitoring | API activity logging, audit trail, compliance | Performance metrics (use CloudWatch), real-time monitoring |
| X-Ray | Monitoring | Distributed tracing, service map, latency analysis | Infrastructure metrics (use CloudWatch), log aggregation |
| SQS | Messaging | Decoupling services, message buffering, async processing | Real-time streaming (use Kinesis), pub/sub (use SNS) |
| SNS | Messaging | Pub/sub notifications, SMS, email, push | Point-to-point queuing (use SQS), streaming (use Kinesis) |
| Kinesis | Messaging | Real-time streaming, data ingestion, analytics | Simple message queuing (use SQS), notification (use SNS) |
| Step Functions | Orchestration | Workflow orchestration, state machines, error handling | Simple Lambda chaining (use Lambda destinations) |

### AWS Architecture Diagram Template (Text)

```
                    ┌─────────────────────────────────────────────┐
                    │              AWS Cloud                      │
                    │  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
                    │  │  Route 53 │  │CloudFront│  │   WAF    │  │
                    │  │   (DNS)   │  │   (CDN)  │  │ (Sec)    │  │
                    │  └─────┬────┘  └─────┬────┘  └──────────┘  │
                    │        │             │                      │
                    │  ┌─────▼─────────────▼──────────────────┐   │
                    │  │          Application Load Balancer    │   │
                    │  └─────┬────────────────────┬───────────┘   │
                    │        │                    │               │
                    │  ┌─────▼──────────┐  ┌─────▼──────────┐    │
                    │  │  Auto Scaling   │  │  Auto Scaling   │    │
                    │  │  Group (Web)    │  │  Group (API)    │    │
                    │  │  EC2/ECS/Fargate│  │  EC2/ECS/Fargate│    │
                    │  └────────┬───────┘  └────────┬────────┘    │
                    │           │                    │             │
                    │  ┌────────▼────────────────────▼──────────┐ │
                    │  │              Amazon ElastiCache        │ │
                    │  │          (Caching Layer)               │ │
                    │  └────────┬────────────────────▲──────────┘ │
                    │           │                    │            │
                    │  ┌────────▼────────────────────┴──────────┐ │
                    │  │          Amazon RDS (Primary)           │ │
                    │  │          Multi-AZ Deployment            │ │
                    │  └────────┬───────────────────────────────┘ │
                    │           │ (replication)                   │
                    │  ┌────────▼───────────────────────────────┐ │
                    │  │          Amazon RDS (Standby)           │ │
                    │  │          Different AZ                   │ │
                    │  └────────────────────────────────────────┘ │
                    │                                             │
                    │  ┌──────────────────────────────────────┐   │
                    │  │          Amazon S3 (Static Assets)    │   │
                    │  │          + SQS/SNS/EventBridge       │   │
                    │  └──────────────────────────────────────┘   │
                    └─────────────────────────────────────────────┘
```

### When to Use AWS Services

| Requirement | Primary Choice | Alternative |
|-------------|---------------|-------------|
| Web application hosting | EC2 + ALB + Auto Scaling | Elastic Beanstalk, Lightsail |
| Static website | S3 + CloudFront | Amplify Console |
| Container orchestration | ECS on Fargate | EKS, ECS on EC2 |
| Serverless API | API Gateway + Lambda | ALB + Lambda targets |
| Relational database | RDS Multi-AZ | Aurora, RDS on EC2 |
| NoSQL database | DynamoDB DAX | DynamoDB, DocumentDB |
| Data warehouse | Redshift | Athena for ad-hoc queries |
| Real-time streaming | Kinesis Data Streams | MSK (Kafka), SQS FIFO |
| Message queue | SQS | RabbitMQ on EC2, MQ |
| CI/CD | CodePipeline + CodeBuild | Jenkins on EC2, GitLab |
| Monitoring | CloudWatch + X-Ray | Grafana + Prometheus on EC2 |
| Infrastructure as Code | CDK (TypeScript/Python) | Terraform, CloudFormation |

---

## 2. Azure Architecture Patterns

### Core Services Reference

| Service | Category | When to Use | When NOT to Use |
|---------|----------|-------------|-----------------|
| Azure VMs | Compute | Full OS control, legacy app, specific OS requirements | Containerized apps, stateless microservices |
| App Service | Compute | Web apps, API apps, mobile backends, easy CI/CD | Complex microservices, stateful long-running processes |
| Azure Functions | Compute | Event-driven, short-lived (10 min max), triggers from 200+ sources | Long-running workflows, stateful apps, CPU-intensive tasks |
| AKS | Compute | Kubernetes-native, hybrid deployment, service mesh | Simple containers that can use App Service, single-container apps |
| Container Instances | Compute | Simple container, quick start, burst workloads | Production orchestration (use AKS), complex deployments |
| Storage Blob | Storage | Large files, backups, data lakes, tiered storage | Databases, structured data, high IOPS workloads |
| Azure Files | Storage | SMB file shares, lift-and-shift, hybrid storage | High-performance computing, database storage |
| Managed Disks | Storage | VM disk, database storage, high IOPS | Shared access (use Azure Files), serverless workloads |
| Azure SQL | Database | SQL Server workloads, managed database, built-in HA | Non-Microsoft databases, NoSQL, massive scale |
| Cosmos DB | Database | Global distribution, multi-model, high throughput | Simple key-value (use Table Storage), SQL workloads |
| Azure Cache for Redis | Database | Caching, session state, pub/sub | Persistent storage, relational data |
| Azure VNet | Networking | Isolated network, VPN, ExpressRoute, peering | Single VM simple deployments |
| Load Balancer | Networking | TCP/UDP load balancing, high availability | HTTP/HTTPS apps (use App Gateway), global routing (use Traffic Manager) |
| App Gateway | Networking | HTTP/HTTPS, WAF, path-based routing, SSL termination | Non-HTTP traffic (use Load Balancer) |
| Traffic Manager | Networking | DNS-based global traffic routing, failover | Application-layer routing (use Front Door) |
| Front Door | Networking | Global HTTP load balancing, acceleration, WAF | Internal-only apps, non-HTTP workloads |
| API Management | Networking | API gateway, transformation, rate limiting, developer portal | Simple API proxying (use App Gateway) |
| Azure AD | Security | Identity, SSO, MFA, Conditional Access, app registration | Application-level auth (use App Service auth) |
| Key Vault | Security | Secrets management, encryption keys, certificates | Application-level token handling |
| Microsoft Defender | Security | Cloud security posture, workload protection, threat detection | Network-level security (use NSG/ASG) |
| Monitor | Monitoring | Metrics, logs, alerts, application insights | Infrastructure tracing (use Azure X-Ray equivalent) |
| Log Analytics | Monitoring | Log query, analysis, visualization, KQL | Real-time monitoring (use Metrics) |
| Azure Policy | Governance | Compliance, resource tagging, allowed locations | Resource creation (use RBAC), cost management |

### Azure Architecture Patterns

**Typical Enterprise Web App:**
```
User → Traffic Manager → Front Door → App Gateway (WAF) → App Service (Web) → App Service (API) → Azure SQL
                                          ↓                                                    ↑
                                    Azure Redis Cache ──────────────────────────────────────────┘
```

**Hybrid Cloud Pattern:**
```
On-prem ──ExpressRoute──→ Azure VNet (Gateway Subnet)
                            │
                    ┌───────┴───────┐
                    │               │
              Azure SQL MI      App Service
              (Managed Instance)  (Hybrid Connections)
                    │               │
              On-prem SQL       ────┘
              (Always On AG)
```

---

## 3. GCP Architecture Patterns

### Core Services Reference

| Service | Category | When to Use | When NOT to Use |
|---------|----------|-------------|-----------------|
| Compute Engine | Compute | Full VM control, GPU/TPU workloads, custom OS | Serverless workloads, container-only apps |
| GKE | Compute | Kubernetes-native, Autopilot, multi-cluster | Simple apps that can use Cloud Run |
| Cloud Run | Compute | Serverless containers, auto-scaling, pay-per-request | Stateful workloads, long-running (>60 min), GPU |
| Cloud Functions | Compute | Event-driven, lightweight, 9 min timeout | Complex workflows (use Cloud Run or Workflows) |
| App Engine | Compute | Autoscaling web apps, built-in services | Custom networking, container control (use GKE) |
| Cloud Storage | Storage | Multi-class object storage, data lake, backups | Database workloads, high IOPS |
| Persistent Disk | Storage | Compute Engine disk, high performance | Shared access (use Filestore) |
| Filestore | Storage | Shared NFS for Compute/GKE | Single-instance (use Persistent Disk) |
| Cloud SQL | Database | MySQL, PostgreSQL, SQL Server, managed | Massive scale (use Spanner), NoSQL |
| Cloud Spanner | Database | Globally distributed, strong consistency, high scale | Single-region, smaller DB (use Cloud SQL) |
| Firestore | Database | Mobile apps, real-time sync, serverless apps | Complex queries, SQL workloads |
| Bigtable | Database | HBase-compatible, high throughput, large scale | Relational data, small datasets |
| BigQuery | Database | Analytics, data warehouse, serverless queries | OLTP workloads, real-time transactions |
| Memorystore | Database | Redis/Memcached, caching, low latency | Persistent storage |
| VPC | Networking | Network isolation, firewall rules, peering | Single-project apps (use default VPC) |
| Cloud Load Balancing | Networking | Global HTTP(S)/TCP/UDP, autoscaling | Simple internal LB (use regional LB) |
| Cloud CDN | Networking | Global content delivery, cache, DDoS | Internal services, low-traffic regions |
| Cloud DNS | Networking | DNS management, DNSSEC, routing policies | Traffic management (use Cloud LB) |
| Cloud NAT | Networking | Outbound internet without public IP | Inbound traffic (use Load Balancer) |
| Cloud Armor | Security | WAF, DDoS protection, IP allowlist/blocklist | Network-level security (use firewall rules) |
| Cloud IAM | Security | Fine-grained access, roles, service accounts | Application auth (use Firebase Auth) |
| Cloud KMS | Security | Key management, encryption, HSM | Data encryption at app level |
| Security Command Center | Security | Threat detection, vulnerability scanning | Real-time blocking (use Cloud Armor) |
| Cloud Monitoring | Monitoring | Metrics, uptime checks, alerting, dashboards | Tracing (use Cloud Trace), logging (use Cloud Logging) |
| Cloud Logging | Monitoring | Log storage, query, analysis | Real-time monitoring (use Cloud Monitoring) |
| Cloud Trace | Monitoring | Distributed tracing, latency analysis | Infrastructure monitoring (use Cloud Monitoring) |
| Pub/Sub | Messaging | Async messaging, event-driven, streaming ingest | Point-to-point (use tasks), real-time streaming (use Dataflow) |
| Workflows | Orchestration | Service orchestration, HTTP-based, error handling | Complex workflows (use Cloud Composer/Airflow) |
| Dataflow | Data | Stream/batch processing, Apache Beam | Simple ETL (use Dataproc), analytics (use BigQuery) |

### GCP Architecture Patterns

**Serverless Web App:**
```
Cloud DNS → Cloud CDN → Cloud Load Balancing → Cloud Armor → Cloud Run (Web) → Firestore
                                              ↓                             ↑
                                        Cloud Tasks                     Cloud Run (Worker)
                                              ↓
                                        Cloud Scheduler
```

**Data Analytics Pipeline:**
```
Pub/Sub → Dataflow → BigQuery → Looker / Data Studio
   ↑          ↓          ↓
Source     Cloud S3    Cloud Storage
(Streaming) (Batch)    (Staging)
```

---

## 4. Multi-Cloud Architecture Patterns

### Cross-Cloud Networking

**VPN-Based Multi-Cloud Connectivity:**
```
AWS VPC ──── VPN ──── Azure VNet ──── VPN ──── GCP VPC
    │                                         │
    │          Cloudflare / Megaport           │
    └──────────── (WAN Optimization) ──────────┘
```

**Using Cloud Interconnects:**
```
AWS Direct Connect ──→ Equinix / Megaport ──→ Azure ExpressRoute
                         │
                         └──→ GCP Dedicated Interconnect
```

**Software-Defined WAN (SD-WAN):**
```
           ┌─ AWS Region (us-east-1)
           │
Site A ─── SD-WAN Hub ──── Azure Region (eastus)
           │
           └─ GCP Region (us-central1)
```

### Data Replication Across Clouds

**Active-Passive Cross-Cloud Database:**
```
Primary: AWS RDS (us-east-1)
    │
    ├──→ Replica: AWS RDS (us-west-2) [cross-region]
    │
    └──→ Cross-cloud sync via GoldenGate / Striim
            │
            └──→ Standby: Azure SQL (eastus)
```

**Active-Active Cross-Cloud:**
```
User ─→ DNS Load Balancer (Route53 + Azure Traffic Manager)
           │
     ┌─────┴─────┐
     │           │
  AWS App     Azure App
     │           │
  DynamoDB    Cosmos DB
  Global DB   Multi-region
     │           │
     └── Sync ──┘ (via CDC / Kafka)
```

### Unified IAM

**Federated Identity Across Clouds:**
```
Azure AD (Primary IdP)
    │
    ├──→ AWS IAM Identity Center (SAML/OIDC)
    │
    ├──→ GCP Workforce Identity Federation
    │
    └──→ OCI IAM Federation
```

**Cross-Cloud Audit Trail:**
```
AWS CloudTrail ──┐
                 ├──→ Central SIEM (Splunk / Datadog / ELK)
Azure Monitor ───┤
                 │
GCP Cloud Audit ─┘
```

### Multi-Cloud Strategy Drivers

| Driver | Pattern | Complexity |
|--------|---------|------------|
| Vendor lock-in avoidance | Abstract IaC (Terraform), cloud-agnostic services | High |
| Best-of-breed services | Use each provider's best (AWS Lambda + GCP BigQuery) | Medium |
| Compliance / Data residency | Region-specific clouds for data sovereignty | Medium |
| Cost optimization | Use spot instances across providers, negotiate discounts | High |
| Disaster recovery | Active-passive cross-cloud DR | High |
| Acquisition integration | Hybrid during migration period | Very High |
| Geographic reach | Use region presence where each provider excels | Medium |

### Multi-Cloud Anti-Patterns

- Building complete abstraction layer over all providers (increased complexity, reduced native features)
- Real-time database sync across providers (latency, consistency challenges)
- Active-active global deployment from day one (start active-passive)
- Same application code on all providers without adaptation (Terraform/Helm helps but still differences)
- Ignoring data egress costs (significant in multi-cloud)

---

## 5. Cost Optimization Strategies

### Reserved Instances (RI) & Savings Plans

| Strategy | AWS | Azure | GCP |
|----------|-----|-------|-----|
| RI term | 1 or 3 years | 1 or 3 years | 1 or 3 years |
| Payment options | All upfront, partial, no upfront | All upfront, monthly | All upfront, monthly |
| Convertible | Yes (change instance family) | Yes (change instance family) | Yes (change instance family) |
| Regional scope | Regional or zonal | Regional | Regional |
| Auto-renew | Optional | Optional | Yes |
| Discount range | Up to 72% | Up to 72% | Up to 70% |

**Savings Plans:**
- Compute Savings Plans (AWS): Apply to any compute, most flexible, up to 66%
- EC2 Instance Savings Plans (AWS): Apply to specific instance family, up to 72%
- Azure Reserved Instances: Apply to VM series
- GCP Committed Use Discounts: Compute Engine or GKE

### Spot / Preemptible / Low-Priority Instances

| Feature | AWS Spot | GCP Preemptible | Azure Spot |
|---------|----------|-----------------|------------|
| Discount | Up to 90% | 60-91% | Up to 90% |
| Max runtime | No max (can be reclaimed) | 24 hours | No max (can be reclaimed) |
| Reclaim notice | 2 minutes | 30 seconds | 30 seconds |
| Best for | Batch, stateless, fault-tolerant | Batch, stateless, fault-tolerant | Batch, stateless, fault-tolerant |
| Capacity pools | Instance type + AZ | Region | Region + size |

**Spot Instance Strategies:**
- Use Spot Fleet / EC2 Fleet for diversity across instance types
- Implement graceful shutdown handling (SQS queue drain, checkpoint)
- Mix spot (60-80%) with on-demand (20-40%) for baseline
- Use CloudWatch Events to handle termination notifications
- For GCP: use node auto-repair and node auto-upgrade in GKE with preemptible nodes

### Right-Sizing

**Process:**
1. Collect metrics: CPU, memory, network, disk I/O (30+ days)
2. Identify underutilized instances (< 10% average CPU)
3. Identify overutilized instances (> 80% average CPU)
4. Right-size down for underutilized, up for overutilized
5. Consider latest generation instances (better performance/cost)
6. Repeat quarterly

**Tools:**
| Provider | Tool | Features |
|----------|------|----------|
| AWS | Compute Optimizer | ML-powered recommendations, supports EC2, Auto Scaling, EBS, Lambda |
| Azure | Advisor | Cost recommendations, performance, reliability, security |
| GCP | Recommender | Rightsizing recommendations, idle IPs, committed use discounts |

### Storage Tier Optimization

| Tier | AWS | Azure | GCP |
|------|-----|-------|-----|
| Hot | S3 Standard | Blob Hot | Standard |
| Cool | S3 Infrequent Access | Blob Cool | Nearline |
| Cold | S3 Glacier | Blob Archive | Coldline |
| Archive | S3 Glacier Deep Archive | Blob Cool Archive | Archive |

### Compute Optimization

**AWS:**
- Graviton processors (ARM): 20-40% better price/performance
- Compute Optimized (C-series) vs General Purpose (M-series)
- T-series burstable: good for variable workloads

**Azure:**
- B-series burstable: dev/test, variable load
- F-series compute optimized
- Azure Hybrid Benefit: use on-prem Windows Server/SQL licenses

**GCP:**
- Tau T2D/T2A: cost-optimized, ARM (Ampere Altra)
- Custom machine types: pay only for what you use
- Sole-tenant nodes: compliance, license requirements

### Cost Optimization Checklist

| Category | Action | Savings |
|----------|--------|---------|
| Compute | Use RIs/Committed Use for steady-state | Up to 72% |
| Compute | Use Spot/Preemptible for flexible workloads | Up to 90% |
| Compute | Right-size instances (analyze 30d metrics) | 20-40% |
| Compute | Use latest generation instances | 10-20% |
| Compute | Delete unattached volumes | 100% on volumes |
| Compute | Use auto-scaling to match demand | 30-50% |
| Storage | Move unused data to colder tiers | 50-80% |
| Storage | Delete incomplete multipart uploads | Variable |
| Storage | Enable S3 Intelligent-Tiering (AWS) | Automatic savings |
| Storage | S3 Lifecycle policies for automatic transition | 40-60% |
| Network | Use CDN to reduce origin traffic | 20-50% |
| Network | Use NAT Gateway efficiently | $32/mo per AZ |
| Network | Delete unused Load Balancers | $20-25/mo each |
| Network | Minimize cross-region data transfer | 100% on transfer |
| Database | Use RDS Reserved Instances | Up to 60% |
| Database | Right-size RDS instance class | 20-40% |
| Database | Remove unused RDS read replicas | 100% on replicas |
| Database | Use Aurora Serverless for variable workloads | 50-70% |
| Serverless | Optimize Lambda memory setting | 10-30% |
| Serverless | Use Lambda Provisioned Concurrency wisely | Variable |
| General | Tag resources for cost allocation | Better visibility |
| General | Set budgets and alerts | Prevent surprises |
| General | Review and delete unused resources monthly | Variable |

---

## 6. Common Design Problems with Cloud Solutions

| Problem | AWS Solution | Azure Solution | GCP Solution |
|---------|-------------|----------------|--------------|
| Global e-commerce platform | CloudFront + S3 + ALB + ECS/Fargate + RDS Aurora + ElastiCache + DynamoDB + SQS | Front Door + App Service + Azure SQL + Redis + Cosmos DB + Queue Storage | Cloud CDN + Cloud Storage + Cloud Run + Cloud SQL + Memorystore + Firestore + Pub/Sub |
| Real-time chat system | API Gateway + Lambda + DynamoDB Streams + API Gateway WebSocket | Azure Functions + Cosmos DB Change Feed + SignalR | Cloud Run + Firestore real-time + Cloud Functions |
| Video transcoding pipeline | S3 + SQS + Lambda/EC2 + Elastic Transcoder + CloudFront | Blob Storage + Queue + Functions + Media Services | Cloud Storage + Pub/Sub + Transcoder API + Cloud CDN |
| IoT data ingestion | IoT Core + Kinesis + Lambda + DynamoDB + S3 | IoT Hub + Stream Analytics + Functions + Cosmos DB | IoT Core + Pub/Sub + Dataflow + Bigtable |
| Batch data processing | Step Functions + Batch/EMR + S3 + Redshift | Logic Apps + Batch + Blob + Synapse | Workflows + Dataproc/Dataflow + Cloud Storage + BigQuery |
| Microservices with service mesh | EKS + App Mesh + Cloud Map + X-Ray | AKS + Azure Service Mesh + Dapr | GKE + Istio/Anthos Service Mesh + Cloud Trace |
| Disaster recovery across regions | RDS cross-region read replica + S3 CRR + Route53 failover | SQL Geo-Replication + RA-GRS + Traffic Manager | Cloud SQL cross-region replica + Cloud Storage multi-region + Cloud DNS |
| Serverless data lake | S3 + Glue + Athena + QuickSight | Data Lake Storage + Azure Data Factory + Synapse + Power BI | Cloud Storage + Dataproc + BigQuery + Looker |

---

*Last updated: July 2026*
