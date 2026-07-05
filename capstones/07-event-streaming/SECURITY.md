# Security: Event Streaming

## Authentication
- SASL/PLAIN (username/password)
- SASL/SCRAM (salted challenge response)
- SASL/GSSAPI (Kerberos) for enterprise
- mTLS (mutual TLS)

## Authorization
- ACLs: principal + resource (topic/group) + operation (READ/WRITE/CREATE/DELETE)
- Super users bypass ACL checks
- Prefix-based ACLs for multi-tenant deployments

## Encryption
- TLS 1.3 for client-broker and inter-broker communication
- Encryption at rest for log segments (disk-level or Kafka-level)

## Audit
- Audit logs for all administrative operations
- Produce/consume event tracking for compliance
- Consumer offset monitoring for data leakage detection
