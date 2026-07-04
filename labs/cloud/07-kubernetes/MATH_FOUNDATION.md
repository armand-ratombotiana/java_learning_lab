# Math Foundation for Kubernetes

## Resource Allocation

### Pod Resource Calculation
```
Node capacity: 4 vCPU, 16GB RAM
System overhead: 0.5 vCPU, 2GB RAM
Available for pods: 3.5 vCPU, 14GB RAM

Java app requests: 0.25 vCPU, 256MB RAM
Java app limits: 0.5 vCPU, 512MB RAM

Max pods (CPU-based): 3.5 / 0.5 = 7 pods (limits)
Max pods (RAM-based): 14GB / 512MB = 28 pods
Limiting factor: CPU → max 7 pods per node
Actual capacity: 7 × 0.25 = 1.75 vCPU request → undercommitted
Overcommit ratio: 7 × 0.5 / 3.5 = 1.0 (limits = capacity)

For better density: use requests only for guaranteed resources
```

### Cluster Sizing
```
Desired: 100 Java microservice pods
Each: 0.25 vCPU, 512MB memory

vCPU needed: 100 × 0.25 = 25 vCPU
Memory needed: 100 × 512MB = 50GB
Overhead: 20% → 30 vCPU, 60GB

Node choice: m5.large (2 vCPU, 8GB RAM) = 15 nodes
Node choice: m5.xlarge (4 vCPU, 16GB RAM) = 7-8 nodes
Node choice: m5.2xlarge (8 vCPU, 32GB RAM) = 4 nodes
```

## Scaling Math

### Horizontal Pod Autoscaler (HPA)
```
Target: 70% CPU utilization
Current utilization: 50% across 3 pods

Desired replicas = ceil(3 × 50 / 70) = ceil(2.14) = 3 (no scale)

Current utilization: 85% across 3 pods
Desired replicas = ceil(3 × 85 / 70) = ceil(3.64) = 4 (scale out)

Scale-down threshold: 50% (HPA default cooldown: 5 min)
Metrics aggregation: average across all pods
```

### Cluster Autoscaler
```
Trigger: 1+ unschedulable pods (pending due to resource shortage)
Scale up: add node from node group
Scale down: node utilization < 50% for 10 min, pods can move

Cost: $0.10/hr per m5.large = $73/month per node
Autoscaler can save 30-50% by scaling down during low traffic
```

## Availability Math

### Multi-Zone Cluster
```
Single zone availability: 99.5%
Three zone availability: 1 - (1-0.995)³ = 99.99875%

Downtime per year:
  Single zone: 0.005 × 8760 = 43.8 hours
  Three zones: 0.0000125 × 8760 = 0.11 hours (6.6 minutes)
```

### Pod Anti-Affinity
```
Required anti-affinity: pods = 3, zones = 3
  → 1 pod per zone
  → Loss of 1 zone = 33% capacity loss

Preferred anti-affinity: pods = 3, zones = 3
  → Scheduler tries to spread (best effort)
  → May place 2 pods in same zone if resources tight

Impact: 3 pods across 3 zones = 99.99875% availability
```
