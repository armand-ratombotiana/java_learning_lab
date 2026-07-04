# Service Mesh Architecture

## Istio Architecture Overview
```
┌──────────────────────────────────────────────────────────────┐
│                       Control Plane (istiod)                  │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │    Pilot     │  │   Citadel    │  │     Galley          │ │
│  │ - Service    │  │ - CA         │  │ - Config validation │ │
│  │   discovery  │  │ - Cert       │  │ - Config ingestion  │ │
│  │ - Traffic    │  │   management │  │ - Config distribution│ │
│  │   management │  │ - SPIFFE IDs │  └────────────────────┘ │
│  │ - xDS API    │  └──────────────┘                         │ │
│  └──────┬───────┘                                           │ │
└─────────┼────────────────────────────────────────────────────┘
          │ xDS (Envoy configuration)
┌─────────┼────────────────────────────────────────────────────┐
│         ▼                                                     │
│  ┌────────────────────────────────────────────────────────┐  │
│  │                   Data Plane                            │  │
│  │                                                         │  │
│  │  Service A          Service B          Service C        │  │
│  │  ┌──────┬──────┐  ┌──────┬──────┐  ┌──────┬──────┐    │  │
│  │  │ App  │Envoy │  │ App  │Envoy │  │ App  │Envoy │    │  │
│  │  └──────┴──────┘  └──────┴──────┘  └──────┴──────┘    │  │
│  │                     │                                    │  │
│  │  ┌──────────────────┴──────────────────────────────┐    │  │
│  │  │  Ingress Gateway (edge proxy for external traffic)│   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

## Components Summary
- **istiod**: Unified control plane (Pilot + Citadel + Galley)
- **Envoy**: L7 proxy — sidecar, ingress, egress gateway
- **Ingress Gateway**: External traffic entry point
- **Egress Gateway**: Controlled external traffic exit
- **Kiali**: Mesh visualization dashboard
- **Prometheus**: Metrics collection
- **Grafana**: Metrics dashboards
- **Jaeger/Zipkin**: Distributed tracing
