# Cloud Certification Guide

## Table of Contents

1. [Amazon Web Services (AWS) Certifications](#1-amazon-web-services-aws-certifications)
2. [Microsoft Azure Certifications](#2-microsoft-azure-certifications)
3. [Google Cloud Platform (GCP) Certifications](#3-google-cloud-platform-gcp-certifications)
4. [Oracle Cloud Certifications](#4-oracle-cloud-certifications)
5. [CNCF / Kubernetes Certifications](#5-cncf--kubernetes-certifications)
6. [HashiCorp Certifications](#6-hashicorp-certifications)
7. [Cloudflare Certifications](#7-cloudflare-certifications)
8. [Cross-Provider Certification Strategy](#8-cross-provider-certification-strategy)

---

## 1. Amazon Web Services (AWS) Certifications

### Certification Roadmap

```
                     ┌─────────────────────┐
                     │  Cloud Practitioner  │ (Foundation)
                     │      (CLF-C02)       │
                     └──────────┬──────────┘
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
          ▼                     ▼                     ▼
  ┌──────────────┐   ┌──────────────────┐   ┌──────────────────┐
  │ Solutions    │   │       DevOps     │   │      Security    │
  │ Architect -- │   │   Engineer --    │   │    Specialty     │
  │   Associate  │   │   Professional   │   │   (SCS-C02)      │
  │  (SAA-C03)   │   │  (DOP-C02)       │   └──────────────────┘
  └──────┬───────┘   └──────────────────┘
         │
         ▼
  ┌──────────────────┐
  │ Solutions        │
  │ Architect --     │
  │   Professional   │
  │  (SAP-C02)       │
  └──────────────────┘
```

### AWS Certified Solutions Architect — Associate (SAA-C03)

**What It Proves:**
- Ability to design secure, resilient, and cost-effective AWS architectures
- Understanding of core AWS services (compute, storage, database, networking)
- Knowledge of the Well-Architected Framework
- Ability to make trade-off decisions between services

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | 65 questions (multiple choice, multiple response) |
| Duration | 130 minutes |
| Cost | $150 USD |
| Passing | 720/1000 |
| Validity | 3 years |
| Language | English, Japanese, Korean, Simplified Chinese |

**Study Path (6-8 weeks):**

| Week | Topic | Resources |
|------|-------|-----------|
| 1 | AWS Global Infrastructure, IAM, S3 | AWS documentation, A Cloud Guru course |
| 2 | EC2, EBS, Auto Scaling, ELB | Hands-on labs, tutorialsdojo practice tests |
| 3 | VPC, Route 53, CloudFront | VPC wizard, AWS networking whitepapers |
| 4 | RDS, DynamoDB, ElastiCache | Database comparison whitepaper |
| 5 | Lambda, ECS, EKS, Beanstalk | AWS re:Invent videos, hands-on labs |
| 6 | Security, KMS, WAF, Shield | Security whitepaper, IAM policy examples |
| 7 | Well-Architected Framework, Cost | Well-Architected whitepaper, AWS Pricing |
| 8 | Review, practice exams | TutorialsDojo exams (score > 85%), AWS sample questions |

**Exam Tips:**
- Read the question fully — AWS often tests the "MOST" correct answer
- Look for keywords like "cost-effective", "highly available", "fault-tolerant", "secure"
- Know the difference between: S3 Standard vs IA vs Glacier; EBS gp3 vs io2; RDS Multi-AZ vs Read Replicas
- Understand VPC: public/private subnets, NAT Gateway, VPC endpoints, Security Groups vs NACLs
- IAM policies: managed vs inline, resource vs condition-based
- Memorize Well-Architected 6 Pillars: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, Sustainability
- Eliminate obviously incorrect answers first (usually 2)

### AWS Certified Solutions Architect — Professional (SAP-C02)

**What It Proves:**
- Advanced ability to design complex, multi-account AWS architectures
- Migration and hybrid cloud expertise
- Cost optimization at enterprise scale
- Enterprise governance and compliance

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | 75 questions |
| Duration | 180 minutes |
| Cost | $300 USD |
| Passing | 750/1000 |
| Validity | 3 years |

**Additional Topics vs Associate:**
- AWS Organizations, SCPs, multi-account strategies
- Advanced VPC: Transit Gateway, VPC Peering, Direct Connect, VPN
- Hybrid architectures: Storage Gateway, DataSync, FSx
- Migration: MGN, DMS, SMS, CloudEndure
- Advanced security: HSM, ACM Private CA, RAM
- Large-scale data processing: EMR, Glue, Athena, Kinesis
- Disaster Recovery strategies: Pilot Light, Warm Standby, Multi-Site

### AWS Certified DevOps Engineer — Professional (DOP-C02)

**What It Proves:**
- CI/CD pipeline design and management
- Infrastructure as Code (CloudFormation, CDK)
- Monitoring, logging, and observability
- Configuration management and automation

**Key Topics:**
- CodePipeline, CodeBuild, CodeDeploy, CodeStar
- CloudFormation: nested stacks, cross-stack references, stack sets
- CloudWatch: metrics, logs, alarms, composite alarms, Contributor Insights
- X-Ray: tracing, annotations, segments
- Systems Manager: Parameter Store, Run Command, Automation, Patch Manager
- Auto Scaling: lifecycle hooks, cooldown, warm pools, predictive scaling
- Blue/Green and Canary deployments
- OpsWorks, Elastic Beanstalk (deployment policies)

### AWS Certified Security — Specialty (SCS-C02)

**What It Proves:**
- IAM advanced: policy conditions, permission boundaries, SCPs
- Data encryption: KMS, CloudHSM, ACM, envelope encryption
- Network security: VPC endpoints, security groups, NACLs, WAF, Shield
- Incident response: GuardDuty, Security Hub, Detective
- Compliance: Artifact, Audit Manager, Config

**Study Resources:**
- AWS Security Whitepapers
- AWS re:Invent Security sessions
- Hands-on labs with IAM policies, KMS, WAF configurations
- TutorialsDojo Security Specialty practice exams

---

## 2. Microsoft Azure Certifications

### Certification Roadmap

```
        ┌──────────────────┐
        │   AZ-900         │
        │ Azure    │
        │ Fundamentals     │
        └────────┬─────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│  AZ-104  │ │  AZ-204  │ │  AI-102  │
│ Admin    │ │  Developer│ │  AI Eng  │
└────┬─────┘ └──────────┘ └──────────┘
     │
     ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│  AZ-305  │ │  AZ-400  │ │  AZ-500  │
│ Architect│ │  DevOps  │ │  Security│
│ Expert   │ │  Expert  │ │  Eng     │
└──────────┘ └──────────┘ └──────────┘
```

### AZ-104: Azure Administrator

**What It Proves:**
- Manage Azure identities and governance
- Implement and manage storage
- Deploy and manage compute resources
- Configure and manage virtual networking
- Monitor and back up Azure resources

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | 40-60 questions |
| Duration | 120 minutes |
| Cost | $165 USD |
| Passing | 700/1000 |
| Validity | 1 year (renewable) |

**Key Topics:**
- Azure AD: users, groups, roles, RBAC, policies
- Storage: blob, file, disk, storage accounts, replication
- Compute: VMs, VMSS, App Service, ACI, AKS basics
- Networking: VNet, subnets, peering, VPN, ExpressRoute, Load Balancer
- Monitoring: Azure Monitor, Log Analytics, Alerts
- Backup: Azure Backup, Site Recovery

**Study Path (6-8 weeks):**
| Week | Topic |
|------|-------|
| 1 | Azure AD, RBAC, Policy, Subscriptions |
| 2 | Storage accounts, blob storage, file shares |
| 3 | VMs, VMSS, availability sets, zones |
| 4 | VNet, subnets, NSG, peering, VPN |
| 5 | App Service, ACI, AKS basics |
| 6 | Monitoring, backup, disaster recovery |
| 7-8 | Practice exams, review weak areas |

### AZ-305: Azure Solutions Architect Expert

**What It Proves:**
- Design identity, governance, and monitoring solutions
- Design data storage solutions
- Design compute and networking solutions
- Design business continuity solutions
- Recommended before: AZ-104 or significant Azure experience

**Key Design Areas:**
- Identity: Azure AD B2B/B2C, Conditional Access, PIM, managed identities
- Storage: Cosmos DB, SQL Database, Synapse, Data Lake
- Compute: App Service, AKS, Functions, Logic Apps
- Networking: Hub-spoke topology, Azure Firewall, Front Door, Traffic Manager
- HA/DR: Azure Site Recovery, geo-replication, Availability Zones
- Migration: Azure Migrate, DMS, ASR

### AZ-500: Azure Security Engineer

**What It Proves:**
- Manage identity and access (Azure AD, Conditional Access, PIM)
- Implement platform protection (NSG, Azure Firewall, DDoS protection)
- Manage security operations (Defender, Sentinel, Security Center)
- Secure data and applications (Key Vault, encryption, SQL security)

### AZ-400: Azure DevOps Engineer Expert

**What It Proves:**
- Design and implement CI/CD (Azure Pipelines, GitHub Actions)
- Implement container orchestration (Docker, AKS, Helm)
- Implement infrastructure as code (ARM, Bicep, Terraform on Azure)
- Implement feedback mechanisms (monitoring, logging)

---

## 3. Google Cloud Platform (GCP) Certifications

### Certification Roadmap

```
        ┌──────────────────┐
        │  Cloud Digital   │
        │     Leader       │ (Business/Leadership)
        └────────┬─────────┘
                 │
                 ▼
        ┌──────────────────┐
        │ Associate Cloud  │
        │    Engineer      │ (ACE)
        │   (candidate may │
        │   skip to Pro)   │
        └────────┬─────────┘
                 │
    ┌────────────┼──────────────────────────┐
    │            │                          │
    ▼            ▼                          ▼
┌──────────┐ ┌──────────┐           ┌──────────┐
│  Cloud   │ │   Data   │           │  Cloud   │
│ Architect│ │ Engineer │  ...      │ Security │
│ (Pro)    │ │ (Pro)    │           │ (Pro)    │
└──────────┘ └──────────┘           └──────────┘
```

### Google Cloud Associate Cloud Engineer (ACE)

**What It Proves:**
- Deploy and manage applications on GCP
- Manage GCP projects, IAM, and billing
- Configure networking and security
- Use common GCP services

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | 50 questions |
| Duration | 2 hours |
| Cost | $125 USD |
| Passing | 70% |
| Validity | 3 years |

**Key Topics:**
- Projects: organization, folder, project hierarchy
- IAM: roles (primitive, predefined, custom), service accounts
- Compute: Compute Engine, GKE, Cloud Run, Cloud Functions
- Storage: Cloud Storage, Persistent Disk, Filestore
- Database: Cloud SQL, Firestore, Bigtable
- Networking: VPC, firewall rules, Cloud NAT, VPC peering
- Monitoring: Cloud Monitoring, Logging, Error Reporting

### Google Cloud Professional Cloud Architect

**What It Proves:**
- Design and plan cloud architectures
- Manage and provision infrastructure
- Design for security and compliance
- Analyze and optimize technical and business processes

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | Case studies + multiple choice |
| Duration | 2 hours |
| Cost | $200 USD |
| Passing | 70% |
| Validity | 2 years |

**Case Study Approach:**
The exam includes 3-4 case studies. You must analyze them and answer questions about:
- Business requirements analysis
- Technical requirements mapping
- Architecture design decisions
- Migration and implementation planning

**Key Study Areas:**

| Area | Weight | Topics |
|------|--------|--------|
| Design & Planning | 25% | Business reqs, technical reqs, migration planning |
| Management & Provisioning | 25% | IaC, CI/CD, deployment strategies |
| Security & Compliance | 25% | IAM, encryption, compliance, DDoS |
| Analysis & Optimization | 25% | Cost, performance, reliability, scalability |

**Additional Topics vs ACE:**
- Anthos (GKE on-premises, multi-cloud)
- Apigee API management
- Cloud Spanner design (interleaved tables, hot-spot avoidance)
- Data processing: Dataflow, Dataproc, BigQuery slot management
- Load balancing: global HTTPS, SSL proxy, TCP proxy, internal
- Hybrid networking: Dedicated Interconnect, Partner Interconnect, HA VPN

### Google Cloud Professional DevOps Engineer

**What It Proves:**
- CI/CD pipeline implementation
- Monitoring, logging, and alerting
- Incident management (SRE principles)
- Infrastructure as Code (Deployment Manager, Terraform)

**Key Topics:**
- Cloud Build, Artifact Registry, Container Registry
- Cloud Deploy: delivery pipelines, rollout strategies
- SRE: SLI, SLO, error budgets, toil reduction
- Cloud Monitoring: uptime checks, alerting policies, dashboards
- Cloud Logging: log-based metrics, log sinks, exclusion filters
- Cloud Profiler, Cloud Trace
- Config Connector, Config Controller (ACM)

---

## 4. Oracle Cloud Certifications

### Certification Roadmap

```
        ┌──────────────────────────┐
        │ OCI Foundations 2024    │
        │   (1Z0-1085)            │
        └───────────┬──────────────┘
                    │
        ┌───────────┴──────────────┐
        │                          │
        ▼                          ▼
┌──────────────────┐   ┌──────────────────┐
│  OCI Architect   │   │  OCI DevOps      │
│    Associate     │   │  Associate       │
└────────┬─────────┘   └──────────────────┘
         │
         ▼
┌──────────────────┐
│  OCI Architect   │
│    Professional  │
└──────────────────┘
```

### OCI Architect Professional

**Key Topics:**
- OCI networking: VCN, DRG, FastConnect, VPN
- Compute: bare metal, VM, dedicated host, autonomous Linux
- Storage: block, object, file, archive, Data Guard
- Database: Autonomous DB, Exadata, RAC, Data Guard
- Security: IAM, compartments, Vault, WAF, Cloud Guard
- Observability: Monitoring, Logging, Events

---

## 5. CNCF / Kubernetes Certifications

### Certification Roadmap

```
        ┌──────────────────┐
        │      KCNA        │
        │  (K8s & Cloud    │
        │   Native Assoc)  │
        └────────┬─────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│   CKA    │ │  CKAD    │ │   CKS    │
│  Admin   │ │  Dev     │ │  Security│
└──────────┘ └──────────┘ └──────────┘
```

### CKA: Certified Kubernetes Administrator

**What It Proves:**
- Kubernetes cluster installation and configuration
- Workload management (Pods, Deployments, DaemonSets, StatefulSets)
- Networking (Services, Ingress, Network Policies, CNI)
- Storage (PersistentVolumes, PersistentVolumeClaims, StorageClasses, CSI)
- Troubleshooting (cluster, nodes, pods, networking)
- Security (RBAC, ServiceAccounts, Pod Security Standards)

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | Hands-on performance-based (terminal) |
| Duration | 2 hours |
| Cost | $395 USD |
| Passing | 75% (on a scale) |
| Validity | 2 years |
| Remote | Yes, with proctoring |

**Study Path (8-10 weeks):**

| Week | Topic | Resources |
|------|-------|-----------|
| 1-2 | K8s architecture, Pods, Deployments | Kubernetes.io docs, KodeKloud course |
| 3-4 | Services, Ingress, Networking | Hands-on labs, CKA scenarios |
| 5-6 | Storage, ConfigMaps, Secrets | PersistentVolume exercises |
| 7-8 | RBAC, Security, Resource Quotas | Pod Security Standards |
| 9 | Troubleshooting, Cluster operations | `kubectl`, systemd, etcdctl |
| 10 | Mock exams (Killer.sh, KodeKloud) | Score > 85% on mocks |

**Key Exam Skills:**
- `kubectl run`, `create`, `apply`, `delete`, `get`, `describe`, `logs`, `exec`
- Creating YAML from scratch (deployments, services, pods)
- etcd backup and restore
- Node maintenance (cordon, drain, uncordon)
- Upgrade cluster (kubeadm upgrade)
- Troubleshoot: pods not starting, services not accessible, nodes NotReady

### CKAD: Certified Kubernetes Application Developer

**What It Proves:**
- Design and build cloud-native applications on K8s
- Configure and use Pods, Deployments, Services, ConfigMaps
- Multi-container Pod patterns (sidecar, adapter, ambassador)
- Observability: liveness/readiness probes, resource limits
- Helm charts, Custom Resource Definitions (basic)

**Exam Differences from CKA:**
- More focus on application patterns, less on cluster operations
- No cluster installation, no etcd backup/restore
- More emphasis on Deployments, ConfigMaps, Secrets, Ingress
- Includes Helm and custom resource usage

### CKS: Certified Kubernetes Security Specialist

**What It Proves:**
- Cluster hardening (RBAC, ServiceAccounts, Pod Security Standards)
- Supply chain security (image scanning, signing, admission controllers)
- Runtime security (Falco, seccomp, AppArmor, Pod Security Admission)
- Network security (Network Policies, encryption, mTLS with service mesh)

**Prerequisites:** Must hold valid CKA certification

---

## 6. HashiCorp Certifications

### Certification Roadmap

```
        ┌──────────────────┐
        │ Terraform Assoc  │
        │    (003)         │
        └────────┬─────────┘
                 │
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────────┐
│  Vault   │ │  Consul  │ │ Terraform    │
│ Associate│ │ Associate│ │ Advanced     │
└──────────┘ └──────────┘ └──────────────┘
```

### Terraform Associate (003)

**What It Proves:**
- Understand Terraform basics: HCL syntax, resources, data sources
- Manage state: backends, remote state, state locking
- Work with modules: public registry, private modules
- Terraform workflow: init, plan, apply, destroy
- Terraform Cloud / HCP Terraform basics

**Exam Details:**
| Item | Detail |
|------|--------|
| Format | 57 questions (multiple choice) |
| Duration | 1 hour |
| Cost | $70.50 USD |
| Passing | 70% |
| Validity | 2 years |
| Language | English, Japanese |

**Key Topics:**
| Topic | Weight |
|-------|--------|
| Infrastructure as Code concepts | 12% |
| Terraform CLI | 18% |
| Configuration language | 16% |
| State management | 14% |
| Modules | 14% |
| Terraform Cloud / Enterprise | 14% |
| Provisioners, functions, expressions | 12% |

**Exam Tips:**
- Know the Terraform workflow (init, validate, plan, apply, destroy)
- Understand state: purpose, storage backends (S3, GCS, AzureRM, Terraform Cloud)
- Module composition: source, version, inputs, outputs
- Understand `count` vs `for_each`
- Know `terraform workspace` commands
- Understand `depends_on`, `lifecycle`, `provisioner`
- Terraform Cloud: workspaces, runs, variables, state versions
- Sentinel basics: policy as code

### Vault Associate (002)

**Key Topics:**
- Auth methods: token, userpass, LDAP, AWS, Azure, GCP, Kubernetes
- Secret engines: KV, database, AWS, Azure, GCP, PKI, transit
- Dynamic secrets: database credentials, cloud credentials
- Policies: path-based, capabilities, templating
- Vault architecture: storage backends, HA, seal/unseal
- Vault CLI and API
- Tokens: create, renew, revoke, orphan
- Encryption as a service (transit engine)

### Consul Associate (002)

**Key Topics:**
- Service mesh: intentions, mTLS, L7 traffic management
- Service discovery: DNS, HTTP API
- Health checks: script, HTTP, TCP, gRPC
- KV store: get, put, delete, blocking queries
- Agent: client vs server, datacenter configuration
- Connect: service mesh, sidecar proxies, transparent proxy

---

## 7. Cloudflare Certifications

### Cloudflare Fundamentals

**Key Topics:**
- Cloudflare global network (anycast, 300+ cities)
- DNS management, DNSSEC
- CDN caching, Tiered Cache, Argo
- DDoS protection (L3/L4/L7)
- WAF rules, rate limiting, IP access rules
- SSL/TLS modes (Flexible, Full, Strict)
- Basic Workers and Workers KV

### Cloudflare Network Professional

**Additional Topics:**
- Magic Transit, Magic Firewall, Magic WAN
- Argo Smart Routing, Tiered Cache optimization
- Traffic steering: geo, latency, random, hash
- Cloudflare Spectrum
- Load balancing: origin pools, monitors, health checks

### Cloudflare Security Professional

**Additional Topics:**
- Advanced WAF rulesets
- Bot Management (automated, verified bot, score-based)
- Rate Limiting (advanced, volume-based)
- Zero Trust: Access, Gateway, Browser Isolation
- API Shield
- SSL/TLS: custom certificates, ACM, CA certificates

---

## 8. Cross-Provider Certification Strategy

### Recommended Certification Paths

**Path A: Cloud Architect (Multi-Cloud)**
```
AWS Solutions Architect Associate (SAA-C03)
  → AWS Solutions Architect Professional (SAP-C02)
    → Azure AZ-305 Solutions Architect Expert
      → GCP Professional Cloud Architect
```

**Path B: DevOps / Platform Engineer**
```
CKA (Kubernetes Administrator)
  → Terraform Associate (003)
    → AWS DevOps Engineer Professional (DOP-C02)
      → CKAD (Kubernetes Application Developer)
```

**Path C: Security Specialist**
```
AWS Security Specialty (SCS-C02)
  → Azure AZ-500 Security Engineer
    → CKS (Kubernetes Security Specialist)
      → Vault Associate
```

**Path D: Developer Focused**
```
AWS Developer Associate (DVA-C02)
  → CKAD (Kubernetes Application Developer)
    → Azure AZ-204 Developer
      → Terraform Associate
```

### Study Time Estimates

| Certification | Study Time | Difficulty | Cost |
|---------------|-----------|------------|------|
| AWS Cloud Practitioner | 2-3 weeks | Easy | $100 |
| AWS Solutions Architect Associate | 6-8 weeks | Medium | $150 |
| AWS Solutions Architect Professional | 8-12 weeks | Hard | $300 |
| AWS DevOps Professional | 8-12 weeks | Hard | $300 |
| AWS Security Specialty | 6-10 weeks | Medium | $300 |
| Azure AZ-900 | 2-3 weeks | Easy | $99 |
| Azure AZ-104 | 6-8 weeks | Medium | $165 |
| Azure AZ-305 | 8-12 weeks | Hard | $165 |
| GCP ACE | 6-8 weeks | Medium | $125 |
| GCP Professional Cloud Architect | 8-12 weeks | Hard | $200 |
| CKA | 8-10 weeks | Hard | $395 |
| CKAD | 6-8 weeks | Medium | $395 |
| CKS | 8-10 weeks | Hard | $395 |
| Terraform Associate | 4-6 weeks | Easy | $70.50 |
| Vault Associate | 4-6 weeks | Medium | $70.50 |

### Cost-Effective Study Resources

| Resource | Cost | Best For |
|----------|------|----------|
| AWS Documentation (free) | Free | All AWS certs |
| Azure Documentation (free) | Free | All Azure certs |
| GCP Documentation (free) | Free | All GCP certs |
| A Cloud Guru / Pluralsight | $35-50/month | Video courses, hands-on labs |
| KodeKloud | $30/month | K8s certs (CKA/CKAD/CKS), Terraform |
| TutorialsDojo | $15-20/exam | Practice exams (AWS, Azure) |
| Whizlabs | $20/month | Practice tests |
| Killer.sh | $45 (1 exam) | CKA/CKAD/CKS mock exams |
| K8s.io docs (free) | Free | CKA/CKAD reference during exam |
| GitHub / O'Reilly | $40/month | Books and learning paths |
| YouTube | Free | FreeCodeCamp, TechWorld with Nana |
| AWS Skill Builder | $30/month | Official AWS learning + free labs |

### General Exam Tips

1. **Hands-on first**: Book time in free tiers / sandbox environments
2. **Take notes**: Write key concepts, port numbers, limits in your own words
3. **Practice exams**: Score > 85% before taking the real exam
4. **Review wrong answers**: Understand WHY incorrect answers are wrong
5. **Sleep well**: The night before the exam, no studying after 8 PM
6. **Exam day**: Read questions carefully, flag difficult ones, eliminate wrong answers
7. **Time management**: For 65 questions / 130 min = 2 minutes per question
8. **Cloud concepts are similar**: After one provider cert, the next is faster (60-70% concepts transfer)
9. **Renew promptly**: Most certs have annual renewal with free training
10. **Document your learning**: Create GitHub repo with notes, diagrams, study guides

---

*Last updated: July 2026*
