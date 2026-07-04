# Container Orchestration Performance

## HPA Tuning
- **Sync period**: Default 15s; can be adjusted via kube-controller-manager flag.
- **Metrics resolution**: Ensure metrics-server scrape interval ≤ HPA sync period.
- **Scale-up vs down behavior**: Configure different stabilization windows (faster up, slower down).
- **Custom metrics**: Use KEDA for event-driven scaling (more responsive than CPU-based).

## Rolling Update Performance
- **Parallelism**: `maxSurge` and `maxUnavailable` control update speed.
- **Image pull policy**: Use `IfNotPresent` for faster pod startup.
- **Init containers**: Run pre-flight checks in init containers (fast, sequential).
- **Readiness gates**: Custom conditions for fine-grained readiness control.

## Cluster Autoscaler Performance
- **Scale-up latency**: 1-5 minutes (node provisioning time).
- **Scale-down**: 10-15 minute cooldown to avoid thrashing.
- **Node pools**: Use multiple instance types for spot/preemptible diversity.

## Resource Optimization
- **Bin-packing**: Use descheduler to evict pods for better resource utilization.
- **Overcommit**: Set requests < limits for Burstable QoS (better utilization).
- **Vertical scaling**: VPA recommender analyzes historical usage for optimal requests.
