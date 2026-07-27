# Company Interview Guide — DevOps Interview Processes & Tool Knowledge Expectations

> Detailed breakdown of each company's interview process with specific tool knowledge expectations.

---

## Table of Contents
1. Google — SRE Interview Process
2. Meta — Production Engineer Interview Process
3. Amazon — Systems Engineer / DevOps Interview Process
4. Netflix — Cloud Engineer Interview Process
5. Microsoft — Azure DevOps Interview Process
6. GitHub — DevOps / SRE Interview Process
7. GitLab — DevOps / SRE Interview Process
8. HashiCorp — Solutions & Software Engineer Interview Process
9. Datadog — SRE & SWE Interview Process
10. New Relic — SRE & SWE Interview Process
11. Splunk — SRE & DevOps Interview Process
12. PagerDuty — SRE & Platform Engineer Interview Process

---

## 1. Google SRE Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Phone Screen (45 min) 
→ Onsite (4-5 rounds, 45 min each) → Hiring Committee → Offer
```

### Recruiter Screen
- Logistics, timeline, team preference.
- Resume walkthrough.
- Basic SRE knowledge check.
- **Tip**: Have your resume ready with quantified impact (e.g., "Reduced P50 latency by 40%").

### Phone Screen (45 min)
- **Format**: Google Docs (no IDE).
- **Coding**: 1-2 LeetCode Medium problems.
  - Common topics: arrays, strings, hash maps, trees, dynamic programming.
- **Systems Basics**:
  - "What happens when you type google.com in your browser?"
  - Load balancing, DNS, CDN, TCP, HTTP.
- **Tip**: Think out loud. Google values communication as much as correctness.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Hard.
- **Topics**: Graphs (DFS, BFS, topological sort), dynamic programming, trees.
- **Expectation**: Optimal time/space complexity. Handle edge cases.
- **Tool knowledge**: Not directly tested. Pure algorithms.

#### Round 2: Coding (45 min)
- Same as Round 1, but a different problem.
- May involve concurrency (e.g., thread-safe counter, producer-consumer).

#### Round 3: System Design (45 min)
- **Topics**: Design YouTube, Google Drive, Google Search, Google Docs.
- **Key areas**: Sharding, replication, caching, consistency models, load balancing.
- **Tool knowledge expected**:
  - Distributed storage: Colossus/GFS (conceptual), Bigtable, Spanner.
  - Kubernetes/Borg: container orchestration concepts.
  - Load balancing: Google Front End (GFE), Maglev.

#### Round 4: SRE Technical (45 min)
- **Linux internals**:
  - Process scheduling (CFS, nice values, real-time).
  - Memory management (virtual memory, page cache, swapping, THP).
  - Filesystem (ext4, XFS, inodes, dentries, page cache).
  - cgroups, namespaces, capabilities.
  - strace, perf, eBPF, bpftrace.
- **Networking**:
  - TCP congestion control (CUBIC, BBR, BBRv3).
  - HTTP evolution (1.1, 2, 3/QUIC).
  - BGP, anycast, DNS, global load balancing.
  - tcpdump, Wireshark, iptables/nftables.
- **Debugging**:
  - "A service is experiencing latency spikes. Walk through debugging."
  - CPU, memory, I/O, network — which tool for which?

#### Round 5: Googleyness & Leadership (45 min)
- **Behavioral questions**:
  - "Tell me about a time you disagreed with your manager."
  - "Describe a project you led that had a significant impact."
  - "How do you handle ambiguous situations?"
  - "Tell me about a time you failed."
- **Leadership without authority** — SREs often influence without direct reports.
- **SRE-specific**: "Describe an incident you led. What was your role? What did you learn?"

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Linux | Expert — kernel, debugging, performance |
| Networking | Expert — TCP, HTTP, load balancing, DNS |
| Kubernetes/Borg | Strong — architecture, scheduling, networking |
| Distributed storage | Strong — GFS, Colossus, Spanner concepts |
| CI/CD | Moderate — Jenkins, Spinnaker, internal tools |
| Monitoring | Strong — Prometheus, Monarch, Borgmon |
| Scripting | Python, Go, Shell |
| Terraform | Moderate — used for GCP resource provisioning |
| Configuration Mgmt | Moderate — Puppet, Chef, or internal tools |

---

## 2. Meta Production Engineer Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Coding Screen (45 min) 
→ Onsite (4 rounds, 45 min each) → Hiring Committee → Offer
```

### Recruiter Screen
- Logistics, team preference (PE, PE Network, SRE).
- Experience overview.
- **Tip**: Emphasize production debugging, automation projects.

### Coding Screen (45 min, CoderPad)
- **Format**: CoderPad — Python, C++, PHP, or Hack.
- **Difficulty**: LeetCode Medium.
- **Topics**: Arrays, strings, trees, hash maps.
- **Expectation**: Working code. Edge cases. Time complexity.
- **Tool knowledge**: Not directly tested.

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design Facebook News Feed, Messenger, Instagram Stories, WhatsApp.
- **Key areas**:
  - Feed generation, ranking, push vs pull.
  - Real-time messaging, presence, delivery guarantees.
  - Video upload pipeline, transcoding, CDN.
- **Tool knowledge expected**:
  - MySQL, Memcached, TAO (graph storage), Cassandra.
  - Hadoop, Hive, Presto (analytics).
  - Thrift (RPC framework).
  - CDN: Akamai, Facebook's own Edge.
  - Load balancing: L4/L7, consistent hashing.

