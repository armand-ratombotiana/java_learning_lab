# Why Secrets Management Exists

## The Problem
- **Hardcoded passwords in code**: Leaked via version control, code review, or compromise.
- **Static credentials**: Long-lived passwords never change — stolen once, used forever.
- **Secret sprawl**: Passwords stored in spreadsheets, wikis, chat, config files.
- **No rotation**: Password rotation is manual, error-prone, often skipped.
- **Weak access control**: Anyone with server access can read secrets from env vars or config files.
- **No audit trail**: Can't determine who accessed which secret when.

## Secrets Management Solution
- **Centralized vault**: All secrets in one secure location with access control.
- **Dynamic secrets**: Short-lived credentials (auto-generated, auto-expired).
- **Rotation policies**: Automated rotation without application downtime.
- **Access control**: Granular policies per secret/application/user.
- **Audit logging**: Complete access history for compliance.
- **Encryption**: Secrets encrypted at rest and in transit.
