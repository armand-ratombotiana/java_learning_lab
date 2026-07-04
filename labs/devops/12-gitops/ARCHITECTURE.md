# GitOps Architecture

## ArgoCD Architecture
```
┌──────────────────────────────────────────────────────────────┐
│                   Kubernetes Cluster                          │
│                                                               │
│  ┌──────────────────── argocd namespace ──────────────────┐  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐             │  │
│  │  │  API     │  │   Repo   │  │   App    │             │  │
│  │  │  Server  │  │  Server  │  │Controller│             │  │
│  │  └──────────┘  └──────────┘  └──────────┘             │  │
│  │  ┌──────────┐  ┌──────────┐                           │  │
│  │  │   Redis  │  │  Dex/SSO │                           │  │
│  │  └──────────┘  └──────────┘                           │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                   │
│  │  App 1   │  │  App 2   │  │  App 3   │  ...              │
│  │(managed) │  │(managed) │  │(managed) │                   │
│  └──────────┘  └──────────┘  └──────────┘                   │
└──────────────────────────────────────────────────────────────┘
```

## GitOps Repository Structure
```
myapp-config/
├── k8s/
│   ├── base/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   └── kustomization.yaml
│   └── overlays/
│       ├── dev/
│       │   ├── kustomization.yaml
│       │   └── values.yaml
│       ├── staging/
│       │   ├── kustomization.yaml
│       │   └── values.yaml
│       └── production/
│           ├── kustomization.yaml
│           └── values.yaml
├── helm/
│   └── myapp-chart/
├── clusters/
│   ├── us-east-1/
│   └── eu-west-1/
└── README.md
```

## CI + GitOps Pipeline
```
Source Code Repo → CI (Build/Test/Push) → Image Registry
                                              │
                                              ▼
Config Repo ← Image Update ← (automated via Flux image-controller)
    │
    ▼
GitOps Operator (ArgoCD/Flux) → Sync → Cluster
```
