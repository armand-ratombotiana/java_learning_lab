# Lab 11 — SSL/TLS Certificate Expiry: Interview Questions

**Q1: What happens when an SSL/TLS certificate expires?**

**Answer:** Clients (browsers, applications, services) will reject the connection with an SSL error. The exact error: HTTPS connections fail with "SSL certificate expired," "NET::ERR_CERT_DATE_INVALID," or "certificate has expired." All services relying on HTTPS to the affected domain will break. Internal service-to-service TLS communication will also fail. The service becomes inaccessible until the certificate is renewed.

**Q2: How do you monitor SSL/TLS certificate expiry?**

**Answer:** 1) External monitoring: use tools like SSL Labs, CheckSSL, or built-in cloud provider certificate managers that alert N days before expiry. 2) Prometheus: blackbox_exporter can probe TLS certificate expiry and expose as metric. 3) Custom scripts: `openssl s_client -connect host:443 2>/dev/null | openssl x509 -noout -dates` to check dates. 4) Certificate Transparency logs: monitor for certificates near expiry. 5) Internal monitoring: cert-manager in Kubernetes tracks certificate expiry. Alert thresholds: 30 days (warning), 14 days (critical), 7 days (emergency).

**Q3: What's the difference between self-signed certificates and CA-signed certificates?**

**Answer:** Self-signed: created by the server operator, not trusted by browsers or clients by default. Must be manually added to trust stores. Used for internal/testing environments. CA-signed: issued by a trusted Certificate Authority (Let's Encrypt, DigiCert, GlobalSign), automatically trusted by browsers and OS trust stores. For production: always use CA-signed certificates. For internal services: use internal CA (e.g., HashiCorp Vault, AWS Private CA) rather than self-signed for manageability.

**Q4: How does Let's Encrypt automate certificate renewal?**

**Answer:** Let's Encrypt uses the ACME protocol for automated renewal: 1) A cron job or cert-manager checks if certificate expires within 30 days. 2) ACME client requests renewal from Let's Encrypt. 3) Let's Encrypt verifies domain ownership (HTTP-01 challenge: serve a file at the domain; DNS-01 challenge: add a DNS TXT record; TLS-ALPN-01: respond on port 443). 4) New certificate issued. 5) Web server reloads the new certificate. 6) Renewal is fully automated with no human intervention.

**Q5: Design a TLS certificate management strategy for 500 microservices.**

**Answer:** 1) Use a service mesh (Istio, Linkerd) for mTLS — automatic certificate rotation between sidecars. 2) For external-facing: use a centralized certificate management service (cert-manager, AWS Certificate Manager). 3) Automate renewal: ACME-based automatic renewal with 60-day validity (Let's Encrypt). 4) Monitoring: certificate expiry dashboard with 30/14/7 day alerts. 5) Certificate inventory: centralized database of all certificates, their domains, issuance dates, expiry dates. 6) Validate: periodically test that all services accept each other's certificates. 7) Emergency process: runbook for manual certificate replacement if automation fails.

**Q6: Tell me about a certificate expiry incident. (STAR)**

**Answer:** Situation: Our mobile app's API calls started failing globally. Investigation showed the API domain's SSL certificate had expired. Task: Renew the certificate ASAP and restore service. Action: I generated a new CSR, submitted to our CA, received the renewed certificate, installed on the load balancer, and verified the new certificate was served. Total time: 20 minutes. Root cause: certificate expiry notification was sent to a distribution list that had a member on vacation and no one else actioned it. Result: I automated renewal with cert-manager (ACMEv2/Let's Encrypt) so human oversight is no longer required. Also added external monitoring (blackbox_exporter) that alerts 30 days before expiry.

**Q7: What is a certificate revocation and how does it work?**

**Answer:** Revocation invalidates a certificate before its expiry date (compromised private key, change in domain ownership, CA security incident). Mechanisms: 1) CRL (Certificate Revocation List) — CA publishes list of revoked serial numbers; clients download periodically. 2) OCSP (Online Certificate Status Protocol) — client queries CA in real-time about specific certificate status. 3) OCSP Stapling — web server queries OCSP and staples (attaches) the signed response to the TLS handshake, avoiding client-side OCSP latency.

**Q8: How does the SSL/TLS handshake work at a high level?**

**Answer:** 1) Client Hello: client sends supported TLS versions, cipher suites, random number. 2) Server Hello: server selects TLS version, cipher suite, sends its random number and certificate (containing public key). 3) Certificate Verification: client verifies certificate chain against trusted root CAs, checks expiry, revocation status. 4) Key Exchange: client generates pre-master secret, encrypts with server's public key (RSA) or uses Diffie-Hellman key exchange. 5) Session Keys: both sides derive symmetric session keys from pre-master secret. 6) Finished: both sides send encrypted "Finished" message to verify handshake succeeded. Total: 2 round trips (TLS 1.2), 1 round trip (TLS 1.3).

**Q9: What are common SSL/TLS configuration vulnerabilities?**

**Answer:** 1) Weak protocols: SSLv2, SSLv3, TLS 1.0, TLS 1.1 enabled. 2) Weak cipher suites: RC4, DES, 3DES, CBC-mode ciphers. 3) Weak key exchange: export-grade RSA, Diffie-Hellman < 2048 bits. 4) Expired certificates. 5) Self-signed certificates in production. 6) Wildcard certificates for sensitive domains. 7) Missing HSTS headers. 8) Incomplete certificate chain (missing intermediate CA). 9) Certificate hostname mismatch. 10) OCSP stapling disabled (privacy/reliability concerns).

**Q10: What is certificate pinning and should you use it?**

**Answer:** Certificate pinning (HPKP) hardcodes the expected certificate or public key in the client. When connecting, the client rejects any certificate not matching the pinned value. Benefits: prevents MITM attacks from compromised CAs. Problems: if you need to rotate the certificate (and it doesn't match the pinned value), all clients break until updated. HPKP was so dangerous it was deprecated in Chrome. Modern alternative: Certificate Transparency (CT) logs — monitor for unauthorized certificate issuance rather than pinning.
