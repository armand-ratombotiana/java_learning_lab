# Docker MOCK_INTERVIEW.md

## Scenario 1: Optimizing Dockerfiles
Your team's Docker images are 2GB+ and builds take 15 minutes. The CI pipeline is slow and storage costs are high.

**Questions**:
1. How would you optimize the Dockerfile for size and build speed?
2. Explain multi-stage builds. Show an example.
3. What's the difference between `COPY` and `ADD`? When would you use each?
4. How do you handle `.dockerignore` and what should be in it?

**Expected approach**: Multi-stage builds, layer ordering (least-changing layers first), using slim/alpine base images, cleaning up apt/npm, `.dockerignore`, BuildKit caching, distroless vs alpine trade-offs.

## Scenario 2: Container Security
A security scan found critical vulnerabilities in your base image.

**Questions**:
1. How do you identify and remediate vulnerable images?
2. Explain Docker content trust (DCT) and image signing.
3. How would you enforce base image policies across the team?
4. What's the difference between `USER` instruction and running as root?

**Expected approach**: Trivy/Snyk scanning in CI, pinned base image tags, minimal images (distroless), `USER` instruction, read-only root filesystem, capabilities drop, seccomp profiles.

## Scenario 3: Docker Networking
A containerized app can't connect to a database container.

**Questions**:
1. Explain Docker network types: bridge, host, overlay, macvlan.
2. How does Docker DNS resolution work for container names?
3. How would you debug a container networking issue?
4. What's the difference between `EXPOSE` and publishing a port?

**Expected approach**: `docker network ls`, `docker network inspect`, user-defined bridge networks for DNS resolution, `docker compose` networking, debugging with `docker exec` + `ping`/`nslookup`/`tcpdump`.

## Scenario 4: Docker in Production
You need to run Docker containers in a production environment on a single VM.

**Questions**:
1. How do you ensure containers restart automatically?
2. How do you manage container logs and resource limits?
3. Explain Docker volumes vs bind mounts. When to use each?
4. How would you monitor Docker container health?

**Expected approach**: `restart: always` / `--restart=unless-stopped`, `--memory`/`--cpus` limits, json-file vs journald logging & log rotation, `docker system prune`, named volumes vs bind mounts, health checks, `docker stats`, cAdvisor, Prometheus Docker exporter.

## Scenario 5: CI/CD with Docker
Design a CI/CD pipeline that builds, tests, and pushes Docker images.

**Questions**:
1. Design the pipeline stages.
2. How do you cache Docker layers for fast builds?
3. How do you tag images for traceability?
4. How would you implement automatic rollback on deployment failure?

**Expected approach**: Stages: lint → test → build → scan → push → deploy. GitHub Actions Docker layer caching (`docker/build-push-action`). Tags: `git-sha`, `branch-name`, semantic version. Rollback via previous image tag + deployment revert.

## Key Docker Interview Questions
1. Explain the Docker architecture (client, daemon, containerd, runc).
2. What's the difference between a Docker image and a container?
3. How do you debug a container that exits immediately?
4. Explain Docker Compose. How is it different from Docker Swarm?
5. What are Docker health checks and how do they work?
6. How does Docker handle data persistence?
7. Explain the Docker build cache and when it gets invalidated.
8. What's the difference between ENTRYPOINT and CMD?
9. How would you migrate from Docker to containerd?
10. Explain container networking: CNM, libnetwork, drivers.

## Whiteboard Challenge
Design a Docker-based deployment for a 3-tier web application (frontend, API, database). Consider networking, data persistence, health checks, resource limits, and log management.

## Follow-up
1. How would you scale this to multiple hosts?
2. How would you handle secrets (DB passwords)?
3. What monitoring would you set up?