# Service Mesh Theory

## Core Concepts
- **Service Mesh**: Dedicated infrastructure layer for handling service-to-service communication, security, and observability.
- **Sidecar Proxy**: Proxy container deployed alongside each application container (Envoy).
- **Control Plane**: Manages and configures proxies (Istiod — Pilot, Mixer, Citadel).
- **Data Plane**: All sidecar proxies handling traffic between services.

## Istio Architecture
- **Istiod**: Control plane — combines Pilot (traffic), Citadel (certificates), Galley (config).
- **Envoy**: High-performance sidecar proxy (data plane).
- **VirtualService**: Defines routing rules for a service.
- **DestinationRule**: Defines traffic policies (load balancing, circuit breaking, mTLS).
- **Gateway**: Ingress/egress traffic management at mesh edge.
- **ServiceEntry**: Registers external services into the mesh.

## Traffic Management
- **Weighted routing**: Split traffic across versions (canary, blue/green).
- **Request routing**: Route based on headers, cookies, or source labels.
- **Fault injection**: Delays and errors for testing resilience.
- **Circuit breaking**: Limits connections to failing services.
- **Mirroring**: Copy traffic to a shadow service for testing.

## Security (mTLS)
- **Mutual TLS**: Both sides verify certificates; traffic encrypted in transit.
- **PeerAuthentication**: Defines mTLS mode (DISABLE, PERMISSIVE, STRICT).
- **RequestAuthentication**: JWT/OIDC validation for end-user auth.
- **AuthorizationPolicy**: RBAC for service-to-service and end-user requests.
