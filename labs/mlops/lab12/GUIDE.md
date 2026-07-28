# Lab 12: Infrastructure as Code for ML — Guide

## Step 1: Understand IaC for ML

Key infrastructure components for ML:
- **Compute**: Training instances (EC2, GKE nodes, Azure ML compute)
- **Storage**: Data lakes (S3, GCS, ADLS), feature stores
- **Networking**: VPC, subnets, security groups
- **ML Services**: SageMaker, Vertex AI, Azure ML
- **CI/CD**: Build pipelines, artifact repos

## Step 2: Explore TerraformConfigGenerator

The `TerraformConfigGenerator` creates HCL configurations for:
- S3 buckets for data and model storage
- ECS Fargate for model serving
- IAM roles for ML workloads
- VPC configuration

## Step 3: Explore PulumiEquivalent

The Java code also shows how Pulumi would express the same infrastructure as real Java code.

## Step 4: Compile and Run

```bash
cd lab12/src
javac com/mlops/lab12/*.java
java com.mlops.lab12.InfrastructureAsCodeLab
```

## Key Concepts

| Concept | Terraform | Pulumi |
|---------|-----------|--------|
| Language | HCL | Java/Python/Go/TS |
| State | tfstate file | managed backend |
| Provider | aws, gcp, azurerm | same (bridged) |
| Module | re-usable .tf files | re-usable classes |
| Resource | resource "aws_s3_bucket" | new Bucket(...) |

## Best Practices
- Use remote state with locking (S3 + DynamoDB)
- Tag all resources for cost tracking
- Store IaC in version control alongside ML code
- Use workspaces for environment separation (dev/staging/prod)
- Run `terraform plan` in CI/CD before apply
