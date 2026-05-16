# Docker & Containers - THEORY

## Overview

Docker is a platform for developing, shipping, and running applications in containers. Containers package up code, runtime, system tools, libraries, and settings - everything needed to run.

## 1. Container Fundamentals

### What is a Container?
- Lightweight, standalone executable package
- Includes everything needed to run software
- Isolated from other containers and host
- Shares host OS kernel (vs VMs that include OS)

### Container vs VM
```
┌─────────────────┐     ┌─────────────────┐
│   Container     │     │   Virtual Machine │
├─────────────────┤     ├─────────────────┤
│   App           │     │   App           │
│   Libraries     │     │   Libraries     │
│   Dependencies  │     │   Dependencies  │
│   Guest OS      │     │   Guest OS      │
├─────────────────┤     ├─────────────────┤
│   Docker Engine │     │   Hypervisor    │
│   Host OS       │     │   Host OS       │
└─────────────────┘     └─────────────────┘
```

### Key Benefits
- **Consistency**: Same environment dev/prod
- **Efficiency**: Lower overhead than VMs
- **Speed**: Start in seconds
- **Portability**: Run anywhere with Docker

## 2. Docker Architecture

### Components
```
┌────────────────────────────────────────────┐
│              Docker Client                  │
└─────────────────┬──────────────────────────┘
                  │
                  ▼
┌────────────────────────────────────────────┐
│           Docker Daemon (dockerd)          │
│  ┌──────────┐ ┌──────────┐ ┌────────────┐ │
│  │ Containers│ │ Images   │ │ Networks   │ │
│  └──────────┘ └──────────┘ └────────────┘ │
└────────────────────────────────────────────┘
                  │
                  ▼
┌────────────────────────────────────────────┐
│            Container Registry              │
│            (Docker Hub, ECR, etc)          │
└────────────────────────────────────────────┘
```

### Key Concepts

#### Images
- Read-only templates for containers
- Layered filesystem
- Defined by Dockerfile
- Tagged with versions

#### Containers
- Running instances of images
- Writable layer on top of image
- Ephemeral by default
- Can be committed to new images

#### Registries
- Storage for Docker images
- Public: Docker Hub
- Private: AWS ECR, Azure ACR, GCR

## 3. Dockerfile Deep Dive

### Basic Instructions

```dockerfile
# Base image
FROM eclipse-temurin:21-jdk-alpine

# Metadata
LABEL maintainer="dev@example.com"
LABEL version="1.0"

# Set working directory
WORKDIR /app

# Copy files
COPY pom.xml .
COPY src ./src

# Install dependencies
RUN ./mvnw dependency:go-offline

# Build application
RUN ./mvnw package -DskipTests

# Environment variables
ENV JAVA_OPTS="-Xmx512m"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "target/app.jar"]
```

### Multi-Stage Builds

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/app.jar app.jar

# Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Best Practices
1. Use specific version tags, not `latest`
2. Order instructions by change frequency
3. Use `.dockerignore` to exclude files
4. Minimize number of layers
5. Use multi-stage builds for smaller images
6. Run as non-root user
7. Use health checks

## 4. Docker Networking

### Network Drivers

#### Bridge (Default)
```yaml
networks:
  - driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

#### Host
- Removes network isolation
- Container uses host networking
- Best for performance

#### Overlay
- Multi-host Docker (Swarm)
- Encrypted by default
- Service discovery

#### None
- No networking
- Completely isolated

### DNS Resolution
- Container names resolve automatically
- User-defined networks enable DNS
- `dns-search` for domain suffix

## 5. Docker Volumes

### Volume Types

#### Named Volumes
```yaml
volumes:
  - mysql-data:/var/lib/mysql
```

#### Bind Mounts
```yaml
volumes:
  - ./config:/etc/app/config:ro
```

#### tmpfs (Memory)
```yaml
volumes:
  - type: tmpfs
    target: /app/sensitive
```

### Volume Drivers
- local (default)
- aws-ebs (EBS)
- azure-file
- gce Persistent Disk

## 6. Docker Compose

### Basic Compose File

```yaml
version: '3.9'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - backend

  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - backend

volumes:
  postgres-data:

networks:
  backend:
    driver: bridge
```

### Scaling

```bash
# Scale service
docker-compose up -d --scale app=3

# Or in compose
deploy:
  replicas: 3
```

## 7. Common Commands

### Building Images
```bash
docker build -t myapp:1.0 .
docker build --build-arg VERSION=1.0 -t myapp:1.0 .
docker build -f Dockerfile.dev -t myapp:dev .
```

### Running Containers
```bash
# Interactive
docker run -it ubuntu bash

# Detached with port mapping
docker run -d -p 8080:8080 --name myapp myapp:1.0

# With environment
docker run -d -e NODE_ENV=production --env-file .env myapp:1.0

# With volume
docker run -d -v ./data:/app/data myapp:1.0
```

### Managing
```bash
# List containers
docker ps -a

# Logs
docker logs -f myapp
docker logs --tail 100 myapp

# Execute command
docker exec -it myapp bash

# Inspect
docker inspect myapp

# Copy files
docker cp config.txt myapp:/app/
```

## 8. Security Best Practices

1. **Minimize Attack Surface**
   - Use Alpine-based images
   - Remove unnecessary tools
   - Scan for vulnerabilities

2. **Don't Run as Root**
   ```dockerfile
   RUN addgroup -S appgroup && adduser -S appuser -G appgroup
   USER appuser
   ```

3. **Use Secrets**
   ```yaml
   secrets:
     db_password:
       file: ./secrets/db_password.txt
   ```

4. **Read-Only Filesystems**
   ```yaml
   read_only: true
   tmpfs:
     - /tmp
   ```

5. **Resource Limits**
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '0.5'
         memory: 512M
   ```

## 9. Debugging

### Common Issues

#### Container Won't Start
```bash
# Check logs
docker logs myapp

# Check exit code
docker wait myapp

# Run with verbose
docker run --rm -it myapp:latest
```

#### Networking Issues
```bash
# Check DNS
docker exec myapp nslookup postgres

# Test connectivity
docker exec myapp curl http://postgres:5432

# Inspect networks
docker network inspect bridge
```

#### Disk Space
```bash
# Check usage
docker system df

# Clean up
docker system prune -a
docker volume prune
```

## Summary

Docker provides a consistent, portable way to package and run applications. Key takeaways:

1. **Images** are templates; **Containers** are running instances
2. Use **multi-stage builds** for smaller, secure images
3. **Docker Compose** for local multi-container apps
4. **Volumes** for persistent data
5. **Networks** for inter-container communication
6. Always follow **security best practices**