# How Terraform Works

## Core Workflow

```
terraform init          → Download providers and modules
terraform plan         → Preview changes (refresh state → compare → diff)
terraform apply        → Execute changes (create/update/delete resources)
terraform destroy      → Delete all managed resources
```

## Execution Flow

```
1. terraform init
   └── Download provider plugins (aws, random, tls)
   └── Initialize backend (local or remote S3)
   └── Download module sources (registry, Git, local)

2. terraform plan
   └── Refresh state (read current AWS resources)
   └── Build dependency graph from configuration
   └── Compare state vs configuration
   └── Compute changes (create/update/delete in order)
   └── Output plan (human-readable + JSON)

3. terraform apply
   └── Confirm plan
   └── Execute graph in dependency order
   └── For each resource:
       ├── Create: POST to AWS API → store ID in state
       ├── Update: Update attributes → update state
       └── Delete: DELETE to AWS API → remove from state
   └── Persist state to backend
```

## Resource Configuration

```hcl
# main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "prod/java-app/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-state-locks"
    encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region
  default_tags {
    tags = {
      Environment = var.environment
      Project     = "java-app"
      ManagedBy   = "Terraform"
    }
  }
}

resource "aws_instance" "web" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = var.instance_type
  subnet_id     = aws_subnet.main.id
  user_data     = templatefile("user-data.sh", {
    app_version = var.app_version
  })

  tags = {
    Name = "java-app-${var.environment}"
  }
}
```

## Variable System

```hcl
# variables.tf
variable "environment" {
  description = "Deployment environment"
  type        = string
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Must be dev, staging, or prod."
  }
}

variable "instance_type" {
  type    = string
  default = "t3.micro"
}

# terraform.tfvars
environment   = "prod"
instance_type = "t3.large"
```
