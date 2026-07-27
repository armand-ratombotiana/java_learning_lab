# DevOps System Design Cheatsheet

> Architecture patterns, trade-offs, and design decisions for DevOps/distributed systems interviews.

---

## Table of Contents
1. CI/CD Pipeline Design
2. Infrastructure as Code
3. Kubernetes Architecture
4. Service Mesh
5. Monitoring/Observability Stack
6. Secrets Management
7. Container Registries & Artifact Management
8. General Distributed Systems Patterns

---

## 1. CI/CD Pipeline Design

### GitHub Actions

#### Architecture
```
Event (push, PR, schedule) → Workflow → Job → Step → Action
```
- **Workflow**: YAML file in `.github/workflows/`.
- **Jobs**: Run on runners (GitHub-hosted or self-hosted). Can be parallel or sequential (`needs`).
- **Steps**: Individual tasks. Can use GitHub Actions, shell commands, or Docker containers.
- **Runners**: Ubuntu, Windows, macOS. Self-hosted can be any environment.

#### Key Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| Events | Trigger workflows | push, pull_request, schedule, workflow_dispatch, repository_dispatch |
| Matrix builds | Test multiple configs | os, node-version, environment |
| Services | Ephemeral dependencies | PostgreSQL, Redis, MySQL |
| Cache | Dependency caching | actions/cache, restore/upload keys |
| Artifacts | Build outputs | upload-artifact, download-artifact |
| Environments | Deployment targets | protection rules, reviewers, wait timer |
| Secrets | Encrypted variables | org-level, repo-level, environment-level |

#### Pipeline Design Patterns
1. **Monorepo Pipeline**:
   - Use path filtering (`paths`, `paths-ignore`).
   - Matrix builds for each service.
   - `concurrency` to cancel redundant runs.
2. **Multi-Environment Deployment**:
   - Dev → Staging → Production with gates.
   - Environment protection rules.
   - Deployment status checks.
3. **CI/CD for Containers**:
   - Build image → Scan (Trivy, Snyk) → Push to registry → Deploy to K8s.
   - Use `docker/build-push-action` with caching.
4. **GitOps Integration**:
   - CI builds and pushes image.
   - CD updates manifests in GitOps repo.
   - ArgoCD syncs automatically.

#### Trade-offs
| Pro | Con |
|-----|-----|
| Tight GitHub integration | Vendor lock-in |
| 2000 min/month free (public) | Limited Windows/macOS hosted minutes |
| Vast marketplace | YAML can become complex |
| Composite actions for reuse | Debugging large workflows is hard |

### GitLab CI

#### Architecture
```
.gitlab-ci.yml → Pipeline → Stages → Jobs
```
- **Runners**: Specific, shared, or group runners. Executors: shell, Docker, Kubernetes.
- **Stages**: Sequential by default. Jobs within a stage run in parallel.
- **Pipeline types**: Branch, Merge Request, Tag, Scheduled, Multi-project, Parent-child.

#### Key Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| Stages | Pipeline organization | build, test, deploy, security |
| Jobs | Individual tasks | script, image, services, artifacts |
| Rules | Conditional execution | rules:changes, rules:if, rules:exists |
| Needs | DAG execution | Run jobs out of stage order |
| Cache | Dependency caching | key, paths, policy: pull-push |
| Artifacts | Build outputs | paths, expire_in, reports |
| Variables | Configuration | CI/CD variables, file type, masking |
| Environments | Deployment tracking | name, url, on_stop, auto_stop |

#### Pipeline Design Patterns
1. **DAG Pipelines**:
   - Use `needs` to create directed acyclic graphs.
   - Parallel jobs can start as soon as their dependencies are met.
2. **Multi-Project Pipelines**:
   - Trigger downstream pipelines from upstream.
   - Use `CI_JOB_TOKEN` for authentication.
3. **Parent-Child Pipelines**:
   - Dynamically generate child pipelines from YAML.
   - Useful for monorepos with microservices.
4. **Auto DevOps**:
   - Built-in CI/CD for common languages.
   - Customizable via variables and templates.

#### Trade-offs
| Pro | Con |
|-----|-----|
| Self-hosted runners | Runner maintenance overhead |
| Built-in registry, container scanning | EE features are expensive |
| DAG support (needs) | Complex YAML rules |
| Review apps | Resource-intensive for large pipelines |

### Jenkins

#### Architecture
```
Jenkins Master → (distributes load) → Jenkins Agents
```
- **Master**: Manages jobs, schedules builds, serves UI.
- **Agents**: Execute builds. Can be static (VMs) or dynamic (Kubernetes, Docker, EC2).
- **Pipeline as Code**: Declarative or Scripted Pipeline in Jenkinsfile.

