# Module 32: Cloud & DevOps - Quizzes

---

## Q1: Docker Base Images
Which base image is recommended for deploying a standard, compiled Java application to production in Docker to minimize image size and attack surface?

A) `ubuntu:latest`
B) `openjdk:17-jdk`
C) `eclipse-temurin:17-jre-alpine`
D) `centos:latest`

**Answer**: C
**Explanation**: For production, a Java Runtime Environment (JRE) based on a minimal OS like Alpine Linux provides the smallest footprint and reduces security vulnerabilities compared to full JDK or full OS images.

---

## Q2: Kubernetes Resources
Which Kubernetes object is responsible for exposing a Deployment to the network, providing load balancing and a stable IP address?

A) ConfigMap
B) Pod
C) ReplicaSet
D) Service

**Answer**: D
**Explanation**: A Kubernetes `Service` provides a stable IP and DNS name to abstract away the dynamic IP addresses of underlying Pods, effectively load balancing traffic across them.

---

## Q3: Graceful Shutdown
Why is it important to configure graceful shutdown in a Spring Boot application running inside Kubernetes?

A) To free up disk space.
B) To ensure the application processes in-flight requests before terminating when Kubernetes sends a `SIGTERM` signal.
C) To prevent the database from deleting records.
D) To make the application start faster.

**Answer**: B
**Explanation**: When Kubernetes scales down a Pod, it sends a `SIGTERM` signal. Graceful shutdown ensures the app finishes processing active requests before the JVM completely halts, preventing data loss or sudden client errors.