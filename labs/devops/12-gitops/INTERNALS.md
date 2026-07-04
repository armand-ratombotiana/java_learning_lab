# GitOps Internals

## ArgoCD Architecture
- **API Server**: gRPC/REST API, UI, and CLI.
- **Repository Server**: Clones Git repos, generates manifests (Helm, Kustomize, plain YAML).
- **Application Controller**: Watches Git and cluster, reconciles differences.
- **Redis**: Cache for Git repo data and application state.
- **Application CRD**: Custom Resource Definition for managing applications.
- **ApplicationSet CRD**: Template-based multi-cluster/multi-environment deployments.

## Flux Architecture
- **Source Controller**: Fetches artifacts from Git, OCI, Helm, S3, Bucket.
- **Kustomize Controller**: Applies Kustomize overlays.
- **Helm Controller**: Manages Helm releases.
- **Notification Controller**: Handles events and notifications.
- **Image Automation Controller**: Updates Git with new image tags.

## Sync Strategies
- **Manual sync**: User triggers sync via CLI/UI.
- **Auto-sync**: Operator syncs automatically on drift detection.
- **Sync waves**: Order resource creation (0: CRDs, 1: Namespaces, 2: Deployments).
- **Prune**: Delete resources not in Git (optional, safer without).

## Health Assessment
- **ArgoCD**: Built-in health checks for standard K8s resources. Custom health checks via Lua.
- **Flux**: Readiness gates based on K8s conditions. Custom health checks possible.

## Multi-Cluster Management
- **ArgoCD**: Register multiple clusters; use ApplicationSets for cluster-specific deployment.
- **Flux**: Flux instance per cluster, or cross-cluster reconciliation.
