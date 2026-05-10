# Kubernetes Module - PROJECTS.md

---

# Mini-Project: K8s Deployment

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: K8s Deployment, Services, ConfigMaps, Helm Charts

This mini-project demonstrates Kubernetes deployment for Java applications.

---

## Project Structure

```
30-kubernetes/
├── pom.xml
├── src/main/java/com/learning/
│   └── Main.java
├── deployment.yaml
├── service.yaml
├── configmap.yaml
└── helm/
    └── chart/
        └── values.yaml
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
</project>
```

---

## Implementation

```yaml
# deployment.yaml
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
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: myapp-config
              key: profiles
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: myapp-service
spec:
  selector:
    app: myapp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: myapp-config
data:
  profiles: "production"
  database-url: "postgres://db:5432/mydb"
```

```java
// Main.java
package com.learning;

public class Main {
    public static void main(String[] args) {
        System.out.println("Kubernetes Demo Application");
    }
}
```

---

## Build Instructions

```bash
cd 30-kubernetes
mvn clean package
docker build -t myapp:latest .
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f configmap.yaml
```

---

# Real-World Project: Helm Chart

```yaml
# helm/values.yaml
replicaCount: 3

image:
  repository: myapp
  tag: latest

service:
  type: LoadBalancer
  port: 80

resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 250m
    memory: 256Mi
```

---

## Build Instructions

```bash
cd 30-kubernetes
mvn clean package -DskipTests
docker build -t myapp:latest .
helm install myapp ./helm
kubectl get pods
kubectl get services
```