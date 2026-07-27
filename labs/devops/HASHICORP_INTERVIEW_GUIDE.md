# HashiCorp Interview Guide

> Comprehensive prep guide for HashiCorp Solutions Engineer, Software Engineer (Infrastructure), and SRE roles.

---

## 1. Role Overview

### Solutions Engineer
- **Focus**: Customer-facing — demonstrate Terraform, Vault, Consul, Nomad.
- **Expectation**: Deep product knowledge, presentation skills, architecture consulting.
- **Levels**: SE I → II → III → Senior → Principal.
- **Skills**: Terraform expert, cloud infrastructure, communication.

### Software Engineer (Infrastructure)
- **Focus**: Build the products — Terraform core, Vault secrets engine, Consul service mesh.
- **Expectation**: Go expertise, distributed systems, API design.
- **Levels**: IC4 → IC5 → IC6 → IC7.
- **Languages**: Go (primary), some Ruby, Python.

### Site Reliability Engineer
- **Focus**: Keep HashiCorp Cloud Platform (HCP) operational.
- **Expectation**: Incident response, reliability engineering, automation.

### Developer Advocate
- **Focus**: Community, content, speaking, tutorials.
- **Expectation**: Deep product knowledge, writing, public speaking.

---

## 2. Interview Process

```
Application → Recruiter Screen (30 min) → Technical Screen (60 min) 
→ Onsite (4-5 rounds, 45 min each) → Offer
```

### Recruiter Screen
- **Length**: 30 minutes.
- **Content**: Logistics, HashiCorp principles overview.
- **Tip**: Know the HashiCorp product suite (Terraform, Vault, Consul, Nomad, Packer, Waypoint, Boundary).

### Technical Screen
- **Length**: 60 minutes.
- **Format**: Tool-specific discussion or system design.
- **Content**:
  - For Solutions Engineer: Terraform module design, provider config.
  - For SWE: Go coding or distributed system design.
- **Example**: "Design a Terraform module for multi-region deployment." "How does Vault handle replication?"

### Onsite Rounds

#### Round 1: Go/Python Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Topics**: Data structures, concurrency (goroutines, channels), error handling.
- **Language**: Go (preferred).
- **Expectation**: Idiomatic Go — interfaces, `context.Context`, `sync` package.
- **Example**: "Implement a thread-safe rate limiter." "Design a concurrent configuration parser."

#### Round 2: System Design (45 min)
- **Topics**: Design Terraform state management at scale, multi-cloud service mesh, secrets rotation system.
- **Key areas**:
  - Terraform: state locking (DynamoDB, Consul), parallelism, drift detection.
  - Consul: service mesh, Connect, gossip protocol, multi-datacenter.
  - Vault: seal/unseal, replication (DR, performance), auto-unseal.
- **Expectation**: Distributed systems knowledge (Raft, gossip, CAP).

#### Round 3: Tool-Specific Mastery (45 min)
- **Deep dive based on role**:
  - **Terraform**: Module structure, provider SDK, state management, sentinel.
  - **Vault**: Secret engines, auth methods, dynamic secrets, policies.
  - **Consul**: Service discovery, Connect, intentions, KV store.
  - **Nomad**: Job specification, task drivers, scheduling.

#### Round 4: Behavioral/Principles (45 min)
- **HashiCorp principles**:
  - **Autonomy**: "Tell me about a project you owned end-to-end."
  - **Transparency**: "How do you ensure decisions are visible?"
  - **Collaboration**: "Describe a cross-team project."
  - **Humility**: "Tell me about a time you were wrong."
- **Open source**: "How do you engage with the open source community?"

#### Round 5: Debugging (45 min)
- **Scenario**:
  - "Terraform plan shows unexpected diff. Debug."
  - "Vault performance degradation. Investigate."
  - "Consul service mesh connectivity issue. Troubleshoot."

---

## 3. Key Product Knowledge

### Terraform
| Topic | Depth | Example Question |
|-------|-------|------------------|
| HCL language | Expert | "How do you use `for_each` vs `count`?" |
| Providers | Deep | "How do you write a Terraform provider?" |
| State management | Expert | "How does state locking work? What backends support it?" |
| Modules | Expert | "Design a reusable VPC module with inputs and outputs." |
| Workspaces | Deep | "When would you use workspaces vs separate directories?" |
| Terraform Cloud | Deep | "How does Sentinel policy integration work?" |
| Error handling | Expert | "A resource creation fails. How do you recover?" |

