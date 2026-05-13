# Docker Module - PROJECTS.md

---

# Mini-Project 1: Dockerfile (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Dockerfile, Base Images, Layers, Build Context

Build optimized Dockerfiles for a Java Spring Boot application.

---

## Project Structure

```
29-docker/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── Dockerfile
├── Dockerfile.optimized
├── Dockerfile.multistage
└── .dockerignore
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>docker-demo</artifactId>
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    @GetMapping("/")
    public String hello() {
        return "Hello from Docker!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    
    @GetMapping("/info")
    public Info info() {
        return new Info("docker-demo", "1.0.0", "Running in container");
    }
    
    record Info(String name, String version, String status) {}
}
```

```dockerfile
# Dockerfile (Basic)
FROM openjdk:17-slim

WORKDIR /app

COPY target/docker-demo-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# Dockerfile.optimized (Production-ready)
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="developer@example.com"
LABEL version="1.0"
LABEL description="Spring Boot Docker Demo"

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Set timezone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone && \
    apk del tzdata

# Copy jar
COPY target/docker-demo-1.0-SNAPSHOT.jar app.jar

# Create heap dump directory
RUN mkdir -p /tmp/dumps && \
    chown -R appuser:appgroup /tmp/dumps

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Expose port
EXPOSE 8080

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

```dockerfile
# .dockerignore
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# IDE files
.idea/
*.iml
.vscode/
*.swp
*.swo
*~

# OS files
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Maven
.mvn/
mvnw
mvnw.cmd
```

---

## Build Instructions

```bash
cd 29-docker

# Build the application first
mvn clean package -DskipTests

# Build Docker image (basic)
docker build -t docker-demo:basic .

# Build optimized image
docker build -f Dockerfile.optimized -t docker-demo:optimized .

# Run the container
docker run -p 8080:8080 docker-demo:optimized

# Check health
curl http://localhost:8080/health
```

---

# Mini-Project 2: Docker Compose (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Docker Compose, Multi-container, Networking, Volume Mounts

Build a complete development environment with Docker Compose.

---

## Project Structure

```
29-docker/
├── docker-compose.yml
├── docker-compose.override.yml
├── docker-compose.prod.yml
├── src/main/java/com/learning/
│   └── App.java
├── init-scripts/
│   └── init.sql
└── nginx/
    └── nginx.conf
```

---

## Implementation

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile.optimized
    container_name: spring-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydb
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_REDIS_HOST=redis
      - SPRING_REDIS_PORT=6379
      - JAVA_OPTS=-Xmx512m -Xms256m
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    volumes:
      - app-logs:/app/logs

  postgres:
    image: postgres:15-alpine
    container_name: postgres-db
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d mydb"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: redis-cache
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
    volumes:
      - redis-data:/data
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  adminer:
    image: adminer:4.8.1
    container_name: adminer
    ports:
      - "8081:8080"
    depends_on:
      - postgres
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    container_name: nginx-proxy
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - nginx-logs:/var/log/nginx
    depends_on:
      - app
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
    name: app-network

volumes:
  postgres-data:
    driver: local
  redis-data:
    driver: local
  app-logs:
    driver: local
  nginx-logs:
    driver: local
```

```yaml
# docker-compose.override.yml (Development overrides)
version: '3.8'

services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DEBUG=true
    volumes:
      - ./src:/app/src
    command: ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
    
  postgres:
    ports:
      - "5432:5432"
    
  redis:
    ports:
      - "6379:6379"
```

```yaml
# docker-compose.prod.yml (Production)
version: '3.8'

services:
  app:
    restart: always
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
    
  postgres:
    restart: always
    volumes:
      - postgres-prod-data:/var/lib/postgresql/data
    
  nginx:
    volumes:
      - /etc/letsencrypt:/etc/letsencrypt:ro
```

```sql
-- init-scripts/init.sql
-- Initialize database schema

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (username, email) VALUES 
    ('admin', 'admin@example.com'),
    ('user1', 'user1@example.com');

INSERT INTO products (name, price, stock) VALUES 
    ('Laptop', 999.99, 10),
    ('Mouse', 29.99, 50),
    ('Keyboard', 79.99, 30);
```

```nginx
# nginx/nginx.conf
events {
    worker_connections 1024;
}

http {
    upstream app {
        server app:8080;
    }
    
    server {
        listen 80;
        server_name localhost;
        
        location / {
            proxy_pass http://app;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        location /health {
            access_log off;
            proxy_pass http://app;
        }
        
        client_max_body_size 10M;
    }
}
```

---

## Build Instructions

