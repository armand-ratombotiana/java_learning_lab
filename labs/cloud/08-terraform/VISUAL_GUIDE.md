# Visual Guide to Terraform

## 1. Terraform Workflow

```
                    ┌──────────────┐
                    │  Code (HCL)  │
                    │  main.tf     │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │  terraform    │
                    │  init         │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │  terraform    │
                    │  plan         │
                    └──────┬───────┘
                           │
           ┌───────────────┴───────────────┐
           │                               │
    ┌──────▼───────┐               ┌──────▼───────┐
    │  Changes      │               │  No changes   │
    │  (to apply)   │               │  (up to date) │
    └──────┬───────┘               └───────────────┘
           │
    ┌──────▼───────┐
    │  terraform    │
    │  apply        │
    └──────┬───────┘
           │
    ┌──────▼───────┐
    │  Infrastructure │
    │  Created/Updated│
    └────────────────┘
```

## 2. Terraform Directory Structure

```
terraform-project/
├── main.tf              # Root configuration
├── variables.tf         # Input variables
├── outputs.tf           # Output values
├── terraform.tfvars     # Variable values (gitignored)
├── terraform.tfvars.example  # Template for values (versioned)
├── versions.tf          # Provider and backend config
├── locals.tf            # Local expressions
├── providers.tf         # Provider configuration
├── modules/
│   ├── networking/      # VPC + subnets
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── compute/         # ASG + ALB
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── database/        # RDS
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
└── environments/
    ├── dev/
    │   └── terraform.tfvars
    ├── staging/
    │   └── terraform.tfvars
    └── prod/
        └── terraform.tfvars
```

## 3. Resource Graph Example

```
VPC Module:
┌───────────┐     ┌───────────┐     ┌───────────┐
│ aws_vpc   │────►│aws_subnet │────►│ aws_route │
│ main      │     │ public    │     │ table_assoc│
└───────────┘     └───────────┘     └───────────┘
     │
     ▼
┌───────────┐     ┌───────────┐
│ aws_igw   │────►│aws_route  │
│ main      │     │ table     │
└───────────┘     └───────────┘

Compute Module:
┌───────────┐     ┌───────────┐     ┌───────────┐
│aws_security│    │aws_lb     │────►│aws_lb_    │
│_group     │     │           │     │target_group│
└───────────┘     └───────────┘     └───────────┘
                                     │
                                ┌────▼────┐
                                │aws_     │
                                │instance │
                                └─────────┘
```

## 4. Terraform Cloud Workflow

```
Developer commits to main branch
       │
       ▼
┌──────────────────────┐
│  VCS Integration      │
│  (GitHub/GitLab)      │
└──────────┬───────────┘
           │ webhook
           ▼
┌──────────────────────┐
│  Terraform Cloud       │
│  ┌────────────────┐  │
│  │ Run Queue       │  │
│  └────────┬───────┘  │
└───────────┼──────────┘
            │
     ┌──────▼──────┐
     │  terraform    │
     │  plan         │
     └──────┬──────┘
            │
     ┌──────▼──────┐
     │  Approve?    │
     │  (optional)  │
     └──────┬──────┘
            │
     ┌──────▼──────┐
     │  terraform    │
     │  apply        │
     └──────┬──────┘
            │
     ┌──────▼──────┐
     │  AWS Infra   │
     │  Updated     │
     └─────────────┘
```
