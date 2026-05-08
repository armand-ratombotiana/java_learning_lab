# Spring Boot Docker - Pedagogic Guide

## Learning Path

### Phase 1: Docker Basics (Day 1)
1. **Docker Fundamentals** - Images, containers
2. **Multi-stage Builds** - Optimized images
3. **JAR Packaging** - Fat JAR for Docker
4. **Alpine Images** - Smaller base images

### Phase 2: Docker Configuration (Day 2)
1. **Dockerfile Structure** - Base image, build, CMD
2. **Layer Optimization** - Minimize layers
3. **Non-root User** - Security best practices
4. **Health Checks** - Container health

### Phase 3: Production (Day 3)
1. **Docker Compose** - Multi-container orchestration
2. **Environment Variables** - Configuration
3. **Volume Mounts** - Persistent data
4. **Networking** - Inter-container communication

## Key Concepts

### Multi-stage Build
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
COPY . /app
RUN mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

### Optimization Tips
- Use Alpine base (smaller)
- Multi-stage builds (no build tools in prod)
- Layer ordering (cache-friendly)
- Non-root user (security)

## Common Patterns

### Basic Dockerfile
1. Base image with JDK
2. Copy JAR file
3. Expose port
4. Run command

### Docker Compose
```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

## Best Practices
- Use Alpine base images
- Multi-stage builds
- Run as non-root
- Health checks
- Externalize configuration