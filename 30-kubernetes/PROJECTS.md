# Kubernetes Module - PROJECTS.md

---

# Mini-Project 1: Pods and Deployments (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Pods, Deployments, ReplicaSets, Labels, Selectors

Build and manage Kubernetes Pods and Deployments for Java applications.

---

## Project Structure

```
30-kubernetes/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── k8s/
│   ├── pod.yaml
│   ├── deployment.yaml
│   ├── replicaset.yaml
│   └── configmap.yaml
└── scripts/
    └── deploy.sh
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>k8s-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Implementation

```java
// App.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class App {
    
    @Value("${app.version:1.0.0}")
    private String version;
    
    @Value("${app.env:default}")
    private String environment;
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @GetMapping("/")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Kubernetes!");
        response.put("version", version);
        response.put("environment", environment);
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("pod", System.getenv("HOSTNAME"));
        return response;
    }
    
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("version", version);
        response.put("environment", environment);
        response.put("podName", System.getenv("HOSTNAME"));
        response.put("podIP", System.getenv("POD_IP"));
        return response;
    }
}
```

```yaml
# k8s/pod.yaml
apiVersion: v1
kind: Pod
metadata:
  name: k8s-demo-pod
  labels:
    app: k8s-demo
    version: v1
    tier: backend
spec:
  containers:
  - name: app
    image: k8s-demo:1.0.0
    imagePullPolicy: IfNotPresent
    ports:
    - containerPort: 8080
      name: http
    env:
    - name: SPRING_PROFILES_ACTIVE
      value: "kubernetes"
    - name: POD_NAME
      valueFrom:
        fieldRef:
          fieldPath: metadata.name
    - name: POD_IP
      valueFrom:
        fieldRef:
          fieldPath: status.podIP
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
      timeoutSeconds: 3
      failureThreshold: 3
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 5
      periodSeconds: 5
      timeoutSeconds: 3
      failureThreshold: 3
    volumeMounts:
    - name: app-logs
      mountPath: /var/log/app
  volumes:
  - name: app-logs
    emptyDir: {}
  restartPolicy: Always
```

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-demo-deployment
  labels:
    app: k8s-demo
    version: v1
spec:
  replicas: 3
  revisionHistoryLimit: 5
  selector:
    matchLabels:
      app: k8s-demo
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: k8s-demo
        version: v1
        tier: backend
    spec:
      serviceAccountName: k8s-demo-sa
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
      - name: app
        image: k8s-demo:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
          protocol: TCP
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        - name: JAVA_OPTS
          value: "-Xmx512m -Xms256m -XX:+UseContainerSupport"
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
          timeoutSeconds: 3
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 3
        volumeMounts:
        - name: app-logs
          mountPath: /var/log/app
      volumes:
      - name: app-logs
        emptyDir: {}
      terminationGracePeriodSeconds: 30
```

```yaml
# k8s/replicaset.yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: k8s-demo-replicaset
spec:
  replicas: 3
  selector:
    matchLabels:
      app: k8s-demo
  template:
    metadata:
      labels:
        app: k8s-demo
    spec:
      containers:
      - name: app
        image: k8s-demo:1.0.0
        ports:
        - containerPort: 8080
```

```yaml
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: k8s-demo-config
data:
  application.yml: |
    spring:
      application:
        name: k8s-demo
    server:
      port: 8080
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics
      endpoint:
        health:
          show-details: always
```

```bash
#!/bin/bash
# scripts/deploy.sh

set -e

echo "=== Deploying to Kubernetes ==="

# Build the application
echo "Building application..."
cd 30-kubernetes
mvn clean package -DskipTests

# Build Docker image
echo "Building Docker image..."
docker build -t k8s-demo:1.0.0 .

# Tag for registry
docker tag k8s-demo:1.0.0 localhost:5000/k8s-demo:1.0.0

# Push to local registry
docker push localhost:5000/k8s-demo:1.0.0

# Apply Kubernetes resources
echo "Applying Kubernetes resources..."
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml

# Wait for deployment
echo "Waiting for deployment..."
kubectl rollout status deployment/k8s-demo-deployment

# Show pods
echo "Pods:"
kubectl get pods -l app=k8s-demo

# Show services
echo "Services:"
kubectl get svc

echo "Deployment complete!"
```