#### Round 2: Coding (45 min)
- **Difficulty**: LeetCode Medium-Hard.
- **Topics**: Strings, dynamic programming, recursion.
- **Expectation**: Clean, readable, production-quality code.
- **Tip**: Write helper functions, handle null/empty, consider concurrency.

#### Round 3: Coding (45 min)
- Same as Round 2, different problem.
- May focus on concurrency: lock-free data structures, thread safety.

#### Round 4: Linux/Production Debugging (45 min)
- **Scenario-based**:
  - "A service is running hot on CPU. Walk through debugging."
  - "Network latency between two data centers has increased. Debug."
  - "MySQL replication lag is growing. Investigate."
  - "A process is leaking memory. How do you find the leak?"
- **Tools**:
  - strace, ltrace, gdb, perf, flamegraphs.
  - tcpdump, tc (traffic control), netstat, ss.
  - top, htop, iostat, vmstat, sar, dmesg.
  - /proc filesystem — /proc/cpuinfo, /proc/meminfo, /proc/slabinfo.
  - eBPF basics: BCC, bpftrace.
- **Meta-specific context**:
  - Tupperware (container management).
  - ZippyDB (RocksDB-based distributed KV).
  - Scuba (real-time analytics database).

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Linux | Expert — debugging, performance analysis |
| Networking | Strong — TCP, BGP, load balancing, CDN |
| Databases | Strong — MySQL, Memcached, Cassandra, RocksDB |
| Python/C++ | Strong — production debugging, performance |
| Hadoop/Data | Moderate — Hive, Presto, Scuba |
| Containers | Moderate — Maille, Tupperware, Docker |
| CI/CD | Moderate — Jenkins, internal tools |

---

## 3. Amazon Systems Engineer / DevOps Interview Process

### Process Overview
```
Application → Online Assessment (optional) → Phone Screen (60 min) 
→ Onsite (5 rounds, 60 min each) → Bar Raiser → Offer
```

### Online Assessment (for SDE/SysDE)
- 2 coding problems (LeetCode Easy-Medium).
- Work style assessment (behavioral survey).
- **Duration**: 90-120 min.

### Phone Screen (60 min)
- **Format**: Chime + Live Coding.
- **Coding**: 1 LeetCode Easy-Medium problem.
- **Leadership Principles**: 2-3 behavioral questions (STAR).
  - "Tell me about a time you went above and beyond."
  - "Describe a time you disagreed with a decision."
- **System Design (optional)**: Design a small system (e.g., URL shortener).

### Onsite Rounds

#### Round 1: Coding (60 min)
- **Difficulty**: LeetCode Easy-Medium.
- **Topics**: Arrays, strings, linked lists, stacks, queues, trees.
- **No LeetCode Hard** at Amazon for SysDE.
- **Expectation**: Working code, testing, walk-through. STAR not expected.

#### Round 2: Coding (60 min)
- Same difficulty. May focus on object-oriented design.
- **Example**: Design a parking lot, elevator system, vending machine.
- **Expectation**: OOP principles, design patterns (Singleton, Factory, Strategy).

#### Round 3: System Design (60 min)
- **Topics**: Design Amazon S3, DynamoDB, Prime Video, shopping cart.
- **Key areas**:
  - Scalability, availability, fault tolerance.
  - Databases: RDS, DynamoDB, ElastiCache.
  - AWS: EC2, S3, VPC, Lambda, ELB, Auto Scaling.
- **Tool knowledge expected**:
  - AWS: deep knowledge of core services.
  - Terraform or CloudFormation for IaC.
  - Kubernetes (EKS), Docker (ECS) for containers.
- **Leadership Principle**: "Are Right, A Lot" — defend your design decisions.

#### Round 4: Behavioral (60 min) — LP Deep Dive
- **5+ STAR stories**.
- Must cover multiple Leadership Principles.
- **Common questions**:
  - "Tell me about a time you took a calculated risk." (Bias for Action)
  - "Tell me about a time you had to dive deep into a problem to fix it." (Dive Deep)
  - "Tell me about a time you were customer-obsessed." (Customer Obsession)
  - "Tell me about a time you failed." (Learn and be Curious)
- **Expectation**: Every answer must explicitly tie to a Leadership Principle.

