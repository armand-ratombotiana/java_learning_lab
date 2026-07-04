# Performance — Terraform

## Plan Speed Optimization

### Targeted Plans
```bash
# Plan only specific resource (faster for large states)
terraform plan -target=module.compute.aws_instance.web

# Apply only specific module
terraform apply -target=module.vpc
```

### Refresh on Demand
```bash
# Skip refresh for faster plan (use cached state)
terraform plan -refresh=false

# Refresh only specific resources
terraform plan -target=aws_instance.web -refresh=true
```

### State Filtering
```bash
# List resources without reading full state
terraform state list

# Show only specific resource
terraform state show aws_instance.web
```

## Parallelism Tuning

```bash
# Default 10 — increase for large deployments
terraform apply -parallelism=20

# For AWS: 10-20 safe
# For local providers: 1-5 (avoid race conditions)
# For many independent resources: 20-30
```

## Provider Cache

```bash
# Enable plugin cache (reuse across projects)
export TF_PLUGIN_CACHE_DIR="$HOME/.terraform.d/plugin-cache"
mkdir -p $TF_PLUGIN_CACHE_DIR

# Set in .terraformrc:
# plugin_cache_dir = "$HOME/.terraform.d/plugin-cache"
```

## State File Optimization

### Split State Files
```bash
# Separate state per environment:
terraform/state/
├── networking.tfstate    # VPC, subnets, IGW
├── compute.tfstate       # ASG, ALB, EC2
├── database.tfstate      # RDS, ElastiCache
└── monitoring.tfstate    # CloudWatch, SNS

# Each has smaller state → faster plans
# Each can be locked independently
# Smaller blast radius
```

### State Refresh Strategies
```
State size  │ Refresh strategy
────────────┼─────────────────
< 100       │ Full refresh (default)
100-500     │ Targeted refresh per workspace
500-1000    │ Split into multiple state files
> 1000      │ Multi-state + targeted operations
```

## CI/CD Integration

### GitHub Actions
```yaml
- name: Terraform Plan
  run: |
    terraform init
    terraform plan -out=tfplan
  
- name: Terraform Apply
  if: github.ref == 'refs/heads/main'
  run: terraform apply tfplan
```

### Terraform Cloud
```
Run tasks:
  - Sentinel policy checks (400ms)
  - Cost estimation (2-5s per run)
  - Config validation (500ms)
Total overhead: ~10-20s per run
```