#### Key Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| Pipeline | CI/CD workflow | Declarative (structured) vs Scripted (flexible) |
| Stages | Pipeline phases | Parallel, sequential, nested |
| Steps | Individual actions | sh, docker, withCredentials, checkout |
| Agents | Execution environment | label, docker, kubernetes, any |
| Credentials | Secret management | usernamePassword, sshUserPrivateKey, file |
| Shared Libraries | Reusable pipeline code | global vars, steps, resources |

#### Pipeline Design Patterns
1. **Multibranch Pipeline**:
   - Automatically creates pipelines for each branch.
   - Uses Jenkinsfile from each branch.
2. **Kubernetes Agent Pod**:
   - Dynamic agents using Kubernetes plugin.
   - Each build runs in an ephemeral pod.
3. **Shared Library**:
   - Centralize pipeline logic.
   - Versioned in Git.
4. **Declarative Matrix**:
   - Multi-configuration builds (OS, toolchain).
   - Parallel execution.

#### Trade-offs
| Pro | Con |
|-----|-----|
| Extremely flexible | High maintenance overhead |
| Vast plugin ecosystem | Groovy DSL is hard to debug |
| Mature, large community | UI is outdated |
| Any agent environment | Scaling master is complex |

### ArgoCD (GitOps CD)

#### Architecture
```
Git Repository → (sync) → ArgoCD → (apply) → Kubernetes Cluster
```
- **Application Controller**: Watches Git repo for changes. Syncs desired state to cluster.
- **API Server**: gRPC/REST API. Exposes UI, CLI.
- **Repo Server**: Caches repo contents. Generates manifests (Helm, Kustomize, plain YAML).
- **Application Controller**: Actual sync loop. Health assessment. Pruning. Auto-healing.

#### Key Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| Application | Unit of deployment | source (repo, path, targetRevision), destination (server, namespace) |
| Project | RBAC grouping | source restrictions, destination restrictions, cluster-scoped resources |
| ApplicationSet | Multi-cluster/namespace | generators (list, git, cluster, matrix, merge) |
| Sync Policy | Automation | automated.sync, automated.prune, automated.selfHeal |
| Sync Waves | Ordered deployment | annotations, phase, hook |
| Health Checks | Resource status | Lua scripts for custom resources |

#### Patterns
1. **App of Apps**:
   - One root Application manifests many child Applications.
   - Recursive pattern for managing multiple microservices.
2. **ApplicationSet with Git Generator**:
   - Generate Applications from directories in a Git repo.
   - Each directory = one Application.
3. **Progressive Delivery**:
   - Sync waves for ordered deployment (CRDs first, then controllers, then apps).
   - Blue-green or canary with Argo Rollouts.
4. **Multi-Cluster Management**:
   - Single ArgoCD instance manages multiple clusters.
   - ApplicationSet with cluster generator.

#### Trade-offs
| Pro | Con |
|-----|-----|
| Declarative GitOps | Learning curve for sync waves |
| Auto-healing | Can drift from cluster state |
| Multi-cluster support | Large-scale repo server caching |
| UI + CLI + API | RBAC configuration complex |

---

## 2. Infrastructure as Code

### Terraform (HashiCorp)

#### Architecture
```
Terraform Core → (reads) → State (backend) → Providers → (CRUD) → Cloud API
```

#### Key Concepts
| Concept | Description | Design Patterns |
|---------|-------------|-----------------|
| State | Mapping of resources to real world | Remote backend (S3, Consul, Terraform Cloud), state locking (DynamoDB) |
| Provider | Interface to cloud API | AWS, GCP, Azure, K8s, Helm, custom providers |
| Resource `resource "type" "name" {}` | Infrastructure component | Terraform registry, version constraints |
| Data Source `data "type" "name" {}` | Read external resources | Cross-referencing, dependency inversion |
| Module | Reusable configuration | root module, child module, registry modules |
| Workspace | State isolation | Default workspace, named workspaces, Terraform Cloud workspaces |
| Variable `variable` | Input parameters | type, default, validation, sensitive |
| Output `output` | Exported values | sensitive, depends_on |
| Backend | State storage location | s3, consul, remote (Terraform Cloud), azurerm |

#### Module Design Patterns
1. **Composable Modules**:
   - Separate infra into: `networking`, `compute`, `database`, `security`.
   - Each module is self-contained with inputs, outputs, and resources.
2. **Terragrunt**:
   - DRY configurations for multi-environment.
   - Remote state management automation.
3. **Provider Version Pinning**:
   - `required_providers` block with version constraints.
   - Lock file (.terraform.lock.hcl) for reproducible builds.
4. **State Isolation**:
   - Separate state per environment (dev/staging/prod).
   - Separate state per service (network, app, data).

