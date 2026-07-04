# Refactoring — Docker

## 1. From Single-Stage to Multi-Stage Build

### Before
```dockerfile
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./mvnw package
EXPOSE 8080
CMD ["java", "-jar", "target/app.jar"]
# Size: 540MB
# Includes JDK, compiler, build tools in production image
```

### After
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml mvnw ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
# Size: 180MB (67% smaller)
```

## 2. From Manual Docker Commands to Docker Compose

### Before
```bash
# Start database
docker run -d --name db -e POSTGRES_PASSWORD=pass postgres:16

# Start app (find DB IP first)
docker run -d --name app -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://172.17.0.2:5432/app \
  myapp
```

### After
```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports: - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/app
  db:
    image: postgres:16
    environment:
      POSTGRES_PASSWORD: pass
```

```bash
docker compose up -d  # One command, DNS resolution via service name
```

## 3. From Thick JAR to Layered JAR (Spring Boot)

### Before
```dockerfile
COPY target/app.jar app.jar
# Any code change = whole layer invalidated
```

### After
```dockerfile
# Extract Spring Boot layered JAR
COPY target/app.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Dependencies layer (rarely changes)
COPY --from=builder dependencies/ ./
# Spring Boot loader layer
COPY --from=builder spring-boot-loader/ ./
# Application classes (changes frequently)
COPY --from=builder application/ ./
```

## 4. From Version Tags to Semantic Tags

### Before
```bash
docker tag myapp:latest
# Never know what version "latest" actually is
```

### After
```bash
# CI pipeline tags:
docker tag myapp:1.0.0-build.123  # Build number
docker tag myapp:1.0.0             # Release version
docker tag myapp:1.0               # Major.minor
docker tag myapp:1                  # Major
docker tag myapp:latest             # Last stable release
```
