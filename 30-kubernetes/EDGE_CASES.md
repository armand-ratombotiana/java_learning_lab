# Kubernetes Edge Cases

## Pod Lifecycle Issues

### CrashLoopBackOff

```bash
# Check pod status
kubectl get pods

# View logs
kubectl logs myapp-xyz
kubectl logs --previous myapp-xyz

# Debug
kubectl describe pod myapp-xyz
```

### ImagePullBackOff

```yaml
# Fix: Specify full image path with tag
spec:
  containers:
  - image: registry.example.com/myapp:v1.0.0
    imagePullPolicy: Always
```

### Pending Pods

```bash
# Check resource quotas
kubectl describe resourcequota

# Check node resources
kubectl describe nodes

# Check scheduling
kubectl get events --field-selector involvedObject.name=myapp-xyz
```

---

## Resource Issues

### Out of Memory (OOMKilled)

```yaml
# Fix: Increase memory limits
resources:
  requests:
    memory: "512Mi"
  limits:
    memory: "1Gi"
```

### CPU Throttling

```yaml
# Fix: Adjust CPU limits
resources:
  limits:
    cpu: "1000m"  # Increase from 500m
```

### Eviction

```yaml
# Fix: Add resources
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
```

---

## Networking Issues

### DNS Resolution Failures

```bash
# Check DNS
kubectl exec -it myapp -- nslookup kubernetes.default

# Check CoreDNS
kubectl get pods -n kube-system -l k8s-app=kube-dns
```

### Service Not Found

```yaml
# Fix: Ensure service matches pod labels
# Service
spec:
  selector:
    app: myapp  # Must match pod label
# Pod
spec:
  containers:
  - name: myapp
---
# or use endpoints
kubectl get endpoints myapp-service
```

### Connection Timeout

```yaml
# Fix: Increase timeout
livenessProbe:
  timeoutSeconds: 5
  failureThreshold: 3

readinessProbe:
  timeoutSeconds: 3
  failureThreshold: 3
```

---

## Volume Issues

### Mount Errors

```yaml
# Fix: Ensure volume exists
# PVC
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: myapp-data
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

### Volume Permissions

```yaml
# Fix: Set correct permissions
securityContext:
  runAsUser: 1000
  fsGroup: 1000
```

---

## Configuration Issues

### ConfigMap Not Updated

```yaml
# Fix: Update to trigger pod restart
# Option 1: Update ConfigMap
kubectl apply -f configmap.yaml

# Option 2: Restart pods
kubectl rollout restart deployment/myapp
```

### Secret Not Updated

```bash
# Secrets update immediately but require pod restart
kubectl rollout restart deployment/myapp
```

---

## Deployment Issues

### Rollback Failed

```bash
# Check revision history
kubectl rollout history deployment/myapp

# Rollback to previous
kubectl rollout undo deployment/myapp

# Rollback to specific revision
kubectl rollout undo deployment/myapp --to-revision=2
```

### Stuck in Pending

```bash
# Check events
kubectl get events | grep myapp

# Check node capacity
kubectl describe nodes
```

---

## Security Issues

### Forbidden Error

```yaml
# Fix: Create proper RBAC
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: myapp-role
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: myapp-rolebinding
subjects:
- kind: ServiceAccount
  name: myapp-sa
roleRef:
  kind: Role
  name: myapp-role
  apiGroup: rbac.authorization.k8s.io
```

### ImagePullSecrets

```yaml
# Fix: Create secret and reference
kubectl create secret docker-registry myapp-registry \
  --docker-server=registry.example.com \
  --docker-username=user \
  --docker-password=pass

# In deployment
spec:
  imagePullSecrets:
  - name: myapp-registry
```

---

## Troubleshooting Checklist

1. Check pod status: `kubectl get pods`
2. View logs: `kubectl logs <pod>`
3. Check events: `kubectl get events`
4. Describe resource: `kubectl describe <resource>`
5. Check node status: `kubectl get nodes`
6. Verify DNS: `kubectl exec <pod> -- nslookup <service>`
7. Check endpoints: `kubectl get endpoints`
8. Review resource quotas: `kubectl get resourcequota`

---

## Best Practices

1. **Always set resource limits**
2. **Use health checks**
3. **Monitor application logs**
4. **Set up proper RBAC**
5. **Use namespaces for isolation**
6. **Implement proper networking**
7. **Use secrets for sensitive data**
8. **Regular backup etcd**