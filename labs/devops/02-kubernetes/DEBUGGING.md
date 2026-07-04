# Kubernetes Debugging Guide

## Diagnostic Commands
```powershell
# Pod logs
kubectl logs -f <pod-name>

# Describe resource
kubectl describe pod <pod-name>

# Events
kubectl get events --sort-by=.metadata.creationTimestamp

# Execute command in pod
kubectl exec -it <pod-name> -- sh

# Port forwarding for debugging
kubectl port-forward svc/my-service 8080:80

# Interactive debug pod
kubectl run debug --image=nicolaka/netshoot -it --rm

# Check pod resource usage
kubectl top pod <pod-name>
```

## Common Issues
- **ImagePullBackOff**: Wrong image name, registry auth, or tag.
- **CrashLoopBackOff**: Application error, check logs.
- **Pending**: Insufficient resources, PVC not bound, node selector mismatch.
- **Init:Error**: Init container failed.
- **Node NotReady**: Check kubelet status on node.
