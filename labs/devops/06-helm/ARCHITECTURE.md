# Helm Architecture

## Component Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                        Helm Client                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Chart    │  │ Template │  │ Release  │  │ Repository│   │
│  │ Loader   │  │ Engine   │  │ Manager  │  │ Client   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└──────────────────────────┬──────────────────────────────────┘
                           │ k8s API
┌──────────────────────────▼──────────────────────────────────┐
│                  Kubernetes Cluster                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Helm Releases (stored as Secrets in target namespace) │ │
│  │  ┌──────────────────────────────────────────────────┐ │ │
│  │  │ Release: myapp v1 (Deployment, Service, ConfigMap)│ │ │
│  │  │ Release: myapp v2 (Deployment, Service, ConfigMap)│ │ │
│  │  └──────────────────────────────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Chart Repository (OCI or HTTP)                        │ │
│  │  myapp-0.1.0.tgz, myapp-0.2.0.tgz, index.yaml         │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

## Release Storage
- **Kubernetes Secrets** in release namespace
- Secret name format: `sh.helm.release.v1.<name>.v<revision>`
- Data: compressed, encrypted (base64) release object
- No Tiller since Helm 3 — direct API communication
