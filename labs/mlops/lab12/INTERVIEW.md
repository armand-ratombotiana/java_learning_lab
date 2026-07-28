# Lab 12: Interview Questions

## FAANG-Level Questions

### Q1: Design an IaC strategy for a multi-environment ML platform (dev/staging/prod).
**Answer**: Use Terraform workspaces or separate state files per environment. Store state remotely (S3 + DynamoDB locking). Use modules for shared infrastructure (VPC, IAM) and environment-specific configurations (instance sizes, replica counts). Implement Terragrunt for DRY configuration across environments. Run plan in CI, require approval for prod apply.

### Q2: How do you manage secrets and sensitive configuration in ML IaC?
**Answer**: Use a secrets manager (AWS Secrets Manager, HashiCorp Vault, GCP Secret Manager). Never store secrets in Terraform state. Reference secrets via data sources (e.g., `data.aws_secretsmanager_secret`). Use `.tfvars` files for non-sensitive config, keeping them in git. Use environment variables for CI/CD pipeline secrets.

### Q3: Compare Terraform vs Pulumi for ML infrastructure.
**Answer**: Terraform uses HCL (domain-specific language) with mature provider ecosystem and state management. Pulumi uses general-purpose languages (Java, Python, TypeScript) enabling loops, conditionals, and testing with standard frameworks. For ML teams already using Python/Java, Pulumi offers a lower learning curve. Terraform has better community modules for ML (SageMaker, Vertex AI).

### Q4: How do you handle infrastructure drift in ML environments?
**Answer**: Use `terraform plan` in scheduled CI jobs to detect drift. Implement drift remediation with `terraform apply` (auto or manual). Use `prevent_destroy` lifecycle on critical resources (databases, model registry). Tag resources with environment and owner for accountability.

## LeetCode / NeetCode References
- **Design a Configuration Management System** — Infrastructure state management
- **Design a Resource Allocation System** — Compute resource provisioning
- **Design a Cloud Cost Optimizer** — Infrastructure cost management
