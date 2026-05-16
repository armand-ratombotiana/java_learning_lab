# Docker & Containers - REAL WORLD PROJECT

## Project: Production-Ready Java Application Platform

Build a production-ready Docker platform with:
- Spring Boot microservices
- Database migrations
- Health monitoring
- Log aggregation
- Security hardening

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Load Balancer (ALB)                       │
│                          │                                  │
│    ┌─────────────────────┼─────────────────────┐           │
│    │                     │                     │            │
│ ┌──▼───┐          ┌──────▼──────┐      ┌──────▼──────┐     │
│ │ App 1│          │    App 2    │      │    App 3    │     │
│ └──┬───┘          └──────┬──────┘      └──────┬──────┘     │
│    │                     │                     │            │
│ ┌──▼─────────────────────▼─────────────────────▼──────┐    │
│ │              Docker Swarm Overlay Network           │    │
│ └─────────────────────────────────────────────────────┘    │
│    │                     │                     │            │
│ ┌──▼───┐          ┌──────▼──────┐      ┌──────▼──────┐     │
│ │Postgres│         │    Redis    │      │    Kafka    │     │
│ └───────┘          └────────────┘      └─────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## Implementation

### 1. Project Structure

```
my-platform/
├── apps/
│   ├── user-service/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   └── order-service/
│       ├── Dockerfile
│       └── pom.xml
├── postgres/
│   └── init.sql
├── redis/
│   └── redis.conf
├── monitoring/
│   └── prometheus.yml
├── nginx/
│   └── nginx.conf
├── docker-compose.yml
├── .env
├── .dockerignore
└── README.md
```

### 2. Production Dockerfile

```dockerfile
# apps/user-service/Dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

# Security: Non-root user
RUN addgroup -S app && adduser -S app -G app

WORKDIR /build

# Copy dependency files first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src
COPY migrations ./migrations

# Build
RUN mvn package -Pprod -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: Read-only filesystem, non-root
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy artifact
COPY --from=builder /build/target/*.jar app.jar

# Security: Set ownership
RUN chown app:app app.jar

# Add CA certificates for HTTPS
RUN apk add --no-cache ca-certificates openssl

USER app

# Security: No new privileges
USER app

EXPOSE 8080

# Health check with retries
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Resource limits
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 3. Production docker-compose.yml

```yaml
version: '3.9'

services:
  # Application Services
  user-service:
    build:
      context: ./apps/user-service
      dockerfile: Dockerfile
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - JAVA_OPTS=-Xmx384m -Xms256m
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/users
      - SPRING_REDIS_HOST=redis
      - LOGGING_LEVEL_ROOT=INFO
      - LOGGING_FORMAT_JSON=true
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 120s
    read_only: true
    tmpfs:
      - /tmp:size=100M
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - backend
    restart: on-failure

  order-service:
    build:
      context: ./apps/order-service
      dockerfile: Dockerfile
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - JAVA_OPTS=-Xmx384m -Xms256m
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/orders
      - USER_SERVICE_URL=http://user-service:8080
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    depends_on:
      postgres:
        condition: service_healthy
      kafka:
        condition: service_started
    networks:
      - backend
    restart: on-failure

  # Infrastructure
  postgres:
    image: postgres:15-alpine
    volumes:
      - pg-data:/var/lib/postgresql/data
      - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    environment:
      POSTGRES_DB: platform
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    command: >
      postgres
      -c max_connections=200
      -c shared_buffers=256MB
      -c effective_cache_size=512MB
      -c maintenance_work_mem=64MB
      -c checkpoint_completion_target=0.9
      -c wal_buffers=16MB
      -c default_statistics_target=100
      -c random_page_cost=1.1
      -c effective_io_concurrency=200
      -c max_wal_size=1GB
      -c min_wal_size=80MB
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - backend
    restart: on-failure

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD} --maxmemory 256mb --maxmemory-policy allkeys-lru
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - backend
    restart: on-failure

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_LOG_RETENTION_MS: 604800000
    networks:
      - backend
    restart: on-failure

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - backend
    restart: on-failure

  # Reverse Proxy
  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - user-service
      - order-service
    networks:
      - backend
    restart: on-failure

  # Monitoring
  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--storage.tsdb.retention.time=30d'
    networks:
      - backend

  grafana:
    image: grafana/grafana:latest
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana-data:/var/lib/grafana
    depends_on:
      - prometheus
    networks:
      - backend

