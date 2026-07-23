# Interview Cheatsheet: SSL/TLS Certificate Expiry

## Key Diagnostic Commands
- `openssl s_client -connect host:443` — check cert chain
- `openssl x509 -in cert.pem -text -noout` — inspect cert
- `curl -vI https://host` — full TLS handshake debug
- `keytool -list -keystore <store>` — Java keystore inspection
- `certbot certificates` — Let's Encrypt status
- `sslyze <host>` — TLS scanner
- Qualys SSL Labs (online) — full TLS assessment

## Common Metrics to Check
- Certificate expiry date (days remaining)
- Certificate renewal success/failure rate
- TLS handshake failure rate
- SSL certificate validation errors in logs
- OCSP/CRL response time

## Typical Root Causes
- Certificate not renewed before expiry
- Auto-renewal failed (DNS, port 80 accessibility)
- Incorrect intermediate certificate chain
- Hostname mismatch (SAN not including hostname)
- Keystore not updated after renewal
- Certificate pinned in code and not updated
- TLS version mismatch (client supports TLS 1.0, server requires 1.2)

## Interview Question Patterns
- "How do you monitor certificate expiry for thousands of domains?"
- "Design a certificate auto-renewal system"
- "How does certificate pinning work and why is it risky?"
- "What happens during a TLS handshake?"

## STAR Story Template
**S**: CDN started serving 502 errors — TLS handshake failed on edge
**T**: Restore HTTPS connectivity
**A**: Found certificate expired 2 hours ago, renewed with Let's Encrypt, added monitoring with 30-day alert
**R**: Site restored, added automated renewal check, never expired again
