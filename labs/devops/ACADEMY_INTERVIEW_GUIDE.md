# DevOps Academy — Interview Guide

> Per-company DevOps/SRE/Platform interview preparation for the 12 most relevant employers.

---

## Table of Contents
1. Google
2. Meta (Production Engineer)
3. Amazon (Systems Engineer / DevOps)
4. Netflix (Cloud Engineer)
5. Microsoft (Azure DevOps)
6. GitHub
7. GitLab
8. HashiCorp
9. Datadog
10. New Relic
11. Splunk
12. PagerDuty

---

## 1. Google

### Role Types
- **Site Reliability Engineer (SRE)**: Classic SRE — 50% ops, 50% software engineering. Own SLIs/SLOs/SLAs, incident response, capacity planning, toil reduction.
- **Software Engineer, Infrastructure**: Builds internal tooling, cluster management, Borg/Omega, networking stack.
- **Cloud DevOps Engineer** (Google Cloud): Customer-facing, helps enterprises adopt GCP, CI/CD, Terraform, Kubernetes.

### SRE vs DevOps vs Platform Engineer at Google
| Role | Focus | Interview Emphasis |
|------|-------|-------------------|
| SRE | Reliability, automation, incident response | Coding (hard), System Design, SRE fundamentals |
| DevOps (Cloud) | GCP adoption, customer workflows | Coding, GCP services, IaC, CI/CD |
| Platform Engineer | Internal tooling, developer productivity | Coding (hard), distributed systems, API design |

### Interview Rounds (SRE)
1. **Phone Screen** (45 min): Coding — LeetCode medium. Systems basics — Linux, networking, distributed systems.
2. **Onsite (4-5 rounds)**:
   - **Coding** (2 rounds): LeetCode hard. Algorithms, data structures.
   - **System Design**: Design a distributed system (e.g., YouTube, Google Drive, key-value store).
   - **SRE Technical**: Linux internals, networking (TCP/IP, DNS, HTTP/2), debugging production incidents.
   - **Googleyness & Leadership**: Behavioral, cultural fit, handling ambiguity, leadership without authority.
3. **SRE-Specific Add-on**: Past incidents, toil reduction projects, automation impact.

### Key Prep Areas
- Linux: process lifecycle, signals, cgroups, namespaces, kernel tuning.
- Networking: TCP congestion control, BGP, HTTP/3, QUIC, load balancing algorithms.
- Distributed systems: consensus (Paxos/Raft), replication, CAP theorem, consistent hashing.
- Kubernetes: architecture, scheduler, controller manager, etcd, CNI, CSI.

### Study Resources
- _Site Reliability Engineering_ (Google SRE book).
- _The Site Reliability Workbook_.
- LeetCode (Hard): especially graphs, trees, dynamic programming, concurrency.

---

## 2. Meta (Production Engineer)

### Role Types
- **Production Engineer (PE)**: Hybrid SRE/DevOps. Build and maintain infrastructure for Facebook, Instagram, WhatsApp, Messenger.
- **Production Engineer (Network)**: Focused on backbone, data center networking, BGP, SDN, Wedge/FBOSS.
- **Site Reliability Engineer (smaller org)**: More traditional SRE within specific product teams.

### SRE vs DevOps vs Platform Engineer at Meta
Meta uses "Production Engineer" as a unified title. PEs write code, manage infrastructure, and respond to incidents. Less separation than Google.

### Interview Rounds
1. **Recruiter Screen** (30 min): Overview, logistics.
2. **Coding Screen** (45 min, CoderPad): LeetCode Medium. Python, C++, PHP, or Hack.
3. **Onsite (4 rounds)**:
   - **System Design** (1 round): Design a system at scale (e.g., news feed, chat, video upload).
   - **Coding** (2 rounds): LeetCode Medium-Hard. Emphasis on clean, production-quality code.
   - **Linux/Production Debugging** (1 round): Debug a real-world production issue — strace, tcpdump, perf, gdb.
