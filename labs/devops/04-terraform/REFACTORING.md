# Terraform Refactoring

## Before (Flat Configuration)
```hcl
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_subnet" "public_a" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
}

resource "aws_subnet" "public_b" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
}

resource "aws_security_group" "web" {
  name   = "web-${aws_vpc.main.id}"
  vpc_id = aws_vpc.main.id
}
# ... more duplicated resources
```

## After (Modular)
```hcl
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = "5.0.0"

  name = "myapp-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["us-east-1a", "us-east-1b"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24"]

  enable_nat_gateway = true
  tags = { Environment = var.environment }
}

resource "aws_security_group" "web" {
  name   = "web-sg"
  vpc_id = module.vpc.vpc_id

  dynamic "ingress" {
    for_each = var.allowed_ports
    content {
      from_port   = ingress.value
      to_port     = ingress.value
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }
}
```

## Refactoring Gains
- Reduced lines: 200+ → 30
- Reusable module across environments
- Dynamic blocks eliminate repetition
- Variables replace hardcoded values
