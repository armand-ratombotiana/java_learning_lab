# 56 - Kubernetes Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Pod | Smallest deployable unit |
| Deployment | Manages replica sets |
| Service | Network endpoint for pods |
| Ingress | HTTP/HTTPS routing |
| ConfigMap | Non-sensitive configuration |
| Secret | Sensitive data storage |
| Namespace | Resource isolation |

## Pod Definition

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: myapp-pod
  labels:
    app: myapp
    version: v1
spec:
  containers:
  - name: myapp
    image: myapp:1.0
    ports:
    - containerPort: 8080
    env:
    - name: ENV
      value: production
    resources:
      requests:
        memory: "256Mi"
        cpu: "250m"
      limits:
        memory: "512Mi"
        cpu: "500m"
```

## Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: myapp
        image: myapp:1.0
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## Service

```yaml
# ClusterIP (default - internal)
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP

---
# NodePort (exposes on each node)
apiVersion: v1
kind: Service
metadata:
  name: myapp-nodeport
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    nodePort: 30080
    targetPort: 8080
  type: NodePort

---
# LoadBalancer (cloud provider)
apiVersion: v1
kind: Service
metadata:
  name: myapp-lb
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: myapp.example.com
    http:
      paths:
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 80
```

## ConfigMap & Secrets

```yaml
# ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
data:
  application.properties: |
    server.port=8080
    spring.profiles.active=production
  log-level: "INFO"

---
# Secret
apiVersion: v1
kind: Secret
metadata:
  name: myapp-secret
type: Opaque
stringData:
  db-password: changeme
  api-key: secret123
```

## Config Usage in Pod

```yaml
spec:
  containers:
  - name: myapp
    image: myapp:1.0
    envFrom:
    - configMapRef:
        name: myapp-config
    - secretRef:
        name: myapp-secret
    env:
    - name: SPECIAL_KEY
      valueFrom:
        configMapKeyRef:
          name: myapp-config
          key: log-level
```

## Commands

```bash
# Deploy resources
kubectl apply -f deployment.yaml
kubectl apply -f .

# Check status
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get all

# Debug
kubectl describe pod myapp-pod
kubectl logs myapp-pod
kubectl exec -it myapp-pod -- /bin/bash

# Scale
kubectl scale deployment myapp-deployment --replicas=5

# Update
kubectl set image deployment/myapp-deployment myapp=myapp:2.0

# Rollback
kubectl rollout history deployment/myapp-deployment
kubectl rollout undo deployment/myapp-deployment

# Delete
kubectl delete -f deployment.yaml
```

## StatefulSet

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  serviceName: mysql
  replicas: 3
  selector:
    matchLabels:
      app: mysql
  template:
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        volumeMounts:
        - name: data
          mountPath: /var/lib/mysql
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi
```

## Horizontal Pod Autoscaler

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp-deployment
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## Best Practices

Use namespaced resources for environment isolation. Set resource requests and limits. Use readiness and liveness probes. Store sensitive data in Secrets. Use ConfigMaps for configuration. Implement proper logging and monitoring. Use labels for organization and selection.