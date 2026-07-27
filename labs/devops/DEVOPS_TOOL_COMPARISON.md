# DevOps Tool Comparison — Interview Guide

> Tool comparisons, trade-offs, and when-to-use for DevOps/SRE/Platform interviews.

---

## Table of Contents
1. Container Runtimes: Docker vs containerd vs Podman
2. Orchestrators: Kubernetes vs Nomad vs Docker Swarm
3. IaC: Terraform vs Pulumi vs Crossplane
4. Config Management: Ansible vs Chef vs Puppet
5. Monitoring: Prometheus vs Datadog vs New Relic
6. GitOps: ArgoCD vs Flux
7. Packaging: Helm vs Kustomize
8. Service Mesh: Istio vs Linkerd vs Consul Connect
9. CI/CD: Jenkins vs GitHub Actions vs GitLab CI
10. Secrets: Vault vs AWS Secrets Manager vs Sealed Secrets

---

## 1. Container Runtimes: Docker vs containerd vs Podman

### Docker
**Type**: Full container platform (CLI + daemon + build + runtime).

| Pro | Con |
|-----|-----|
| Most popular, largest ecosystem | Monolithic daemon (dockerd) |
| Easy to use, `docker compose` | Requires root (by default) |
| Extensive documentation | Daemon can be a single point of failure |
| Vast image ecosystem on Docker Hub | Slower cold start than containerd |
| `docker build`, `docker push`, `docker run` all-in-one | Security: daemon runs as root |

**When to use**:
- Local development and testing.
- CI/CD where simplicity matters.
- Teams already invested in Docker tooling.
- You need Docker Compose for multi-container apps.

### containerd
**Type**: Core container runtime (OCI-compliant). Industry standard for Kubernetes.

| Pro | Con |
|-----|-----|
| Lightweight, minimal | No Docker CLI (need ctr, nerdctl) |
| OCI-compliant | Less developer-friendly UI |
| Used by Kubernetes (since v1.24) | No built-in compose support |
| Better performance than dockerd | Smaller community for non-K8s use |
| Daemonless options available | Image building requires separate tool |

**When to use**:
- Kubernetes nodes (default runtime since v1.24).
- Production container workloads without Docker daemon overhead.
- Edge/IoT devices with limited resources.

### Podman
**Type**: Daemonless container engine (rootless by default).

| Pro | Con |
|-----|-----|
| Rootless by default (security) | Smaller ecosystem |
| Daemonless architecture | Less mature than Docker |
| Docker-compatible CLI (alias docker=podman) | Some Docker features missing |
| Kubernetes YAML generation (`podman generate kube`) | Windows/macOS support via VM |
| Systemd integration | Build performance can be slower |

**When to use**:
- Security-sensitive environments (rootless containers).
- Running containers as systemd services.
- Teams wanting Docker-like experience without daemon.
- K8s manifest generation from running containers.

### Comparison Matrix

| Feature | Docker | containerd | Podman |
|---------|--------|------------|--------|
| Architecture | Daemon-based | Daemon-based (lightweight) | Daemonless (fork/exec) |
| Rootless | Optional (rootful by default) | Via userns-remap | Default |
| OCI-compliant | Yes | Yes | Yes |
| Docker CLI | Native | Via nerdctl | Compatible (alias) |
| Compose | docker compose | nerdctl compose | podman-compose |
| K8s Integration | Deprecated (dockershim removed) | Default CRI runtime | Via CRI-O |
| Image Building | Built-in | Via buildkit | Built-in (buildah) |
| Resource Usage | Higher | Lower | Lowest |
| Maturity | Most mature | Very mature | Growing |

### Interview Soundbite
> "Docker is great for development because of its tooling and ecosystem. For production Kubernetes, containerd is the standard — it's lighter and built for the CRI interface. Podman is emerging as a more secure alternative for edge deployments where rootless operation matters."

---

## 2. Orchestrators: Kubernetes vs Nomad vs Docker Swarm

### Kubernetes
**Type**: Production-grade container orchestrator (CNCF graduated).

| Pro | Con |
|-----|-----|
| Massive ecosystem, industry standard | Extremely complex |
| Self-healing, auto-scaling | Steep learning curve |
| Extensible (CRDs, operators) | Resource-heavy control plane |
| Multi-cloud, hybrid deployments | Upgrade complexity |
| Rich networking (CNI) and storage (CSI) | Overkill for simple workloads |
| Service mesh, GitOps ecosystem | Cluster management overhead |

