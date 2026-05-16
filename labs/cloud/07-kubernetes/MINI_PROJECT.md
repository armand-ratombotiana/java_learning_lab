# Kubernetes - MINI PROJECT

## Project: Deploy Java Microservices Stack

Deploy a complete microservices stack with:
- User service (Spring Boot)
- Order service (Spring Boot)
- PostgreSQL database
- Redis cache

## Prerequisites

- Kubernetes cluster (minikube, kind, or cloud)
- kubectl configured
- Helm (optional)

## Architecture

```
                    ┌────────────────┐
                    │    Ingress     │
                    └───────┬────────┘
                            │
            ┌───────────────┼───────────────┐
            │               │               │
      ┌─────▼─────┐   ┌─────▼─────┐   ┌─────▼─────┐
      │   User    │   │  Order    │   │   API     │
      │  Service  │   │  Service  │   │  Gateway  │
      └─────┬─────┘   └─────┬─────┘   └───────────┘
            │               │
      ┌─────▼─────┐   ┌─────▼─────┐
      │ Postgres  │   │   Redis   │
      └───────────┘   └───────────┘
```

## Implementation

### Step 1: Create Namespace

```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: microservices
  labels:
    name: microservices
```

### Step 2: Create ConfigMap

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: microservices
data:
  SPRING_PROFILES_ACTIVE: "kubernetes"
  JAVA_OPTS: "-Xms256m -Xmx512m -XX:+UseG1GC"
```

### Step 3: Create Secrets

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: microservices
type: Opaque
stringData:
  DB_USER: "appuser"
  DB_PASSWORD: "supersecret123"
  REDIS_PASSWORD: "redis123"
```

### Step 4: User Service Deployment

```yaml
# user-service.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  namespace: microservices
spec:
  replicas: 2
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: app
        image: myregistry/user-service:1.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres:5432/users"
        - name: SPRING_REDIS_HOST
          value: "redis"
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: db-credentials
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

### Step 5: Order Service Deployment

```yaml
# order-service.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: microservices
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
      - name: app
        image: myregistry/order-service:1.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres:5432/orders"
        - name: USER_SERVICE_URL
          value: "http://user-service:8080"
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: db-credentials
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

### Step 6: Services

```yaml
# services.yaml
---
apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: microservices
spec:
  selector:
    app: user-service
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP

---
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: microservices
spec:
  selector:
    app: order-service
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP

---
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: microservices
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
  type: ClusterIP

---
apiVersion: v1
kind: Service
metadata:
  name: redis
  namespace: microservices
spec:
  selector:
    app: redis
  ports:
  - port: 6379
    targetPort: 6379
  type: ClusterIP
```

### Step 7: HPA

```yaml
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service-hpa
  namespace: microservices
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service
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

## Deploy

```bash
# Create namespace
kubectl apply -f namespace.yaml

# Deploy infrastructure (Postgres, Redis)
kubectl apply -f infra/

# Deploy services
kubectl apply -f user-service.yaml
kubectl apply -f order-service.yaml
kubectl apply -f services.yaml

# Apply HPA
kubectl apply -f hpa.yaml

# Check status
kubectl get all -n microservices

# View logs
kubectl logs -n microservices -l app=user-service

# Scale
kubectl scale deployment user-service -n microservices --replicas=5

# Rollback
kubectl rollout undo deployment user-service -n microservices
```

## Test

```bash
# Port forward for local testing
kubectl port-forward -n microservices svc/user-service 8080:8080

# Test health
curl http://localhost:8080/actuator/health

# Test API
curl http://localhost:8080/api/users

# Load test
kubectl run -it load-test --image=busybox -- /bin/sh
# wget -qO- http://user-service:8080/actuator/health
```

## Cleanup

```bash
kubectl delete namespace microservices
```

## Challenges

1. **Add Ingress**: Expose services via Ingress
2. **Add NetworkPolicy**: Restrict traffic between services
3. **Add Persistent Storage**: Use PVC for PostgreSQL
4. **Add Monitoring**: Deploy Prometheus/Grafana
5. **Add TLS**: Configure HTTPS

## Deliverables

- [ ] Namespace created
- [ ] ConfigMap and Secrets configured
- [ ] User and Order services deployed
- [ ] Services accessible internally
- [ ] HPA configured and working
- [ ] Health checks implemented
- [ ] Logs viewable