4. **Behavioral** (sometimes embedded across rounds): Ownership, conflict resolution, proactivity.

### Key Prep Areas
- Linux: strace, ltrace, /proc, /sys, perf, eBPF fundamentals, systemd.
- Networking: BGP, VIPs, load balancing (L4/L7), CDN, DNS, NTP.
- Databases: MySQL, Memcached, Cassandra — replication, sharding, caching patterns.
- Distributed systems: Hadoop, HDFS, TAO (graph), Scuba (real-time analytics).
- Meta-specific: Maille (container orchestration), Tupperware (container management). But open-source alternatives are OK.

### Study Resources
- _Designing Data-Intensive Applications_ (Martin Kleppmann).
- Meta Engineering Blog.
- LeetCode, especially concurrency (print in order, dining philosophers, etc.).

---

## 3. Amazon (Systems Engineer / DevOps Engineer)

### Role Types
- **Systems Engineer (SysDE)**: Focused on infrastructure, tooling, automation. Less coding-heavy than SDE.
- **DevOps Engineer**: CI/CD, infrastructure as code, containerization, monitoring.
- **SRE** (AWS): Reliability of AWS services — DynamoDB, S3, EC2.
- **Cloud Support Engineer**: Customer-facing troubleshooting.

### SRE vs DevOps vs Platform Engineer at Amazon
Amazon calls SRE "SRE" and DevOps "DevOps Engineer." SysDE is closest to Platform Engineer. All use Amazon's Leadership Principles heavily.

### Interview Rounds
1. **Phone Screen** (1 hour): Coding (LeetCode Easy-Medium), behavioral (2-3 LP questions).
2. **Onsite (5 rounds)**:
   - **Coding** (2 rounds): Arrays, strings, trees, graphs. No LeetCode Hard typically.
   - **System Design** (1 round): Design a scalable service (e.g., parking lot, elevator, URL shortener, Amazon cart).
   - **Behavioral** (2 rounds): STAR format. 3-5 Leadership Principles per round.
   - **Bar Raiser** (1 round): Senior interviewer — higher bar, LP deep dive.
3. **LP Deep Dive**: Every single answer must demonstrate an Amazon Leadership Principle.

### Key Prep Areas
- AWS: EC2, S3, VPC, IAM, Lambda, ECS, EKS, CloudFormation, CDK.
- Linux: system administration, shell scripting, networking.
- CI/CD: CodePipeline, Jenkins, GitHub Actions.
- IaC: Terraform, CloudFormation, CDK.
- Scaling: auto scaling groups, ELB, Route53, CloudFront.
- Databases: RDS, DynamoDB, ElastiCache — read replicas, partitioning, DAX.

### Amazon Leadership Principles (priority)
1. Customer Obsession
2. Ownership
3. Invent and Simplify
4. Are Right, A Lot
5. Learn and Be Curious
6. Hire and Develop the Best
7. Insist on the Highest Standards
8. Think Big
9. Bias for Action
10. Frugality
11. Earn Trust
12. Dive Deep
13. Have Backbone; Disagree and Commit
14. Deliver Results
15. Strive to be Earth's Best Employer
16. Success and Scale Bring Broad Responsibility

### Study Resources
- _Amazon Leadership Principles_ (memorize with STAR stories).
- _AWS Well-Architected Framework_.
- LeetCode (Easy-Medium).

---

## 4. Netflix (Cloud Engineer)

### Role Types
- **Cloud Engineer**: Manage AWS infrastructure, build internal tools, chaos engineering, content delivery.
- **SRE** (less common): Netflix has a unique "full site reliability" culture — devs own reliability.
- **Platform Engineer**: Internal developer platform, tooling, Spinnaker, Titus.

### SRE vs DevOps vs Platform Engineer at Netflix
Netflix has a unique culture. "Freedom and Responsibility." SRE principles are distributed to every engineering team. Cloud Engineers build and maintain the platform.

