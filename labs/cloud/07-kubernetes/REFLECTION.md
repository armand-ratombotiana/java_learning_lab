# Reflection — Kubernetes

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| Pod and Deployment creation | ☐ | ☐ | ☐ |
| Service types and networking | ☐ | ☐ | ☐ |
| ConfigMaps and Secrets | ☐ | ☐ | ☐ |
| Ingress and TLS | ☐ | ☐ | ☐ |
| Persistent Volumes and StatefulSets | ☐ | ☐ | ☐ |
| Helm charts | ☐ | ☐ | ☐ |
| RBAC and security | ☐ | ☐ | ☐ |
| HPA and cluster autoscaler | ☐ | ☐ | ☐ |
| Monitoring (Prometheus/Grafana) | ☐ | ☐ | ☐ |

## Journal Prompts

1. How does Kubernetes change the way you deploy and manage Java applications?

2. Compare Kubernetes to Docker Compose — when would you use each?

3. What is the most challenging aspect of Kubernetes for a team transitioning from EC2?

4. How would you design a multi-region disaster recovery plan for a K8s workload?

5. What security measures are essential before running Kubernetes in production?

## Key Takeaways
- Declarative configuration drives desired state reconciliation
- Deployments manage rolling updates and rollbacks automatically
- Services provide stable networking for ephemeral pods
- Probes (liveness, readiness, startup) ensure application health
- ConfigMaps and Secrets externalize configuration from images
- Helm packages Kubernetes applications as reusable charts
- RBAC, NetworkPolicies, PodSecurity enforce security
- HPA + Cluster Autoscaler provide elastic scaling
- Always set resource requests and limits
- GitOps (ArgoCD) provides deployment automation and audit trail
