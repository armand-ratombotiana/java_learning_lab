# Kubernetes - THEORY

## Overview

Kubernetes (K8s) is an open-source container orchestration platform that automates deployment, scaling, and management of containerized applications.

## 1. Core Concepts

### What is Kubernetes?
- Container orchestration system
- Automates deployment, scaling, networking
- Self-healing and rollback capabilities
- Works with Docker, containerd, CRI-O

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Kubernetes Cluster                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                   Control Plane                       │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────────────┐ │   │
│  │  │ kube-  │ │ kube-  │ │ etcd   │ │ cloud-          │ │   │
│  │  │ apiserver│ │scheduler│ │       │ │ controller     │ │   │
│  │  └────────┘ └────────┘ └────────┘ └────────────────┘ │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                     Data Plane                        │   │
│  │                                                       │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐              │   │
│  │  │ Node 1  │  │ Node 2  │  │ Node 3  │              │   │
│  │  │┌─────┐ │  │┌─────┐ │  │┌─────┐ │              │   │
│  │  ││Pod A│ │  ││Pod B│ │  ││Pod C│ │              │   │
│  │  │└─────┘ │  │└─────┘ │  │└─────┘ │              │   │
│  │  └─────────┘  └─────────┘  └─────────┘              │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Control Plane Components

1. **kube-apiserver**: API server - front-end for control plane
2. **etcd**: Key-value store for cluster state
3. **kube-scheduler**: Assigns pods to nodes
4. **kube-controller-manager**: Runs controller processes
5. **cloud-controller-manager**: Cloud-specific controllers

### Node Components

1. **kubelet**: Agent on each node
2. **kube-proxy**: Network proxy
3. **container runtime**: Docker, containerd, CRI-O

## 2. Core Resources

### Pods
- Smallest deployable unit
- Contains one or more containers
- Shared network namespace
- Ephemeral by nature

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  containers:
  - name: app
    image: my-app:1.0
    ports:
    - containerPort: 8080
    resources:
      limits:
        memory: "256Mi"
        cpu: "500m"
```

### ReplicaSet
- Ensures specified number of pod replicas running
- Usually managed by Deployment

### Deployment
- Manages ReplicaSets
- Provides rolling updates and rollbacks

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: app
        image: my-app:1.0
        ports:
        - containerPort: 8080
```

### Service
- Stable network endpoint
- Load balancing across pods
- Types: ClusterIP, NodePort, LoadBalancer

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  selector:
    app: my-app
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

### ConfigMap & Secret
- Configuration and sensitive data
- Decoupled from container images

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  DATABASE_HOST: postgres
  CACHE_SIZE: "1000"
---
apiVersion: v1
kind: Secret
metadata:
  name: app-secret
type: Opaque
stringData:
  DB_PASSWORD: supersecret
```

## 3. Networking

### DNS and Service Discovery
- Cluster DNS (kube-dns/coredns)
- Services get DNS names
- Pods can reach services via DNS

### Network Policies
- Control traffic flow between pods
- Ingress and egress rules
- Namespace isolation

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-network-policy
spec:
  podSelector:
    matchLabels:
      role: api
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          role: frontend
    ports:
    - protocol: TCP
      port: 8080
```

## 4. Storage

### Persistent Volumes (PV)
- Cluster-wide storage resource
- Lifecycle independent of pods

### Persistent Volume Claims (PVC)
- Request for storage by pods
- Abstracts underlying storage

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: app-storage
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
```

### Storage Classes
- Dynamic volume provisioning
- Different storage types (SSD, HDD, network)

## 5. Scaling

### Horizontal Pod Autoscaler (HPA)
- Automatically scales pods based on metrics

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: my-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: my-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### Vertical Pod Autoscaler (VPA)
- Adjusts container resource requests

## 6. Helm

### Package Manager for Kubernetes
- Charts: package templates
- Values: configuration overrides
- Release: deployed chart instance

```bash
# Install chart
helm install my-release bitnami/wordpress

# List releases
helm list

# Upgrade
helm upgrade my-release bitnami/wordpress

# Rollback
helm rollback my-release 1
```

### Chart Structure

```
my-chart/
├── Chart.yaml
├── values.yaml
├── charts/
├── templates/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── _helpers.tpl
└── README.md
```

## 7. Best Practices

### Resource Management
- Set CPU/memory limits
- Use LimitRanges
- Implement QoS classes

### Health Checks
- Liveness probes: is container alive?
- Readiness probes: can container serve traffic?
- Startup probes: initial startup

### Security
- Use SecurityContext
- Run as non-root
- Read-only root filesystem
- Network policies

### High Availability
- Multiple replicas
- PodDisruptionBudget
- Anti-affinity rules

## Summary

Kubernetes provides:
1. **Orchestration**: Manage containerized apps at scale
2. **Self-healing**: Restart failed containers
3. **Scaling**: Horizontal and vertical autoscaling
4. **Service discovery**: Built-in DNS
5. **Storage**: Persistent volumes and claims
6. **Rolling updates**: Zero-downtime deployments