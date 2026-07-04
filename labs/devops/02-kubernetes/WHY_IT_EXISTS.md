# Why Kubernetes Exists

## The Problem
- **Manual container management**: Running containers on single hosts doesn't scale.
- **No self-healing**: Failed containers stay dead.
- **No load balancing**: Manual port mapping and proxy configuration.
- **No rolling updates**: Updating applications causes downtime.
- **No auto-scaling**: Cannot scale based on load automatically.
- **Vendor lock-in**: Each cloud has different container management APIs.

## Kubernetes' Solution
- **Declarative state**: You declare desired state; K8s reconciles actual state.
- **Self-healing**: Restarts, replaces, and reschedules failed containers.
- **Service discovery**: DNS-based service resolution built-in.
- **Rolling updates**: Zero-downtime deployments with rollback.
- **Auto-scaling**: Horizontal Pod Autoscaler based on CPU/memory/custom metrics.
- **Portability**: Same manifests work on-premises, any cloud, or hybrid.