---

## Build Instructions

```bash
cd 30-kubernetes
mvn clean package -DskipTests
docker build -t k8s-demo:1.0.0 .

# Deploy to Kubernetes
kubectl apply -f k8s/deployment.yaml
kubectl rollout status deployment/k8s-demo-deployment

# View pods
kubectl get pods -l app=k8s-demo
kubectl describe pod <pod-name>
```

---

# Mini-Project 2: Services and Ingress (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Services (ClusterIP, NodePort, LoadBalancer), Ingress, DNS

Expose Kubernetes applications using Services and Ingress.

---

## Project Structure

```
30-kubernetes/
├── k8s/
│   ├── service-clusterip.yaml
│   ├── service-nodeport.yaml
│   ├── service-loadbalancer.yaml
│   └── ingress.yaml
└── scripts/
    └── service-test.sh
```

---

## Implementation

```yaml
# k8s/service-clusterip.yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-demo-clusterip
  labels:
    app: k8s-demo
spec:
  type: ClusterIP
  selector:
    app: k8s-demo
  ports:
  - name: http
    protocol: TCP
    port: 80
    targetPort: 8080
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 10800
```

```yaml
# k8s/service-nodeport.yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-demo-nodeport
  labels:
    app: k8s-demo
spec:
  type: NodePort
  selector:
    app: k8s-demo
  ports:
  - name: http
    protocol: TCP
    port: 8080
    targetPort: 8080
    nodePort: 30080
  externalTrafficPolicy: Cluster
```

```yaml
# k8s/service-loadbalancer.yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-demo-lb
  labels:
    app: k8s-demo
  annotations:
    service.beta.kubernetes.io/aws-load-balancer-type: "nlb"
    service.beta.kubernetes.io/aws-load-balancer-cross-zone-load-balancing-enabled: "true"
spec:
  type: LoadBalancer
  selector:
    app: k8s-demo
  ports:
  - name: http
    protocol: TCP
    port: 80
    targetPort: 8080
  externalTrafficPolicy: Local
```

```yaml
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: k8s-demo-ingress
  labels:
    app: k8s-demo
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/ssl-redirect: "false"
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "30"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "60"
spec:
  tls:
  - hosts:
    - k8s-demo.example.com
    secretName: k8s-demo-tls
  rules:
  - host: k8s-demo.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: k8s-demo-clusterip
            port:
              number: 80
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: k8s-demo-clusterip
            port:
              number: 80
```

```yaml
# k8s/ingress-advanced.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: k8s-demo-ingress-v2
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/rewrite-target: /$2
spec:
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /v1(/|$)(.*)
        pathType: ImplementationSpecific
        backend:
          service:
            name: k8s-demo-v1
            port:
              number: 80
      - path: /v2(/|$)(.*)
        pathType: ImplementationSpecific
        backend:
          service:
            name: k8s-demo-v2
            port:
              number: 80
```

```bash
#!/bin/bash
# scripts/service-test.sh

echo "=== Testing Kubernetes Services ==="

# Get cluster IP service
echo "ClusterIP Service:"
kubectl get svc k8s-demo-clusterip -o wide

# Test within cluster
kubectl run test --image=busybox --rm -it --restart=Never -- \
    sh -c "wget -qO- http://k8s-demo-clusterip/health"

# Get NodePort service
echo "NodePort Service:"
kubectl get svc k8s-demo-nodeport

# Get LoadBalancer service
echo "LoadBalancer Service:"
kubectl get svc k8s-demo-lb -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

# Get Ingress
echo "Ingress:"
kubectl get ingress k8s-demo-ingress

# Describe service
echo "Service Details:"
kubectl describe svc k8s-demo-clusterip

echo "Test complete!"
```

---

## Build Instructions

```bash
cd 30-kubernetes

# Apply services
kubectl apply -f k8s/service-clusterip.yaml
kubectl apply -f k8s/service-nodeport.yaml
kubectl apply -f k8s/service-loadbalancer.yaml

# Apply ingress
kubectl apply -f k8s/ingress.yaml

# Test services
./scripts/service-test.sh
```

---

# Mini-Project 3: ConfigMaps and Secrets (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: ConfigMaps, Secrets, Environment Variables, Volume Mounts

