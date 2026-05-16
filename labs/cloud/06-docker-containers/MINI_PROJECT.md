# Docker & Containers - MINI PROJECT

## Project: Containerized Java Microservices

Build a containerized microservices stack with:
- Java Spring Boot application
- PostgreSQL database
- Redis cache
- Nginx reverse proxy

## Prerequisites

- Docker and Docker Compose installed
- Java 21+ and Maven installed
- Basic Spring Boot knowledge

## Architecture

```
                    ┌─────────────┐
                    │   Client    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │    Nginx    │
                    │   (Proxy)   │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
       ┌──────▼──────┐     │     ┌──────▼──────┐
       │   Gateway   │     │     │   Gateway   │
       │   Service   │     │     │   Service   │
       └──────┬──────┘     │     └─────────────┘
              │            │
    ┌─────────┴────────────┴─────────┐
    │                                 │
┌───▼───┐                    ┌───────▼───┐
│ User  │                    │   Order   │
│Service│                    │  Service  │
└───┬───┘                    └───────┬───┘
    │                               │
    │      ┌────────────┐           │
    └──────┤  PostgreSQL├───────────┘
           └────────────┘
```

## Implementation

### Step 1: Create Spring Boot Services

Create two microservices:
- `user-service`: User management (port 8081)
- `order-service`: Order management (port 8082)

### Step 2: Create Dockerfile for Each Service

```dockerfile
# user-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/user-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 3: Create docker-compose.yml

```yaml
version: '3.9'

services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - user-service
      - order-service
    networks:
      - app-net

  user-service:
    build: ./user-service
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/users
      SPRING_REDIS_HOST: redis
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app-net

  order-service:
    build: ./order-service
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
      USER_SERVICE_URL: http://user-service:8081
    depends_on:
      postgres:
        condition: service_healthy
      user-service:
        condition: service_healthy
    networks:
      - app-net

  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: secret
    volumes:
      - pg-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser"]
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

### Step 4: Create Nginx Configuration

```nginx
# nginx/nginx.conf
events {
    worker_connections 1024;
}

http {
    upstream user-service {
        server user-service:8081;
    }
    
    upstream order-service {
        server order-service:8082;
    }
    
    server {
        listen 80;
        
        location /api/users {
            proxy_pass http://user-service;
            proxy_set_header Host $host;
        }
        
        location /api/orders {
            proxy_pass http://order-service;
            proxy_set_header Host $host;
        }
    }
}
```

### Step 5: Multi-Stage Build for User Service

Create optimized Dockerfile:

```dockerfile
# user-service/Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=builder --chown=app:app /app/target/*.jar app.jar
RUN chown app:app app.jar
USER app
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Running the Project

```bash
# Build all services
cd user-service && mvn package && cd ..
cd order-service && mvn package && cd ..

# Start all services
docker-compose up -d --build

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Scale order service
docker-compose up -d --scale order-service=3

# Stop all
docker-compose down
```

## Testing

```bash
# Test user service
curl http://localhost/api/users

# Test order service
curl http://localhost/api/orders

# Health checks
docker-compose ps
```

## Challenges

1. **Add service discovery**: Use DNS names for inter-service communication
2. **Add health checks**: Implement proper health endpoints
3. **Optimize images**: Use multi-stage builds and Alpine base
4. **Add monitoring**: Integrate Prometheus metrics
5. **Add tracing**: Implement distributed logging

## Deliverables

- [ ] Two working Spring Boot services
- [ ] Dockerfiles with multi-stage builds
- [ ] Docker Compose orchestration
- [ ] Nginx reverse proxy
- [ ] PostgreSQL and Redis integration
- [ ] Health checks configured
- [ ] All services accessible via nginx