### Interview Rounds
1. **Recruiter Screen** (30 min): Culture fit, overview.
2. **Technical Phone Screen** (1 hour): System design or architecture discussion.
3. **Onsite (4-5 rounds)**:
   - **System Design** (2 rounds): Design systems at Netflix scale (e.g., CDN, recommendation system, video encoding pipeline).
   - **Coding** (1-2 rounds): LeetCode Medium. Java, Python, or Go.
   - **Cultural Fit** (embedded): Freedom and Responsibility debate, candor, context vs control.
   - **Operations/Chaos** (1 round): Chaos Monkey, Simian Army, incident response, fault tolerance.
4. **No behavioral STAR format** — Netflix prefers direct, candid conversation.

### Key Prep Areas
- AWS: at massive scale — multi-region, multi-AZ, S3, DynamoDB, EC2, VPC, CloudFront.
- Chaos Engineering: Chaos Monkey, Chaos Kong, fault injection, blast radius.
- CDN: Open Connect Appliance (OCA), cache hierarchy, streaming protocols (HLS, DASH).
- Microservices: gRPC, REST, circuit breakers (Hystrix), bulkheads.
- Containers: Titus (container platform), Docker, Kubernetes.
- CI/CD: Spinnaker (canary analysis, blue-green deployment).

### Netflix Culture
- "Freedom and Responsibility" — no vacation policy, no expense policy.
- "Context, not Control."
- "Highly Aligned, Loosely Coupled."
- "Stunning Colleagues."
- "Informed Captains."

### Study Resources
- _Netflix Tech Blog_ (medium.com/netflix-techblog).
- _Chaos Engineering_ (Nora Jones & Casey Rosenthal).
- _Building Microservices_ (Sam Newman).

---

## 5. Microsoft (Azure DevOps Engineer)

### Role Types
- **Azure DevOps Engineer Expert**: Design and implement DevOps strategies — CI/CD, IaC, containers, monitoring.
- **SRE at Microsoft**: Azure SRE, Office 365 SRE, Bing SRE.
- **Cloud Solution Architect**: Customer-facing, Azure adoption.

### SRE vs DevOps vs Platform Engineer at Microsoft
Microsoft uses standard titles. Azure DevOps is the certification path. SRE exists inside Azure and product teams.

### Interview Rounds
1. **Recruiter Screen** (30 min).
2. **Technical Screen** (45-60 min): System design or coding. LeetCode Medium.
3. **Onsite (4-5 rounds)**:
   - **Design** (1-2 rounds): Design Azure-scale services (e.g., blob storage, VM orchestration, CI/CD pipeline).
   - **Coding/Algorithm** (1-2 rounds): LeetCode Medium. C#, Python, TypeScript, or Go.
   - **Behavioral** (1 round): STAR format. Growth mindset, collaboration, customer focus.
   - **Azure-specific** (1 round, if applicable): Azure DevOps, ARM/Bicep, Azure Kubernetes Service, Azure Pipelines.

### Key Prep Areas
- Azure: Azure DevOps (Boards, Repos, Pipelines, Test Plans, Artifacts), ARM, Bicep, Terraform on Azure.
- Azure Kubernetes Service (AKS): cluster management, node pools, network policies, Azure CNI.
- CI/CD: Azure Pipelines, GitHub Actions, multi-stage YAML, release gates, approval flows.
- IaC: ARM templates, Bicep, Terraform, Pulumi.
- Monitoring: Azure Monitor, Log Analytics, Application Insights.
- Containers: ACR (Container Registry), Docker, Helm.
- Security: Managed identities, Key Vault, RBAC, Policy, Blueprints.

### Microsoft Culture
- "Growth Mindset" — learn from failures, seek feedback.
- "Customer Obsession" (but less intense than Amazon).
- "Diversity & Inclusion."
- "One Microsoft."

### Study Resources
- _Microsoft Learn_ — Azure DevOps, AZ-400, AZ-104.
- _Azure Architecture Center_.
- LeetCode (Medium preferred).

---

## 6. GitHub

