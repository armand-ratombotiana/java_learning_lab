# Interview Questions — IaC Deep

## Beginner

Q: What is Terraform state and why is it important?
A: State maps real-world resources to configuration, tracks metadata, and enables team collaboration.

Q: What is the difference between Terraform and Pulumi?
A: Terraform uses HCL (declarative DSL); Pulumi uses real programming languages (TypeScript, Python, Java, Go).

## Intermediate

Q: How do Terraform workspaces work for environment isolation?
A: Each workspace has its own state file. Resources are named/prefixed differently per workspace.

Q: What are remote backends and why use them?
A: Remote backends store state remotely (S3, Terraform Cloud), enable locking, and support team collaboration.

## Advanced

Q: Design a multi-environment IaC strategy for 10 microservices.
A: Monorepo with shared modules, per-environment workspaces, remote backend with locking, pipeline-driven apply with plan approval.

Q: How would you implement drift detection and remediation?
A: Periodic plan execution comparing state to real resources, alerting on drift, auto-remediation via scheduled applies.
