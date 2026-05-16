# Docker & Containers - FLASHCARDS

## Basics

### Card 1
**Q:** What is a container?
**A:** Lightweight, standalone executable package containing code, runtime, libraries, and settings.

### Card 2
**Q:** Container vs VM difference?
**A:** Containers share host OS kernel; VMs include their own OS (heavier).

### Card 3
**Q:** What is a Docker image?
**A:** Read-only template used to create containers.

### Card 4
**Q:** What is a Docker registry?
**A:** Storage system for Docker images (Docker Hub, ECR, ACR).

## Dockerfile

### Card 5
**Q:** What does FROM do?
**A:** Specifies the base image for the container.

### Card 6
**Q:** COPY vs ADD?
**A:** COPY is preferred; ADD has extra features (URL support, extraction).

### Card 7
**Q:** Multi-stage build purpose?
**A:** Separate build and runtime environments for smaller, secure images.

### Card 8
**Q:** Layer caching optimization?
**A:** Put rarely changing instructions (dependencies) first, frequently changing last.

### Card 9
**Q:** What is the difference between CMD and ENTRYPOINT?
**A:** CMD can be overridden; ENTRYPOINT is the main executable.

### Card 10
**Q:** HEALTHCHECK syntax?
**A:** HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD curl -f http://localhost:8080/health || exit 1

## Docker Compose

### Card 11
**Q:** What is depends_on used for?
**A:** Defines service startup order; can wait for health checks.

### Card 12
**Q:** Named volume vs bind mount?
**A:** Named volume: managed by Docker, reusable; Bind mount: host directory mapping.

### Card 13
**Q:** How to scale services?
**A:** docker-compose up --scale app=3

### Card 14
**Q:** What does condition: service_healthy do?
**A:** Delays service start until health check passes.

### Card 15
**Q:** Bridge network DNS resolution?
**A:** Container names resolve automatically in user-defined networks.

## Networking

### Card 16
**Q:** Port mapping syntax?
**A:** -p host:container (e.g., -p 8080:8080)

### Card 17
**Q:** Host network mode?
**A:** Removes isolation; container uses host networking directly.

### Card 18
**Q:** Overlay network use case?
**A:** Multi-host Docker (Swarm) communication.

### Card 19
**Q:** How to create custom network?
**A:** docker network create --driver bridge mynet

### Card 20
**Q:** Container DNS resolution?
**A:** User-defined networks enable automatic DNS; default bridge requires --link.

## Volumes

### Card 21
**Q:** tmpfs use case?
**A:** In-memory storage for sensitive/temporary data (not persisted).

### Card 22
**Q:** Volume data persistence?
**A:** Named volumes persist after container removal.

### Card 23
**Q:** Backup volume command?
**A:** docker run -v vol:/data -v $(pwd):/backup alpine tar cvf /backup/backup.tar /data

### Card 24
**Q:** Volume driver purpose?
**A:** Enable cloud storage backends (EBS, Azure Files, GCE PD).

### Card 25
**Q:** Share data between containers?
**A:** Use shared named volume or bind mount to same host path.

## Security

### Card 26
**Q:** Why not run as root?
**A:** Container breakout could give root access on host.

### Card 27
**Q:** .dockerignore purpose?
**A:** Exclude unnecessary files from build context (reduces size, improves security).

### Card 28
**Q:** Add non-root user command?
**A:** RUN addgroup -S app && adduser -S app -G app; USER app

### Card 29
**Q:** Read-only filesystem flag?
**A:** --read-only prevents writes to container filesystem.

### Card 30
**Q:** Why use specific image tags?
**A:** Reproducibility - avoid unexpected updates from :latest.

## Commands

### Card 31
**Q:** Build image command?
**A:** docker build -t myapp:1.0 .

### Card 32
**Q:** Run container detached?
**A:** docker run -d -p 8080:8080 --name myapp myapp:1.0

### Card 33
**Q:** View container logs?
**A:** docker logs -f myapp

### Card 34
**Q:** Execute command in running container?
**A:** docker exec -it myapp bash

### Card 35
**Q:** Clean up unused resources?
**A:** docker system prune -a

---

**Total: 35 flashcards**