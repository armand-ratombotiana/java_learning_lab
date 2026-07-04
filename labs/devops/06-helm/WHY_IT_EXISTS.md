# Why Helm Exists

## The Problem
- **Complex YAML management**: Running multiple K8s manifests per application is error-prone.
- **No package management**: No standard way to distribute K8s applications.
- **Environment variability**: Copy-pasting YAML for dev/staging/prod with slight differences.
- **No versioning**: Hard to track and rollback application versions.
- **No dependency management**: Applications that depend on databases, message queues, etc.

## Helm's Solution
- **Packaging**: Standard chart format for distributing K8s applications.
- **Templating**: Parameterize YAML with Go templates; one chart for all environments.
- **Release management**: Track releases with rollback support.
- **Dependencies**: Charts can depend on other charts (subcharts).
- **Repositories**: Centralized chart distribution (Artifact Hub, OCI registries).
- **Lifecycle hooks**: Run jobs before/after install, upgrade, delete.
