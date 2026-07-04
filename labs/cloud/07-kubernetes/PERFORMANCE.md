# Performance — Kubernetes

## Pod Resource Optimization

### JVM in Container
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1"
```

```bash
# JVM flags for containerized environment
-XX:+UseContainerSupport        # Auto-detect container memory
-XX:MaxRAMPercentage=70.0       # Heap = 70% of container memory
-XX:+UseZGC                     # Low-pause GC (JDK 17+)
-XX:ConcGCThreads=2             # GC threads based on CPU limit
-Djava.security.egd=file:/dev/./urandom  # Faster startup
```

### Limit Ranges
```yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: java-app-limits
spec:
  limits:
  - max:
      memory: "2Gi"
      cpu: "2"
    min:
      memory: "128Mi"
      cpu: "100m"
    default:
      memory: "512Mi"
      cpu: "500m"
    defaultRequest:
      memory: "256Mi"
      cpu: "250m"
    type: Container
```

## Cluster Autoscaling

### Cluster Autoscaler on EKS
```bash
# Cluster Autoscaler scales EC2 node groups
# Trigger: pending pods due to resource shortage

# Install
eksctl create iamserviceaccount --cluster java-cluster \
  --name cluster-autoscaler --namespace kube-system \
  --attach-policy-arn arn:aws:iam::xxx:policy/AutoScaling \
  --approve

kubectl apply -f https://raw.githubusercontent.com/kubernetes/autoscaler/master/cluster-autoscaler/cloudprovider/aws/examples/cluster-autoscaler-autodiscover.yaml
```

### Karpenter (EKS Optimized)
- Directly provisions EC2 instances (no need for ASG)
- Selects optimal instance type for pod requirements
- Sub-second node provisioning (vs 3-5 min with ASG)
- Consolidates pods to fewer nodes when possible

## Pod Anti-Affinity for Performance

```yaml
spec:
  affinity:
    podAntiAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        podAffinityTerm:
          labelSelector:
            matchExpressions:
            - key: app
              operator: In
              values:
              - java-app
          topologyKey: topology.kubernetes.io/zone
  # Spread pods across zones (preferred)
```

## Network Performance

### EKS with AWS VPC CNI
```
Pod-to-pod latency: ~0.3ms (same node), ~1ms (different node)
Pod-to-Pod throughput: up to 25 Gbps (depending on instance type)

AWS VPC CNI:
  - Each pod gets VPC IP (no NAT)
  - Direct ENI attachment (via trunking)
  - Security groups apply to pods (native AWS networking)
  - Max pods = ENI limits per instance
```

## Performance Benchmarking

```bash
# Install metrics server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Check pod resource usage
kubectl top pods
kubectl top nodes

# Install K6 benchmarking tool
kubectl run k6 --image=grafana/k6 -- /bin/sleep 3600
kubectl exec -it k6 -- k6 run -e TARGET=http://java-app-svc --vus 100 --duration 30s
```