Manage application configuration using ConfigMaps and Secrets.

---

## Project Structure

```
30-kubernetes/
├── k8s/
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── deployment-config.yaml
│   └── env-from-configmap.yaml
└── scripts/
    └── update-config.sh
```

---

## Implementation

```yaml
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  # Configuration properties
  app.name: "k8s-demo"
  app.environment: "production"
  app.max-connections: "100"
  app.cache-ttl: "300"
  
  # Application.yml content
  application.yml: |
    spring:
      application:
        name: ${APP_NAME}
      datasource:
        url: jdbc:postgresql://postgres:5432/${DB_NAME}
        hikari:
          maximum-pool-size: ${MAX_CONNECTIONS}
          idle-timeout: 30000
    server:
      port: 8080
      tomcat:
        max-threads: 200
    logging:
      level: INFO
```

```yaml
# k8s/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  # Database credentials
  db.username: "admin"
  db.password: "changeme"
  
  # JWT secret
  jwt.secret: "your-secret-key-here"
  
  # API keys
  api.key: "api-key-value"
  
  # TLS certificates (base64 encoded)
  # tls.crt: LS0tLS1...
  # tls.key: LS0tLS1...
```

```yaml
# k8s/deployment-config.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-demo-with-config
spec:
  replicas: 2
  selector:
    matchLabels:
      app: k8s-demo
  template:
    metadata:
      labels:
        app: k8s-demo
    spec:
      containers:
      - name: app
        image: k8s-demo:1.0.0
        env:
        - name: APP_NAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: app.name
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: db.password
        envFrom:
        - configMapRef:
            name: app-config
            optional: false
        - secretRef:
            name: app-secrets
            optional: false
        volumeMounts:
        - name: config-volume
          mountPath: /config
        - name: secrets-volume
          mountPath: /secrets
          readOnly: true
      volumes:
      - name: config-volume
        configMap:
          name: app-config
          items:
          - key: application.yml
            path: application.yml
      - name: secrets-volume
        secret:
          secretName: app-secrets
          items:
          - key: db.password
            path: db-password
            mode: 0400
```

```bash
#!/bin/bash
# scripts/update-config.sh

echo "=== Updating Configuration ==="

# Update ConfigMap
echo "Updating ConfigMap..."
kubectl apply -f k8s/configmap.yaml

# Update Secret
echo "Updating Secret..."
kubectl apply -f k8s/secret.yaml

# Rollout deployment to apply changes
echo "Rolling out deployment..."
kubectl rollout restart deployment/k8s-demo-with-config

# Wait for rollout
kubectl rollout status deployment/k8s-demo-with-config

echo "Configuration updated!"
```

---

## Build Instructions

```bash
cd 30-kubernetes

# Apply config and secrets
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment-config.yaml

# View config
kubectl get configmap app-config
kubectl describe configmap app-config

# View secrets (masked)
kubectl get secret app-secrets
kubectl describe secret app-secrets
```

---

# Mini-Project 4: Helm Charts (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Helm Charts, Templates, Values, Release Management

Package and deploy applications using Helm charts.

---

## Project Structure

```
30-kubernetes/
├── helm/
│   └── chart/
│       ├── Chart.yaml
│       ├── values.yaml
│       ├── values-prod.yaml
│       ├── values-staging.yaml
│       ├── templates/
│       │   ├── deployment.yaml
│       │   ├── service.yaml
│       │   ├── ingress.yaml
│       │   ├── configmap.yaml
│       │   └── secret.yaml
│       └── .helmignore
└── scripts/
    └── helm-deploy.sh
```

---

## Implementation

```yaml
# helm/chart/Chart.yaml
apiVersion: v2
name: k8s-demo-chart
description: A Helm chart for K8s Demo Application
type: application
version: 1.0.0
appVersion: "1.0.0"
keywords:
  - spring-boot
  - java
  - kubernetes
maintainers:
  - name: Developer
    email: dev@example.com
```

