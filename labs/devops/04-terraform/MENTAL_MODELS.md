# Mental Models for Terraform

## 1. Blueprint Analogy
- **Terraform config** = Architectural blueprint
- **terraform plan** = Engineer review of blueprint
- **terraform apply** = Construction
- **terraform destroy** = Demolition
- **State file** = As-built drawings (what was actually built)

## 2. Desired State Reconciliation
Like Kubernetes: you define desired state, Terraform computes the diff between desired and current state, then generates a plan to reach desired state from current state.

## 3. Directed Acyclic Graph (DAG)
Terraform builds a dependency graph. Resources that depend on others are created after their dependencies. Independent resources are created in parallel. This prevents ordering errors.

## 4. Immutable Infrastructure
Rather than fixing a broken server, destroy and recreate it. Terraform's `create_before_destroy` and `prevent_destroy` meta-arguments support this pattern.