#### State Management
| Backend | Locking | Encryption | Use Case |
|---------|---------|------------|----------|
| S3 + DynamoDB | DynamoDB | S3 SSE-S3/KMS | AWS-native, widely used |
| Consul | Native | TLS | HashiCorp ecosystem |
| GCS + Cloud Storage Lock | Cloud Storage Lock | GCS encryption | GCP-native |
| Azure Storage | Blob lease | Azure Storage encryption | Azure-native |
| Terraform Cloud | Native | Native | Team collaboration |

#### Trade-offs
| Pro | Con |
|-----|-----|
| Declarative, idempotent | State file management is critical |
| Vast provider ecosystem | HCL can be verbose |
| Immutable infrastructure | Not great for mutable config (use Ansible) |
| Plan/apply workflow | Slow for large configurations |

### Pulumi

#### Architecture
```
Pulumi CLI → (reads) → State (backend) → Providers (SDK) → (CRUD) → Cloud API
```

#### Key Concepts
| Concept | Description | Design Patterns |
|---------|-------------|-----------------|
| Stack | Isolated deployment environment | Separate dev/staging/prod stacks |
| Project | One infrastructure project | Language-specific, configuration |
| Resource `new Resource()` | Infrastructure component | Same as Terraform resources |
| Component | Reusable infrastructure | Language-native composition (classes, functions) |
| Output | Exported values | `.apply()` for transformations |

#### Language Options
| Language | Strengths | When to Choose |
|----------|-----------|---------------|
| TypeScript | Type safety, async/await | Teams familiar with JS/TS |
| Python | Simplicity, data science | Python-centric teams |
| Go | Performance, concurrency | Infrastructure teams using Go |
| .NET/C# | Enterprise, F# | .NET ecosystem teams |
| Java | Mature, typed | Java-based teams |

#### Trade-offs
| Pro | Con |
|-----|-----|
| Real programming languages | Smaller community than Terraform |
| No DSL to learn | State management still required |
| Better for complex logic | Provider maturity varies |
| Strong type checking | Build/compile step needed |

### Crossplane

#### Architecture
```
Crossplane (in-cluster) → Provider → (CRUD) → Cloud API
```

#### Key Concepts
| Concept | Description |
|---------|-------------|
| Provider | Kubernetes CRD-based Terraform-style providers |
| Managed Resource | Single cloud resource (e.g., `Bucket`, `Database`) |
| Composition | Template for creating multiple managed resources |
| Claim | Namespace-scoped request for a composite resource |
| Composite Resource (XR) | Cluster-scoped instantiation of a composition |

#### Design Patterns
1. **Platform Abstraction Layer**:
   - Define Compositions for common patterns: `DBCluster`, `VPCNetwork`, `K8sCluster`.
   - Platform team owns Compositions. App teams create Claims.
2. **GitOps with Crossplane**:
   - ArgoCD manages Crossplane resources.
   - Git repo contains Claims for app infrastructure.
   - Crossplane provisions cloud resources automatically.
3. **Multi-Cloud Abstraction**:
   - Define `PostgreSQLInstance` claim.
   - Different Compositions for RDS (AWS), Cloud SQL (GCP), Azure DB.

#### Trade-offs
| Pro | Con |
|-----|-----|
| Kubernetes-native IaC | Complex setup (providers, RBAC) |
| GitOps-friendly | Less mature than Terraform |
| Platform engineering focus | Provider coverage limited |
| No external state needed | In-cluster resource overhead |

---

## 3. Kubernetes Architecture

### Control Plane

#### Components
| Component | Purpose | High Availability Design |
|-----------|---------|------------------------|
| kube-apiserver | REST API gateway | Multiple instances (odd count), HAProxy/LB in front |
| etcd | Key-value store for cluster state | 3, 5, or 7 nodes. Raft consensus. TLS encryption. |
| kube-scheduler | Assigns pods to nodes | Active-active, leader election via lease |
| kube-controller-manager | Runs controllers | Active-active, leader election per controller |
| cloud-controller-manager | Cloud provider integration | Node controller, service controller, route controller |

#### API Server Flow
```
kubectl → Authentication (TLS certs, tokens, OIDC) 
→ Authorization (RBAC, ABAC, Webhook) 
→ Admission Control (Mutating → Validating) 
→ etcd (persist) → Scheduler (watch)
```

### Worker Nodes

#### Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| kubelet | Node agent, pod lifecycle | Register with API server, report node status |
| kube-proxy | Network proxy per node | Modes: IPTables, IPVS, userspace, kernelspace (eBPF) |
| container runtime | Runs containers | Docker, containerd, CRI-O, runc |