**When to use**:
- Microservices architectures (>5 services).
- Multi-team, multi-service platform.
- Complex networking and storage requirements.
- Need for auto-scaling, self-healing, rolling updates.

### Nomad (HashiCorp)
**Type**: Simple, flexible orchestrator (single binary).

| Pro | Con |
|-----|-----|
| Simple to install and operate | Smaller ecosystem |
| Multi-platform (Docker, VM, raw exec, Java) | Less built-in functionality |
| Single binary (server + client) | Fewer integrations (no built-in CNI) |
| Integrates with Consul + Vault | Smaller community |
| Performance: 10K+ nodes per cluster | Missing many K8s features (no built-in Ingress) |

**When to use**:
- Simple workload scheduling without need for K8s complexity.
- Multi-platform workloads (containers + VMs + batch).
- Small to medium clusters.
- HashiCorp ecosystem shops.

### Docker Swarm
**Type**: Docker-native orchestration (built into Docker Engine).

| Pro | Con |
|-----|-----|
| Simple to set up (docker swarm init) | Limited features vs K8s |
| Integrated with Docker CLI | No auto-scaling |
| Declarative service model | Smaller community (declining) |
| Low learning curve for Docker users | Limited networking options |
| Built-in load balancing (routing mesh) | No CRDs/operators |

**When to use**:
- Small teams already using Docker.
- Simple deployment scenarios (single service).
- Development/staging environments.
- When Kubernetes is overkill.

### Comparison Matrix

| Feature | Kubernetes | Nomad | Docker Swarm |
|---------|-----------|-------|-------------|
| Installation complexity | High | Low | Very low |
| Learning curve | Steep | Moderate | Shallow |
| Auto-scaling | Native (HPA, VPA) | Via job scaling | Not native |
| Service discovery | Service + DNS | Consul required | Built-in DNS |
| Networking | CNI (Calico, Cilium, etc.) | Simple port mapping | Routing mesh |
| Storage | CSI, built-in volumes | Host volumes, CSI | Volumes, bind mounts |
| Batch scheduling | Via Job, CronJob | Native batch support | Limited |
| Multi-region | Via KubeFed | Native multi-region | Not supported |
| Enterprise adoption | Most common | Growing | Declining |
| Upgrade complexity | High | Moderate | Low |

### Interview Soundbite
> "For most production environments, Kubernetes is the right choice due to its ecosystem and flexibility. But if you need a simple schedule-and-forget approach, or you're running batch jobs on mixed workloads, Nomad is a strong alternative. Docker Swarm is fine for small-scale Docker-native setups, but I wouldn't start a new project on it today."

---

## 3. IaC: Terraform vs Pulumi vs Crossplane

### Terraform (HashiCorp)
**Type**: Declarative infrastructure provisioning tool (HCL DSL).

| Pro | Con |
|-----|-----|
| Mature, largest provider ecosystem | HCL DSL — another language to learn |
| Immutable infrastructure | State management complexity |
| `plan`/`apply` workflow | Slow for large configurations |
| Multiple state backends | Not ideal for mutable config (use Ansible) |
| Sentinel for policy as code | Module registry can be inconsistent |

**When to use**:
- Provisioning cloud infrastructure (AWS, GCP, Azure).
- Multi-cloud infrastructure management.
- Teams familiar with declarative configuration.
- External infrastructure for Kubernetes clusters.

### Pulumi
**Type**: Infrastructure as code with real programming languages.

| Pro | Con |
|-----|-----|
| Real programming languages (TS, Python, Go, .NET, Java) | Smaller community |
| Type safety, code reuse, loops, conditionals | Provider parity with Terraform is behind |
| Strong testing capabilities (unit tests for infra) | State management still required |
| Native package management (npm, pip, go mod) | Build/compile step required |
| Automation API for embedding IaC | Less mature policy as code |

**When to use**:
- Teams that prefer TypeScript/Python over HCL.
- Complex infrastructure logic (conditional resources, loops).
- Integrating IaC into application codebases.
- Strong type checking and IDE support needed.

### Crossplane (Upbound/CNCF)
**Type**: Kubernetes-native infrastructure provisioning.

