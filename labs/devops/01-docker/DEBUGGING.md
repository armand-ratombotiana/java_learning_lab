# Docker Debugging Guide

## Common Debugging Commands
```powershell
# View container logs
docker logs -f <container_id>

# Execute command in running container
docker exec -it <container_id> sh

# Inspect container details
docker inspect <container_id>

# View resource usage
docker stats

# Copy files from container
docker cp <container_id>:/app/logs ./

# Debug build failures
docker build --no-cache -t myapp .
docker run -it myapp sh
```

## Common Issues
- **Build fails**: Check Dockerfile syntax, build context path, and network access.
- **Container exits immediately**: Check entrypoint/cmd, run with `-it` interactively.
- **Port conflicts**: Change host port mapping.
- **Disk space**: Run `docker system df` and clean with `docker system prune`.
- **Volume permission errors**: Check UID/GID of container user vs host user.
