# Interview Questions — Docker

## Beginner

**Q1**: What is the difference between a Docker image and a container?

**Q2**: Explain the Docker build process.

**Q3**: What is Docker Compose and when would you use it?

## Intermediate

**Q4**: What are Docker layers and how does caching work?

**Q5**: How do you pass environment variables to a Docker container at runtime?

**Q6**: Explain the difference between Docker's bridge, host, and overlay network modes.

**Q7**: How would you containerize a Spring Boot application for production?

## Advanced

**Q8**: What is multi-stage build and why is it important for Java applications?

**Q9**: How does Docker handle resource limits (CPU, memory) at the kernel level?

**Q10**: Explain Docker's container networking model (CNM).

**Q11**: How would you design a secure Docker setup for a multi-tenant Java platform?

## Sample Answers

**A1**: An image is a read-only template (layered filesystem + metadata). A container is a running instance of an image (with writable layer, isolated namespaces, cgroups). Multiple containers can run from the same image.

**A2**: Docker reads the Dockerfile instruction by instruction. Each instruction creates a new layer. Docker checks its cache: if instruction and context are unchanged, the cached layer is reused. The final image is the union of all layers plus metadata (entrypoint, ports, env).

**A3**: Docker Compose defines and runs multi-container applications from a YAML file. Use when your app has multiple services (web, database, cache, queue). One command (docker compose up) starts all services with networking, volumes, and dependencies resolved.

**A4**: Each Dockerfile instruction creates a read-only layer. Layers are stacked (UnionFS). Docker caches layers and reuses them if the instruction and context (file content, not timestamp) haven't changed. Cache invalidation: COPY changes break cache from that point forward. Optimize by ordering stable instructions first (apt install → deps → app code).

**A5**: `docker run -e MY_VAR=value myapp` or `--env-file .env`. In Compose: `environment:` block or `env_file: .env`. For secrets: `docker secret` or Docker Compose `secrets:` (mounted to /run/secrets/).

## Key Topics for Docker
- Dockerfile instructions (FROM, RUN, COPY, ADD, CMD, ENTRYPOINT, EXPOSE, ENV, WORKDIR, USER)
- Image layering and caching optimization
- Multi-stage builds
- Docker Compose (services, networks, volumes, dependencies)
- Docker networking (bridge, host, overlay, macvlan, none)
- Volumes and bind mounts
- Container resource limits (cgroups)
- Docker security (non-root user, read-only FS, capability dropping)
- Registry and image tagging
