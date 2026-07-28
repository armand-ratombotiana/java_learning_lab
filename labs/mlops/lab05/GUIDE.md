# Lab 05: Model Serving with Docker — Guide

## Step 1: Implement a Model Server

Create a lightweight HTTP server using Java's built-in `com.sun.net.httpserver` that loads a model and serves predictions via POST /predict.

## Step 2: Write a Dockerfile

Use multi-stage build:
1. **Build stage**: `eclipse-temurin:21-jdk` — compile the Java code
2. **Runtime stage**: `eclipse-temurin:21-jre` — minimal JRE for running

## Step 3: Build and Run

```bash
cd lab05
docker build -t mlops-model-server .
docker run -p 8080:8080 mlops-model-server
```

## Step 4: Test the API

```bash
curl -X POST http://localhost:8080/predict \
  -H "Content-Type: application/json" \
  -d '{"features": [5.1, 3.5, 1.4, 0.2]}'
```

## Dockerfile Anatomy

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY src/com/mlops/lab05/*.java ./com/mlops/lab05/
RUN javac com/mlops/lab05/*.java

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app .
EXPOSE 8080
CMD ["java", "com.mlops.lab05.ModelServingLab"]
```

## Best Practices
- Use slim base images (alpine-based when possible)
- Implement health check endpoints (/healthz, /readyz)
- Set JVM memory limits (-Xmx, -Xms)
- Use layered caching for faster builds
- Scan images for vulnerabilities (Trivy, Snyk)
