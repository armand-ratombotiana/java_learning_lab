# Module 55: Spring Native & GraalVM - Mini Project

**Project Name**: Serverless Native Image Build  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Create a lightweight Spring Boot 3 REST API, package it as a GraalVM Native Image using Cloud Native Buildpacks, and measure the massive difference in memory consumption and startup time compared to the standard JVM.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Create a Spring Boot 3 application using Java 17+.
   - Add the `spring-boot-starter-web` dependency.

2. **The Application**:
   - Create a simple `@RestController` with a `GET /hello` endpoint returning "Hello Native World!".
   - Keep the application simple (avoid adding JPA/Hibernate for this exercise to keep compilation times manageable).

3. **Standard JVM Execution & Measurement**:
   - Build the standard JAR: `mvn clean package`.
   - Run the JAR: `java -jar target/myapp.jar`.
   - Note the startup time printed in the console (e.g., `Started Application in 2.5 seconds`).
   - Use a tool like `jps` and `jmap` (or simply Task Manager / Activity Monitor) to measure the memory consumed by the running process (typically ~300MB+ for a basic Spring app). Stop the app.

4. **Native Image Build**:
   - Ensure Docker is running on your machine.
   - Use the built-in Spring Boot buildpacks plugin to compile the native image:
     `mvn spring-boot:build-image -Pnative`
   - *Note: This step will take several minutes and consume a large amount of CPU/RAM as GraalVM performs global static analysis.*

5. **Native Execution & Measurement**:
   - Run the generated Docker container: `docker run --rm -p 8080:8080 myapp:0.0.1-SNAPSHOT`.
   - Note the startup time printed in the console. It should be drastically reduced (e.g., `Started Application in 0.05 seconds`).
   - Measure the memory consumption of the Docker container using `docker stats`. It should be a fraction of the JVM's usage (e.g., ~50MB).

---

## 💡 Solution Blueprint

**No code changes are strictly necessary** in a basic Spring Boot 3 app to enable Native compilation. The magic happens in the Maven execution.

```bash
# 1. Standard Build & Run
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
# Observe: ~2.5 seconds startup, ~300MB RAM

# 2. Native Build (Docker required)
mvn spring-boot:build-image -Pnative

# 3. Native Run
docker run --rm -p 8080:8080 demo:0.0.1-SNAPSHOT
# Observe: ~0.05 seconds startup, ~40MB RAM
```