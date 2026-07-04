# Refactoring — Kubernetes

## 1. From Monolithic Deployment to Microservices

### Before
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: monolith
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: app
        image: monolith:1.0.0  # All-in-one JAR
        ports:
        - containerPort: 8080
```

### After
```yaml
# API Gateway
apiVersion: apps/v1
kind: Deployment
metadata: {name: gateway}
spec:
  replicas: 2
  template:
    spec:
      containers:
      - name: gateway
        image: gateway:1.0.0
        ports: [{containerPort: 8080}]
---
# User Service
apiVersion: apps/v1
kind: Deployment
metadata: {name: user-svc}
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: user-svc
        image: user-svc:1.0.0
        ports: [{containerPort: 8081}]
---
# Order Service
apiVersion: apps/v1
kind: Deployment
metadata: {name: order-svc}
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: order-svc
        image: order-svc:1.0.0
        ports: [{containerPort: 8082}]
```

## 2. From Hardcoded Config to ConfigMap + Secrets

### Before
```yaml
env:
- name: DB_URL
  value: "jdbc:postgresql://localhost:5432/mydb"
- name: DB_PASSWORD
  value: "password123"
```

### After
```yaml
envFrom:
- configMapRef:
    name: app-config
- secretRef:
    name: app-secrets
```

```bash
kubectl create configmap app-config --from-literal=DB_URL=jdbc:postgresql://db:5432/mydb
kubectl create secret generic app-secrets --from-literal=DB_PASSWORD=password123
```

## 3. From Manual Scaling to HPA

### Before
```bash
kubectl scale deployment spring-app --replicas=5
# Manual, no automation, must predict traffic
```

### After
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: spring-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: spring-app
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## 4. From Deployment to StatefulSet (for Stateful Apps)

### Before
```yaml
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        volumeMounts:
        - mountPath: /var/lib/mysql
          name: data
  volumeClaimTemplates:  # Not valid for Deployment!
```

### After
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  serviceName: mysql
  replicas: 3
  template:
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        volumeMounts:
        - mountPath: /var/lib/mysql
          name: data
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      storageClassName: gp3
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 20Gi
```
