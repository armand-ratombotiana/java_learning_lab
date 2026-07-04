# Secrets Management Code Deep Dive

## Vault Configuration
```hcl
# vault-config.hcl
storage "raft" {
  path = "/vault/data"
  node_id = "node1"
}

listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = false
  tls_cert_file = "/vault/config/cert.pem"
  tls_key_file  = "/vault/config/key.pem"
}

api_addr = "https://vault.example.com:8200"
cluster_addr = "https://vault.example.com:8201"

ui = true

seal "awskms" {
  region     = "us-east-1"
  kms_key_id = "alias/vault-unseal"
}
```

## Enable Secret Engine and Write Secret
```powershell
# Enable KV v2 engine
vault secrets enable -path=secret kv-v2

# Write a secret
vault kv put secret/myapp/api-key value=abc123

# Read a secret
vault kv get secret/myapp/api-key

# List versions
vault kv list secret/myapp
```

## Database Dynamic Secrets
```hcl
# Enable database engine
vault secrets enable database

# Configure PostgreSQL connection
vault write database/config/postgres-db \
    plugin_name=postgresql-database-plugin \
    allowed_roles="app-role" \
    connection_url="postgresql://{{username}}:{{password}}@postgres:5432/mydb" \
    username="vault_admin" \
    password="admin_password"

# Create role
vault write database/roles/app-role \
    db_name=postgres-db \
    creation_statements="CREATE USER \"{{name}}\" WITH PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";" \
    default_ttl="1h" \
    max_ttl="24h"

# Request dynamic credentials
vault read database/creds/app-role
```

## App Authentication (Kubernetes)
```hcl
# Enable Kubernetes auth
vault auth enable kubernetes

# Configure Kubernetes auth
vault write auth/kubernetes/config \
    token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
    kubernetes_host="https://$KUBERNETES_SERVICE_HOST:$KUBERNETES_SERVICE_PORT" \
    kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt

# Create policy
vault policy write myapp-policy - <<EOF
path "secret/data/myapp/*" {
  capabilities = ["read"]
}
EOF

# Create role binding
vault write auth/kubernetes/role/myapp \
    bound_service_account_names=myapp-sa \
    bound_service_account_namespaces=default \
    policies=myapp-policy \
    ttl=1h
```