| Pro | Con |
|-----|-----|
| Kubernetes-native (CRDs, controllers) | Requires Kubernetes cluster |
| GitOps-native (manage via ArgoCD) | Less mature than Terraform |
| Platform engineering abstraction layer | Provider coverage is smaller |
| No external state management | In-cluster resource overhead |
| Compositions for reusable patterns | Complex initial setup |

**When to use**:
- Platform engineering teams building internal developer platforms.
- Organizations already invested in Kubernetes.
- GitOps-first organizations.
- Need to provide self-service infrastructure to application teams.

### Comparison Matrix

| Feature | Terraform | Pulumi | Crossplane |
|---------|-----------|--------|------------|
| Language | HCL | TypeScript, Python, Go, .NET, Java | Kubernetes YAML/CRDs |
| State management | Backend (S3, Consul, etc.) | Backend (S3, GCS, etc.) | Kubernetes-native (etcd) |
| Provider count | 2000+ | 1000+ | 100+ |
| Plan/apply | Yes | Preview/up | Kubernetes reconciliation |
| Policy as code | Sentinel | Crossguard, Pulumi Policy | OPA, Kyverno |
| GitOps integration | Manual (ArgoCD + Terraform controller) | Native (Automation API) | Native (CRDs in Git) |
| Learning curve | Moderate | Moderate (language-dependent) | High |
| Maturity | Very high | High | Moderate |

### Interview Soundbite
> "Terraform remains the industry standard — it's battle-tested with the largest provider ecosystem. Pulumi is excellent if you prefer real programming languages and want type safety. Crossplane is the future of platform engineering — it brings infrastructure into the Kubernetes control plane and enables self-service through GitOps."

---

## 4. Config Management: Ansible vs Chef vs Puppet

### Ansible (Red Hat)
**Type**: Agentless configuration management (SSH/pull-based).

| Pro | Con |
|-----|-----|
| Agentless (SSH) | Slower for large fleets (SSH overhead) |
| Easy to learn (YAML) | No native state enforcement (idempotency via playbook design) |
| Extensive module library | Windows support is weaker than Linux |
| Push-based (or pull via ansible-pull) | Complex workflows need AWX/Tower |
| No central server needed (for ad-hoc) | Not ideal for continuous compliance |

### Chef
**Type**: Agent-based configuration management (Ruby DSL).

| Pro | Con |
|-----|-----|
| Strong compliance (InSpec, Chef Automate) | Ruby DSL learning curve |
| Client-server architecture | Chef Server is complex to operate |
| Idempotent resources | Heavier agent than Ansible |
| Test kitchen for testing cookbooks | Smaller community than Ansible |
| Strong Windows support | Declining adoption |

### Puppet
**Type**: Agent-based configuration management (declarative DSL).

| Pro | Con |
|-----|-----|
| Declarative DSL (resources, classes) | Ruby DSL (custom) |
| Strong reporting and enforcement | PuppetServer complexity |
| Resource abstraction layer | Agent overhead |
| PuppetDB for inventory and facts | Declining adoption |
| Forge module ecosystem | Windows support weaker than Chef |

### Comparison Matrix

| Feature | Ansible | Chef | Puppet |
|---------|--------|------|--------|
| Architecture | Agentless (SSH/WinRM) | Agent (client-server) | Agent (client-server) |
| Language | YAML | Ruby DSL | Ruby-based DSL |
| Pull/Push | Push (default), Pull (ansible-pull) | Pull | Pull |
| Idempotency | Playbook-dependent | Built-in resources | Built-in resources |
| Compliance | Limited | InSpec (strong) | Compliance enforcement |
| Learning curve | Low | Moderate | Moderate |
| Windows support | Good | Best | Moderate |
| Community size | Largest | Declining | Declining |
| Server required | No (AWX optional) | Yes (Chef Server) | Yes (PuppetServer) |
| Best for | Config management, automation | Compliance-heavy orgs | Large enterprise config |

### Interview Soundbite
> "Ansible is my default choice for configuration management — it's agentless, easy to learn, and integrates well with cloud and networking. Chef still has strong compliance features with InSpec. Puppet's resource abstraction layer is powerful but both Chef and Puppet are losing share to Ansible and Terraform (for provisioning)."

---

## 5. Monitoring: Prometheus vs Datadog vs New Relic

