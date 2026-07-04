# Common Mistakes — Docker

## Dockerfile Mistakes

### 1. Using JDK Instead of JRE for Runtime
**Mistake**: `FROM eclipse-temurin:17-jdk` for final image.
**Issue**: Image 300MB+ larger than necessary.
**Fix**: Multi-stage build: JDK for build, JRE for runtime.

### 2. Running as Root
**Mistake**: No `USER` directive in Dockerfile.
**Issue**: Container runs as root — security risk.
**Fix**: `RUN addgroup -S app && adduser -S app -G app && USER app`

### 3. No Layer Caching Optimization
**Mistake**: Copying all files before dependency install.
```dockerfile
COPY . .
RUN npm install   # Reinstalls every time any file changes
```
**Fix**: Copy dependency files first, install, then copy source.
```dockerfile
COPY package.json .
RUN npm install
COPY . .
```

### 4. Not Using .dockerignore
**Mistake**: No `.dockerignore` file.
**Issue**: Build context includes `node_modules`, `target/`, `.git` → slow builds.
**Fix**: Add `.dockerignore` excluding build artifacts.

## Runtime Mistakes

### 5. No Health Check
**Mistake**: No HEALTHCHECK in Dockerfile.
**Issue**: Orchestrator doesn't know if app is actually healthy (vs process alive).
**Fix**: `HEALTHCHECK CMD curl -f http://localhost:8080/actuator/health`

### 6. Memory Not Limited
**Mistake**: `docker run` without `-m` flag.
**Issue**: Container can use all host memory; JVM -Xmx defaults to 25% of host.
**Fix**: `docker run -m 512m` and `-Xmx358m`.

### 7. Running in Foreground Not Detached
**Mistake**: Using `docker run myapp` (foreground) in production.
**Fix**: `docker run -d myapp` (detached). But always test in foreground first.

## Docker Compose Mistakes

### 8. Hardcoded Environment Variables
**Mistake**: Writing secrets in docker-compose.yml.
**Issue**: Secrets committed to version control.
**Fix**: Use `.env` file (gitignored) or `docker secrets`.

### 9. No depends_on Conditions
**Mistake**: `depends_on: - db` without health check condition.
**Issue**: App starts before DB is ready → crashes, retries.
**Fix**: Use `condition: service_healthy` and add healthcheck to DB service.

### 10. Using latest Tag
**Mistake**: `image: postgres:latest` in production.
**Issue**: Unpredictable upgrades; major version change breaks app.
**Fix**: Pin version: `image: postgres:16.2-alpine`.
