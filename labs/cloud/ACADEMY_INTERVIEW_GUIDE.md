# Cloud Academy Interview Guide

## Overview

Comprehensive interview preparation for cloud engineering roles across major cloud providers and infrastructure companies. This guide covers role types, certifications, interview rounds, and key preparation strategies for each company.

---

## Table of Contents

1. [Amazon Web Services (AWS)](#1-amazon-web-services-aws)
2. [Microsoft Azure](#2-microsoft-azure)
3. [Google Cloud Platform (GCP)](#3-google-cloud-platform-gcp)
4. [Oracle Cloud Infrastructure (OCI)](#4-oracle-cloud-infrastructure-oci)
5. [Cloudflare](#5-cloudflare)
6. [HashiCorp](#6-hashicorp)
7. [Datadog](#7-datadog)
8. [Docker Inc.](#8-docker-inc)
9. [Kubernetes / CNCF](#9-kubernetes-cncf)
10. [Cross-Company Preparation Strategy](#10-cross-company-preparation-strategy)

---

## 1. Amazon Web Services (AWS)

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Cloud Architect | L6-L8 | Enterprise architecture, migration strategy, Well-Architected reviews |
| Cloud Engineer / SysOps | L4-L6 | EC2, VPC, IAM, automation, operational excellence |
| Solutions Architect | L5-L7 | Customer-facing presales, technical demos, RFP responses |
| DevOps Engineer | L5-L7 | CI/CD pipelines, infrastructure as code, container orchestration |
| Security Engineer | L5-L7 | IAM policies, KMS, Shield, WAF, compliance |
| Data Engineer | L5-L7 | Redshift, EMR, Kinesis, Glue, Athena |
| Networking Engineer | L5-L7 | Direct Connect, VPN, Transit Gateway, Route 53 |

### Certifications Expected

- **Associate Level**: AWS Certified Solutions Architect - Associate, AWS Certified Developer - Associate, AWS Certified SysOps Administrator - Associate
- **Professional Level**: AWS Certified Solutions Architect - Professional, AWS Certified DevOps Engineer - Professional
- **Specialty**: AWS Certified Security - Specialty, AWS Certified Data Analytics - Specialty, AWS Certified Advanced Networking - Specialty, AWS Certified Machine Learning - Specialty

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Phone Screen | 45 min | Background, tech stack, motivation, Leadership Principles check |
| Technical Phone | 60 min | System design, architecture decisions, AWS service selection |
| Loop (Onsite) — 4-5 rounds | 45 min each | |
| Round 1 | System Design | Large-scale distributed systems, trade-off analysis |
| Round 2 | Leadership Principles | Behavioral questions with STAR/LP framework |
| Round 3 | Technical Deep Dive | Previous project architecture, design decisions |
| Round 4 | Coding / Problem Solving | Algorithms, data structures, scripting |
| Round 5 | Bar Raiser | Leadership Principles, overall fit, raises hiring bar |

### Preparation Focus

- **16 Leadership Principles**: Customer Obsession, Ownership, Invent and Simplify, Are Right A Lot, Learn and Be Curious, Hire and Develop the Best, Insist on the Highest Standards, Think Big, Bias for Action, Frugality, Earn Trust, Dive Deep, Have Backbone Disagree and Commit, Deliver Results, Strive to be Earths Best Employer, Success and Scale Bring Broad Responsibility
- **Well-Architected Framework**: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, Sustainability
- **AWS Service Depth**: Know 20+ core services in depth, understand service limits, pricing models, and common failure modes
- **System Design**: Prepare whiteboard designs for web apps, data pipelines, real-time systems, and microservices

---

## 2. Microsoft Azure

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Azure Cloud Architect | Level 61-67 | Enterprise agreements, hybrid cloud, migration |
| Azure Solutions Architect | Level 63-68 | Customer-facing, Azure solutions design, pricing |
| Cloud Engineer | Level 62-65 | ARM templates, Terraform, Azure DevOps, PowerShell |
| Azure DevOps Engineer | Level 62-66 | CI/CD, Azure Pipelines, artifacts, release management |
| Azure Security Engineer | Level 63-67 | Azure AD, Conditional Access, Defender, Sentinel |
| Azure Data Engineer | Level 62-66 | Synapse, Data Factory, Cosmos DB, Databricks |
| Azure AI Engineer | Level 63-67 | Cognitive Services, OpenAI, ML pipelines |

### Certifications Expected

- **Fundamental**: AZ-900 Azure Fundamentals
- **Associate**: AZ-104 Azure Administrator, AZ-204 Azure Developer, AI-102 AI Engineer, DP-100 Data Scientist
- **Expert**: AZ-305 Azure Solutions Architect Expert, AZ-400 DevOps Engineer Expert, AZ-500 Security Engineer
- **Specialty**: DP-300 Azure Database Administrator, SC-100 Cybersecurity Architect

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Experience, role alignment, compensation |
| Technical Screen | 60 min | Azure service knowledge, architecture scenarios |
| Loop (Onsite) — 4-5 rounds | 45 min each | |
| Round 1 | System Design | Hybrid cloud, migration, enterprise integration |
| Round 2 | Technical Deep Dive | Previous implementation, IaC, automation |
| Round 3 | Behavioral / Microsoft Competencies | Growth mindset, customer obsession, diversity |
| Round 4 | Whiteboarding | Azure architecture for given scenario |
| Round 5 | Cross-team collaboration | Partner engineering, stakeholder management |

### Preparation Focus

- **Microsoft-specific behaviors**: Growth mindset, customer-obsessed, diverse and inclusive, one Microsoft, make a difference
- **Hybrid-first**: Azure Arc, Azure Stack HCI, hybrid connectivity, VPN Gateway, ExpressRoute
- **Enterprise integration**: Active Directory, Azure AD B2B/B2C, identity federation
- **Cost management**: Azure Pricing Calculator, TCO, Azure Reservations, Azure Hybrid Benefit
- **Governance**: Azure Policy, Blueprints, Management Groups, RBAC

---

## 3. Google Cloud Platform (GCP)

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Cloud Architect | L4-L6 | GCP infrastructure design, migration, best practices |
| Cloud Engineer | L4-L5 | Compute Engine, GKE, Cloud Storage, IAM |
| Solutions Architect | L5-L7 | Customer-facing, technical sales, proof of concepts |
| Site Reliability Engineer (SRE) | L5-L7 | Reliability, SLIs/SLOs/SLAs, incident response |
| Cloud Developer | L4-L6 | Cloud Run, Cloud Functions, App Engine, Firestore |
| Data Engineer | L5-L6 | BigQuery, Dataflow, Pub/Sub, Dataproc |
| Security Engineer | L5-L7 | Cloud CUD, BeyondCorp, VPC Service Controls |

### Certifications Expected

- **Fundamental**: Google Cloud Digital Leader
- **Associate**: Google Cloud Associate Cloud Engineer
- **Professional**: Google Cloud Professional Cloud Architect, Professional Data Engineer, Professional Cloud Developer, Professional Cloud Network Engineer, Professional Cloud Security Engineer, Professional DevOps Engineer
- **Specialty**: Google Cloud Machine Learning Engineer, Looker Business Analyst

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, GCP experience, role expectations |
| Technical Phone | 60 min | System design, GCP service selection, trade-offs |
| Loop (Onsite) — 4-5 rounds | 45 min each | |
| Round 1 | Googleyness / Leadership | General cognitive ability, leadership, ambiguity |
| Round 2 | System Design | Large-scale, distributed, multi-region, resilience |
| Round 3 | Technical Coding | Algorithms, data structures, scripting for cloud automation |
| Round 4 | Architecture Deep Dive | Previous projects, design decisions, trade-offs |
| Round 5 | SRE-Specific (if applicable) | Incident management, monitoring, automation |

### Preparation Focus

- **Googleyness**: General cognitive ability, comfort with ambiguity, bias to action, intellectual humility
- **SRE culture**: Google SRE books, SLI/SLO/SLA definition, error budgets, toil reduction
- **GCP-native**: Serverless-first (Cloud Run, Cloud Functions), BigQuery for analytics, GKE for containers
- **Network architecture**: Premium Tier vs Standard Tier, VPC sharing, Private Google Access
- **IAM**: Resource hierarchy (Org -> Folder -> Project -> Resource), custom roles, service accounts

---

## 4. Oracle Cloud Infrastructure (OCI)

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| OCI Cloud Architect | IC3-IC5 | OCI migration, architecture, autonomous database |
| Cloud Solutions Architect | IC4-IC6 | Customer-facing, Oracle workload optimization |
| OCI Engineer | IC3-IC5 | IaaS, PaaS, networking, automation |
| Oracle Database Cloud Specialist | IC4-IC6 | Autonomous DB, Exadata Cloud, RAC migration |

### Certifications Expected

- **Foundations**: Oracle Cloud Infrastructure Foundations 2024
- **Associate**: OCI Architect Associate
- **Professional**: OCI Architect Professional, OCI DevOps Professional
- **Specialty**: OCI Data Management, OCI Security

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, Oracle ecosystem experience |
| Technical Screen | 60 min | OCI services, networking, database architecture |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | OCI-native architecture, Exadata integration |
| Round 2 | Technical Deep Dive | Previous Oracle workload migrations |
| Round 3 | Behavioral | Collaboration, problem-solving, Oracle values |
| Round 4 | Hands-on | OCI CLI, Terraform, console navigation |

### Preparation Focus

- **Oracle ecosystem**: Autonomous Database, Exadata, RAC, Data Guard
- **OCI networking**: FastConnect, DRG, VCN peering, security lists
- **Migration**: ZDM (Zero Downtime Migration), GoldenGate, Data Pump
- **Pricing**: Bring-your-own-license, Universal Credits, OCI discounts

---

## 5. Cloudflare

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Solutions Engineer | IC3-IC5 | Customer-facing technical demonstrations, proofs of concept |
| Systems Engineer | IC4-IC6 | Edge network architecture, DDoS mitigation, CDN |
| Network Engineer | IC4-IC6 | Anycast, BGP, peering, edge data center operations |
| Security Engineer | IC4-IC6 | WAF rules, DDoS, bot management, zero trust |

### Certifications Expected

- Cloudflare Fundamentals
- Cloudflare Network Professional
- Cloudflare Security Professional

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, interest in Cloudflare |
| Technical Screen | 60 min | Web performance, DNS, DDoS, CDN concepts |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Global traffic routing, edge compute, caching |
| Round 2 | Networking Deep Dive | TCP/IP, HTTP/2, QUIC, BGP, anycast |
| Round 3 | Security | DDoS mitigation, WAF, zero trust, TLS |
| Round 4 | Behavioral / Culture | Cloudflare values, transparency, reliability |

### Preparation Focus

- **Edge network**: Anycast routing, Argo Smart Routing, Tiered Cache
- **Performance**: HTTP/2, HTTP/3, QUIC, Brotli compression, OCSP stapling
- **Security**: WAF, Rate Limiting, DDoS protection, SSL/TLS, Zero Trust
- **Workers ecosystem**: Cloudflare Workers, KV, Durable Objects, R2 storage

---

## 6. HashiCorp

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Solutions Engineer | IC3-IC5 | Customer demos, Proof of Concepts for Terraform/Vault/Consul |
| Software Engineer | IC4-IC6 | Open source tooling, provider development, core product |
| Cloud Engineer | IC4-IC5 | HCP (HashiCorp Cloud Platform) infrastructure |
| Developer Advocate | IC4-IC5 | Community engagement, content creation, open source |

### Certifications Expected

- Terraform Associate (003)
- Vault Associate (002)
- Consul Associate (002)
- Terraform Advanced

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, IaC experience, open source contributions |
| Hiring Manager | 45 min | Technical breadth, HashiCorp product experience |
| Technical Screen | 60 min | Terraform state management, modules, Vault secrets, Consul service mesh |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Multi-cloud IaC, secret management, service discovery |
| Round 2 | Technical Deep Dive | IaC workflows, state management, Sentinel policies |
| Round 3 | Open Source Contribution | GitHub history, community involvement, technical writing |
| Round 4 | Behavioral / HashiCorp Principles | Transparency, humility, iteration, empathy |

### Preparation Focus

- **HashiCorp Terraform**: State management, workspaces, modules, providers, Sentinel policies, HCL
- **HashiCorp Vault**: Secret engines, dynamic secrets, transit encryption, auth methods
- **HashiCorp Consul**: Service mesh, connect, service discovery, health checks
- **HashiCorp Way of Working**: Open source ethos, community-driven development, rigorous documentation
- **HCP**: Cloud platform for managed HashiCorp services

---

## 7. Datadog

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Solutions Architect | IC3-IC5 | Customer onboarding, architecture guidance, observability |
| Software Engineer | IC4-IC6 | Agent development, APM, backend infrastructure |
| Site Reliability Engineer | IC4-IC6 | Internal infrastructure, reliability, performance |
| Technical Account Manager | IC3-IC5 | Enterprise customer support, health monitoring |

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, observability tooling experience |
| Technical Screen | 60 min | Monitoring, logging, tracing fundamentals |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Monitoring pipeline at scale, custom metrics |
| Round 2 | Coding | Python/Go, data processing, API design |
| Round 3 | Infrastructure | Agent architecture, integrations, Kubernetes monitoring |
| Round 4 | Behavioral | Datadog values, customer impact, collaboration |

### Preparation Focus

- **Observability**: Metrics, traces, logs — the three pillars
- **Datadog products**: APM, Log Management, Infrastructure, Synthetics, RUM, SIEM
- **Agent architecture**: Python agent, integrations, DogStatsD, custom check writing
- **Integrations**: AWS, Azure, GCP, Kubernetes, Docker, databases, queues

---

## 8. Docker Inc.

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Software Engineer | IC4-IC6 | Docker Engine, desktop, registry, Compose |
| Solutions Architect | IC4-IC6 | Customer demos, DevSecOps workflows |
| Product Manager | L4-L6 | Developer tooling, container ecosystem |
| Developer Advocate | IC4-IC5 | Community content, Docker Captain relations |

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, container experience |
| Technical Screen | 60 min | Docker internals, networking, storage drivers |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Registry architecture, image distribution |
| Round 2 | Operating Systems / Linux | Namespaces, cgroups, overlay filesystems |
| Round 3 | Coding | Go, container runtime, security scanning |
| Round 4 | Behavioral | Docker values, open source, developer experience |

### Preparation Focus

- **Container fundamentals**: Namespaces, cgroups, union filesystems (overlay2, AUFS)
- **Docker Engine**: containerd, runc, containerd-shim, gRPC API
- **Docker Compose**: Multi-service orchestration, Compose specification
- **Image distribution**: Registry, Docker Hub, mirroring, image signing
- **Docker Desktop**: Hyper-V/WSL2 backend, macOS Hypervisor.framework

---

## 9. Kubernetes / CNCF

### Role Types

| Role | Seniority | Focus |
|------|-----------|-------|
| Kubernetes Engineer | Mid-Senior | Cluster operations, networking, storage, security |
| Platform Engineer | Mid-Senior | Internal developer platforms, operator patterns, CRDs |
| SRE | Mid-Senior | Cluster reliability, auto-scaling, disaster recovery |
| Cloud Architect (K8s) | Senior | Multi-cluster, service mesh, GitOps, hybrid deployments |

### Certifications Expected

- **CKA**: Certified Kubernetes Administrator
- **CKAD**: Certified Kubernetes Application Developer
- **CKS**: Certified Kubernetes Security Specialist
- **KCNA**: Kubernetes and Cloud Native Associate

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, cloud native experience |
| Technical Phone | 60 min | Kubernetes architecture, pod lifecycle, networking |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Multi-cluster, service mesh, GitOps workflows |
| Round 2 | Hands-on Troubleshooting | Cluster debugging, pod failures, networking issues |
| Round 3 | Deep Dive | Previous deployment patterns, operator development |
| Round 4 | Behavioral / CNCF | Open source, community values, collaboration |

### Preparation Focus

- **Core K8s**: Pods, deployments, services, ingress, ConfigMaps, secrets, StatefulSets
- **Networking**: CNI plugins (Calico, Cilium), Service types, NetworkPolicies, Ingress controllers
- **Storage**: CSI drivers, PersistentVolumes, PersistentVolumeClaims, StorageClasses
- **Security**: RBAC, Pod Security Standards, OPA/Gatekeeper, service mesh mTLS
- **Operators**: CRDs, controller pattern, operator-sdk, Helm charts
- **GitOps**: ArgoCD, Flux, progressive delivery (Argo Rollouts, Flagger)

---

## 10. Cross-Company Preparation Strategy

### Universal Cloud Interview Topics

| Topic | Weight | Key Areas |
|-------|--------|-----------|
| Compute | 15% | Virtual machines, containers, serverless, auto-scaling |
| Storage | 12% | Object, block, file, tiers, lifecycle, durability |
| Networking | 15% | VPC, DNS, CDN, load balancing, VPN, peering |
| Security | 12% | IAM, encryption, compliance, secrets management |
| Databases | 10% | Relational, NoSQL, caching, replication |
| Monitoring | 8% | Metrics, logs, traces, alerting, dashboards |
| CI/CD | 8% | Pipelines, IaC, GitOps, artifact management |
| Cost | 5% | Pricing models, reserving, rightsizing, FinOps |
| Migration | 5% | 6 Rs (Rehost, Replatform, Refactor, etc.), assessment |
| Architecture | 10% | HA, DR, scalability, trade-offs, patterns |

### Recommended Study Timeline

| Phase | Duration | Focus |
|-------|----------|-------|
| Foundation | 4-6 weeks | Cloud fundamentals, core services across 3 providers |
| Deep Dive | 4-6 weeks | Primary target company services, architecture patterns |
| System Design | 2-3 weeks | Whiteboard practice, trade-off analysis, mock interviews |
| Behavioral | 1-2 weeks | STAR stories, company values, situation examples |
| Mock Interviews | 2-3 weeks | Full loops with peers, recording, feedback iteration |
| Certification | 4-8 weeks | Relevant cert for target company and role |

### Recommended Resources

- **Books**: Designing Data-Intensive Applications, AWS Well-Architected Framework whitepapers, Google SRE books, The Phoenix Project
- **Courses**: A Cloud Guru, Cloud Academy, Coursera cloud specializations, KodeKloud
- **Practice**: Pulumi/Terraform for IaC, Killercoda labs, Play with Kubernetes, AWS/Azure/GCP free tier
- **Communities**: r/aws, r/azure, r/googlecloud, Cloud Native Computing Foundation Slack, AgileBytes
- **Mock Platforms**: Pramp, interviewing.io, Exponent, Prepfully

### Key Differentiators by Company

| Company | What They Value Most |
|---------|---------------------|
| AWS | Leadership Principles, deep service knowledge, scale mindset |
| Azure | Hybrid cloud, enterprise integration, Microsoft ecosystem |
| GCP | Data/AI, SRE culture, Googleyness, scalability |
| Oracle | Database expertise, enterprise workloads, licensing |
| Cloudflare | Edge computing, network performance, security |
| HashiCorp | IaC expertise, open source contributions, community |
| Datadog | Observability depth, monitoring at scale, integrations |
| Docker | Container internals, developer experience, tooling |
| CNCF | Cloud native patterns, open source governance, Kubernetes |

### Salary Ranges (US Cloud Roles, 2024-2025)

| Role | Base Salary | Total Compensation |
|------|-------------|-------------------|
| Cloud Engineer (Mid) | $120k-$160k | $150k-$220k |
| Cloud Engineer (Sr) | $160k-$210k | $220k-$350k |
| Solutions Architect | $140k-$200k | $200k-$350k |
| Cloud Architect | $170k-$230k | $250k-$400k+ |
| DevOps Engineer | $130k-$180k | $180k-$300k |
| SRE | $150k-$210k | $220k-$400k |

---

*Last updated: July 2026*
