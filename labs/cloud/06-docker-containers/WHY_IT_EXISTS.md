# Why Docker Exists

## The Problem
"Works on my machine" — inconsistent environments between dev, test, and production. Every team member and environment has slightly different OS versions, library versions, JDK versions, and configurations.

## Docker's Answer
Package an application with all its dependencies into a standardized unit (container). Run the same unit anywhere: laptop, server, cloud. Containers share the host OS kernel but have isolated filesystems, networks, and processes.

## Why Docker for Java?
- **Consistent JDK version**: No "Java 8 vs 11 vs 17" conflicts
- **Reproducible builds**: Same Maven dependencies, same JVM args, same config
- **Microservices**: Each service in its own container, independently deployable
- **Dev/Prod parity**: Same Docker image runs on laptop and in production
- **CI/CD friendly**: Images built, tested, and deployed in pipeline
