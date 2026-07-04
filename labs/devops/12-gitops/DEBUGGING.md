# GitOps Debugging Guide

## ArgoCD Debugging
```powershell
# Check application status
argocd app get myapp

# Check details
argocd app get myapp -o yaml

# Get events
kubectl get events -n argocd --sort-by='.lastTimestamp'

# Check repository connection
argocd repo list

# Test sync (dry run)
argocd app sync myapp --dry-run

# Check logs
kubectl logs -n argocd deployment/argocd-application-controller

# Rollback
argocd app rollback myapp <revision>
```

## Flux Debugging
```powershell
# Check sources
flux get sources git

# Check kustomizations
flux get kustomizations

# Suspend/resume
flux suspend kustomization myapp
flux resume kustomization myapp

# Trace reconciliation
flux reconcile kustomization myapp

# Check logs
flux logs --kind=Kustomization --name=myapp
```

## Common Issues
- **OutOfSync**: Manual change in cluster; auto-sync or revert change.
- **Sync failed**: Invalid YAML, missing dependencies, RBAC issues.
- **Health degraded**: Pod crash, probe failure, resource quota exceeded.
- **Repository connection failed**: Wrong URL, auth error, network issue.
- **Prune blocked**: Resource has `prevent-destroy` annotation.
- **ApplicationSet not generating**: Generator error, template syntax issue.
