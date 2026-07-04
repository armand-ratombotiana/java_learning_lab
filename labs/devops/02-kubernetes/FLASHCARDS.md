# Kubernetes Flashcards

**Q: What is the smallest deployable unit in Kubernetes?**
A: A Pod.

**Q: What is etcd?**
A: Distributed key-value store that holds all cluster state.

**Q: What does a Deployment manage?**
A: ReplicaSets, providing declarative updates for Pods.

**Q: What is a Service?**
A: A stable network endpoint for accessing a set of Pods.

**Q: What is an Ingress?**
A: HTTP/HTTPS routing rules to Services, typically with TLS termination.

**Q: What is kubelet?**
A: Node agent that ensures containers are running in Pods.

**Q: What is the scheduler's job?**
A: Assigns Pods to Nodes based on resource availability and constraints.

**Q: What are ConfigMaps for?**
A: Injecting non-sensitive configuration data into Pods.

**Q: What is the default service type?**
A: ClusterIP (internal cluster-only IP).

**Q: What is RBAC in Kubernetes?**
A: Role-Based Access Control for API server authorization.
