# Module 32: Cloud & DevOps - Edge Cases & Pitfalls

---

## Pitfall 1: Fat Base Images

### ❌ Wrong
Using full JDK images (e.g., `ubuntu` or standard `openjdk`) for production Docker images. This creates massive attack surfaces, slows down CI/CD pipelines, and wastes storage.
```dockerfile
FROM ubuntu:latest
RUN apt-get update && apt-get install -y openjdk-17-jdk
# ❌ Resulting image is 500MB+!
```

### ✅ Correct
Use Alpine Linux or distroless JRE images for the final deployment artifact. Only use the JDK for multi-stage builds.
```dockerfile
FROM eclipse-temurin:17-jre-alpine
# ✅ Resulting image is ~150MB
```

---

## Pitfall 2: Environment-Specific Configurations in Images

### ❌ Wrong
Baking database URLs or API keys directly into the Docker image or source code.
```dockerfile
ENV DB_URL="jdbc:mysql://prod-db:3306/prod"
```

### ✅ Correct
Design images to be environment-agnostic (run anywhere). Pass environment-specific variables at runtime using Kubernetes ConfigMaps, Secrets, or Docker run arguments.

---

## Pitfall 3: Ignoring Application Graceful Shutdown

### ❌ Wrong
Allowing containers to be killed abruptly. When Kubernetes scales down or updates a deployment, it sends a `SIGTERM` signal. If the app doesn't handle it, ongoing transactions will fail.

### ✅ Correct
Configure Spring Boot for graceful shutdown so it stops accepting new requests but finishes processing active ones before terminating.
```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```