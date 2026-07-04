# Interview Questions — Kubernetes

## Beginner

**Q1**: What is the difference between a Pod and a Deployment?

**Q2**: Explain how a Service works in Kubernetes.

**Q3**: What is kubelet and what does it do?

## Intermediate

**Q4**: How does Kubernetes handle rolling updates?

**Q5**: What are ConfigMaps and Secrets? When would you use each?

**Q6**: Explain the difference between liveness, readiness, and startup probes.

**Q7**: How does Horizontal Pod Autoscaler work?

## Advanced

**Q8**: Explain the Kubernetes scheduler algorithm.

**Q9**: How does etcd ensure consistency in a Kubernetes cluster?

**Q10**: What's the difference between a Deployment and a StatefulSet?

**Q11**: Design a multi-tenant Kubernetes architecture with tenant isolation.

**Q12**: How does the AWS VPC CNI plugin work for EKS? What are its benefits and limitations?

## Sample Answers

**A1**: A Pod is the smallest deployable unit (one or more containers with shared networking/storage). A Deployment manages ReplicaSets, which manage Pods — providing rolling updates, rollbacks, scaling, and self-healing.

**A2**: A Service provides a stable network endpoint (ClusterIP) backed by a set of Pods selected by labels. kube-proxy configures iptables/IPVS rules to load-balance traffic to healthy Pods. Services can be ClusterIP (internal), NodePort (node port), or LoadBalancer (cloud LB).

**A3**: kubelet runs on each worker node. It watches the API Server for Pods assigned to its node, creates/runs containers via the container runtime (containerd), executes health probes, and reports Pod/node status back to the API Server.

**A4**: Rolling update gradually replaces old pods with new ones. Configurable via maxSurge (extra pods during update) and maxUnavailable (pods that can be down). K8s updates one pod at a time (or N for larger deployments), waiting for readiness before proceeding. If new pods fail, the rollout pauses; rollback reverts to previous ReplicaSet.

**A5**: Both store configuration externally. ConfigMaps hold non-sensitive data (environment names, URLs, config files). Secrets store sensitive data (passwords, API keys, certificates). Secrets are base64-encoded and can be encrypted at rest (KMS). Never store secrets in ConfigMaps.

## Key Topics for CKA/CKAD
- Pod design (labels, selectors, annotations)
- Deployments (strategy, rolling update, rollback)
- Services (ClusterIP, NodePort, LoadBalancer, Headless)
- Ingress and Ingress Controllers
- ConfigMaps and Secrets
- Persistent Volumes and Persistent Volume Claims
- StatefulSets (for stateful applications)
- Network Policies (pod-level firewall)
- RBAC (Roles, ClusterRoles, RoleBindings, ServiceAccounts)
- Helm (charts, templates, values)
- Namespaces for isolation
