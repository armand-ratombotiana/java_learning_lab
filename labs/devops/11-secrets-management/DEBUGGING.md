# Secrets Management Debugging Guide

## Vault Debug Commands
```powershell
# Check vault status
vault status

# List enabled secret engines
vault secrets list

# List auth methods
vault auth list

# Read policy
vault policy read myapp

# Check token capabilities
vault token capabilities secret/data/myapp/api-key

# Check audit log (if file audit enabled)
Get-Content /vault/logs/audit.log

# Rekey vault
vault operator rekey -init

# Seal/unseal
vault operator seal
vault operator unseal <key>
```

## Common Issues
- **Vault sealed**: After restart, Vault starts sealed; must unseal.
- **Permission denied**: Check policy; use `vault token capabilities`.
- **Lease expired**: Application doesn't renew lease; implement renewal logic.
- **Connection refused**: Check Vault address, network, TLS configuration.
- **Storage backend full**: Monitor storage usage; compact Raft or increase size.
- **Token not found**: Token revoked or expired; generate new token.
