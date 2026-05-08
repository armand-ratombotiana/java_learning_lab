# Cloud & DevOps Exercises

## Exercise 1: Design a CI/CD Pipeline
Create a GitHub Actions workflow for a Java Spring Boot app with:
- Build on push to main
- Run unit tests
- Build Docker image
- Deploy to staging

## Exercise 2: Write Kubernetes YAML
Create deployment.yaml for a Java application with:
- 3 replicas
- Health checks
- ConfigMap for environment variables
- Service for cluster access

## Exercise 3: Terraform Infrastructure
Write Terraform to provision:
- One EC2 instance
- Security group with port 8080
- S3 bucket for data storage

## Exercise 4: Containerize a Java App
Create a multi-stage Dockerfile for a Spring Boot app with:
- Builder stage with Maven
- Minimal JRE runtime image
- Non-root user

## Exercise 5: Monitoring Stack
Design a Prometheus + Grafana setup for Java app monitoring with:
- JVM metrics
- Request latency
- Error rates