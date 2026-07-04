# Helm Theory

## Core Concepts
- **Chart**: Helm package containing Kubernetes resource templates and metadata.
- **Release**: A running instance of a chart in a Kubernetes cluster.
- **Repository**: Collection of published charts (index.yaml + packaged charts).
- **Template**: Go template file that renders into Kubernetes YAML.
- **Values**: Configuration parameters injected into templates (values.yaml + overrides).
- **Hook**: Chart resources that run at specific lifecycle events (pre/post install, upgrade, delete).

## Chart Structure
```
mychart/
├── Chart.yaml          # Metadata (name, version, description)
├── values.yaml         # Default configuration values
├── templates/          # Template files (Go templates + Sprig functions)
│   ├── NOTES.txt       # Helpful text shown after install
│   ├── _helpers.tpl    # Reusable named templates
│   ├── deployment.yaml
│   ├── service.yaml
│   └── ingress.yaml
├── charts/             # Subchart dependencies
└── crds/               # Custom Resource Definitions
```

## Helm Three-Way Strategy
Helm uses three-way strategic merge patch:
- **Previous release** (last applied manifest)
- **Current release** (latest manifest)
- **Live cluster state** (what's actually running)

This correctly handles manual edits to resources.
