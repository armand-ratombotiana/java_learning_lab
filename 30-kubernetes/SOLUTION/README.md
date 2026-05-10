# Kubernetes Solution

## Concepts Covered

### Deployments
- Replica management
- Container configuration
- Resource limits and requests
- Health checks (liveness/readiness)
- Security context

### Services
- ClusterIP, NodePort, LoadBalancer
- Port mapping
- Service discovery

### Ingress
- TLS configuration
- Routing rules
- Annotations

### Configuration
- ConfigMap for configuration
- Secret for sensitive data
- Environment variables

### Scaling
- HorizontalPodAutoscaler
- PodDisruptionBudget

### RBAC
- ServiceAccount
- Role and RoleBinding

## Commands

```bash
# Apply resources
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml

# Scale deployment
kubectl scale deployment myapp --replicas=5

# View status
kubectl get pods -l app=myapp
kubectl describe deployment myapp
kubectl logs -f deployment/myapp

# Access application
kubectl port-forward svc/myapp-service 8080:80
```

## Running Tests

```bash
mvn test -Dtest=KubernetesSolutionTest
```