### Prometheus (CNCF)
**Type**: Open-source metrics monitoring + alerting.

| Pro | Con |
|-----|-----|
| Open source, CNCF graduated | Limited to metrics (no built-in logs/traces) |
| Pull-based (simpler, secure) | Complex to scale globally |
| Powerful PromQL query language | Storage limitations (local TSDB) |
| Kubernetes-native service discovery | No built-in high availability (need Thanos) |
| Alertmanager for alerting | UI is basic (need Grafana) |
| Large exporter ecosystem | Long-term storage requires external solution |

**When to use**:
- Kubernetes-native monitoring.
- Teams wanting open-source with customization.
- Metrics-focused monitoring (with Grafana for visualization, Loki/Tempo for logs/traces).
- On-premise or air-gapped environments.

### Datadog
**Type**: SaaS observability platform (metrics, logs, traces, APM, RUM).

| Pro | Con |
|-----|-----|
| All-in-one (metrics, logs, traces, APM, RUM) | Expensive at scale |
| 500+ integrations out of the box | Vendor lock-in |
| Easy to set up for cloud-native | Agent resource overhead |
| Machine learning anomaly detection | Data retention requires higher tiers |
| Dashboard sharing, mobile app | On-premise option limited |
| Excellent support | Learning Datadog-specific queries |

**When to use**:
- Budget allows SaaS costs.
- Teams want single pane of glass (log/metrics/traces in one tool).
- Need out-of-the-box cloud integrations.
- Less operational overhead (no infrastructure to manage).

### New Relic
**Type**: SaaS observability platform (MELT — Metrics, Events, Logs, Traces).

| Pro | Con |
|-----|-----|
| NRQL query language (powerful) | Usage-based pricing can be unpredictable |
| Free tier (100GB/month ingest) | UI changes frequently |
| APM agent is mature | NRQL learning curve |
| Distributed tracing (tail-based sampling) | Less Kubernetes-native than Datadog |
| Entity relationship graph | Agent overhead similar to Datadog |

**When to use**:
- Need free tier for small-scale monitoring.
- Strong APM requirements (application-centric).
- Teams comfortable with SQL-like query language.
- Build-your-own-dashboard culture.

### Comparison Matrix

| Feature | Prometheus | Datadog | New Relic |
|---------|-----------|---------|-----------|
| Type | Open source | SaaS | SaaS |
| Metrics | Native | Integrated | Integrated |
| Logs | Export to Loki | Native log management | Native log management |
| Traces | Via Tempo/OpenTelemetry | APM - distributed traces | APM - distributed traces |
| Query language | PromQL | DogStatsD + Log queries | NRQL |
| Alerting | Alertmanager | Alerts + AI | Alerts + anomaly |
| Cost | Free (self-hosted) | $$$ (per host, per metric) | $$ (usage-based) |
| K8s integration | Native (kube-prometheus-stack) | Strong (Operator, DaemonSet) | Moderate (Operator) |
| Uptime/Synthetics | Blackbox exporter | Integrated (Synthetics) | Integrated (Synthetics) |
| RUM | Not native | Real User Monitoring | Browser monitoring |

### Interview Soundbite
> "Prometheus is the gold standard for Kubernetes-native metrics — it's open-source, has a powerful query language, and a large ecosystem. For SaaS, Datadog offers the best all-in-one experience with 500+ integrations and excellent APM. New Relic has a strong free tier and powerful NRQL queries. I usually recommend Prometheus + Grafana for cost control and Datadog for teams that want to focus on operations rather than tool maintenance."

---

## 6. GitOps: ArgoCD vs Flux

### ArgoCD
**Type**: Declarative GitOps for Kubernetes (CNCF graduated).

| Pro | Con |
|-----|-----|
| Web UI is excellent | Complexity with App of Apps pattern |
| ApplicationSet for multi-cluster | Sync options can be confusing |
| SSO integration | RBAC can be complex |
| Rich health checks | Large number of CRDs |
| Extensive CLI (`argocd`) | Repo server caching issues at scale |
| Can manage non-K8s resources (config management plugin) | Multi-cluster management is via ApplicationSet |

### Flux
**Type**: GitOps toolkit for Kubernetes (CNCF graduated).

