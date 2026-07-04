# Container Orchestration Theory

## Auto-Scaling
- **Horizontal Pod Autoscaler (HPA)**: Scale replicas based on CPU/memory/custom metrics.
- **Vertical Pod Autoscaler (VPA)**: Adjust CPU/memory requests/limits automatically.
- **Cluster Autoscaler**: Scale node pool size based on pending pods.

## Deployment Strategies
- **RollingUpdate**: Gradually replace old pods with new (default, zero-downtime).
- **Recreate**: Delete all old pods before creating new (downtime, simple).
- **Blue/Green**: Run two versions simultaneously, switch traffic.
- **Canary**: Gradually shift traffic percentage to new version.
- **A/B Testing**: Route traffic based on headers/cookies.

## Probes
- **livenessProbe**: Restarts container if it fails (is container alive?).
- **readinessProbe**: Removes pod from Service endpoints if it fails (is pod ready?).
- **startupProbe**: Delays liveness/readiness checks until startup completes (for slow-start apps).

## Pod Disruption Budgets (PDB)
- Ensures minimum available pods during voluntary disruptions (node maintenance, upgrades).
- `minAvailable: 2` or `maxUnavailable: 1`.

## Resource Management
- **Requests**: Guaranteed resources for scheduling.
- **Limits**: Maximum resources a container can consume.
- **Quality of Service (QoS)**: Guaranteed, Burstable, BestEffort.
- **ResourceQuota**: Namespace-level resource constraints.
- **LimitRange**: Default requests/limits per namespace.
