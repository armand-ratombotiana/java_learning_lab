# Interview: API Security

## Q1: Conceptual Understanding
**Q**: What are the most critical API security risks according to OWASP API Top 10?
**A**: Broken Object Level Authorization (#1) — users accessing objects they shouldn't. Broken Authentication (#2) — weak or guessable credentials. Excessive data exposure (#3) — returning full object graphs when only subsets are needed.

## Q2: Implementation
**Q**: How would you implement rate limiting for a production API?
**A**: Use sliding window log or token bucket algorithm. Store counters in Redis for distributed rate limiting. Apply per-user quotas (by API key or user ID) and global limits. Return 429 with Retry-After header. Use WebFlux or filter chains for non-blocking.

## Q3: System Design
**Q**: Design a secure API gateway architecture.
**A**: Gateway handles: TLS termination, OAuth2 token validation (JWKS), rate limiting (Redis), input validation (JSON Schema), logging/audit, WAF rules. Backend services receive validated, sanitized requests with user context in headers.

## Coding Challenge
Implement a sliding window rate limiter in Java that allows N requests per T seconds per user.
