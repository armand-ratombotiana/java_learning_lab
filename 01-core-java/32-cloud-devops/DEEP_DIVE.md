# Module 32: Cloud & DevOps - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-31, especially Docker and Microservices concepts  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Cloud Native Applications](#intro)
2. [Dockerization of Java Apps](#docker)
3. [Kubernetes Orchestration Basics](#k8s)
4. [CI/CD Pipelines (GitHub Actions / Jenkins)](#cicd)
5. [Infrastructure as Code (Terraform)](#iac)

---

## 1. Introduction to Cloud Native Applications <a name="intro"></a>
Cloud Native applications are designed to be highly scalable, resilient, and manageable. They leverage containerization, dynamic orchestration, and microservices architecture. Twelve-Factor App principles are a fundamental building block.

---

## 2. Dockerization of Java Apps <a name="docker"></a>
Containerizing a Java application ensures that it runs consistently across different environments. A `Dockerfile` defines the container image.

```dockerfile
# Use a lightweight JRE base image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the fat jar into the container
COPY target/my-app-1.0.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Kubernetes Orchestration Basics <a name="k8s"></a>
Kubernetes (K8s) automates the deployment, scaling, and management of containerized applications. Key resources include:
- **Deployment**: Manages replicas and rolling updates.
- **Service**: Exposes the application to the network (ClusterIP, NodePort, LoadBalancer).
- **ConfigMap & Secret**: Externalize configuration.

---

## 4. CI/CD Pipelines (GitHub Actions / Jenkins) <a name="cicd"></a>
Continuous Integration and Continuous Deployment (CI/CD) automates the build, test, and deployment phases.

```yaml
# GitHub Actions example
name: Java CI

on:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build with Maven
      run: mvn clean package
```

---

## 5. Infrastructure as Code (Terraform) <a name="iac"></a>
Infrastructure as Code (IaC) is the process of managing and provisioning computing infrastructure through machine-readable definition files. Terraform is a popular tool for this.