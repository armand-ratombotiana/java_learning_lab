# REST APIs — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is REST? Explain the six constraints of RESTful architecture.

**Expected coverage**: Uniform Interface (resource identification via URI, representation manipulation via representations, self-descriptive messages, HATEOAS), Stateless, Cacheable, Client-Server, Layered System, Code on Demand (optional). Contrast with RPC-style APIs.

**Q2**: Explain the HTTP methods and their properties: GET, POST, PUT, PATCH, DELETE.

**Expected coverage**: Safe (GET, HEAD, OPTIONS), Idempotent (PUT, DELETE, GET, HEAD, OPTIONS, PATCH only if full replacement), POST is neither safe nor idempotent. HTTP response codes for each method (200, 201, 204, etc.).

**Q3**: What is RESTful resource naming? Give examples of good vs bad API URLs.

**Expected coverage**: Nouns not verbs (/users not /getUsers), plural nouns (/users/{id}), hierarchy (/users/{id}/orders/{orderId}), query for filtering/sorting (/users?role=admin&sort=created_at), avoid deep nesting (limit to 2-3 levels), versioning (/v1/users).

## Intermediate (3 questions)

**Q4**: Explain HATEOAS (Hypermedia as the Engine of Application State). Why is it rarely implemented?

**Expected coverage**: Server returns links to related actions/resources, client navigates via links without hardcoded URLs, benefits (discoverability, decoupling), reasons for low adoption (extra complexity, client libraries hardcode endpoints anyway, documentation serves the same purpose).

**Q5**: How do you handle authentication and authorization in REST APIs?

**Expected coverage**: Basic Auth (with HTTPS), API Keys (in header or query), JWT (Bearer tokens, stateless, claims), OAuth 2.0 flows (Authorization Code, Client Credentials), API key rotation, rate limiting per key.

**Q6**: Design pagination for a REST API returning 10 million records.

**Expected coverage**: Offset/limit (simple but unstable for changing data), cursor-based (stable, uses opaque cursor token for next page), keyset pagination (WHERE id > last_seen), page metadata in response (total, next, prev links), default page size limits.

## Advanced (2 questions)

**Q7**: You notice API response times increased from 50ms to 500ms. Walk through debugging.

**Expected coverage**: curl -w timing breakdown, database query analysis (N+1 problem), caching layers (Redis, CDN), connection pooling, serialization overhead (JSON vs binary), network latency vs server processing, profiling tools (async-profiler, flamegraphs).

**Q8**: Design a REST API versioning strategy that supports backward compatibility while evolving the schema.

**Expected coverage**: URL versioning (/v1/) vs header versioning (Accept: application/vnd.company.v1+json), graceful degradation (ignore unknown fields), tolerance for optional new fields, deprecation headers (Sunset, Deprecation), migration guides, sunset timelines.
