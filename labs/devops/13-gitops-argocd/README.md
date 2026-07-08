# 13-GitOps ArgoCD — GitOps Principles & Multi-Cluster Management

## Overview
GitOps is an operational framework that applies DevOps best practices — version control, collaboration, compliance, and CI/CD — to infrastructure automation. ArgoCD is a declarative, GitOps continuous delivery tool for Kubernetes that implements a pull-based deployment model. This lab covers GitOps principles, ArgoCD architecture, ApplicationSets, sync strategies, multi-cluster management, and progressive delivery patterns.

GitOps treats Git as the single source of truth for both application code and infrastructure configuration. When a change is pushed to Git, the GitOps operator automatically reconciles the cluster state with the desired state defined in Git. This approach provides an audit trail, simplifies rollbacks, and enables self-healing infrastructure that automatically corrects configuration drift.

ArgoCD extends the GitOps model with a rich set of features including a web UI, CLI, automated sync policies, health assessment, resource pruning, and integration with various configuration management tools like Helm, Kustomize, and Jsonnet. It supports multiple clusters from a single ArgoCD instance and provides fine-grained access control through projects and RBAC.

## Prerequisites
- Kubernetes fundamentals (Pods, Deployments, Services)
- Git version control basics
- Understanding of YAML manifests
- Helm basics (for Helm chart integration)

## Learning Objectives
- Understand GitOps principles and the pull-based deployment model
- Install and configure ArgoCD on a Kubernetes cluster
- Define ArgoCD Applications pointing to Git repositories
- Configure automated sync policies with pruning and self-healing
- Implement ApplicationSet generators for multi-cluster deployments
- Manage sync waves and resource ordering
- Implement progressive delivery with canary deployments
- Configure RBAC and projects for multi-team environments
- Handle secrets in GitOps workflows using SealedSecrets or External Secrets Operator
- Implement disaster recovery and cluster bootstrapping with ArgoCD

## Lab Structure
- `THEORY.md` — Core GitOps principles and ArgoCD architecture
- `MATH_FOUNDATION.md` — Mathematical concepts: reconciliation loops, dependency graphs, progressive delivery
- `CODE_DEEP_DIVE.md` — Hands-on ArgoCD YAML manifests and configurations
- `EXERCISES.md` — Practical exercises for ArgoCD deployment and management
- `QUIZ.md` — Self-assessment questions on GitOps and ArgoCD concepts
- All other standard DevOps Academy files provide additional depth

## Key Concepts
1. **Desired State**: The entire system configuration stored declaratively in Git
2. **Reconciliation Loop**: Continuous comparison of desired vs actual state
3. **Pull-Based Deployment**: The operator pulls changes from Git rather than CI pushing to cluster
4. **Self-Healing**: Automatic correction of configuration drift detected during reconciliation
5. **ApplicationSet**: Template-based generation of Applications for multiple environments or clusters
6. **Sync Waves**: Ordered execution groups for managing resource creation dependencies
7. **Progressive Delivery**: Gradual rollout of changes through canary or blue-green deployments
8. **Drift Detection**: Comparison of live cluster state against desired state in Git
9. **Resource Pruning**: Automatic removal of resources that no longer exist in Git
10. **Multi-Cluster Management**: Managing multiple Kubernetes clusters from a single ArgoCD instance
