# Step-by-Step Helm Guide

## 1. Install Helm
```powershell
choco install kubernetes-helm
helm version
```

## 2. Add a Repository
```powershell
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

## 3. Search and Install a Chart
```powershell
helm search repo nginx
helm install my-nginx bitnami/nginx --version 15.0.0
```

## 4. Create Your Own Chart
```powershell
helm create mychart
cd mychart
```

## 5. Explore Chart Structure
```powershell
ls
cat Chart.yaml
cat values.yaml
```

## 6. Lint and Template
```powershell
helm lint .
helm template .   # Render templates locally
```

## 7. Install Your Chart
```powershell
helm install myrelease . --values values.yaml
```

## 8. Upgrade with Custom Values
```powershell
helm upgrade myrelease . --set replicaCount=3
```

## 9. Rollback
```powershell
helm history myrelease
helm rollback myrelease 1
```

## 10. Package and Share
```powershell
helm package . -d dist/
# Push to OCI registry
helm push mychart-0.1.0.tgz oci://myregistry.azurecr.io/helm
```
