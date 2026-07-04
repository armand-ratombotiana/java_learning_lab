# Docker Exercises

## Exercise 1: Hello Docker
Run `hello-world` container. Verify it works.

## Exercise 2: Interactive Container
Run an Ubuntu container interactively, install curl, run a web request, exit.

## Exercise 3: Build Your Own Image
Write a Dockerfile for a simple Python Flask app (port 5000). Build and run it.

## Exercise 4: Docker Compose
Create a docker-compose.yml with:
- PostgreSQL 15 service (port 5432)
- Adminer service (port 8080)
- A custom app service that connects to PostgreSQL

## Exercise 5: Multi-stage Build
Take the Python Flask app from Ex3. Create a multi-stage Dockerfile:
- Stage 1: Build stage (install build tools, compile)
- Stage 2: Runtime (minimal Python image, copy artifacts)

## Exercise 6: Optimization
Given a bloated Dockerfile, optimize it:
- Use Alpine base
- Combine RUN commands
- Add .dockerignore
- Reduce final image size

## Exercise 7: Debugging
Intentionally break a Dockerfile and debug the build failure using logs and interactive inspection.