#### Round 5: Bar Raiser (60 min)
- **Independent interviewer** — higher bar, protects company standards.
- Mix of coding, system design, and behavioral.
- **Typical**:
  - 1 coding problem (Medium).
  - 2-3 behavioral questions (LPs they haven't heard yet).
  - 1 system design question.
- **Tip**: The Bar Raiser can veto an offer even if all other rounds pass.

### Amazon Leadership Principles — STAR Story Bank
Prepare **2 stories per LP**. Use this template:
| LP | Story |
|----|-------|
| Customer Obsession | "A client needed a feature that wasn't on the roadmap. I advocated for it..." |
| Ownership | "I found a critical bug in production. I stayed until 2 AM to fix it..." |
| Invent and Simplify | "I automated a manual deployment process, reducing deploy time from 2 hours to 10 minutes..." |
| Are Right, A Lot | "The team wanted to use MongoDB. I advocated for DynamoDB because..." |
| Learn and Be Curious | "I learned Terraform to migrate our entire infrastructure..." |
| Hire and Develop the Best | "I mentored a junior engineer who became a top performer..." |
| Insist on the Highest Standards | "I refused to sign off on a release because of test coverage..." |
| Think Big | "I proposed a multi-region architecture that supported global expansion..." |
| Bias for Action | "Production was down. I rolled back without waiting for approval..." |
| Frugality | "I downsized EC2 instances after right-sizing analysis..." |
| Earn Trust | "I was transparent about an outage in the postmortem..." |
| Dive Deep | "I debugged a memory leak down to the kernel level..." |
| Have Backbone; Disagree and Commit | "I disagreed with the architect's choice but committed once decided..." |
| Deliver Results | "I completed the migration ahead of schedule..." |
| Strive to be Earth's Best Employer | "I created an inclusive on-call rotation..." |
| Success and Scale Bring Broad Responsibility | "I implemented security best practices across the org..." |

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| AWS | Expert — majority of services, IAM, networking |
| Linux | Strong — system administration, shell scripting |
| Terraform/CloudFormation | Strong — IaC, modules, state management |
| CI/CD | Strong — CodePipeline, Jenkins, GitHub Actions |
| Docker/Kubernetes | Moderate-Strong — ECS, EKS, Fargate |
| Databases | Strong — DynamoDB, RDS, ElastiCache, Aurora |
| Scripting | Python, Shell, Java |
| Monitoring | Moderate — CloudWatch, Prometheus, Grafana |

---

## 4. Netflix Cloud Engineer Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Phone Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Culture fit: Do you understand Netflix's culture?
- Experience with AWS at scale.
- **Tip**: Read the Netflix Culture Deck before this call.

### Technical Phone Screen (60 min)
- **System Design**: Design a subsystem (e.g., video upload pipeline, CDN, recommendation).
- **Coding**: LeetCode Medium (optional).
- **Architecture discussion**: Microservices, chaos engineering, CI/CD.

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design Netflix CDN (Open Connect), encoding pipeline, personalization, AB testing framework.
- **Key areas**:
  - Content delivery: cache hierarchy, ISP peering, SSD caching.
  - Video encoding: chunked encoding, per-title optimization, different resolutions.
  - Chaos engineering: failure injection, blast radius, steady state.
- **Tool knowledge**:
  - AWS: S3, EC2, DynamoDB, Auto Scaling, CloudFront.
  - Titus (container orchestration).
  - Spinnaker (CI/CD).
  - Chaos Monkey, Chaos Kong, Latency Monkey.

#### Round 2: System Design (45 min)
- Another design problem, possibly more focused.
- **Example**: Design a system for AB experimentation at Netflix scale.
- **Key considerations**: Statistical significance, traffic splitting, metrics collection, rollback.

#### Round 3: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Java (primary), Python, Go.
- **Tip**: Netflix uses Java heavily. Know Java concurrency, streams, memory model.

#### Round 4: Cultural/Contextual (45 min)
- **Informal conversation** with senior engineers.
- **Topics**:
  - "How do you make decisions without a rulebook?"
  - "Tell me about a time you had to make a trade-off."
  - "How do you handle a teammate who isn't performing?"
  - "Describe a project you started from nothing."
- **No STAR format** — Netflix prefers direct, honest, candid conversation.

#### Round 5: Operations/Chaos Engineering (45 min)
- **Scenario**:
  - "An AWS region goes down. How does Netflix survive?"
  - "Design a chaos experiment for a critical service."
  - "How do you measure resilience?"
- **Key concepts**:
  - Fault domains, blast radius, steady-state hypothesis.
  - Automated rollback, canary analysis.
  - Multi-region active-active architecture.

### Unique Interview Aspects
- **No behavioral STAR format** — Netflix prefers candid discussion.
- **Cultural fit is as important as technical skill**.
- **"Stunning Colleagues"** — they expect you to be exceptional.
- **Chaos engineering knowledge is a must**.

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| AWS | Expert — massive scale, multi-region |
| Java | Expert — memory model, concurrency, JVM tuning |
| Chaos Engineering | Expert — steady-state, blast radius, hypothesis |
| Spinnaker | Strong — pipelines, canary, blue-green |
| Titus/Docker | Strong — container orchestration |
| CDN | Strong — Open Connect, cache hierarchy |
| Microservices | Strong — gRPC, circuit breakers, bulkhead |
| Monitoring | Moderate — Atlas, Spectator, Prometheus |

---

## 5. Microsoft Azure DevOps Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (45-60 min) 
→ Onsite (4-5 rounds, 45 min each) → ASAPP → Offer
```

### Recruiter Screen
- Logistics, team preference.
- **Tip**: Emphasize Azure experience, certifications (AZ-400, AZ-104).

### Technical Screen (45-60 min)
- **System Design**: Design a CI/CD pipeline, AKS cluster, or monitoring solution.
- **Coding (optional)**: LeetCode Medium (if coding round included).
- **Azure knowledge**: Quick assessment of ARM, Bicep, Azure DevOps.

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design Azure DevOps, AKS, Azure Blob Storage, Cosmos DB.
- **Key areas**:
  - CI/CD pipeline architecture, agent pools, parallel jobs.
  - Container orchestration: AKS clusters, node pools, networking.
  - Multi-region, geo-redundancy, disaster recovery.
- **Tool knowledge**:
  - Azure DevOps: pipelines, repos, artifacts, boards, test plans.
  - ARM/Bicep: declarative IaC, modules, deployments.
  - AKS: cluster creation, scaling, upgrades, network policies.
  - Monitoring: Azure Monitor, Log Analytics, Application Insights.

#### Round 2: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: C# (preferred), Python, TypeScript, or Go.
- **Topics**: Arrays, strings, trees, hash maps.
- **Tip**: If using C#, know LINQ, async/await, Task, value types vs reference types.

#### Round 3: Coding or Design (45 min)
- Second coding round OR additional design round.
- May include concurrency or database design.
- **Example**: Design a SQL table schema for a DevOps artifact registry.

#### Round 4: Behavioral (45 min) — Growth Mindset
- **Microsoft-specific behavioral**:
  - "Tell me about a time you had to learn a new technology quickly."
  - "Describe a project that didn't go as planned. What did you learn?"
  - "How do you give and receive feedback?"
  - "Tell me about a time you advocated for a technical decision."
- **Growth Mindset**: Focus on learning from failures, self-improvement.
- **Collaboration**: "How do you work with teams you don't directly manage?"

#### Round 5: Azure/Azure DevOps Deep Dive (45 min)
- **Deep technical** with a senior engineer.
- **Topics**:
  - Azure Pipelines YAML structure, templates, variable groups, library.
  - Multi-stage pipelines, environment approvals, checks, gates.
  - Deployment strategies: canary, blue-green, rolling, ring-based.
  - Kubernetes on Azure: AKS networking (Azure CNI, kubenet), Istio, Ingress.
  - Terraform on Azure: state in Azure Storage, modules, workspaces.
- **Ask about**: "How do you handle secrets in pipelines?" (Key Vault, Variable Groups, Azure Key Vault task).

### Microsoft Certifications (Bonus)
| Certification | Relevance |
|--------------|-----------|
| AZ-400: DevOps Engineer Expert | Directly relevant |
| AZ-104: Azure Administrator | Good foundation |
| AZ-204: Azure Developer | Coding + Azure |
| AZ-300/AZ-301: Solutions Architect | System design |

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Azure DevOps | Expert — pipelines, repos, artifacts, boards |
| Azure | Strong — AKS, ACR, Key Vault, Monitor, Storage |
| ARM/Bicep/Terraform | Strong — IaC, modules, deployments |
| Docker/Kubernetes | Strong — AKS, Helm, container networking |
| CI/CD Patterns | Strong — canary, blue-green, rolling |
| C#/.NET | Strong (if coding in C#) |
| Scripting | PowerShell, Azure CLI, Python |
| Monitoring | Strong — Azure Monitor, App Insights |

---

## 6. GitHub DevOps / SRE Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → References → Offer
```

### Recruiter Screen
- Logistics, remote-first culture.
- **Tip**: Emphasize remote collaboration, async communication skills.

### Technical Screen (60 min)
- **Pairing session**:
  - Real-world problem: debug a CI pipeline, design a GitHub Action, optimize a slow Git operation.
  - Or coding: LeetCode Medium.
- **Git knowledge**: "How does `git merge` work internally?"

### Onsite Rounds

#### Round 1: System Design (45 min)
- **Topics**: Design GitHub Actions, GitHub Packages, Codespaces, Issue search.
- **Key areas**:
  - Actions: runner architecture, job distribution, caching, artifact storage.
  - Packages: container registry, Docker V2 API, storage backend (S3/Azure Blob).
  - Codespaces: VS Code remote, containerized dev environments, prebuilds.
  - Search: Elasticsearch/Lucene, indexing, query optimization.
- **Tool knowledge**:
  - Ruby on Rails, Go, TypeScript — GitHub's stack.
  - MySQL, Redis, Elasticsearch — storage.
  - Docker, Kubernetes — runtime.
  - GitHub Actions, Packages, Pages — features.

#### Round 2: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Ruby (preferred), Go, or TypeScript.
- **Topics**: Strings, arrays, trees.
- **Tip**: Ruby — know blocks, procs, lambdas, each/map/select.

#### Round 3: Debugging/Incident Response (45 min)
- **Simulated incident**:
  - "GitHub.com is experiencing elevated error rates. Walk through your investigation."
  - "A user reports that their push is taking 10 seconds. Debug."
  - "GitHub Pages is serving stale content. What happened?"
- **Expected**: Systematic approach — check dashboards, logs, recent deployments, database queries.

#### Round 4: Git/Version Control Deep Dive (45 min)
- **Git internals**:
  - What is the `.git` directory structure?
  - How does Git store objects? (blob, tree, commit, tag).
  - What is a pack file? How does Git compress objects?
  - Explain `git rebase` vs `git merge` internally.
  - How does `git bisect` work?
  - What is a detached HEAD state?
  - How do you recover lost commits? (reflog).
- **GitHub-specific**:
  - How does GitHub resolve merge conflicts?
  - How does the Pull Request model work? (refs/pull/ namespace).
  - How does GitHub Pages publish?

#### Round 5: Behavioral (45 min)
- **Remote-first collaboration**:
  - "How do you communicate asynchronously?"
  - "How do you build trust in a remote team?"
  - "Tell me about a time you worked with someone in a different timezone."
- **Open source ethos**:
  - "Have you contributed to open source?"
  - "How do you handle community feedback?"
  - "What open source projects do you admire?"
- **Ship to learn**: "Describe a time you shipped something imperfect."

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Git | Expert — internals, objects, merge strategies |
| Ruby/Rails | Strong (preferred) — API, ActiveRecord |
| Go | Moderate-Strong — services, tooling |
| MySQL/Redis/ES | Strong — schema design, query optimization |
| Docker/Kubernetes | Strong — containerization, K8s architecture |
| GitHub Actions | Strong — workflows, runners, API |
| CI/CD | Moderate-Strong — build, test, deploy pipelines |

---

## 7. GitLab DevOps / SRE Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Assessment 
→ Onsite (4-5 rounds, 45 min each) → References → Offer
```

### Recruiter Screen
- Overview, CREDIT values alignment.
- **Tip**: Read GitLab's Handbook before — knowledge of CREDIT values is expected.

### Technical Assessment
- **Take-home or live coding**:
  - Take-home: Build a small CI pipeline or API.
  - Live: LeetCode Medium + CI/CD scenario.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Ruby on Rails, Go, or Python.
- **Topics**: Database modeling, API design, background jobs (Sidekiq).

#### Round 2: GitLab CI/CD Deep Dive (45 min)
- **Scenario-based**:
  - "Design a CI pipeline for a monorepo with multiple services."
  - "How would you implement a review app in GitLab CI?"
  - "Optimize a pipeline that takes 30 minutes to run."
  - "How does GitLab CI cache work under the hood?"
- **Key concepts**:
  - .gitlab-ci.yml structure: stages, jobs, rules, needs, artifacts, cache.
  - Runners: shared vs specific, autoscaling, Kubernetes executor.
  - Multi-project pipelines, parent-child pipelines.
  - CI/CD variables, environment scoping, protected branches.

#### Round 3: System Design (45 min)
- **Topics**: Design GitLab.com — multi-region, 50TB+ data, 1M+ CI jobs/day.
- **Key areas**:
  - GitLab Geo architecture: primary/secondary sites, replication, disaster recovery.
  - Database sharding: partitioning GitLab.com's PostgreSQL, table sizes, vacuum.
  - CI fleet: runner scaling, job queue, shared vs group runners.
  - Storage: Gitaly (Git repository storage), object storage (S3-compatible).
- **Tool knowledge**:
  - PostgreSQL, Redis, Sidekiq.
  - Gitaly, GitLab Shell, Workhorse.
  - Kubernetes, Helm, Auto DevOps.
  - Prometheus, Grafana, Thanos, Loki, Jaeger.
  - Terraform, Ansible, Chef.

#### Round 4: Debugging Production (45 min)
- **Real GitLab.com incidents** (publicly documented):
  - PostgreSQL replication lag cascading failure.
  - Redis connection pool exhaustion.
  - Kubernetes node failure causing pod evictions.
  - Gitaly topology change causing Git operations to fail.
- **Approach**: How would you detect, triage, mitigate, and prevent recurrence?
- **Tip**: Read GitLab.com incident postmortems (publicly available).

#### Round 5: Values Alignment (45 min)
- **CREDIT values**:
  - **Collaboration**: "Tell me about a time you worked with a difficult stakeholder."
  - **Results**: "Describe a project where you delivered measurable results."
  - **Efficiency**: "How do you prioritize work when everything is urgent?"
  - **Diversity**: "How have you contributed to an inclusive team culture?"
  - **Iteration**: "Describe a time you shipped a minimum viable change."
  - **Transparency**: "How do you communicate bad news?"
- **Handbook-first**: "Have you read any part of the GitLab Handbook?"

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| GitLab CI | Expert — YAML, rules, needs, runners, caching |
| GitLab architecture | Strong — Rails, Sidekiq, Gitaly, Geo |
| PostgreSQL | Strong — partitioning, vacuum, connection pooling |
| Redis | Strong — Sentinel, Cluster, connection management |
| Kubernetes | Strong — Helm, Auto DevOps, cluster management |
| Monitoring | Strong — Prometheus, Grafana, Loki, Thanos |
| Ruby/Rails | Strong — ActiveRecord, API, background jobs |
| Terraform/Ansible | Strong — infrastructure automation |

---

## 8. HashiCorp Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Logistics, HashiCorp principles overview.
- **Tip**: Know HashiCorp's product suite well.

### Technical Screen (60 min)
- **Tool-specific discussion**:
  - Terraform: modules, providers, state, workspaces, `terraform plan` vs `apply`.
  - Vault: secret engines, auth methods, dynamic secrets, replication.
  - Consul: service discovery, Connect, intentions, gossip protocol.
  - Nomad: job specifications, task drivers, scheduling.
- **Or system design** (for SWE roles): Design a distributed secrets store.

### Onsite Rounds

#### Round 1: Go/Python Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Topics**: Data structures, concurrency (goroutines, channels), error handling.
- **Tip**: HashiCorp uses Go extensively. Know Go idioms — interfaces, `context` package, `sync` package.

#### Round 2: System Design (45 min)
- **Topics**: Design Terraform state management at scale, multi-cloud service mesh, secrets rotation system.
- **Key areas**:
  - Terraform: state locking (DynamoDB, Consul), state storage backends, concurrent operations.
  - Consul: service mesh, Connect, intentions, multi-datacenter federation.
  - Vault: HA, replication (DR, performance), seal/unseal, auto-unseal.
- **Tool knowledge**:
  - Go, gRPC, Protocol Buffers.
  - Consul, Vault, Terraform, Nomad internals.
  - Cloud providers: AWS, GCP, Azure, Kubernetes.

#### Round 3: Tool-Specific Mastery (45 min)
- **Deep dive** based on role:
  - **Terraform role**: "Design a multi-region Terraform module structure." "How would you implement a custom provider?" "State file corruption — how to recover?"
  - **Vault role**: "Design a certificate rotation system." "How does Vault handle encryption as a service?" "Design a multi-tenant Vault architecture."
  - **Consul role**: "Design service mesh for 5000 services." "How does Consul handle network partitions?" "Gossip protocol efficiency."
  - **Nomad role**: "Design a job scheduler for batch and long-running processes." "How does Nomad handle bin packing?"

#### Round 4: Behavioral/Principles (45 min)
- **HashiCorp principles**:
  - **Autonomy**: "Tell me about a time you owned a project end-to-end."
  - **Transparency**: "How do you ensure decisions are visible to the team?"
  - **Collaboration**: "Describe a cross-team project that required coordination."
  - **Humility**: "Tell me about a time you were wrong."
- **Open source**: "How do you engage with the open source community?"

#### Round 5: Debugging (45 min)
- **Scenario**:
  - "Terraform plan outputs a diff you didn't expect. Debug."
  - "Vault performance degradation. Investigate."
  - "Consul service mesh connectivity issue. Troubleshoot."
  - "Nomad job placement failure. Find the root cause."

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Go | Expert — concurrency, interfaces, gRPC |
| Terraform | Expert — HCL, providers, state, modules |
| Vault | Expert — secrets engines, auth, replication |
| Consul | Expert — service mesh, gossip, KV store |
| Nomad | Strong — job spec, scheduling, task drivers |
| gRPC/Protobuf | Strong — API design, message formats |
| Cloud Providers | Strong — multi-cloud, IAM, networking |
| Distributed Systems | Strong — consensus (Raft), gossip, CAP |

---

## 9. Datadog SRE / SWE Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Overview, Datadog tech stack.
- **Tip**: Know Datadog products — Metrics, Logs, APM, RUM, Synthetics.

### Technical Screen (60 min)
- **System design** or **coding**:
  - Design a monitoring system.
  - LeetCode Medium coding problem.
- **Optional**: Debugging a real Datadog Agent issue.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Medium-Hard.
- **Languages**: Python, Go, or Java.
- **Topics**: Data structures, algorithms, concurrency.

#### Round 2: System Design (45 min)
- **Topics**: Design distributed tracing system, log aggregation pipeline, custom metric ingestion.
- **Key areas**:
  - Trace propagation (DD-TRACE, B3, W3C), sampling (head-based, tail-based), storage.
  - Log pipeline: collection, parsing, exclusion, indexing, archive.
  - Metrics: aggregation, downsampling, rollups, histograms.
- **Tool knowledge**:
  - Datadog Agent, dogstatsd, check scheduling.
  - Prometheus, OpenMetrics, OpenTelemetry.
  - Kubernetes, Docker, AWS/GCP.
  - Cassandra, S3, Kafka — storage and streaming.

#### Round 3: Debugging/Incident (45 min)
- **Scenario**:
  - "The Datadog Agent is using 2GB of memory. Debug."
  - "A customer reports missing metrics for their service. Investigate."
  - "APM traces are incomplete — some spans are missing. Find root cause."
- **Approach**: Check logs, metrics (of the monitoring system), recent changes, configs.

#### Round 4: Observability Deep Dive (45 min)
- **Topics**:
  - PromQL: rate(), irate(), increase(), histogram_quantile().
  - Log queries: filtering, aggregation, patterns.
  - APM: service map, flame graphs, trace search.
  - RUM: Core Web Vitals, session replay.
  - Synthetic: browser tests, API tests, locations.
- **Best practices**:
  - USE method (Utilization, Saturation, Errors).
  - RED method (Rate, Errors, Duration).
  - Four golden signals (Latency, Traffic, Errors, Saturation).

#### Round 5: Behavioral (45 min)
- **High agency**: "Tell me about a time you took initiative beyond your role."
- **Customer empathy**: "Describe a time you went out of your way for a customer."
- **Scale**: "How do you ensure reliability when serving 1000s of customers?"

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Datadog | Expert — Agent, dogstatsd, integrations |
| Python/Go | Strong — Agent, backend services |
| Prometheus | Strong — PromQL, service discovery, alerting |
| Observability | Expert — MELT (Metrics, Events, Logs, Traces) |
| Kubernetes | Strong — container monitoring, cluster metrics |
| Cloud (AWS/GCP) | Strong — EC2, S3, Lambda, GKE |
| Cassandra/Kafka | Moderate-Strong — data ingestion pipeline |

---

## 10. New Relic SRE / SWE Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Logistics, New Relic products.
- **Tip**: Know New Relic One, NRQL, and the MELT paradigm.

### Technical Screen (60 min)
- **System design** or **coding**:
  - Design an ingestion pipeline for telemetry data.
  - LeetCode Medium coding problem.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Java, Go, or Python.
- **Topics**: Data structures, algorithms, concurrency.

#### Round 2: System Design (45 min)
- **Topics**: Design a telemetry ingestion pipeline, NRQL query engine, alert evaluation system.
- **Key areas**:
  - Ingestion: normalization, sampling, aggregation, backpressure.
  - Query: NRQL parser, query planner, execution engine.
  - Alerts: threshold evaluation, anomaly detection, incident correlation.
- **Tool knowledge**:
  - New Relic Agent, NRQL.
  - Kafka, Cassandra, S3.
  - Kubernetes, Docker, Terraform.

#### Round 3: Observability Design (45 min)
- **Scenario**: "Design an instrumentation strategy for a 200-microservice application."
- **Key considerations**:
  - Which libraries (OpenTelemetry, New Relic agent)?
  - Sampling strategies (head-based, tail-based).
  - Metrics (RED, USE method), dashboards, alerts.
  - Distributed tracing: service map, trace grouping.
  - Logs: structured logging, correlation IDs.

#### Round 4: Debugging (45 min)
- **Scenario**:
  - "NRQL query is timing out. Debug."
  - "Telemetry data is missing for a specific customer. Investigate."
  - "Alert is firing incorrectly (false positive). Root cause."

#### Round 5: Behavioral (45 min)
- **Radical transparency**: "Tell me about a time you shared bad news proactively."
- **Customer obsessed**: "Describe a time you went deep to solve a customer problem."
- **Intellectual honesty**: "Tell me about a time you changed your mind based on data."

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| New Relic | Expert — NRQL, dashboards, alerts, agents |
| Java/Go/Python | Strong — instrumentation, backend |
| Telemetry Pipeline | Strong — ingestion, normalization, sampling |
| Observability | Expert — MELT, RED/USE methods |
| Kafka/Cassandra | Strong — streaming, storage |
| Kubernetes/Docker | Strong — containerized deployment |
| Terraform | Moderate — IaC for monitoring infrastructure |

---

## 11. Splunk SRE / DevOps Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Logistics, Splunk products.
- **Tip**: Know Splunk architecture (indexers, search heads, forwarders).

### Technical Screen (60 min)
- **SPL (Search Processing Language)** :
  - Write SPL queries for given scenarios.
  - Data onboarding: props.conf, transforms.conf.
- **System design (optional)** : Design a log aggregation system.

### Onsite Rounds

#### Round 1: SPL Deep Dive (45 min)
- **Complex SPL tasks**:
  - "Write a search that correlates login attempts from different IPs."
  - "Create a data model for web traffic."
  - "Optimize a slow search — which commands to avoid?" (sort, transaction).
  - "Design a scheduled search that alerts when error rate > 5%."
- **Knowledge objects**: event types, tags, lookups, macros, data models.

#### Round 2: System Design (45 min)
- **Topics**: Design Splunk indexer cluster, multi-site replication, log aggregation for 10K servers.
- **Key areas**:
  - Indexer cluster: peer nodes, master node, search factor, replication factor.
  - Search head cluster: captain, knowledge bundles, sticky sessions.
  - Forwarders: universal vs heavy, load balancing, SSL.
  - Multi-site: indexer replication, disaster recovery.

#### Round 3: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Python, Java, or Go.
- **Topics**: Data structures, algorithms.

#### Round 4: Infrastructure (45 min)
- **Topics**: Splunk on Kubernetes, operator, HEC (HTTP Event Collector).
- **Infrastructure as Code**: Terraform, Ansible for Splunk deployment.
- **Monitoring**: Monitoring Splunk itself — indexer health, search performance, license usage.

#### Round 5: Behavioral (45 min)
- **Data-driven**: "Tell me about a time you used data to drive a decision."
- **Customer-first**: "Describe a time you went above and beyond for a customer."
- **Collaboration**: "How do you work with teams that don't understand your domain?"

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Splunk | Expert — SPL, indexers, forwarders, knowledge objects |
| Python | Strong — scripting, data ingestion |
| Terraform/Ansible | Strong — infra automation |
| Kubernetes | Moderate-Strong — Splunk on K8s, operator |
| Networking | Strong — syslog, TCP, UDP, data ingestion |

---

## 12. PagerDuty SRE / Platform Engineer Interview Process

### Process Overview
```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- Logistics, PagerDuty values.
- **Tip**: Know incident management lifecycle and PagerDuty products.

### Technical Screen (60 min)
- **System design** or **coding**:
  - Design an incident management system.
  - LeetCode Medium coding problem.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Ruby, Go, or Kotlin.
- **Topics**: Data structures, concurrency.

#### Round 2: System Design (45 min)
- **Topics**: Design on-call scheduling, event correlation engine, service catalog.
- **Key areas**:
  - Scheduling: layered schedules, rotations, overrides, timezone handling.
  - Event correlation: deduplication, grouping, noise suppression.
  - Service catalog: dependencies, ownership, readiness score.
- **Tool knowledge**:
  - Ruby/Rails, Go, Kafka, Cassandra.
  - Kubernetes, Docker, Terraform.
  - PagerDuty API, webhooks, Terraform provider.

#### Round 3: Incident Response Simulation (45 min)
- **Tabletop exercise**:
  - "A critical service is down. Walk through your incident response."
  - "A customer reports they're not receiving alerts. Debug."
  - "A PagerDuty service is degraded. How do you communicate this?"
- **Key areas**:
  - Severity levels (SEV1-SEV5), escalation policies.
  - Incident commander role, communication channels.
  - Stakeholder updates, postmortem timeline.

#### Round 4: Service Catalog & CMDB (45 min)
- **Design a service dependency graph**:
  - How do you model ownership, dependencies, and readiness?
  - How do you detect missing service ownership?
  - How do you measure service maturity?
- **API and automation**: Using PagerDuty API for service management.

#### Round 5: Behavioral (45 min)
- **Blameless culture**: "Tell me about a time you handled a mistake gracefully."
- **Calm under pressure**: "Describe a high-stress incident you managed."
- **Continuous improvement**: "How do you ensure lessons learned become action items?"
- **Ownership**: "Describe a time you went beyond your scope to fix a problem."

### Tool Knowledge Expectations (Summary)
| Tool/Area | Depth Required |
|-----------|---------------|
| Ruby/Rails/Go | Strong — backend services |
| Kubernetes/Docker | Strong — containerized platform |
| Incident Management | Expert — lifecycle, severity, escalation |
| On-call Scheduling | Expert — rotations, overrides, timezones |
| Event Correlation | Strong — deduplication, grouping |
| Terraform | Moderate-Strong — service management |
| Kafka/Cassandra | Moderate — event streaming, storage |

---

## Comparison: Interview Process by Company

| Company | Phone Screen | Coding | System Design | Behavioral | Unique Rounds |
|---------|-------------|--------|---------------|------------|---------------|
| Google | Coding + Systems | 2 rounds (Hard) | 1 round | Googleyness | SRE Technical |
| Meta | Coding (Medium) | 2 rounds (Med-Hard) | 1 round | Embedded | Debugging Production |
| Amazon | Coding + LP | 2 rounds (Easy-Med) | 1 round | 2 LP rounds | Bar Raiser |
| Netflix | System Design | 1 round (Medium) | 2 rounds (Hard) | Cultural | Chaos Engineering |
| Microsoft | Design/Coding | 1-2 rounds (Medium) | 1 round | Growth Mindset | Azure Deep Dive |
| GitHub | Pairing | 1 round (Medium) | 1 round | Remote Culture | Git Internals |
| GitLab | CI/CD Scenario | 1 round (Medium) | 1 round | CREDIT Values | CI/CD Deep Dive |
| HashiCorp | Tool Discussion | 1 round (Medium) | 1 round | Principles | Tool-Specific Mastery |
| Datadog | Design/Coding | 1 round (Med-Hard) | 1 round | High Agency | Observability Deep Dive |
| New Relic | Design/Coding | 1 round (Medium) | 1 round | Transparency | Observability Design |
| Splunk | SPL Queries | 1 round (Medium) | 1 round | Data-driven | SPL Deep Dive |
| PagerDuty | Design/Coding | 1 round (Medium) | 1 round | Blameless | Incident Response Sim |

---

## General Timeline Estimate by Company

| Company | Time to Offer (avg) | Number of Rounds | Prep Time |
|---------|--------------------|-------------------|-----------|
| Google | 4-8 weeks | 5-6 | 3-6 months |
| Meta | 3-6 weeks | 4-5 | 2-4 months |
| Amazon | 3-6 weeks | 5-6 | 2-4 months |
| Netflix | 4-6 weeks | 5-6 | 2-4 months |
| Microsoft | 3-5 weeks | 4-5 | 1-3 months |
| GitHub | 3-5 weeks | 4-5 | 1-3 months |
| GitLab | 3-5 weeks | 4-5 | 1-3 months |
| HashiCorp | 3-5 weeks | 4-5 | 1-3 months |
| Datadog | 3-5 weeks | 4-5 | 1-3 months |
| New Relic | 3-5 weeks | 4-5 | 1-3 months |
| Splunk | 3-5 weeks | 4-5 | 1-3 months |
| PagerDuty | 3-5 weeks | 4-5 | 1-3 months |

---

## Interview Preparation Checklist (Per Company)

### Google
- [ ] Read SRE Book (first 5 chapters)
- [ ] Master Linux debugging (strace, perf, eBPF)
- [ ] Practice LeetCode Hard — graphs, DP, trees
- [ ] Prepare 3 Googleyness stories
- [ ] System design: YouTube, Drive, Search

### Meta
- [ ] Practice production debugging (strace, perf, flamegraphs)
- [ ] Prepare ownership stories
- [ ] Practice LeetCode Medium — arrays, strings, trees
- [ ] System design: News Feed, Messenger, Stories
- [ ] Review MySQL, Memcached, Cassandra

### Amazon
- [ ] Memorize 16 Leadership Principles
- [ ] Prepare 2 STAR stories per LP
- [ ] Practice LeetCode Easy-Medium
- [ ] Master AWS (EC2, S3, VPC, IAM, EKS)
- [ ] System design: S3, DynamoDB, shopping cart

### Netflix
- [ ] Read Culture Deck
- [ ] Study chaos engineering (Chaos Monkey)
- [ ] Practice system design (2 rounds)
- [ ] Prepare for candid cultural discussions
- [ ] Master AWS at scale

### Microsoft
- [ ] Review AZ-400 content
- [ ] Prepare Growth Mindset stories
- [ ] Master Azure DevOps (pipelines, YAML, artifacts)
- [ ] Practice C# / .NET (if applicable)
- [ ] System design: CI/CD pipeline, AKS

### GitHub
- [ ] Study Git internals (Pro Git book)
- [ ] Practice remote collaboration stories
- [ ] Master GitHub Actions (custom actions, runners)
- [ ] System design: Actions, Packages
- [ ] Open source contribution history

### GitLab
- [ ] Read GitLab Handbook
- [ ] Master GitLab CI YAML
- [ ] Learn GitLab architecture (Rails, Sidekiq, Gitaly)
- [ ] Prepare CREDIT value stories
- [ ] System design: GitLab.com at scale

### HashiCorp
- [ ] Master Terraform (providers, modules, state)
- [ ] Learn Vault, Consul, Nomad basics
- [ ] Practice Go concurrency
- [ ] Prepare autonomy/humility stories
- [ ] System design: secrets management, service mesh

### Datadog
- [ ] Master Datadog products (Metrics, Logs, APM)
- [ ] Learn PromQL and RED/USE methods
- [ ] Practice Python/Go coding
- [ ] Prepare high agency stories
- [ ] System design: monitoring system

### New Relic
- [ ] Master NRQL
- [ ] Learn OpenTelemetry
- [ ] Practice Java/Go coding
- [ ] Prepare transparency stories
- [ ] System design: telemetry pipeline

### Splunk
- [ ] Master SPL
- [ ] Understand indexer/search head architecture
- [ ] Learn Splunk on K8s
- [ ] Prepare data-driven stories
- [ ] System design: log aggregation

### PagerDuty
- [ ] Master incident management lifecycle
- [ ] Practice Ruby/Go coding
- [ ] Prepare blameless culture stories
- [ ] Learn on-call scheduling patterns
- [ ] System design: incident management system

---

_End of COMPANY_INTERVIEW_GUIDE.md_