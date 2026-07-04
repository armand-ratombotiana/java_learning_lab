# Why Advanced Orchestration Exists

## The Problem
- **Static scaling**: Manual replica counts can't handle traffic spikes.
- **Deployment downtime**: Updating applications causes user-facing outages.
- **Health blind spots**: K8s assumes containers are healthy unless told otherwise.
- **Resource contention**: Noisy neighbor pods consume all node resources.
- **Maintenance disruptions**: Node drains kill critical pods without warning.

## The Solution
- **HPA**: Automatically scales based on real-time metrics.
- **Rolling updates + probes**: Zero-downtime deployments with health verification.
- **PDBs**: Ensure availability during node maintenance.
- **Resource quotas**: Prevent namespace resource abuse.
- **QoS classes**: Critical pods get guaranteed resources.
