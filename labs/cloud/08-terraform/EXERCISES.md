# Terraform IaC - EXERCISES

## Exercise 1: Basic Configuration

Create a Terraform configuration that:
1. Uses AWS provider
2. Creates an EC2 instance
3. Uses variables for instance type

```hcl
# Solution
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_instance" "app" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = var.instance_type
  
  tags = {
    Name = "app-server"
  }
}
```

## Exercise 2: Variables and Outputs

Create:
1. Variables for environment, project name
2. Outputs for instance IP and ARN

```hcl
# variables.tf
variable "environment" {
  type    = string
  default = "dev"
}

# outputs.tf
output "public_ip" {
  value = aws_instance.app.public_ip
}
```

## Exercise 3: VPC Module

Create a VPC module with:
1. Public and private subnets
2. Internet gateway
3. Route tables

## Exercise 4: State Management

Configure S3 backend with:
1. Bucket for state storage
2. DynamoDB for locking

```hcl
terraform {
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "prod/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-locks"
  }
}
```

## Exercise 5: Working with Data Sources

Get latest Ubuntu AMI and use in instance.

```hcl
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]
  
  filter {
    name   = "name"
    values = ["ubuntu-minimal/images/*ubuntu-jammy-22.04-amd64*"]
  }
}

resource "aws_instance" "app" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"
}
```

## Exercise 6: Remote Provisioner

Configure remote-exec to:
1. Connect via SSH
2. Install Nginx

```hcl
connection {
  type        = "ssh"
  host        = self.public_ip
  user        = "ubuntu"
  private_key = file("~/.ssh/id_rsa")
}

provisioner "remote-exec" {
  inline = [
    "sudo apt-get update",
    "sudo apt-get install -y nginx"
  ]
}
```

## Exercise 7: Modules

Create and use a module:
1. Define network module
2. Call from root configuration

```hcl
# Root
module "vpc" {
  source  = "./modules/vpc"
  cidr    = "10.0.0.0/16"
  env     = var.environment
}
```

## Exercise 8: Workspaces

Create workspaces for dev/staging/prod and use conditional resources.

```bash
terraform workspace new prod
terraform workspace select prod
```

## Exercise 9: Import Existing Resources

Import an existing EC2 instance into Terraform state.

```bash
terraform import aws_instance.existing i-1234567890abcdef0
```

## Exercise 10: Complete Stack

Create a complete infrastructure with:
1. VPC
2. ECS cluster
3. RDS database
4. Load balancer

---

## Solutions

### Exercise 3: VPC Module

```hcl
# modules/vpc/main.tf
variable "cidr" {}
variable "environment" {}

resource "aws_vpc" "main" {
  cidr_block = var.cidr
  
  tags = {
    Name = "vpc-${var.environment}"
  }
}

resource "aws_subnet" "public" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.cidr, 4, count.index)
  availability_zone = "us-east-1${count.index == 0 ? "a" : "b"}"
  map_public_ip_on_launch = true
  
  tags = {
    Name = "public-subnet-${count.index}"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
}
```

### Exercise 8: Conditional Resources

```hcl
resource "aws_instance" "app" {
  count = terraform.workspace == "prod" ? 3 : 1
  
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = terraform.workspace == "prod" ? "t3.large" : "t3.micro"
  
  tags = {
    Name = "app-${terraform.workspace}-${count.index}"
  }
}
```

### Exercise 10: Complete Stack

```hcl
# main.tf
module "vpc" {
  source = "./modules/vpc"
  cidr   = "10.0.0.0/16"
}

module "ecs" {
  source            = "./modules/ecs"
  vpc_id            = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
}

module "rds" {
  source             = "./modules/rds"
  vpc_id             = module.vpc.vpc_id
  subnet_ids         = module.vpc.private_subnet_ids
  instance_class     = terraform.workspace == "prod" ? "db.t3.large" : "db.t3.micro"
}
```