# Azure Interview Guide

## Overview
Comprehensive preparation guide for Microsoft Azure technical interviews — Solutions Architect, Cloud Engineer, DevOps Engineer, Security Engineer.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| Azure Solutions Architect | Level 63-68 | Customer-facing, enterprise architecture, hybrid cloud, migration |
| Cloud Engineer | Level 62-65 | ARM/Bicep, Terraform, Azure DevOps, PowerShell, governance |
| Azure DevOps Engineer | Level 62-66 | CI/CD pipelines, IaC, release management, artifacts |
| Azure Security Engineer | Level 63-67 | Azure AD, Defender, Sentinel, Key Vault, compliance |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, compensation, team fit |
| Technical Screen | 60 min | Azure service knowledge, architecture scenarios |
| ASM (Appropriate Screening Method) | 30 min | Decision-making, growth mindset, collaboration |
| Onsite Loop | 4-5 x 45 min | System design, technical depth, behavioral, cross-team |

## Core Technical Topics

### Compute
- **Azure VMs**: Series (D, E, F, B, N), Availability Sets vs Availability Zones, VMSS, Disk types (Ultra, Premium, Standard SSD/HDD), Azure Dedicated Host
- **App Service**: App Service Plan (B, S, P, Isolated), deployment slots, auto-scaling, VNet integration, Hybrid Connections, App Service Environment (ASE)
- **Azure Functions**: Consumption, Premium, Dedicated plans, triggers and bindings, Durable Functions, function app settings
- **AKS**: Cluster architecture, node pools, system/user nodes, uptime SLA, Azure CNI vs Kubenet, managed identities, pod identity
- **Container Instances**: Quick containers, GPU support, VNet deployment

### Storage
- **Blob Storage**: Hot, Cool, Cold, Archive tiers, access tiers, lifecycle management, soft delete, versioning, replication (LRS, ZRS, GRS, RA-GRS)
- **Azure Files**: SMB file shares, Azure File Sync (hybrid), snapshot, soft delete, identity-based auth
- **Managed Disks**: Ultra, Premium SSD v2, Premium SSD, Standard SSD, Standard HDD, disk bursting, encryption at rest with CMK
- **Azure NetApp Files**: High-performance NFS/SMB, latency-sensitive workloads, SAP, HPC

### Database
- **Azure SQL**: DTU vs vCore, SQL Managed Instance, elastic pools, geo-replication, failover groups, long-term retention, Ledger
- **Cosmos DB**: Multi-region writes, consistency levels, partition keys, RU provisioning, SDK connectivity modes, Change Feed
- **Azure Database for MySQL/PostgreSQL**: Flexible Server, High Availability (zone-redundant, same-zone), read replicas, Azure Database Migration Service
- **Azure Cache for Redis**: Standard vs Premium vs Enterprise tiers, clustering, persistence, geo-replication, active geo-replication

### Networking
- **VNet**: Address space, subnets, VNet peering, service endpoints, private endpoints, VNet integration for PaaS, Azure DNS
- **Azure Load Balancer**: Basic vs Standard, public vs internal, HA ports, outbound rules, backend pool
- **Application Gateway**: WAF, path-based routing, URL rewrite, SSL offloading, autoscaling, end-to-end TLS
- **Azure Front Door**: Global HTTP load balancer, SSL termination, WAF, URL-based routing, caching, compression
- **Traffic Manager**: DNS-based traffic routing, routing methods (priority, weighted, performance, geographic, multivalue, subnet)
- **Azure Firewall**: DNAT/SNAT, network rules, application rules, threat intelligence, DNS proxy, forced tunneling
- **ExpressRoute**: Circuit, local vs standard vs premium, private peering, Microsoft peering, FastPath, bandwidth options
- **VPN Gateway**: Site-to-Site, Point-to-Site, VNet-to-VNet, Active-Active, ExpressRoute coexistence