```yaml
# helm/chart/values.yaml
replicaCount: 3

image:
  repository: k8s-demo
  tag: 1.0.0
  pullPolicy: IfNotPresent

imagePullSecrets: []
nameOverride: ""
fullnameOverride: ""

serviceAccount:
  create: true
  annotations: {}
  name: ""

service:
  type: ClusterIP
  port: 80
  targetPort: 8080

ingress:
  enabled: true
  className: nginx
  annotations: {}
  hosts:
    - host: k8s-demo.example.com
      paths:
        - path: /
          pathType: Prefix
  tls: []

resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 250m
    memory: 256Mi

autoscaling:
  enabled: false
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80

nodeSelector: {}

tolerations: []

affinity: {}

livenessProbe:
  enabled: true
  path: /actuator/health/liveness
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  enabled: true
  path: /actuator/health/readiness
  initialDelaySeconds: 5
  periodSeconds: 5

configMap:
  enabled: true
  data: {}

secret:
  enabled: true
  data: {}
```

```yaml
# helm/chart/values-prod.yaml
replicaCount: 5

image:
  tag: "1.0.0-prod"

service:
  type: LoadBalancer

ingress:
  enabled: true
  tls:
    - secretName: k8s-demo-tls
      hosts:
        - k8s-demo.example.com

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 20

configMap:
  data:
    app.environment: "production"
    app.max-connections: "200"
```

```yaml
# helm/chart/templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "chart.fullname" . }}
  labels:
    {{- include "chart.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      {{- include "chart.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      annotations:
        checksum/config: {{ include (print $.Template.BasePath "/configmap.yaml") . | sha256sum }}
        checksum/secret: {{ include (print $.Template.BasePath "/secret.yaml") . | sha256sum }}
      labels:
        {{- include "chart.selectorLabels" . | nindent 8 }}
    spec:
      serviceAccountName: {{ include "chart.serviceAccountName" . }}
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        imagePullPolicy: {{ .Values.image.pullPolicy }}
        ports:
        - name: http
          containerPort: {{ .Values.service.targetPort }}
          protocol: TCP
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: {{ .Values.configMap.data.app.environment | default "default" }}
        {{- if .Values.configMap.data }}
        envFrom:
        - configMapRef:
            name: {{ include "chart.fullname" . }}-config
        {{- end }}
        livenessProbe:
          httpGet:
            path: {{ .Values.livenessProbe.path }}
            port: http
          initialDelaySeconds: {{ .Values.livenessProbe.initialDelaySeconds }}
          periodSeconds: {{ .Values.livenessProbe.periodSeconds }}
        readinessProbe:
          httpGet:
            path: {{ .Values.readinessProbe.path }}
            port: http
          initialDelaySeconds: {{ .Values.readinessProbe.initialDelaySeconds }}
          periodSeconds: {{ .Values.readinessProbe.periodSeconds }}
        resources:
          {{- toYaml .Values.resources | nindent 10 }}
      {{- with .Values.nodeSelector }}
      nodeSelector:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      {{- with .Values.affinity }}
      affinity:
        {{- toYaml . | nindent 8 }}
      {{- end }}
```

```yaml
# helm/chart/templates/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: {{ include "chart.fullname" . }}
  labels:
    {{- include "chart.labels" . | nindent 4 }}
spec:
  type: {{ .Values.service.type }}
  ports:
  - port: {{ .Values.service.port }}
    targetPort: http
    protocol: TCP
    name: http
  selector:
    {{- include "chart.selectorLabels" . | nindent 4 }}
```

```bash
#!/bin/bash
# scripts/helm-deploy.sh

set -e

RELEASE_NAME=${1:-k8s-demo}
NAMESPACE=${2:-default}

echo "=== Deploying with Helm ==="
echo "Release: $RELEASE_NAME"
echo "Namespace: $NAMESPACE"

# Create namespace if not exists
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Install chart
helm install $RELEASE_NAME ./helm/chart \
    --namespace $NAMESPACE \
    --create-namespace \
    --values ./helm/chart/values.yaml

# Wait for deployment
kubectl rollout status deployment/$RELEASE_NAME-k8s-demo-chart -n $NAMESPACE

# Show status
helm list -n $NAMESPACE
kubectl get pods -n $NAMESPACE

echo "Helm deployment complete!"
```

---

## Build Instructions

```bash
cd 30-kubernetes

# Install Helm chart
./scripts/helm-deploy.sh k8s-demo default

# Upgrade
helm upgrade k8s-demo ./helm/chart --values ./helm/chart/values-prod.yaml

# Rollback
helm rollback k8s-demo 1

# Uninstall
helm uninstall k8s-demo
```

