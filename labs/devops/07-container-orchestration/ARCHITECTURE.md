# Container Orchestration Architecture

## Advanced Orchestration Components
```
┌─────────────────────────────────────────────────────────────┐
│                     Control Plane                            │
│  ┌─────────┐  ┌─────────┐  ┌──────────┐  ┌─────────────┐  │
│  │ HPA     │  │Cluster  │  │ Scheduler│  │ Controller │  │
│  │Controller│  │Autoscaler│  │          │  │ Manager    │  │
│  └─────────┘  └─────────┘  └──────────┘  └─────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                     Worker Node                              │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Deployment: myapp v1 (3 replicas)                    │   │
│  │  Pod: myapp-7d8f9 (Container: app)                    │   │
│  │    ├── Liveness Probe: /health (port 8080)            │   │
│  │    ├── Readiness Probe: /ready (port 8080)            │   │
│  │    ├── CPU req: 200m, limit: 500m                     │   │
│  │    └── Mem req: 256Mi, limit: 512Mi                   │   │
│  │  Pod: myapp-8e2a3 (Container: app)                    │   │
│  │  Pod: myapp-9f3b1 (Container: app)                    │   │
│  └──────────────────────────────────────────────────────┘   │
│  PDB: minAvailable=2                                        │
└──────────────────────────────────────────────────────────────┘
```

## Scaling Components Interaction
```
Metrics Server → HPA → Deployment → ReplicaSet → Pods
Cluster Autoscaler ↔ Cloud Provider API ↔ Node Pools
KEDA ↔ Event Sources (Kafka, RabbitMQ, HTTP) → HPA
VPA → Recommends CPU/Mem → Deployment (mutating webhook)
```