### Role Types
- **DevOps Engineer / Site Reliability Engineer**: Keep GitHub online — 100M+ repositories, 50M+ developers.
- **Platform Engineer**: Build GitHub Actions runners, GitHub Packages infrastructure, Codespaces backend.
- **Security Engineer**: Supply chain security, Dependabot, secret scanning.

### Interview Rounds
1. **Recruiter Screen** (30 min).
2. **Technical Screen** (60 min): Coding or system design. GitHub uses pairing.
3. **Onsite (4-5 rounds)**:
   - **System Design**: Design a GitHub feature (e.g., Actions runner, Package registry, Issue search).
   - **Coding**: LeetCode Medium. Ruby, Go, or TypeScript.
   - **Debugging/Incident**: Real incident analysis — how would you debug a production issue?
   - **Git/Version Control Deep Dive**: Git internals — objects, refs, pack files, merge strategies.
   - **Behavioral**: Collaboration, open source ethos, remote work, async communication.

### Key Prep Areas
- Git internals: .git directory structure, blob/tree/commit objects, pack files, reflog, bisect.
- GitHub API: REST, GraphQL, webhooks, deployments API, Checks API.
- Actions: custom actions, composite actions, self-hosted runners, scaling runners.
- Containers: GitHub Container Registry, Docker, Kubernetes.
- Database: MySQL, Redis, Elasticsearch — schemas, indexing, query optimization.
- Scaling: 100M+ repositories, 50M+ developers — global replication, caching, sharding.

### GitHub Culture
- "Optimize for the Reader."
- "Ship to Learn."
- "Default to Open."
- "Remote-first."
- "Communicate with Clarity and Empathy."

### Study Resources
- _Git Pro Book_.
- GitHub Engineering Blog.
- GitHub Changelog.

---

## 7. GitLab

### Role Types
- **DevOps Engineer**: GitLab.com infrastructure — multi-region Kubernetes, 50TB+ data.
- **Backend Engineer, Govern/Verify/Release**: Feature development, CI/CD engine.
- **Site Reliability Engineer**: Keep GitLab.com operational — incident response, capacity planning.
- **Infrastructure Engineer**: Automation, Terraform, Ansible, Chef migration.

### Interview Rounds
1. **Recruiter Screen** (30 min): Overview, GitLab values.
2. **Technical Assessment** (take-home or live coding).
3. **Onsite (4-5 rounds)**:
   - **Coding**: Ruby on Rails, Go, or Python. Database modeling, API design.
   - **GitLab CI/CD Deep Dive**: How would you implement a new CI feature? Pipeline architecture, caching, artifacts.
   - **System Design**: Design GitLab.com at scale (multi-region, Geo, replication).
   - **Debugging Production**: RCA of a real GitLab.com incident (postgres saturation, Redis failover, K8s node failure).
   - **Values Alignment**: GitLab Handbook, CREDIT values (Collaboration, Results, Efficiency, Diversity, Iteration, Transparency).

### Key Prep Areas
- GitLab CI: .gitlab-ci.yml structure, stages, rules, needs, artifacts, cache, multi-project pipelines.
- GitLab architecture: Rails monolith (now decomposing), Sidekiq, Redis, PostgreSQL, Gitaly, Go services.
- GitLab Geo: multi-region, read-only replicas, disaster recovery, replication lag.
- Kubernetes: GitLab Agent for Kubernetes, Auto DevOps, cluster management.
- Observability: Prometheus, Grafana, Thanos, ELK, Loki, Jaeger.
- Database: PostgreSQL — partitioning, vacuum, connection pooling (PgBouncer), query optimization.

### GitLab Culture (CREDIT)
- **Collaboration** — Everyone can contribute.
- **Results** — Measure outcomes, not output.
- **Efficiency** — Bias for action, lean.
- **Diversity** — Diverse teams build better products.
- **Iteration** — Minimum viable change.
- **Transparency** — Public handbook, public issue tracker.

### Study Resources
- _GitLab Handbook_ (handbook.gitlab.com).
- GitLab Engineering Blog.
- GitLab.com Runbooks.

---

