# Container Orchestration Internals

## HPA Controller
- Watches `horizontalpodautoscalers` API resource.
- Periodically calculates desired replica count.
- Updates deployment/statefulset replica count.
- Respects `--horizontal-pod-autoscaler-sync-period` (default 15s).
- Implements scale-up/scale-down stabilization windows.

## Cluster Autoscaler
- Watches unschedulable pods (Pending state due to resource constraints).
- Triggers node group scale-up when pending pods can't be scheduled.
- Triggers node group scale-down when nodes are underutilized for N minutes.
- Respects PDBs — won't scale down if it would violate PDB.

## Pod Lifecycle
```
Pending → ContainerCreating → Running → Succeeded/Failed
    │                          │
    │                          ├── liveness failure → restart
    │                          ├── readiness failure → remove from Service
    │                          └── startup failure (before startup probe passes)
```

## QoS Implementation
- **Guaranteed**: All containers have limits = requests (CPU and memory).
- **Burstable**: At least one container has requests < limits.
- **BestEffort**: No requests or limits set.
- OOM/Kubelet eviction order: BestEffort → Burstable → Guaranteed.
