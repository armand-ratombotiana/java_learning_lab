# GitLab DevOps/SRE Interview Guide

> Comprehensive prep guide for GitLab DevOps Engineer, SRE, Backend Engineer, and Infrastructure Engineer roles.

---

## 1. Role Overview

### DevOps Engineer
- **Focus**: GitLab.com infrastructure — multi-region Kubernetes, 50TB+ data.
- **Expectation**: You keep GitLab.com operational and scalable.
- **Levels**: Intermediate → Senior → Staff → Principal.
- **Languages**: Ruby on Rails, Go, Python.

### Site Reliability Engineer
- **Focus**: Incident response, capacity planning, reliability.
- **Expectation**: SRE principles, on-call, automation.

### Backend Engineer (CI/CD, Verify, Release)
- **Focus**: Feature development on GitLab's CI/CD engine.
- **Expectation**: Ruby on Rails, database modeling, API design.

### Infrastructure Engineer
- **Focus**: Terraform, Ansible, Chef migration, multi-region K8s.
- **Expectation**: IaC, automation, cloud infrastructure.

---

## 2. Interview Process

```
Application → Recruiter Screen (30 min) → Technical Assessment 
→ Onsite (4-5 rounds, 45 min each) → References → Offer
```

### Recruiter Screen
- **Length**: 30 minutes.
- **Content**: Logistics, GitLab CREDIT values alignment.
- **Tip**: Read the GitLab Handbook. Knowledge of CREDIT values is expected.

### Technical Assessment
- **Format**: Take-home or live coding.
- **Take-home**: Build a small CI pipeline or API within a few days.
- **Live**: LeetCode Medium + CI/CD scenario.

### Onsite Rounds

#### Round 1: Coding (45 min)
- **Difficulty**: LeetCode Medium.
- **Languages**: Ruby on Rails, Go, or Python.
- **Topics**: Database modeling, API design (REST/GraphQL), background jobs (Sidekiq).
- **Example**: "Design an API for a CI job queue." "Model a database for pipeline artifacts."

#### Round 2: GitLab CI/CD Deep Dive (45 min)
- **Scenario-based**:
  - "Design a CI pipeline for a monorepo with 5 services."
  - "How would you implement a review app?"
  - "Optimize a slow pipeline (30 min → 5 min)."
  - "How does GitLab CI caching work under the hood?"
- **Key concepts**:
  - `.gitlab-ci.yml` structure: stages, jobs, rules, needs, artifacts, cache.
  - Runners: shared, specific, autoscaling, Kubernetes executor.
  - Multi-project and parent-child pipelines.
  - CI variables, environment scoping.

#### Round 3: System Design (45 min)
- **Topics**: Design GitLab.com — multi-region, 50TB+ data, 1M+ CI jobs/day.
- **Key areas**:
  - GitLab Geo architecture: primary/secondary sites, replication, disaster recovery.
  - Database: PostgreSQL partitioning, table sizes, vacuum, connection pooling.
  - CI fleet: runner scaling, job queue, shared vs group runners.
  - Storage: Gitaly (Git repo storage), object storage.
- **Tool knowledge**: PostgreSQL, Redis, Sidekiq, Gitaly, GitLab Shell, Workhorse, Prometheus, Grafana, Thanos, Loki, Jaeger.

#### Round 4: Debugging Production (45 min)
- **Real GitLab.com incidents** (publicly documented):
  - PostgreSQL replication lag → cascading failure.
  - Redis connection pool exhaustion.
  - K8s node failure → pod evictions.
  - Gitaly topology change → Git operations failure.
- **Expectation**: Systematic approach: detect, triage, mitigate, prevent.

#### Round 5: Values Alignment (45 min)
- **CREDIT values**:
  - **Collaboration**: Cross-team project with difficult stakeholder.
  - **Results**: Measurable impact project.
  - **Efficiency**: Prioritization under pressure.
  - **Diversity**: Inclusive team culture contribution.
  - **Iteration**: Minimum viable change example.
  - **Transparency**: Communicating bad news.
- **Handbook-first**: "Have you read any part of the GitLab Handbook?"

---

## 3. GitLab Architecture Deep Dive

### Core Components
| Component | Purpose | Language | Design Consideration |
|-----------|---------|----------|---------------------|
| Rails monolith | Web UI, API, business logic | Ruby on Rails | Decomposing into Go services |
| Sidekiq | Background job processing | Ruby | Redis-backed, 100K+ jobs/min |
| Gitaly | Git repository storage | Go | NFS replacement, RPC-based |
| GitLab Shell | SSH access, Git operations | Go | Session management |
| Workhorse | Reverse proxy, file uploads | Go | Handles large requests |
| Redis | Caching, Sidekiq, shared state | — | Sentinel cluster, 100GB+ data |
| PostgreSQL | Primary database | — | 50+ tables, partitioned, 10TB+ |
| Object Storage | Artifacts, packages, uploads | — | S3-compatible (GCS, MinIO) |

### GitLab Geo
| Feature | Purpose | Design Consideration |
|---------|---------|---------------------|
| Primary site | Read-write | Single writable site |
| Secondary sites | Read-only replicas | Multiple per region |
| Database replication | PostgreSQL streaming | Synchronous or asynchronous |
| File replication | Object storage sync | Background worker |
| Redirect | Read requests to nearest secondary | DNS-based |

