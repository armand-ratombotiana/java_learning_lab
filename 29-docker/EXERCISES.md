# Docker Exercises

## Exercise 1: Multi-Stage Build

### Task
Create a multi-stage Dockerfile for a Spring Boot application that:
1. Uses Maven to build
2. Uses minimal JRE for runtime
3. Runs as non-root user
4. Includes health check

### Solution
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn dependency:go-offline -B || true
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Exercise 2: Docker Compose with Dependencies

### Task
Create a docker-compose.yml that sets up:
1. Spring Boot application
2. PostgreSQL database with health check
3. Redis cache
4. Proper dependency startup order

### Solution
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
      - SPRING_REDIS_HOST=redis
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=secret
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U user -d mydb"]
      interval: 10s
      timeout: 5s
      retries: 5
    volumes:
      - db-data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    volumes:
      - redis-data:/data

volumes:
  db-data:
  redis-data:
```

---

## Exercise 3: Resource Limits

### Task
Configure resource limits for a Java application container:
- CPU: 0.5 cores
- Memory: 512MB limit, 256MB reservation
- Disable OOM kill

### Solution
```yaml
services:
  app:
    image: myapp:latest
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M
    mem_limit: 512m
    mem_reservation: 256m
    oom_kill_disable: true
```

---

## Exercise 4: Network Isolation

### Task
Create separate networks for frontend and backend:
- Frontend can access backend
- Backend can only access database
- Database is internal

### Solution
```yaml
version: '3.8'

networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true

services:
  nginx:
    networks:
      - frontend
    ports:
      - "80:80"

  api:
    networks:
      - frontend
      - backend

  db:
    networks:
      - backend
    networks:
      - backend:
          aliases:
            - database

volumes:
  db-data:
```

---

## Exercise 5: Secrets Management

### Task
Configure Docker secrets for database password:
1. Use Docker secrets
2. Mount as file
3. Use in application

### Solution
```yaml
# secrets/prodSecrets.txt
# (Put actual password in this file)

version: '3.8'

secrets:
  db_password:
    file: ./secrets/prodSecrets.txt

services:
  app:
    secrets:
      - db_password
    environment:
      - SPRING_DATASOURCE_PASSWORD_FILE=/run/secrets/db_password
```

---

## Exercise 6: Health Check Configuration

### Task
Configure health check for a Spring Boot app:
- Check every 30 seconds
- Timeout after 10 seconds
- Start after 60 seconds
- Use actuator health endpoint

### Solution
```yaml
services:
  app:
    image: myapp:latest
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
```

---

## Exercise 7: Log Rotation

### Task
Configure logging with:
- JSON format
- Max file size 10MB
- Max 3 files
- Compress old files

### Solution
```yaml
services:
  app:
    image: myapp:latest
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
        compress: "true"
    environment:
      - LOGGING_FORMAT=json
```

---

## Exercise 8: Build with BuildKit

### Task
Use BuildKit for faster builds with caching.

### Solution
```bash
# Enable BuildKit
export DOCKER_BUILDKIT=1

# docker-compose.yml
services:
  app:
    build:
      context: .
      builder: "dockerfile"

# Dockerfile with cache mount
# syntax=docker/dockerfile:1.4
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B
COPY --link src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests
```

---

## Exercise 9: Environment Variables

### Task
Configure environment variables with:
- Default values
- Variable substitution
- Sensitive defaults

### Solution
```yaml
version: '3.8'

services:
  app:
    image: myapp:latest
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES:-production}
      - JAVA_OPTS=${JAVA_OPTS:- -Xmx512m}
      - DB_PASSWORD=${DB_PASSWORD:-change_me}
    env_file:
      - ./app.env
```

---

## Exercise 10: Volume Backup

### Task
Create a script to backup named volumes.

### Solution
```bash
#!/bin/bash
# backup.sh

BACKUP_NAME="backup-$(date +%Y%m%d-%H%M%S)"

# Create backup container
docker run --rm \
  -v myapp_db-data:/data \
  -v $(pwd):/backup \
  alpine \
  tar czf /backup/${BACKUP_NAME}.tar.gz -C /data .

echo "Backup created: ${BACKUP_NAME}.tar.gz"
```