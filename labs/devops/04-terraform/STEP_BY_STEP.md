# Step-by-Step Terraform Guide

## 1. Install Terraform
```powershell
# Download from terraform.io or use choco
choco install terraform
terraform --version
```

## 2. Create First Configuration
```powershell
mkdir first-terraform && cd first-terraform
```

## 3. Write main.tf
```hcl
terraform {
  required_providers {
    local = {
      source = "hashicorp/local"
      version = "~> 2.0"
    }
  }
}

resource "local_file" "hello" {
  content  = "Hello, Terraform!"
  filename = "${path.module}/hello.txt"
}
```

## 4. Initialize
```powershell
terraform init
```

## 5. Plan
```powershell
terraform plan
```

## 6. Apply
```powershell
terraform apply -auto-approve
cat hello.txt
```

## 7. Modify and Reapply
```hcl
content = "Hello, Terraform v2!"
```
```powershell
terraform plan
terraform apply -auto-approve
```

## 8. Destroy
```powershell
terraform destroy -auto-approve
```

## 9. Add AWS Provider (if AWS access available)
```hcl
provider "aws" { region = "us-east-1" }
resource "aws_s3_bucket" "data" {
  bucket = "my-tf-test-bucket-${random_id.suffix.hex}"
}
resource "random_id" "suffix" {
  byte_length = 4
}
```