volumes:
  pg-data:
  redis-data:
  prometheus-data:
  grafana-data:

networks:
  backend:
    driver: bridge
```

### 4. Environment Variables (.env)

```bash
# Database
DB_USER=appuser
DB_PASSWORD=secure_password_here

# Redis
REDIS_PASSWORD=redis_password_here

# Monitoring
GRAFANA_PASSWORD=admin_password_here

# Application
JWT_SECRET=your_jwt_secret_here
```

### 5. Prometheus Configuration

```yaml
# monitoring/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'user-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['user-service:8080']
        labels:
          service: 'user-service'

  - job_name: 'order-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['order-service:8080']
        labels:
          service: 'order-service'
```

### 6. Nginx Configuration

```nginx
# nginx/nginx.conf
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format json_log escape=json
        '{'
        '"time":"$time_iso8601",'
        '"remote_addr":"$remote_addr",'
        '"request":"$request",'
        '"status":$status,'
        '"upstream":"$upstream_addr",'
        '"response_time":$request_time'
        '}';

    access_log /var/log/nginx/access.log json_log;

    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml application/json application/javascript;

    upstream user_service {
        least_conn;
        server user-service:8080 max_fails=3 fail_timeout=30s;
    }

    upstream order_service {
        least_conn;
        server order-service:8080 max_fails=3 fail_timeout=30s;
    }

    server {
        listen 80;
        server_name _;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;

        location /api/users {
            proxy_pass http://user_service;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location /api/orders {
            proxy_pass http://order_service;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        location /actuator {
            proxy_pass http://user_service;
            proxy_http_version 1.1;
        }

        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

## Deployment

### Build and Deploy

```bash
# Build all services
docker-compose build --parallel

# Deploy stack
docker-compose up -d

# Scale services
docker-compose up -d --scale user-service=3 --scale order-service=5

# Check status
docker-compose ps

# View logs
docker-compose logs -f --tail=100 user-service

# Scale down gracefully
docker-compose up -d --scale user-service=2
```

### Monitoring

```bash
# Prometheus UI
open http://localhost:9090

# Grafana UI
open http://localhost:3000

# Container stats
docker stats

# System health
docker-compose ps
```

### Maintenance

```bash
# Backup database
docker-compose exec postgres pg_dump -U appuser platform > backup.sql

# Restore database
docker-compose exec -T postgres psql -U appuser platform < backup.sql

# Clean up old images
docker image prune -a

# Rolling update
docker-compose up -d --build user-service
```

## Testing

```bash
# Health checks
curl http://localhost/health

# API tests
curl http://localhost/api/users
curl http://localhost/api/orders

# Load testing
docker-compose up -d loadtest
```

## Security Checklist

- [ ] Non-root users in containers
- [ ] Read-only filesystem
- [ ] Resource limits
- [ ] Secrets via environment variables
- [ ] HTTPS termination at nginx
- [ ] Security headers
- [ ] No sensitive data in images
- [ ] Regular base image updates
- [ ] Vulnerability scanning
- [ ] Audit logging

## Deliverables

- [ ] Two production-ready microservices
- [ ] Security-hardened Dockerfiles
- [ ] Production docker-compose.yml
- [ ] Monitoring stack (Prometheus, Grafana)
- [ ] Nginx reverse proxy with SSL
- [ ] Database migrations
- [ ] Health checks and readiness probes
- [ ] Log aggregation
- [ ] Backup and restore scripts