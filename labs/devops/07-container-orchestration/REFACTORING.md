# Advanced Orchestration Refactoring

## Before (Basic Deployment)
```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: app
        image: myapp:1.0
        ports:
        - containerPort: 8080
```

## After (Production-Ready)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      containers:
      - name: app
        image: myapp:1.0
        ports:
        - containerPort: 8080
        resources:
          requests: { cpu: 200m, memory: 256Mi }
          limits: { cpu: 500m, memory: 512Mi }
        startupProbe:
          httpGet: { path: /health, port: 8080 }
          initialDelaySeconds: 5
          periodSeconds: 5
          failureThreshold: 30
        livenessProbe:
          httpGet: { path: /health, port: 8080 }
          periodSeconds: 15
          failureThreshold: 3
        readinessProbe:
          httpGet: { path: /ready, port: 8080 }
          periodSeconds: 10
          failureThreshold: 2
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
---
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: myapp-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: myapp
```

## Gains
- Zero-downtime deployments (maxUnavailable: 0)
- Auto-scaling based on CPU utilization
- Health monitoring with probes
- HA with PDB protection