## 8. HashiCorp

### Role Types
- **Solutions Engineer**: Customer-facing — demonstrate Terraform, Vault, Consul, Nomad.
- **Software Engineer (Infrastructure)**: Build the products themselves — Terraform core, Vault secrets engine, Consul mesh.
- **Site Reliability Engineer**: Keep HashiCorp Cloud Platform (HCP) operational.
- **Developer Advocate**: Community, content, speaking.

### Interview Rounds
1. **Recruiter Screen** (30 min): Culture, HashiCorp principles.
2. **Technical Screen** (60 min): System design or tooling-specific (Terraform provider, Vault policy, Consul service mesh).
3. **Onsite (4-5 rounds)**:
   - **Go/Python Coding**: LeetCode Medium. HashiCorp products are written in Go.
   - **System Design**: Design a secrets rotation system, multi-cloud networking with Consul, or Terraform state management at scale.
   - **Tool-Specific Mastery**: Terraform module design, Vault HA, Consul service mesh, Nomad scheduling.
   - **Behavioral/Principles**: HashiCorp principles — autonomy, transparency, collaboration, humility.
   - **Debugging**: Debug a Terraform provider bug or Vault performance degradation.

### Key Prep Areas
- Terraform: HCL, providers, modules, state locking, `terraform plan`/`apply` workflow, sentinel policy as code.
- Vault: secret engines, auth methods, dynamic secrets, transit encryption, replication, PKI.
- Consul: service discovery, service mesh (Connect), intentions, KV store, gossip protocol.
- Nomad: job scheduling, task drivers, client/server architecture, namespaces.
- Packer: image building (AMI, GCE, Docker), HCL2 templates, post-processors.
- Waypoint: application deployment platform, build/deploy/release workflow.
- Boundary: identity-based access management for infrastructure.

### HashiCorp Principles
- **Autonomy** — "We don't have a heavy process. We trust each other."
- **Transparency** — "Open source means open development."
- **Collaboration** — "Better together."
- **Humility** — "No ego."

### Study Resources
- _Terraform: Up & Running_ (Yevgeniy Brikman).
- HashiCorp Learn (learn.hashicorp.com).
- HashiCorp GitHub — source code, RFCs.

---

## 9. Datadog

### Role Types
- **Site Reliability Engineer**: Datadog's own infrastructure — 500+ services, petabyte-scale telemetry.
- **Software Engineer (Agent, Backend, Platform)**: Build monitoring products — Agent, backend pipeline, query engine.
- **Solutions Engineer**: Customer-facing — help adopt Datadog, observability best practices.
- **Support Engineer**: Deep troubleshooting, product expertise.

### Interview Rounds
1. **Recruiter Screen** (30 min): Overview, Datadog tech stack.
2. **Technical Screen** (60 min): Coding or system design.
3. **Onsite (4-5 rounds)**:
   - **Coding**: Python, Go, or Java. LeetCode Medium-Hard.
   - **System Design**: Design a monitoring/observability system (e.g., distributed tracing, log aggregation, custom metric ingestion).
   - **Debugging/Incident**: How would you debug high latency, dropped metrics, or agent memory growth?
   - **Observability Deep Dive**: PromQL, Log queries, APM, RUM, synthetic monitoring.
   - **Behavioral**: Collaboration, customer empathy, dealing with scale.

### Key Prep Areas
- Datadog Agent: metrics collection, integrations (100s), check scheduling, dogstatsd.
- Metrics: custom metrics, histograms, distributions, tag-based aggregation.
- Logs: log collection, parsing, exclusion filters, indexing, archives.
- APM: distributed tracing, trace propagation (DD-Trace, B3, W3C), sampling, profiling.
- Infrastructure: Datadog operates on AWS and GCP — EC2, S3, K8s, serverless.
- Observability patterns: USE (Utilization, Saturation, Errors), RED (Rate, Errors, Duration) method.

### Datadog Culture
- "Customer Centric."
- "High Agency."
- "Ownership — do whatever it takes."
- "Move fast but don't break things."

