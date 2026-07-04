# Visual Guide to GitOps

## GitOps Workflow
```
┌─────────────────────────────────────────────────────────────┐
│                         Git Repository                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │K8s YAML  │  │ Helm     │  │Kustomize │  │ App      │  │
│  │ manifests│  │ charts   │  │ overlays │  │ Config   │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└────────────────────────┬────────────────────────────────────┘
                         │ pull
┌────────────────────────▼────────────────────────────────────┐
│                     GitOps Operator                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Source: GitRepository / HelmRepository                │ │
│  │  Sync: Kustomization / HelmRelease                     │ │
│  │  Health: Resource status, readiness                    │ │
│  │  Notification: Slack, Email, Webhook                   │ │
│  └──────────────────────┬─────────────────────────────────┘ │
└─────────────────────────┼───────────────────────────────────┘
                          │ apply
┌─────────────────────────▼───────────────────────────────────┐
│                    Kubernetes Cluster                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  App v1  │  │  App v2  │  │  Config  │  │  Secrets │  │
│  │(desired  │  │(desired  │  │  Maps    │  │          │  │
│  │ state)   │  │ state)   │  │          │  │          │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└────────────────────────────────────────────────────────────-┘
```

## CI/GitOps Pipeline
```
Developer → Git Push → CI Pipeline → Build/Test → Push Image
                                      │
                                      │
                    ┌─────────────────┘
                    ▼
            Update Git Manifests (new image tag)
                    │
                    ▼
            Git Commit → PR → Merge
                    │
                    ▼
            GitOps Operator syncs → Deploy to cluster
                    │
                    ▼
            Health Check → Ready
```
