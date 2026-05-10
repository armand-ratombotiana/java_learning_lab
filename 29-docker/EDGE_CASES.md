# Docker Edge Cases

## Build Failures

### No Space Left on Device

```bash
# Clean up Docker
docker system prune -a
docker volume prune

# Or build with no cache
docker build --no-cache .
```

### Network Timeouts

```dockerfile
# Use --network=host during build
RUN --network=host mvn dependency:go-offline

# Or increase timeout
RUN timeout 300 mvn package
```

### Private Registry Authentication

```bash
# Login to registry
docker login registry.example.com

# Use in Dockerfile
FROM registry.example.com/base:1.0
```

---

## Runtime Issues

### Permission Denied

```dockerfile
# Create user and set ownership
RUN addgroup -S app && adduser -S app -G app
COPY --chown=app:app . .
USER app
```

### Out of Memory

```yaml
# docker-compose.yml
services:
  app:
    mem_limit: 1g
    mem_reservation: 256m
    oom_kill_disable: true
```

### Port Conflicts

```yaml
services:
  app:
    ports:
      - "8080:8080"  # Host:Container
    # Or use dynamic port
    ports:
      - "${APP_PORT:-8080}:8080"
```

---

## Network Issues

### DNS Resolution

```yaml
services:
  app:
    dns:
      - 8.8.8.8
    dns_search:
      - local.example.com
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

### Connection Refused

```bash
# Check if service is running
docker-compose ps

# Check logs
docker-compose logs service

# Restart with fresh network
docker-compose down -v
docker-compose up -d
```

---

## Volume Issues

### Permission Issues

```yaml
# Use named volume with proper permissions
volumes:
  data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /path/to/data

# Or fix permissions
RUN chown -R app:app /data
```

### Data Not Persisting

```yaml
# Use named volumes instead of bind mounts
volumes:
  db-data:
    driver: local

services:
  db:
    volumes:
      - db-data:/var/lib/postgresql/data
```

---

## Health Check Failures

### Health Check Timing Out

```yaml
# Increase timeout and retries
services:
  app:
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 120s
```

### Application Not Ready

```yaml
# Use startup script
services:
  app:
    command: >
      sh -c "
        java -jar app.jar &
        wait-for-it db:5432 --timeout=60 --
        java -jar app.jar
      "
```

---

## Resource Exhaustion

### Too Many Open Files

```yaml
services:
  app:
    ulimits:
      nofile:
        soft: 65536
        hard: 65536
```

### PID Limit

```yaml
services:
  app:
    pids_limit: 100
```

---

## Security Issues

### Root User Running

```dockerfile
# Create non-root user
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser
```

### Sensitive Data Exposure

```dockerfile
# Don't copy secrets into image
# Use runtime injection
ENV DB_PASSWORD_FILE=/run/secrets/db_password

# Or use secret mount
secrets:
  - db_password
```

---

## Image Registry Issues

### Image Not Found

```bash
# Check image exists
docker pull image:tag

# Use specific digest
docker pull image@sha256:abc...
```

### Registry Certificate Issues

```bash
# Add certificate
echo "CERT" >> /etc/docker/certs.d/registry.example.com/ca.crt

# Or use insecure registry (dev only)
# /etc/docker/daemon.json
{
  "insecure-registries": ["registry.example.com"]
}
```

---

## Best Practices

1. **Always set resource limits**
2. **Use health checks for critical services**
3. **Handle signals properly**
4. **Manage log rotation**
5. **Monitor container resources**
6. **Use proper logging drivers**
7. **Handle graceful shutdowns**