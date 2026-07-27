# HashiCorp Interview Guide

## Overview
Comprehensive preparation guide for HashiCorp technical interviews — Solutions Engineer, Software Engineer, Cloud Engineer, and Developer Advocate roles.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| Solutions Engineer | IC3-IC5 | Customer demos, POCs, IaC workflows, enterprise adoption |
| Software Engineer | IC4-IC6 | Core product development (Terraform, Vault, Consul), Go, open source |
| Cloud Engineer | IC4-IC5 | HCP (HashiCorp Cloud Platform) infrastructure, operations |
| Developer Advocate | IC4-IC5 | Community, content creation, open source, technical writing |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, IaC experience, open source awareness |
| Hiring Manager | 45 min | Technical breadth, product experience, HashiCorp principles |
| Technical Screen | 60 min | Terraform state/modules/providers, Vault secrets, Consul mesh |
| Onsite Loop | 4 x 45 min | System design, technical depth, open source, behavioral |

## HashiCorp Products Overview

### Terraform
**Core concepts:**
- HCL (HashiCorp Configuration Language): blocks, arguments, expressions, functions, dynamic blocks
- Resources and data sources: resource lifecycle, `count` vs `for_each`, `depends_on`, `lifecycle` (create_before_destroy, prevent_destroy, ignore_changes)
- State: local backends vs remote backends (S3, GCS, AzureRM, Terraform Cloud), state locking, state migrations, `terraform state` commands
- Modules: root module, child modules, module composition, source types (registry, GitHub, S3, GCS), version constraints
- Providers: official vs community, provider requirements, provider configuration, provider aliases
- Workspaces: CLI workspaces vs Terraform Cloud workspaces, variable hierarchy
- Terraform Cloud / HCP Terraform: runs, plans, applies, remote state, VCS integration, policy enforcement (Sentinel), private module registry

**Advanced topics:**
- Sentinel policy as code: enforcement levels (advisory, soft mandatory, hard mandatory), import policies
- Terraform import: `terraform import`, `import` blocks
- Terraform test: unit tests, integration tests, mock provider
- Terraform Stacks: deploy-time configuration, components
- Provider development: Go SDK v2, acceptance tests, CRUD operations, custom resources
- State management best practices: isolation per environment, per team, per component

### Vault
**Core concepts:**
- Auth methods: Token, Userpass, LDAP, AppRole, Kubernetes, AWS, Azure, GCP, OIDC
- Secret engines: KV v2 (versioned), databases (dynamic credentials), AWS (IAM/STS), Azure (service principals), GCP (service account keys), PKI (certificates), Transit (encryption-as-a-service), TOTP, SSH
- Policies: path-based, capabilities (create, read, update, delete, list, sudo, deny), templating
- Dynamic secrets: short-lived, auto-rotation, lease management
- Encryption-as-a-service: transit engine, key rotation, convergent encryption, HMAC

**Advanced topics:**
- Vault architecture: storage backends (Consul, Raft, Integrated Storage), seal/unseal, auto-unseal (KMS, Azure Key Vault, GCP Cloud KMS), HA cluster
- Performance replicas (enterprise): scale read operations, DR replication
- Identity: entities, groups, aliases, external groups
- Sentinel policies (enterprise): EGP for paths, RGP for responses
- Vault Agent: auto-auth, caching, template rendering (consul-template)
- Vault Secrets Operator: Kubernetes CRD-based synchronization

### Consul
**Core concepts:**
- Service discovery: DNS (`.service.consul`), HTTP API, sidecar proxies
- Health checks: script, HTTP, TCP, gRPC, Docker, TTL, H2PING
- KV store: get, put, delete, CAS, blocking queries, watches
- Connect service mesh: intentions (allow/deny), mTLS, sidecar proxies (Envoy, built-in), ingress/terminating gateways, transparent proxy
- Configuration entries: service-defaults, proxy-defaults, service-resolver, service-splitter, service-router, service-intentions

**Advanced topics:**
- Agent: client vs server mode, datacenter configuration, LAN vs WAN gossip, RPC forwarding
- ACL system: tokens, roles, policies, service identity
- Consul on Kubernetes: Consul Helm chart, Consul Dataplane, Admin Partition
- Admin partitions (enterprise): multi-tenancy
- Service mesh observability: Envoy metrics, Envoy access logs, Consul UI
- Service mesh security: intentions, mTLS, SPIFFE identities, certificate rotation

