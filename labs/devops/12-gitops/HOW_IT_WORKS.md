# How GitOps Works

## ArgoCD Workflow
```
Developer pushes to Git
    │
    ▼
Git repo updated (new manifest version)
    │
    ▼
ArgoCD detects change (poll or webhook)
    │
    ▼
ArgoCD diffs desired (Git) vs actual (cluster)
    │
    ├── Match → OK (no action)
    │
    └── Mismatch → Sync (apply changes)
        │
        ▼
    Health check → Healthy/Synced
```

## Flux Workflow
```
Developer commits change to Git
    │
    ▼
Flux GitRepository source controller detects change
    │
    ▼
Flux Kustomize/Helm controller reconciles
    │
    ▼
Applies to cluster
    │
    ▼
Health check → Ready
```

## GitOps CI/CD Pipeline
```
CI Pipeline:
  Code commit → Build → Test → Push image to registry
    → Update Git repository (new image tag in manifest)

CD Pipeline (GitOps):
  Git commit → GitOps operator syncs → Deploy to cluster
    → Health check → Ready
```

## Drift Correction
```
User runs `kubectl edit deployment` (manual change)
    │
    ▼
ArgoCD detects drift (out-of-sync)
    │
    ├── Auto-sync enabled → Corrects immediately
    │
    └── Auto-sync disabled → Shows OutOfSync status
```