---

# Real-World Project: K8s Cluster (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Production-grade K8s cluster, RBAC, Network Policies, Storage, Monitoring

Build a production-ready Kubernetes cluster with comprehensive infrastructure.

---

## Project Structure

```
30-kubernetes/
├── k8s/
│   ├── namespaces/
│   │   ├── dev.yaml
│   │   ├── staging.yaml
│   │   └── prod.yaml
│   ├── rbac/
│   │   ├── role.yaml
│   │   ├── rolebinding.yaml
│   │   └── serviceaccount.yaml
│   ├── network-policies/
│   │   └── default-deny.yaml
│   ├── persistent-volume.yaml
│   ├── statefulset.yaml
│   └── horizontal-pod-autoscaler.yaml
├── helm/
│   └── chart/
└── scripts/
    ├── setup-cluster.sh
    └── monitoring.sh
```

---

## Complete Implementation

```yaml
# k8s/namespaces/dev.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: dev
  labels:
    environment: dev
    name: dev
---
apiVersion: v1
kind: Namespace
metadata:
  name: staging
  labels:
    environment: staging
    name: staging
---
apiVersion: v1
kind: Namespace
metadata:
  name: production
  labels:
    environment: production
    name: production
```

```yaml
# k8s/rbac/role.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: app-role
  namespace: default
rules:
- apiGroups: [""]
  resources: ["pods", "services", "configmaps", "secrets"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: ["apps"]
  resources: ["deployments", "replicasets"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: [""]
  resources: ["pods/log"]
  verbs: ["get", "list"]
```

```yaml
# k8s/rbac/rolebinding.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: app-role-binding
  namespace: default
subjects:
- kind: ServiceAccount
  name: app-sa
  namespace: default
roleRef:
  kind: Role
  name: app-role
  apiGroup: rbac.authorization.k8s.io
```

```yaml
# k8s/network-policies/default-deny.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-app-to-db
spec:
  podSelector:
    matchLabels:
      app: database
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: backend
```

```yaml
# k8s/persistent-volume.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: app-data-pvc
  namespace: production
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: standard
  selector:
    matchLabels:
      type: data
```

```yaml
# k8s/statefulset.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: app-statefulset
  namespace: production
spec:
  serviceName: app-headless
  replicas: 3
  selector:
    matchLabels:
      app: stateful-app
  template:
    metadata:
      labels:
        app: stateful-app
    spec:
      terminationGracePeriodSeconds: 10
      containers:
      - name: app
        image: k8s-demo:1.0.0
        ports:
        - containerPort: 8080
          name: http
        volumeMounts:
        - name: app-data
          mountPath: /data
  volumeClaimTemplates:
  - metadata:
      name: app-data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 1Gi
```

```yaml
# k8s/horizontal-pod-autoscaler.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: k8s-demo-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: k8s-demo-deployment
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
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 15
```

```bash
#!/bin/bash
# scripts/setup-cluster.sh

set -e

echo "=== Setting up Kubernetes Cluster ==="

# Create namespaces
echo "Creating namespaces..."
kubectl apply -f k8s/namespaces/

# Apply RBAC
echo "Applying RBAC..."
kubectl apply -f k8s/rbac/

# Apply network policies
echo "Applying network policies..."
kubectl apply -f k8s/network-policies/default-deny.yaml

# Deploy application
echo "Deploying application..."
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service-clusterip.yaml

# Apply HPA
echo "Setting up autoscaling..."
kubectl apply -f k8s/horizontal-pod-autoscaler.yaml

# Verify deployment
echo "Verifying deployment..."
kubectl get all -n production

echo "Cluster setup complete!"
```

---

## Build Instructions

```bash
cd 30-kubernetes

# Setup cluster
./scripts/setup-cluster.sh

# Deploy with Helm
helm install k8s-demo ./helm/chart --namespace production

# View all resources
kubectl get all -A

# View HPA status
kubectl get hpa -n production
kubectl describe hpa -n production
```

---

## Kubernetes Best Practices

### Resource Management
```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```

### Health Checks
```yaml
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

### Security
```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  fsGroup: 1000

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
```