#### Pod Lifecycle
```
Pending → ContainerCreating → Running → Succeeded/Failed
```
- **InitContainers**: Sequential setup before app containers.
- **PostStart** and **PreStop**: Lifecycle hooks.
- **Probes**: `livenessProbe` (restart container), `readinessProbe` (remove from Service), `startupProbe` (delay liveness).

### Networking

#### CNI Interface
| CNI Provider | Characteristics | Use Case |
|-------------|-----------------|----------|
| Calico | NetworkPolicy enforcement, eBPF mode | Production, security-focused |
| Flannel | Simple overlay (VXLAN, host-gw) | Small clusters, simplicity |
| Weave | Mesh networking, encryption | Multi-cloud, encrypted |
| Cilium | eBPF-based, L7 policies, Hubble | Advanced networking, observability |
| Amazon VPC CNI | Native VPC IPs, no overlay | AWS-native, performance |
| Azure CNI | Azure VNET IPs | Azure-native |

#### Service Types
| Type | DNS | Cluster Access | External Access | Use Case |
|------|-----|----------------|-----------------|----------|
| ClusterIP | `svc.cluster.local` | Cluster-internal only | None | Inter-service communication |
| NodePort | Node IP + port | NodeIP:NodePort | Cluster-internal to external dev | Dev/test exposure |
| LoadBalancer | Cloud LB DNS | Cloud LB + NodePort | Cloud LB | Production services |
| ExternalName | CNAME record | External DNS alias | Via alias | Service migration |

#### Ingress vs Gateway API
| Feature | Ingress (v1) | Gateway API |
|---------|-------------|-------------|
| Standardization | Kubernetes core | Newer, extensible |
| Protocol | HTTP/HTTPS | HTTP, TCP, UDP, TLS, gRPC |
| Multi-tenancy | Namespace-based | Route splitting, reference grants |
| Backends | Service | Service, custom backends |
| Advanced routing | Annotations | Explicit route rules, weights, headers |

### Storage

#### Volume Types
| Type | Use Case | Characteristics |
|------|----------|-----------------|
| emptyDir | Temporary scratch | Pod lifecycle, node-local |
| hostPath | Node file access | Node-specific, not portable |
| ConfigMap/Secret | Configuration | Ephemeral, in-memory, projected volumes |
| PersistentVolume + PVC | Long-term data | Decoupled lifecycle, many provisioners |
| CSI driver | Third-party storage | EBS, EFS, GCE PD, Azure Disk, NFS, Portworx |

#### Storage Design Patterns
1. **StatefulSet + PVC Template**:
   - Each replica gets its own PVC.
   - Scale up/down with storage.
2. **Dynamic Provisioning**:
   - StorageClass with provisioner.
   - PVC automatically creates PV.
3. **Snapshot and Restore**:
   - VolumeSnapshot API.
   - CSI snapshot support.
4. **RWX (ReadWriteMany)**:
   - NFS, EFS, GlusterFS, Azure Files.
   - Shared storage across pods.

### Scheduling

#### Scheduling Algorithm
```
Node filtering (predicates) → Node scoring (priorities) → Binding
```
1. **Filtering**: Remove nodes that don't meet pod requirements:
   - PodFitsResources (CPU/memory).
   - PodMatchNodeSelector (nodeSelector, affinity).
   - PodToleratesNodeTaints (taints/tolerations).
   - PodFitsHostPorts, CheckNodeCondition.
2. **Scoring**: Rank remaining nodes:
   - MostRequestedPriority (bin packing).
   - LeastRequestedPriority (spreading).
   - BalancedResourceAllocation (even CPU/memory).
   - NodeAffinityPriority, TaintTolerationScorePriority.
3. **Bind**: Assign pod to highest-scoring node.

#### Scheduling Controls
| Control | Effect | Example |
|---------|--------|---------|
| nodeSelector | Simple label match | `nodeSelector: { disktype: ssd }` |
| nodeAffinity | Advanced expression | `requiredDuringSchedulingIgnoredDuringExecution` |
| podAffinity | Co-locate related pods | `topologyKey: kubernetes.io/hostname` |
| podAntiAffinity | Spread pods apart | `topologyKey: topology.kubernetes.io/zone` |
| taints + tolerations | Node repel, pod tolerate | `key: dedicated, value: gpu, effect: NoSchedule` |
| topologySpreadConstraints | Even distribution | MaxSkew, topologyKey, whenUnsatisfiable |

### Cluster Design Patterns

#### Cluster Types
| Cluster Type | Use Case | Considerations |
|-------------|----------|----------------|
| Single tenant (one team) | Small team, simple monolith | Shared cluster, RBAC |
| Multi-tenant (multiple teams) | Many teams, microservices | Namespace per team, RBAC, resource quotas, network policies |
| Multi-cluster (env per cluster) | Dev/staging/prod isolation | Separate clusters, GitOps for consistency |
| Federation (future) | Cross-cluster workloads | KubeFed, Cluster API |

