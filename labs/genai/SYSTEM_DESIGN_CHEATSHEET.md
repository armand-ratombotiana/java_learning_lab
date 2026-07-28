# System Design Cheatsheet for GenAI

## Common GenAI System Design Problems
| Problem | Key Components |
|---------|---------------|
| Design a ChatGPT-like service | Load balancer, model server(s), KV cache, rate limiter, guardrails |
| Design a RAG pipeline | Ingestion (chunk, embed, index), retrieval (ANN), generation (LLM) |
| Design an agent platform | LLM, tool registry, memory/context, orchestrator, human-in-the-loop |
| Design a model serving platform | Model registry, autoscaling, batching, GPU scheduling, canary deploy |
| Design an evaluation platform | Test sets, evaluator models, dashboards, A/B framework |

## Key Architecture Decisions
- **Stateless vs stateful**: Stateless for scaling, stateful for session.
- **Batching**: Dynamic batching improves throughput at cost of latency.
- **Caching**: Semantic cache (embedding similarity) vs exact cache.
- **Async vs sync**: Async for ingestion, sync for real-time inference.
- **Fallback**: Smaller model fallback when primary is overloaded.
