# Interview Cheatsheet: API Rate Limiting

## Key Diagnostic Commands
- `nginx -t && nginx -s reload` — check and reload rate limit config
- API gateway logs: 429 status codes, Retry-After headers
- WAF logs: rate limit rule hits
- `redis-cli --raw keys 'ratelimit:*'` — check rate limit keys
- Application metrics: rate limit counter

## Common Metrics to Check
- 429 (Too Many Requests) rate
- Requests per client/IP/API key
- Rate limit counter per window
- Retry-After header values returned
- Client retry behavior (backoff compliance)
- Distributed rate limit accuracy (drift)

## Typical Root Causes
- Misconfigured rate limit thresholds
- Missing rate limiting on critical endpoints
- Distributed rate limiter clock skew
- Token bucket refill too slow
- Client not respecting 429/Retry-After
- DDoS attack hitting unauthenticated endpoints
- Burst traffic exceeding burst allowance

## Interview Question Patterns
- "Design a distributed rate limiter"
- "Compare token bucket vs. sliding window algorithms"
- "How do you test that rate limiting works correctly?"
- "How do you set Retry-After headers and ensure clients respect them?"

## STAR Story Template
**S**: API started returning 429 to legitimate users during marketing campaign
**T**: Allow legitimate traffic while still blocking abuse
**A**: Increased rate limit per API key for authenticated users, added sliding window per IP for unauthenticated
**R**: False positives dropped to zero, abuse still blocked
