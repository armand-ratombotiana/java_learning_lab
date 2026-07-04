# Visual Guide to Service Mesh

## Istio Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                      Control Plane (istiod)                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  Pilot   │  │ Citadel  │  │  Galley  │  │Telemetry │  │
│  │(traffic) │  │(security)│  │(config)  │  │(metrics) │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────────┘  │
└───────┼──────────────┼─────────────┼────────────────────────┘
        │              │             │
┌───────▼──────────────▼─────────────▼────────────────────────┐
│                     Data Plane                               │
│                                                              │
│  ┌─────────────────────┐      ┌─────────────────────┐       │
│  │  Service A          │      │  Service B          │       │
│  │  ┌─────┐  ┌──────┐ │      │  ┌─────┐  ┌──────┐ │       │
│  │  │ App │──│Envoy │─────mTLS──│ App │──│Envoy │ │       │
│  │  │container│sidecar│ │      │  │container│sidecar│ │       │
│  │  └─────┘  └──────┘ │      │  └─────┘  └──────┘ │       │
│  └─────────────────────┘      └─────────────────────┘       │
└──────────────────────────────────────────────────────────────┘
```

## Traffic Routing
```
         ┌────────────────────────────────────┐
         │         VirtualService              │
         │  Weight: v1=90%, v2=10%            │
         └──────┬──────────────┬───────────────┘
                 │              │
          ┌──────▼─────┐  ┌─────▼──────┐
          │ v1 (prod)  │  │ v2 (canary)│
          │ 9 pods     │  │ 1 pod      │
          └────────────┘  └────────────┘
```

## mTLS Communication
```
Service A (Envoy)          Service B (Envoy)
     │                           │
     │─── Client Hello ─────────▶│
     │◀── Server Hello + Cert ───│
     │─── Client Cert ──────────▶│
     │─── Certificate Verify ───▶│
     │◀── mTLS Established ─────│
     │══ Encrypted Traffic ═════▶│
```
