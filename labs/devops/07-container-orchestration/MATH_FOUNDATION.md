# Math Foundation for Container Orchestration

## HPA Formula
```
desiredReplicas = ceil[currentReplicas × (metricValue / targetMetricValue)]
```
- Handles multi-metric: takes max across all defined metrics.
- Stabilization window prevents thrashing (scale up/down cooldown).

## Rolling Update Math
```
maxUnavailable (absolute) = ceil(replicas × maxUnavailable%)
maxSurge (absolute) = ceil(replicas × maxSurge%)
```

## Cluster Autoscaler
- **Scale-up**: Needs sum(unschedulable pod requests) ≤ available node capacity.
- **Scale-down**: Node utilization < 50% for 10+ minutes, pods can be rescheduled.

## Resource Quotas
- Scalar: `count/deployments: "5"`, `count/pods: "20"`.
- Resource: `requests.cpu: "8"`, `limits.memory: "32Gi"`.
- Storage: `requests.storage: "100Gi"`, `count/persistentvolumeclaims: "10"`.

## KEDA Scaling
```
replicas = max(activationThreshold, ceil(metricValue / targetValue))
```
- Multiple triggers evaluated; highest result wins.
- Cooldown period prevents rapid scale-down.

## Pod Priority
- Priority = integer value (higher = more important).
- Preemption: lower-priority pods evicted to make room for higher-priority.