```bash
cd 29-docker

# Start development environment
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Start with override (development)
docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d

# Production deployment
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

# Mini-Project 3: Multi-stage Build (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Multi-stage Builds, Build Arguments, Build Cache Optimization

Build optimized production images using multi-stage Docker builds.

---

## Project Structure

```
29-docker/
├── pom.xml
├── src/main/java/com/learning/
│   └── App.java
├── Dockerfile.multistage
├── Dockerfile.jib
├── Dockerfile.buildpack
└── build-config.toml
```

---

## Implementation

```dockerfile
# Dockerfile.multistage
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy only pom files first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="developer@example.com"
LABEL version="1.0.0"

WORKDIR /app

# Install required packages
RUN apk add --no-cache \
    curl \
    && rm -rf /var/cache/apk/*

# Copy artifacts from builder
COPY --from=builder /build/target/*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Set ownership
RUN chown -R appuser:appgroup /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# JVM options for container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

```dockerfile
# Dockerfile.jib - Using Google Jib (no Docker daemon required)
FROM eclipse-temurin:17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# Dockerfile.buildpack - Using Cloud Native Buildpacks
FROM paketobuildpacks/builder:tiny

WORKDIR /workspace

COPY . .

RUN /workspace/mvnw spring-boot:build-image \
    -DskipTests \
    -Dspring-boot.build-image.imageName=myapp:1.0.0
```

```bash
# build-config.toml (for buildpack configuration)
[[buildpacks]]
  uri = "docker://paketobuildpacks/java"

[[buildpacks]]
  uri = "docker://paketobuildpacks/spring-boot"

[build]
  builder = "docker://paketobuildpacks/builder:tiny"
  build-image = "paketobuildpacks/run:tiny"

[[processes]]
  type = "web"
  command = "java -jar target/*.jar"
```

---

## Build Instructions

```bash
cd 29-docker

# Build multi-stage image
docker build -f Dockerfile.multistage -t docker-demo:multistage .

# Build with build arguments
docker build \
  --build-arg VERSION=1.0.0 \
  --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
  -t docker-demo:1.0.0 .

# Use Jib (no Docker daemon required)
mvn jib:build -Djib.to.image=registry.example.com/app:1.0.0

# Use buildpack
pack build docker-demo:pack --builder paketobuildpacks/builder:tiny

# View image layers
docker history docker-demo:multistage
```

---

# Mini-Project 4: Networking (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Docker Networks, DNS Resolution, Service Discovery, Port Mapping

Build a microservices architecture with Docker networking.

---

## Project Structure

```
29-docker/
├── docker-compose.yml
├── nginx/
│   └── nginx.conf
├── src/
│   ├── user-service/
│   └── order-service/
└── networks/
    └── docker-network.sh
```

---

## Implementation

```yaml
# docker-compose.yml - Microservices Architecture
version: '3.8'

services:
  # User Service
  user-service:
    build: ./user-service
    container_name: user-service
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SERVER_PORT=8081
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/users
      - EUREKA_INSTANCE_HOSTNAME=user-service
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - frontend
      - backend
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Order Service
  order-service:
    build: ./order-service
    container_name: order-service
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SERVER_PORT=8082
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/orders
      - USER_SERVICE_URL=http://user-service:8081
    depends_on:
      postgres:
        condition: service_healthy
      user-service:
        condition: service_started
    networks:
      - frontend
      - backend
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8082/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # API Gateway (Nginx)
  api-gateway:
    image: nginx:alpine
    container_name: api-gateway
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/gateway.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - user-service
      - order-service
    networks:
      - frontend
    healthcheck:
      test: ["CMD", "nginx", "-t"]
      interval: 30s
      timeout: 10s
      retries: 3

  # PostgreSQL
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_MULTIPLE_DATABASES=users,orders
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - backend
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s

  # Redis (Session/Cache)
  redis:
    image: redis:7-alpine
    networks:
      - backend
    volumes:
      - redis-data:/data

networks:
  frontend:
    driver: bridge
    name: frontend-network
    ipam:
      driver: default
      config:
        - subnet: 172.28.0.0/16
  backend:
    driver: bridge
    name: backend-network
    ipam:
      driver: default
      config:
        - subnet: 172.29.0.0/16

volumes:
  postgres-data:
  redis-data:
```

```nginx
# nginx/gateway.conf
upstream user_service {
    server user-service:8081;
}

upstream order_service {
    server order-service:8082;
}

server {
    listen 80;
    server_name localhost;
    
    location /api/users {
        proxy_pass http://user_service;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    location /api/orders {
        proxy_pass http://order_service;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-User-Service-Host http://user_service:8081;
    }
    
    location /health {
        access_log off;
        return 200 "Gateway OK";
    }
}
```

```bash
#!/bin/bash
# networks/docker-network.sh

set -e

echo "=== Docker Networking Demo ==="

# Create custom networks
echo "Creating networks..."
docker network create --driver bridge --subnet 172.28.0.0/16 frontend-network
docker network create --driver bridge --subnet 172.29.0.0/16 backend-network

# List networks
echo "Available networks:"
docker network ls

# Inspect network
echo "Inspecting frontend-network:"
docker network inspect frontend-network

# Run containers on specific networks
echo "Running container on networks..."
docker run -d --name test-app \
    --network frontend-network \
    nginx:alpine

docker run -d --name test-db \
    --network backend-network \
    postgres:15-alpine

# Connect containers to multiple networks
echo "Connecting to multiple networks..."
docker network connect backend-network test-app

# Check DNS resolution
echo "Testing DNS resolution..."
docker exec test-app ping -c 1 test-db

# Show network statistics
echo "Network statistics:"
docker network inspect frontend-network --format='{{range .Containers}}{{.Name}}: {{.IPv4Address}}{{"\n"}}{{end}}'

# Cleanup
echo "Cleaning up..."
docker stop test-app test-db
docker rm test-app test-db
docker network rm frontend-network backend-network

echo "Done!"
```

---

## Build Instructions

```bash
cd 29-docker

# Start the microservices
docker-compose up -d

# Check network configuration
docker network ls
docker network inspect docker_frontend-network

# Test service communication
docker exec -it user-service curl http://order-service:8082/actuator/health

# View logs
docker-compose logs -f

# Scale services
docker-compose up -d --scale order-service=3
```

---

# Real-World Project: Microservices Deployment (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Service Mesh, Load Balancing, Health Checks, Auto-scaling, Monitoring

Build a production-ready microservices deployment with comprehensive infrastructure.

---

## Project Structure

```
29-docker/
├── docker-compose.yml
├── docker-compose.prod.yml
├── docker-compose.monitoring.yml
├── docker-compose.dev.yml
├── .env
├── Makefile
├── src/
│   ├── user-service/
│   ├── order-service/
│   ├── payment-service/
│   └── gateway-service/
├── k8s/
│   └── deployment.yaml
├── monitoring/
│   ├── prometheus.yml
│   └── grafana-dashboard.json
└── scripts/
    ├── deploy.sh
    ├── scale.sh
    └── health-check.sh
```

---

## Complete Implementation

```yaml
# docker-compose.yml - Complete Microservices Stack
version: '3.8'

services:
  # User Management Service
  user-service:
    build:
      context: ./src/user-service
      dockerfile: Dockerfile
    image: user-service:${VERSION:-latest}
    container_name: user-service
    environment:
      - SPRING_PROFILES_ACTIVE=${PROFILE:-dev}
      - SERVER_PORT=8081
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/users
      - JWT_SECRET=${JWT_SECRET:-secret}
      - REDIS_HOST=redis
    env_file:
      - .env
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-network
    deploy:
      replicas: 2
      resources:
        limits:
          memory: 512M
        reservations:
          memory: 256M
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 20s
      timeout: 5s
      retries: 3
      start_period: 30s

  # Order Service
  order-service:
    build:
      context: ./src/order-service
      dockerfile: Dockerfile
    image: order-service:${VERSION:-latest}
    container_name: order-service
    environment:
      - SPRING_PROFILES_ACTIVE=${PROFILE:-dev}
      - SERVER_PORT=8082
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/orders
      - USER_SERVICE_URL=http://user-service:8081
      - PAYMENT_SERVICE_URL=http://payment-service:8083
    depends_on:
      postgres:
        condition: service_healthy
      user-service:
        condition: service_started
    networks:
      - app-network
    deploy:
      replicas: 2
      resources:
        limits:
          memory: 512M

  # Payment Service
  payment-service:
    build:
      context: ./src/payment-service
      dockerfile: Dockerfile
    image: payment-service:${VERSION:-latest}
    container_name: payment-service
    environment:
      - SPRING_PROFILES_ACTIVE=${PROFILE:-dev}
      - SERVER_PORT=8083
    networks:
      - app-network
    secrets:
      - payment-api-key

  # API Gateway
  gateway-service:
    build:
      context: ./src/gateway-service
      dockerfile: Dockerfile
    image: gateway-service:${VERSION:-latest}
    container_name: gateway-service
    ports:
      - "${GATEWAY_PORT:-80}:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=${PROFILE:-dev}
      - USER_SERVICE_URL=http://user-service:8081
      - ORDER_SERVICE_URL=http://order-service:8082
      - PAYMENT_SERVICE_URL=http://payment-service:8083
    depends_on:
      - user-service
      - order-service
      - payment-service
    networks:
      - app-network
    deploy:
      replicas: 2

  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=${DB_NAME:-appdb}
      - POSTGRES_USER=${DB_USER:-postgres}
      - POSTGRES_PASSWORD=${DB_PASSWORD:-postgres}
      - POSTGRES_MAX_CONNECTIONS=100
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-postgres}"]
      interval: 10s
      timeout: 5s

  # Redis Cache
  redis:
    image: redis:7-alpine
    command: >
      redis-server
      --appendonly yes
      --maxmemory 256mb
      --maxmemory-policy allkeys-lru
      --timeout 60
    volumes:
      - redis-data:/data
    networks:
      - app-network

  # Message Queue
  rabbitmq:
    image: rabbitmq:3-management-alpine
    environment:
      - RABBITMQ_DEFAULT_USER=admin
      - RABBITMQ_DEFAULT_PASS=admin
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq
    networks:
      - app-network

  # Monitoring - Prometheus
  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    ports:
      - "9090:9090"
    networks:
      - app-network

  # Monitoring - Grafana
  grafana:
    image: grafana/grafana:latest
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    volumes:
      - grafana-data:/var/lib/grafana
      - ./monitoring/dashboards:/etc/grafana/provisioning/dashboards:ro
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
    name: app-network

volumes:
  postgres-data:
  redis-data:
  rabbitmq-data:
  prometheus-data:
  grafana-data:

secrets:
  payment-api-key:
    file: ./secrets/payment-api-key.txt
```

```bash
#!/bin/bash
# scripts/deploy.sh

set -e

ENVIRONMENT=${1:-dev}
VERSION=${2:-latest}

echo "=== Deploying Microservices ==="
echo "Environment: $ENVIRONMENT"
echo "Version: $VERSION"

# Set environment variables
export PROFILE=$ENVIRONMENT
export VERSION=$VERSION
export COMPOSE_PROJECT_NAME=microservices

# Pull latest images
echo "Pulling latest images..."
docker-compose pull

# Build custom images
echo "Building custom images..."
docker-compose build

# Start infrastructure services first
echo "Starting infrastructure..."
docker-compose up -d postgres redis rabbitmq

# Wait for infrastructure
echo "Waiting for infrastructure..."
sleep 10

# Start application services
echo "Starting application services..."
docker-compose up -d

# Run health checks
echo "Running health checks..."
./scripts/health-check.sh

# Show status
echo "Deployment complete!"
docker-compose ps
```

```bash
#!/bin/bash
# scripts/health-check.sh

SERVICES=("user-service:8081" "order-service:8082" "payment-service:8083" "gateway-service:8080")

for service in "${SERVICES[@]}"; do
    IFS=':' read -r name port <<< "$service"
    echo "Checking $name..."
    
    for i in {1..10}; do
        if curl -sf "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo "✓ $name is healthy"
            break
        fi
        echo "  Attempt $i/10..."
        sleep 2
    done
done

echo "Health check complete!"
```

```yaml
# Makefile
.PHONY: help build up down logs scale clean

help:
	@echo "Available commands:"
	@echo "  make build     - Build all Docker images"
	@echo "  make up        - Start all services"
	@echo "  make down      - Stop all services"
	@echo "  make logs      - View logs"
	@echo "  make scale N   - Scale services to N instances"
	@echo "  make clean     - Clean up volumes"

build:
	docker-compose build

up:
	docker-compose up -d

down:
	docker-compose down

logs:
	docker-compose logs -f

scale:
	docker-compose up -d --scale user-service=$(N) --scale order-service=$(N)

clean:
	docker-compose down -v
	docker system prune -f
```

---

## Build Instructions

```bash
cd 29-docker

# Development environment
make build
make up

# Production environment
PROFILE=prod VERSION=1.0.0 docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# View services
make logs

# Scale services
make scale N=3

# Health check
./scripts/health-check.sh
```

---

## Best Practices

### Dockerfile Optimization
```dockerfile
# Use specific versions
FROM eclipse-temurin:17.0.8-jre-alpine

# Combine RUN commands
RUN apk update && \
    apk add --no-cache curl && \
    rm -rf /var/cache/apk/*

# Use .dockerignore
# Copy only necessary files
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
```

### Docker Compose Best Practices
```yaml
# Use healthchecks
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost/health"]
  interval: 30s
  timeout: 10s
  retries: 3

# Resource limits
deploy:
  resources:
    limits:
      memory: 512M
      cpus: '0.5'
```

### Security Best Practices
```dockerfile
# Never run as root
RUN addgroup -g 1000 appuser
USER appuser

# Use secrets for sensitive data
secrets:
  - db_password
```