### Security
- **Azure AD**: Tenant, Conditional Access, Identity Protection, Privileged Identity Management (PIM), managed identities, app registrations, B2B, B2C
- **Microsoft Defender for Cloud**: Cloud Security Posture Management (CSPM), workload protection, regulatory compliance, security score
- **Azure Sentinel**: SIEM + SOAR, data connectors, analytics rules, automation rules, playbooks
- **Key Vault**: Secrets, keys, certificates, soft-delete, purge protection, RBAC vs access policies, managed HSM
- **Azure Policy**: Built-in vs custom policy definitions, initiative, assignment, remediation, exemption, guest configuration
- **Role-Based Access Control (RBAC)**: Built-in roles, custom roles, scope (management group, subscription, RG, resource)

### Monitoring
- **Azure Monitor**: Metrics, Logs (Log Analytics), alerts, action groups, workbooks, Application Insights
- **Application Insights**: Distributed tracing, dependency tracking, availability tests, smart detection, usage analysis
- **Log Analytics**: KQL queries, log-based alerts, workspaces, table schemas, workspace insights

## Microsoft Competencies

1. **Growth Mindset** — Open to feedback, learns from failures, seeks improvement
2. **Customer Obsession** — Deep understanding of customer needs, anticipates future requirements
3. **Diversity and Inclusion** — Actively includes diverse perspectives, creates belonging
4. **Collaboration** — Breaks silos, shares credit, works across teams
5. **Making a Difference** — Creates impact beyond role, drives results
6. **Technical Excellence** — Deep technical knowledge applied practically
7. **Innovation** — Introduces new ideas that create customer value

## Azure Key Architecture Patterns

### Hub-Spoke Topology
```
Management Subscription
├── Hub VNet (Connectivity)
│   ├── Azure Firewall
│   ├── Azure Bastion
│   ├── VPN/ExpressRoute Gateway
│   └── Azure DNS Private Resolver
└── Shared Services VNet
    ├── Domain Controllers
    ├── Management Tools (Log Analytics, Automation)
    └── Shared File Servers

Application Subscriptions
├── Spoke VNet (PROD)
│   ├── App Tier (App Service, AKS)
│   ├── Data Tier (Azure SQL, Cosmos DB)
│   └── Private Endpoints
├── Spoke VNet (STAGING)
└── Spoke VNet (DEV)
```

### Hybrid Cloud Pattern
```
On-Premises
├── Active Directory
├── SQL Server
├── File Server
└── MPLS/WAN

Azure (Connected via ExpressRoute + VPN)
├── Azure AD Connect (Identity Sync)
├── Azure Files + File Sync (Cloud tiering)
├── Azure SQL Managed Instance (Distributed AG)
├── Azure Site Recovery (DR)
└── Azure Backup (Long-term retention)
```

## Sample System Design Problems

1. **Enterprise application migration with hybrid connectivity**
2. **Design a multi-region active-active application with Cosmos DB**
3. **Design a secure CI/CD pipeline with Azure DevOps**
4. **Design a landing zone for enterprise Azure adoption (CAF)**
5. **Design a data analytics platform with Azure Synapse**

## Exam Path (Target: AZ-305)

| Cert | Focus | Time |
|------|-------|------|
| AZ-900 | Fundamentals | 2-3 weeks |
| AZ-104 | Administrator | 6-8 weeks |
| AZ-305 | Architect Expert | 8-12 weeks |
| AZ-500 | Security Engineer | 6-10 weeks |

## Key Azure Limits

| Service | Limit |
|---------|-------|
| VNet per subscription | 1000 |
| VNet peering per VNet | 500 |
| Public IP per subscription | 1000 |
| NSG rules per NSG | 1000 |
| Storage account capacity | 5 PiB |
| App Service instances per plan | 30 (isolated: 100) |
| Functions Consumption max execution | 10 min |
| Functions Premium max execution | 60 min |

---

*Last updated: July 2026*