### CI/CD Architecture
| Component | Purpose | Scale Consideration |
|-----------|---------|---------------------|
| GitLab Runner | Job execution | Autoscaling (Docker Machine, K8s) |
| Job queue | Pending jobs (Redis) | Priority, concurrency limits |
| Artifact storage | Job outputs | Object storage, expiration policy |
| Cache | Dependency caching | S3-compatible, distributed |

---

## 4. GitLab CI/CD Deep Dive

### Essential YAML Structure
```yaml
stages:
  - build
  - test
  - deploy

variables:
  DEPLOY_ENV: "staging"

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - node_modules/

build-job:
  stage: build
  script:
    - npm install
    - npm run build
  artifacts:
    paths:
      - dist/

test-job:
  stage: test
  script:
    - npm test
  needs: ["build-job"]

deploy-prod:
  stage: deploy
  script:
    - deploy.sh
  rules:
    - if: $CI_COMMIT_BRANCH == "main"
      when: manual
```

### Key Keywords
| Keyword | Purpose | Example |
|---------|---------|---------|
| `stages` | Ordered pipeline phases | `build → test → deploy` |
| `needs` | DAG execution | `test needs: [build]` |
| `rules` | Conditional job execution | `rules:changes: - src/*` |
| `cache` | Dependency caching | `cache:key: ${CI_COMMIT_REF_SLUG}` |
| `artifacts` | Build outputs | `artifacts:paths: [dist/]` |
| `environment` | Deployment tracking | `environment:name: production` |
| `resource_group` | Mutual exclusion | Prevent concurrent deploys |
| `parallel` | Parallel job matrix | `parallel:matrix: - OS: [ubuntu, macos]` |

### CI/CD Patterns at GitLab Scale
1. **Monorepo**: Use `rules:changes` to build only affected services.
2. **Review apps**: Dynamic environments per MR.
3. **Auto DevOps**: Built-in CI/CD for common languages.
4. **Multi-project pipelines**: Trigger downstream project pipelines.
5. **Parent-child pipelines**: Dynamic configuration generation.

---

## 5. GitLab CREDIT Values

| Value | Definition | Interview Example |
|-------|------------|-------------------|
| **Collaboration** | Everyone can contribute | Cross-team infrastructure project |
| **Results** | Measure outcomes, not output | Reduced CI pipeline time by 70% |
| **Efficiency** | Bias for action, lean | Automated manual process, saved 10h/week |
| **Diversity** | Diverse teams build better products | Mentored underrepresented group |
| **Iteration** | Minimum viable change | Shipped v1 with core features, iterated |
| **Transparency** | Public handbook, public issues | Shared incident postmortem publicly |

---

## 6. Key Technical Areas

### PostgreSQL at Scale
| Topic | Depth | Example Question |
|-------|-------|------------------|
| Partitioning | Deep | "How does GitLab partition large tables?" |
| Vacuum | Expert | "How do you monitor and tune autovacuum?" |
| Connection pooling | Expert | "How does PgBouncer work? Why is it needed?" |
| Replication lag | Expert | "How do you detect and handle replication lag?" |
| Query optimization | Expert | "How do you identify and fix slow queries?" |

### Redis
| Topic | Depth | Example |
|-------|-------|---------|
| Sentinel | Deep | "How does Redis Sentinel handle failover?" |
| Cluster | Deep | "When would you use Redis Cluster vs Sentinel?" |
| Memory management | Expert | "How do you monitor and evict Redis keys?" |

### Multi-Region Architecture
| Topic | Depth | Example |
|-------|-------|---------|
| Geo sites | Deep | "How does GitLab Geo handle write conflicts?" |
| Disaster recovery | Expert | "Design DR plan for GitLab.com." |
| Data residency | Deep | "How do you ensure EU data stays in EU?" |

---

## 7. Study Resources

### Books
- _GitLab Handbook_ (handbook.gitlab.com) — read the whole thing.
- _The Site Reliability Workbook_.

### Online
- GitLab Engineering Blog (about.gitlab.com/blog).
- GitLab.com Incident Postmortems (public).
- GitLab Documentation (docs.gitlab.com).

### Labs
- Set up a GitLab instance with CI/CD.
- Configure GitLab Runner with Kubernetes.
- Set up GitLab Geo replication.
- Create multi-project pipelines.

---

## 8. Preparation Checklist

- [ ] Read the GitLab Handbook (especially CI/CD, Engineering, Values).
- [ ] Master GitLab CI YAML (stages, needs, rules, cache, artifacts).
- [ ] Understand GitLab architecture (Rails, Sidekiq, Gitaly, Redis, PostgreSQL).
- [ ] Learn GitLab Geo concepts (replication, failover, disaster recovery).
- [ ] Prepare CREDIT value stories (2 per value).
- [ ] Study GitLab.com incident postmortems.
- [ ] Practice Ruby (Rails, ActiveRecord, Sidekiq).
- [ ] System design: GitLab.com at scale.

---

_End of GITLAB_INTERVIEW_GUIDE.md_