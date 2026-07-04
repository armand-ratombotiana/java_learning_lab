# Terraform Security

## Secrets Management
- **Mark variables as sensitive**: `variable "db_password" { sensitive = true }`.
- **Use Vault provider**: `vault_generic_secret` for dynamic secrets.
- **Never hardcode secrets**: Use environment variables or encrypted variable files.
- **State file encryption**: Encrypt remote state at rest (S3 SSE, KMS).

## Authentication
- Use IAM roles for EC2 instances instead of access keys.
- Use OIDC federation for CI/CD pipelines.
- Scope provider credentials to minimal required permissions.
- Rotate credentials regularly.

## Compliance & Policy
- **Sentinel (Terraform Cloud)**: Policy as Code for CI/CD enforcement.
- **OPA/Conftest**: Policy checking for Terraform plans.
- **Checkov/terrascan**: Static analysis for security misconfigurations.
- **tfsec**: Security scanner for Terraform code.

## Common Security Issues
- Exposing S3 buckets publicly
- Overly permissive security groups (0.0.0.0/0)
- Disabled encryption on storage (EBS, RDS, S3)
- Hardcoded secrets in variables/user_data
- Overly broad IAM policies
