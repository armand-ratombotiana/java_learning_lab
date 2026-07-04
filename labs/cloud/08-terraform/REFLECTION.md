# Reflection — Terraform

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| HCL syntax and basic resources | ☐ | ☐ | ☐ |
| Variables, outputs, locals | ☐ | ☐ | ☐ |
| State management (local/remote) | ☐ | ☐ | ☐ |
| Modules (create, use, publish) | ☐ | ☐ | ☐ |
| Workspaces and environment separation | ☐ | ☐ | ☐ |
| Data sources and remote state | ☐ | ☐ | ☐ |
| Terraform Cloud / Terraform Enterprise | ☐ | ☐ | ☐ |
| Security (secrets, state encryption, IAM) | ☐ | ☐ | ☐ |
| Sentinel policies and compliance | ☐ | ☐ | ☐ |

## Journal Prompts

1. How does Terraform change the way you think about deploying infrastructure?

2. What was the most challenging concept in this lab (state, modules, graph)?

3. How would you design Terraform configurations for a team of 10 developers?

4. When would you use Terraform workspaces vs separate state files per environment?

5. How does Infrastructure as Code affect incident response and disaster recovery?

## Key Takeaways
- Terraform = declarative IaC for any cloud/API-based platform
- State is the source of truth — protect it (remote backend, locking, encryption)
- Modules enable reuse, consistency, and maintainability
- Always review `terraform plan` before `apply`
- Never hardcode secrets — use variables, environment, or Secrets Manager
- Workspaces separate environments with same code
- `terraform import` brings existing resources under management
- Use `prevent_destroy` for critical resources
- Keep state files focused (split by domain/environment)
- Terraform Cloud adds collaboration, policy, and cost estimation