| Pro | Con |
|-----|-----|
| Simpler architecture (source, kustomize, helm, notification controllers) | Less mature UI |
| Multi-tenancy via namespaces | Health checks less rich |
| Notification controller (Slack, Teams) | Kustomize-first (Helm via HelmRelease) |
| Weave GitOps UI (paid) | Less ecosystem |
| Image update automation (ImageUpdateAutomation) | Learning curve for Flux-specific concepts |
| Kustomize-native | Documentation can be overwhelming |

### Comparison Matrix

| Feature | ArgoCD | Flux |
|---------|--------|------|
| Architecture | Single controller + API | Multiple controllers |
| SSO | Native (OIDC, SAML, Dex, GitHub) | Via Weave GitOps (paid) |
| Multi-cluster | ApplicationSet generators | Kustomization + Clusters |
| UI | Excellent | Basic (Weave GitOps paid) |
| Helm | Via Helm chart or repo | HelmRelease CRD |
| Kustomize | Native support | Native + Kustomization CRD |
| Notifications | Webhook, API | Notification controller |
| Pruning | Automatic | Automatic |
| Image updates | Argocd-image-updater (external) | ImageUpdateAutomation (built-in) |
| Learning curve | Moderate | Moderate-High |

### Interview Soundbite
> "ArgoCD has the best UI and the richest feature set for GitOps — the ApplicationSet pattern is powerful for multi-cluster management. Flux has a cleaner architecture with multiple independent controllers and built-in image update automation. I usually recommend ArgoCD for teams that want a rich UI and SSO, and Flux for teams that want a simpler, more Kubernetes-native approach."

---

## 7. Packaging: Helm vs Kustomize

### Helm
**Type**: Kubernetes package manager (templating engine).

| Pro | Con |
|-----|-----|
| Templating — single chart for multiple environments | Template debugging is hard |
| Chart repository (ArtifactHub) | Template complexity (`{ .Values.something }}`) |
| Release management (install, upgrade, rollback) | No native diff against cluster |
| Dependency management (subcharts) | Custom resource validation limited |
| Helmfile for multi-chart deployments | Chart upgrades can be destructive |

### Kustomize
**Type**: Kubernetes-native configuration customization.

| Pro | Con |
|-----|-----|
| Native Kubernetes (kubectl -k) | No templating (patching only) |
| No template syntax (plain YAML with overlays) | Learning curve for patches (strategic merge, JSON patch) |
| Base + overlay pattern | No release management |
| Patches, transformers, generators | No chart repository concept |
| Built into kubectl | Complex transformations can be confusing |

### Comparison Matrix

| Feature | Helm | Kustomize |
|---------|------|-----------|
| Templating | Go templates | Patches (no template) |
| Base/overlay | Subcharts | Base + overlay |
| Diff | helm diff plugin | kubectl diff |
| Testing | Helm test | No native testing |
| Rollback | Built-in (helm rollback) | No native rollback |
| Registry | ArtifactHub | OCI (v2) |
| Dependency mgmt | Requirements.yaml | No native |
| Secret management | SOPS plugin | Sealed Secrets |
| Learning curve | Steep | Moderate |

### Interview Soundbite
> "Helm is better for packaging and distributing applications (third-party charts, versioned releases). Kustomize is better for customizing your own infrastructure without template complexity. I often use both: Kustomize for base infrastructure overlays and Helm for packaged applications with release management needs."

---

## 8. Service Mesh: Istio vs Linkerd vs Consul Connect

### Istio
**Type**: Feature-rich service mesh (Envoy-based).

| Pro | Con |
|-----|-----|
| Most features (traffic mgmt, security, observability) | High resource overhead (Envoy per pod) |
| WASM extensibility | Complex installation and operation |
| mTLS, authorization policies (L7) | Steep learning curve |
| Multi-cluster mesh | Upgrade complexity |
| Ambient mesh (sidecar-less option) | Debugging is difficult |

**When to use**:
- Complex traffic management (canary, fault injection, mirroring).
- Zero-trust security with L7 authorization.
- Multi-cluster mesh networking.

### Linkerd
**Type**: Ultralight service mesh (Rust proxy).

| Pro | Con |
|-----|-----|
| Low resource overhead (10-20MB/proxy) | Fewer features than Istio |
| Simple to install (linkerd install) | No WASM extensibility |
| Automatic mTLS | No L7 authorization (L4 only) |
| Kubernetes-native (CRDs) | Smaller ecosystem |
| Fast control plane | Less customizable |

