# Terraform MOCK_INTERVIEW.md

## Scenario 1: State Management Disaster
A teammate ran `terraform apply` from their local machine and the state file is now corrupted.

**Questions**:
1. How would you recover from a corrupted state file?
2. What's the best practice for state storage and locking?
3. How do you handle state file conflicts in a team?
4. What's the difference between `terraform import` and `terraform state mv`?

**Expected approach**: Remote state (S3 + DynamoDB locking, GCS, Azure Storage). Recovery: restore from backup state, `terraform state pull`/`push`, `terraform import`. Team workflow: remote state, state locking, CI pipeline runs apply, no local applies for production.

## Scenario 2: Module Design
Your team has 10 projects that each define VPC, subnets, and security groups separately.

**Questions**:
1. Design a reusable Terraform module for a VPC.
2. How do you version modules? (Git tags, registry?)
3. How do you handle module input validation?
4. How do you test Terraform modules?

**Expected approach**: Module with `variable` declarations (CIDR blocks, AZs, tags), `outputs` (VPC ID, subnet IDs), versioned via Git tags or Terraform Registry. Input validation via `validation` block. Testing via Terratest (Go), `terraform validate`, `terraform plan`, checkov.

## Scenario 3: Multi-Environment Infrastructure
You need to manage dev, staging, and prod environments with Terraform.

**Questions**:
1. How would you structure your Terraform directories?
2. Compare workspaces vs directory-per-environment.
3. How do you manage environment-specific variables?
4. How do you handle secrets in Terraform?

**Expected approach**: Directory structure: `environments/dev`, `environments/staging`, `environments/prod` with separate state. Workspaces for simpler cases. Variables via `terraform.tfvars` per environment, `-var-file`. Secrets via Vault, AWS Secrets Manager, or encrypted via SOPS.

## Scenario 4: Terraform with Kubernetes
You need to provision an EKS cluster and deploy applications using Terraform.

**Questions**:
1. How do you provision EKS with Terraform?
2. How do you manage the kubeconfig for Terraform to deploy to the cluster?
3. How do you handle the chicken-and-egg problem of provisioning + deploying?
4. What Terraform providers do you need?

**Expected approach**: AWS provider for EKS, Kubernetes/Helm providers for in-cluster resources. Data source for EKS cluster auth. Use `terraform_data` or null resource for post-provisioning config. Order: VPC → EKS → (kubeconfig) → K8s resources. Or use two stacks.

## Scenario 5: Terraform at Scale
Your Terraform configuration has 2000+ resources and `terraform plan` takes 5 minutes.

**Questions**:
1. How would you refactor for faster performance?
2. How do you break a monolithic Terraform into manageable pieces?
3. What's a data source and how does it affect `plan` time?
4. How would you use `terraform_remote_state` vs data sources?

**Expected approach**: Break into independent modules/workspaces/stacks. Use `terraform state list` and `terraform state mv`. Use targeted applies (`-target`) for emergencies. Use `depends_on` sparingly. Use data sources judiciously (they query APIs each plan).

## Key Terraform Interview Questions
1. Explain the Terraform execution plan (init → plan → apply → destroy).
2. What's the difference between `terraform apply` and `terraform plan`?
3. How does Terraform handle dependencies between resources?
4. Explain Terraform's `lifecycle` meta-argument.
5. What's a `provider` and how do you configure multiple providers?
6. Explain `count` vs `for_each`. When to use each?
7. What's the difference between `locals` and `variables`?
8. How do you handle drift detection and remediation?
9. What's `terraform taint` and when would you use it?
10. Explain the structure of a Terraform provider.

## Whiteboard Challenge
Design a Terraform module structure for a multi-service, multi-environment AWS infrastructure with networking, ECS, RDS, and monitoring.

## Follow-up
1. How would you migrate resources from one state file to another?
2. How would you add policy as code (Sentinel/OPA)?
3. How would you handle secrets rotation in Terraform?