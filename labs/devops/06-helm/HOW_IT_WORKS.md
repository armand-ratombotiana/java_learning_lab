# How Helm Works

## Installation Flow
```
helm install myapp ./mychart
    │
    ▼
Helm reads Chart.yaml and values.yaml
    │
    ▼
Template engine parses templates/ with values
    │
    ▼
Generates complete Kubernetes YAML manifests
    │
    ▼
Helm communicates with K8s API server (NO TILLER)
    │
    ▼
Creates resources and stores release in Secrets
    │
    ▼
Display NOTES.txt
```

## Upgrade Flow
```
helm upgrade myapp ./mychart --set image.tag=v2
    │
    ▼
Load previous release manifest
Load current chart templates + new values
Fetch live cluster state
    │
    ▼
Three-way diff → Patch only changed resources
    │
    ▼
Create new release revision
```

## Rollback Flow
```
helm rollback myapp 1
    │
    ▼
Load release revision 1 manifest
Compare with current live state
Apply diff to revert changes
```

## Release Storage
Releases stored as Kubernetes Secrets in the target namespace.
- Secret name: `sh.helm.release.v1.<release-name>.v<revision>`
- Each Helm operation creates a new revision.
