# Service Mesh Debugging Guide

## Debug Commands
```powershell
# Check Istio status
istioctl version
istioctl proxy-status

# Check Envoy configuration
istioctl proxy-config cluster <pod-name>
istioctl proxy-config listener <pod-name>
istioctl proxy-config route <pod-name>

# Check proxy logs
kubectl logs <pod-name> -c istio-proxy

# Test connectivity
kubectl exec <pod-name> -c istio-proxy -- curl -v httpbin:8000

# Check mTLS
istioctl authz check <pod-name>

# Check ingress gateway
kubectl logs -l istio=ingressgateway -n istio-system

# Analyze configuration
istioctl analyze
```

## Common Issues
- **Service not reachable**: Check VirtualService, DestinationRule, and Gateway configuration.
- **mTLS connection failed**: Check PeerAuthentication mode, certificate expiry.
- **Sidecar not injected**: Check namespace label, webhook status.
- **High sidecar latency**: Check Envoy admin endpoint, resource limits.
- **Configuration rejected**: Use `istioctl analyze` for validation.
