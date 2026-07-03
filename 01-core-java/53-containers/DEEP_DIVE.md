# Module 53: Containers & Docker for Java - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-52  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Containers](#intro)
2. [Dockerizing Java Applications](#dockerizing)
3. [Multi-Stage Builds](#multi-stage)
4. [Docker Compose](#docker-compose)
5. [JVM Container Awareness](#jvm-awareness)

---

## 1. Introduction to Containers <a name="intro"></a>
Containers provide a standardized, isolated environment for applications to run, packing the code and all its dependencies (JRE, libraries, OS utilities) into a single image. Docker is the industry standard for containerization.

---

## 2. Dockerizing Java Applications <a name="dockerizing"></a>
To dockerize a Java app, you create a `Dockerfile`. A basic Dockerfile looks like this:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/my-app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Multi-Stage Builds <a name="multi-stage"></a>
Multi-stage builds allow you to compile your application and package the final runtime image in a single Dockerfile, discarding the heavy build tools (like Maven/Gradle and the JDK).

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 4. Docker Compose <a name="docker-compose"></a>
Docker Compose is a tool for defining and running multi-container Docker applications via a `docker-compose.yml` file.

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
```

---

## 5. JVM Container Awareness <a name="jvm-awareness"></a>
Historically, the JVM did not respect Docker memory and CPU limits, looking instead at the host machine's hardware, which led to immediate OOM kills by Docker.
Java 10+ (and backported to 8u191) introduced **Container Awareness**. The JVM now correctly reads cgroup limits. Use `-XX:MaxRAMPercentage` instead of hardcoded `-Xmx` values when running in containers to allow the heap to scale dynamically with the container limit.