# Lab 11 — SSL/TLS Certificate: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 15 minutes (certificate renewal + installation) |
| RPO | 0 (no data loss — communication fails but data intact) |
| MTD | 30 minutes before customer-facing SLA breach |

## Scenarios

### Scenario A: Certificate Expired

**Trigger:** Monitoring alert: certificate expired or expiring < 24 hours
**Recovery:**
1. Generate new CSR with same subject/ SANs
2. Submit to CA (auto-renew if ACME configured)
3. Install new certificate on load balancer / web server
4. Verify new certificate is served: `openssl s_client -connect host:443`
5. Verify clients can connect (curl, browser test)
6. Update certificate tracking inventory

### Scenario B: Certificate Revoked

**Trigger:** CA revokes certificate (compromised key, CA security incident)
**Recovery:**
1. Generate new key pair (do NOT reuse compromised key)
2. Generate new CSR
3. Submit to CA for new certificate
4. Install on all servers
5. Check for any OCSP/CRL stapling issues
6. Investigate: why was it revoked? Was our key compromised?

### Scenario C: CA Outage

**Trigger:** CA is unavailable, cannot renew certificate
**Recovery:**
1. Use backup CA (have multiple CAs configured)
2. If no backup CA: request extension from CA support
3. Temporarily increase monitoring for certificate expiry
4. If expired and CA unavailable: consider emergency alternative (self-signed with trust store update for internal services)
5. Plan: always have certificates from 2 different CAs in rotation
