# Why Terraform Exists

## The Problem
- **Manual infrastructure management**: Clicking through cloud consoles, error-prone.
- **Configuration drift**: Manual changes make actual state diverge from documented state.
- **No version control**: Infrastructure changes not tracked or reviewed.
- **Non-reproducible environments**: Each environment configured differently.
- **Vendor lock-in**: Cloud-specific tools (CloudFormation, ARM templates) don't work across providers.

## Terraform's Solution
- **Declarative configuration**: Define what you want, not how to do it.
- **Provider ecosystem**: 2000+ providers for clouds, SaaS, databases, DNS, etc.
- **State management**: Track what's deployed; detect and reconcile drift.
- **Plan/Apply workflow**: Preview changes before making them; safe, auditable.
- **Modularity**: Reusable modules for common patterns.
- **Immutability**: Infrastructure changes are always incremental and predictable.
