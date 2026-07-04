# Common Terraform Mistakes

1. **Committing state files** — never commit `.tfstate` to git; use remote backends.
2. **Storing secrets in config** — use `sensitive = true`, variables, or Vault.
3. **Not using remote state** — losing local state = losing infrastructure management.
4. **Ignoring `terraform plan` output** — always review plan before apply.
5. **Hardcoding values** — use variables and locals for reusable configs.
6. **Missing provider version constraints** — leads to unexpected upgrades.
7. **Modifying resources outside Terraform** — causes drift; use `terraform import` instead.
8. **Using count when for_each is better** — count on list causes reindexing issues.
9. **Not using modules** — duplication across environments.
10. **Forgetting `terraform fmt`** — inconsistent formatting across team.
11. **Running `terraform apply` without plan file** — skip review step.
12. **Misunderstanding `create_before_destroy`** — leads to downtime or resource conflicts.
