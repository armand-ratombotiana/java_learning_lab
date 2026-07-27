# Mock Interview — Terraform

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Hands-on
- **Difficulty**: Associate/Professional

## Warm-Up (5 min)

Q1: What is Infrastructure as Code and what problems does it solve?

Q2: Explain the Terraform workflow: init, plan, apply, destroy. What happens in each step?

## Technical Questions (20 min)

### Question 1: Terraform State (10 min)
Your team has been using Terraform with local state. You need to move to remote state for team collaboration.

**Question**: Design the remote state setup:
- Which backend would you use (S3, AzureRM, GCS, Terraform Cloud)?
- How do you handle state locking?
- How do you manage multiple environments (dev, staging, prod)?
- What is the state file security consideration?

### Question 2: Module Design (10 min)
Write a reusable Terraform module for an AWS EC2 instance with:
- Inputs: instance_type, ami_id, subnet_id, security_group_ids, tags
- Outputs: instance_id, public_ip, private_ip
- Resource: aws_instance with user_data script that installs Java 17

## Behavioral Question (10 min)

**Question**: Tell me about a time Terraform state corruption or drift caused an issue. How did you fix it and what preventive measures did you implement?

## System Design Whiteboard (10 min)

**Problem**: Design a Terraform project structure for a company with:
- 3 environments: dev, staging, prod
- 5 separate application teams
- Shared infrastructure (VPC, IAM, monitoring) vs application-specific
- Multi-region deployment (us-east-1, eu-west-1, ap-southeast-1)
- Terraform Cloud for remote operations

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| State | Remote backends, locking, isolation | Local state works | No state awareness |
| Modules | Reusable, versioned, composed | Basic modules | Single monolithic config |
| HCL | Functions, expressions, loops, conditions | Basic syntax | Simple resources only |
| Workspaces | Environment management, variable hierarchy | Knows workspaces | Single workspace |
| CI/CD | Integration with pipelines, plan/apply approval | Manual apply | No automation |

## Sample Solution Outline

### Remote State with S3
```hcl
terraform {
  backend "s3" {
    bucket         = "mycompany-terraform-state"
    key            = "prod/network/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-state-lock"
    encrypt        = true
  }
}
```

### EC2 Module
```hcl
variable "instance_type" { type = string }
variable "ami_id"        { type = string }
variable "subnet_id"     { type = string }
variable "security_group_ids" { type = list(string) }
variable "tags"          { type = map(string) }

resource "aws_instance" "this" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = var.subnet_id
  vpc_security_group_ids = var.security_group_ids
  tags                   = var.tags
  
  user_data = <<-EOF
    #!/bin/bash
    apt-get update
    apt-get install -y openjdk-17-jre-headless
    EOF
}

output "instance_id"  { value = aws_instance.this.id }
output "public_ip"    { value = aws_instance.this.public_ip }
output "private_ip"   { value = aws_instance.this.private_ip }
```

### Project Structure
```
terraform/
├── environments/
│   ├── dev/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── terraform.tfvars
│   ├── staging/
│   │   └── ...
│   └── prod/
│       └── ...
├── modules/
│   ├── vpc/
│   ├── ec2/
│   ├── rds/
│   └── iam/
└── global/
    ├── iam/
    └── route53/
```
- Workspaces per environment or separate directory structure
- Terraform Cloud: workspace per environment + per team
- Sentinel policies for cost governance, security compliance
- Run tasks: Checkov, tfsec, Infracost for validation
