# Configuration Management MOCK_INTERVIEW.md

## Scenario 1: Ansible at Scale
Your team manages 500 servers with Ansible. Playbooks take 30 minutes to run.

**Questions**:
1. How would you speed up Ansible execution?
2. Explain Ansible pull vs push model.
3. How do you handle secret variables in Ansible?
4. How do you test Ansible playbooks?

**Expected approach**: Speed: `forks` setting, `serial` for rolling, `async` for long tasks, `strategy: free`. Pull model for large fleets, push for smaller. Secrets: Ansible Vault, or integration with Vault/AWS Secrets Manager. Testing: `ansible-playbook --check`, Molecule, `--syntax-check`.

## Scenario 2: Configuration Drift
Servers configured by Ansible are drifting from desired state.

**Questions**:
1. How do you detect configuration drift?
2. How do you remediate drift automatically?
3. How do you alert on drift?
4. What's the difference between mutable and immutable infrastructure?

**Expected approach**: Detection: periodic `ansible-playbook --check`, compliance scanning (InSpec, OpenSCAP), scheduled remediation. Remediation: cron job or CI pipeline that runs playbooks periodically. Mutable: Ansible/Chef/Puppet — continuous enforcement. Immutable: AMI/images rebuilt, replaced rather than patched.

## Scenario 3: Multi-OS Configuration
Your servers run Ubuntu, CentOS, and Windows. You need a unified config management.

**Questions**:
1. How does Ansible handle multiple OS families?
2. How do you write OS-agnostic roles?
3. How do you handle package name differences?
4. How would you test across OS families?

**Expected approach**: Fact gathering (ansible_os_family, ansible_distribution), vars files per OS (vars/RedHat.yml, vars/Debian.yml), `when` conditions. Package names via `{{ apache_package }}` variable per OS. Testing: Molecule with multiple platforms, Vagrant or Docker.

## Scenario 4: Infrastructure as Code + Config Mgmt
You need both provisioning (Terraform) and configuration management (Ansible).

**Questions**:
1. How do Terraform and Ansible work together?
2. Where's the boundary between them?
3. How do you pass Terraform outputs to Ansible?
4. What's the execution order?

**Expected approach**: Terraform provisions infrastructure (VMs, networks, load balancers). Ansible configures the VMs (packages, configs, services). Terraform outputs (IPs, hostnames) pass to Ansible via inventory files or dynamic inventory. Run Terraform first, then Ansible. Or use `local-exec` in Terraform to trigger Ansible.

## Scenario 5: Immutable Infrastructure Migration
You need to migrate from mutable servers (Ansible-managed) to immutable (Packer + Terraform).

**Questions**:
1. What's the benefit of immutable infrastructure?
2. How do you build server images?
3. How do you handle stateful data?
4. What's the migration strategy?

**Expected approach**: Immutable: no patching, rebuild + replace. Images via Packer (AMI, GCE Image). Stateful data offloaded to managed services (RDS, ElastiCache, S3). Migration: new services on immutable, old services migrate gradually via blue-green or canary.

## Key Configuration Management Interview Questions
1. What's the difference between configuration management and orchestration?
2. Explain idempotency in configuration management.
3. How does Ansible work under the hood (SSH, modules, facts)?
4. Explain Ansible playbooks, roles, and inventories.
5. What's the difference between Ansible, Chef, and Puppet?
6. How do you handle configuration for ephemeral environments?
7. What's the difference between push and pull models?
8. How do you manage secrets in configuration management?
9. Explain the concept of infrastructure as code.
10. How do you handle configuration validation and testing?

## Whiteboard Challenge
Design a configuration management strategy for a 1000-server fleet with mixed OS (Ubuntu, RHEL, Windows). Consider secrets, compliance, drift detection, and integration with provisioning (Terraform).

## Follow-up
1. How would you handle compliance auditing (CIS benchmarks)?
2. How would you integrate with service discovery?
3. How would you handle server bootstrapping (first run)?