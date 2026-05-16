# Docker & Containers - CODE DEEP DIVE

## Java Application Dockerfile Examples

### Spring Boot Application

```dockerfile
# Dockerfile for Spring Boot
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="dev@example.com"
LABEL app="spring-boot-app"

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy jar
COPY target/*.jar app.jar

# Set permissions
RUN chown appuser:appgroup app.jar

# Environment
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENV SERVER_PORT=8080

EXPOSE 8080

USER appuser

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Quarkus Native Image

```dockerfile
# Multi-stage for GraalVM native
FROM quay.io/quarkus/ubi-quarkus-mandrel:23.0-java-21 AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY src ./src
RUN ./mvnw package -Pnative -DskipTests

FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /app
COPY --from=build /app/target/*-runner /app/application
EXPOSE 8080
ENTRYPOINT ["./application"]
```

### Multi-Container Java App

```yaml
# docker-compose.yml for Java microservices
version: '3.9'

services:
  gateway:
    build: ./gateway
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx256m
    depends_on:
      user-service:
        condition: service_healthy
      order-service:
        condition: service_healthy
    networks:
      - app-net

  user-service:
    build: ./user-service
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    environment:
      - DB_HOST=postgres
      - DB_PORT=5432
    networks:
      - app-net

  order-service:
    build: ./order-service
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    depends_on:
      kafka:
        condition: service_started
    networks:
      - app-net

  postgres:
    image: postgres:15-alpine
    volumes:
      - postgres-data:/var/lib/postgresql/data
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    networks:
      - app-net

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    networks:
      - app-net

  redis:
    image: redis:7-alpine
    networks:
      - app-net

volumes:
  postgres-data:

networks:
  app-net:
    driver: bridge
```

## Docker Java API Examples

### Using Docker Java SDK

```java
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerOperations {
    
    public void buildImage(DockerClient docker) {
        docker.buildImageCmd()
            .withDockerfile(new File("Dockerfile"))
            .withTag("myapp:1.0")
            .withNoCache(true)
            .exec(new BuildImageResultCallback())
            .awaitImageId();
    }
    
    public void runContainer(DockerClient docker) {
        docker.createContainerCmd("openjdk:21-jre-alpine")
            .withName("my-java-app")
            .withEnv("JAVA_OPTS=-Xmx512m")
            .withPortBindings(Collections.singletonMap(
                new PortBinding(Ports.Binding.bindPort(8080), 
                    new ExposedPort(8080))))
            .exec();
    }
}
```

## Build Patterns

### Layer Caching Optimization

```dockerfile
# Bad - dependencies reinstalled on every code change
FROM maven:3.9-eclipse-temurin-21
COPY . .
RUN mvn package

# Good - cache dependencies
FROM maven:3.9-eclipse-temurin-21
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package
```

### Testing in Container

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS test
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
COPY src/test ./src/test
RUN mvn test

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=test /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Health Check Examples

### Spring Boot Actuator

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
```

### Custom Health Endpoint

```java
@RestController
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Health> health() {
        Health health = Health.builder()
            .status(Status.UP)
            .database(checkDatabase())
            .cache(checkCache())
            .build();
        return ResponseEntity.ok(health);
    }
}
```