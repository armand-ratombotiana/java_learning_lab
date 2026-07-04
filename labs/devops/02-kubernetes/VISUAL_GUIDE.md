# Visual Guide to Kubernetes

## Cluster Architecture
```
┌─────────────────────────────────────────────────────┐
│                   Control Plane                      │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐              │
│  │   API   │  │Scheduler│  │Control  │              │
│  │ Server  │  │         │  │Manager  │              │
│  └────┬────┘  └─────────┘  └─────────┘              │
│       │                                              │
│  ┌────▼────┐                                         │
│  │  etcd   │                                         │
│  └─────────┘                                         │
└──────────────────────┬───────────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────────┐
│                 Worker Node                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ kubelet  │  │kube-proxy│  │  Container       │  │
│  │          │  │          │  │  Runtime(CONTAINERD)│  │
│  └────┬─────┘  └──────────┘  └──────────────────┘  │
│       │                                              │
│  ┌────▼─────────────────────────────────────┐       │
│  │  Pod: app-v1-7d8f9 (Container: nginx)    │       │
│  │  Pod: app-v1-8e2a3 (Container: nginx)    │       │
│  │  Pod: fluentd-9f3b1 (Container: fluentd) │       │
│  └───────────────────────────────────────────┘       │
└──────────────────────────────────────────────────────┘
```

## Deploying an Application
```
kubectl apply -f deploy.yaml
    │
    ▼
API Server → etcd (store desired state)
    │
    ▼
Deployment Controller → Creates ReplicaSet
    │
    ▼
ReplicaSet Controller → Creates Pod Specs
    │
    ▼
Scheduler → Binds Pods to Nodes
    │
    ▼
kubelet → Runs Containers via CRI
```
