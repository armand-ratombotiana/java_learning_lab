# Step-by-Step — Terraform Java App Infrastructure

## Step 1: Install Terraform
```powershell
# Download Terraform from https://terraform.io/downloads
# Add to PATH
terraform --version
```

## Step 2: Create Project Structure
```powershell
mkdir terraform-java-infra && cd terraform-java-infra
New-Item -ItemType Directory -Path modules/vpc, modules/compute, modules/rds
```

## Step 3: Configure Provider and Backend
```hcl
# versions.tf
terraform {
  required_version = ">= 1.0"
  backend "s3" {
    bucket         = "java-app-terraform-state"
    key            = "dev/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
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

## Step 4: Create VPC Module
```hcl
# modules/vpc/main.tf
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = { Name = "${var.environment}-vpc" }
}

resource "aws_subnet" "public" {
  count             = length(var.public_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.public_subnet_cidrs[count.index]
  availability_zone = var.azs[count.index]
  map_public_ip_on_launch = true
  tags = { Name = "${var.environment}-public-${count.index}" }
}

resource "aws_subnet" "private" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.azs[count.index]
  tags = { Name = "${var.environment}-private-${count.index}" }
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
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}
```

## Step 5: Use Module in Root
```hcl
# main.tf
module "vpc" {
  source = "./modules/vpc"
  environment = var.environment
  vpc_cidr    = "10.0.0.0/16"
  public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnet_cidrs = ["10.0.10.0/24", "10.0.11.0/24"]
  azs = ["us-east-1a", "us-east-1b"]
}

module "compute" {
  source = "./modules/compute"
  environment   = var.environment
  vpc_id        = module.vpc.vpc_id
  subnet_ids    = module.vpc.private_subnet_ids
  instance_type = var.instance_type
  app_version   = var.app_version
}

module "rds" {
  source = "./modules/rds"
  environment     = var.environment
  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnet_ids
  db_name         = var.db_name
  db_username     = var.db_username
  db_password     = var.db_password
}
```

## Step 6: Apply
```bash
terraform init
terraform plan -out=tfplan
terraform apply tfplan
```

## Step 7: Clean Up
```bash
terraform destroy
```