#### HA Control Plane
- Stacked etcd: etcd co-located with control plane.
- External etcd: Separate etcd cluster.
- Min 3 control plane nodes.
- Load balancer in front of API servers.

#### Node Sizing
| Node Size | Pods per Node | Use Case |
|-----------|---------------|----------|
| Small (m5.large, 2 vCPU, 8GB) | 30-50 | Dev/test, low density |
| Medium (m5.xlarge, 4 vCPU, 16GB) | 50-100 | General production |
| Large (m5.4xlarge, 16 vCPU, 64GB) | 100-250 | High density, cost optimization |
| Extra Large (m5.8xlarge+) | 250+ | Batch, heavy workloads |

---

## 4. Service Mesh

### Istio

#### Architecture
```
Data Plane: Envoy sidecar proxies (per pod)
Control Plane: istiod (Pilot, Mixer, Citadel, Galley merged)
```

#### Components
| Component | Purpose | Design Considerations |
|-----------|---------|----------------------|
| Envoy proxy | Data plane — traffic intercept, L7 policies | Sidecar injection (manual/auto), resource overhead |
| istiod | Control plane — config, certificate, telemetry | HA, multiple replicas, leader election |
| Pilot | Service discovery, traffic management | xDS protocol (LDS, RDS, CDS, EDS) |
| Citadel (in istiod) | Certificate management | mTLS, SPIFFE-compatible identities, CA rotation |
| Telemetry (in istiod) | Metrics, access logs | Prometheus integration, adapters deprecated |

#### Traffic Management
| Resource | Purpose | Example |
|----------|---------|---------|
| VirtualService | Traffic routing, retries, fault injection | Route 90% to v1, 10% to v2 |
| DestinationRule | Load balancing, pool settings, mTLS | `trafficPolicy.loadBalancer: ROUND_ROBIN` |
| Gateway | Ingress/egress mesh traffic | TLS termination, host-based routing |
| ServiceEntry | External service integration | Add external MySQL to mesh |
| Sidecar | Proxy configuration per namespace | Limit proxy scope, improve performance |

#### Security
| Feature | Mechanism | Design Pattern |
|---------|-----------|----------------|
| mTLS | Mutual TLS between all mesh services | PERMISSIVE (onboarding) → STRICT |
| AuthorizationPolicy | L7/L4 access control | JWT, IP blocks, methods, paths |
| PeerAuthentication | Service-to-service auth | mTLS mode per namespace |
| RequestAuthentication | End-user auth | JWKS validation, claim mapping |

#### Observability
| Tool | Data | Integration |
|------|------|-------------|
| Prometheus | Metrics (HTTP, TCP, gRPC) | Istio telemetry v2, WASM filters |
| Grafana | Dashboards | Pre-built Istio dashboards |
| Jaeger/Zipkin | Distributed tracing | Trace propagation (B3, W3C) |
| Kiali | Topology, service graphs | Real-time dependency maps |
| Grafana Loki | Access logs | L7 access logs, Envoy's access log format |

### Linkerd

#### Architecture
```
Data Plane: Linkerd2-proxy (micro-proxy per pod, written in Rust)
Control Plane: destination, identity, proxy-injector
```

#### Key Differences vs Istio
| Aspect | Istio | Linkerd |
|--------|-------|---------|
| Proxy | Envoy (C++) | Linkerd2-proxy (Rust) |
| Control plane | istiod (Go) | destination, identity (Go) |
| Resource overhead | 50-100MB per proxy | 10-20MB per proxy |
| Feature set | Extensive (L7, WASM, external auth) | Focused (L7, mTLS, metrics) |
| Performance | Higher latency (1-3ms) | Lower latency (sub-ms) |
| Complexity | High | Low |
| mTLS | Automatic, SPIFFE-based | Automatic, via identity controller |
| Extensions | WASM filters, EnvoyFilter | Linkerd extensions (viz, jaeger, multi-cluster) |

### Consul Connect

#### Architecture
```
Sidecar proxy (Envoy or built-in) + Consul servers + Connect CA
```

#### Key Features
- **Intentions**: Service-to-service access control (allow/deny).
- **Service Mesh**: L4/L7 traffic management via Envoy.
- **Multi-platform**: VMs, containers, K8s, Nomad.
- **Mesh gateways**: Multi-datacenter mesh federation.

### Design Patterns and Trade-offs

| Pattern | Mesh | When to Use |
|---------|------|-------------|
| Zero-trust network | Any | All service-to-service traffic requires mTLS |
| Canary deployment | Istio, Linkerd | Route percentage to new version, automated promotion |
| Fault injection | Istio | Test resilience (delays, aborts) |
| Multi-mesh gateway | Consul, Istio | Services in different clusters/DCs |
| Observability-as-platform | Istio, Linkerd | Centralized metrics, tracing, logs |
| Sidecar-less (ambient mesh) | Istio | Lower overhead, per-node proxy |

