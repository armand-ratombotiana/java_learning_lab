# Helm Internals

## Architecture (Helm 3)
```
┌─────────────────────────────────────┐
│           Helm CLI (client)          │
│  ┌─────────┐  ┌─────────┐          │
│  │ SDK/    │  │ Template│          │
│  │ Library │  │ Engine  │          │
│  └────┬────┘  └─────────┘          │
│       │                             │
│  ┌────▼────┐                        │
│  │ Chart   │                        │
│  │ Loader  │                        │
│  └─────────┘                        │
└──────────────┬──────────────────────┘
               │ k8s API calls
┌──────────────▼──────────────────────┐
│      Kubernetes API Server           │
│  (no Tiller — direct communication)  │
└─────────────────────────────────────┘
```

## Template Engine
- **Go templates**: `{{ .Values.replicaCount }}`
- **Sprig functions**: 70+ string/math/crypto helpers.
- **Flow control**: `if`, `with`, `range` blocks.
- **Named templates**: Defined in `_helpers.tpl`, called with `{{ template "name" . }}`.
- **Pipelines**: Chain functions: `{{ .Values.version | upper | quote }}`.

## Chart Dependencies
```yaml
# Chart.yaml
dependencies:
  - name: postgresql
    version: "~12.0"
    repository: "https://charts.bitnami.com/bitnami"
    condition: postgresql.enabled
```

## Security: Signed Charts
- Provenance file (.prov) contains chart hash + PGP signature.
- `helm verify` checks signature against known public keys.
- OCI registries support content-addressable storage for charts.
