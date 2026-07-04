# Security — Terraform

## Secrets Management

### Never Hardcode Secrets
```hcl
# ❌ BAD: Secret in code
variable "db_password" {
  default = "SuperSecret123!"
}

# ✅ GOOD: Read from environment
variable "db_password" {
  type      = string
  sensitive = true
}
# Set via: export TF_VAR_db_password="actual-value"

# ✅ BETTER: Read from AWS Secrets Manager
data "aws_secretsmanager_secret_version" "db" {
  secret_id = "prod/db/password"
}
resource "aws_db_instance" "main" {
  password = data.aws_secretsmanager_secret_version.db.secret_string
}
```

### Sensitive Variables in Output
```hcl
# ✅ Mark outputs as sensitive
output "db_password" {
  value     = aws_db_instance.main.password
  sensitive = true
}
# terraform output shows: <sensitive>
# terraform output -json: still shows (redacted in logs)
```

## State File Encryption

### S3 Backend Encryption
```hcl
terraform {
  backend "s3" {
    bucket         = "terraform-state"
    key            = "prod/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true  # Server-side encryption
    kms_key_id     = "arn:aws:kms:us-east-1:xxx:key/yyy"  # Customer-managed key
    dynamodb_table = "terraform-locks"
  }
}
```

### State File Contains:
- Resource IDs (VPC IDs, EC2 IDs)
- Resource attributes (including some secrets if exposed as attributes)
- Provider configuration

## IAM Policies for Terraform

### Least Privilege for CI/CD
```json
{
  "Effect": "Allow",
  "Action": [
    "ec2:*",
    "s3:*",
    "rds:*",
    "elasticloadbalancing:*"
  ],
  "Resource": "*",
  "Condition": {
    "ForAnyValue:StringEquals": {
      "aws:ResourceTag/ManagedBy": "Terraform"
    }
  }
}
```

### Terraform State Bucket Policy
```json
{
  "Effect": "Deny",
  "Principal": "*",
  "Action": "s3:DeleteBucket",
  "Resource": "arn:aws:s3:::terraform-state",
  "Condition": {
    "Bool": {
      "aws:SecureTransport": "false"
    }
  }
}
```

## Sentinel (Policy as Code) — Terraform Cloud

```python
# require-mandatory-tags.sentinel
import "tfplan"

main = rule {
  all tfplan.resources as _, resource {
    all resource.applied as address, instance {
      instance.tags contains "Environment" else true
      instance.tags contains "Owner" else true
      instance.tags contains "CostCenter" else true
    }
  }
}
```
- Enforce security/compliance policies before apply
- Allowed: tags, encryption, instance types, regions, CIDR ranges
