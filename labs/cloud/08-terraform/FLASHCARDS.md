# Terraform IaC - FLASHCARDS

## Basics

### Card 1
**Q:** What is Terraform?
**A:** IaC tool for provisioning and managing cloud infrastructure using declarative configuration.

### Card 2
**Q:** Terraform workflow?
**A:** Write (config) → Plan (preview) → Apply (provision).

### Card 3
**Q:** What does `terraform init` do?
**A:** Initializes working directory, downloads providers, sets up backend.

### Card 4
**Q:** What does `terraform plan` do?
**A:** Creates execution plan, shows what changes will be made.

### Card 5
**Q:** What does `terraform apply` do?
**A:** Executes plan, creates/updates infrastructure.

## Resources

### Card 6
**Q:** Resource syntax?
**A:** `resource "type" "name" { ... }`

### Card 7
**Q:** How reference resource attribute?
**A:** `aws_instance.app.public_ip`

### Card 8
**Q:** What is data source?
**A:** Read-only info from existing infrastructure: `data "aws_ami" "ubuntu" { ... }`

### Card 9
**Q:** What is lifecycle?
**A:** Control resource update behavior: `lifecycle { create_before_destroy = true }`

### Card 10
**Q:** What is provisioner?
**A:** Execute scripts on local/remote after resource creation.

## Variables

### Card 11
**Q:** Define variable?
**A:** `variable "name" { type = string, default = "value" }`

### Card 12
**Q:** Use variable?
**A:** `var.name`

### Card 13
**Q:** What is local value?
**A:** Computed value for reuse within config: `locals { full_name = "${var.first}-${var.last}" }`

### Card 14
**Q:** Variable precedence (highest to lowest)?
**A:** 1. CLI -var, 2. -var-file, 3. ..auto.tfvars, 4. terraform.tfvars, 5. default

### Card 15
**Q:** Sensitive variable?
**A:** Mark with `sensitive = true` to hide from output.

## State

### Card 16
**Q:** What is state?
**A:** JSON file tracking real infrastructure, used to map config to reality.

### Card 17
**Q:** Local vs remote state?
**A:** Local: single user, no locking. Remote (S3): team sharing, state locking.

### Card 18
**Q:** State locking purpose?
**A:** Prevent concurrent modifications causing corruption.

### Card 19
**Q:** Never do with state?
**A:** Never commit to version control, never manually edit.

### Card 20
**Q:** Import existing resource?
**A:** `terraform import aws_instance.existing i-1234567890`

## Modules

### Card 21
**Q:** What is module?
**A:** Reusable, self-contained group of resources.

### Card 22
**Q:** Call module?
**A:** `module "vpc" { source = "./modules/vpc", ... }`

### Card 23
**Q:** Module sources?
**A:** Local (./), Registry (terraform-aws-modules/vpc/aws), Git (git::https://...)

### Card 24
**Q:** Output from module?
**A:** Define in module with `output "name" { value = ... }`

### Card 25
**Q:** Version module?
**A:** Add version constraint: `source = "module/version?ref=v1.0.0"`

## Workspaces

### Card 26
**Q:** What is workspace?
**A:** Named state container for same config with different vars.

### Card 27
**Q:** Default workspace?
**A:** "default" - always exists.

### Card 28
**Q:** Switch workspace?
**A:** `terraform workspace select prod`

### Card 29
**Q:** Workspace in state key?
**A:** `${terraform.workspace}/terraform.tfstate`

### Card 30
**Q:** When use workspaces vs directories?
**A:** Workspaces: similar envs (dev/staging/prod). Directories: completely different infra.

## Backend

### Card 31
**Q:** S3 backend benefits?
**A:** Shared access, state locking (DynamoDB), encryption, versioning.

### Card 32
**Q:** Backend configuration?
**A:** In terraform {} block: backend "s3" { bucket, key, region }

### Card 33
**Q:** Partial backend config?
**A:** Specify bucket/key at CLI: `terraform init -backend-config="bucket=..."`

## Provisioners

### Card 34
**Q:** Local-exec?
**A:** Run command on local machine after resource creation.

### Card 35
**Q:** Remote-exec?
**A:** Run command on remote resource via SSH/WinRM.

---

**Total: 35 flashcards**