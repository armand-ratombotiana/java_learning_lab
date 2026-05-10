# Docker Module - PROJECTS.md

---

# Mini-Project: Docker Containerization

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Dockerfile, Docker Compose, Multi-stage Builds, Container Management

This mini-project demonstrates Docker containerization for Java applications.

---

## Project Structure

```
29-docker/
├── pom.xml
├── src/main/java/com/learning/
│   └── Main.java
├── Dockerfile
├── Dockerfile.multistage
└── docker-compose.yml
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
</project>
```

---

## Implementation

```dockerfile
# Dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/docker-demo-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```dockerfile
# Dockerfile.multistage
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db
      - redis
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=pass
  
  redis:
    image: redis:7-alpine

networks:
  default:
    name: mynetwork
```

```java
// Main.java
package com.learning;

public class Main {
    public static void main(String[] args) {
        System.out.println("Docker Demo Application Running");
    }
}
```

---

## Build Instructions

```bash
cd 29-docker
mvn clean package
docker build -t myapp .
docker-compose up -d
```

---

# Real-World Project

```yaml
# Advanced docker-compose.yml
services:
  app:
    build:
      context: .
      dockerfile: Dockerfile.multistage
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
```

---

## Build Instructions

```bash
cd 29-docker
mvn clean package
docker build -t myapp:latest .
docker-compose up --build
```