# Math Foundation for Docker

## Image Size Math

### Multi-Stage Build Savings
```
Single stage (JDK image):
  Base: eclipse-temurin:17-jdk = 350MB
  + Dependencies: 140MB
  + App JAR: 50MB
  Total: 540MB

Multi-stage (JRE image):
  Base: eclipse-temurin:17-jre = 80MB
  + Dependencies: 50MB (only runtime deps)
  + App JAR: 50MB
  Total: 180MB

Savings: 67% smaller (540MB → 180MB)
Faster pulls: bandwidth × size difference
```

### Alpine vs Full Distro
```
Feature       │ Ubuntu  │ Alpine  │ Distroless
──────────────┼─────────┼─────────┼────────────
Size          │ 70MB    │ 5MB     │ ~2MB
libc          │ glibc   │ musl    │ glibc
Package mgr   │ apt     │ apk     │ none
Shell         │ bash    │ sh      │ none
Java compat   │ Perfect │ Some issues │ Perfect
Debugging     │ Easy    │ Tricky  │ Hard

Recommendation for Java:
  Dev: eclipse-temurin:17-jdk-focal (full OS, debug tools)
  Prod: eclipse-temurin:17-jre-alpine (small, secure)
  Ultra-prod: gcr.io/distroless/java17 (minimal attack surface)
```

## Resource Overhead

### Container vs VM
```
Hardware: 1 server (16 CPU, 64GB RAM)
──────────┬──────────────┬──────────────
          │ VMs          │ Containers
──────────┼──────────────┼──────────────
Hypervisor│ Type 1 (5%)  │ None
Guest OS  │ 2-4GB per VM │ Shared kernel
Max apps  │ ~5-10 / server │ ~20-50 / server
Startup   │ 30-60s       │ <1s
Overhead  │ High         │ Minimal (~1MB per container)
──────────┴──────────────┴──────────────
```

## Resource Calculation

### Capacity Planning
```
Java app: 256MB heap, 100MB overhead = 512MB per container
Available server: 64GB RAM
  OS overhead: 2GB
  Docker overhead: 1GB
  Available for containers: 61GB
  Max containers: 61GB / 0.5GB ≈ 122 containers (CPU permitting)

CPU: 16 cores, 0.25 vCPU per container (burst to 1)
  Max: 64 containers with guaranteed CPU
  Overcommit: 128 containers (if not all active simultaneously)
```

## Build Cache Efficiency

### Layer Reuse
```
Layer          │ Change freq │ Build time   │ Cache hit
───────────────┼─────────────┼──────────────┼─────────
OS packages    │ Monthly     │ 2-5 min       │ 95%
JDK/JRE        │ Quarterly   │ 1-2 min       │ 98%
Maven deps     │ Weekly      │ 2-5 min       │ 80-90%
App code       │ Daily       │ 10-30s        │ 50-70%

Optimization: Order layers from stable → volatile
  FROM jre
  COPY pom.xml  (rarely changes)
  RUN mvn deps  (cached unless pom.xml changes)
  COPY src     (changes every build)
  RUN mvn package
```
