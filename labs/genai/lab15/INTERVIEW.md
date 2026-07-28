# Lab 15: Interview Questions

## Q1: Design a GenAI platform architecture.
**A:** Layers: 1) API Gateway (auth, rate limiting, routing), 2) Orchestration Layer (prompt management, guardrails, fallback), 3) Model Serving (GPU cluster, model registry, autoscaling), 4) Observability (metrics, tracing, logging), 5) Storage (KV cache, vector DB, user data). Use async messaging between components.

## Q2: How do you handle multi-model serving?
**A:** Model registry with versioning, model-router based on capability/ cost/latency requirements, warm pool of GPU nodes, dynamic loading/unloading based on demand.

## Q3: How would you implement A/B testing for LLMs?
**A:** 1) Assign users to variants, 2) Route traffic proportionally, 3) Collect quality metrics (human eval, LLM-as-judge, task metrics), 4) Statistical significance testing, 5) Gradual rollout or rollback.

## Q4: What are the most important reliability patterns for a GenAI platform?
**A:** Circuit breaker (fallback to smaller model), retry with exponential backoff, bulkhead (separate pools per tenant), caching (exact + semantic), graceful degradation (degraded responses under load).

## Q5: How do you think about cost vs quality trade-offs in platform design?
**A:** Tiered model offering: small/fast (high volume, low complexity), medium (balanced), large (complex reasoning). Use model routing based on query complexity. Cache aggressively for repeated queries.
