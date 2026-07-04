# Service Mesh Internals

## Envoy Proxy Architecture
- **Listener**: Port on which Envoy accepts connections.
- **Cluster**: Group of upstream endpoints (service pods).
- **Endpoint**: Actual pod IP:port.
- **Filter**: L3/L4/L7 processing chain (HTTP, TCP, TLS).
- **xDS API**: Dynamic configuration — LDS (Listener), CDS (Cluster), EDS (Endpoint), RDS (Route).

## Istiod Components
- **Pilot**: Platform adapter (K8s, Consul, VMs) → translates into Envoy configuration via xDS.
- **Citadel**: Certificate Authority — issues and rotates certificates for workloads.
- **Galley**: Configuration validation, ingestion, and distribution.
- **Proxy**: Injects sidecar and manages lifecycle.

## mTLS Certificate Flow
1. Pod starts → sidecar Envoy requests certificate from Citadel via SDS (Secret Discovery Service).
2. Citadel issues X.509 certificate signed by Istio CA (or pluggable CA).
3. Certificate stored in Envoy SDS cache; rotated before expiry (default 24h).
4. Envoy uses certificate for mTLS handshake with other Envoys.

## Telemetry
- **Metrics**: Prometheus-formatted — request count, duration, size, TCP stats.
- **Access logs**: Structured logs per request (source, destination, response code, duration).
- **Traces**: Distributed tracing — Envoy generates trace headers (Zipkin, Jaeger, Datadog).
