# Security: AI Assistant

## Prompt Injection Prevention
- Separate system, user, and tool messages in the API call format
- Never include user input directly in the system prompt
- Validate that tool calls actually match registered tool schemas
- Rate limit API calls to prevent prompt injection probing

## Tool Security
- Calculator: use safe expression parser (not eval())
- Email: require user confirmation before sending
- Web search: sanitize search queries, no direct URL fetching
- File operations: sandboxed in a restricted directory

## Data Protection
- Conversation data may contain PII — encrypt at rest
- TLS 1.3 for all API endpoints
- No logging of raw LLM responses (may contain PII)
- Session isolation: users cannot access other users' conversations

## Authentication & Authorization
- API key or OAuth2 for assistant API
- Per-user rate limiting
- Admin endpoints for tool management
- Audit logging of all tool executions