### Vault
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Secret engines | Expert | "What's the difference between KV v1 and v2?" |
| Auth methods | Expert | "How does Kubernetes auth work?" |
| Dynamic secrets | Expert | "How does Vault generate dynamic database credentials?" |
| PKI | Deep | "How would you set up a PKI hierarchy with Vault?" |
| Policies | Expert | "Write a policy that grants read access to secret/data/team-a/*." |
| Storage backend | Deep | "Compare Consul vs Integrated Raft storage." |
| Replication | Deep | "What's the difference between DR and performance replication?" |
| Seal/Unseal | Expert | "How does auto-unseal work with AWS KMS?" |

### Consul
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Service discovery | Expert | "How does Consul register services? How does DNS work?" |
| Service mesh | Expert | "How do Connect sidecars work? What are intentions?" |
| Gossip protocol | Deep | "Explain SWIM gossip protocol. How does it scale?" |
| KV store | Deep | "How would you use the KV store for feature flags?" |
| Multi-DC | Deep | "How does multi-datacenter federation work?" |
| Health checks | Expert | "What types of health checks does Consul support?" |

### Nomad
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Job specification | Expert | "Write a Nomad job for a web service." |
| Task drivers | Deep | "Compare Docker, exec, and raw_exec drivers." |
| Scheduling | Deep | "How does Nomad schedule jobs? Compare to K8s scheduler." |
| Scaling | Deep | "How does Nomad autoscale? How does it integrate with Consul?" |

---

## 4. HashiCorp Principles

### Autonomy
- "We don't have a heavy process. We trust each other to make the right decisions."
- **Interview**: Describe a time you took initiative without being told.

### Transparency
- "Open source means open development. Everything is public."
- **Interview**: How do you communicate decisions and rationale?

### Collaboration
- "Better together. We welcome contributions and feedback."
- **Interview**: Describe a cross-team project.

### Humility
- "No ego. We're all learners."
- **Interview**: Describe a time you were wrong and learned from it.

---

## 5. System Design — HashiCorp Focus

### Common Design Problems
1. **Distributed secrets management system** — Vault-like.
2. **Service mesh for multi-cloud** — Consul-like.
3. **Infrastructure provisioning at scale** — Terraform-like.
4. **Job scheduler for mixed workloads** — Nomad-like.

### Key Design Considerations
- **Consensus**: Raft for strong consistency (Terraform state, Vault storage).
- **Gossip**: SWIM for cluster membership (Consul).
- **Plugin architecture**: Providers (Terraform), secret engines (Vault).
- **Security**: mTLS, encryption at rest, audit logging.

---

## 6. Go Coding Interview Prep

### Must-Know Go Patterns
| Pattern | Example | Use Case |
|---------|---------|----------|
| Goroutines | `go func()` | Concurrency |
| Channels | `ch := make(chan int)` | Communication |
| Select | `select { case <-ch1: ... }` | Multiplexing |
| Context | `ctx, cancel := context.WithTimeout(...)` | Cancellation, deadlines |
| Interfaces | `type Reader interface { Read(p []byte) (n int, err error) }` | Abstraction |
| Error handling | `if err != nil { return fmt.Errorf("failed: %w", err) }` | Error wrapping |
| Sync | `sync.Mutex`, `sync.WaitGroup`, `sync.Once` | Synchronization |
| Testing | `testing.T`, `testing.B` | Unit and benchmark tests |

### Common Go Interview Problems
- Implement a concurrent rate limiter.
- Implement a thread-safe key-value store.
- Parse and validate a configuration file.
- Implement a retry mechanism with exponential backoff.
- Write a simple HTTP server with graceful shutdown.

---

## 7. Study Resources

### Books
- _Terraform: Up & Running_ (Yevgeniy Brikman).
- _The Go Programming Language_ (Donovan & Kernighan).

### Online
- HashiCorp Learn (learn.hashicorp.com).
- HashiCorp GitHub — source code, RFCs.
- Go by Example (gobyexample.com).

### Labs
- Terraform: Build and use modules.
- Vault: Configure dynamic secrets.
- Consul: Set up service mesh.
- Nomad: Deploy a job.

---

## 8. Preparation Checklist

- [ ] Master Terraform (modules, state, providers, HCL).
- [ ] Learn Vault (secret engines, auth, policies, replication).
- [ ] Understand Consul (service discovery, mesh, gossip).
- [ ] Know Nomad basics (job spec, task drivers).
- [ ] Practice Go coding (concurrency, interfaces, context).
- [ ] System design practice (state management, secrets rotation, service mesh).
- [ ] Prepare principle-aligned behavioral stories.
- [ ] Understand open source contribution patterns.

---

_End of HASHICORP_INTERVIEW_GUIDE.md_