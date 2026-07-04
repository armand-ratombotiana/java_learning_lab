# Terraform Debugging Guide

## Debug Commands
```powershell
# Enable debug logging
$env:TF_LOG="DEBUG"
terraform apply 2> debug.log

# Validate syntax
terraform validate

# Format configuration
terraform fmt -check

# Show current state
terraform show

# List resources in state
terraform state list

# Show specific resource
terraform state show aws_instance.web

# Import existing resource
terraform import aws_instance.web i-1234567890abcdef0

# Refresh state without changes
terraform refresh
```

## Common Issues
- **Provider errors**: Check credentials, region, API endpoint.
- **State locking failures**: Release lock with `terraform force-unlock <lock_id>`.
- **Resource already exists**: Import or remove from state with `terraform state rm`.
- **Dependency cycle**: Check for circular references between resources.
- **Rate limiting**: Add retry logic or increase timeouts for cloud APIs.
