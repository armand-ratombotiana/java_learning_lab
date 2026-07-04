# Architecture — Kubernetes

## Microservice Deployment Architecture

```
                        ┌─────────────────────┐
                        │   Ingress Controller │
                        │   (ALB / nginx)      │
                        └──────────┬──────────┘
                                   │
              ┌────────────────────┼────────────────────┐
              │                    │                    │
         ┌────▼────┐         ┌─────▼─────┐        ┌─────▼─────┐
         │ Service   │         │ Service   │        │ Service   │
         │ API GW    │         │ User      │        │ Order     │
         │(Deploy 2) │         │(Deploy 3) │        │(Deploy 3) │
         │           │         │           │        │           │
         │ ConfigMap │         │ Secrets   │        │ HPA (CPU) │
         └────┬──────┘         └─────┬─────┘        └─────┬─────┘
              │                      │                    │
              │              ┌───────▼───────┐            │
              │              │ PostgreSQL    │            │
              │              │ (StatefulSet) │            │
              │              │ 3 pods, 1 PVC │            │
              │              └───────────────┘            │
              │                                           │
              │              ┌───────────────────────────┐│
              │              │  Redis Cache               ││
              │              │  (StatefulSet)             ││
              │              │  3 pods, headless svc     ││
              └──────────────┴───────────────────────────┘

Namespaces:
  prod
  staging
  dev
  monitoring (Prometheus, Grafana)
  ingress-nginx
  kube-system
```

## GitOps with ArgoCD

```
Developer pushes to Git
       │
       ▼
┌─────────────────┐
│    Git Repo      │
│  (git.example.   │
│   com/app)       │
└────────┬────────┘
         │ webhook
         ▼
┌─────────────────┐
│  ArgoCD          │
│  (runs in k8s)   │
└────────┬────────┘
         │ sync
         ▼
┌─────────────────┐
│  K8s Cluster     │
│  Desired State = │
│  Git Repo State  │
└─────────────────┘

Benefits:
  - Git = Single source of truth
  - Automatic sync (or manual approval)
  - Rollback = revert Git commit
  - Drift detection (corrects manual changes)
```

## Observability Stack

```
           ┌─────────────────────────────────────┐
           │  Metrics: Prometheus                  │
           │   → Scrapes pods via annotations      │
           │   → ServiceMonitor CRD               │
           │                                      │
           │  Logs: Loki + Fluent Bit              │
           │   → Sidecar collects container logs   │
           │   → Ships to Loki (S3 backend)       │
           │                                      │
           │  Tracing: Tempo / Jaeger              │
           │   → OpenTelemetry agent (sidecar)     │
           │   → Traces stored in S3              │
           │                                      │
           │  Visualization: Grafana               │
           │   → Dashboards per service           │
           │   → Alerts (PagerDuty, Slack)        │
           └─────────────────────────────────────┘
```

## Helm Chart Structure

```
spring-app/
├── Chart.yaml          # Metadata (name, version, dependencies)
├── values.yaml         # Default configuration values
├── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── configmap.yaml
│   ├── hpa.yaml
│   └── _helpers.tpl    # Reusable template helpers
└── charts/             # Subchart dependencies (if any)
```

```bash
# Deploy with Helm
helm install spring-app ./spring-app \
  --set image.tag=1.0.0 \
  --set replicaCount=5 \
  --namespace prod
```
