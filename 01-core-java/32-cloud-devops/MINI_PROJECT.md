# Module 32: Cloud & DevOps - Mini Project

**Project Name**: Zero-Downtime Deployment Pipeline  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Create a complete CI/CD pipeline definition and Kubernetes deployment configuration for a Java Spring Boot application, demonstrating containerization best practices, Health Checks, and rolling updates.

## 📝 Requirements

### Core Features

1. **Optimized Dockerfile**:
   - Write a Multi-Stage `Dockerfile`.
   - **Stage 1 (Build)**: Use an image with Maven and the JDK (e.g., `maven:3.9-eclipse-temurin-17`). Copy the `pom.xml` and `src`, and run `mvn clean package`.
   - **Stage 2 (Run)**: Use a minimal JRE image (e.g., `eclipse-temurin:17-jre-alpine`). Copy ONLY the compiled `.jar` file from Stage 1. Add a non-root user and run the jar.

2. **Kubernetes Deployment (`deployment.yaml`)**:
   - Define a `Deployment` for the Java app with 3 replicas.
   - Configure **Liveness Probes**: Point to `/actuator/health/liveness`. If it fails, Kubernetes should restart the pod.
   - Configure **Readiness Probes**: Point to `/actuator/health/readiness`. If it fails, Kubernetes stops routing traffic to the pod.
   - Configure **Resource Limits/Requests**: Limit CPU to 500m and Memory to 512Mi.
   - Configure a **Rolling Update Strategy**: maxSurge 1, maxUnavailable 0.

3. **Kubernetes Service (`service.yaml`)**:
   - Expose the Deployment internally via a `ClusterIP` Service on port 80, routing to the container's port 8080.

4. **CI/CD Pipeline (`.github/workflows/main.yml`)**:
   - Write a GitHub Actions YAML file that triggers on push to the `main` branch.
   - Include a step to checkout code, a step to set up JDK 17, and a step to run `mvn test`.
   - Include a step (mocked) to build and push the Docker image to a registry.

---

## 💡 Solution Blueprint

1. **Multi-Stage Dockerfile**:
   ```dockerfile
   # Stage 1: Build
   FROM maven:3.9-eclipse-temurin-17 AS builder
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package -DskipTests

   # Stage 2: Run
   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   
   # Security: Run as non-root user
   RUN addgroup -S spring && adduser -S spring -G spring
   USER spring:spring
   
   COPY --from=builder /app/target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Kubernetes Deployment**:
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: java-app-deployment
   spec:
     replicas: 3
     strategy:
       type: RollingUpdate
       rollingUpdate:
         maxSurge: 1
         maxUnavailable: 0
     selector:
       matchLabels:
         app: java-app
     template:
       metadata:
         labels:
           app: java-app
       spec:
         containers:
         - name: java-app
           image: myregistry/java-app:1.0.0
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
             initialDelaySeconds: 15
             periodSeconds: 10
           readinessProbe:
             httpGet:
               path: /actuator/health/readiness
               port: 8080
             initialDelaySeconds: 15
             periodSeconds: 5
   ```