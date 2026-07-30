# 08 - Service Mesh

## Topics Covered
- Sidecar proxy pattern (Envoy, Linkerd)
- Istio architecture (control plane, data plane)
- Traffic management (virtual services, destination rules, gateways)
- Observability (metrics, traces, access logs)
- mTLS (mutual TLS, SPIFFE identities)
- Circuit breaking, retries, timeouts
- Canary deployments, weighted routing

## Goal
Understand how service meshes provide a dedicated infrastructure layer for service-to-service communication.

## Exercises

1. Simulate sidecar proxy intercepting requests.
2. Implement a simple mTLS handshake simulation.
3. Model a virtual service with weighted routing for canary deployment.
4. Implement circuit breaker with half-open state.