### Other HashiCorp Products

| Product | Purpose | Key Concepts |
|---------|---------|--------------|
| **Boundary** | Identity-based access to hosts | Targets, sessions, workers, credential brokering |
| **Nomad** | Workload orchestration | Jobs, task groups, scheduling, node classes, CSI volumes |
| **Packer** | Machine image creation | Builders, provisioners, post-processors, HCL templates |
| **Waypoint** | Application deployment | Build, deploy, release, runners, HCL workflows |
| **Sentinel** | Policy as code | Import policies, rule-based, enforcement levels |

## HashiCorp Principles

1. **Transparency** — Open source everything, public roadmaps, RFC process
2. **Iteration** — Ship early, iterate based on feedback, pragmatic over perfect
3. **Community** — Contributors, partners, ecosystem, meetups
4. **Empathy** — Understand user needs, build for practitioners
5. **Wabi-Sabi** — Embrace imperfection, find beauty in completeness over perfection

## Sample Interview Questions

### Terraform-Specific
1. **Design Terraform state management for a multi-team, multi-environment setup**
2. **How would you structure Terraform modules for reusable infrastructure?**
3. **Walk through a Terraform state corruption scenario and recovery**
4. **What are the differences between `count` and `for_each`? When would you use each?**
5. **Design a Sentinel policy that enforces tagging and restricts expensive instance types**
6. **How do you handle secrets in Terraform?** (Vault provider, sensitive parameters)

### Vault-Specific
1. **How would you set up dynamic database credentials for a MySQL application?**
2. **Design a PKI infrastructure with Vault for a Kubernetes cluster**
3. **Explain the Vault seal/unseal process and auto-unseal options**
4. **How do you handle Vault disaster recovery?** (DR replication, backup)
5. **Design a secret management strategy for a multi-cloud application**

### Consul-Specific
1. **How does Consul service discovery work compared to DNS-based approaches?**
2. **Design a service mesh with Consul Connect for microservices in Kubernetes**
3. **Explain Consul intentions and how they enforce service-to-service security**
4. **How does Consul handle multi-datacenter deployments?**

### General Architecture
1. **Design an IaC pipeline for multi-cloud deployments** (AWS + Azure + GCP)
2. **How would you implement zero-trust security for a distributed application?** (Vault + Consul + Boundary)
3. **Design a secrets lifecycle management system** (creation, rotation, revocation, auditing)
4. **Compare Terraform vs Pulumi vs CloudFormation vs ARM/Bicep**

## Open Source Contribution Guide (for Interview)

**Prepare to discuss:**
- Your contributions to Terraform, Vault, Consul, or related projects
- Pull request workflow: forking, branching, tests, documentation
- Provider development experience (Terraform Provider SDK)
- Community engagement: issues, discussions, GitHub stars badges
- Technical writing: documentation contributions, blog posts

## Key Open Source Repositories

| Repository | Stars | Language | Description |
|------------|-------|----------|-------------|
| terraform-provider-aws | 10K+ | Go | Terraform AWS provider |
| terraform-provider-azurerm | 4.5K+ | Go | Terraform Azure provider |
| terraform-provider-google | 2.5K+ | Go | Terraform Google provider |
| vault | 30K+ | Go | HashiCorp Vault |
| consul | 28K+ | Go | HashiCorp Consul |
| terraform | 42K+ | Go | HashiCorp Terraform |
| packer | 15K+ | Go | HashiCorp Packer |
| boundary | 2.5K+ | Go | HashiCorp Boundary |
| nomad | 15K+ | Go | HashiCorp Nomad |
| terraform-cdk | 5K+ | TypeScript | CDK for Terraform |

## Cert Path

| Certification | Focus | Time |
|--------------|-------|------|
| Terraform Associate (003) | Fundamentals, workflow, state, modules, Cloud | 4-6 weeks |
| Vault Associate (002) | Auth, secrets, policies, encryption | 4-6 weeks |
| Consul Associate (002) | Service discovery, service mesh, KV, health | 4-6 weeks |
| Terraform Advanced | Provider dev, advanced state, Sentinel, Stacks | 8-10 weeks |

---

*Last updated: July 2026*
