# Configuration Management Theory

## Core Concepts
- **Idempotency**: Running the same configuration multiple times produces the same result.
- **Desired State**: Declare the target state; tools enforce it automatically.
- **Drift Detection**: Detect and correct unauthorized changes to configuration.
- **Pull vs Push**: Agent-based (pull) vs agentless (push) models.

## Tools Comparison
| Feature | Ansible | Puppet | Chef |
|---------|---------|--------|------|
| Architecture | Push (agentless) | Pull (agent) | Pull (agent) |
| Language | YAML | DSL (Ruby-based) | Ruby DSL |
| Idempotent | Yes | Yes | Yes |
| Learning curve | Low | Medium | High |
| Windows support | Good | Good | Good |
| Orchestration | Built-in | Built-in | Via third-party |

## Ansible Concepts
- **Playbook**: YAML file defining configuration policies.
- **Play**: Maps hosts to tasks.
- **Task**: Single action (install package, start service, copy file).
- **Module**: Reusable unit of work (apt, yum, copy, template, service).
- **Role**: Organizational unit for playbooks, files, templates, variables.
- **Inventory**: List of managed hosts (static file or dynamic from cloud).

## Puppet Concepts
- **Manifest**: .pp file with resource declarations.
- **Class**: Collection of resources (like a role).
- **Module**: Collection of manifests and files.
- **Facter**: System inventory tool (facts: OS, IP, memory, etc.).
- **Catalog**: Compiled manifest applied to node.

## Chef Concepts
- **Cookbook**: Collection of recipes, templates, and files.
- **Recipe**: Ruby DSL file with resources.
- **Resource**: Package, service, file, template, etc.
- **Ohai**: Node inventory data collector.
- **Knife**: CLI tool for managing Chef infrastructure.
