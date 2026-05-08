# Pedagogic Guide: Terraform

## Learning Path

### Phase 1: Foundation (Day 1)
- Learn Infrastructure as Code principles
- Understand Terraform workflow (init, plan, apply)
- Write basic configuration

### Phase 2: Core Skills (Day 2-3)
- Manage resources and data sources
- Use variables and outputs
- Structure with modules

### Phase 3: Advanced (Day 4-5)
- Configure state management
- Use workspaces for environments
- Implement best practices

## Key Concepts

| Concept | Description |
|---------|-------------|
| Provider | Plugin for cloud/API |
| Resource | Infrastructure component |
| Data Source | Read-only resource info |
| Variable | Input parameter |
| Output | Exposed value |
| Module | Reusable configuration |

## Prerequisites
- Basic cloud knowledge (AWS/Azure/GCP)
- Command line familiarity

## Common Pitfalls
- State drift from reality
- Secrets in state file
- Unlocked state corruption