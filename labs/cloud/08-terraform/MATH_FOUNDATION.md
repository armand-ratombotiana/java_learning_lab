# Math Foundation for Terraform

## State Size and Performance

### State File Size
```
Resources │ State size │ plan time
──────────┼────────────┼────────────
100       │ ~50KB      │ 5-10s
1000      │ ~500KB     │ 30-60s
10000     │ ~5MB       │ 3-10 min

Best practices:
  - Split large infra into multiple state files (stacks)
  - Use remote state data source (terraform_remote_state)
  - Target specific resources: terraform apply -target=resource
```

## Plan Optimization

### Parallelism
```
terraform apply -parallelism=N
  Default: 10 (simultaneous resource operations)
  Max: depends on API rate limits
  AWS: 10-20 safe
  For 100 resources: 100/10 = 10 waves
  For 1000 resources: 1000/10 = 100 waves

With -parallelism=20:
  1000/20 = 50 waves (2x speedup)
```

## Cost Estimation

### Resource Cost Math
```
Terraform plan output shows resource changes.
Cost estimation (Infracost plugin):

Example cost for Java app infra:
  VPC:                $0.00
  NAT Gateway (×2):   $64.80/month
  ALB:                $22.50/month
  EC2 t3.medium (×3): $248.40/month (on-demand)
  RDS db.r5.large:    $175.20/month
  S3 bucket:          $0.023/GB/month
  ElastiCache:        $124.20/month
  Total: ~$635/month
```

## Refresh Time Math

### State Refresh
```
terraform plan (refresh = true):
  1. List all resources from state (N resources)
  2. Read each resource via API (N API calls)
  3. Compare with state

For 100 resources: 100 API calls
AWS API rate limits: ~100 req/s
Estimated refresh time: 2-5s

For 1000 resources: 1000 API calls
Estimated refresh time: 15-30s
```

## Module Versioning

### Module Resolution
```
Terraform Registry modules:
  terraform-aws-modules/vpc/aws v5.0.0
  ├── Downloads module (cached locally)
  ├── Resolves transitive dependencies
  └── Stores in .terraform/modules/

Version constraints:
  version = "~> 5.0.0"  # >= 5.0.0, < 5.1.0
  version = ">= 4.0, < 6.0"  # 4.x or 5.x
  version = "~> 5.0"  # >= 5.0, < 6.0

Lock file: .terraform.lock.hcl (commit to Git)
```
