# Security: RAG Platform

## Data Protection
- Uploaded documents may contain PII/confidential data — encrypt at rest
- Embeddings may leak document content — restrict access per collection
- TLS 1.3 for all API endpoints

## Access Control
- Collection-level permissions (READ, WRITE, ADMIN)
- API key authentication for programmatic access
- User authentication via OAuth2/OIDC for web UI

## LLM Security
- Prompt injection prevention: clearly separate system, context, and user input in prompt template
- Output sanitization: LLM should not produce harmful content
- Rate limiting per user/API key

## Compliance
- Audit logging of all queries and responses
- Data retention policies for uploaded documents
- GDPR: right to deletion of documents and embeddings
