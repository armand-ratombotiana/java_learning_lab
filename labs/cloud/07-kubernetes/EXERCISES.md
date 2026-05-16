# Kubernetes - EXERCISES

## Exercise 1: Create Your First Deployment

Create a Deployment for a Java application:
1. Image: eclipse-temurin:21-jre-alpine
2. Replicas: 3
3. Port: 8080
4. Resource limits

```bash
# Solution
kubectl create deployment java-app --image=eclipse-temurin:21-jre-alpine --replicas=3 --port=8080
```

## Exercise 2: Expose Deployment as Service

Create a LoadBalancer service:
1. Type: LoadBalancer
2. Port mapping: 80 -> 8080

```bash
# Solution
kubectl expose deployment java-app --type=LoadBalancer --port=80 --target-port=8080
```

## Exercise 3: Scale Application

Scale deployment to 5 replicas, then scale down to 2.

```bash
# Solution
kubectl scale deployment java-app --replicas=5
kubectl scale deployment java-app --replicas=2
```

## Exercise 4: Rolling Update

Update image version and observe rolling update.

```bash
# Solution
kubectl set image deployment/java-app app=eclipse-temurin:21-jre-alpine:v2
kubectl rollout status deployment java-app
```

## Exercise 5: Rollback

Rollback to previous version after a failed deployment.

```bash
# Solution
kubectl rollout undo deployment java-app
kubectl rollout history deployment java-app
```

## Exercise 6: Create ConfigMap

Create ConfigMap with database configuration and inject into pod.

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  DB_HOST: "postgres"
  DB_PORT: "5432"
  CACHE_SIZE: "1000"
```

## Exercise 7: Create Secret

Create Secret for database password and use in deployment.

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
stringData:
  DB_PASSWORD: supersecret123
```

## Exercise 8: Health Probes

Add liveness and readiness probes to a Spring Boot app.

```yaml
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

## Exercise 9: Horizontal Pod Autoscaler

Create HPA to scale based on CPU utilization at 70%.

```bash
# Solution
kubectl autoscale deployment java-app --cpu-percent=70 --min=2 --max=10
```

## Exercise 10: Create Ingress

Create Ingress rule for the application with TLS.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
spec:
  tls:
  - hosts:
    - app.example.com
    secretName: app-tls
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: java-app
            port:
              number: 80
```

---

## Solutions

### Exercise 1: Deployment YAML

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: java-app
  template:
    metadata:
      labels:
        app: java-app
    spec:
      containers:
      - name: app
        image: eclipse-temurin:21-jre-alpine
        ports:
        - containerPort: 8080
        resources:
          limits:
            memory: "512Mi"
            cpu: "500m"
```

### Exercise 2: Service YAML

```yaml
apiVersion: v1
kind: Service
metadata:
  name: java-app
spec:
  type: LoadBalancer
  selector:
    app: java-app
  ports:
  - port: 80
    targetPort: 8080
```

### Exercise 3: Scaling

```bash
# Commands
kubectl scale deployment java-app --replicas=5
kubectl get deployment java-app
kubectl scale deployment java-app --replicas=2
```

### Exercise 4: Rolling Update

```bash
# Update image
kubectl set image deployment/java-app app=my-registry/app:v2

# Monitor rollout
kubectl rollout status deployment java-app

# View history
kubectl rollout history deployment java-app
```

### Exercise 5: Rollback

```bash
# Rollback to previous
kubectl rollout undo deployment java-app

# Rollback to specific revision
kubectl rollout undo deployment java-app --to-revision=2

# View history with details
kubectl rollout history deployment java-app --revision=2
```

### Exercise 6: ConfigMap

```bash
# Create from literal
kubectl create configmap app-config --from-literal=DB_HOST=postgres --from-literal=DB_PORT=5432

# Create from file
kubectl create configmap app-config --from-file=config.properties

# Use in deployment
kubectl set env deployment/java-app --from=configmap/app-config
```

### Exercise 7: Secret & Usage

```bash
# Create secret
kubectl create secret generic db-credentials --from-literal=DB_PASSWORD=supersecret123

# Use in deployment
kubectl patch deployment java-app -p '{"spec":{"template":{"spec":{"containers":[{"name":"app","env":[{"name":"DB_PASSWORD","valueFrom":{"secretKeyRef":{"name":"db-credentials","key":"DB_PASSWORD"}}}]}]}}}}'
```

### Exercise 8: Full Deployment with Probes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-app
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: app
        image: my-registry/spring-boot:1.0
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 10
          failureThreshold: 30
```

### Exercise 9: HPA YAML

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: java-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: java-app
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

### Exercise 10: Complete Ingress

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - app.example.com
    secretName: app-tls
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: java-app
            port:
              number: 80
```