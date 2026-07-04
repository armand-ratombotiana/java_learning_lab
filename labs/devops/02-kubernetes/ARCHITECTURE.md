# Kubernetes Architecture

## High-Level Architecture
```
┌────────────────────────────────────────────────────────────┐
│                      Control Plane                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐             │
│  │  API     │  │Scheduler │  │ Controller   │             │
│  │  Server  │  │          │  │ Manager      │             │
│  └────┬─────┘  └──────────┘  └──────────────┘             │
│       │                                                    │
│  ┌────▼─────┐                                             │
│  │   etcd   │                                             │
│  └──────────┘                                             │
└───────────────────┬────────────────────────────────────────┘
                    │
┌───────────────────▼────────────────────────────────────────┐
│                   Worker Node 1                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐    │
│  │ kubelet  │  │kube-proxy│  │     Container        │    │
│  │          │  │          │  │     Runtime           │    │
│  └────┬─────┘  └──────────┘  └──────────────────────┘    │
│       │                                                    │
│  ┌────▼─────────────────────────────────────┐              │
│  │ Pod: app (c1: nginx, c2: sidecar)        │              │
│  │ Pod: cache (c1: redis)                   │              │
│  │ Pod: worker (c1: celery)                 │              │
│  └───────────────────────────────────────────┘              │
└──────────────────────────────────────────────────────────────┘
```

## API Request Flow
```
kubectl → AuthN → AuthZ → Admission → Validation → etcd → Controllers
```

## Networking Model
- Every Pod gets a unique IP (flat network, no NAT).
- Pods can communicate across nodes without NAT.
- Agents (kube-proxy) handle port-forwarding to service IPs.
