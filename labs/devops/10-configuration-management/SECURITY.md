# Configuration Management Security

## Ansible Security
- **Ansible Vault**: Encrypt sensitive variables (`ansible-vault encrypt vars/secret.yml`).
- **SSH keys**: Use dedicated deploy keys, not personal keys.
- **become**: Limit privilege escalation; use `become_user` sparingly.
- **secrets lookup**: HashiCorp Vault, AWS Secrets Manager integration.
- **Control node security**: Restrict access to the Ansible control node.

## Puppet Security
- **SSL/TLS**: All agent-master communication encrypted.
- **Certificate signing**: Agent certificates must be signed by master.
- **RBAC**: Puppet Enterprise provides role-based access control.
- **Environment isolation**: Separate environments prevent cross-environment interference.

## Chef Security
- **SSL/TLS**: Chef Server and clients communicate over HTTPS.
- **Validator key**: Initial node registration; rotate after bootstrap.
- **Data bag encryption**: Encrypt sensitive data bags with shared secret.
- **knife client/key**: Manage client key rotation.

## General CM Security
- **Least privilege**: CM tools should have minimum required permissions.
- **Audit logging**: Track who changed what and when.
- **Secret rotation**: Regularly rotate SSH keys, API tokens, and passwords.
- **Version control**: All CM code in git with branch protection and PR review.
- **Compliance**: Use CM to enforce CIS benchmarks, STIGs, and security policies.
