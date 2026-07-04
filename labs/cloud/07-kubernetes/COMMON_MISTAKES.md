# Common Mistakes — Kubernetes

## Deployment Mistakes

### 1. No Resource Limits
**Mistake**: Pods without `resources.limits.memory`.
**Issue**: Java pod can use unlimited memory → OOM other pods, node instability.
**Fix**: Always set requests (guaranteed) and limits (cap).

### 2. Readiness Probe Misconfigured
**Mistake**: Readiness probe same as liveness probe.
**Issue**: Pods with transient failures get killed instead of traffic-removed.
**Fix**: Readiness = "ready to serve" (might fail); Liveness = "should be restarted" (always succeed or restart).

### 3. Using latest Tag
**Mistake**: `image: myapp:latest`.
**Issue**: Unpredictable which version runs; can't rollback by tag.
**Fix**: Use semantic versions: `myapp:1.0.0`.

### 4. Not Setting Pod Disruption Budget
**Mistake**: No PDB for stateful workload.
**Issue**: Node drain can terminate all replicas simultaneously.
**Fix**: `minAvailable: 2` or `maxUnavailable: 1`.

## Service Mistakes

### 5. Wrong Service Type
**Mistake**: LoadBalancer for internal-only service.
**Issue**: Creates unnecessary public ALB (costs money).
**Fix**: Use ClusterIP for internal, NodePort for dev access, LB only when needed.

### 6. Missing Selector
**Mistake**: Service selector doesn't match pod labels.
**Issue**: No endpoints created; service returns connection refused.
**Fix**: `kubectl get endpoints` — check endpoint addresses.

## Configuration Mistakes

### 7. Secrets in ConfigMaps
**Mistake**: Storing passwords in ConfigMap (base64 only, not encrypted).
**Issue**: Anyone with access to etcd or ConfigMap can read secrets.
**Fix**: Use Secret resource (base64 + encryption at rest + RBAC).

### 8. Hardcoded Values in Pod Spec
**Mistake**: Environment variables hardcoded in deployment YAML.
**Issue**: Configuration can't be changed without redeploy.
**Fix**: Use ConfigMap + Secret with envFrom.

## Networking Mistakes

### 9. No Network Policy
**Mistake**: Default K8s allows all pod-to-pod traffic.
**Issue**: Compromised pod can attack other services.
**Fix**: Apply NetworkPolicy to restrict ingress/egress per namespace/pod.

### 10. Ingress Without TLS
**Mistake**: Ingress rule with no TLS section.
**Issue**: Traffic to domain is HTTP, not HTTPS.
**Fix**: Use cert-manager + Let's Encrypt for automatic TLS certificates.

## Stateful Mistakes

### 11. Using Deployment for Stateful Workloads
**Mistake**: Deployment with PVC for database.
**Issue**: All pods share same PVC (read-write conflicts) or data loss on reschedule.
**Fix**: Use StatefulSet (stable identity, dedicated PVC per pod).

### 12. Not Planning for PVC Failure
**Mistake**: No storage class with reclaim policy.
**Issue**: Deleting PVC doesn't release underlying storage (EBS) → orphaned volumes.
**Fix**: Set reclaimPolicy: Delete or use dynamic provisioning.
