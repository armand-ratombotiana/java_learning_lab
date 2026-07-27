# Mock Interview — Docker & Containers

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Hands-on
- **Difficulty**: Associate Level

## Warm-Up (5 min)

Q1: What is the difference between a Docker image and a Docker container?

Q2: Explain the Dockerfile layers and how layer caching works during builds.

## Technical Questions (20 min)

### Question 1: Dockerfile Optimization (10 min)
You have the following Dockerfile for a Java Spring Boot application. Identify at least 5 issues and optimize it.

```dockerfile
FROM openjdk:11
COPY . /app
WORKDIR /app
RUN ./mvnw package
EXPOSE 8080
CMD ["java", "-jar", "target/app.jar"]
```

### Question 2: Docker Compose (10 min)
Design a docker-compose.yml for a Java microservice application that includes:
- 1 Spring Boot API service (connects to PostgreSQL)
- 1 PostgreSQL database
- 1 Redis cache
- Health checks, volume mounts, networks, environment variables

## Behavioral Question (10 min)

**Question**: Tell me about a time you containerized an existing application. What challenges did you face with dependencies, networking, or data persistence?

## System Design Whiteboard (10 min)

**Problem**: Design a container image build and deployment pipeline for a Java application. Requirements:
- Multi-stage Docker build for minimal image size
- Image security scanning
- 12-factor app configuration
- Zero-downtime deployment (rolling update)
- Registry with tag management (semantic versioning)

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| Dockerfile | Multi-stage, layer order, .dockerignore, slim base | Basic Dockerfile | Single-stage, fat image |
| Compose | Networks, volumes, health checks, depends_on | Basic services | Single flat file |
| Security | Non-root user, image scanning, secrets | Basic security | Root user, leaks secrets |
| Deployment | Rolling, health checks, graceful shutdown | Basic start/stop | No HA consideration |

## Sample Solution Outline

### Optimized Dockerfile
```dockerfile
FROM maven:3.8-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
CMD ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
      - SPRING_REDIS_HOST=cache
    depends_on:
      db:
        condition: service_healthy
      cache:
        condition: service_started
    networks:
      - backend
    deploy:
      replicas: 2
      update_config:
        order: start-first
  db:
    image: postgres:15-alpine
    volumes:
      - pgdata:/var/lib/postgresql/data
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_PASSWORD_FILE=/run/secrets/db_password
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
    networks:
      - backend
    secrets:
      - db_password
  cache:
    image: redis:7-alpine
    networks:
      - backend
volumes:
  pgdata:
networks:
  backend:
secrets:
  db_password:
    file: ./secrets/db_password.txt
```
