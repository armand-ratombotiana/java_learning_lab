# Refactoring — Terraform

## 1. From Hardcoded Resources to Variables

### Before
```hcl
resource "aws_instance" "web" {
  ami           = "ami-0c55b159cbfafe1f0"
  instance_type = "t3.micro"
}
```

### After
```hcl
variable "ami_id" {
  type        = string
  description = "AMI ID for EC2 instances"
}

variable "instance_type" {
  type    = string
  default = "t3.micro"
}

resource "aws_instance" "web" {
  ami           = var.ami_id
  instance_type = var.instance_type
}
```

## 2. From Single File to Modules

### Before
```hcl
# main.tf (everything — 300 lines)
resource "aws_vpc" "main" { ... }
resource "aws_subnet" "public" { ... }
resource "aws_instance" "web" { ... }
resource "aws_db_instance" "main" { ... }
# Hard to reuse, test, or change
```

### After
```
terraform-project/
├── main.tf          # Module calls only (~30 lines)
├── modules/
│   ├── vpc/         # Reusable VPC module
│   ├── compute/     # ALB + ASG module
│   └── database/    # RDS module
├── environments/
│   ├── dev/         # Dev-specific .tfvars
│   └── prod/        # Prod-specific .tfvars
```
```hcl
# main.tf
module "vpc" {
  source   = "./modules/vpc"
  env_name = var.environment
  vpc_cidr = var.vpc_cidr
}

module "compute" {
  source     = "./modules/compute"
  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnet_ids
}

module "database" {
  source     = "./modules/database"
  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.database_subnet_ids
}
```

## 3. From Local State to Remote State

### Before
```hcl
terraform {
  # No backend block = local state
}
# State file: terraform.tfstate
# Lost if disk fails; no team collaboration
```

### After
```hcl
terraform {
  backend "s3" {
    bucket         = "company-terraform-state"
    key            = "java-app/${var.environment}/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
```

## 4. From Single Environment to Workspaces

### Before
```
Separate directories for dev/prod:
  terraform/    ├── dev/
                └── prod/
  Code duplicated, drift between environments
```

### After
```
Single directory with workspaces:
  terraform workspace new dev
  terraform workspace new prod

terraform workspace select dev
terraform apply -var-file="dev.tfvars"

terraform workspace select prod
terraform apply -var-file="prod.tfvars"
```

## 5. From Null-Resource to Native Resource

### Before
```hcl
resource "null_resource" "provision" {
  provisioner "local-exec" {
    command = "aws s3api create-bucket --bucket my-bucket"
  }
}
# Not managed by Terraform state — can't update/delete properly
```

### After
```hcl
resource "aws_s3_bucket" "main" {
  bucket = "my-app-${random_id.suffix.hex}"
}
# Managed, tracked, can update, will be deleted on destroy
```
