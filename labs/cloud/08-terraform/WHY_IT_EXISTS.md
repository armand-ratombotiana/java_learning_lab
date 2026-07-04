# Why Terraform Exists

## The Problem
Click-ops: manually creating infrastructure in the AWS Console leads to:
- Inconsistent environments (dev ≠ prod)
- No version control for infrastructure
- No audit trail
- Difficult disaster recovery
- Manual error-prone changes

## Terraform's Answer
Infrastructure as Code (IaC): define all cloud resources in declarative HCL files, version-controlled in Git, applied programmatically.

## Why Terraform Over Alternatives?
| Feature | Terraform | CloudFormation | Pulumi |
|---------|-----------|----------------|--------|
| Cloud support | Multi-cloud | AWS only | Multi-cloud |
| Language | HCL (DSL) | JSON/YAML | TypeScript, Python, Go |
| State management | Remote backends | AWS-managed | Managed backends |
| Modularity | Terraform Registry | Nested stacks | NPM/PyPI packages |
| Community | Vast ecosystem | AWS-only | Growing |

## Why Terraform for Java?
- Define EC2, RDS, ECS, EKS resources for Java apps
- Modules for reusable patterns (VPC, ALB, ASG, RDS)
- Workspaces for environment separation (dev/staging/prod)
- Remote state with S3 + DynamoDB for team collaboration
