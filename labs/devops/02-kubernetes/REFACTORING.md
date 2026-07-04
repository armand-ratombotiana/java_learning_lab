# Kubernetes Refactoring

## Before (Monolithic Deployment)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: monolith
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: app
        image: myapp:1.0
        env:
        - name: DB_URL
          value: "jdbc:postgresql://localhost:5432/db"
        - name: DB_PASSWORD
          value: "password123"
```

## After (Refactored with Best Practices)
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    spec:
      serviceAccountName: app-sa
      securityContext:
        runAsUser: 1000
        runAsNonRoot: true
      containers:
      - name: app
        image: myapp:1.0
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: db-secret
        resources:
          requests: { cpu: 200m, memory: 256Mi }
          limits: { cpu: 1, memory: 512Mi }
        livenessProbe: { httpGet: { path: /health, port: 8080 } }
        readinessProbe: { httpGet: { path: /ready, port: 8080 } }
```

## Gains
- Security: non-root, secrets externalized
- Reliability: probes, resource limits, rolling updates
- Maintainability: config externalized, horizontal scaling ready
