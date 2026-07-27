# API Gateways — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is an API Gateway? What problems does it solve?

**Expected coverage**: Single entry point for multiple backend services, cross-cutting concerns (auth, rate limiting, logging, routing, transformation, caching, circuit breaking), solves client complexity (one endpoint instead of many), enforces security policies, protocol translation (HTTP to gRPC), API versioning, traffic management.

**Q2**: Compare API Gateway with a Load Balancer and a Service Mesh sidecar.

**Expected coverage**: Load balancer (L4/L7 distribution, health checks, no content awareness or auth), API Gateway (L7 + auth, routing, transformation, rate limiting, aggregation), Service Mesh sidecar (L7 traffic management between services, mTLS, observability, circuit breaking). Gateway handles edge (north-south), mesh handles internal (east-west). Often used together: Gateway → Mesh → Services.

**Q3**: What are the main API Gateway features you should always consider?

**Expected coverage**: Request routing, authentication/authorization (JWT, OAuth2, API keys), rate limiting (per client, per endpoint), TLS termination, caching, request/response transformation, logging/monitoring, CORS handling, circuit breaking, retry policies, canary routing, IP whitelisting/blacklisting, WebSocket support.

## Intermediate (3 questions)

**Q4**: Compare Kong, NGINX, AWS API Gateway, and Envoy as API Gateways.

**Expected coverage**: Kong (NGINX-based, plugin ecosystem, Lua scripting, enterprise features), NGINX (high performance, config-based, limited out-of-box auth, can extend via modules), AWS API Gateway (fully managed, AWS integration, Lambda/auth/caching built-in, cost per call), Envoy (high-performance proxy, L3/L4/L7, xDS API, often used as sidecar, complex config via API). Factors: Self-managed vs managed, scaling, community, plugin ecosystem.

**Q5**: How do you implement rate limiting in an API Gateway?

**Expected coverage**: Algorithm (Token Bucket: burst allowance + sustained rate, Sliding Window: more accurate but more memory, Leaky Bucket: fixed rate, Fixed Window: simple but sharp edges). Storage (in-memory for single instance, Redis for distributed). Configuration (per API key, per endpoint, per user). Response headers (X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset). Retry-After header for exceeded limits.

**Q6**: How does an API Gateway handle authentication? Explain JWT validation.

**Expected coverage**: Client sends JWT in Authorization: Bearer <token>, gateway validates (signature verification with RS256/ES256 public key, expiration check, issuer check, audience check), extracts claims, passes identity to backend via header (X-User-ID, X-User-Roles), rotation via JWKS endpoint. OAuth2 flows (Authorization Code, Client Credentials) integrated with gateway.

## Advanced (2 questions)

**Q7**: Design an API Gateway strategy for migrating from a monolithic API to microservices.

**Expected coverage**: Decompose monolith service-by-service behind gateway, route requests based on path to new or old backend, run monolith and microservices in parallel until migration complete, implement feature flags in gateway for canary releases, transform responses for backward compatibility, incrementally deprecate old endpoints.

**Q8**: Your API Gateway is becoming a bottleneck. What do you do?

**Expected coverage**: Profile gateway (CPU, memory, request latency), scale horizontally (add instances behind LB), offload TLS termination (L4 LB terminates TLS before gateway), implement caching (static responses, auth tokens, JWKS), move shared logic to edge (Cloudflare Workers, Lambda@Edge), reduce overhead (async logging, connection pooling to backends), consider splitting into multiple gateways per domain.
