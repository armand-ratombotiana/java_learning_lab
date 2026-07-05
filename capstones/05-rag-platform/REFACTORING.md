# Refactoring: RAG Platform

## Current Pain Points
- Ingestion pipeline is synchronous (blocks on PDF parsing + embedding)
- No caching for repeated queries (same question asked multiple times)
- Single embedding model — no multi-vector retrieval (sparse + dense)
- Hardcoded prompt template cannot be configured per use case

## Suggested Improvements
- Make ingestion fully async with job queue (Redis/SQS)
- Add response cache for exact-match and semantic-similar queries
- Implement hybrid search: BM25 (sparse) + embedding (dense) with fusion
- Externalize prompt templates to config with per-collection overrides
- Add streaming response for LLM generation (SSE, WebSocket)
- Implement document versioning and incremental indexing
