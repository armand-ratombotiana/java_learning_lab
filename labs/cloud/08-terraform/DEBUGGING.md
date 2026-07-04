# Debugging — Terraform

## Plan/Apply Errors

### Resource Already Exists
```
Error: "creating EC2 Instance: InvalidParameterValue: Value (i-xxx) for parameter
instanceId is invalid."
Check:
  - Resource already exists in AWS but not in state
  - Run terraform import to bring it under management
  - Or: remove resource and let Terraform create new one
```

### API Rate Limiting
```
Error: "RequestLimitExceeded" (Throttling)
Check:
  - AWS API rate limits exceeded
Fix:
  - Reduce parallelism: terraform apply -parallelism=5
  - Add retry configuration:
    provider "aws" {
      max_retries = 5
    }
```

### Credential Issues
```
Error: "No valid credential sources found"
Check:
  - AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY set?
  - AWS_PROFILE configured?
  - IAM role valid (for EC2/CI)?
  - Default region set?
```

## State Debugging

### State Lock Error
```
Error: "Error acquiring the state lock"
Check:
  - Another team member running terraform?
  - Stale lock from crashed process?
Fix:
  terraform force-unlock <LOCK_ID>  # Only if sure no one is running
```

### State Drift
```
Symptom: Plan shows changes for resources you didn't modify
Check:
  - Manual AWS Console changes?
  - Resource imported without full attributes?
Fix:
  - terraform apply refreshes to actual state
  - Or: update configuration to match current state
  - Use ignore_changes for attributes modified outside TF
```

## Module Debugging

### Module Not Found
```
Error: "Module not found" or "Failed to download module"
Check:
  - Source URL correct?
  - Registry module exists at version?
  - Git SSH keys configured (for private modules)?
Fix:
  terraform init  # Downloads all modules
  terraform providers  # Lists all provider sources
```

### Invalid Module Variable
```
Error: "Unsupported argument" in module call
Check:
  - Module's variables.tf — does this variable exist?
  - Variable type matches?
  - Required variable supplied?
Fix:
  - Read module's variables.tf for exact names
  - Use terraform-docs or module registry docs
```

## Provisioner Debugging

```hcl
# Debug provisioner output
resource "aws_instance" "web" {
  provisioner "remote-exec" {
    inline = [
      "echo 'Starting provisioner...'",
      "java -version",
      "systemctl status app"
    ]
    connection {
      type        = "ssh"
      host        = self.public_ip
      user        = "ec2-user"
      private_key = file("~/.ssh/id_rsa")
    }
  }
}
```
- Check security group allows SSH from Terraform host
- Debug: `connection { ... timeout = "5m" }` to give more time
- Alternative: use user_data instead of provisioner (idempotent)

## Output Debugging

```bash
# Show all resource attributes
terraform show

# Query specific attribute
terraform output -json | jq '.alb_dns_name.value'

# Debug logging
TF_LOG=DEBUG terraform apply  # Verbose logs
TF_LOG_PATH=./terraform.log terraform plan
```
