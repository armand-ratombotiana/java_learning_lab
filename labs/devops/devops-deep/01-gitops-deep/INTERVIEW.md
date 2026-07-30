# Interview Questions — GitOps

## Q1: What is the difference between GitOps and CI/CD?
**A:** CI/CD pipelines push artifacts to environments. GitOps uses a Git repository as the single source of truth; an operator pulls and reconciles state automatically.

## Q2: How does drift detection work in ArgoCD?
**A:** ArgoCD polls or uses webhooks to compare live cluster state against the desired state in Git. If divergence is detected, it can auto-sync or mark the application as OutOfSync.

## Q3: When would you choose Flux over ArgoCD?
**A:** Flux is lighter weight, has tighter Kustomize integration, and built-in SOPS support. ArgoCD is better for teams needing a rich UI, SSO, and multi-cluster management.

## Q4: What is a sync wave in ArgoCD?
**A:** A sync wave defines the order of resource application (e.g., wave 0: CRDs, wave 1: namespaces, wave 2: apps). Resources in earlier waves must be healthy before later waves proceed.
