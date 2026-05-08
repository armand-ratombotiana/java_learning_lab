# Spring Boot Docker

Containerization module for Spring Boot applications.

## Overview

- Docker containerization
- Multi-stage builds
- Optimized images
- Docker Compose integration

## Key Files

- `Dockerfile` - Container definition
- `docker-compose.yml` - Multi-container setup

## Building

```bash
mvn clean package
docker build -t spring-boot-app .
```

## Running

```bash
docker run -p 8080:8080 spring-boot-app
```

## Dockerfile Pattern

Uses multi-stage build:
1. Build stage with Maven
2. Runtime stage with JRE only
3. Optimized Alpine base

## Docker Compose

Run with dependencies:
```bash
docker-compose up
```

## Dependencies

- spring-boot-starter (embedded server)
- Docker / Docker Compose

## Image Optimization

- Multi-stage builds
- Alpine base
- Non-root user
- Minimal layers

## Version

Spring Boot 3.3.0