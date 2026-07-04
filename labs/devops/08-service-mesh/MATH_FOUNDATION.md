# Math Foundation for Service Mesh

N/A — Service mesh primarily involves networking and security concepts with minimal mathematics.

## Load Balancing Algorithms
- **Round robin**: Simple sequential distribution.
- **Least connections**: Route to endpoint with fewest active connections.
- **Ring hash**: Consistent hashing for session affinity (same client → same backend).
- **Random**: Random selection (simple, good for large pools).

## Circuit Breaking
- **Error rate threshold**: Trip circuit when error rate > N% over M seconds.
- **Connection limits**: Max pending requests, max requests per connection.
- **Half-open state**: After cooldown, allow limited requests to probe recovery.

## Retry Budget
- **Max retries per request**: Default 2 (Envoy may retry on connection failure, 503).
- **Timeout**: Per-request timeout; retries may increase total execution time.

## Rate Limiting
- **Token bucket**: Fixed capacity per time window.
- **Distributed rate limiting**: Global rate limit across all Envoy instances (requires external service).

## TLS Certificate Math
- **X.509 certificates**: RSA/ECDSA key pairs for identity.
- **Certificate rotation**: 24-hour default; renewable via SDS.
- **SPIFFE standard**: Identity format: `spiffe://cluster.local/ns/<namespace>/sa/<serviceaccount>`.
