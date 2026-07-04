# Step-by-Step Secrets Management Guide

## 1. Deploy Vault in Dev Mode
```powershell
docker run -d --name vault -p 8200:8200 vault:latest server -dev
$env:VAULT_ADDR="http://localhost:8200"
$env:VAULT_TOKEN="root"  # dev token
```

## 2. Access Vault UI
Open http://localhost:8200. Log in with token "root".

## 3. Write and Read a Secret
```powershell
vault kv put secret/devops/sample password=hello123
vault kv get secret/devops/sample
```

## 4. Enable Database Dynamic Secrets (if Postgres available)
```powershell
vault secrets enable database
# Follow code deep dive for database config
vault read database/creds/app-role
```

## 5. Set Up Policies
```hcl
path "secret/data/devops/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}
```
```powershell
vault policy write devops policy.hcl
```

## 6. Create Token with Policy
```powershell
vault token create -policy=devops
```

## 7. Set Up Kubernetes Auth (if K8s available)
```powershell
vault auth enable kubernetes
# Configure as shown in code deep dive
```

## 8. Rotate a Secret
```powershell
vault kv put secret/myapp/api-key value=newkey456
# Version 2 created; version 1 still available
vault kv get -version=1 secret/myapp/api-key
```

## 9. Clean Up
```powershell
docker stop vault && docker rm vault
```
