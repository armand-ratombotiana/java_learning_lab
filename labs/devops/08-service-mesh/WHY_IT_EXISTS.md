# Why Service Mesh Exists

## The Problem
- **Service-to-service communication complexity**: Retries, timeouts, circuit breaking, load balancing need library support.
- **Security gaps**: No built-in encryption or authentication between services.
- **Observability challenges**: No distributed tracing, metrics, or access logs per request.
- **Language-specific implementations**: Each team implements networking logic differently.
- **Configuration drift**: Inconsistent policies across services.

## Service Mesh Solution
- **Out-of-process architecture**: Application code doesn't need to handle networking — sidecar handles it.
- **Universal mTLS**: Encrypt all inter-service traffic without code changes.
- **Traffic policies**: Centralized routing, retry, timeout, and circuit breaker configuration.
- **Deep observability**: Automatic metrics, distributed tracing, and access logs for all traffic.
- **Platform abstraction**: Same mesh works across Kubernetes, VMs, and serverless.
