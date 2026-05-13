# Debugging Kubernetes Pod and Service Issues

## Common Failure Scenarios

### Pod Stuck in Pending State

Pods remain in Pending status and never schedule. The desired replica count cannot be achieved. Applications don't start because pods cannot be created. This indicates resource or scheduling issues.

Insufficient cluster resources cause pending pods. Check node CPU and memory capacity with `kubectl describe nodes`. Nodes may have taints preventing pod scheduling. Review node resources vs pod requests.

Storage volume issues prevent pod startup. PVCs may be stuck in Pending if storage provisioner has issues. Check PVC status with `kubectl get pvc`. Verify storage class exists and is working.

### Pod Crashing / Restart Loop

Containers restart repeatedly. Pod shows CrashLoopBackOff status. Logs show the application crashing immediately. Memory limits may be too low, or the application has a startup error.

OOMKilled indicates memory limits are too low. Check container memory usage and increase limits. Review the application for memory leaks. Use `kubectl describe pod` to see the exit code.

Application errors cause crashes—check the logs with `kubectl logs`. Review the application startup sequence. Verify environment variables and configuration are correct.

### Service Not Reaching Pods

Service exists but cannot route traffic to pods. Requests return "no endpoints" errors. DNS resolves but connections fail. This happens when pod selectors don't match or pods aren't ready.

Verify pod labels match service selector. Use `kubectl get pods --show-labels` to see pod labels. Check the service selector with `kubectl get svc`. The labels must match exactly.

Pods may not be ready for traffic. Check pod readiness gates with `kubectl describe pod`. The container may not have reported ready. Review the readinessProbe configuration.

## Stack Trace Examples

**Image pull failure:**
```
Failed to pull image "nginx:latest": rpc error: code = Unknown desc = Error response from daemon
```

**Liveness probe failure:**
```
Liveness probe failed: HTTP probe failed with statuscode: 503
```

**Eviction due to memory pressure:**
```
The node was low on resource: memory
```

## Debugging Techniques

### Pod Diagnostics

Use `kubectl describe pod` to see pod events and status. Check for image pull, scheduling, and readiness failures. Review the Events section at the bottom.

Check pod logs with `kubectl logs` and `kubectl logs --previous` for previous container logs. Use `kubectl exec` to enter containers and debug interactively.

Review resource requests and limits. Check if limits are causing throttling. Monitor actual usage vs requested to optimize configuration.

### Service Debugging

Verify service endpoints exist with `kubectl get endpoints`. No endpoints means selector mismatch. Use `kubectl describe endpoints` to see which pods are included.

Test service connectivity from within the cluster. Create a debug pod with `kubectl run debug --image=busybox --rm -it -- sh`. Use `wget` to test service endpoints.

Check DNS resolution with `nslookup <service-name>`. The service should resolve to cluster IP. Check CoreDNS pods are running with `kubectl get pods -n kube-system`.

## Best Practices

Set appropriate resource requests and limits. Use horizontal pod autoscaler for scaling. Configure proper liveness and readiness probes.

Use network policies to restrict traffic between pods. Implement proper pod disruption budgets. Use labels and selectors for service organization.

Monitor cluster events and set up alerts. Review pod logs continuously. Implement proper logging and tracing.