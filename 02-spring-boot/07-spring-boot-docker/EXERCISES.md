# Spring Boot Docker - Exercises

## Lab Exercises

### Exercise 1: Build JAR
```bash
mvn clean package -DskipTests
```
- Verify JAR in target/ folder

### Exercise 2: Docker Build
```bash
docker build -t spring-boot-app .
```
- Review Dockerfile
- Note multi-stage build

### Exercise 3: Run Container
```bash
docker run -p 8080:8080 spring-boot-app
```
- Verify application runs in container

### Exercise 4: Multi-stage Build
- Understand builder stage
- Optimize for smaller image
- Use alpine base

### Exercise 5: Docker Compose
- Review docker-compose.yml
- Multiple services
- Environment configuration

### Exercise 6: JAR vs Native
- Compare regular JAR vs native executable
- Build native with GraalVM (optional)

## Build & Run
```bash
mvn clean package
docker build -t spring-boot-app .
docker run -p 8080:8080 spring-boot-app
```