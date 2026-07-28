# Lab 06: Interview Questions

## FAANG-Level Questions

### Q1: Design a Kubernetes-based ML serving platform for 500+ models.
**Answer**: Use a multi-tenant architecture with namespaces per team. Implement an Admission Controller for resource quotas and model validation. Use Istio service mesh for traffic splitting (canary deployments), mTLS, and observability. Each model gets a Deployment + Service + HPA. Use a model router (e.g., Seldon or custom) to route inference requests to the correct model pod based on model name/version.

### Q2: How do you handle model-specific resource requirements in K8s?
**Answer**: Use LimitRanges per namespace to enforce min/max resources. For GPU models, use node affinity with taints/tolerations to schedule on GPU nodes. Implement a custom scheduler or use Volcano/Kubeflow for gang scheduling of distributed training jobs.

### Q3: Rolling update vs blue-green vs canary deployments for ML models.
**Answer**: Rolling update is simplest but can cause partial errors during transition. Blue-green requires double resources but has instant rollback. Canary (via Istio/Service Mesh) routes X% traffic to new model version, enabling gradual rollout with metrics comparison. For ML, canary is preferred because you can compare model metrics before full rollout.

### Q4: How do you debug a model pod that's crashing on startup?
**Answer**: Check `kubectl logs <pod>`, describe pod for events, check liveness/readiness probe configuration, exec into pod to test model loading locally. Common issues: OOM (increase memory limit), model file not found (check volume mount), model dependency missing (check container image).

## LeetCode / NeetCode References
- **Design a Load Balancer** — K8s Service/Ingress concepts
- **Design Autoscaler** — HPA scaling logic
- **Design Distributed Cache** — Distributed model caching across pods
