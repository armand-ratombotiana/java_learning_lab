# Refactoring: AI Assistant

## Current Pain Points
- Agent loop is sequential (tools executed one at a time; parallel tool calls not supported)
- No conversation persistence (all sessions lost on restart)
- No plugin architecture (tools must be registered in code)
- No multi-modal support (text only, no image input/output)
- No guardrails (no content moderation, no PII detection)

## Suggested Improvements
- Support parallel tool calls: execute multiple tools simultaneously, merge results
- Add conversation persistence: store sessions in PostgreSQL with vector search for retrieval
- Implement plugin system: external tools registered via REST webhook, discovered at runtime
- Add multi-modal support: accept images (vision models), generate images (DALL-E)
- Add guardrails: content moderation before/after LLM call, PII detection and masking
- Implement rate limiting per user session
- Add usage tracking and cost attribution per user/session
