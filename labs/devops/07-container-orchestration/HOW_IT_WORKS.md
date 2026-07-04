# How Advanced Orchestration Works

## HPA Algorithm
```
Desired Replicas = ceil[currentReplicas × (currentMetricValue / desiredMetricValue)]

For multiple metrics: desiredReplicas = max(desiredReplicas for each metric)

Example:
  - currentReplicas = 4
  - currentCPU = 75%, targetCPU = 50%
  - desiredReplicas = ceil[4 × (75/50)] = ceil[6] = 6
```

## Rolling Update Strategy
```
maxSurge: 25% → Allow 25% extra pods during update
maxUnavailable: 25% → Allow 25% pods unavailable during update

Example with 4 replicas:
1. Create 1 new pod (25% of 4)
2. Delete 1 old pod when new pod is ready
3. Repeat until all pods replaced
```

## Canary Deployment Flow
```
1. Deploy v2 with 1 replica alongside v1 (3 replicas)
2. Service routes to both (75% v1, 25% v2 via replica weight)
3. Monitor error rates and latency for v2
4. If healthy, scale up v2 to 3, scale down v1 to 0
5. Delete v1 deployment
```

## Probe Check Flow
```
kubelet → Run probe command/HTTP/TCP → Success → Container healthy
                                     → Failure → Count towards failureThreshold
                                               → Threshold exceeded → Action (restart/remove)
```
