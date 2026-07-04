# GitOps Flashcards

**Q: What is GitOps?**
A: Methodology using Git as single source of truth for declarative infrastructure.

**Q: What is ArgoCD?**
A: GitOps operator by Intuit (CNCF graduated) for Kubernetes.

**Q: What is Flux?**
A: GitOps operator by Weaveworks (CNCF graduated) for Kubernetes.

**Q: What is a GitOps operator?**
A: Cluster component that reconciles Git state with cluster state.

**Q: What is the pull-based model?**
A: Operator pulls desired state from Git (vs CI server pushing to cluster).

**Q: What is self-healing?**
A: Operator detects and corrects configuration drift automatically.

**Q: What is an Application in ArgoCD?**
A: CRD defining Git source, destination cluster, and sync policy.

**Q: What is an ApplicationSet?**
A: ArgoCD CRD for multi-cluster/multi-environment application generation.

**Q: What is a Kustomization in Flux?**
A: CRD that applies Kustomize overlays from a source to the cluster.

**Q: What is progressive delivery?**
A: Gradual rollout with monitoring (canary, blue/green) via Argo Rollouts.