**When to use**:
- Teams wanting simple, low-overhead service mesh.
- mTLS and basic observability without complexity.
- Smaller clusters where Istio is overkill.

### Consul Connect (HashiCorp)
**Type**: Service mesh with multi-platform support.

| Pro | Con |
|-----|-----|
| Multi-platform (K8s, VMs, Nomad) | Less K8s-native |
| Integrated with HashiCorp ecosystem | Consul server overhead |
| Intentions for access control | Fewer traffic management features |
| Service discovery + mesh in one | Smaller community |
| Mesh gateways for multi-DC | Learning curve for non-K8s users |

**When to use**:
- HashiCorp ecosystem (Consul, Vault, Nomad).
- Multi-platform services (K8s + VMs).
- Multi-datacenter mesh.

### Comparison Matrix

| Feature | Istio | Linkerd | Consul Connect |
|---------|-------|---------|----------------|
| Proxy | Envoy (C++) | Linkerd2-proxy (Rust) | Envoy or built-in |
| Resource overhead | 50-100MB | 10-20MB | 30-60MB |
| mTLS | Automatic + manual | Automatic | Automatic |
| L7 auth | AuthorizationPolicy | No | Via intentions |
| Traffic splitting | VirtualService | ServiceProfiles | Service splitter |
| Multi-cluster | Mesh federation | Service mirroring | Mesh gateways |
| WASM | Yes | No | No |
| Observability | Rich (Kiali, Jaeger) | Good (viz extension) | Via Consul UI |

### Interview Soundbite
> "Istio is the most feature-rich mesh — I'd use it when advanced traffic management or L7 security policies are needed. Linkerd is the simplest mesh with the lowest overhead — great for mTLS and basic observability. Consul Connect is best in multi-platform environments or HashiCorp-centric shops."

---

## 9. CI/CD: Jenkins vs GitHub Actions vs GitLab CI

### Jenkins
**Type**: Extensible CI/CD automation server.

| Pro | Con |
|-----|-----|
| Extremely flexible (any workflow) | High maintenance overhead |
| 1800+ plugins | Groovy DSL is hard to debug |
| Any agent (Linux, Windows, Mac, K8s) | UI is dated |
| Shared libraries for reuse | Plugin compatibility issues |
| Pipeline as Code (Jenkinsfile) | Scaling master is complex |

**When to use**:
- Complex build pipelines requiring custom plugins.
- Multi-platform builds (Linux, Windows, Mac).
- Organizations with existing Jenkins investment.

### GitHub Actions
**Type**: Built-in CI/CD for GitHub repositories.

| Pro | Con |
|-----|-----|
| Tight GitHub integration | Vendor lock-in (GitHub) |
| Marketplace with 10K+ actions | Limited to GitHub repos |
| Matrix builds | Debugging large YAML is hard |
| Composite actions for reuse | Windows/macOS minute limits |
| Self-hosted runners | Complex workflows become unwieldy |

**When to use**:
- Teams already using GitHub.
- Open source projects (2000 min/month free).
- Simple to moderate CI/CD needs.

### GitLab CI
**Type**: Built-in CI/CD for GitLab repositories.

| Pro | Con |
|-----|-----|
| Integrated with GitLab (repo + CI + registry) | EE features are expensive |
| DAG pipelines (needs) | Runner maintenance |
| Auto DevOps | YAML rules complexity |
| Built-in container registry | Resource-intensive for large pipelines |
| Review apps | Scaling shared runners |

**When to use**:
- Teams already using GitLab.
- Complex pipeline DAGs.
- Need integrated container registry and packages.

### Comparison Matrix

| Feature | Jenkins | GitHub Actions | GitLab CI |
|---------|---------|---------------|-----------|
| Configuration | Jenkinsfile (Groovy) | YAML (GitHub) | YAML (GitLab) |
| Marketplace | 1800+ plugins | 10K+ actions | Templates (limited) |
| Runners | Self-hosted (any) | GitHub-hosted + self-hosted | GitLab-hosted + self-hosted |
| Container support | Docker, K8s | Docker, K8s | Docker, K8s |
| Multi-platform | Excellent | Good (via matrix) | Good (via tags) |
| DAG pipelines | Via plugins | via `needs` | native (needs) |
| Secrets management | Credentials plugin | Built-in (org/repo/env) | Built-in (CI/CD variables) |
| Maintenance | High | Low (SaaS) | Moderate |

