# How Docker Works

## Docker Architecture

```
┌─────────────────────────────────────────────┐
│  Docker Client (CLI / API)                   │
│  docker build, docker run, docker pull       │
└─────────────────────┬───────────────────────┘
                      │ REST API (Unix socket / HTTP)
┌─────────────────────▼───────────────────────┐
│  Docker Daemon (dockerd)                     │
│  ┌─────────┐ ┌──────────┐ ┌──────────────┐ │
│  │ Images  │ │Containers│ │ Volumes      │ │
│  │ (store) │ │(runtime) │ │(persist)     │ │
│  └─────────┘ └──────────┘ └──────────────┘ │
└─────────────────────┬───────────────────────┘
                      │ containerd (OCI runtime)
┌─────────────────────▼───────────────────────┐
│  containerd                                   │
│  ┌──────────┐ ┌──────────┐ ┌─────────────┐  │
│  │  runc    │ │  gRPC    │ │ Snapshotter │  │
│  └────┬─────┘ └──────────┘ └─────────────┘  │
└───────┼──────────────────────────────────────┘
        │
┌───────▼──────────────────────────────────────┐
│  Host Linux Kernel                            │
│  ┌──────────┐ ┌──────────┐ ┌────────────┐  │
│  │ cgroups  │ │namespaces│ │UnionFS     │  │
│  │(CPU/mem) │ │(isolate) │ │(layers)    │  │
│  └──────────┘ └──────────┘ └────────────┘  │
└──────────────────────────────────────────────┘
```

## Image Build Process

1. **Dockerfile** parsed line by line
2. Each instruction creates a new layer
3. Layer cache: if instruction and context unchanged, reuse cached layer
4. Optimistic caching: COPY . changes → all subsequent layers rebuild
5. Final image: union of all layers (hash = sha256 of manifest)

```dockerfile
# Multi-stage build (common pattern)
# Stage 1: Build with JDK
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run with JRE (smaller)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Container Runtime

1. Docker client: `docker run -d -p 8080:8080 myapp:latest`
2. Daemon pulls image (if not cached), creates container
3. containerd creates OCI bundle (config.json + rootfs)
4. runc creates cgroup + namespaces → starts process
5. Java process starts in isolated environment
6. Docker attaches networking, volumes, environment
