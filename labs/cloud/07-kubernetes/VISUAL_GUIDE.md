# Visual Guide to Kubernetes

## 1. Cluster Architecture

```
                           ┌─────────────────────────┐
                           │   Control Plane          │
                           │  ┌────────────────────┐ │
                           │  │  API Server (kube-  │ │
                           │  │  apiserver)         │ │
                           │  └──────────┬─────────┘ │
                           │             │            │
                           │  ┌──────────▼─────────┐ │
                           │  │  etcd (key-value    │ │
                           │  │  store)             │ │
                           │  └────────────────────┘ │
                           │             │            │
                           │  ┌──────────▼─────────┐ │
                           │  │  Scheduler          │ │
                           │  │  Controller Manager │ │
                           │  └────────────────────┘ │
                           └─────────────────────────┘
                                      │
            ┌─────────────────────────┼─────────────────────────┐
            │                         │                         │
     ┌──────▼──────┐          ┌──────▼──────┐          ┌──────▼──────┐
     │  Node 1      │          │  Node 2      │          │  Node 3      │
     │  (us-east-1a)│          │  (us-east-1b)│          │  (us-east-1c)│
     │              │          │              │          │              │
     │  ┌────┐     │          │  ┌────┐     │          │  ┌────┐     │
     │  │Pod │     │          │  │Pod │     │          │  │Pod │     │
     │  └────┘     │          │  └────┘     │          │  └────┘     │
     │  ┌────┐     │          │  ┌────┐     │          │  ┌────┐     │
     │  │Pod │     │          │  │Pod │     │          │  │Pod │     │
     │  └────┘     │          │  └────┘     │          │  └────┘     │
     │  ┌────┐     │          │  ┌────┐     │          │             │
     │  │Pod │     │          │  │Pod │     │          │             │
     │  └────┘     │          │  └────┘     │          │             │
     └─────────────┘          └─────────────┘          └─────────────┘
```

## 2. Pod with Probes

```
Pod: java-app-6f8d4c7b9-abcde
┌──────────────────────────────────────┐
│   IP: 10.0.1.5                        │
│   Node: ip-10-0-0-1.ec2.internal      │
│                                       │
│   Init Containers:                    │
│   ┌──────────────────────────────┐   │
│   │ init-db-schema (completed)    │   │
│   └──────────────────────────────┘   │
│                                       │
│   Containers:                         │
│   ┌──────────────────────────────┐   │
│   │ java-app (Running)            │   │
│   │  Port: 8080 (HTTP)           │   │
│   │  Requests: 256M/250m         │   │
│   │  Limits: 512M/500m           │   │
│   │                               │   │
│   │  Liveness:  ──► /health/live  │   │
│   │  Readiness: ──► /health/ready │   │
│   │  Startup:   ──► /health/start │   │
│   └──────────────────────────────┘   │
│   ┌──────────────────────────────┐   │
│   │ fluent-bit (Running)          │   │
│   │  (sidecar — log collection)  │   │
│   └──────────────────────────────┘   │
└──────────────────────────────────────┘
```

## 3. Rolling Update Strategy

```
v1 (3 replicas):
┌──────┐  ┌──────┐  ┌──────┐
│ v1   │  │ v1   │  │ v1   │
│ pod1 │  │ pod2 │  │ pod3 │
└──────┘  └──────┘  └──────┘

Rollout to v2 (maxSurge=1, maxUnavailable=0):
┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐
│ v1   │  │ v1   │  │ v1   │  │ v2   │  ← surge (new)
└──────┘  └──────┘  └──────┘  └──────┘
            ↓ ready
┌──────┐  ┌──────┐  ┌──────┐
│ v1   │  │ v1   │  │ v2   │  ← terminate v1 pod
└──────┘  └──────┘  └──────┘
            ↓ ...repeat...
┌──────┐  ┌──────┐  ┌──────┐
│ v2   │  │ v2   │  │ v2   │  ← rollout complete
└──────┘  └──────┘  └──────┘
```

## 4. Service Types

```
ClusterIP (default):
[Pod]──[Pod]──[Pod]──┐
                      │ ClusterIP: 10.96.0.1:80
                      │ (internal only)
```

```
NodePort:
[Pod]──[Pod]──[Pod]──┐
                      │ NodePort: <nodeIP>:30080
                      │ (external, port 30000-32767)
```

```
LoadBalancer:
[Pod]──[Pod]──[Pod]──┐
                      │ LoadBalancer: ELB/ALB
                      │ (cloud LB, external)
```
