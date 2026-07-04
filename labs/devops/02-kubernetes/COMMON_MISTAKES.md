# Common Kubernetes Mistakes

1. **Not setting resource requests/limits** — leads to noisy neighbor and OOM kills.
2. **Using `latest` tag** — unreproducible; always pin versions.
3. **Storing secrets in ConfigMaps or YAML files** — use external secrets operators.
4. **Running as root** — containers run as root by default; set securityContext.
5. **Not configuring liveness/readiness probes** — K8s can't detect dead containers.
6. **Hardcoding ConfigMaps/Secrets** — mount as volumes or env vars from manifests.
7. **Ignoring pod anti-affinity** — all pods may schedule on one node.
8. **Overly permissive RBAC** — namespace-scoped roles should limit permissions.
9. **Storing state in pods** — use StatefulSets with PVCs for stateful workloads.
10. **Not using network policies** — all pods can communicate by default.
11. **Mixing namespaces incorrectly** — Service DNS resolution differs across namespaces.
12. **Forgetting `kubectl apply` vs `kubectl create`** — apply is declarative, create is imperative.
