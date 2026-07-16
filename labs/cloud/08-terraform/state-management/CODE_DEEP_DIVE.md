# Terraform State Code Deep Dive

This lab provides the configuration required to migrate from local state to a secure, locked remote backend using AWS S3 and DynamoDB.

## 💻 HCL Implementation

### 1. The Backend Configuration (`backend.tf`)

```hcl file="labs/cloud/08-terraform/state-management/SOLUTION/backend.tf"
terraform {
  backend "s3" {
    # The name of the S3 bucket to store the state file
    bucket         = "my-company-terraform-state"
    key            = "production/network/terraform.tfstate"
    region         = "us-east-1"

    # The DynamoDB table used for state locking
    dynamodb_table = "terraform-state-locks"
    
    # Enable server-side encryption for security
    encrypt        = true
  }
}
```

### 2. State Management Commands

Once the backend is configured, you use the CLI to interact with the state.

```bash
# 1. Initialize and migrate local state to S3
terraform init -migrate-state

# 2. List all resources currently in the state
terraform state list

# 3. Show detailed attributes of a specific resource
terraform state show aws_instance.web_server

# 4. Manually remove a resource from state (without destroying it in AWS)
# Useful if you want to stop managing a resource with Terraform
terraform state rm aws_instance.legacy_server

# 5. Import an existing AWS resource into your Terraform state
# Usage: terraform import <address> <id>
terraform import aws_s3_bucket.manual_bucket my-existing-bucket-name
```

## 🔍 Key Takeaways
1. **The Migration Process**: When you add the `backend` block and run `init`, Terraform detects that you have a local `terraform.tfstate` file and offers to upload its contents to S3.
2. **Encryption at Rest**: The `encrypt = true` flag is mandatory for production. Since state files often contain sensitive secrets (DB passwords, API keys), they must be encrypted in S3.
3. **State rm vs Destroy**: `terraform state rm` only deletes the record from the JSON file. The actual server in AWS continues to run. `terraform destroy` deletes the record AND kills the server.