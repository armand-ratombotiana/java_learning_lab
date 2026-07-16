# Terraform State Management

Welcome to the atomic mastery lab for **Terraform State**. This lab is part of the Cloud Academy's Infrastructure as Code module.

## 🧠 What You Will Master
- The purpose of the `terraform.tfstate` file.
- Remote State storage and locking (S3 + DynamoDB).
- State commands: `list`, `show`, `rm`, `import`.
- Handling state drift and manual interventions.
- State security and sensitive data management.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - Why state is the source of truth.
2. [INTERNALS.md](./INTERNALS.md) - State file schema and locking mechanics.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Configuring remote backends and state migration.