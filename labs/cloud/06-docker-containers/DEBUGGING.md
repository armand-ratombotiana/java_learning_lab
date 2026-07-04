# Debugging — Docker

## Container Won't Start

```
Symptom: `docker run` exits immediately
Check:
  1. docker logs <container-id>
  2. docker inspect <container-id> (State section)
  3. Check OOM: docker inspect --format '{{.State.OOMKilled}}' <container-id>

Common issues:
  - Port already in use: change -p 8080:8080 to -p 8081:8080
  - Entrypoint script error: check CMD/ENTRYPOINT syntax
  - Missing environment variable: add -e MY_VAR=value
  - Java -Xmx larger than container memory: reduce heap
```

## Connection Refused

```
Symptom: Can't connect from host (curl http://localhost:8080)
Check:
  1. Is container running? docker ps
  2. Is port mapped? docker port <container-id>
  3. App listening on 0.0.0.0? (not localhost!)
  4. Check app startup logs: docker logs <container-id>

Fix:
  - Spring Boot: server.address=0.0.0.0
  - Check port mapping: -p 8080:8080 (host:container)
```

## Out of Memory

```
Symptom: Container exits with code 137 (SIGKILL)
Check:
  - docker inspect: OOMKilled = true
  - docker stats: actual memory usage
  - JVM -Xmx too high for container limit

Fix:
  - Set -m 512m on container
  - Set -Xmx358m (-Xmx ≈ 70% of container memory)
  - Monitor with: docker stats --no-stream
```

## Layer Cache Misses

```
Symptom: Build is slow, no layer caching
Check:
  1. Are COPY commands ordered correctly?
  2. Does .dockerignore exclude irrelevant files?
  3. Are host file timestamps changing (invalidating cache)?

Fix:
  COPY pom.xml .   # Only pom.xml (cache hit if pom unchanged)
  RUN mvn deps     # Cached
  COPY src ./src   # Only src changed → rebuild from here
```

## Volume Permission Issues

```
Symptom: "Permission denied" when writing to mounted volume
Cause: Container user (UID) doesn't match host directory ownership
Fix:
  - Match UID: RUN adduser -u 1001 -S app && USER app
  - Or: chown the volume on mount
  - Or: use Docker named volumes (managed permissions)
```

## Network Debugging

```bash
# Test DNS resolution inside container
docker exec <container-id> nslookup google.com

# Test connectivity
docker exec <container-id> ping 8.8.8.8

# Inspect network
docker network ls
docker network inspect bridge

# View container IP
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <container>
```
