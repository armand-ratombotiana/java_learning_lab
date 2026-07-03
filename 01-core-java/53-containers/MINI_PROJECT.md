# Module 53: Containers & Docker - Mini Project

**Project Name**: Dockerized Spring Boot App with Database  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Create an optimized, multi-stage Dockerfile for a Java application and use Docker Compose to run the application alongside a PostgreSQL database, ensuring proper networking and environment variable configuration.

## 📝 Requirements

### Core Features

1. **The Application**:
   - Create a simple Spring Boot app that connects to a database (Spring Data JPA) and exposes a REST endpoint to save and fetch users.

2. **Multi-Stage Dockerfile**:
   - Stage 1: Use `maven:3.9-eclipse-temurin-17` to build the app (`mvn clean package -DskipTests`).
   - Stage 2: Use `eclipse-temurin:17-jre-alpine` as the base.
   - Create a non-root user `appuser`.
   - Copy the JAR from the builder stage.
   - Set the `ENTRYPOINT` to run the JAR, using `-XX:MaxRAMPercentage=75.0`.

3. **Docker Compose**:
   - Create a `docker-compose.yml` file.
   - Define a `db` service using the `postgres:15-alpine` image. Configure it with a username, password, and database name.
   - Define an `app` service that builds from the current directory.
   - Expose port `8080`.
   - Use environment variables in the `app` service to pass the database URL (`jdbc:postgresql://db:5432/mydb`), username, and password to the Spring Boot app.
   - Use `depends_on` to ensure the database starts before the app.

4. **Execution**:
   - Run `docker-compose up --build`.
   - Verify the app connects to the database and handles REST requests successfully.

---

## 💡 Solution Blueprint

1. **Dockerfile**:
   ```dockerfile
   # Build Stage
   FROM maven:3.9-eclipse-temurin-17 AS builder
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package -DskipTests

   # Run Stage
   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   
   RUN addgroup -S appgroup && adduser -S appuser -G appgroup
   USER appuser:appgroup

   COPY --from=builder /app/target/*.jar app.jar
   EXPOSE 8080
   
   ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
   ```

2. **docker-compose.yml**:
   ```yaml
   version: '3.8'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
       environment:
         - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
         - SPRING_DATASOURCE_USERNAME=myuser
         - SPRING_DATASOURCE_PASSWORD=mypass
       depends_on:
         - db
     db:
       image: postgres:15-alpine
       environment:
         - POSTGRES_DB=mydb
         - POSTGRES_USER=myuser
         - POSTGRES_PASSWORD=mypass
       ports:
         - "5432:5432"
   ```