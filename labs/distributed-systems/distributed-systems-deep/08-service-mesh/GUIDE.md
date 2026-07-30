# Service Mesh — Deep Dive Guide

## Sidecar Proxy

Each service instance deploys alongside a proxy (Envoy). All traffic in/out flows through the sidecar.

**Benefits**:
- Transparent to application code
- Centralized traffic policy, observability, security
- Protocol upgrades (HTTP/1.1 → HTTP/2, gRPC)

## Istio Architecture

**Control Plane (istiod)**:
- Pilot: service discovery, traffic management
- Citadel: certificate issuance (mTLS)
- Galley: config validation, distribution

**Data Plane**: Envoy proxies (sidecars + gateways)

## Traffic Management

- **VirtualService**: define routing rules (host, match, route)
- **DestinationRule**: subset definitions, load balancer settings, connection pool
- **Gateway**: ingress/egress traffic management

## mTLS

Each workload is issued a SPIFFE identity (spiffe://cluster.local/ns/.../sa/...). Envoy exchanges certificates and encrypts all traffic.

## Circuit Breaking

Three states:
1. **Closed**: normal operation, requests pass
2. **Open**: failures exceed threshold, requests fail fast
3. **Half-Open**: after timeout, allow probe requests; success → close, failure → open

## Observability

Istio exports: HTTP metrics (request count, duration, size), access logs, distributed tracing (Jaeger/Zipkin).