### Study Resources
- _Datadog Documentation_ (docs.datadoghq.com).
- _The Art of Monitoring_ (James Turnbull).
- Davis (Datadog's AI) — ML for anomaly detection.

---

## 10. New Relic

### Role Types
- **Site Reliability Engineer**: Keep New Relic's multi-tenant platform operational (US, EU, FedRAMP).
- **Software Engineer (Platform, Data Pipeline)**: Build telemetry ingestion, storage, query engine.
- **Developer Success Engineer**: Customer-facing observability expertise.
- **Support Engineer**: Troubleshoot, reproduce, escalate.

### Interview Rounds
1. **Recruiter Screen** (30 min).
2. **Technical Screen** (60 min): Coding or system design.
3. **Onsite (4-5 rounds)**:
   - **Coding**: Java, Go, or Python. LeetCode Medium.
   - **System Design**: Design a telemetry ingestion pipeline or NRQL query engine.
   - **Observability Design**: How would you instrument a microservice application? NRQL deep dive.
   - **Debugging**: Root cause analysis of a production issue.
   - **Behavioral**: Customer focus, collaboration, continuous learning.

### Key Prep Areas
- New Relic One: NRQL (New Relic Query Language), dashboards, alerts.
- Telemetry ingestion: metrics, events, logs, traces (MELT) — normalization, sampling, aggregation.
- Distributed tracing: span/trace model, W3C trace context, tail-based sampling.
- Infrastructure: 1000s of hosts, 100+ services, multi-region, Kubernetes.
- Alerts: NRQL alert conditions, anomaly detection, incident intelligence.

### New Relic Culture
- "Radical Transparency."
- "Customer Obsessed."
- "Intellectual Honesty."
- "Go Beyond."

### Study Resources
- _New Relic Documentation_ (docs.newrelic.com).
- New Relic Blog.
- New Relic Explorer — NRQL tutorials.

---

## 11. Splunk

### Role Types
- **Splunk SRE**: Keep Splunk Cloud operational — petabyte-scale data, multi-tenant, SOC2.
- **DevOps Engineer**: Build and maintain Splunk's internal infrastructure and CI/CD.
- **Solutions Engineer**: Customer-facing — data onboarding, dashboards, alerts.
- **Platform Engineer**: Splunk's platform development, Kubernetes-based deployments.

### Interview Rounds
1. **Recruiter Screen** (30 min).
2. **Technical Screen** (60 min): SPL queries, system design.
3. **Onsite (4-5 rounds)**:
   - **SPL (Search Processing Language) Deep Dive**: Write complex searches, data models, macros, lookups.
   - **System Design**: Design a log aggregation system or Splunk indexer cluster.
   - **Coding**: Python, Java, or Go. LeetCode Medium.
   - **Infrastructure**: Kubernetes, Terraform, CI/CD pipelines.
   - **Behavioral**: Customer empathy, data-driven, collaboration.

### Key Prep Areas
- Splunk architecture: indexers, search heads, forwarders, heavy forwarders, deployment server.
- SPL: search, eval, stats, transaction, eventtypes, tags, lookups, subsearches.
- Data onboarding: inputs.conf, props.conf, transforms.conf — source types, line breaking, timestamp extraction.
- Splunk Cloud: AWS, multi-tenant, index management, data retention.
- Alerting: scheduled searches, correlation searches, notable events.
- Knowledge objects: data models, pivot, dashboards, report acceleration.
- Infrastructure: Splunk on Kubernetes, operator, HEC (HTTP Event Collector).

### Splunk Culture
- "Data-driven."
- "Innovation from the application up."
- "Customer-first."
- "One Splunk."

### Study Resources
- _Splunk Documentation_ (docs.splunk.com).
- _Splunk Certified Power User / Admin_.
- Splunk Answers community.

---

## 12. PagerDuty

### Role Types
- **Site Reliability Engineer**: Keep PagerDuty operational — incident lifecycle, on-call, real-time operations.
- **Platform Engineer**: Build the developer platform, internal tools, service catalog.
- **Backend Engineer**: PagerDuty's core — event processing, alerting, scheduling, orchestration.
- **Support Engineer**: Customer-facing troubleshooting.

### Interview Rounds
1. **Recruiter Screen** (30 min): Overview, PagerDuty values.
2. **Technical Screen** (60 min): Coding or system design.
3. **Onsite (4-5 rounds)**:
   - **Coding**: Ruby, Go, or Kotlin. LeetCode Medium.
   - **System Design**: Design an incident management system, on-call scheduling, event correlation engine.
   - **Incident Response Simulation**: Walk through a real incident — triage, escalation, communication, postmortem.
   - **Service Catalog & CMDB**: Design a service dependency graph, ownership, readiness.
   - **Behavioral**: Ownership, calm under pressure, blameless culture, continuous improvement.

### Key Prep Areas
- Incident management: severity levels, escalation policies, notification rules, suppression.
- Event management: deduplication, correlation, grouping, auto-resolution.
- On-call scheduling: rotations (daily, weekly, custom), overrides, layered schedules, handoffs.
- Service catalog: service dependencies, ownership, runbooks, readiness score.
- Automation: PagerDuty Actions, webhooks, API, Terraform provider.
- AIOps: Event intelligence, noise reduction, predictive alerts.
- Reliability: PagerDuty's own multi-region, multi-cloud architecture.

### PagerDuty Culture
- "Customer First, People Always."
- "Assume Good Intent."
- "Bias for Action."
- "Be Honest and Direct."
- "Cultivate Belonging."

### Study Resources
- _PagerDuty Blog_.
- _Incident Management Documentation_ (docs.pagerduty.com).
- _The Incident Management Handbook_ (PagerDuty).

---

## Cross-Company Comparison Table

| Company | Primary Role(s) | Coding Difficulty | System Design | Behavioral Focus | Unique Element |
|---------|----------------|-------------------|---------------|------------------|----------------|
| Google | SRE | Hard | Hard | Googleyness | SRE book, toil reduction |
| Meta | Production Engineer | Medium-Hard | Medium | Ownership | Debugging production systems |
| Amazon | SysDE / DevOps | Easy-Medium | Medium | Leadership Principles | STAR format, 16 LPs |
| Netflix | Cloud Engineer | Medium | Hard | Candor, Freedom & Resp | Chaos engineering |
| Microsoft | Azure DevOps / SRE | Medium | Medium | Growth mindset | Azure-specific, certifications |
| GitHub | DevOps / SRE | Medium | Medium | Collaboration | Git internals, remote-first |
| GitLab | DevOps / SRE | Medium | Medium | CREDIT values | Handbook, CI/CD deep dive |
| HashiCorp | Solutions / SWE | Medium | Medium-Hard | Humility, transparency | Tool-specific mastery |
| Datadog | SRE / SWE | Medium-Hard | Hard | High agency | Observability design |
| New Relic | SRE / SWE | Medium | Hard | Radical transparency | NRQL, telemetry pipeline |
| Splunk | SRE / DevOps | Medium | Medium | Data-driven | SPL, indexer architecture |
| PagerDuty | SRE / Platform | Medium | Medium | Blameless culture | Incident response simulation |

---

## General Preparation Timeline

### 4-8 weeks before
- Pick 2-3 target companies. Focus.
- Read company engineering blogs.
- Master one IaC tool (Terraform) and one config management tool (Ansible).
- Practice 2 LeetCode problems/day.

### 2-4 weeks before
- System design: 1 design/day. Whiteboard.
- Behavioral stories: write 8-10 STAR stories.
- Tool-specific: Terraform modules, Kubernetes manifests, Dockerfiles.
- Mock interviews with peers.

### 1 week before
- Review company-specific format.
- Prepare questions for the interviewer.
- Sleep, nutrition, exercise.
- Technical deep-dive into your own projects.

### Day of
- Arrive early (virtual or in-person).
- Have water, comfortable clothes.
- STAR template on a sticky note.
- "Tell me about yourself" — 2 minute version.

---

## Common DevOps Interview Questions (by Category)

### Linux
- What happens when you type `ls` in a terminal?
- Explain process states, zombie processes, orphan processes.
- How does `OOM killer` work? How do you tune it?
- Explain cgroups v1 vs v2.
- Namespaces: pid, network, mount, user, uts.
- Systemd: units, targets, journald, socket activation.
- Filesystem: inodes, hard links, symlinks, mount namespaces.
- Performance: `top`, `htop`, `perf`, `strace`, `lsof`, `ss`, `iptables`.

### Networking
- OSI model. TCP vs UDP. TCP handshake.
- HTTP/1.1 vs HTTP/2 vs HTTP/3 (QUIC).
- DNS resolution, recursion, caching, TTL, anycast.
- Load balancing: L4 vs L7, algorithms (RR, least connections, consistent hashing).
- TLS handshake, cipher suites, mutual TLS.
- BGP basics: ASN, peering, route advertisement.
- Kubernetes networking: CNI, Service mesh, kube-proxy modes (IPTables, IPVS, eBPF).

### Kubernetes
- Architecture: etcd, API server, scheduler, controller manager, kubelet, kube-proxy.
- Pod lifecycle: InitContainers, postStart, preStop, probes (liveness, readiness, startup).
- Scheduling: node selectors, affinity/anti-affinity, taints, tolerations, scheduler plugins.
- Networking: Pod networking, Service types (ClusterIP, NodePort, LoadBalancer), Ingress, Gateway API.
- Storage: PersistentVolume, PersistentVolumeClaim, StorageClass, CSI.
- Security: RBAC, PodSecurityStandard, NetworkPolicy, ServiceAccount, Secrets.

### CI/CD
- Difference between CI and CD. Give examples.
- How does `git clone` work? How does `git merge` work? Merge vs rebase.
- Pipeline stages: build, test, package, deploy. Multi-stage Dockerfiles.
- Canary vs blue-green vs rolling deployment. When to use which?
- Artifact management: Nexus, Artifactory, S3, ECR.
- GitOps: ArgoCD vs Flux. Push vs pull model.

### Infrastructure as Code
- Terraform: state management, remote state, state locking, modules, workspaces, `terraform plan` vs `apply`.
- Terraform vs Pulumi vs CloudFormation vs CDK.
- Configuration drift: how to detect and remediate?
- Immutable vs mutable infrastructure.
- Ansible vs Terraform — when to use which?
- Secret management: Vault, SOPS, Sealed Secrets, AWS Secrets Manager.

### Monitoring & Observability
- Metrics vs logs vs traces. When to use each?
- Prometheus: pull model, service discovery, PromQL, recording rules, alerting rules, Alertmanager.
- Grafana: dashboards, provisioning, alerting, Loki, Tempo.
- Distributed tracing: spans, traces, context propagation, sampling (head-based vs tail-based).
- ELK stack: Elasticsearch, Logstash, Kibana, Beats. Elastic Clusters.
- SLO: SLI definition, SLO target, error budget, burn rate.

### Distributed Systems
- CAP theorem. PACELC.
- Consistency models: strong, eventual, causal, read-your-writes.
- Consensus: Paxos, Raft, Zab.
- Replication: single-leader, multi-leader, leaderless.
- Sharding: key-based, hash-based, range-based, re-balancing.
- Caching: CDN, in-memory (Redis, Memcached), write-through, write-behind, cache-aside.

### Security
- Shift-left security: SAST, DAST, SCA, container scanning, secret scanning.
- Supply chain security: SLSA, in-toto, sigstore, cosign, SBOM.
- Zero Trust: identity-aware proxy, mTLS, SPIFFE, SPIRE.
- Secrets rotation: Vault, automatic rotation, dynamic secrets.
- Policy as Code: Open Policy Agent (OPA), Sentinel, Kyverno.

---

_End of ACADEMY_INTERVIEW_GUIDE.md_