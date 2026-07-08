# Security for Streaming Analytics

## Pipeline Security
- TLS for Kafka, Flink, database connections
- Authentication for all data sources
- Authorization for stream processing jobs

## Dashboard Security
- Grafana authentication (LDAP, OAuth)
- Row-level filters for multi-tenant dashboards
- Audit logging of dashboard access

## Data Protection
- Encrypt sensitive fields before streaming
- Mask PII in dashboard visualizations
- Retention limits on stream data
