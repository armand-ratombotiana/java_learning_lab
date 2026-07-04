# Advanced Orchestration Debugging

## Debug Commands
```powershell
# Check HPA status
kubectl describe hpa app-hpa
kubectl get hpa -o yaml

# Check current metrics
kubectl top pods
kubectl top nodes

# Check rollout status
kubectl rollout history deployment/myapp
kubectl rollout status deployment/myapp

# Check events
kubectl get events --sort-by='.lastTimestamp'

# Check PDB status
kubectl describe pdb app-pdb

# Check quota usage
kubectl describe quota team-quota

# Simulate resource constraints
kubectl run stress --image=progrium/stress -- --cpu 2 --memory 512M
```

## Common Issues
- **HPA not scaling**: Check metrics-server is running; check HPA target ref.
- **Rolling update stuck**: Check pod status, PDB might prevent termination.
- **Probe failures**: Check probe endpoint, container logs, network policy.
- **Pod pending**: Check resource quota, node capacity, PVC binding.
- **Cluster autoscaler not working**: Check cloud provider config, node group tags.
