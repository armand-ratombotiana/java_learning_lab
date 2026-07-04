# Interview Questions — Terraform

## Beginner

**Q1**: What is Infrastructure as Code and why is it important?

**Q2**: Explain the difference between `terraform plan` and `terraform apply`.

**Q3**: What is a Terraform provider?

## Intermediate

**Q4**: How does Terraform manage state? Why is state important?

**Q5**: What are Terraform modules and why would you use them?

**Q6**: Explain the difference between `count` and `for_each`.

**Q7**: How do you handle secrets in Terraform?

## Advanced

**Q8**: Explain Terraform's execution plan and dependency graph.

**Q9**: How does Terraform's remote state locking work?

**Q10**: Design a Terraform architecture for a multi-team, multi-environment infrastructure.

**Q11**: What is Terraform drift and how do you detect/fix it?

**Q12**: Compare Terraform vs Pulumi vs CloudFormation for a Java microservice deployment.

## Sample Answers

**A1**: Infrastructure as Code (IaC) manages infrastructure (servers, networks, databases) through machine-readable definition files, not manual configuration. Benefits: version control, repeatability, audit trail, automated provisioning, disaster recovery.

**A2**: `terraform plan` creates an execution plan showing what resources will be created, modified, or deleted — without making changes. `terraform apply` executes the plan. Always review the plan before applying.

**A3**: A provider is a plugin that allows Terraform to manage resources in a specific platform (AWS, Azure, GCP, Kubernetes). Providers translate Terraform HCL into API calls to the target platform. Each provider has resources and data sources.

**A4**: State maps real-world infrastructure to your configuration. It contains resource IDs, attributes, metadata, and dependencies. State is stored in a file (terraform.tfstate) — local or remote (S3). Without state, Terraform wouldn't know what it manages. State enables planning, drift detection, and resource deletion.

**A5**: Modules are reusable, self-contained packages of Terraform configurations. They encapsulate related resources (e.g., a VPC module creates VPC + subnets + route tables). Benefits: reuse across projects, consistent patterns, simplified root configurations, versioning.

## Key Topics for Terraform Associate Exam
- Terraform workflow (init, plan, apply, destroy)
- HCL syntax (blocks, arguments, expressions, functions)
- Variables and outputs
- State management (local, remote S3, locking)
- Modules (registry, local, Git)
- Resource behavior (create, destroy, update)
- Workspaces for environment separation
- Provisioners (file, remote-exec, local-exec) and when to avoid them
- Data sources for reading existing infrastructure
- Terraform Cloud (remote operations, VCS, Sentinel)
- `terraform import`, `terraform taint`, `terraform state` commands
