# Company Interview Guide — Cloud Roles

## Table of Contents

1. [Amazon Web Services](#1-amazon-web-services)
2. [Microsoft Azure](#2-microsoft-azure)
3. [Google Cloud Platform](#3-google-cloud-platform)
4. [Cloudflare](#4-cloudflare)
5. [Netflix](#5-netflix)
6. [HashiCorp](#6-hashicorp)
7. [Oracle Cloud Infrastructure](#7-oracle-cloud-infrastructure)
8. [Datadog](#8-datadog)
9. [Cross-Company Cloud Interview Tips](#9-cross-company-cloud-interview-tips)

---

## 1. Amazon Web Services

### Company Overview
AWS is the leading cloud provider (~32% market share). Interview process emphasizes Leadership Principles, deep technical knowledge, and customer obsession.

### Solutions Architect Certification Path

| Step | Certification | Preparation Time | Notes |
|------|--------------|------------------|-------|
| 1 | AWS Cloud Practitioner | 2-4 weeks | Foundational, optional but recommended |
| 2 | AWS Solutions Architect - Associate | 6-8 weeks | Required entry level certification |
| 3 | AWS Solutions Architect - Professional | 8-12 weeks | Advanced, multi-account, hybrid scenarios |
| 4 | Specialty (Security / Networking / Data) | 6-10 weeks | Domain-specific expertise |

### Leadership Principles (LP)

**Most Frequently Tested in Interviews:**

| LP | Typical Question Angle |
|----|----------------------|
| Customer Obsession | Describe a time you went above and beyond for a customer |
| Ownership | Tell me about a time you owned a project beyond your scope |
| Invent and Simplify | When did you simplify a complex system |
| Are Right, A Lot | When were you right and others disagreed |
| Bias for Action | Describe a time you took action with incomplete information |
| Deliver Results | Tell me about a project that seemed impossible but you delivered |
| Dive Deep | How did you find the root cause of a complex issue |
| Have Backbone; Disagree and Commit | When did you disagree with your manager |

**Response Framework (STAR + LP):**
- **S**ituation: Set the context (project, timeline, team)
- **T**ask: Your specific responsibility
- **A**ction: The actions you took (what YOU did, not the team)
- **R**esult: Quantified outcome (%, $, time saved, customer impact)
- **LP Link**: Explicitly name the Leadership Principle demonstrated

### System Design at AWS

**Common Design Problems:**
- Design YouTube/Netflix (video streaming platform)
- Design Amazon shopping cart
- Design a URL shortener at scale
- Design a distributed key-value store
- Design a real-time chat system
- Design a notification service

**AWS-Native Solution Architecture:**
- Compute: EC2 Auto Scaling, Application Load Balancer, ECS/EKS Fargate
- Storage: S3 + CloudFront for static content, EBS/EFS for stateful
- Database: RDS Multi-AZ, DynamoDB DAX, ElastiCache, Aurora
- Messaging: SQS, SNS, EventBridge, Kinesis
- Monitoring: CloudWatch, X-Ray, CloudTrail
- Security: IAM roles, KMS, WAF, Shield

**Key Architecture Principles:**
- Design for failure (everything fails all the time)
- Loose coupling (async messaging, event-driven)
- Implement elasticity (scale up/down automatically)
- Think parallel (distribute load, shard data)
- Secure by design (least privilege, defense in depth)

### Interview Preparation Timeline (12 weeks)

| Week | Focus |
|------|-------|
| 1-2 | AWS Fundamentals: core services, Well-Architected Framework |
| 3-4 | Cert Preparation: Solutions Architect Associate |
| 5-6 | Deep Dive: compute, storage, database, networking |
| 7-8 | System Design: whiteboard practice, AWS-native patterns |
| 9-10 | Leadership Principles: prepare 15+ STAR stories covering 10+ LPs |
| 11 | Mock interviews: full loops with peers, recording |
| 12 | Final review, relaxation, logistics |

---

## 2. Microsoft Azure

### Company Overview
Azure is #2 cloud provider (~23% market share). Strong in enterprise, hybrid cloud, and Microsoft ecosystem integration. Interview process focuses on growth mindset, customer obsession, and technical depth.

### Microsoft-Specific Interview Culture

**Microsoft Competencies Tested:**
- **Growth Mindset**: Open to learning, feedback, and new approaches
- **Customer Obsession**: Understand and anticipate customer needs
- **Diversity and Inclusion**: Respect different perspectives, create inclusive environments
- **Collaboration**: Work across teams, one Microsoft approach
- **Making a Difference**: Impact beyond your immediate role
- **Technical Excellence**: Deep knowledge with practical application
- **Innovation**: New ideas that create value

**Microsoft Interview Philosophy:**
- "Hire people who are smarter than you"
- "Growth mindset over fixed mindset"
- "Preference for depth over breadth"
- "Technical aptitude and problem-solving process"

### Hybrid Cloud Focus

**Core Hybrid Concepts:**
| Concept | Azure Service | Description |
|---------|---------------|-------------|
| On-premises extension | Azure Arc | Manage on-prem resources with Azure control plane |
| Hybrid identity | Azure AD Connect | Sync on-prem AD with Azure AD |
| Hybrid networking | ExpressRoute | Dedicated private connection to Azure |
| Hybrid storage | Azure Stack HCI | Hyperconverged infrastructure on-prem |
| Hybrid management | Azure Policy | Governance across on-prem and cloud |
| Disaster recovery | Azure Site Recovery | Replicate VMs to Azure for DR |

**Hybrid Scenario Questions:**
- How would you extend an on-premises Active Directory to Azure?
- Design a hybrid networking architecture for a financial services company
- How do you handle data residency requirements with a hybrid deployment?
- Design a disaster recovery plan using Azure Site Recovery
- How would you migrate a SQL Server database with minimal downtime?

### Enterprise Integration

**Key Services for Enterprise:**
- **Azure AD**: Identity, Conditional Access, Identity Protection, Privileged Identity Management
- **Azure Policy**: Compliance, governance, resource tagging, deny policies
- **Management Groups**: Organization hierarchy, policy inheritance
- **Azure Blueprints**: Environment setup, role assignments, policy assignments
- **Cost Management**: Budgets, alerts, recommendations, chargeback

### Interview Rounds Deep Dive

| Round | Focus | Preparation |
|-------|-------|-------------|
| Recruiter | Background, compensation, availability | Know your story, salary expectations |
| Technical Screen | Azure services, architecture basics | Review AZ-900/AZ-104 material |
| ASM (Appropriate Screening Method) | Decision-making, collaboration | Prepare 5+ behavioral stories |
| Loop — System Design | Enterprise architecture, hybrid scenarios | Practice whiteboarding |
| Loop — Technical Deep Dive | Previous projects, IaC, automation | Deep knowledge of 2-3 projects |
| Loop — Behavioral | Microsoft competencies | STAR stories aligned to competencies |
| Loop — Cross-team | Stakeholder management | Collaboration examples |

---

## 3. Google Cloud Platform

### Company Overview
GCP is #3 cloud provider (~11% market share). Known for data/AI strength, Kubernetes leadership, and SRE culture. Interview emphasizes general cognitive ability, Googleyness, and scalability thinking.

### Google-Specific Interview Culture

**Googleyness Dimensions:**
- **Comfort with Ambiguity**: Work without clear direction, figure it out
- **Bias to Action**: Move fast, prototype, iterate
- **Intellectual Humility**: Willing to be wrong, learn from others
- **Collaboration**: Team player, inclusive
- **Passion**: Enthusiasm for technology, impact
- **General Cognitive Ability**: Learn quickly, solve novel problems
- **Leadership**: Influence without authority, take initiative

**Google Interview Philosophy:**
- "Hire people who are smarter than you"
- "Focus on how you think, not what you know"
- "Scalability and systems thinking are critical"
- "Data-driven decision making"

### SRE Culture at Google

**Core SRE Principles (from Google SRE books):**

| Principle | Description |
|-----------|-------------|
| SLIs/SLOs/SLAs | Define measurable reliability targets |
| Error Budgets | Balance reliability with feature velocity |
| Toil Reduction | Automate manual, repetitive work |
| Monitoring | Define symptoms, not causes |
| Incident Response | Blameless postmortems, incident command system |
| Capacity Planning | Demand forecasting, load testing |
| Release Engineering | Safe, gradual rollouts, canary deployments |

**SRE Interview Topics:**
- Design a monitoring system for a distributed application
- How do you define SLOs for a new service?
- Your service is exceeding error budget — what do you do?
- Design an incident response process
- How would you automate incident remediation?
- Capacity planning for a viral product

### Scalability Focus

**GCP-Native Scalability Patterns:**

| Pattern | GCP Service | When to Use |
|---------|-------------|-------------|
| Horizontal scaling | Managed Instance Groups | Stateless workloads |
| Serverless | Cloud Run, Cloud Functions | Event-driven, bursty traffic |
| Autoscaling | GKE Autopilot | Containerized microservices |
| Data processing | BigQuery, Dataflow | Analytical workloads |
| Caching | Memorystore (Redis/Memcached) | Read-heavy workloads |
| CDN | Cloud CDN | Global content delivery |
| Load balancing | Cloud Load Balancing | Multi-region traffic distribution |

**Scalability Scenario Questions:**
- Design a system that scales from 100 to 10M users
- How would you handle a sudden traffic spike?
- Design a globally distributed database
- How do you ensure eventual consistency at scale?
- Resource optimization for a data processing pipeline

### Interview Rounds Deep Dive

| Round | Focus | Preparation |
|-------|-------|-------------|
| Recruiter | Background, role alignment | Understand GCP role levels |
| Phone Screen | Algorithms, data structures | LeetCode medium-hard |
| Onsite — System Design | Large-scale distributed systems | DDIA book, YouTube design |
| Onsite — Googleyness | Ambiguity, leadership, collaboration | 8-10 behavioral stories |
| Onsite — Technical Deep Dive | Past projects, design decisions | Know your resume depth |
| Onsite — Coding | Cloud automation scripts, API design | Python/Go, bash |
| Onsite (SRE) | Troubleshooting, incident response | Hands-on debugging scenarios |

---

## 4. Cloudflare

### Company Overview
Cloudflare operates one of the world's largest edge networks. Interview process focuses on network performance, security, and edge computing. Strong emphasis on transparency and reliability.

### Cloud-First Company Culture

**Cloudflare Values:**
- **Transparency**: Default to open, share information
- **Reliability**: Build systems that can withstand anything
- **Performance**: Every millisecond matters
- **Security**: Protect the Internet
- **Developer Experience**: Make complex things simple

**Interview Themes:**
- "How would you protect a website from DDoS?"
- "Design a global CDN"
- "Explain how DNS works"
- "How does anycast routing work?"
- "What happens when you type a URL in a browser?"

### Performance Focus

**Key Performance Concepts:**

| Concept | Importance | Details |
|---------|-----------|---------|
| Anycast | Critical | Single IP, multiple locations, BGP routing |
| TCP optimizations | High | Fast Open, BBR congestion control, TFO |
| HTTP/2, HTTP/3 | High | Multiplexing, header compression, QUIC |
| TLS/SSL | High | Handshake optimization, OCSP stapling, 0-RTT |
| Caching | Critical | Tiered Cache, Argo Smart Routing |
| Image optimization | Medium | Polish, Mirage, WebP conversion |
| Brotli compression | Medium | Better compression than gzip |

**Performance Scenario Questions:**
- A website loads slowly from Asia — diagnose and fix
- Design a caching strategy for a global e-commerce site
- How would you reduce TTFB (Time to First Byte)?
- Compare CDN architectures: pull vs push zones
- Design for WebSocket at global scale

### Platform Capabilities

**Cloudflare Product Stack:**

| Layer | Product | Use Case |
|-------|---------|----------|
| DNS | Cloudflare DNS | Fastest DNS resolver, DNSSEC, analytics |
| CDN | Cloudflare CDN | Global content delivery, cache rules |
| Security | WAF, DDoS, Bot Management | Application security |
| Zero Trust | Cloudflare Access, Gateway | Secure access without VPN |
| Edge Compute | Workers, Durable Objects, R2 | Serverless at the edge |
| Network | Magic Transit, Spectrum, Argo | Network optimization |
| Developer | Pages, Queues, D1 | Full-stack edge applications |

---

## 5. Netflix

### Company Overview
Netflix pioneered cloud-first architecture, running nearly entirely on AWS. Interview process is unique: no whiteboard coding, focus on real-world engineering.

### Cloud-First Culture

**Netflix Engineering Culture:**
- **Freedom and Responsibility**: Engineers own their decisions
- **Context over Control**: Share information, trust teams
- **High Alignment, High Autonomy**: Align on goals, freedom to execute
- **Highly Aligned, Loosely Coupled**: Teams operate independently
- **Pragmatic Innovation**: Try new things, but ship value

**Interview Philosophy:**
- "We hire adults and give them freedom"
- "No coding on whiteboards in interviews"
- "Focus on real engineering scenarios"
- "Culture is our #1 priority"

### Interview Process

| Round | Duration | Focus |
|-------|----------|-------|
| Recruiter | 30 min | Background, culture fit, role expectations |
| Hiring Manager | 45 min | Technical overview, team context, project scope |
| Technical Screen | 60 min | Previous architecture, deep dive into one project |
| Loop — 4 rounds | 45 min each | |
| Round 1 | System Design | Netflix-style: chaos engineering, resilience |
| Round 2 | Technical Deep Dive | Your projects, trade-offs, failures |
| Round 3 | Coding Pairing | Real-world problem, IDE, collaborative |
| Round 4 | Behavioral / Culture | Freedom & Responsibility, inclusion |

### Netflix Cloud Architecture

**Key Netflix AWS Architecture Components:**

| Component | AWS Service | Purpose |
|-----------|-------------|---------|
| Compute | EC2, Auto Scaling | Video encoding, serving |
| CDN | CloudFront (Open Connect) | Content delivery |
| Database | Cassandra, EVCache, S3 | Distributed storage |
| Messaging | SQS, SNS, Apache Kafka | Event streaming |
| CI/CD | Spinnaker, Jenkins | Deployment pipeline |
| Monitoring | Atlas, Winston | Metrics, alerting |
| Chaos | Chaos Monkey, Simian Army | Resilience testing |

---

## 6. HashiCorp

### Company Overview
HashiCorp builds infrastructure software for multi-cloud environments. Products include Terraform, Vault, Consul, Nomad, and Boundary. Interview process values open source contributions, IaC depth, and HashiCorp principles.

### Infrastructure as Code Focus

**Core IaC Concepts:**

| Concept | HashiCorp Tool | Description |
|---------|---------------|-------------|
| Resource provisioning | Terraform | Declarative infrastructure management |
| Configuration management | Packer | Machine image creation |
| Secret management | Vault | Dynamic secrets, encryption |
| Service networking | Consul | Service mesh, service discovery |
| Workload orchestration | Nomad | Batch scheduling, job orchestration |
| Access management | Boundary | Just-in-time access, session recording |

**Terraform Advanced Topics:**
- State management: remote backends (S3, HCP Terraform), state locking, state migrations
- Module composition: public registry, private modules, version constraints
- Provider development: Go SDK, acceptance testing, custom resources
- Sentinel policies: policy as code, enforcement levels, mock evaluations
- Workspaces: multiple environments, variable hierarchy, remote operations
- HCL syntax: expressions, functions, dynamic blocks, for_each

### Open-Source Community Culture

**HashiCorp Principles:**
- **Transparency**: Open source code, public roadmap, community RFCs
- **Iteration**: Ship early, iterate based on feedback
- **Community**: Contributors, partners, ecosystem growth
- **Empathy**: Understand user needs, build for practitioners
- **Wabi-Sabi**: Embrace imperfect, pragmatic solutions

**Open Source Interview Questions:**
- Tell me about a significant open source contribution you made
- How do you handle a controversial pull request?
- Describe your experience maintaining an open source project
- How would you build community around a new tool?
- What makes a good open source contribution?

### Interview Process

| Round | Focus | Preparation |
|-------|-------|-------------|
| Recruiter Screen | Background, IaC experience | Know HashiCorp products well |
| Hiring Manager | Technical overview, principles | Read HashiCorp blog/whitepapers |
| Technical Screen | Terraform state/modules/providers | Hands-on Terraform experience |
| Loop — System Design | Multi-cloud IaC workflows | Practice with Terraform, Vault |
| Loop — Technical Depth | HashiCorp products, alternatives | Know competitors (Pulumi, Ansible) |
| Loop — Community | Open source, contributions | GitHub profile, PR history |
| Loop — Behavioral | HashiCorp principles | STAR stories aligned to principles |

---

## 7. Oracle Cloud Infrastructure

### Company Overview
OCI is a strong contender for enterprise database workloads and mission-critical systems. Interview process focuses on database expertise, enterprise migration, and OCI-native architecture.

### Enterprise Focus

**OCI Enterprise Strengths:**
- Autonomous Database (self-driving, self-securing, self-repairing)
- Exadata Cloud Service (high-performance database) 
- OCI Dedicated Region (cloud in customer data center)
- Oracle Cloud VMware Solution (run VMware on OCI)
- Zero-downtime migration (ZDM, GoldenGate)

### Interview Process

| Round | Focus | Preparation |
|-------|-------|-------------|
| Recruiter | Background, Oracle ecosystem | Know Oracle value proposition |
| Technical Screen | OCI services, architecture | OCI Architect Associate material |
| Loop — System Design | Enterprise migration, database | ZDM, Data Guard, RAC patterns |
| Loop — Technical Deep Dive | Previous projects | Enterprise-scale cloud projects |
| Loop — Behavioral | Oracle values | Innovation, customer success |

---

## 8. Datadog

### Company Overview
Datadog is the leading cloud monitoring and observability platform. Interview process emphasizes depth in monitoring, system design for metrics pipelines, and strong coding ability.

### Observability Focus

**Core Concepts:**
- **Metrics**: Time-series data, aggregation, dimensionality
- **Traces**: Distributed tracing, span relationships, latency analysis
- **Logs**: Structured logging, log parsing, aggregation pipelines
- **Profiling**: Continuous profiling, flame graphs, optimization

### Interview Process

| Round | Focus | Preparation |
|-------|-------|-------------|
| Recruiter | Background, observability tooling | Know Datadog products |
| Technical Screen | Monitoring concepts, simple design | Three pillars of observability |
| Loop — System Design | Metrics pipeline at scale | Apache Kafka, stream processing |
| Loop — Coding | Python/Go, data structures | Data processing, API design |
| Loop — Infrastructure | Agent architecture, integrations | K8s monitoring, custom checks |
| Loop — Behavioral | Datadog values | Customer impact examples |

---

## 9. Cross-Company Cloud Interview Tips

### Universal Technical Topics

| Topic | Must-Know |
|-------|-----------|
| Compute | VM vs container vs serverless trade-offs |
| Storage | Object vs block vs file, when to use each |
| Networking | VPC, subnets, routing, firewalls, DNS |
| Database | SQL vs NoSQL, replication, sharding |
| Security | IAM, encryption, secrets, compliance |
| Monitoring | Metrics, logs, traces, alerting |
| CI/CD | Pipelines, IaC, GitOps, canary deployments |
| Cost | Pricing models, optimization, FinOps |

### Behavioral Story Bank

**Prepare 12-15 stories covering:**
- Migration challenges (2 stories)
- Cost optimization success (1-2 stories)
- Outage handling and incident response (2 stories)
- Multi-cloud strategy (1-2 stories)
- Vendor lock-in discussion (1 story)
- Security incident (1 story)
- Performance optimization (1-2 stories)
- Team leadership / mentoring (1-2 stories)
- Conflict resolution (1 story)
- Failure and learning (1 story)

### Recommended Cloud Interview Books

- Designing Data-Intensive Applications — Martin Kleppmann
- AWS Well-Architected Framework Whitepapers — AWS
- Site Reliability Engineering — Google
- The DevOps Handbook — Gene Kim
- Terraform: Up and Running — Yevgeniy Brikman
- Kubernetes Up and Running — Kelsey Hightower
- System Design Interview — Alex Xu
- Cloud Native Patterns — Cornelia Davis

---

*Last updated: July 2026*
