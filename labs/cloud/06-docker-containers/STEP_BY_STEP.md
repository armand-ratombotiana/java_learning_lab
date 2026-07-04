# Step-by-Step — Containerize a Spring Boot App

## Step 1: Create Spring Boot Application
```bash
# Using Spring Initializr or:
mkdir spring-docker-app && cd spring-docker-app
# Create basic pom.xml + Application.java + Controller.java
```

## Step 2: Create Dockerfile
```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Step 3: Create .dockerignore
```dockerfile
# .dockerignore
.git
node_modules
target/
*.md
Dockerfile
.dockerignore
```

## Step 4: Build the Image
```bash
# Build
docker build -t spring-app:1.0.0 .

# Check image size
docker images spring-app:1.0.0
```

## Step 5: Run Container
```bash
# Run in background
docker run -d --name my-app -p 8080:8080 spring-app:1.0.0

# Check logs
docker logs my-app

# Test
curl http://localhost:8080/actuator/health

# View running containers
docker ps
```

## Step 6: Create Docker Compose with Database
```yaml
# docker-compose.yml
version: "3.8"
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/appdb
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: changeme
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: changeme
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app"]
      interval: 10s
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f app

# Stop
docker compose down -v  # -v removes volumes
```

## Step 7: Push to Registry
```bash
# Tag for registry
docker tag spring-app:1.0.0 myrepo/spring-app:1.0.0

# Push
docker push myrepo/spring-app:1.0.0
```

## Step 8: Clean Up
```bash
# Stop and remove container
docker stop my-app
docker rm my-app

# Remove image
docker rmi spring-app:1.0.0

# Prune unused resources
docker system prune -a
```
