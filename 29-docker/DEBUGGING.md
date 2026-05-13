# Debugging Docker Container Issues

## Common Failure Scenarios

### Container Won't Start

Containers exit immediately after starting. The container status shows "exited" with a non-zero code. Application fails to run inside the container. This is typically a configuration or application error.

Check the exit code to diagnose. Code 127 means command not found—verify the ENTRYPOINT or CMD. Code 126 means command is not executable—check file permissions. Code 1 is application error—check application logs.

Environment variables may be missing or incorrect. The application expects variables not provided in docker run or Dockerfile. Check for required configuration and pass variables via -e or env_file.

### Networking Issues

Containers cannot reach each other or external services. Connections timeout or refuse. DNS resolution fails for service names. Network configuration prevents communication.

Docker networks isolate containers. By default, containers on different networks cannot communicate. Use `docker network inspect` to verify container network membership. Create and connect containers to the same network.

Port mapping conflicts prevent container startup. If the host port is already in use, the container fails to start. Check for other processes using the same port with `netstat` or `docker ps`.

### Stack Trace Examples

**Exit code 1:**
```
docker: Error response from daemon: oci runtime error: container process died
    exit code: 1
```

**Port binding failure:**
```
docker: Error response from daemon: driver failed programming external connectivity on endpoint
    Bind for 0.0.0.0:8080 failed: port is already allocated
```

**Volume mount error:**
```
docker: Error response from daemon: invalid mount config for type "bind": bind source path does not exist
```

## Debugging Techniques

### Inspecting Container State

Use `docker ps -a` to see all containers and their status. Use `docker logs` to see container output. Use `docker inspect` for detailed container configuration.

Check container logs with `docker logs <container>`. Use `--tail` to limit output, `--follow` to stream. Check both stdout and stderr for errors.

Use `docker exec` to enter running containers. Run diagnostic commands inside the container. Check file system, environment variables, and running processes.

### Network Diagnostics

Use `docker network ls` to list networks. Use `docker network inspect` to see containers on a network. Check the NetworkMode setting in docker inspect output.

Test connectivity from inside the container: `docker exec <container> ping <target>`. Use `docker exec <container> curl` to test HTTP endpoints.

Use `docker stats` to monitor container resource usage. Check for memory limits causing OOM kills. Monitor CPU throttling for CPU-limited containers.

## Best Practices

Use official base images and keep them updated. Scan images for vulnerabilities regularly. Use multi-stage builds to reduce image size.

Set appropriate resource limits (memory, CPU). Use health checks to detect unhealthy containers. Use restart policies for automatic recovery.

Log to stdout/stderr for docker log collection. Avoid writing logs to volume mounts. Use docker log drivers to send logs to aggregation systems.