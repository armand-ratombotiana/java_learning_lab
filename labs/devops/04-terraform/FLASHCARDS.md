# Terraform Flashcards

**Q: What is Terraform?**
A: Infrastructure as Code tool by HashiCorp for provisioning cloud resources.

**Q: What is HCL?**
A: HashiCorp Configuration Language — declarative language for Terraform.

**Q: What is a provider?**
A: Plugin that enables Terraform to interact with cloud APIs.

**Q: What is state in Terraform?**
A: JSON file tracking managed resource metadata and dependencies.

**Q: What is a module?**
A: Reusable collection of Terraform resources with inputs/outputs.

**Q: What does `terraform init` do?**
A: Initializes directory, downloads providers, configures backend.

**Q: What does `terraform plan` do?**
A: Creates execution plan showing what changes will be made.

**Q: What is remote state?**
A: State stored in a shared backend (S3, GCS, Consul) for team collaboration.

**Q: What is `terraform import` used for?**
A: Brings existing infrastructure under Terraform management.

**Q: What is the Terraform dependency graph?**
A: A DAG of resources that determines creation and deletion order.
