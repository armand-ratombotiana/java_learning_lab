# Terraform Theory

## Core Concepts
- **Infrastructure as Code (IaC)**: Managing infrastructure through machine-readable definition files.
- **HCL**: HashiCorp Configuration Language — declarative language for defining resources.
- **Provider**: Plugin that interacts with cloud/API (AWS, GCP, Azure, Kubernetes, etc.).
- **Resource**: Infrastructure object (EC2 instance, DNS record, S3 bucket).
- **Data Source**: Read-only query of existing infrastructure.
- **State**: Snapshot of managed infrastructure stored in `terraform.tfstate`.
- **Module**: Reusable collection of resources with inputs and outputs.

## Terraform Workflow
1. **Write**: Define infrastructure in `.tf` files.
2. **Init**: Initialize working directory (`terraform init`).
3. **Plan**: Preview changes (`terraform plan`).
4. **Apply**: Execute changes (`terraform apply`).
5. **Destroy**: Remove resources (`terraform destroy`).

## Key Features
- **Execution Plans**: Show what will happen before applying.
- **Resource Graph**: Dependency graph determines parallel/sequential operations.
- **Change Automation**: Minimal changes to reach desired state.
- **State Management**: Track resource metadata across runs.
