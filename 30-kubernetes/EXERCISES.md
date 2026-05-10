# Kubernetes Exercises

## Exercise 1: Basic Deployment

### Task
Create a deployment for a Java application with:
- 3 replicas
- Health checks
- Resource limits

### Solution
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
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
        image: myapp:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
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
          initialDelaySeconds: 5
          periodSeconds: 5
```

---

## Exercise 2: Service Configuration

### Task
Create a ClusterIP service and NodePort service for the deployment.

### Solution
```yaml
# clusterip-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-internal
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP

---
# nodeport-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-external
spec:
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080
    nodePort: 30080
  type: NodePort
```

---

## Exercise 3: ConfigMap and Secrets

### Task
Create ConfigMap with application config and Secret with database credentials.

### Solution
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
data:
  application.properties: |
    spring.datasource.url=jdbc:postgresql://db:5432/mydb
    spring.redis.host=redis
    logging.level=INFO

---
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: myapp-secret
type: Opaque
stringData:
  db-username: appuser
  db-password: changeme
```

---

## Exercise 4: Rolling Update

### Task
Configure rolling update with maxSurge and maxUnavailable.

### Solution
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
  # ... rest of deployment
```

---

## Exercise 5: Ingress Setup

### Task
Create an Ingress for the application with TLS.

### Solution
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  ingressClassName: nginx
  rules:
  - host: myapp.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp-service
            port:
              number: 80
  tls:
  - hosts:
    - myapp.example.com
    secretName: myapp-tls
```

---

## Exercise 6: ResourceQuota and LimitRange

### Task
Create ResourceQuota and LimitRange for a namespace.

### Solution
```yaml
# resourcequota.yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: myapp-quota
spec:
  hard:
    requests.memory: "2Gi"
    limits.memory: "4Gi"
    requests.cpu: "2"
    pods: "10"

---
# limitrange.yaml
apiVersion: v1
kind: LimitRange
metadata:
  name: myapp-limits
spec:
  limits:
  - max:
      memory: "1Gi"
      cpu: "1000m"
    min:
      memory: "128Mi"
      cpu: "100m"
    default:
      memory: "512Mi"
      cpu: "500m"
    type: Container
```

---

## Exercise 7: PodDisruptionBudget

### Task
Create a PDB to ensure minimum available replicas during updates.

### Solution
```yaml
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

---

## Exercise 8: Helm Chart

### Task
Create a basic Helm chart for the application.

### Solution
```yaml
# Chart.yaml
apiVersion: v2
name: myapp
description: My Java Application
version: 1.0.0

# values.yaml
replicaCount: 3

image:
  repository: myapp
  tag: latest

service:
  type: LoadBalancer
  port: 80

resources:
  limits:
    memory: 512Mi
    cpu: 500m
  requests:
    memory: 256Mi
    cpu: 200m

# templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Chart.Name }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      app: {{ .Chart.Name }}
  template:
    metadata:
      labels:
        app: {{ .Chart.Name }}
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        ports:
        - containerPort: 8080
        resources:
          {{- toYaml .Values.resources | nindent 10 }}
```

---

## Exercise 9: HPA Setup

### Task
Create HorizontalPodAutoscaler for the deployment.

### Solution
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: myapp-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  minReplicas: 2
  maxReplicas: 10
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

---

## Exercise 10: Security Context

### Task
Create a deployment with security context settings.

### Solution
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  template:
    spec:
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: myapp
        image: myapp:latest
        securityContext:
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop:
            - ALL
```