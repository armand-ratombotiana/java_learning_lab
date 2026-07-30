# System Design Cheatsheet

## The Design Framework

1. **Clarify Requirements** — Functional, non-functional, scale
2. **Estimate Scale** — QPS, storage, bandwidth, cache needs
3. **High-Level Design** — Core components and data flow
4. **Deep Dive** — Detail critical components, trade-offs
5. **Address Bottlenecks** — Caching, sharding, replication, CDN

## Common AI System Designs

### LLM Serving Platform
```
Client → Load Balancer → API Gateway → Request Queue → Model Replicas → Cache
```
- **Key metrics**: Latency p50/p95/p99, throughput, cost per token
- **Bottlenecks**: GPU memory (KV-cache), batch scheduler
- **Trade-offs**: Batch size vs. latency, model size vs. cost
- **Scaling**: Horizontal (more replicas), vertical (bigger GPUs), model parallelism

### RAG System
```
Query → Embedding Model → Vector DB (ANN Search) → Retrieve Chunks → Re-rank → LLM → Response
```
- **Key metrics**: Recall@k, precision@k, end-to-end latency
- **Bottlenecks**: Embedding computation, ANN search latency
- **Trade-offs**: Chunk size vs. context relevance, k value vs. latency
- **Scaling**: Shard vector index, cache frequent queries

### Multi-Agent System
```
User Query → Orchestrator → Route to Specialist → Execute Tools → Aggregate → Response
```
- **Key metrics**: Task completion rate, steps per task, cost per task
- **Bottlenecks**: LLM calls per step, tool execution latency
- **Trade-offs**: More agents = better specialization but higher complexity and cost

### Observability Platform
```
Request → Metrics Collector → Token Tracker → Cost Calculator → Drift Detector → Dashboard
```
- **Key metrics**: Metrics cardinality, write throughput, query latency
- **Bottlenecks**: High-cardinality metric storage
- **Trade-offs**: Sampling rate vs. accuracy, retention period vs. storage cost

## Infrastructure Patterns

| Pattern | Problem Solved | Example |
|---------|---------------|---------|
| Consistent Hashing | Even distribution with minimal rehashing | Cache sharding |
| Circuit Breaker | Prevent cascading failures | Model inference calls |
| Bulkhead | Isolate resource pools | Per-tenant model instances |
| Rate Limiting | Protect against abuse | API throttling |
| Queue + Worker | Decouple request receipt from processing | Async inference |
| Cache-Aside | Reduce latency for repeated requests | Response caching |

## Database Comparisons

| Database | Best For | Not Good For |
|----------|----------|--------------|
| PostgreSQL | Structured data, ACID, geo queries | Very high write throughput |
| Vector DB (Pinecone/Qdrant) | Similarity search at scale | Exact match queries |
| Redis | Cache, real-time counters, pub/sub | Large data persistence |
| Cassandra | High write throughput, time-series | Complex joins, ad-hoc queries |
| S3/GCS | Blob storage, model artifacts, logs | Low-latency access |

## Capacity Planning Formulas

| Metric | Formula |
|--------|---------|
| QPS | Daily active users × avg requests per user / 86400 |
| Storage | Daily data ingested × retention days × replication factor |
| Bandwidth | QPS × avg response size |
| Cache memory | QPS × cached fraction × avg data size × TTL |
| GPU memory | Model params × bytes per param (FP16=2, FP32=4) + KV-cache