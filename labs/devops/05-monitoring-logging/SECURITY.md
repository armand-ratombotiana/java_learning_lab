# Monitoring & Logging Security

## Log Security
- **Never log secrets**: Passwords, tokens, API keys, PII.
- **Log scrubbing**: Use Logstash filters to redact sensitive fields.
- **Access controls**: Restrict Kibana/Elasticsearch access based on index permissions.
- **Encryption**: TLS for log shipping (Filebeat → Logstash → Elasticsearch).
- **Audit logs**: Monitor access to log data itself.

## Prometheus Security
- **TLS**: Enable TLS for Prometheus endpoints and Alertmanager.
- **Basic auth**: Protect /metrics endpoints from unauthorized scraping.
- **Network segmentation**: Limit Prometheus network access to scrape targets.
- **Scrape authentication**: Use bearer token authentication for targets.
- **Alertmanager webhooks**: Use HTTPS webhooks with secrets.

## Grafana Security
- **Authentication**: LDAP, OAuth, SAML integration.
- **Authorization**: Folder/panel-level permissions.
- **API keys**: Rotate service account tokens.
- **Data source credentials**: Encrypt stored credentials.

## Compliance Considerations
- Log retention policies for SOC2/HIPAA (typically 1-7 years).
- Immutable log storage (append-only, write-once-read-many).
- Chain of custody for forensic logs.
- Anonymization of PII in log aggregation.