---

## 5. Monitoring/Observability Stack

### Prometheus

#### Architecture
```
Targets (scrape) → Prometheus Server → TSDB (local/remote) → Query via PromQL
```
- **Service discovery**: Static, Kubernetes, Consul, EC2, DNS SRV.
- **Alerting**: Recording rules → Alerting rules → Alertmanager → (notify) Email, Slack, PagerDuty.

#### PromQL
| Function | Purpose | Example |
|----------|---------|---------|
| `rate()` | Per-second increase (counters) | `rate(http_requests_total[5m])` |
| `irate()` | Instant rate (spiky data) | `irate(http_requests_total[1m])` |
| `increase()` | Absolute increase | `increase(errors_total[1h])` |
| `histogram_quantile()` | Latency percentiles | `histogram_quantile(0.99, ...)` |
| `avg_over_time()` | Average over window | `avg_over_time(cpu_usage[5m])` |
| `topk()`/`bottomk()` | Top/bottom N series | `topk(5, max_cpu)` |

#### Design Patterns
1. **HA with Thanos**:
   - Thanos Sidecar on each Prometheus.
   - Thanos Query for global view.
   - Thanos Store for long-term retention (S3/GCS).
   - Thanos Compactor for dedup and downsampling.
2. **Federation**:
   - Global Prometheus scrapes aggregated data from regional Prometheus instances.
   - Hierarchical architecture for multi-region.
3. **Cardinality Management**:
   - Avoid high-cardinality labels (user_id, session_id, request_id).
   - Use bounded labels (status_code, endpoint, method).

### Grafana

#### Architecture
```
Data Sources (Prometheus, Loki, Tempo, Elasticsearch, CloudWatch, etc.) 
→ Grafana Server → Dashboards, Alerts, Explore
```

#### Key Features
| Feature | Purpose | Design Pattern |
|---------|---------|----------------|
| Dashboards | Visualization | Jsonnet, Terraform provisioning, folder structure |
| Explore | Ad-hoc querying | PromQL builder, log query, trace view |
| Alerting | Unified alerting | Grafana-managed or Loki/Prometheus-managed rules |
| Provisioning | Automation | YAML config, Terraform provider |
| Reporting | Scheduled PDFs | Enterprise feature |
| RBAC | Access control | Viewer, Editor, Admin, custom roles |

#### Dashboard Design Principles
1. **USE Method**: Utilization, Saturation, Errors (resource-focused).
2. **RED Method**: Rate, Errors, Duration (service-focused).
3. **Four Golden Signals**: Latency, Traffic, Errors, Saturation.
4. **Single Pane of Glass**: Top-level overview → Service-level → Instance-level (drill-down).

### Loki

#### Architecture
```
Agent (Promtail) → Loki (ingester, querier, compactor) → Object Store (S3/GCS)
```

#### Components
| Component | Purpose |
|-----------|---------|
| Promtail | Log collection, label extraction |
| Ingester | Write log streams to object store |
| Querier | Read logs, execute LogQL |
| Compactor | Merge index, retention |
| Distributor | Load balance writes |

#### LogQL
| Query | Purpose |
|-------|---------|
| `{job="api-server"} |= "error"` | Filter by label and content |
| `rate({job="api-server"} |= "error" [5m])` | Error rate per second |
| `avg_over_time({job="api"} |= "slow" \| json \| latency > 500ms` | Average latency over time |

### Tempo (Distributed Tracing)

#### Architecture
```
Distributor → Ingester → Object Store → Querier → Frontend
```

#### Key Concepts
| Concept | Description |
|---------|-------------|
| Trace | End-to-end request path |
| Span | Single operation with timing |
| Trace ID | Correlation ID across spans |
| Context propagation | Pass trace ID between services (W3C TraceContext, B3) |
| Sampling | Head-based (probabilistic), tail-based (latency-focused) |

### ELK Stack

#### Architecture
```
Beats (log shipping) → Logstash (parsing, enrichment) → Elasticsearch (storage, search) → Kibana (UI)
```

| Component | Purpose | Scale Considerations |
|-----------|---------|---------------------|
| Filebeat | Lightweight log forwarding | Module-based (nginx, mysql, system) |
| Metricbeat | System/service metrics | CPU, memory, disk, network per host |
| Heartbeat | Uptime monitoring | ICMP, TCP, HTTP checks |
| Logstash | Data processing pipeline | Grok parsing, geo-enrichment, output plugins |
| Elasticsearch | Full-text search, aggregation | Cluster sizing, shards, ILM, snapshots |
| Kibana | Visualization, discovery | Lens, TSVB, dashboard, alerts, Canvas |

