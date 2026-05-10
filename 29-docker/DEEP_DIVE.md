# Docker Deep Dive

## Dockerfile Best Practices

### Layer Caching Optimization

```dockerfile
# Bad: Copy everything then build
FROM openjdk:17-slim
COPY . .
RUN mvn package

# Good: Cache dependencies separately
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Multi-Stage Builds

```dockerfile
# Builder stage - compile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Test stage - run tests
FROM builder AS test-runner
COPY --from=builder /build/target/*.jar app.jar
RUN java -jar app.jar --spring.profiles.active=test

# Production stage
FROM eclipse-temurin:17-jre-alpine AS production
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Docker Compose Patterns

### Multi-Container Application

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
      - SPRING_REDIS_HOST=redis
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_started
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - backend
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=secret
    volumes:
      - db-data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user -d mydb"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - backend

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass redis_password
    volumes:
      - redis-data:/data
    networks:
      - backend

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    networks:
      - backend

volumes:
  db-data:
  redis-data:

networks:
  backend:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

## Networking Patterns

### Custom Networks

```yaml
version: '3.8'

networks:
  frontend:
    driver: bridge
    ipam:
      config:
        - subnet: 172.18.0.0/16
  backend:
    driver: bridge
    internal: true
    ipam:
      config:
        - subnet: 172.19.0.0/16

services:
  frontend:
    networks:
      - frontend
  backend:
    networks:
      - backend
  api:
    networks:
      - frontend
      - backend
```

### DNS and Service Discovery

```yaml
services:
  app:
    hostname: myapp
    domainname: example.com
    dns:
      - 8.8.8.8
      - 8.8.4.4
    extra_hosts:
      - "local.api:host-gateway"
```

## Volume Patterns

### Named Volumes

```yaml
volumes:
  mydata:
    driver: local
  
  # NFS volume
  nfs-data:
    driver: local
    driver_opts:
      type: nfs
      o: addr=nfs-server,nfsvers=4
      device: ":/path/to/data"
```

### Bind Mounts

```yaml
services:
  app:
    volumes:
      - type: bind
        source: ./config
        target: /app/config
        read_only: true
      - type: bind
        source: ./logs
        target: /app/logs
```

### tmpfs Mounts

```yaml
services:
  app:
    tmpfs:
      - /tmp:size=100m,mode=1777
```

## Security Patterns

### Security Options

```yaml
services:
  app:
    security_opt:
      - no-new-privileges:true
    cap_drop:
      - ALL
    read_only: true
    tmpfs:
      - /tmp
    user: "1000:1000"
```

### secrets (Docker Swarm)

```yaml
secrets:
  db_password:
    file: ./db_password.txt

services:
  app:
    secrets:
      - db_password
    environment:
      - SPRING_DATASOURCE_PASSWORD_FILE=/run/secrets/db_password
```

## Resource Management

### Resource Limits

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
          pids: 100
        reservations:
          cpus: '0.25'
          memory: 256M
```

## Health Checks

```yaml
services:
  app:
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health/liveness"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
```

## Optimization Patterns

### BuildKit Features

```dockerfile
# syntax=docker/dockerfile:1.4
FROM eclipse-temurin:17-jre-alpine

# Use build cache
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline

COPY --link pom.xml .
COPY --link src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests
```

### Image Optimization

```dockerfile
# Use specific version tags
FROM eclipse-temurin:17.0.9.9-jre-alpine@sha256:...

# Clean up in same layer
RUN apk add --no-cache curl && \
    rm -rf /var/cache/* /tmp/*

# Remove unnecessary files
RUN rm -rf /var/lib/apt/lists/* \
    /usr/share/man \
    /usr/share/doc
```

## Logging Patterns

```yaml
services:
  app:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
        compress: "true"
    environment:
      - LOG_LEVEL=INFO
      - LOG_FORMAT=json
```

## Best Practices

1. **Use Multi-stage Builds**: Minimize final image size
2. **Don't Run as Root**: Use specific user
3. **Use Specific Tags**: Avoid latest tag
4. **Health Checks**: Always add health checks
5. **Resource Limits**: Set memory and CPU limits
6. **Clean Up**: Remove unnecessary files
7. **Layer Ordering**: Order instructions optimally
8. **Use .dockerignore**: Exclude unnecessary files