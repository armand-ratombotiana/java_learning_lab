# Mock Interview: API Gateways

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Architect Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design an API Gateway for a microservices platform with 200+ services.

---

## Transcript

**Interviewer**: "We're moving from monolith to 200+ microservices. Each service exposes its own API. We need an API Gateway that provides: authentication, rate limiting, routing, aggregation, and monitoring."

**Candidate**: "API Gateway sits between clients and services. It handles cross-cutting concerns so individual services don't have to. I'll design it as a multi-layer gateway."

**Interviewer**: "What layers?"

**Candidate**: "Layer 1 — Edge Gateway: handles TLS termination, DDoS protection, IP whitelisting, logging. Layer 2 — Authentication Gateway: validates JWT/OAuth tokens, extracts user context. Layer 3 — Routing Gateway: routes requests to the appropriate service based on path pattern. Layer 4 — Aggregation Gateway: can compose multiple service calls for complex responses."

**Interviewer**: "How does routing work?"

**Candidate**: "Route table: `path → service URL`. Example: `/v1/users/*` → user-service, `/v1/orders/*` → order-service. Route table is stored in a distributed config store (ZooKeeper or ETCD) and cached in the gateway. The gateway supports dynamic routing (canary, A/B testing), circuit breaking, and retries with exponential backoff."

**Interviewer**: "How do you handle rate limiting at the gateway?"

**Candidate**: "Distributed rate limiting using Redis. Per-client rate limits (by API key) and per-endpoint rate limits. Token bucket algorithm with configurable capacity (requests/second). Rate limit headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`. When exceeded, return 429 Too Many Requests."

**Interviewer**: "What about authentication?"

**Candidate**: "The gateway validates JWT tokens from the `Authorization` header. It checks: signature (via public key from auth service), expiry, issuer, audience. Extracts user_id and roles from the token and passes them to downstream services via headers (`X-User-Id`, `X-User-Roles`). This way, services don't need authentication logic."

**Interviewer**: "How do you handle partial failures when aggregating?"

**Candidate**: "The aggregation layer uses a scatter-gather pattern. It sends requests to all involved services in parallel. If a service fails (e.g., service returns 5xx or times out after 500ms), the aggregator has options: 1) Return partial results with error information, 2) Return cached fallback data, 3) Return full failure. The choice depends on the use case — for a dashboard, partial results are acceptable. For checkout, a full failure is better."

**Interviewer**: "How do you scale the gateway itself?"

**Candidate**: "The gateway is stateless (state lives in shared Redis). Horizontal scaling behind a load balancer is straightforward. The route table and config changes are pushed via distributed config. Health checks: the gateway exposes a `/health` endpoint that checks connectivity to downstream services."

---

## Key Takeaways

- **Multi-layer gateway**: Edge → Auth → Route → Aggregate
- **Dynamic routing**: Route table in distributed config, cached locally
- **Distributed rate limiting**: Redis-backed token bucket
- **JWT authentication**: Validate at gateway, propagate user context
- **Scatter-gather**: Parallel aggregation with smart failure handling
- **Stateless design**: Enables horizontal scaling
