# Kubernetes MOCK_INTERVIEW.md

## Scenario 1: Pod CrashLoopBackOff
A deployment is stuck in CrashLoopBackOff. All pods restart immediately.

**Questions**:
1. How would you diagnose the issue?
2. What tools/commands would you use?
3. How do liveness, readiness, and startup probes differ?
4. How would you fix a container that exits with non-zero code?

**Expected approach**: `kubectl describe pod`, `kubectl logs`, `kubectl get events`, check image tag, env vars, configmaps, secrets. Probes: liveness (restart container), readiness (service traffic), startup (delay liveness). Fix: debug locally, fix entrypoint, add probes.

## Scenario 2: Cluster Scaling
Your application traffic spikes 5x during business hours. Current pods can't handle load.

**Questions**:
1. How would you set up horizontal pod autoscaling (HPA)?
2. Explain vertical pod autoscaling (VPA) vs HPA.
3. How does Cluster Autoscaler work?
4. What metrics should you base scaling decisions on?

**Expected approach**: HPA based on CPU/memory or custom metrics (requests per second). VPA adjusts resource requests. Cluster Autoscaler adds/removes nodes. Metrics: CPU utilization, request latency, queue depth, 4 golden signals.

## Scenario 3: RBAC and Security
A new team member needs read-only access to one namespace.

**Questions**:
1. Design the RBAC configuration (Role, RoleBinding).
2. How would you audit who did what in the cluster?
3. Explain PodSecurityPolicy (deprecated) vs PodSecurityAdmission.
4. How do NetworkPolicies work?

**Expected approach**: `Role` with `get, list, watch` verbs on pods/services in namespace. `RoleBinding` for specific user/SA. Audit logs via `kubectl logs kube-apiserver`, cloud audit logs, `kubescape`. PodSecurityAdmission: privileged, baseline, restricted. NetworkPolicy: ingress/egress rules with pod/namespace selectors.

## Scenario 4: Stateful Application
You need to run a stateful database (PostgreSQL) on Kubernetes.

**Questions**:
1. Design a StatefulSet for PostgreSQL. What's different from Deployment?
2. How does PersistentVolumeClaim templating work in StatefulSets?
3. How do you handle database backups?
4. What's the deployment strategy for StatefulSets?

**Expected approach**: StatefulSet with `volumeClaimTemplates`, ordered rolling update, stable network identity (`pod-{0..N-1}.service`). Backups via `pg_dump` cronjob, Velero, or cloud-native snapshots. `podManagementPolicy: OrderedReady`. Anti-affinity for HA.

## Scenario 5: Service Exposure
You need to expose multiple microservices to the internet.

**Questions**:
1. Compare Service types (ClusterIP, NodePort, LoadBalancer).
2. How does Ingress work? When to use it vs Gateway API?
3. Design an Ingress for routing `api.example.com` and `app.example.com`.
4. How would you handle TLS termination?

**Expected approach**: Ingress controller (NGINX, Traefik, HAProxy) with TLS secret, host-based routing, annotations for rate limiting. Gateway API for more advanced routing (HTTP, TCP, TLS, gRPC). AWS LoadBalancer Controller for ALB/NLB integration.

## Key Kubernetes Interview Questions
1. Explain the Kubernetes control plane components.
2. How does the scheduler work (filtering → scoring → binding)?
3. What's etcd and how does it achieve consensus?
4. Explain kube-proxy modes (iptables, IPVS, eBPF).
5. How do you upgrade a Kubernetes cluster with zero downtime?
6. What's the difference between a CNI and CSI?
7. Explain taints and tolerations with examples.
8. How do you debug a node that's NotReady?
9. What's the pod eviction lifecycle?
10. Explain Kubernetes QoS classes (Guaranteed, Burstable, BestEffort).

## Whiteboard Challenge
Design a multi-tenant Kubernetes cluster for 5 teams. Consider namespaces, RBAC, resource quotas, network policies, and monitoring.

## Follow-up
1. How would you handle a node failure?
2. How do you ensure etcd backup and recovery?
3. What GitOps tool would you use and why?