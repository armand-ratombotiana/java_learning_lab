# Module 32: Cloud & DevOps - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between a Container (Docker) and a Virtual Machine (VM)?
**Answer**:
- **Virtual Machine**: Emulates an entire physical computer. It includes a full guest Operating System (OS), which runs on top of a hypervisor. Because each VM has its own OS, they are heavy, consume a lot of RAM/CPU, and take minutes to boot up.
- **Container**: Packages an application and its dependencies into a single image, but *shares the host machine's OS kernel*. Containers do not have a full guest OS, making them extremely lightweight, fast to spin up (often milliseconds), and highly portable.

### Q2: What are Liveness and Readiness probes in Kubernetes, and why do we need both?
**Answer**:
- **Liveness Probe**: Asks, "Is the application alive or frozen?" If a Liveness probe fails, Kubernetes assumes the application is deadlocked or in a crash loop and will automatically **kill and restart** the Pod.
- **Readiness Probe**: Asks, "Is the application ready to handle incoming traffic?" If an application is still starting up, loading caches, or applying database migrations, it shouldn't receive traffic yet. If the Readiness probe fails, Kubernetes will **remove the Pod's IP from the Service load balancer**, but it will *not* restart the Pod. Once the probe passes again, traffic resumes.

### Q3: Explain the concept of Infrastructure as Code (IaC).
**Answer**:
Infrastructure as Code (IaC) is the practice of managing and provisioning computing infrastructure (networks, virtual machines, load balancers) through machine-readable definition files (like Terraform `.tf` files or AWS CloudFormation YAML), rather than physical hardware configuration or interactive configuration tools (clicking through a web UI).
**Benefits**: Version control for infrastructure, reproducible environments, disaster recovery (re-spin up the entire cloud environment instantly), and automated deployment via CI/CD pipelines.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Multi-Stage Docker Builds
**Problem**: An intern wrote a `Dockerfile` for a Spring Boot application. The final image size is 800MB. They used `FROM maven:3.9-eclipse-temurin-17` and ran `mvn package` inside the container. Explain why the image is so large and how you would rewrite the Dockerfile to solve it using Multi-Stage builds.

**Solution**:
The image is massive because it contains the entire Maven build tool, the JDK (which includes compilers and tools not needed to *run* the app), and all the downloaded dependencies in the `~/.m2` directory.
A Multi-Stage build uses two `FROM` instructions. The first stage builds the app. The second stage uses a tiny JRE image and copies *only* the final compiled `.jar` from the first stage, leaving all the build tools behind.

```dockerfile
# Stage 1: Build environment (discarded later)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Runtime environment (final image)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/app.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Scenario 2: Rolling Deployments
**Problem**: You deploy a new version of your Java microservice to Kubernetes. However, during the deployment, users experience a 5-second window where they receive `502 Bad Gateway` errors. Assuming the code works fine, what is the most likely cause of this downtime, and how do you fix it in Kubernetes?

**Solution**:
The most likely cause is that Kubernetes killed the old version of the application before the new version was actually ready to accept traffic (e.g., Spring Boot takes a few seconds to start up Tomcat).
To fix this, you must configure a **Readiness Probe**. Kubernetes will start the new Pod, but it will *wait* until the Readiness Probe returns an HTTP 200 OK. Only then will it route traffic to the new Pod and subsequently terminate the old Pod, ensuring Zero Downtime.