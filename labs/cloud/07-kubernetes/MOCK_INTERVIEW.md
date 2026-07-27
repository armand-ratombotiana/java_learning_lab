# Mock Interview — Kubernetes

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Troubleshooting
- **Difficulty**: Professional (CKA level)

## Warm-Up (5 min)

Q1: Explain the architecture of a Kubernetes cluster. What are the main components of the control plane and worker nodes?

Q2: What is the difference between a Deployment and a StatefulSet? When would you use each?

## Technical Questions (20 min)

### Question 1: Deployment Design (10 min)
Create a Kubernetes Deployment YAML for a Java Spring Boot application with these requirements:
- 3 replicas, rolling update (max surge 1, max unavailable 0)
- Resource requests: 512Mi RAM, 500m CPU; limits: 1Gi RAM, 1000m CPU
- Liveness probe: HTTP GET /actuator/health/liveness, initial delay 30s
- Readiness probe: HTTP GET /actuator/health/readiness, initial delay 15s
- Environment variables from a ConfigMap and a Secret (database password)

### Question 2: Networking (10 min)
A Pod cannot connect to a Service in the same namespace. Walk through the troubleshooting steps. Consider:
- Service DNS resolution
- Endpoints/reachable targets
- Network policies
- CNI issues
- Pod readiness

## Behavioral Question (10 min)

**Question**: Tell me about a time you debugged a complex Kubernetes issue in production. What was the problem and how did you resolve it?

## System Design Whiteboard (10 min)

**Problem**: Design a GitOps workflow for deploying microservices to Kubernetes. Include:
- Source code repositories
- Image building and registry
- Manifest repository (GitOps)
- ArgoCD or Flux deployment
- Canary or blue-green deployment strategy
- Rollback mechanism

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| Deployments | Probes, strategies, resource limits | Basic deployment | Missing probes |
| Services | ClusterIP, NodePort, LoadBalancer, Ingress | Basic service types | Can't differentiate |
| Networking | DNS, CNI, Network Policies, debugging | Basic networking | No troubleshooting |
| Security | RBAC, PodSecurity, Secrets, ServiceAccount | Basic RBAC | No security awareness |
| GitOps | ArgoCD/Flux, sync strategies, rollbacks | Knows GitOps concept | Not familiar |

## Sample Solution Outline

### Deployment YAML
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: spring-app
  template:
    metadata:
      labels:
        app: spring-app
    spec:
      containers:
      - name: app
        image: myrepo/spring-app:1.0.0
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 15
          periodSeconds: 5
        env:
        - name: DB_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: db_url
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: db_password
```

### Troubleshooting Pod-Service Connectivity
1. Check Service exists: `kubectl get svc`
2. Check Endpoints: `kubectl get endpoints` (should match pod IPs)
3. Check DNS: `kubectl run test --image=busybox --rm -it -- nslookup <service>`
4. Check Pod logs: `kubectl logs <pod>`
5. Check NetworkPolicies: `kubectl get netpol -n <namespace>`
6. Check kube-proxy: `kubectl logs -n kube-system kube-proxy-<node>`
7. Check CNI pods: `kubectl get pods -n kube-system | grep -E 'calico|cilium|flannel|weave'`
8. Check kube-dns/CoreDNS: `kubectl logs -n kube-system <coredns-pod>`

### GitOps Workflow
- Developer pushes code → GitHub
- GitHub Actions / Jenkins builds Docker image → pushes to ECR
- Updates manifest repo (k8s-manifests) with new image tag
- ArgoCD syncs from manifest repo to EKS cluster
- ArgoCD detects drift, applies changes
- Canary deployment: Flagger or Argo Rollouts for traffic shifting (10% → 50% → 100%)
- Prometheus metrics evaluate canary success
- Rollback: `argocd app rollback <app-name> <revision>` or revert in Git
