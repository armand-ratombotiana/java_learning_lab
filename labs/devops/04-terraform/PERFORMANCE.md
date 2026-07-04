# Terraform Performance

## Optimization Tips
- **Parallelism**: Use `terraform apply -parallelism=N` (default 10).
- **State size**: Large states (>100MB) slow down operations; split into workspaces.
- **Refresh**: Skip refresh with `-refresh=false` for faster plans (be careful).
- **Targeted applies**: `terraform apply -target=resource` for focused changes (use sparingly).
- **Provider caching**: Cache provider plugins locally.

## State Optimization
- Use remote state backends with locking (S3 + DynamoDB).
- Partition state by environment and region.
- Use `terraform state pull` / `terraform state push` for state maintenance.
- Implement state file lifecycle policies (versioning, expiration).

## Large Infrastructure Management
- Split configurations by service/team.
- Use `terraform_remote_state` data source sparingly (creates coupling).
- Consider Terragrunt for DRY configuration management.
- Use workspaces for environment isolation.

## Common Bottlenecks
- API rate limiting (cloud providers)
- Large numbers of resources in one state file
- Slow provider operations (DNS propagation, database creation)
- Network latency to remote backends
