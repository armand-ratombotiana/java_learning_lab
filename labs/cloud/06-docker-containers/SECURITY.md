# Security — Docker

## Image Security

### Use Trusted Base Images
```dockerfile
# Verified official images (Docker Official Images program)
FROM eclipse-temurin:17-jre-alpine  # ✅ Official, signed
FROM adoptopenjdk:17-jre-hotspot    # ✅ Verified
FROM openjdk:17-jre-slim             # ✅ Official

# Avoid unknown publishers
FROM mypersonal-repo/jre:latest      # ❌ Unknown origin
```

### Image Scanning
```bash
# Docker Scout (requires subscription)
docker scout quickview spring-app:1.0.0
docker scout cves spring-app:1.0.0

# Trivy (open source)
trivy image spring-app:1.0.0

# Grype (open source)
grype spring-app:1.0.0

# Fix CVEs:
# 1. Update base image
# 2. Patch OS packages
# 3. Update application dependencies
```

### Sign Images (Docker Content Trust)
```bash
export DOCKER_CONTENT_TRUST=1
docker push myapp:1.0.0  # Signed automatically
# Verify on pull:
docker trust inspect --pretty myapp:1.0.0
```

## Runtime Security

### Run as Non-Root User
```dockerfile
RUN addgroup -S app && adduser -S app -G app
USER app
# Container runs as non-root even if entrypoint needs root
# Use sudoers for specific escalated commands (avoid if possible)
```

### Drop Capabilities
```bash
docker run --cap-drop=ALL --cap-add=NET_BIND_SERVICE myapp
# Drop all capabilities, add only what's needed
# Typical: --cap-drop=ALL (Java apps don't need kernel capabilities)
```

### Read-Only Root Filesystem
```bash
docker run --read-only --tmpfs /tmp --tmpfs /var/run myapp
# Container filesystem is read-only (except /tmp and /var/run)
# Prevents malware writing to filesystem
```

## Supply Chain Security

### SBOM (Software Bill of Materials)
```bash
# Generate SBOM for Docker image
docker sbom spring-app:1.0.0 > sbom.spdx.json

# SBOM contains:
# - All packages and versions
# - Licenses
# - Dependency tree
# - Known vulnerabilities
```

### Signing in CI/CD
```yaml
# GitLab CI example
build:
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_TAG .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_TAG
    - cosign sign $CI_REGISTRY_IMAGE:$CI_COMMIT_TAG
```

## Network Security

### Isolate Networks
```yaml
# Docker Compose — separate networks per tier
services:
  app:
    networks:
      - frontend  # Can reach reverse proxy
      - backend   # Can reach DB
  db:
    networks:
      - backend   # Cannot reach frontend/reverse proxy

networks:
  frontend:
  backend:
```