### Interview Soundbite
> "GitHub Actions is my go-to for GitHub-based projects — low maintenance and great marketplace. GitLab CI has the best DAG support and integrated registry. Jenkins gives maximum flexibility but comes with significant maintenance costs — I'd only choose it for complex build pipelines that can't be handled by SaaS CI/CD."

---

## 10. Secrets: Vault vs AWS Secrets Manager vs Sealed Secrets

### HashiCorp Vault
**Type**: Comprehensive secrets management platform.

| Pro | Con |
|-----|-----|
| Multi-platform (any secret type, any cloud) | Operational complexity |
| Dynamic secrets (short-lived, auto-expire) | HA configuration is complex |
| Encryption as a service (transit engine) | Storage backend configuration |
| PKI (certificate management) | Audit log management |
| Replication (DR + performance) | UI is functional not pretty |

**When to use**:
- Multi-cloud or hybrid environments.
- Dynamic secrets (database creds, AWS IAM).
- PKI/TLS certificate management.
- Encryption as a service (transit).

### AWS Secrets Manager
**Type**: AWS-native secrets management.

| Pro | Con |
|-----|-----|
| Native AWS integration | AWS-only |
| Automatic rotation (RDS, DocumentDB, Redshift) | Cost ($0.40/secret/month + rotation) |
| IAM integration | No dynamic secrets outside AWS |
| Cross-region replication | Limited to AWS compute |
| Encryption with KMS | No PKI engine |

**When to use**:
- AWS-only infrastructure.
- RDS/Redshift credential rotation.
- Teams already invested in AWS IAM.

### Sealed Secrets (Bitnami)
**Type**: Git-safe encrypted Kubernetes secrets.

| Pro | Con |
|-----|-----|
| Git-safe — store encrypted secrets in repo | Decrypts to plaintext Secret (base64) in cluster |
| Simple setup | No rotation without manual update |
| Kubernetes-native CRD | No dynamic secrets |
| No external dependencies | Key management (backup public/private key) |
| Open source | Limited to Kubernetes |

**When to use**:
- GitOps workflows (store secrets alongside manifests).
- Small teams needing encrypted secrets in Git.
- No external secrets service available.

### Comparison Matrix

| Feature | Vault | AWS Secrets Manager | Sealed Secrets |
|---------|-------|-------------------|----------------|
| Type | Standalone platform | Cloud-managed | Kubernetes CRD |
| Dynamic secrets | Yes (DB, cloud, PKI) | No (static, auto-rotate) | No |
| Multi-cloud | Yes | AWS-only | K8s-only |
| PKI engine | Yes | No | No |
| Encryption transit | Yes (transit engine) | No | No |
| Audit logging | Detailed | CloudTrail | K8s audit |
| Rotation | Manual or scripted | Automatic (DB) | Manual |
| Complexity | High | Low | Very low |
| Cost | Free (operational cost) | $0.40/secret/month | Free |

### Interview Soundbite
> "Vault is the most comprehensive — dynamic secrets, PKI, and encryption as a service make it ideal for security-conscious organizations. AWS Secrets Manager is great for AWS-native shops that want automatic rotation with minimal operational overhead. Sealed Secrets is perfect for GitOps teams that need a simple way to encrypt secrets in their repo without an external service."

---

## Decision Flowchart Summary

```
Q: Where does your app run?
├── Kubernetes only
│   ├── Need GitOps? → ArgoCD or Flux
│   ├── Need simple packaging? → Kustomize
│   ├── Need packaged apps? → Helm
│   └── Need service mesh?
│       ├── Full features → Istio
│       ├── Simple/light → Linkerd
│       └── Multi-platform → Consul
├── Cloud-native (AWS/GCP/Azure)
│   ├── See Terraform vs Pulumi vs Crossplane
│   └── See monitoring: Prometheus vs Datadog
├── Hybrid/on-prem
│   ├── Vault for secrets
│   └── Ansible for config mgmt
└── Simple/monolith
    ├── Docker Compose + Nomad
    └── Prometheus + Grafana
```

---

_End of DEVOPS_TOOL_COMPARISON.md_