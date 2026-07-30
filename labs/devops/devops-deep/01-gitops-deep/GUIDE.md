# GitOps — Step-by-Step Guide

## 1. GitOps Core Principles
- **Declarative**: system state described in manifests.
- **Versioned**: Git history is the audit trail.
- **Automated**: operator reconciles desired vs actual state.
- **Pull-based**: cluster pulls from Git (vs push from CI).

## 2. ArgoCD vs Flux
- ArgoCD: Web UI, project-scoped, sync waves, SSO.
- Flux: lighter, Kustomize-native, SOPS encryption, source-controller.

## 3. Drift Detection
- Operator periodically compares cluster state with Git state.
- On drift: auto-sync (or alert for manual sync).

## 4. Sync Strategies
- **Automatic**: apply on every commit to the branch.
- **Manual**: require approval for sync.
- **Phased**: sync waves (CRDs first, then namespaces, then apps).

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab01/*.java
java --enable-preview -cp out com.devops.deep.lab01.GitOpsLab
```
