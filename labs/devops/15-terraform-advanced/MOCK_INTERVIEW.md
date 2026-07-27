# Terraform Advanced MOCK_INTERVIEW.md

## Scenario 1: Terraform Provider Development
Your internal infrastructure tool has no Terraform provider. You need to build one.

**Questions**:
1. What's the Terraform provider framework?
2. How do you define resources and data sources?
3. How do you handle CRUD operations?
4. How do you test a Terraform provider?

**Expected approach**: Terraform Plugin Framework (new) or SDK v2 (legacy). Define `schema.Resource` with CRUD functions. Testing via `resource.Test`, acceptance tests with real API. Use `tfprotov6`/`tfprotov5` for protocol-level. Use `terraform-plugin-testing` library.

## Scenario 2: Terraform at Enterprise Scale
100+ engineers are using Terraform. You need governance.

**Questions**:
1. How do you enforce policy as code?
2. How do you manage state across teams?
3. How do you handle Terraform version upgrades?
4. How do you approve changes?

**Expected approach**: Policy: Sentinel (Terraform Cloud) or OPA (open source). State management: Terraform Cloud workspaces, remote state per team/component. Version: `required_version` in config, CI/CD with consistent version. Approval: VCS PR workflow, Terraform Cloud run checks, manual applies for production.

## Scenario 3: Error Handling and Debugging
`terraform apply` fails with a cryptic error. Resources are in an unknown state.

**Questions**:
1. How do you debug Terraform errors?
2. How do you recover from partial applies?
3. What's `terraform state rm` and `terraform import`?
4. How do you use `TF_LOG`?

**Expected approach**: `TF_LOG=DEBUG` for verbose logs. Partial apply: `terraform apply` again (re-runs failed resources). `terraform state rm` to remove unmanaged resources, `terraform import` to re-add. `terraform state list` to see known resources. Use `terraform plan` to see what needs to change.

## Scenario 4: Advanced State Operations
You need to refactor Terraform configuration without destroying resources.

**Questions**:
1. How do you move resources between state files?
2. How do you refactor from `count` to `for_each`?
3. How do you split a monolith into modules?
4. How do you handle resource renaming?

**Expected approach**: `terraform state mv` to move resources. `moved` block (Terraform 1.1+) for refactoring without state commands. `count` to `for_each`: `terraform state mv 'module.x[0]' 'module.x["a"]'` (one by one). Splitting: `terraform state mv` between backends. Renaming: `moved` block or `removed` block.

## Scenario 5: Multi-Cloud Terraform
You need to manage resources across AWS, GCP, and Azure.

**Questions**:
1. How do you organize multi-cloud configurations?
2. How do you handle provider authentication?
3. How do you manage cross-cloud networking?
4. How do you ensure consistency?

**Expected approach**: Organization: modules per cloud, root config per environment. Provider aliases for multiple regions. Auth: different provider configs per cloud, environment variables, or assume roles. Cross-cloud networking: VPN/peering via cloud-specific modules, or cloud-agnostic via Consul/Terraform.

## Key Advanced Terraform Interview Questions
1. Explain Terraform's internal graph and how it handles dependencies.
2. What's the `moved` block and how does it help refactoring?
3. How does Terraform Cloud work?
4. Explain the `sensitive` parameter and how it affects output.
5. How do you handle preconditions and postconditions?
6. What's the `import` block (Terraform 1.5+) vs `terraform import`?
7. How do you use `check` blocks?
8. Explain the `terraform test` command.
9. How do you integrate Terraform with CI/CD?
10. What's the difference between `terraform plan` and `terraform apply` in automation?

## Whiteboard Challenge
Design a Terraform-based multi-cloud infrastructure platform with:
- Shared networking (VPC/VNet/VPC per cloud)
- Application modules (ECS/GKE/AKS)
- Database modules (RDS/Cloud SQL/Azure DB)
- Policy enforcement (OPA/Sentinel)
- CI/CD integration with approval gates

## Follow-up
1. How would you handle provider authentication across clouds?
2. How would you implement cost tagging?
3. How would you handle Terraform upgrades across the organization?