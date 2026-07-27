# GitOps ArgoCD MOCK_INTERVIEW.md

## Scenario 1: Sync Failure
ArgoCD is stuck in an "OutOfSync" state for an Application.

**Questions**:
1. How do you diagnose the sync failure?
2. What are common causes of OutOfSync?
3. How do you force a sync?
4. How do you handle sync conflicts?

**Expected approach**: Check `argocd app get APPNAME`, `argocd app logs APPNAME`. Common causes: drift between Git and cluster, resource already exists, validation errors, RBAC issues. Force sync: `argocd app sync APPNAME --force` (use with caution). Conflicts: manual resolution, revert changes, or use self-heal.

## Scenario 2: ApplicationSet Design
You have 20 microservices with identical deployment patterns but different configs.

**Questions**:
1. How would you use ApplicationSet to reduce duplication?
2. Compare ApplicationSet generators (list, git, cluster, matrix, merge).
3. How do you handle service-specific configuration?
4. How do you manage per-service health checks?

**Expected approach**: ApplicationSet with git generator — one Application per directory. Per-service config via `values.json` in each directory or per-service Kustomize overlay. Health checks via ArgoCD resource customizations (Lua patches) or standard Kubernetes health.

## Scenario 3: Multi-Cluster Management
You manage 10 Kubernetes clusters across 3 environments.

**Questions**:
1. How does ArgoCD manage multiple clusters?
2. How do you register clusters with ArgoCD?
3. How do you deploy the same app to multiple clusters?
4. How do you handle cluster-specific configuration?

**Expected approach**: Register clusters via `argocd cluster add`. ApplicationSet with cluster generator — iterate over registered clusters. Cluster-specific config via `values-{{name}}.yaml` or `kustomize/overlays/{{cluster}}`. Use `argo-cd` secrets for cluster credentials.

## Scenario 4: ArgoCD with Helm
Your team uses Helm charts. You need ArgoCD to manage them.

**Questions**:
1. How does ArgoCD handle Helm charts from a repo?
2. How do you pass custom values to Helm?
3. How do you handle Helm hooks?
4. How does ArgoCD handle Helm upgrades?

**Expected approach**: `spec.source.helm`: chart, repoURL, targetRevision, values, valuesFiles. Custom values in Application spec or separate values files. Helm hooks map to ArgoCD sync waves. ArgoCD replaces `helm install`/`upgrade` with direct manifest apply (no Helm release management).

## Scenario 5: Disaster Recovery with ArgoCD
Your entire cluster is lost. How do you recover?

**Questions**:
1. How do you restore ArgoCD itself?
2. How do you restore Applications?
3. How do you handle stateful workloads?
4. What's the RTO?

**Expected approach**: Backup ArgoCD config (Applications, Projects, Repo credentials) to Git. Restore: install ArgoCD, re-register clusters, re-create Applications from backup. Stateful workloads: restore from Velero/cloud snapshots. RTO: depends on number of Applications and cluster size.

## Key ArgoCD Interview Questions
1. Explain the ArgoCD architecture.
2. How does ArgoCD handle RBAC?
3. What's a Sync Wave? How do you use it?
4. Explain the difference between sync and refresh.
5. How does ArgoCD handle resources not in Git?
6. What's the ArgoCD config management plugin?
7. How do ArgoCD projects work?
8. Explain the App of Apps pattern with an example.
9. How does ArgoCD SSO integration work?
10. How do you monitor ArgoCD itself?

## Whiteboard Challenge
Design an ArgoCD-based GitOps platform for 50+ microservices across 3 Kubernetes clusters (dev, staging, prod). Include ApplicationSet, Helm support, secret management, and multi-env promotion with approval gates.

## Follow-up
1. How would you handle rolling back a bad deployment?
2. How would you integrate ArgoCD with a CI pipeline?
3. How would you implement canary analysis with Argo Rollouts?