# Secrets Management Refactoring

## Before (Static Secrets in Config)
```yaml
# application.yml (committed to git!)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: app_user
    password: SuperSecret123!
```

## After (Vault Dynamic Secrets)
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://vault-proxy:5432/mydb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```
```yaml
# Vault Agent sidecar config
pid_file = "/tmp/pid"
auto_auth {
  method "kubernetes" {
    mount_path = "auth/kubernetes"
    config {
      role = "myapp"
    }
  }
}
template {
  destination = "/etc/secrets/database"
  contents = <<EOH
DB_USERNAME={{ with secret "database/creds/app-role" }}{{ .Data.username }}{{ end }}
DB_PASSWORD={{ with secret "database/creds/app-role" }}{{ .Data.password }}{{ end }}
EOH
}
```

## Gains
- No secrets in git
- Dynamic credentials (auto-expire and rotate)
- Access controlled via Vault policies
- Audit logging for all secret access
- Zero-trust: no static credentials anywhere
