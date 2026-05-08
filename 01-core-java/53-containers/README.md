# 53 - Containers

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Status](https://img.shields.io/badge/Status-Complete-success?style=for-the-badge)

**Docker and Kubernetes for Java developers**

</div>

---

## Overview

This module covers containerization with Docker, Kubernetes orchestration, container networking, and multi-stage builds for Java applications.

### Learning Objectives

- Create Dockerfiles for Java applications
- Understand container networking and resource limits
- Deploy applications to Kubernetes
- Use multi-stage builds for optimized images

---

## Topics Covered

### Docker Basics
- Container vs Image vs Registry
- Dockerfile instructions: FROM, RUN, COPY, ENTRYPOINT
- Layer caching and optimization

### Multi-Stage Builds
- Build stage (Maven/Gradle)
- Runtime stage (JRE only)
- Image size: ~400MB -> ~50MB

### Kubernetes
- Pod: smallest deployable unit
- Deployment: declarative updates
- Service: stable network endpoint
- ConfigMap/Secret: configuration

### Resource Management
- CPU and memory limits
- Java in containers: `-XX:MaxRAMPercentage`
- UseContainerSupport (JDK 10+)

---

## Running the Module

```bash
cd 01-core-java/53-containers
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.containers.Lab"
```

---

## Best Practices

- Use Alpine or distroless base images
- Order Dockerfile instructions by change frequency
- Set resource requests and limits
- Scan images for vulnerabilities (trivy)

---

<div align="center">

[Exercises](./EXERCISES.md) | [Pedagogic Guide](./PEDAGOGIC_GUIDE.md)

</div>