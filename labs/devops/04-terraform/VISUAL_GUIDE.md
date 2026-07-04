# Visual Guide to Terraform

## Terraform Workflow
```
Write Config ──▶ terraform init ──▶ terraform plan ──▶ terraform apply
   .tf files        Download          Preview            Execute
                    providers         changes            changes
                        │                                    │
                        ▼                                    ▼
                 ┌──────────────┐                    ┌──────────────┐
                 │  Terraform   │                    │  Update      │
                 │  .terraform  │                    │  State File  │
                 │  directory   │                    │  .tfstate    │
                 └──────────────┘                    └──────────────┘
```

## Resource Graph Example
```
aws_vpc.main ──▶ aws_subnet.public ──▶ aws_instance.web
      │                                     │
      └────────▶ aws_subnet.private ──▶ aws_db_instance.db
```

## Provider Configuration
```
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}
```

## State Management Flow
```
Local:  terraform.tfstate (working dir)
Remote: S3 + DynamoDB (locking)
         │
         ▼
Terraform Cloud (collaboration)
```
