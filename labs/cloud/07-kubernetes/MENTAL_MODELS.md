# Mental Models for Kubernetes

## 1. The Ship (Cluster) Analogy

```
Kubernetes Cluster = Container Ship
├── Master Nodes = Bridge (captain, navigation)
│   ├── API Server = Radio room (all communication)
│   ├── Scheduler = Navigator (where to place containers)
│   ├── Controller Manager = Engineer (keep things running)
│   └── etcd = Ship log (source of truth)
│
├── Worker Nodes = Crew quarters
│   ├── kubelet = Deck officer (reports to bridge)
│   ├── kube-proxy = Bosun (manages network rules)
│   └── containerd = Galley (prepares containers)
│
└── Pods = Cargo containers
    ├── One or more containers
    ├── Shared IP, storage, lifecycle
    └── Ephemeral (can be replaced at any time)
```

## 2. The Desired State Loop

```
I want 3 replicas of my Java app (desired state)
         │
         ▼
kubectl apply -f deployment.yaml
         │
         ▼
API Server stores in etcd
         │
         ▼
Controller Manager compares: current (0) vs desired (3)
         │
         ▼
Scheduler assigns 3 pods to worker nodes
         │
         ▼
kubelet starts containers on each node
         │
         ▼
Node reports back: 3 running pods (current state = desired)
         │
         ▼
If pod dies → Controller creates replacement (always reconciles)
```

## 3. The Service Abstraction

```
Users ──► Service (stable IP/DNS)
              │
              │ label selector
              │
      ┌───────┼───────┐
      │       │       │
      ▼       ▼       ▼
   Pod-A    Pod-B    Pod-C
   (app:v1) (app:v1) (app:v1)

Service = stable front door to dynamic pods
Pods come and go; Service IP never changes
Load balancing: round-robin or session affinity
```

## 4. The YAML Stack

```
Kubernetes resources are YAML documents describing desired state.

Deployment ──► manages ReplicaSet ──► manages Pods
Service     ──► exposes Pods via stable endpoint
Ingress     ──► HTTP/HTTPS routing to Services
ConfigMap   ──► external configuration (non-sensitive)
Secret      ──► external configuration (sensitive)
PVC         ──► persistent storage request
```

## 5. The Control Plane vs Data Plane

```
Control Plane (brain):        Data Plane (muscle):
┌──────────────────────┐     ┌──────────────────────┐
│ API + Scheduler +    │     │ kubelet + kube-proxy  │
│ Controller + etcd    │     │ + runtime (containerd) │
│                      │     │                        │
│ Makes decisions      │     │ Executes decisions     │
│ Stores state         │     │ Runs actual containers │
│ Schedules workloads  │     │ Proxies traffic        │
└──────────────────────┘     └──────────────────────┘
```
