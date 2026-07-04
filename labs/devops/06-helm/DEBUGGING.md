# Helm Debugging Guide

## Debug Commands
```powershell
# Dry run (show what would be created)
helm install myrelease . --dry-run --debug

# Render templates locally
helm template .

# Check chart syntax
helm lint .

# List releases
helm list -a

# Get release status
helm status myrelease

# View release history
helm history myrelease

# Get rendered manifest
helm get manifest myrelease

# Get values for a release
helm get values myrelease
```

## Common Issues
- **Template rendering error**: Check Go template syntax, closing braces, pipelines.
- **Release not found**: Check namespace (`--namespace` flag).
- **Upgrade fails**: Check three-way merge conflicts; manually reconcile with `kubectl edit`.
- **Dependency issues**: Run `helm dependency build` or `helm dependency update`.
- **Hook failures**: Check hook annotations and resource definitions in templates.
- **Already exists error**: Resources from previous partial install; `helm install --replace`.
