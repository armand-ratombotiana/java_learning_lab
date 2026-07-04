# Performance — Docker

## Image Size Optimization

### Minimize Layers
```dockerfile
# Bad: 3 layers for 3 commands
RUN apt-get update
RUN apt-get install -y curl
RUN rm -rf /var/lib/apt/lists/*

# Good: 1 layer
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*
```

### Alpine Base
```
Feature          │ Ubuntu 22.04  │ Alpine 3.19
─────────────────┼───────────────┼─────────────
Base image size  │ 70MB          │ 5MB
JRE 17 (full)    │ ~270MB        │ ~280MB (glibc compat)
Total            │ ~340MB        │ ~285MB (16% smaller)
Package install  │ apt (slow)    │ apk (fast)
```

### Distroless for Production
```
gcr.io/distroless/java17-debian12: ~130MB total
No package manager, no shell, no utilities
Only what's needed to run Java
Security: reduced attack surface
```

## Build Performance

### BuildKit Caching
```bash
# Enable BuildKit (Docker 18.09+)
DOCKER_BUILDKIT=1 docker build .

# Cache mounts for Maven
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests
# Maven dependencies cached between builds (even if pom.xml changes)
```

### Parallel Builds with Compose
```bash
# Build services in parallel
docker compose build --parallel

# With BuildKit, parallel stage execution
```

## Runtime Performance

### JVM in Container
```bash
# JDK 10+ detects container limits automatically
# Use these flags for best container performance:
docker run -m 512m openjdk:17 \
  java -XX:+UseContainerSupport \
       -XX:MaxRAMPercentage=70.0 \
       -XX:+UseZGC \
       -XX:ConcGCThreads=2 \
       -jar app.jar

# UseContainerSupport: JVM reads cgroup limits
# MaxRAMPercentage=70: heap = 70% of 512m = 358MB
# ZGC: low-pause garbage collector
```

### Network Performance
```
Bridge:     ~40 Gbps (host to container)
Host mode:  ~100 Gbps (no network translation)
Overlay:    ~20 Gbps (VXLAN encapsulation overhead)

For maximum network performance: host networking
For maximum isolation: bridge/overlay
```

### Volume Performance
```
Bind mount: native filesystem performance (fastest)
Named volume: managed by Docker, copy-on-write
tmpfs mount: in-memory, fastest (but non-persistent)
```

## Benchmark Example

```bash
# Java Spring Boot app performance comparison
# Native vs Containerized vs VM

Metric         │ Native │ Container │ VM
───────────────┼────────┼───────────┼──────
Startup time   │ 3.2s   │ 3.5s      │ 35s
Memory (idle)  │ 180MB  │ 185MB     │ 1.2GB
Throughput     │ 100%   │ 98%       │ 95%
Image/Disk     │ 50MB   │ 180MB     │ 4GB+
```
