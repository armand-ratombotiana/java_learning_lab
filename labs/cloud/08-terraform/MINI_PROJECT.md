# Terraform IaC - MINI PROJECT

## Project: Java Application Infrastructure

Create Terraform configuration for a Java application with:
- VPC with public/private subnets
- ECS cluster with Fargate
- RDS PostgreSQL database
- Application Load Balancer

## Prerequisites

- Terraform 1.5+
- AWS account
- AWS CLI configured

## Structure

```
terraform/
├── main.tf
├── variables.tf
├── outputs.tf
├── providers.tf
└── modules/
    └── vpc/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

## Implementation

### Step 1: Provider Configuration

```hcl
# providers.tf
terraform {
  required_version = ">= 1.5.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "my-terraform-state-${var.environment}"
    key    = "java-app/terraform.tfstate"
    region = "us-east-1"
  }
}

provider "aws" {
  region = var.aws_region
}
```

### Step 2: Variables

```hcl
# variables.tf
variable "aws_region" {
  default = "us-east-1"
}

variable "environment" {
  validation {
    condition = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Must be dev, staging, or prod."
  }
}

variable "project_name" {
  default = "java-app"
}
```

### Step 3: VPC Module

```hcl
# modules/vpc/main.tf
variable "environment" {}
variable "project_name" {}

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
}

resource "aws_subnet" "public" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index}.0/24"
  availability_zone = "us-east-1${count.index == 0 ? "a" : "b"}"
  map_public_ip_on_launch = true
}

resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 10}.0/24"
  availability_zone = "us-east-1${count.index == 0 ? "a" : "b"}"
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
}

resource "aws_route_table_association" "public" {
  count          = 2
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}
```

### Step 4: Main Configuration

```hcl
# main.tf
module "vpc" {
  source      = "./modules/vpc"
  environment = var.environment
  project_name = var.project_name
}

resource "aws_security_group" "ecs" {
  name        = "${var.project_name}-ecs-${var.environment}"
  vpc_id      = module.vpc.vpc_id
  
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lb" "main" {
  name               = "${var.project_name}-alb-${var.environment}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.ecs.id]
  subnets            = module.vpc.public_subnet_ids
  
  enable_deletion_protection = var.environment == "prod"
}

resource "aws_lb_target_group" "main" {
  name     = "${var.project_name}-tg"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = module.vpc.vpc_id
  
  health_check {
    path = "/actuator/health"
  }
}

resource "aws_lb_listener" "main" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"
  
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.main.arn
  }
}
```

### Step 5: Outputs

```hcl
# outputs.tf
output "vpc_id" {
  value = module.vpc.vpc_id
}

output "alb_dns_name" {
  value = aws_lb.main.dns_name
}

output "alb_zone_id" {
  value = aws_lb.main.zone_id
}
```

## Deploy

```bash
# Initialize
terraform init

# Plan
terraform plan -var="environment=dev"

# Apply
terraform apply -var="environment=dev"

# Destroy
terraform destroy -var="environment=dev"
```

## Testing

```bash
# Get outputs
terraform output alb_dns_name

# Test endpoint
curl http://$(terraform output -raw alb_dns_name)/actuator/health
```

## Challenges

1. **Add RDS**: Create database module
2. **Add ECS**: Add task definition and service
3. **Add Redis**: Create ElastiCache module
4. **Add Monitoring**: Add CloudWatch alarms

## Deliverables

- [ ] VPC with subnets
- [ ] Security groups
- [ ] Application Load Balancer
- [ ] Target group and listener
- [ ] Variable validation
- [ ] S3 backend
- [ ] Outputs