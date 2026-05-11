# Docker Commands Reference

## Image Management

| Command | Description |
|---------|-------------|
| `docker build -t name:tag .` | Build image from Dockerfile |
| `docker images` | List local images |
| `docker rmi <image>` | Remove image |
| `docker pull <image>` | Pull from registry |
| `docker push <image>` | Push to registry |

## Container Operations

| Command | Description |
|---------|-------------|
| `docker run -d -p 8080:80 name` | Run container (detached, port map) |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers |
| `docker stop <id>` | Stop container |
| `docker rm <id>` | Remove container |
| `docker logs <id>` | View container logs |
| `docker exec -it <id> sh` | Shell into container |

## Docker Compose

| Command | Description |
|---------|-------------|
| `docker compose up -d` | Start services |
| `docker compose down` | Stop services |
| `docker compose ps` | List services |
| `docker compose logs -f` | View logs |

## Useful Flags
- `-d` Detached mode
- `-p host:container` Port mapping
- `-v host:container` Volume mount
- `--name` Container name
- `--network` Network name