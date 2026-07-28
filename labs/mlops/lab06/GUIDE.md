# Lab 06: Kubernetes for ML — Guide

## Step 1: Build and Push Docker Image

```bash
cd lab05
docker build -t mlops-model-server:latest .
docker tag mlops-model-server:latest mlops-model-server:v1
```

## Step 2: Create Deployment Manifest

The `k8s-deployment.yaml` defines:
- 3 replicas with rolling update strategy
- Resource requests (256m CPU, 512Mi memory) and limits (1 CPU, 1Gi memory)
- Liveness and readiness probes pointing to /healthz and /readyz

## Step 3: Create Service and HPA

The service exposes port 80 targeting container port 8080.
The HPA scales from 2-10 replicas based on CPU utilization at 60%.

## Step 4: Deploy to Kubernetes

```bash
kubectl apply -f k8s-deployment.yaml
kubectl apply -f k8s-service.yaml
kubectl apply -f k8s-hpa.yaml
kubectl get pods
kubectl get svc
```

## Step 5: Test the Deployment

```bash
kubectl port-forward service/mlops-model-service 8080:80
curl -X POST http://localhost:8080/predict -H "Content-Type: application/json" -d '{"features":[1.0,2.0,3.0,4.0]}'
```

## Key Kubernetes Concepts for ML

| Concept | ML Application |
|---------|---------------|
| Pod | Single instance of model server |
| Deployment | Rolling update of model versions |
| Service | Stable network endpoint for model API |
| HPA | Auto-scale based on inference load |
| ConfigMap | Model configuration, feature flags |
| Secret | API keys, model encryption keys |
| Ingress | External routing, TLS termination |
| Namespace | Isolation per team/project |

## Best Practices
- Use pod disruption budgets for critical model serving
- Implement graceful shutdown (preStop hook)
- Set proper resource requests/limits to avoid CPU throttling
- Use affinity rules to spread model pods across nodes
- Monitor with Prometheus metrics + Grafana dashboards