#### Design Patterns
1. **Elastic Cloud/Kubernetes Operator**:
   - ECK (Elastic Cloud on Kubernetes).
   - Automated cluster management, scaling, upgrades.
2. **Index Lifecycle Management (ILM)**:
   - Hot phase (fast SSDs, high replication).
   - Warm phase (standard disks, lower replication).
   - Cold phase (slower storage, read-only).
   - Delete phase (retention expiration).
3. **Cross-Cluster Search**:
   - Federate search across multiple Elastic clusters.
   - Useful for multi-region/multi-tenant.

---

## 6. Secrets Management

### HashiCorp Vault

#### Architecture
```
Vault Clients → API -> Vault Server (storage backend, seal mechanism) 
→ Secrets Engines (KV, AWS, PKI, Transit, Database)
```

#### Key Concepts
| Concept | Purpose | Design Pattern |
|---------|---------|----------------|
| Secret Engine | Manage secrets type | KV v2 (encrypted at rest), Dynamic secrets (short-lived) |
| Auth Method | Identity verification | Token, Kubernetes, LDAP, AWS IAM, JWT/OIDC |
| Policy | Access control (HCL) | `path "secret/data/*" { capabilities = ["read"] }` |
| Seal/Unseal | Encryption key protection | Auto unseal (KMS), Shamir seals, unseal keys |
| Replication | Multi-DC | DR replication, Performance replication |

#### Design Patterns
1. **Dynamic Secrets**:
   - Database credentials: Vault creates short-lived PostgreSQL/MySQL creds.
   - AWS IAM: Vault creates temporary STS credentials.
   - PKI: Vault issues short-lived TLS certificates.
2. **Kubernetes Integration**:
   - Vault Agent Injector: sidecar, automount secrets.
   - CSI Provider: Secrets Store CSI Driver.
   - Kubernetes Auth Method: ServiceAccount-based auth.
3. **Vault HA**:
   - Active/passive cluster with leader election (storage backend).
   - Performance standby for read scaling.

### AWS Secrets Manager

#### Architecture
```
AWS Secrets Manager API → Encrypted with KMS → RBAC via IAM → Automatic rotation
```

#### Key Features
| Feature | Description | Cost Consideration |
|---------|-------------|-------------------|
| Automatic rotation | Lambda-based, built-in for RDS, Redshift | Pay per secret + per rotation |
| Encryption | KMS-managed, customer-managed keys | KMS costs |
| Replication | Cross-region replica secrets | Additional cost per region |
| Random secret generation | Auto-generate passwords | No additional cost |
| Tags and policies | IAM policies, resource tags | IAM complexity |

### Sealed Secrets (Bitnami)

#### Architecture
```
kubeseal (CLI) → SealedSecret CRD → Sealed Secrets Controller → Secret (in cluster)
```

#### Flow
1. User creates a Secret template.
2. `kubeseal` encrypts it using controller's public key → SealedSecret YAML.
3. SealedSecret YAML is safe to store in Git.
4. Controller decrypts it (using private key) → creates regular Secret in cluster.

#### Trade-off
| Pro | Con |
|-----|-----|
| Git-safe encrypted secrets | Secret is decrypted to plaintext Secret (base64) |
| Simple to set up | Key management is manual (backup public/private key) |
| No external dependency | No dynamic secrets (static only) |
| Kubernetes-native CRD | No rotation without manual update |

### External Secrets Operator

#### Architecture
```
ExternalSecrets CRD → Controller → Provider (AWS Secrets Manager, GCP SM, Azure KV, Vault) → Secret (in cluster)
```

#### Key Features
| Feature | Description | Use Case |
|---------|-------------|----------|
| SecretStore | Provider configuration | Namespaced or cluster-wide |
| ExternalSecret | Defines secret mapping | Sync from provider to K8s Secret |
| PushSecret | K8s secret to provider | Bi-directional (advanced) |
| Refresh Interval | Poll for changes | Update K8s Secret when provider changes |

---

## 7. Container Registries & Artifact Management

### Container Registries

| Registry | Features | Use Case |
|----------|----------|----------|
| Docker Hub | Public/private, automated builds, webhooks | Open source, small teams |
| GitHub Container Registry | Integrated with GitHub, fine-grained permissions | GitHub-centric workflow |
| GitLab Container Registry | Integrated with GitLab CI, built-in | GitLab-centric workflow |
| AWS ECR | IAM integration, image scanning, replication | AWS-native |
| Azure Container Registry | Tasks, geo-replication, OCI artifacts | Azure-native |
| Google Container Registry (deprecated) | Being replaced by Artifact Registry | GCP-native |
| Harbor | Vulnerability scanning, replication, RBAC, immutable tags | Enterprise, multi-cloud |
| Quay | Robot accounts, build triggers, CLA scanning | Enterprise, security |

