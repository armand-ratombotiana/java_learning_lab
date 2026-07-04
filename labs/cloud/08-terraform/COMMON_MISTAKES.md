# Common Mistakes — Terraform

## State Mistakes

### 1. Local State for Team Projects
**Mistake**: Default local state file (terraform.tfstate) in Git.
**Issue**: State file contains secrets; conflicts when multiple team members run.
**Fix**: Remote state (S3 + DynamoDB locking) for all team projects.

### 2. No State Locking
**Mistake**: S3 backend without DynamoDB table.
**Issue**: Two team members can run apply simultaneously → state corruption.
**Fix**: Add `dynamodb_table = "terraform-locks"` to backend config.

### 3. Deleting State File
**Mistake**: Deleting terraform.tfstate or S3 state.
**Issue**: Terraform loses track of managed resources — can't modify or destroy.
**Fix**: Recover from state backup; or import resources back.

### 4. Committing .tfvars with Secrets
**Mistake**: terraform.tfvars with DB password committed to Git.
**Issue**: Secrets in version control.
**Fix**: Use `terraform.tfvars.example` (gitignored); use environment variables or Vault.

## Configuration Mistakes

### 5. Hardcoded Resource Names
**Mistake**: `bucket = "my-app-bucket"` (globally unique, already taken).
**Issue**: Apply fails with "BucketAlreadyExists".
**Fix**: Use `random_pet` or variable suffix: `bucket = "my-app-${random_id.bucket.hex}"`

### 6. Not Using `prevent_destroy`
**Mistake**: No lifecycle protection on production RDS/state bucket.
**Issue**: `terraform destroy` or accidental deletion wipes critical data.
**Fix**: `lifecycle { prevent_destroy = true }` on critical resources.

### 7. Too Many Resources in One State
**Mistake**: Entire infrastructure in single terraform.tfstate (500+ resources).
**Issue**: Slow plans (~10 min), large blast radius, team conflicts.
**Fix**: Separate state files per domain (networking, compute, database, monitoring).

### 8. Ignoring `terraform plan` Output
**Mistake**: `terraform apply` without reviewing plan.
**Issue**: Unexpected resource replacements, deletions, or security changes.
**Fix**: Always review plan output in CI/CD (plan step requires approval).

## Module Mistakes

### 9. Module Version Pinning
**Mistake**: `source = "terraform-aws-modules/vpc/aws"` (no version).
**Issue**: Breaking changes when module updates.
**Fix**: `source = "terraform-aws-modules/vpc/aws" version = "5.0.0"`

### 10. Overly Specific Modules
**Mistake**: Module that only works with exact CIDRs.
**Issue**: Can't reuse module for different environments.
**Fix**: Parameterize everything (variables with defaults).

## Variable Mistakes

### 11. Not Validating Variables
**Mistake**: No validation block for required format.
**Issue**: Wrong instance type, region, environment name.
**Fix**: `validation { condition = contains(["dev","prod"], var.env) ... }`

### 12. Overusing Count Instead of for_each
**Mistake**: Using `count` for resources that may have different configs.
**Issue**: Changing index order destroys all resources.
**Fix**: Use `for_each` with map keys for stable resource addresses.
