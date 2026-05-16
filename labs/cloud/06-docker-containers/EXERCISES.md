# Docker & Containers - EXERCISES

## Exercise 1: Basic Dockerfile

Create a Dockerfile for a simple Java application:

1. Use `eclipse-temurin:21-jdk-alpine` as base
2. Copy source files
3. Compile with Maven
4. Run tests
5. Build final image with JRE only

```bash
# Solution structure
my-app/
├── Dockerfile
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── app/
                    └── Main.java
```

## Exercise 2: Multi-Stage Build

Create a multi-stage Dockerfile for a Spring Boot app:

1. Stage 1: Build with Maven
2. Stage 2: Runtime with JRE
3. Use non-root user in runtime
4. Add health check

## Exercise 3: Docker Compose

Create a docker-compose.yml for:
- Java application (Spring Boot)
- PostgreSQL database
- Redis cache

Include:
- Health checks
- Volume mounts
- Environment variables
- Network configuration

## Exercise 4: Optimize Dockerfile

Given this inefficient Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn package
```

Optimize it to leverage layer caching.

## Exercise 5: Security Hardening

Harden a Dockerfile by:
1. Using specific image tags
2. Adding non-root user
3. Setting read-only filesystem
4. Adding resource limits
5. Removing unnecessary tools

## Exercise 6: Network Troubleshooting

Debug connectivity issues:
1. Two containers can't communicate
2. DNS resolution fails
3. Port mapping not working

## Exercise 7: Volume Management

Create and manage volumes:
1. Create named volume
2. Mount to container
3. Backup volume data
4. Restore to new container

## Exercise 8: Image Optimization

Reduce image size from 800MB to under 200MB:
1. Use Alpine base
2. Multi-stage build
3. Remove layer bloat

## Exercise 9: Health Check Implementation

Implement custom health check:
1. Add Spring Boot actuator
2. Configure health endpoint
3. Verify with curl

## Exercise 10: Log Management

Configure logging:
1. JSON log format
2. Log rotation
3. Centralized logging

---

## Solutions

### Exercise 1: Basic Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown app:app app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Exercise 2: Multi-Stage with Health

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
RUN chown app:app app.jar

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Exercise 3: Docker Compose

```yaml
version: '3.9'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/mydb
      SPRING_REDIS_HOST: redis
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-net

  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: secret
    volumes:
      - pg-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - app-net

  redis:
    image: redis:7-alpine
    networks:
      - app-net

volumes:
  pg-data:

networks:
  app-net:
    driver: bridge
```

### Exercise 4: Optimized Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy only pom.xml first to leverage cache
COPY pom.xml .
RUN mvn dependency:go-offline
# Then copy source
COPY src ./src
RUN mvn package
```

### Exercise 5: Hardened Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine@sha256:abc123...

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
COPY --chown=app:app app.jar app.jar

# Security: read-only, no root, resource limits
USER app
read_only: true
tmpfs:
  - /tmp:size=100M

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "-Xmx256m", "-Xms128m", "app.jar"]
```

### Exercise 6: Network Debugging

```bash
# List networks
docker network ls

# Inspect network
docker network inspect app-net

# Check DNS
docker exec container1 ping container2

# Test connectivity
docker exec container1 curl http://container2:8080

# View logs
docker logs container1
```

### Exercise 7: Volume Management

```bash
# Create volume
docker volume create my-data

# Use in container
docker run -v my-data:/data alpine

# Backup
docker run -v my-data:/data -v $(pwd):/backup alpine tar cvf /backup/backup.tar /data

# Restore
docker run -v my-data:/data -v $(pwd):/backup alpine tar xvf /backup/backup.tar -C /
```

### Exercise 8: Image Optimization

Before: 800MB
After: 150MB

```dockerfile
# Use Alpine JDK
FROM eclipse-temurin:21-jdk-alpine AS build

# Multi-stage
FROM eclipse-temurin:21-jre-alpine

# Copy only artifact
COPY --from=build /app/target/*.jar app.jar
```

### Exercise 9: Health Check

```properties
# application.properties
management.endpoint.health.show-details=always
management.health.db.enabled=true
management.health.redis.enabled=true
```

### Exercise 10: Log Management

```json
{
  "logging": {
    "pattern": {
      "console": "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    },
    "json": {
      "enabled": true
    }
  }
}
```