### Design Patterns

1. **Image Tagging Strategy**:
   - `git-sha` (immutable, traceable).
   - `branch-name` (mutable, latest of branch).
   - `semver` (releases).
   - Avoid `latest` tag in production.
2. **Cross-Region Replication**:
   - ECR, ACR, and Harbor support geo-replication.
   - Pull through cache for edge locations.
3. **Vulnerability Scanning**:
   - Scan on push, on schedule.
   - Integrate with CI/CD (fail on critical vulnerabilities).
   - Policies to block deployment based on scan results.
4. **Image Cleanup**:
   - Retention policies (untagged images, age-based).
   - Lifecycle rules (ECR, ACR, Harbor).
   - Avoid storage cost explosion.

### Dependency Management

#### Artifact Repositories
| Tool | Formats | Use Case |
|------|---------|----------|
| JFrog Artifactory | All (Maven, npm, Docker, PyPI, Helm, Terraform) | Enterprise universal |
| Sonatype Nexus | Maven, npm, PyPI, Docker, NuGet, Raw | Java-centric teams |
| GitHub Packages | npm, Docker, Maven, NuGet, RubyGems | GitHub-native |
| GitLab Packages | npm, Maven, PyPI, NuGet, Conan | GitLab-native |

#### Design Patterns
1. **Proxy vs Hosted Repos**:
   - Proxy: Cache external dependencies (npmjs.org, Maven Central).
   - Hosted: Your own packages.
   - Virtual: Merge proxy + hosted for consumer simplicity.
2. **CI Integration**:
   - CI builds push to hosted repo.
   - CI pulls from proxy repo (with cache).
   - Immutable versions (never delete published versions).
3. **Promotion**:
   - Package promotion: dev → staging → prod.
   - Only promoted packages can be deployed.
   - Prevent rollback to unverified version.

---

## 8. General Distributed Systems Patterns for DevOps

### CAP / PACELC

| Theorem | Meaning | Impact on DevOps |
|---------|---------|------------------|
| CAP | Consistency + Availability + Partition Tolerance — pick 2 | CP (etcd, ZK) vs AP (Cassandra, DynamoDB) |
| PACELC | If partition (P), tradeoff C vs A. Else (E), tradeoff L vs C | Realistic: most systems pick different tradeoffs |

### Replication Patterns

| Pattern | Description | Examples |
|---------|-------------|----------|
| Single-leader | One writable, many readable | PostgreSQL, MySQL, MongoDB |
| Multi-leader | Multiple writable nodes | DynamoDB, Cassandra, Spanner |
| Leaderless | Any node can accept writes | Cassandra, Riak |
| Quorum | W + R > N for consistency | Cassandra, etcd |

### Consensus Algorithms
| Algorithm | Type | Use Case |
|-----------|------|----------|
| Paxos | Both strong & weak | Theoretical foundation, complex |
| Raft | Strong | etcd, Consul, Vault, Kafka (KRaft) |
| Zab | Strong | Zookeeper |
| Gossip | Weak | Consul (membership), Cassandra |
| Viewstamped Replication | Strong | Old but simpler than Paxos |

### Caching Patterns
| Pattern | Description | Use Case |
|---------|-------------|----------|
| Cache-aside | App loads from cache, falls back to DB | Read-heavy workloads |
| Write-through | Write to cache, sync to DB | Write-heavy with consistency |
| Write-behind | Write to cache, async to DB | High throughput, eventual consistency |
| Read-through | Cache loads from DB on miss | Simplify application logic |
| Refresh-ahead | Cache proactively refreshes | Predictable access patterns |

### Rate Limiting
| Algorithm | Pros | Cons |
|-----------|------|------|
| Token Bucket | Burst support | Memory per user |
| Leaky Bucket | Smooth output | Strict rate, no burst |
| Sliding Window | Precise per window | Clock skew issues |
| Fixed Window Counter | Simple, low memory | Traffic spikes at boundaries |
| Sliding Window Log | Most accurate | Memory-intensive |

### Load Balancing Algorithms
| Algorithm | Behavior | Use Case |
|-----------|----------|----------|
| Round Robin | Sequential distribution | Stateless, even load |
| Least Connections | Fewest active connections | Long-lived connections |
| Weighted Round Robin | Capacity-based weights | Heterogeneous servers |
| IP Hash | Consistent mapping | Session persistence |
| Consistent Hashing | Minimal rehashing | Distributed caching |

---

_End of SYSTEM_DESIGN_CHEATSHEET.md_