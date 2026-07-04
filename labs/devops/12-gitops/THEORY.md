# GitOps Theory

## Core Principles
1. **Declarative Configuration**: Entire system described declaratively (YAML, Helm, Kustomize).
2. **Version Controlled and Immutable**: Git is the single source of truth; all changes go through Git.
3. **Automated Sync**: GitOps operator automatically reconciles cluster state with Git state.
4. **Pull-based Deployment**: Operator pulls desired state from Git (vs pushing from CI/CD).
5. **Self-Healing**: Operator detects drift and corrects it automatically.
6. **Observability**: Cluster state visible via Git diff, sync status, and health checks.

## Key Components
- **Git Repository**: Source of truth containing Kubernetes manifests, Helm charts, Kustomize overlays.
- **GitOps Operator**: ArgoCD or Flux — runs in cluster, syncs Git state to cluster.
- **CI Pipeline**: Builds and tests code, pushes artifacts (images), updates Git config.
- **CD Pipeline**: GitOps operator detects Git change, applies to cluster.

## GitOps vs Traditional CI/CD
| Aspect | Traditional CI/CD | GitOps |
|--------|------------------|--------|
| Deployment model | Push-based (CI server) | Pull-based (operator) |
| State storage | CI/CD database | Git repository |
| Drift detection | Manual or external | Built-in (operator reconciles) |
| Rollback | Rerun previous job | Git revert + sync |
| Approval | CI/CD pipeline gate | Git PR/Merge approval |
| Audit trail | CI/CD logs | Git history |
