# GitOps MOCK_INTERVIEW.md

## Scenario 1: GitOps Introduction
Your team deploys via CI/CD that pushes to the cluster directly. You want to adopt GitOps.

**Questions**:
1. What problems does GitOps solve?
2. Explain the push vs pull deployment model.
3. How does ArgoCD implement GitOps?
4. What's the role of the Git repository?

**Expected approach**: GitOps: Git as single source of truth, cluster auto-syncs, declared state is applied. Pull model: GitOps operator watches repo, applies to cluster. Push model: CI/CD pushes direct to cluster. ArgoCD: Application CRD, projects, sync policies, auto-healing. Git repo: contains manifests (Helm, Kustomize, YAML).

## Scenario 2: GitOps for Multi-Environment
You need to manage dev, staging, and prod environments with GitOps.

**Questions**:
1. How do you structure Git repos for multi-env?
2. How do you manage environment-specific config?
3. How do you promote changes across env?
4. How do you handle approval workflows?

**Expected approach**: Option 1: single repo with directories (dev/, staging/, prod/). Option 2: per-environment repos. Option 3: app repo + config repo. Promotion: merge PR from dev to staging branch, then to prod. Approvals via PR reviews + ArgoCD sync policy (manual sync for prod).

## Scenario 3: Drift Detection and Healing
Someone makes a manual change to the cluster. It doesn't match Git.

**Questions**:
1. How does ArgoCD detect drift?
2. What happens when drift is detected?
3. How do you auto-heal?
4. How do you prevent manual changes?

**Expected approach**: ArgoCD periodically compares Git state vs cluster state (default 3 min). Self-healing mode: `syncPolicy.automated.selfHeal: true`. Auto-prune: remove resources not in Git. Prevention: RBAC to restrict direct cluster access, audit logs, kubectl plugins that warn.

## Scenario 4: GitOps and CI/CD Integration
How do CI/CD and GitOps work together?

**Questions**:
1. Where does CI end and CD begin?
2. How do you trigger GitOps from CI?
3. How do you handle image updates?
4. How do you implement canary deployments with GitOps?

**Expected approach**: CI: build, test, push image. CD: GitOps operator detects image change, syncs. Image updates: ArgoCD Image Updater, Flux ImageUpdateAutomation. Canary: Argo Rollouts + ArgoCD, Flux canary with Flagger. CI commits new image tag to Git repo → ArgoCD applies.

## Scenario 5: GitOps Security
Your GitOps setup needs to be secure.

**Questions**:
1. How do you store secrets in GitOps?
2. How does ArgoCD authenticate to the Git repo?
3. How do you secure ArgoCD itself?
4. How do you audit changes?

**Expected approach**: Secrets: Sealed Secrets, SOPS, External Secrets Operator, Vault CSI. Git auth: SSH deploy keys, GitHub App, HTTPS with token. ArgoCD security: RBAC (projects, roles, policies), SSO (OIDC, Dex), audit logs. Audit: Git history, ArgoCD audit logs, webhook notifications.

## Key GitOps Interview Questions
1. What's GitOps and what problems does it solve?
2. Compare ArgoCD vs Flux.
3. Explain the difference between push and pull deployments.
4. What's the GitOps operator pattern?
5. How does ArgoCD handle Helm charts vs Kustomize?
6. Explain ApplicationSet and its generators.
7. How do you handle disaster recovery with GitOps?
8. What's the App of Apps pattern?
9. How does GitOps handle secret management?
10. What's the role of a Sync Policy?

## Whiteboard Challenge
Design a GitOps workflow for a platform with 50+ microservices deployed across 3 Kubernetes clusters (dev, staging, prod). Include CI integration, image updates, secret management, and multi-env promotion.

## Follow-up
1. How would you handle a Git repo being unavailable?
2. How would you migrate from push-based to pull-based?
3. How would you handle emergency hotfixes?